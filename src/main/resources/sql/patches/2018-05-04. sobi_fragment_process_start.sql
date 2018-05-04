
SET SEARCH_PATH = master;

-- Add a new indexed field to the sobi fragment table storing the last time processing was initiated on the file.
ALTER TABLE sobi_fragment
ADD COLUMN process_start_date_time TIMESTAMP WITHOUT TIME ZONE;

CREATE INDEX sobi_fragment_process_start_date_time_idx ON sobi_fragment USING BTREE (process_start_date_time);

COMMENT ON COLUMN sobi_fragment.process_start_date_time IS 'Timestamp of the last time processing was initiated for this fragment';

UPDATE sobi_fragment
SET process_start_date_time = processed_date_time
WHERE process_start_date_time IS NULL;
