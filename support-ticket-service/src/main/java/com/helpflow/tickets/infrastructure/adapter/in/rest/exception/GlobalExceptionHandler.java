package com.helpflow.tickets.infrastructure.adapter.in.rest.exception;

import com.helpflow.tickets.application.exception.ResourceNotFoundException;
import com.helpflow.tickets.application.exception.TicketAccessDeniedException;
import com.helpflow.tickets.domain.exception.AssignmentNotAllowedException;
import com.helpflow.tickets.domain.exception.DomainException;
import com.helpflow.tickets.domain.exception.InvalidTicketStateException;
import com.helpflow.tickets.infrastructure.adapter.in.rest.logging.TraceIdFilter;
import com.helpflow.tickets.infrastructure.observability.HelpFlowMetrics;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final HelpFlowMetrics metrics;

    public GlobalExceptionHandler(HelpFlowMetrics metrics) {
        this.metrics = metrics;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "Recurso no encontrado", "not-found", ex.getMessage(), request);
    }

    @ExceptionHandler({TicketAccessDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ProblemDetail> handleAccessDenied(Exception ex, HttpServletRequest request) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        metrics.recordAccessDenied();
        return build(HttpStatus.FORBIDDEN, "Acceso denegado", "access-denied", ex.getMessage(), request);
    }

    @ExceptionHandler({
            DomainException.class,
            AssignmentNotAllowedException.class,
            InvalidTicketStateException.class
    })
    public ResponseEntity<ProblemDetail> handleDomainException(Exception ex, HttpServletRequest request) {
        log.warn("Violación de regla de negocio: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio no cumplida", "business-rule-violation", ex.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.warn("Error de validación en parámetros de la solicitud");
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos enviados no cumplen las validaciones requeridas");
        problem.setTitle("Error de validación");
        problem.setType(URI.create("https://helpflow.com/errors/validation-error"));

        List<Map<String, String>> invalidParams = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .toList();

        problem.setProperty("invalidParams", invalidParams);
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException ex, HttpServletRequest request) {
        log.warn("Fallo de autenticación: {}", ex.getMessage());
        metrics.recordAuthFailure();
        return build(HttpStatus.UNAUTHORIZED, "No autorizado", "unauthorized",
                ex.getMessage() != null && !ex.getMessage().isBlank() ? ex.getMessage() : "Credenciales inválidas o token expirado",
                request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        log.warn("Cuerpo de la solicitud ilegible: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "Solicitud mal formada", "malformed-request",
                "El cuerpo de la solicitud es inválido o contiene valores no permitidos (por ejemplo, un enum desconocido)",
                request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        log.warn("Parámetro con tipo inválido: {}", ex.getName());
        return build(HttpStatus.BAD_REQUEST, "Parámetro inválido", "invalid-parameter",
                "El parámetro '" + ex.getName() + "' tiene un valor inválido", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        String cause = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : ex.getMessage();
        log.warn("Violación de integridad en base de datos: {}", cause);
        return build(HttpStatus.CONFLICT, "Conflicto de datos", "data-conflict",
                "La operación viola una restricción de datos (valor duplicado o referencia inexistente)", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ProblemDetail> handleNoResource(NoResourceFoundException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Ruta no encontrada", "not-found",
                "No existe el recurso solicitado", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido", "method-not-allowed",
                ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", "internal-server-error",
                "Ha ocurrido un error interno inesperado en el servidor", request);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String title, String typeSlug,
                                                String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://helpflow.com/errors/" + typeSlug));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(status).body(problem);
    }

    private void enrichProblemDetail(ProblemDetail problem, HttpServletRequest request) {
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("timestamp", Instant.now().toString());
        problem.setProperty("traceId", TraceIdFilter.getCurrentTraceId());
    }

    private Map<String, String> formatFieldError(FieldError error) {
        return Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido"
        );
    }
}
