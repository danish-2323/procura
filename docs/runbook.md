# Procura 2.0: Operations Runbook

## Local Development Setup
1. Prerequisites:
   - Java 21 LTS
   - Node.js 20+ (Node 24 tested)
   - Docker & Docker Compose
   - Maven 3.9+ (or use Maven wrapper)

2. Start Infrastructure:
   ```bash
   docker compose up -d postgres minio
   ```

3. Run Backend:
   ```bash
   cd backend
   mvn spring-boot:run
   ```

4. Run Frontend:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. Verification Endpoints:
   - Health: `http://localhost:8080/actuator/health`
   - Swagger OpenAPI: `http://localhost:8080/swagger-ui/index.html`
   - Frontend: `http://localhost:5173`
   - MinIO Console: `http://localhost:9001` (user: `minioadmin`, pass: `minioadmin`)
