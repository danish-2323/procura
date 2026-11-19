# Procura 2.0: 5-Minute End-to-End Demo Script

Works with AI on (`ai.enabled=true`) or off (`ai.enabled=false`).

1. **Requisition (Employee)**:
   - Login as `employee@procura.local`.
   - Submit requisition for ₹3,00,000 for Dell Enterprise Laptops.
   - Status transitions: `DRAFT -> SUBMITTED -> PENDING_APPROVAL`.

2. **Manager Approval (Department Manager)**:
   - Login as `manager.eng@procura.local`.
   - Review pending approval step 1. Approve with note.

3. **Finance Approval (Finance Manager)**:
   - Login as `finance@procura.local`.
   - Step 2 approved. Status: `APPROVED`.
   - Budget ledger records `RESERVE` of ₹3,00,000 against Engineering.

4. **Sourcing & RFQ (Procurement Officer)**:
   - Login as `procurement@procura.local`.
   - Create RFQ from approved requisition, invite 3 verified suppliers.
   - Close RFQ and evaluate quotations (scoring matrix: price 50%, delivery 25%, rating 15%, mismatch 10%).
   - Award quotation to winning supplier.

5. **Purchase Order Issuance**:
   - PO generated: `PO-2026-00001`.
   - Confirm PO: Ledger converts ₹3,00,000 from `RESERVE` to `COMMIT`.

6. **Warehouse Receiving (Warehouse Manager)**:
   - Login as `warehouse@procura.local`.
   - Delivery 1: Receive 60 units accepted -> inventory available stock increases.
   - Delivery 2: Receive 40 units accepted -> PO status becomes `FULLY_RECEIVED`.

7. **Invoice & 3-Way Match (Finance / Supplier)**:
   - Supplier or Finance submits invoice for ₹3,05,000 (+₹5,000 overbilling).
   - Match engine runs cumulative 3-way check -> Flags `DISCREPANCY` (Price tolerance exceeded).
   - Finance resolves: provides audited override or credit note.
   - Invoice approved -> Budget ledger records `SPEND` ₹3,00,000, `UNCOMMIT` ₹3,00,000.

8. **Audit & Analytics (Auditor & Director)**:
   - Login as `auditor@procura.local`: inspect immutable audit log and ledger entries (read-only).
   - Login as `director@procura.local`: review spend analytics and budget reconciliation.
