
-- Change status to be either 'OPEN' or 'CLOSED'
-- Then rename 'status' column to 'state'

CREATE TEMP TABLE status_map (status text, new_status text);
INSERT INTO status_map(status, new_status)
VALUES
('NEW', 'OPEN'),
('EXISTING', 'OPEN'),
('REGRESSION', 'OPEN'),
('RESOLVED', 'CLOSED')
;

UPDATE master.spotcheck_mismatch m
SET status = (SELECT new_status FROM status_map sm WHERE sm.status = m.status);


ALTER TABLE master.spotcheck_mismatch
RENAME status TO state;
