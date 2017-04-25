CREATE OR REPLACE FUNCTION master.log_agenda_updates() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
  agenda_no smallint;          -- Agenda no
  year smallint;               -- Agenda year
  old_values hstore;           -- Old record key/value pairs
  new_values hstore;           -- New record key/value pairs
  data_diff hstore;            -- The data values that have been updated
  ignored_columns text[];      -- Column names to exclude from data_diff
  fragment_id text := NULL;    -- The fragment id that caused the insert/update
  published_date_time timestamp without time zone := NULL; -- The published date derived from the fragment_id
BEGIN
  ignored_columns := ARRAY['agenda_no', 'year', 'modified_date_time', 'last_fragment_id'];
  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    agenda_no := NEW.agenda_no;
    year := NEW.year;
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;
    SELECT master.sobi_fragment.published_date_time INTO published_date_time FROM master.sobi_fragment WHERE master.sobi_fragment.fragment_id = NEW.last_fragment_id;
  ELSE
    agenda_no := OLD.agenda_no;
    year := OLD.year;
  END IF;
  IF TG_OP IN ('UPDATE', 'DELETE') THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;
  IF TG_OP = 'INSERT' THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE' THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;
  -- Add the sobi change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT','DELETE') OR data_diff != ''::hstore THEN
    INSERT INTO master.agenda_change_log (agenda_no, year, table_name, action, data, sobi_fragment_id, published_date_time)
    VALUES (agenda_no, year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;
  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;
END;$$;



CREATE FUNCTION master.log_bill_updates() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
  bill_print_no text;          -- Bill print no
  bill_session_year smallint;  -- Bill session year
  old_values hstore;           -- Old record key/value pairs
  new_values hstore;           -- New record key/value pairs
  data_diff hstore;            -- The data values that have been updated
  ignored_columns text[];      -- Column names to exclude from data_diff
  fragment_id text := NULL;    -- The fragment id that caused the insert/update
  published_date_time timestamp without time zone := NULL; -- The published date derived from the fragment_id

BEGIN
  ignored_columns := ARRAY['bill_print_no', 'bill_session_year', 'modified_date_time', 'last_fragment_id'];

  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    bill_print_no := NEW.bill_print_no;
    bill_session_year := NEW.bill_session_year;
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;
    SELECT master.sobi_fragment.published_date_time INTO published_date_time FROM master.sobi_fragment WHERE master.sobi_fragment.fragment_id = NEW.last_fragment_id;
    RAISE NOTICE 'published_date_time is currently: %', published_date_time;
  ELSE
    bill_print_no := OLD.bill_print_no;
    bill_session_year := OLD.bill_session_year;
  END IF;

  IF TG_OP IN ('UPDATE', 'DELETE') THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;

  IF TG_OP = 'INSERT' THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE' THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;

  -- Add the sobi change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT','DELETE') OR data_diff != ''::hstore THEN
    INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, sobi_fragment_id, published_date_time)
    VALUES (bill_print_no, bill_session_year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
  END IF;

  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;

END;$$;
