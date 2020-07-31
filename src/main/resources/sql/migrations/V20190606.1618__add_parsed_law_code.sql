-- Add and initialize year column
ALTER TABLE IF EXISTS master.bill_amendment
    ADD COLUMN IF NOT EXISTS related_laws JSON;
