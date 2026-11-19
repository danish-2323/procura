# Procura 2.0: API Examples

All endpoints reside under `/api/v1`.

### Standard Response Format
```json
{
  "timestamp": "2026-03-30T10:00:00Z",
  "status": 200,
  "data": { ... }
}
```

### Standard Error Body
```json
{
  "timestamp": "2026-03-30T10:00:00Z",
  "status": 422,
  "code": "BUSINESS_RULE_VIOLATION",
  "message": "Received quantity exceeds ordered quantity remaining",
  "ruleId": "BR-13",
  "path": "/api/v1/goods-receipts",
  "traceId": "c8a411e7-8b65-4f40-9764-2e9e63e26bb2",
  "fieldErrors": []
}
```

### Idempotency Header
All mutating state-change POST requests accept:
`Idempotency-Key: <UUID>`
Stored for 24 hours with the response hash, unique per `(user, method, endpoint, key)`.
