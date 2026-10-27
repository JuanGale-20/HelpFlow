-- V2__indexes_and_constraints.sql: Índices de rendimiento y restricciones

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_document ON users(document);
CREATE INDEX IF NOT EXISTS idx_technicians_code ON technicians(code);
CREATE INDEX IF NOT EXISTS idx_technicians_user_id ON technicians(user_id);
CREATE INDEX IF NOT EXISTS idx_technicians_status ON technicians(status);

CREATE INDEX IF NOT EXISTS idx_tickets_requester ON tickets(requester_id);
CREATE INDEX IF NOT EXISTS idx_tickets_technician ON tickets(assigned_technician_id);
CREATE INDEX IF NOT EXISTS idx_tickets_status ON tickets(status);
CREATE INDEX IF NOT EXISTS idx_tickets_sla_deadline ON tickets(sla_deadline);

CREATE INDEX IF NOT EXISTS idx_assignments_ticket ON assignments(ticket_id);
CREATE INDEX IF NOT EXISTS idx_assignments_technician ON assignments(technician_id);

CREATE INDEX IF NOT EXISTS idx_history_ticket ON assignment_history(ticket_id);
CREATE INDEX IF NOT EXISTS idx_history_actor ON assignment_history(actor_id);
