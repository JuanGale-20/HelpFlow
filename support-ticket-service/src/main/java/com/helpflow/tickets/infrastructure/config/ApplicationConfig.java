package com.helpflow.tickets.infrastructure.config;

import com.helpflow.tickets.domain.service.AssignmentPolicy;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public AssignmentPolicy assignmentPolicy() {
        return new AssignmentPolicy();
    }
}
