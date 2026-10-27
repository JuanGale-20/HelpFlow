-- V1__schema.sql: Estructura base de tablas para HelpFlow

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    document VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    site VARCHAR(100) NOT NULL,
    area VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS technicians (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    specialty VARCHAR(50) NOT NULL,
    site VARCHAR(100) NOT NULL,
    max_active_tickets INT NOT NULL DEFAULT 5,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

CREATE TABLE IF NOT EXISTS tickets (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL REFERENCES users(id),
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,
    site VARCHAR(100) NOT NULL,
    affected_asset VARCHAR(150),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    sla_deadline TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    assigned_technician_id BIGINT REFERENCES technicians(id),
    first_response_at TIMESTAMP WITH TIME ZONE,
    resolved_at TIMESTAMP WITH TIME ZONE,
    escalation_count INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS assignments (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    technician_id BIGINT NOT NULL REFERENCES technicians(id),
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL,
    estimated_hours INT,
    availability VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS assignment_history (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL REFERENCES tickets(id) ON DELETE CASCADE,
    previous_status VARCHAR(20),
    new_status VARCHAR(20) NOT NULL,
    actor_id BIGINT REFERENCES users(id),
    observation TEXT,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS sla_policies (
    id BIGSERIAL PRIMARY KEY,
    priority VARCHAR(20) NOT NULL UNIQUE,
    duration_hours INT NOT NULL
);
