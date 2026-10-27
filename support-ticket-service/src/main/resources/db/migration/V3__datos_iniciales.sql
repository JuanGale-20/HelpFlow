-- V3__datos_iniciales.sql: Semillero inicial de datos (Admin, 20 Técnicos TEC-001..TEC-020 y Políticas SLA)

-- Contraseña por defecto para usuarios sembrados: 'admin123' (BCrypt)
-- $2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2

-- 1. Usuario Administrador Principal
INSERT INTO users (id, document, name, email, password_hash, site, area, status, role)
VALUES (1, 'DOC-ADMIN-01', 'Administrador General HelpFlow', 'admin@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Dirección TI', 'ACTIVO', 'ROLE_ADMIN');

-- 2. Usuarios para los 20 Técnicos
INSERT INTO users (id, document, name, email, password_hash, site, area, status, role) VALUES
(2, 'DOC-TEC-001', 'Carlos Mendoza', 'tec01@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Soporte Hardware', 'ACTIVO', 'ROLE_TECNICO'),
(3, 'DOC-TEC-002', 'Laura Gómez', 'tec02@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Norte', 'Soporte Hardware', 'ACTIVO', 'ROLE_TECNICO'),
(4, 'DOC-TEC-003', 'Andrés Rivas', 'tec03@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Sur', 'Soporte Hardware', 'ACTIVO', 'ROLE_TECNICO'),
(5, 'DOC-TEC-004', 'Diana Torres', 'tec04@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Occidente', 'Soporte Hardware', 'ACTIVO', 'ROLE_TECNICO'),

(6, 'DOC-TEC-005', 'Javier López', 'tec05@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Soporte Software', 'ACTIVO', 'ROLE_TECNICO'),
(7, 'DOC-TEC-006', 'Paula Castro', 'tec06@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Norte', 'Soporte Software', 'ACTIVO', 'ROLE_TECNICO'),
(8, 'DOC-TEC-007', 'Mateo Ramírez', 'tec07@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Sur', 'Soporte Software', 'ACTIVO', 'ROLE_TECNICO'),
(9, 'DOC-TEC-008', 'Sofía Morales', 'tec08@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Occidente', 'Soporte Software', 'ACTIVO', 'ROLE_TECNICO'),

(10, 'DOC-TEC-009', 'Felipe Ortiz', 'tec09@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Infraestructura Red', 'ACTIVO', 'ROLE_TECNICO'),
(11, 'DOC-TEC-010', 'Valentina Ruiz', 'tec10@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Norte', 'Infraestructura Red', 'ACTIVO', 'ROLE_TECNICO'),
(12, 'DOC-TEC-011', 'Santiago Vargas', 'tec11@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Sur', 'Infraestructura Red', 'ACTIVO', 'ROLE_TECNICO'),
(13, 'DOC-TEC-012', 'Mariana Pardo', 'tec12@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Occidente', 'Infraestructura Red', 'ACTIVO', 'ROLE_TECNICO'),

(14, 'DOC-TEC-013', 'Camilo Peña', 'tec13@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Seguridad y Accesos', 'ACTIVO', 'ROLE_TECNICO'),
(15, 'DOC-TEC-014', 'Natalia Herrera', 'tec14@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Norte', 'Seguridad y Accesos', 'ACTIVO', 'ROLE_TECNICO'),
(16, 'DOC-TEC-015', 'Diego Silva', 'tec15@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Sur', 'Seguridad y Accesos', 'ACTIVO', 'ROLE_TECNICO'),
(17, 'DOC-TEC-016', 'Camila Benítez', 'tec16@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Occidente', 'Seguridad y Accesos', 'ACTIVO', 'ROLE_TECNICO'),

