-- Adds a redistricting_sponsor column to represent bills sponsored by the Independent redistricting commission.
ALTER TABLE master.bill_sponsor
ADD COLUMN redistricting_sponsor boolean
NOT NULL DEFAULT false;
