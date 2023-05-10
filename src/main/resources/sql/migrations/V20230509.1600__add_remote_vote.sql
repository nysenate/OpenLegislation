CREATE TABLE master.bill_vote_remote_attendance(
    vote_date           TIMESTAMP without time zone NOT NULL,
    sequence_no         SMALLINT NOT NULL,
    vote_type           master.VOTE_TYPE NOT NULL,
    session_year        SMALLINT NOT NULL,
    session_member_id   INT NOT NULL REFERENCES public.session_member,
    published_date_time TIMESTAMP without time zone,
    modified_date_time  TIMESTAMP without time zone,
    created_date_time   TIMESTAMP without time zone NOT NULL DEFAULT now(),
    last_fragment_id    TEXT,
    PRIMARY KEY(vote_date, sequence_no, vote_type)
);

--Usually just returns the single relevant bill_pint_no,
--but a change to the attendance table means many changes to bill data.
CREATE FUNCTION master.bill_print_nums(curr_row RECORD, table_name TEXT)
    RETURNS TEXT []
    LANGUAGE plpgsql
AS $$DECLARE
    bill_print_nums     TEXT[] := ARRAY [];
    temp_print_num      TEXT;
BEGIN
    IF table_name != 'bill_remote_vote'
        THEN EXECUTE array_append(bill_print_nums, curr_row.bill_print_no);
    ELSE
        FOR temp_print_num IN SELECT bill_print_no
                       FROM master.bill_amendment_vote_info
                       WHERE curr_row.vote_date = vote_date
                         AND curr_row.sequence_no = sequence_no
                         AND curr_row.vote_type = vote_type
        LOOP
            EXECUTE array_append(bill_print_nums, temp_print_num);
        END LOOP;
    END IF;
    RETURN bill_print_nums;
END;$$;

--The session year is named slightly differently in the attendance table.
CREATE FUNCTION master.session_year(curr_row RECORD, table_name TEXT)
    RETURNS SMALLINT
    LANGUAGE plpgsql
AS
$$BEGIN
    IF table_name != 'bill_remote_vote'
    THEN RETURN curr_row.bill_session_year;
    ELSE
        RETURN curr_row.session_year;
    END IF;
END;$$;

CREATE OR REPLACE FUNCTION master.log_bill_updates()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$DECLARE
    bill_print_nums     TEXT [] := ARRAY []; -- Bill print no
    bill_session_year   SMALLINT; -- Bill session year
    temp_print_num      TEXT; -- Used in for loop
    old_values          hstore; -- Old record key/value pairs
    new_values          hstore; -- New record key/value pairs
    data_diff           hstore; -- The data values that have been updated
    ignored_columns     TEXT [] := ARRAY ['bill_print_no', 'bill_session_year', 'session_year', 'modified_date_time', 'last_fragment_id']; -- Column names to exclude from data_diff
    fragment_id         TEXT := NULL; -- The fragment id that caused the insert/update
    published_date_time TIMESTAMP WITHOUT TIME ZONE := NULL; -- The published date derived from the fragment_id

BEGIN
    IF TG_OP IN ('INSERT', 'UPDATE')
    THEN
        bill_print_nums := master.bill_print_nums(NEW, TG_TABLE_NAME);
        bill_session_year := master.session_year(NEW, TG_TABLE_NAME);
        new_values := delete(hstore(NEW.*), ignored_columns);
        fragment_id := NEW.last_fragment_id;
        SELECT master.leg_data_fragment.published_date_time
            INTO published_date_time
        FROM master.leg_data_fragment
        WHERE master.leg_data_fragment.fragment_id = NEW.last_fragment_id;
    ELSE
        bill_print_nums := master.bill_print_nums(OLD, TG_TABLE_NAME);
        bill_session_year := master.session_year(OLD, TG_TABLE_NAME);
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
        FOREACH temp_print_num IN ARRAY bill_print_nums
        LOOP
            INSERT INTO master.bill_change_log (bill_print_no, bill_session_year, table_name, action, data, leg_data_fragment_id, published_date_time)
            VALUES (bill_print_no, bill_session_year, TG_TABLE_NAME, TG_OP, data_diff, fragment_id, published_date_time);
        END LOOP;
    END IF;

    IF TG_OP IN ('INSERT', 'UPDATE')
    THEN
        RETURN NEW;
    ELSE
        RETURN OLD;
    END IF;
END;$$;
