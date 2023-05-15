--Adds support for remote voting info.
CREATE TABLE master.bill_vote_remote_attendance(
    vote_date           TIMESTAMP without time zone NOT NULL,
    sequence_no         SMALLINT NOT NULL,
    vote_type           master.VOTE_TYPE NOT NULL,
    session_member_id   INT NOT NULL REFERENCES public.session_member,
    session_year        SMALLINT NOT NULL,
    published_date_time TIMESTAMP without time zone,
    modified_date_time  TIMESTAMP without time zone,
    created_date_time   TIMESTAMP without time zone NOT NULL DEFAULT now(),
    last_fragment_id    TEXT,
    PRIMARY KEY(vote_date, sequence_no, vote_type, session_member_id)
);

--Usually just returns the single relevant bill_pint_no,
--but a change to the attendance table means many changes to bill data.
CREATE FUNCTION master.bill_print_nums(curr_row RECORD, table_name TEXT)
    RETURNS TEXT []
    LANGUAGE plpgsql
AS $$
BEGIN
    IF table_name != 'bill_vote_remote_attendance'
        THEN RETURN ARRAY [curr_row.bill_print_no];
    ELSE
        RETURN ARRAY(SELECT bill_print_no
                     FROM master.bill_amendment_vote_info
                     WHERE curr_row.vote_date = vote_date
                         AND curr_row.sequence_no = sequence_no
                         AND curr_row.vote_type = vote_type);
    END IF;
END;$$;

--The session year is named slightly differently in the attendance table.
CREATE FUNCTION master.session_year(curr_row RECORD)
    RETURNS SMALLINT
    LANGUAGE plpgsql
AS
$$BEGIN
    IF hstore(curr_row.*) ? 'bill_session_year'
        THEN RETURN curr_row.bill_session_year;
    ELSE
        RETURN curr_row.session_year;
    END IF;
END;$$;

CREATE PROCEDURE master.bill_updates_helper(operation TEXT, old RECORD, new RECORD, table_name TEXT)
    LANGUAGE plpgsql
AS $$DECLARE
    bill_print_nums     TEXT []; -- Bill print no
    session_year        SMALLINT; -- Session year
    temp_print_num      TEXT; -- Used in for loop
    old_values          hstore; -- Old record key/value pairs
    new_values          hstore; -- New record key/value pairs
    data_diff           hstore; -- The data values that have been updated
    ignored_columns     TEXT [] := ARRAY ['bill_print_no', 'bill_session_year', 'session_year', 'modified_date_time', 'last_fragment_id']; -- Column names to exclude from data_diff
    fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
    published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id
BEGIN
    IF operation IN ('INSERT', 'UPDATE')
    THEN
        bill_print_nums := master.bill_print_nums(new, table_name);
        session_year := master.session_year(new);
        new_values := delete(hstore(new.*), ignored_columns);
        fragment_id := new.last_fragment_id;
        SELECT master.leg_data_fragment.published_date_time
            INTO published_date_time
        FROM master.leg_data_fragment
            WHERE master.leg_data_fragment.fragment_id = new.last_fragment_id;
    ELSE
        bill_print_nums := master.bill_print_nums(old, table_name);
        session_year := master.session_year(old);
        SELECT f.fragment_id, f.published_date_time
            INTO fragment_id, published_date_time
        FROM master.leg_data_fragment f
            WHERE f.process_start_date_time = (SELECT MAX(process_start_date_time) FROM master.leg_data_fragment);
    END IF;

    IF operation IN ('UPDATE', 'DELETE')
    THEN
        old_values := delete(hstore(old.*), ignored_columns);
    END IF;

    IF operation = 'INSERT'
    THEN
        data_diff := new_values;
    ELSIF operation = 'UPDATE'
    THEN
        data_diff := new_values - old_values;
    ELSE
        data_diff := old_values;
    END IF;

    -- Add the sobi change record only for inserts, deletes, and updates where the
    -- non-ignored values were actually changed.
    IF operation IN ('INSERT', 'DELETE') OR data_diff != '' :: hstore
    THEN
        FOREACH temp_print_num IN ARRAY bill_print_nums
            LOOP
                INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, leg_data_fragment_id, published_date_time)
                VALUES (temp_print_num, session_year, table_name, operation, data_diff, fragment_id, published_date_time);
            END LOOP;
    END IF;
END;$$;

--Calls the helper function almost directly.
CREATE OR REPLACE FUNCTION master.log_bill_updates()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$BEGIN
    CALL master.bill_updates_helper(TG_OP, OLD, NEW, TG_TABLE_NAME);
    IF TG_OP IN ('INSERT', 'UPDATE')
    THEN
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;$$;

--Calls the helper function with this table's data, but with the name of the
--attendance table. This way, updates to bill votes will display as attendance
--updates in the log.
CREATE FUNCTION master.attendance_updates()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$BEGIN
    CALL master.bill_updates_helper(TG_OP, OLD, NEW, 'bill_vote_remote_attendance');
    IF TG_OP IN ('INSERT', 'UPDATE')
    THEN
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;$$;

CREATE TRIGGER log_attendance_updates_to_change_log
    BEFORE INSERT OR DELETE OR UPDATE ON master.bill_vote_remote_attendance
    FOR EACH ROW EXECUTE PROCEDURE master.log_bill_updates();

CREATE TRIGGER bill_vote_triggers_attendance_update
    BEFORE INSERT OR DELETE OR UPDATE ON master.bill_amendment_vote_info
    FOR EACH ROW EXECUTE PROCEDURE master.attendance_updates();
