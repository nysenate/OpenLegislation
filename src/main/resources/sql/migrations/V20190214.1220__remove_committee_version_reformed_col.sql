
--- Remove some redundant columns from the committee tables

ALTER TABLE master.committee_version
DROP COLUMN IF EXISTS reformed
;

ALTER TABLE master.committee
DROP COLUMN IF EXISTS current_version
;

ALTER TABLE master.committee
DROP COLUMN IF EXISTS current_session
;
