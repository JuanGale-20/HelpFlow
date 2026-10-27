# HelpFlow - Sistema Integral de GestiÃ³n de Soporte TI

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Architecture](https://img.shields.io/badge/Architecture-Hexagonal%20Ports%20%26%20Adapters-blue.svg)]()
[![Database](https://img.shields.io/badge/Database-PostgreSQL%2016-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose%20Ready-2496ED.svg)](https://www.docker.com/)

**HelpFlow** es una plataforma corporativa para el registro, priorizaciÃ³n, asignaciÃ³n determinista, escalamiento por SLA y cierre de incidencias tecnolÃ³gicas en entornos distribuidos multi-sede.

El sistema se compone de dos microservicios desacoplados:
1. **`support-ticket-service` (Puerto 8080):** Servicio central construido bajo arquitectura hexagonal pura, con persistencia JPA avanzada en PostgreSQL, migraciones Flyway, seguridad stateless mediante JWT, auditorÃ­a transaccional, observabilidad con Micrometer/Prometheus y manejo estandarizado de errores RFC 7807 (`ProblemDetail`).
2. **`technician-dispatch-service` (Puerto 8081):** Microservicio externo simulado y determinista que evalÃºa la categorÃ­a, prioridad y ticket para sugerir tÃ©cnicos y disponibilidad estimada.

---

## 1. Diagramas del Sistema

### 1.1. Arquitectura Hexagonal (`support-ticket-service`)

```mermaid
flowchart TD
    subgraph AdaptadoresEntrada["Adaptadores de Entrada (Inbound Adapters)"]
        REST["REST Controllers\n(AuthController, TicketController, AdminSlaController)"]
        CRON["Task Scheduler\n(TicketEscalationScheduler @Scheduled)"]
    end

    subgraph PuertosEntrada["Puertos de Entrada (Input Ports / Use Cases)"]
        CreateUC["CreateTicketUseCase"]
        AssignUC["AssignTicketUseCase"]
        ManageUC["ManageTicketUseCase"]
        EscalateUC["EscalateTicketsUseCase"]
        QueryUC["QueryTicketsUseCase"]
        AuthUC["RegisterUserUseCase\nManageSlaUseCase"]
    end

    subgraph DominioPuro["NÃºcleo de Dominio (Domain Core - Sin dependencias)"]
        Entities["Entidades & Agregados\n(Ticket, User, Technician, Assignment)"]
        VO["Value Objects\n(TicketId, UserId, TechnicianId, SlaDeadline)"]
        Policies["PolÃ­ticas del Dominio\n(AssignmentPolicy, TicketTransitionPolicy, SlaPolicy)"]
    end

    subgraph PuertosSalida["Puertos de Salida (Output Ports)"]
        TicketRepoPort["TicketRepositoryPort"]
        TechRepoPort["TechnicianRepositoryPort"]
        UserRepoPort["UserRepositoryPort"]
        HistoryRepoPort["HistoryRepositoryPort"]
        AssignRepoPort["AssignmentRepositoryPort"]
        SlaPort["SlaConfigurationPort"]
        DispatchPort["TechnicianDispatchPort"]
        PassPort["PasswordEncoderPort"]
    end

    subgraph AdaptadoresSalida["Adaptadores de Salida (Outbound Adapters)"]
        JPA["Persistencia JPA & PostgreSQL\n(Spring Data + MapStruct + Flyway)"]
        RestClient["HTTP Client\n(TechnicianDispatchRestClientAdapter)"]
        Security["Seguridad\n(BCryptPasswordEncoderAdapter)"]
    end

    REST --> PuertosEntrada
    CRON --> PuertosEntrada
    PuertosEntrada --> DominioPuro
    PuertosEntrada --> PuertosSalida
    TicketRepoPort --> JPA
    TechRepoPort --> JPA
    UserRepoPort --> JPA
    HistoryRepoPort --> JPA
    AssignRepoPort --> JPA
    SlaPort --> JPA
    DispatchPort --> RestClient
    PassPort --> Security
```

---

### 1.2. RelaciÃ³n e InteracciÃ³n entre Microservicios

```mermaid
sequenceDiagram
    autonumber
    actor Usuario as Usuario Solicitante
    participant TicketService as support-ticket-service (8080)
    participant DispatchService as technician-dispatch-service (8081)
    participant DB as PostgreSQL (5432)

    Usuario->>TicketService: POST /tickets (Bearer Token)
    Note over TicketService: Valida usuario ACTIVO y calcula SLA
    TicketService->>DB: INSERT into tickets (Estado: ABIERTO)
    TicketService->>DB: INSERT into assignment_history (Ticket creado)

    TicketService->>DispatchService: POST /technician-dispatch (ticketId, categoria, prioridad)
    alt Dispatch responde exitosamente
        DispatchService-->>TicketService: 200 OK (tecnicoId, disponibilidad, horas)
        Note over TicketService: Valida reglas de asignaciÃ³n (especialidad y carga)
    else Dispatch falla o tÃ©cnico no elegible
        Note over TicketService: Fallback: Reglas Internas (misma sede y menor carga)
    end

    alt TÃ©cnico encontrado
        TicketService->>DB: UPDATE tickets SET status = 'ASIGNADO', technician_id
        TicketService->>DB: INSERT into assignments
        TicketService->>DB: INSERT into assignment_history
    else Sin tÃ©cnicos elegibles
        Note over TicketService: Ticket permanece en estado ABIERTO
    end

    TicketService-->>Usuario: 201 Created (TicketResponse)
```

---

### 1.3. Ciclo de Vida y Transiciones de Estado del Ticket

```mermaid
stateDiagram-v2
    [*] --> ABIERTO: Registro de Ticket
    ABIERTO --> ASIGNADO: Auto-asignaciÃ³n / AsignaciÃ³n manual
    ABIERTO --> CANCELADO: Cancelado por el Usuario (con observaciÃ³n)
    ASIGNADO --> EN_PROGRESO: TÃ©cnico inicia atenciÃ³n (registra firstResponseAt)
    ASIGNADO --> CANCELADO: Cancelado por el Usuario (con observaciÃ³n)
    ASIGNADO --> ASIGNADO: ReasignaciÃ³n por Admin
    EN_PROGRESO --> ASIGNADO: ReasignaciÃ³n por Admin
    EN_PROGRESO --> RESUELTO: TÃ©cnico soluciona el ticket (con observaciÃ³n)
    RESUELTO --> EN_PROGRESO: Reapertura por revisiÃ³n
    RESUELTO --> CERRADO: Cierre definitivo administrativo
    CANCELADO --> [*]
    CERRADO --> [*]
```

---

## 2. Roles y Control de Acceso (Matriz RBAC)

| Funcionalidad | `ROLE_USUARIO` | `ROLE_TECNICO` | `ROLE_ADMIN` |
| :--- | :---: | :---: | :---: |
| Crear tickets |  SÃ­ | âŒ No |  SÃ­ |
| Consultar tickets |  Solo propios |  Solo asignados |  Todos |
| Cancelar ticket propio |  SÃ­ (con motivo) | âŒ No |  SÃ­ |
| Avanzar a `EN_PROGRESO` | âŒ No |  Solo asignados |  SÃ­ |
| Resolver ticket | âŒ No |  Solo asignados |  SÃ­ |
| Reasignar tÃ©cnico | âŒ No | âŒ No |  SÃ­ |
| Configurar horas de SLA | âŒ No | âŒ No |  SÃ­ |

---

## 3. Credenciales de Prueba Pre-cargadas (Flyway V3)

Todos los usuarios tienen la contraseÃ±a: **`admin123`**

| Rol | Correo ElectrÃ³nico | Especialidad / Sede | Carga MÃ¡x. |
| :--- | :--- | :--- | :---: |
| **Administrador** | `admin@helpflow.com` | DirecciÃ³n TI / Sede Central | N/A |
| **TÃ©cnico (Hardware)** | `tec01@helpflow.com` (`TEC-001`) | HARDWARE / Sede Central | 5 |
| **TÃ©cnico (Hardware)** | `tec02@helpflow.com` (`TEC-002`) | HARDWARE / Sede Norte | 5 |
| **TÃ©cnico (Software)** | `tec05@helpflow.com` (`TEC-005`) | SOFTWARE / Sede Central | 5 |
| **TÃ©cnico (Red)** | `tec09@helpflow.com` (`TEC-009`) | RED / Sede Central | 5 |
| **TÃ©cnico (Accesos)** | `tec13@helpflow.com` (`TEC-013`) | ACCESOS / Sede Central | 5 |
| **TÃ©cnico (General)** | `tec17@helpflow.com` (`TEC-017`) | OTRO / Sede Central | 5 |

*(Nota: Existen en total 20 tÃ©cnicos registrados de `TEC-001` a `TEC-020` distribuidos entre las 5 especialidades y 4 sedes).*

---

## 4. Endpoints Principales de la API

### AutenticaciÃ³n (`/auth`)
* `POST /auth/register`: Registro de nuevos usuarios y tÃ©cnicos con documento y correo Ãºnicos.
* `POST /auth/login`: AutenticaciÃ³n con credenciales que retorna el token JWT Bearer.

### Tickets de Soporte (`/tickets`)
* `POST /tickets`: CreaciÃ³n de ticket con auto-asignaciÃ³n inmediata.
* `GET /tickets`: Listado de tickets filtrado segÃºn el rol del actor autenticado.
* `GET /tickets/{id}`: Detalle del ticket con validaciÃ³n de propiedad.
* `GET /tickets/{id}/history`: Historial completo de cambios y observaciones.
* `PATCH /tickets/{id}/status`: TransiciÃ³n de estado (`status` y `observation`).
* `POST /tickets/{id}/assign`: ReasignaciÃ³n manual por administrador.

### AdministraciÃ³n de SLA (`/admin/sla`)
* `GET /admin/sla`: Consulta de las horas lÃ­mites de SLA activas por prioridad.
* `PUT /admin/sla`: ModificaciÃ³n de horas lÃ­mites de SLA por el administrador.

### Observabilidad y Monitoreo (`/actuator`)
* `GET /actuator/health`: Estado de salud de la aplicaciÃ³n y la conexiÃ³n a base de datos.
* `GET /actuator/metrics`: Listado de mÃ©tricas disponibles.
* `GET /actuator/metrics/helpflow.tickets.created`: Contador de tickets por categorÃ­a y prioridad.
* `GET /actuator/metrics/helpflow.tickets.overdue`: Gauge en tiempo real de tickets con SLA vencido.
* `GET /actuator/metrics/helpflow.tickets.first_response_time`: Tiempo medio de primera respuesta.
* `GET /actuator/metrics/helpflow.tickets.resolution_time`: Tiempo medio de resoluciÃ³n.
* `GET /actuator/prometheus`: Exportador para scraping de Prometheus.

---

## 5. Instrucciones de EjecuciÃ³n

### OpciÃ³n A: Despliegue Completo con Docker Compose (Recomendado)

En la raÃ­z del proyecto, ejecuta un Ãºnico comando:

```bash
docker compose up --build -d
```

Esto levantarÃ¡ automÃ¡ticamente en orden:
1. `helpflow-db`: PostgreSQL 16 en el puerto `5432`.
2. `technician-dispatch-service`: Microservicio en el puerto `8081`.
3. `support-ticket-service`: Microservicio central en el puerto `8080`.

Para detener los servicios:
```bash
docker compose down
```

---

### OpciÃ³n B: EjecuciÃ³n Local en Desarrollo

1. **Asegurar PostgreSQL local** en el puerto `5432` con base de datos `helpflow_tickets` (o usar la BD levantada por docker).
2. **Iniciar `technician-dispatch-service`:**
   ```powershell
   cd technician-dispatch-service
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
   .\mvnw.cmd spring-boot:run
   ```
3. **Iniciar `support-ticket-service`:**
   ```powershell
   cd support-ticket-service
   $env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
   .\mvnw.cmd spring-boot:run
   ```

---

## 6. DocumentaciÃ³n Swagger y ColecciÃ³n Postman

* **Swagger UI Interactivo:**
  Accede desde tu navegador a:
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
  *(Permite ingresar el token JWT en el botÃ³n **Authorize** para probar todos los endpoints protegidos).*

* **ColecciÃ³n Postman:**
  Importa el archivo [`HelpFlow.postman_collection.json`](file:///C:/Users/juang/OneDrive/Documentos/riwi-proyects/HelpFlow/HelpFlow.postman_collection.json) ubicado en la raÃ­z del proyecto. Viene preconfigurado con tests automÃ¡ticos para capturar y propagar los tokens JWT en cada peticiÃ³n.

---

## 7. EjecuciÃ³n de Pruebas Automatizadas

Para validar toda la suite de pruebas unitarias de casos de uso y de controladores:

```powershell
# En support-ticket-service:
cd support-ticket-service
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
.\mvnw.cmd test -Dtest="*ServiceTest"

# En technician-dispatch-service:
cd ..\technician-dispatch-service
.\mvnw.cmd test
```
