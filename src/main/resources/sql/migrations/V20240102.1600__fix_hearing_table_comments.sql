COMMENT ON TABLE master.hearing_file IS 'Listing of all hearing files';
COMMENT ON COLUMN master.hearing_file.filename IS 'The name of the hearing file.';
COMMENT ON COLUMN master.hearing_file.staged_date_time IS 'The date time this hearing was recorded into the database.';
COMMENT ON COLUMN master.hearing_file.processed_date_time IS 'The date time this hearing file was processed.';
COMMENT ON COLUMN master.hearing_file.processed_count IS 'The number of times this hearing file has been processed.';
COMMENT ON COLUMN master.hearing_file.pending_processing IS 'Indicates if this hearing file is waiting to be processed';
COMMENT ON COLUMN master.hearing_file.archived IS 'Indicates if this hearing file has been moved to the archive directory.';

COMMENT ON TABLE master.hearing IS 'Listing of all processed hearings';
COMMENT ON COLUMN master.hearing.filename IS 'The name of the file containing this hearing''s info.';
COMMENT ON COLUMN master.hearing.title IS 'The title of the hearing.';
COMMENT ON COLUMN master.hearing.address IS 'The address of this hearing.';
COMMENT ON COLUMN master.hearing.text IS 'The raw text of this hearing.';
COMMENT ON COLUMN master.hearing.date IS 'The date of the hearing';
COMMENT ON COLUMN master.hearing.start_time IS 'Time the hearing started.';
COMMENT ON COLUMN master.hearing.end_time IS 'Time the hearing ended.';

COMMENT ON COLUMN master.hearing_host.name IS 'The committee, Task Force, or other group holding a hearing.';

ALTER TABLE master.hearing_host_public_hearings
    RENAME COLUMN public_hearing_id TO hearing_id;

ALTER TABLE master.hearing_host_public_hearings
    RENAME TO hearing_host_hearing_id_pairs;

ALTER SEQUENCE master.public_hearing_id_seq
    RENAME TO hearing_id_seq;