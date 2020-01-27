ALTER TABLE master.transcript
    DROP CONSTRAINT transcript_pkey;
ALTER TABLE master.transcript
    ADD PRIMARY KEY (date_time);