(18, 'DOC-TEC-017', 'Alejandro Cárdenas', 'tec17@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Central', 'Soporte General', 'ACTIVO', 'ROLE_TECNICO'),
(19, 'DOC-TEC-018', 'Isabella Rojas', 'tec18@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Norte', 'Soporte General', 'ACTIVO', 'ROLE_TECNICO'),
(20, 'DOC-TEC-019', 'Gabriel Suárez', 'tec19@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Sur', 'Soporte General', 'ACTIVO', 'ROLE_TECNICO'),
(21, 'DOC-TEC-020', 'Daniela Ospina', 'tec20@helpflow.com', '$2a$10$W1l16lE31rtbCu2hThmrgOr.nEZC5E8MCLPnlEntCCgbrHhqloFD2', 'Sede Occidente', 'Soporte General', 'ACTIVO', 'ROLE_TECNICO');

-- 3. Registro de los 20 Técnicos vinculados a sus usuarios
INSERT INTO technicians (id, code, user_id, name, specialty, site, max_active_tickets, status) VALUES
(1, 'TEC-001', 2, 'Carlos Mendoza', 'HARDWARE', 'Sede Central', 5, 'ACTIVO'),
(2, 'TEC-002', 3, 'Laura Gómez', 'HARDWARE', 'Sede Norte', 5, 'ACTIVO'),
(3, 'TEC-003', 4, 'Andrés Rivas', 'HARDWARE', 'Sede Sur', 5, 'ACTIVO'),
(4, 'TEC-004', 5, 'Diana Torres', 'HARDWARE', 'Sede Occidente', 5, 'ACTIVO'),

(5, 'TEC-005', 6, 'Javier López', 'SOFTWARE', 'Sede Central', 5, 'ACTIVO'),
(6, 'TEC-006', 7, 'Paula Castro', 'SOFTWARE', 'Sede Norte', 5, 'ACTIVO'),
(7, 'TEC-007', 8, 'Mateo Ramírez', 'SOFTWARE', 'Sede Sur', 5, 'ACTIVO'),
(8, 'TEC-008', 9, 'Sofía Morales', 'SOFTWARE', 'Sede Occidente', 5, 'ACTIVO'),

(9, 'TEC-009', 10, 'Felipe Ortiz', 'RED', 'Sede Central', 5, 'ACTIVO'),
(10, 'TEC-010', 11, 'Valentina Ruiz', 'RED', 'Sede Norte', 5, 'ACTIVO'),
(11, 'TEC-011', 12, 'Santiago Vargas', 'RED', 'Sede Sur', 5, 'ACTIVO'),
(12, 'TEC-012', 13, 'Mariana Pardo', 'RED', 'Sede Occidente', 5, 'ACTIVO'),

(13, 'TEC-013', 14, 'Camilo Peña', 'ACCESOS', 'Sede Central', 5, 'ACTIVO'),
(14, 'TEC-014', 15, 'Natalia Herrera', 'ACCESOS', 'Sede Norte', 5, 'ACTIVO'),
(15, 'TEC-015', 16, 'Diego Silva', 'ACCESOS', 'Sede Sur', 5, 'ACTIVO'),
(16, 'TEC-016', 17, 'Camila Benítez', 'ACCESOS', 'Sede Occidente', 5, 'ACTIVO'),

(17, 'TEC-017', 18, 'Alejandro Cárdenas', 'OTRO', 'Sede Central', 5, 'ACTIVO'),
(18, 'TEC-018', 19, 'Isabella Rojas', 'OTRO', 'Sede Norte', 5, 'ACTIVO'),
(19, 'TEC-019', 20, 'Gabriel Suárez', 'OTRO', 'Sede Sur', 5, 'ACTIVO'),
(20, 'TEC-020', 21, 'Daniela Ospina', 'OTRO', 'Sede Occidente', 5, 'ACTIVO');

-- 4. Políticas de SLA Iniciales
INSERT INTO sla_policies (priority, duration_hours) VALUES
('CRITICA', 4),
('ALTA', 8),
('MEDIA', 24),
('BAJA', 72);

-- 5. Ajuste de secuencias
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('technicians_id_seq', (SELECT MAX(id) FROM technicians));
