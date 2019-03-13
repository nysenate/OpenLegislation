CREATE OR REPLACE FUNCTION master.log_bill_updates()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $$DECLARE
  bill_print_no       TEXT; -- Bill print no
  bill_session_year   SMALLINT; -- Bill session year
  old_values          hstore; -- Old record key/value pairs
  new_values          hstore; -- New record key/value pairs
  data_diff           hstore; -- The data values that have been updated
  ignored_columns     TEXT []; -- Column names to exclude from data_diff
  fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
  published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id

BEGIN
  ignored_columns := ARRAY ['bill_print_no', 'bill_session_year', 'modified_date_time', 'last_fragment_id'];

  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    bill_print_no := NEW.bill_print_no;
    bill_session_year := NEW.bill_session_year;
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;
    SELECT master.leg_data_fragment.published_date_time
           INTO published_date_time
    FROM master.leg_data_fragment
    WHERE master.leg_data_fragment.fragment_id = NEW.last_fragment_id;
  ELSE
    bill_print_no := OLD.bill_print_no;
    bill_session_year := OLD.bill_session_year;
    SELECT f.fragment_id, f.published_date_time
           INTO fragment_id, published_date_time
    FROM master.leg_data_fragment f
    WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.leg_data_fragment);
  END IF;

  IF TG_OP IN ('UPDATE', 'DELETE')
  THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;

  IF TG_OP = 'INSERT'
  THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE'
  THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;

  -- Add the sobi change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT', 'DELETE') OR data_diff != '' :: hstore
  THEN
    INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, leg_data_fragment_id, published_date_time)
    VALUES (bill_print_no, bill_session_year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;

  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;

END;$$;

CREATE OR REPLACE FUNCTION master.log_agenda_updates()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $$DECLARE
  agenda_no           SMALLINT; -- Agenda no
  year                SMALLINT; -- Agenda year
  old_values          hstore; -- Old record key/value pairs
  new_values          hstore; -- New record key/value pairs
  data_diff           hstore; -- The data values that have been updated
  ignored_columns     TEXT []; -- Column names to exclude from data_diff
  fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
  published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id
BEGIN
  ignored_columns := ARRAY ['agenda_no', 'year', 'modified_date_time', 'last_fragment_id'];
  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    agenda_no := NEW.agenda_no;
    year := NEW.year;
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;
    SELECT master.leg_data_fragment.published_date_time
           INTO published_date_time
    FROM master.leg_data_fragment
    WHERE master.leg_data_fragment.fragment_id = NEW.last_fragment_id;
  ELSE
    agenda_no := OLD.agenda_no;
    year := OLD.year;
    SELECT f.fragment_id, f.published_date_time
           INTO fragment_id, published_date_time
    FROM master.leg_data_fragment f
    WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.leg_data_fragment);
  END IF;
  IF TG_OP IN ('UPDATE', 'DELETE')
  THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;
  IF TG_OP = 'INSERT'
  THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE'
  THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;
  -- Add the leg data change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT', 'DELETE') OR data_diff != '' :: hstore
  THEN
    INSERT INTO master.agenda_change_log (agenda_no, year, table_name, action, data, leg_data_fragment_id, published_date_time)
    VALUES (agenda_no, year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;
  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;$$;

CREATE OR REPLACE FUNCTION master.log_calendar_updates()
  RETURNS TRIGGER
  LANGUAGE plpgsql
AS $$DECLARE
  calendar_no         SMALLINT; -- Calendar no
  calendar_year       SMALLINT; -- Calendar year
  old_values          hstore; -- Old record key/value pairs
  new_values          hstore; -- New record key/value pairs
  data_diff           hstore; -- The data values that have been updated
  ignored_columns     TEXT []; -- Column names to exclude from data_diff
  fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
  published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id
BEGIN
  ignored_columns := ARRAY ['calendar_no', 'calendar_year', 'modified_date_time', 'last_fragment_id'];
  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    calendar_no := NEW.calendar_no;
    calendar_year := NEW.calendar_year;
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;
    SELECT master.leg_data_fragment.published_date_time
           INTO published_date_time
    FROM master.leg_data_fragment
    WHERE master.leg_data_fragment.fragment_id = NEW.last_fragment_id;
  ELSE
    calendar_no := OLD.calendar_no;
    calendar_year := OLD.calendar_year;
    SELECT f.fragment_id, f.published_date_time
           INTO fragment_id, published_date_time
    FROM master.leg_data_fragment f
    WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.leg_data_fragment);
  END IF;
  IF TG_OP IN ('UPDATE', 'DELETE')
  THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;
  IF TG_OP = 'INSERT'
  THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE'
  THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;
  -- Add the leg data change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT', 'DELETE') OR data_diff != '' :: hstore
  THEN
    INSERT INTO master.calendar_change_log (calendar_no, calendar_year, table_name, action, data, leg_data_fragment_id, published_date_time)
    VALUES (calendar_no, calendar_year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;
  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;$$;

ALTER TYPE master.sobi_fragment_type RENAME TO leg_data_fragment_type;

ALTER TABLE master.agenda_change_log
RENAME COLUMN sobi_fragment_id TO leg_data_fragment_id;

COMMENT ON COLUMN master.bill.last_fragment_id IS 'Reference to the last leg data fragment that caused an update';

ALTER TABLE master.bill_change_log
RENAME COLUMN sobi_fragment_id TO leg_data_fragment_id;

ALTER TABLE master.calendar_change_log
RENAME COLUMN sobi_fragment_id TO leg_data_fragment_id;

COMMENT ON COLUMN master.committee_version.last_fragment_id IS 'Reference to the leg data fragment that last updated this record';

ALTER TABLE master.sobi_fragment
RENAME COLUMN sobi_file_name TO leg_data_file_name;

ALTER TABLE master.sobi_fragment
RENAME TO leg_data_fragment;

COMMENT ON TABLE master.leg_data_fragment IS 'Listing of all leg data fragments which are extracted from leg data files.';

COMMENT ON COLUMN master.leg_data_fragment.leg_data_file_name IS 'The name of the originating leg data file';

COMMENT ON COLUMN master.leg_data_fragment.fragment_id IS 'A unique id for this fragment';

COMMENT ON COLUMN master.leg_data_fragment.published_date_time IS 'The date this fragment was published';

COMMENT ON COLUMN master.leg_data_fragment.fragment_type IS 'The type of data this fragment contains';

COMMENT ON COLUMN master.leg_data_fragment.text IS 'The text body of the fragment';

COMMENT ON COLUMN master.leg_data_fragment.sequence_no IS 'Preserves the order in which fragments are found in a leg data file';

COMMENT ON COLUMN master.leg_data_fragment.processed_count IS 'The number of times this fragment has been processed';

COMMENT ON COLUMN master.leg_data_fragment.processed_date_time IS 'The last date/time this fragment was processed';

COMMENT ON COLUMN master.leg_data_fragment.staged_date_time IS 'The date/time when this fragment was recorded into the database';

COMMENT ON COLUMN master.leg_data_fragment.pending_processing IS 'Indicates if the fragment is waiting to be processed';

COMMENT ON COLUMN master.leg_data_fragment.manual_fix IS 'Indicates if the contents of the fragment were altered manually';

COMMENT ON COLUMN master.leg_data_fragment.manual_fix_notes IS 'Description of any manual changes made (if applicable)';

COMMENT ON COLUMN master.leg_data_fragment.process_start_date_time IS 'Timestamp of the last time processing was initiated for this fragment';

CREATE OR REPLACE VIEW psf AS

SELECT leg_data_fragment.leg_data_file_name,
       leg_data_fragment.fragment_id,
       leg_data_fragment.published_date_time,
       leg_data_fragment.fragment_type,
       leg_data_fragment.text,
       leg_data_fragment.sequence_no,
       leg_data_fragment.processed_count,
       leg_data_fragment.processed_date_time,
       leg_data_fragment.staged_date_time,
       leg_data_fragment.pending_processing
FROM master.leg_data_fragment
WHERE (leg_data_fragment.pending_processing = true);

COMMENT ON VIEW psf IS 'Pending leg data Fragments';

ALTER TABLE master.sobi_file
RENAME TO leg_data_file;

COMMENT ON TABLE master.leg_data_file IS 'Listing of all leg data files';

COMMENT ON COLUMN master.leg_data_file.file_name IS 'The name of the leg data file';

ALTER SEQUENCE master.sobi_fragment_process_id_seq
RENAME TO leg_data_fragment_process_id_seq;

ALTER TABLE ONLY master.data_process_run ALTER COLUMN id SET DEFAULT nextval('master.leg_data_fragment_process_id_seq'::regclass);

ALTER TABLE master.leg_data_fragment RENAME CONSTRAINT sobi_fragment_pkey TO leg_data_fragment_pkey;

ALTER TABLE master.data_process_run RENAME CONSTRAINT sobi_fragment_process_pkey TO leg_data_fragment_process_pkey;

ALTER TABLE master.leg_data_file RENAME CONSTRAINT sobi_pkey TO leg_data_pkey;

ALTER INDEX master.agenda_change_log_sobi_fragment_id_idx RENAME TO agenda_change_log_leg_data_fragment_id_idx;

ALTER INDEX master.bill_change_log_sobi_fragment_id_idx RENAME TO  bill_change_log_leg_data_fragment_id_idx;

ALTER INDEX master.calendar_change_log_sobi_fragment_id_idx RENAME TO calendar_change_log_leg_data_fragment_id_idx;

ALTER INDEX master.law_change_log_sobi_fragment_id_idx RENAME TO law_change_log_leg_data_fragment_id_idx;

ALTER INDEX master.sobi_fragment_published_date_time_idx RENAME TO leg_data_fragment_published_date_time_idx;

ALTER INDEX master.sobi_fragment_process_start_date_time_idx RENAME TO leg_data_fragment_process_start_date_time_idx;