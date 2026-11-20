-- Phase 1 Migration: Soft-delete columns and seed users for each role

ALTER TABLE departments ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT false;

-- Seed Default Departments (Idempotent ANSI SQL)
INSERT INTO departments (name, budget_total, currency, fiscal_year)
SELECT d.name, d.budget_total, d.currency, d.fiscal_year
FROM (
    VALUES
    ('Engineering', 5000000.00, 'INR', '2026-2027'),
    ('Operations', 3500000.00, 'INR', '2026-2027'),
    ('Marketing', 2000000.00, 'INR', '2026-2027'),
    ('Human Resources', 1500000.00, 'INR', '2026-2027'),
    ('Finance', 2500000.00, 'INR', '2026-2027'),
    ('Procurement', 1800000.00, 'INR', '2026-2027'),
    ('Information Technology', 4000000.00, 'INR', '2026-2027'),
    ('Facilities', 2200000.00, 'INR', '2026-2027')
) AS d(name, budget_total, currency, fiscal_year)
WHERE NOT EXISTS (SELECT 1 FROM departments WHERE departments.name = d.name);

-- Seed Warehouses (Idempotent ANSI SQL)
INSERT INTO warehouses (code, name, address)
SELECT w.code, w.name, w.address
FROM (
    VALUES
    ('WH-MUM-01', 'Mumbai Central Logistics Hub', 'Plot 42, MIDC Industrial Area, Mumbai, MH'),
    ('WH-BLR-01', 'Bengaluru High-Tech Storage', 'Electronic City Phase 1, Bengaluru, KA')
) AS w(code, name, address)
WHERE NOT EXISTS (SELECT 1 FROM warehouses WHERE warehouses.code = w.code);
