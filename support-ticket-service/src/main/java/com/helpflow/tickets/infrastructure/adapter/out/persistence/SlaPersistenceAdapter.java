package com.helpflow.tickets.infrastructure.adapter.out.persistence;

import com.helpflow.tickets.application.port.output.SlaConfigurationPort;
import com.helpflow.tickets.domain.model.TicketPriority;
import com.helpflow.tickets.domain.service.SlaPolicy;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.entity.SlaPolicyEntity;
import com.helpflow.tickets.infrastructure.adapter.out.persistence.repository.SpringDataSlaPolicyRepository;
import java.time.Duration;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class SlaPersistenceAdapter implements SlaConfigurationPort {

    private final SpringDataSlaPolicyRepository repository;

    public SlaPersistenceAdapter(SpringDataSlaPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    public SlaPolicy currentPolicy() {
        List<SlaPolicyEntity> entities = repository.findAll();
        if (entities.isEmpty()) {
            return SlaPolicy.defaultPolicy();
        }

        Map<TicketPriority, Duration> durations = new EnumMap<>(TicketPriority.class);
        for (SlaPolicyEntity entity : entities) {
            durations.put(entity.getPriority(), Duration.ofHours(entity.getDurationHours()));
        }

        for (TicketPriority priority : TicketPriority.values()) {
            if (!durations.containsKey(priority)) {
                return SlaPolicy.defaultPolicy();
            }
        }

        return new SlaPolicy(durations);
    }

    @Override
    public void save(SlaPolicy policy) {
        for (TicketPriority priority : TicketPriority.values()) {
            Duration duration = policy.durationFor(priority);
            int hours = (int) Math.max(1, duration.toHours());
            SlaPolicyEntity entity = repository.findByPriority(priority)
                    .orElseGet(() -> new SlaPolicyEntity(null, priority, hours));
            entity.setDurationHours(hours);
            repository.save(entity);
        }
    }
}
