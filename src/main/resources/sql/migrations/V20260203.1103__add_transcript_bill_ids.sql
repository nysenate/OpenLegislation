-- junction table to link transcripts and bills
CREATE TABLE IF NOT EXISTS master.transcript_bills(
    session_type text NOT NULL,
    date_time timestamp without time zone NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version char NOT NULL,
    FOREIGN KEY (date_time, session_type)
        REFERENCES master.transcript(date_time, session_type),
    FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version)
        REFERENCES master.bill_amendment(bill_print_no, bill_session_year, bill_amend_version)
);

UPDATE master.transcript_file
SET pending_processing = true;