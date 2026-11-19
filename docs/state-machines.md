# Procura 2.0: Domain State Machines

## 1. Requisition State Machine
```
[DRAFT] --(submit)--> [SUBMITTED] --(generate chain)--> [PENDING_APPROVAL]
  |                         |                                |
  |                         |                                +--(all approved)--> [APPROVED]
  |                         |                                |
  |                         |                                +--(any reject)----> [REJECTED]
  |                         |                                |
  +-------------------------+--------------------------------+--(cancel)---------> [CANCELLED]

* Note: Rejected requisition can be cloned into a new DRAFT.
* Note: Edit permitted ONLY in DRAFT state (BR-02).
```

## 2. Approval Chain Step State Machine
```
[PENDING] --(approve)------------> [APPROVED]
  |
  +---------(reject, with comment)-> [REJECTED]
  |
  +---------(delegate)-----------> [DELEGATED]
  |
  +---------(escalate after 48h)-> [ESCALATED]
  |
  +---------(skip rule)----------> [SKIPPED]
```

## 3. RFQ & Quotation State Machines
```
RFQ:
[DRAFT] --(publish)--> [PUBLISHED] --(auto at deadline)--> [CLOSED] --(award)--> [AWARDED]
  |                         |                                 |
  +-------------------------+---------------------------------+--(cancel)-----> [CANCELLED]

Quotation:
[SUBMITTED] --(shortlist)--> [SHORTLISTED] --(award)--> [SELECTED]
  |                                |
  +--------------------------------+--(reject)--------> [REJECTED]
```

## 4. Purchase Order State Machine
```
[DRAFT] --(confirm)--> [CONFIRMED] --(partial receipt)--> [PARTIALLY_RECEIVED]
  |                        |                                     |
  |                        |                                     +--(full receipt)--> [FULLY_RECEIVED]
  |                        |                                                             |
  |                        +-------------------------------------------------------------+--(close)--> [CLOSED]
  |
  +--(cancel)--------> [CANCELLED]
```

## 5. Invoice & 3-Way Match State Machine
```
[UPLOADED] --(optional extract)--> [EXTRACTED] --(run match)--> [MATCHING]
                                                                   |
                 +-------------------------------------------------+
                 |                                                 |
                 v                                                 v
            [MATCHED]                                        [DISCREPANCY]
                 |                                                 |
                 |                                                 +--(finance override)
                 |                                                 |
                 +---------------------> [APPROVED] <--------------+
                                             |
                                             v
                                  [READY_FOR_PAYMENT]
```
