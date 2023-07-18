-- Clears out old functions.
DROP TRIGGER log_attendance_updates_to_change_log ON master.bill_vote_remote_attendance;
DROP TRIGGER bill_vote_triggers_attendance_update ON master.bill_amendment_vote_info;
DROP FUNCTION master.bill_print_nums(curr_row RECORD, table_name TEXT);
DROP FUNCTION master.session_year(curr_row RECORD);
DROP PROCEDURE master.bill_updates_helper(operation TEXT, old RECORD, new RECORD, table_name TEXT);
DROP FUNCTION master.attendance_updates();

-- Return values of triggers have a common structure.
CREATE FUNCTION master.return_from_trigger(operation TEXT, old RECORD, new RECORD)
    RETURNS RECORD
    LANGUAGE plpgsql
AS $$
BEGIN
    IF operation IN ('INSERT', 'UPDATE')
    THEN
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;$$;

-- Simply gets bill information from a record.
CREATE FUNCTION master.get_bill_info_hstore(rec RECORD)
    RETURNS public.hstore
    LANGUAGE plpgsql
AS $$BEGIN
    RETURN public.hstore(ARRAY['bill_session_year', rec.bill_session_year::text, 'bill_print_no', rec.bill_print_no::text]);
END;$$;

-- An insert into the change log labeled as from the remote attendance table
-- may come from that table's trigger, or the related vote info table.
-- This combines some common code between them.
CREATE PROCEDURE master.attendance_trigger_helper(
    triggered_from_info BOOLEAN, to_match RECORD,
    old_input public.hstore, new_input public.hstore, operation TEXT)
    LANGUAGE plpgsql
AS $$DECLARE
    temp_record         RECORD;
    old_hstore          public.hstore := ''::public.hstore;
    new_hstore          public.hstore := ''::public.hstore;
    fields              TEXT;
    source_table               TEXT;
BEGIN
    -- We'll need to augment our data with data from the complementary table.
    IF triggered_from_info THEN
        fields := '*';
        source_table = '"master"."bill_vote_remote_attendance"';
    ELSE
        fields = '"bill_print_no", "bill_session_year"';
        source_table = '"master"."bill_amendment_vote_info"';
    end if;
    FOR temp_record IN EXECUTE
        'SELECT ' || fields ||
        ' FROM ' || source_table ||
        ' WHERE vote_date = $1
          AND sequence_no = $2
          AND vote_type = $3' USING to_match.vote_date, to_match.sequence_no, to_match.vote_type
        LOOP
            IF operation != 'INSERT' THEN
                old_hstore := old_input || public.hstore(temp_record.*);
            END IF;
            IF operation != 'DELETE' THEN
                new_hstore := new_input || public.hstore(temp_record.*);
            END IF;
            CALL master.bill_updates_helper(operation, old_hstore, new_hstore, 'bill_vote_remote_attendance');
        END LOOP;
END;$$;

-- Mimics a trigger on a normal bill table.
CREATE FUNCTION master.attendance_trigger()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    CALL master.attendance_trigger_helper(false,coalesce(OLD, NEW),
        public.hstore(OLD.*), public.hstore(NEW.*), TG_OP);
    RETURN master.return_from_trigger(TG_OP, OLD, NEW);
END;$$;

-- A trigger is needed on the bill_info table as well, in case remote attendance data comes first.
CREATE FUNCTION master.bill_info_triggers_attendance()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$DECLARE
    old_bill_info       public.hstore := ''::public.hstore;
    new_bill_info       public.hstore := ''::public.hstore;
    to_match            RECORD;
BEGIN
    IF TG_OP = 'INSERT' THEN
        new_bill_info := master.get_bill_info_hstore(NEW);
        to_match := NEW;
    ELSE
        old_bill_info := master.get_bill_info_hstore(OLD);
        to_match := OLD;
    END IF;
    CALL master.attendance_trigger_helper(true,to_match,
        old_bill_info,new_bill_info, TG_OP);

    RETURN master.return_from_trigger(TG_OP, OLD, NEW);
END;$$;

CREATE PROCEDURE master.bill_updates_helper(operation TEXT, old public.hstore, new public.hstore, table_name TEXT)
    LANGUAGE plpgsql
AS $$DECLARE
    bill_print_no       TEXT;
    bill_session_year   SMALLINT;
    data_diff           public.hstore; -- The data values that have been updated
    ignored_columns     TEXT [] := ARRAY ['bill_print_no', 'bill_session_year', 'session_year', 'modified_date_time', 'last_fragment_id']; -- Column names to exclude from data_diff
    fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
    published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id
BEGIN
    IF operation IN ('INSERT', 'UPDATE') THEN
        bill_print_no := new -> 'bill_print_no';
        bill_session_year := new -> 'bill_session_year';
        data_diff := public.delete(new, ignored_columns) - old;
        SELECT f.fragment_id, f.published_date_time
            INTO fragment_id, published_date_time
        FROM master.leg_data_fragment f
        WHERE f.fragment_id = new -> 'last_fragment_id';
    ELSE
        bill_print_no := old -> 'bill_print_no';
        bill_session_year := old -> 'bill_session_year';
        data_diff = public.delete(old, ignored_columns);
        SELECT f.fragment_id, f.published_date_time
            INTO fragment_id, published_date_time
        FROM master.leg_data_fragment f
        WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.leg_data_fragment);
    END IF;

    -- Add the change record only for inserts, deletes, and updates where the
    -- non-ignored values were actually changed.
    IF operation IN ('INSERT', 'DELETE') OR data_diff != ''::public.hstore THEN
        INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, leg_data_fragment_id, published_date_time)
        VALUES (bill_print_no, bill_session_year, table_name, operation, data_diff, fragment_id, published_date_time);
    END IF;
END;$$;

--Calls the helper function almost directly.
CREATE OR REPLACE FUNCTION master.log_bill_updates()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$BEGIN
    CALL master.bill_updates_helper(TG_OP, public.hstore(OLD.*), public.hstore(NEW.*), TG_TABLE_NAME);
    RETURN master.return_from_trigger(TG_OP, OLD, NEW);
END;$$;

CREATE TRIGGER log_attendance_updates
    BEFORE INSERT OR DELETE OR UPDATE ON master.bill_vote_remote_attendance
    FOR EACH ROW EXECUTE PROCEDURE master.attendance_trigger();

--Updating the bill info doesn't change attendance data.
CREATE TRIGGER bill_vote_attendance_update_log
    BEFORE INSERT OR DELETE ON master.bill_amendment_vote_info
    FOR EACH ROW EXECUTE PROCEDURE master.bill_info_triggers_attendance();
