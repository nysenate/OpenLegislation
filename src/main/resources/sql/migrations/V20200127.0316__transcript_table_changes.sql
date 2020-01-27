ALTER TABLE  master.transcript
    DROP CONSTRAINT transcript_transcript_file_fkey;
ALTER TABLE master.transcript_file
    ADD COLUMN date_time timestamp;
ALTER TABLE master.transcript_file
    ADD CONSTRAINT transcript_transcript_file_fkey FOREIGN KEY (date_time)
        REFERENCES master.transcript(date_time);