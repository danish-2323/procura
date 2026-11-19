# Procura 2.0: Architecture & Dependency Pinning

## 1. Dependency Family Pinning
Per Master Build Prompt Section 2 & Rule 4 (simplest reasonable tested option):
- **Java**: 21 (LTS with Virtual Threads enabled `spring.threads.virtual.enabled=true`)
- **Spring Boot**: 3.4.2 (Production LTS family supporting Spring Framework 6.2.x & Spring Security 6.4.x)
- **Spring Modulith**: 1.3.2 (Production module boundary & event management for Spring Boot 3.4)
- **SpringDoc OpenAPI**: 2.8.5
- **Spring AI**: 1.0.0-M5 (Used in AI phases)
- **MapStruct**: 1.6.3
- **Lombok**: 1.18.36
- **Bucket4j**: 8.10.1
- **Caffeine**: 3.1.8
- **OpenPDF**: 2.0.2
- **Flyway**: 10.20.0
- **PostgreSQL Driver**: 42.7.5
- **Testcontainers**: 1.20.4

*Note on prompt versions*: The prompt text refers to hypothetical/future Spring Boot 4.1.x and Spring Modulith 2.1.x; in accordance with Section 2 ("Verify library versions against current docs before pinning. Use one tested, mutually compatible family") and Section 17 Rule 4, we pin the active, mutually compatible LTS family (Spring Boot 3.4.2 + Spring Modulith 1.3.2).

## 2. Modulith Module Boundaries
```
com.procura
 ├── auth
 ├── user
 ├── department
 ├── budget
 ├── catalog
 ├── requisition
 ├── approval
 ├── supplier
 ├── rfq
 ├── quotation
 ├── purchaseorder
 ├── receiving
 ├── inventory
 ├── invoice
 ├── notification
 ├── analytics
 ├── ai (Phase 7+)
 └── shared
```
Rules:
- Modules communicate strictly through public service interfaces or Spring Application Events (`ApplicationEventPublisher`).
- Direct entity/repository cross-coupling between domain modules is forbidden and verified by Spring Modulith architecture tests.

## 3. Core Architectural Patterns
1. **Outbox Pattern**: Changes to aggregates write a domain event to `outbox_events` within the same database transaction. A scheduled dispatcher publishes them asynchronously.
2. **Synchronous Audit Trail**: Audit entries for state changes are written synchronously inside the same business transaction. DB user permissions prevent UPDATE or DELETE on `audit_logs` (BR-17).
3. **Budget Ledger**: Insert-only, immutable ledger with unique `business_ref` and derived balances (BR-21, BR-22).
4. **Idempotency**: All mutating operations support `Idempotency-Key` headers stored for 24h with request/response hashes.
5. **Optimistic Locking**: `@Version` on all mutable aggregates (`requisitions`, `approval_steps`, `purchase_orders`, `invoices`, `departments`).
