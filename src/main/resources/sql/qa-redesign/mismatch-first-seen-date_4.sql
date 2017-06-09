
ALTER TABLE master.spotcheck_mismatch
ADD COLUMN first_seen_date_time timestamp without time zone;

UPDATE master.spotcheck_mismatch
SET first_seen_date_time = observed_date_time;

ALTER TABLE master.spotcheck_mismatch
ALTER COLUMN first_seen_date_time SET NOT NULL