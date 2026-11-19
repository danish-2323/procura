# Procura 2.0

> AI-assisted, event-driven, auditable procure-to-pay platform for mid-size enterprises.

[![CI Pipeline](https://github.com/procura/procura/actions/workflows/ci.yml/badge.svg)](https://github.com/procura/procura/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21%20LTS-orange.svg)](https://adoptium.net/)
[![Spring Boot 3.4](https://img.shields.io/badge/Spring%20Boot-3.4.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vite + React](https://img.shields.io/badge/Frontend-React%2018%20%2B%20Vite-blue.svg)](https://react.dev/)

---

## Overview
Procura 2.0 replaces unstructured email and spreadsheet purchasing with a rigorous, auditable procure-to-pay workflow:
1. **Requisitions & Multi-step Approvals** with automated budget reservations.
2. **Sourcing & RFQ** with weighted supplier quotation scoring.
3. **Purchase Orders & Versioning** with PDF generation.
4. **Warehouse Goods Receipt** with quarantine segregation.
5. **Cumulative 3-Way Invoice Matching** across PO deliveries with tolerance enforcement.
6. **Immutable Financial Ledger** and append-only audit trail.
7. **Advisory AI Assistance** (with strict human confirmation and instant kill-switch).

---

## Quickstart (Local Docker Compose)
```bash
# 1. Copy sample environment file
cp .env.example .env

# 2. Start PostgreSQL, MinIO, Backend, and Frontend
docker compose up --build
```
- Frontend UI: [http://localhost:5173](http://localhost:5173)
- Backend API & Actuator: [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
- OpenAPI Swagger Documentation: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- MinIO Storage Console: [http://localhost:9001](http://localhost:9001)

---

## Project Structure
Refer to [docs/architecture.md](docs/architecture.md) for module decomposition and architectural rules.
- `backend/`: Java 21, Spring Boot 3.4.2, Spring Modulith, Spring Security, Flyway, PostgreSQL.
- `frontend/`: React 18, TypeScript, Vite, Tailwind CSS, TanStack Query, shadcn/ui.
- `docs/`: ER diagrams, state machines, business rule catalogue, and demo scripts.
