TRUNCATE master.transcript;

ALTER TABLE master.transcript
ADD COLUMN day_type VARCHAR NOT NULL;

UPDATE master.transcript_file
SET pending_processing = true;
