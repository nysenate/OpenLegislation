TRUNCATE master.transcript;

ALTER TABLE master.transcript
ADD COLUMN day_type VARCHAR;

UPDATE master.transcript_file
SET pending_processing = true;
