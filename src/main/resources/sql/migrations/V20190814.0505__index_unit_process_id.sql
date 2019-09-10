SET SEARCH_PATH = master;

-- Remove old index which was not getting used.
DROP INDEX data_process_run_unit_start_date_time_idx;

-- Create a new index for both process id and start date time.
CREATE INDEX data_process_run_unit_process_id_start_date_time ON data_process_run_unit USING btree (process_id, start_date_time);
