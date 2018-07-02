
-- Modify the log_bill_updates function, removing a debug log statement

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
    SELECT master.sobi_fragment.published_date_time
    INTO published_date_time
    FROM master.sobi_fragment
    WHERE master.sobi_fragment.fragment_id = NEW.last_fragment_id;
  ELSE
    bill_print_no := OLD.bill_print_no;
    bill_session_year := OLD.bill_session_year;
    SELECT f.fragment_id, f.published_date_time
    INTO fragment_id, published_date_time
    FROM master.sobi_fragment f
    WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.sobi_fragment);
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
    INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, sobi_fragment_id, published_date_time)
    VALUES (bill_print_no, bill_session_year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;

  IF TG_OP IN ('INSERT', 'UPDATE')
  THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;

END;$$;
