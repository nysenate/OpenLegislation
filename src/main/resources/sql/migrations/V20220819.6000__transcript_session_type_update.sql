ALTER TABLE master.transcript_file
    DROP COLUMN date_time, DROP COLUMN original_filename;

ALTER TABLE master.transcript
    DROP CONSTRAINT transcript_pkey,
    ADD PRIMARY KEY (date_time, session_type);
