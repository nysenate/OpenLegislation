SET SEARCH_PATH = master;

DROP INDEX IF EXISTS data_process_run_unit_start_date_time_idx;
CREATE INDEX data_process_run_unit_idx ON data_process_run_unit USING btree (start_date_time, process_id);
CREATE INDEX data_process_run_unit_process_id_idx ON data_process_run_unit USING btree (process_id);