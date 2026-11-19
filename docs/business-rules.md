# Procura 2.0: Business Rules Matrix

| Rule ID | Name / Description | Phase | Test Requirement |
|---|---|---|---|
| **BR-01** | Requisition cannot be approved by its creator | Phase 2 | `RequisitionApprovalTest.br01_creatorCannotApprove` |
| **BR-02** | Requisition cannot be edited after submission (DRAFT only) | Phase 2 | `RequisitionLifecycleTest.br02_onlyDraftIsEditable` |
| **BR-03** | No RFQ or PO can be created from a non-APPROVED requisition | Phase 3/4 | `PurchaseOrderServiceTest.br03_cannotCreateFromNonApproved` |
| **BR-04** | Approval steps must be completed in sequence | Phase 2 | `ApprovalChainTest.br04_stepsMustBeCompletedInSequence` |
| **BR-05** | Approver cannot approve same step twice / concurrent resolution | Phase 2 | `ApprovalConcurrencyTest.br05_idempotentAndConcurrentResolve` |
| **BR-06** | Delegation eligibility, loop/chain rejection, dual audit | Phase 2b | `DelegationServiceTest.br06_delegationValidationAndAudit` |
| **BR-07** | Overdue approvals (48h) escalated to next level by scheduled job | Phase 2b | `EscalationJobTest.br07_overdueApprovalEscalation` |
| **BR-08** | Approval fails with BUDGET_EXCEEDED; Finance exception audited | Phase 2 | `BudgetLedgerTest.br08_budgetExceededAndException` |
| **BR-09** | Only VERIFIED suppliers invited to RFQs; blacklisted blocked | Phase 3 | `RfqServiceTest.br09_onlyVerifiedSuppliersAllowed` |
| **BR-10** | Quotations after RFQ deadline rejected (server UTC check) | Phase 3 | `QuotationSubmissionTest.br10_deadlineEnforcedUtc` |
| **BR-11** | Supplier sees only own RFQs, quotations, POs, invoices | Phase 3 | `SupplierScopeTest.br11_supplierIsolation` |
| **BR-12** | PO cannot exceed approved requisition amount beyond tolerance | Phase 4 | `PurchaseOrderValidationTest.br12_poAmountWithinTolerance` |
| **BR-13** | Received quantity per line cannot exceed ordered minus received | Phase 4 | `GoodsReceiptTest.br13_receivedQuantityWithinOrdered` |
| **BR-14** | Invoice number unique per supplier; duplicate detection 30d | Phase 5 | `InvoiceDuplicateTest.br14_duplicateDetectionAndQueue` |
| **BR-15** | Invoice cannot be approved while DISCREPANCY without override | Phase 5 | `InvoiceApprovalTest.br15_discrepancyRequiresOverride` |
| **BR-16** | Split-purchase detection: multiple reqs in 7 days crossing limit | Phase 2b | `SplitPurchaseDetectorTest.br16_splitPurchaseFlagged` |
| **BR-17** | Audit logs append-only (no UPDATE/DELETE permissions on DB user) | Phase 1 | `AuditSecurityTest.br17_auditLogsAppendOnly` |
| **BR-18** | Financial records never hard-deleted; soft delete for master data | Phase 1 | `MasterDataSoftDeleteTest.br18_noHardDeleteFinancials` |
| **BR-19** | User cannot modify another department's restricted records | Phase 1 | `DepartmentScopeTest.br19_departmentScopeIsolation` |
| **BR-20** | Passwords: min 10 chars, BCrypt/Argon2; 5 failed logins lock 15m | Phase 1 | `AuthLockoutTest.br20_passwordRulesAndLockout` |
| **BR-21** | Budget ledger is immutable, insert-only with unique business_ref | Phase 2 | `BudgetLedgerTest.br21_immutableInsertOnlyLedger` |
| **BR-22** | Final req approval = RESERVE; Cancel = RELEASE; PO confirm = COMMIT | Phase 2/4 | `BudgetLedgerTest.br22_reserveReleaseCommitTransitions` |
| **BR-23** | Partial PO commits only its share; remainder stays reserved | Phase 4 | `BudgetLedgerTest.br23_partialPoCommitAndResidual` |
| **BR-24** | PO cancellation creates UNCOMMIT back to reserved or released | Phase 4 | `BudgetLedgerTest.br24_poCancelUncommit` |
| **BR-25** | Invoice approval creates SPEND and UNCOMMIT on PO | Phase 5 | `BudgetLedgerTest.br25_invoiceApprovalSpend` |
| **BR-26** | Approve and pay actions idempotent via unique business_ref | Phase 2/5 | `IdempotencyTest.br26_ledgerIdempotency` |
| **BR-27** | All ledger entries carry currency (INR MVP default) | Phase 2 | `BudgetLedgerTest.br27_currencyIntegrity` |
| **BR-28** | Cumulative 3-way match across PO line invoices vs receipts | Phase 5 | `ThreeWayMatchingTest.br28_cumulativeLineMatching` |
| **BR-29** | Inventory: accepted=available, damaged=quarantine, missing=short | Phase 4 | `InventoryMovementTest.br29_inventoryReceiptSegregation` |
| **BR-30** | One requisition can have many POs referencing winning award | Phase 4 | `PurchaseOrderTest.br30_oneReqToManyPosAwardIntegrity` |
| **BR-31** | Expired mandatory supplier doc moves supplier to RESTRICTED | Phase 3b | `SupplierDocumentExpiryTest.br31_expiredDocRestrictsSupplier` |
