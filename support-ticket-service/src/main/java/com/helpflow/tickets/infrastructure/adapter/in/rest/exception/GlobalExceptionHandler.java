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
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Recurso no encontrado");
        problem.setType(URI.create("https://helpflow.com/errors/not-found"));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler({TicketAccessDeniedException.class, AccessDeniedException.class})
    public ResponseEntity<ProblemDetail> handleAccessDenied(Exception ex, HttpServletRequest request) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        metrics.recordAccessDenied();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Acceso denegado");
        problem.setType(URI.create("https://helpflow.com/errors/access-denied"));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(problem);
    }

    @ExceptionHandler({
            DomainException.class,
            AssignmentNotAllowedException.class,
            InvalidTicketStateException.class
    })
    public ResponseEntity<ProblemDetail> handleDomainException(Exception ex, HttpServletRequest request) {
        log.warn("Violación de regla de negocio: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problem.setTitle("Regla de negocio no cumplida");
        problem.setType(URI.create("https://helpflow.com/errors/business-rule-violation"));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
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

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o token expirado");
        problem.setTitle("No autorizado");
        problem.setType(URI.create("https://helpflow.com/errors/unauthorized"));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno inesperado en el servidor");
        problem.setTitle("Error interno del servidor");
        problem.setType(URI.create("https://helpflow.com/errors/internal-server-error"));
        enrichProblemDetail(problem, request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
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
