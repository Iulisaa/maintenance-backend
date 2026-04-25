# Maintenance App

Lean Spring Boot + Kotlin MVP for:
- equipment maintenance planning
- engineer daily scheduling
- weekend / holiday / leave exclusion
- configurable max tasks per engineer per day
- maintenance report completion
- basic document generation stub

## Stack
- Kotlin
- Spring Boot
- PostgreSQL
- Flyway
- Spring Data JPA
- Swagger UI

## Run
1. Create a PostgreSQL database named `maintenance_app`
2. Update `src/main/resources/application.yml` if needed
3. Start the app

```bash
mvn spring-boot:run
```

Swagger:
- `http://localhost:8080/swagger-ui.html`

## Main API flow

### 1. Create engineers
`POST /api/engineers`

### 2. Add engineer capacity rules
`POST /api/engineers/{engineerId}/capacity-rules`

### 3. Add engineer leaves
`POST /api/engineers/{engineerId}/leaves`

### 4. Create equipments with maintenance rules
`POST /api/equipments`

### 5. Generate planning
`POST /api/planning/generate`

### 6. View tasks
`GET /api/tasks?startDate=2026-01-01&endDate=2026-12-31`

### 7. Complete a task and generate report
`POST /api/reports/tasks/{taskId}/complete`

## Notes
- `activeMonths` format is CSV month numbers, for example:
  - `1,2,3,4,11,12` for HEAT
  - `5,6,7,8,9,10` for COLD
  - `1,2,3,4,5,6,7,8,9,10,11,12` for UNIVERSAL
- The document generator currently writes a text file with a `.pdf.txt` suffix as a safe stub.
- Real PDF and email sending can be plugged in next.
