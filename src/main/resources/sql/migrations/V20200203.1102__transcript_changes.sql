ALTER TABLE master.transcript
    DROP CONSTRAINT transcript_pkey;
ALTER TABLE master.transcript
    ADD PRIMARY KEY (date_time);

ALTER TABLE  master.transcript
    DROP CONSTRAINT IF EXISTS transcript_transcript_file_fkey;
ALTER TABLE master.transcript
    ADD CONSTRAINT transcript_transcript_file_fkey FOREIGN KEY (transcript_filename)
        REFERENCES master.transcript_file(file_name);

ALTER TABLE master.transcript_file
    ADD COLUMN IF NOT EXISTS date_time timestamp;
ALTER TABLE master.transcript_file
    ADD COLUMN IF NOT EXISTS original_filename text;