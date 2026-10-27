package com.helpflow.tickets.infrastructure.scheduler;

import com.helpflow.tickets.application.port.input.EscalateTicketsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class TicketEscalationScheduler {

    private static final Logger log = LoggerFactory.getLogger(TicketEscalationScheduler.class);

    private final EscalateTicketsUseCase escalateTicketsUseCase;

    public TicketEscalationScheduler(EscalateTicketsUseCase escalateTicketsUseCase) {
        this.escalateTicketsUseCase = escalateTicketsUseCase;
    }

    @Scheduled(fixedRateString = "${scheduling.escalation-rate-ms:60000}")
    public void runEscalationJob() {
        try {
            int escalated = escalateTicketsUseCase.escalateAtRisk();
            if (escalated > 0) {
                log.info("Trabajo programado: se escalaron {} tickets por riesgo de SLA", escalated);
            }
        } catch (Exception ex) {
            log.error("Error durante la ejecución del trabajo programado de escalamiento de tickets: {}",
                    ex.getMessage(), ex);
        }
    }
}
