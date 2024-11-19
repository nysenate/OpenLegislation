ALTER TABLE master.transcript
DROP CONSTRAINT transcript_transcript_file_fkey;

ALTER TABLE master.hearing
DROP CONSTRAINT public_hearing_filename_fkey;

ALTER TABLE master.transcript_file
DROP CONSTRAINT transcript_file_pkey;

ALTER TABLE master.transcript_file
RENAME COLUMN file_name TO filename;

ALTER TABLE master.transcript_file
ADD PRIMARY KEY (filename);

ALTER TABLE master.transcript
ADD CONSTRAINT transcript_transcript_file_fkey FOREIGN KEY (transcript_filename)
    REFERENCES master.transcript_file(filename) ON UPDATE CASCADE;

ALTER TABLE master.hearing
ADD CONSTRAINT hearing_transcript_file_fkey FOREIGN KEY (filename)
    REFERENCES master.hearing_file(filename) ON UPDATE CASCADE;