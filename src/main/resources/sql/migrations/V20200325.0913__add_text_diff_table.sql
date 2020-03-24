CREATE TABLE master.bill_amendment_text_diff (
  bill_amendment_text_diff_id SERIAL PRIMARY KEY,
  bill_print_no text NOT NULL,
  bill_session_year smallint NOT NULL,
  bill_amend_version character(1) NOT NULL,
  type text NOT NULL,
  text text NOT NULL
);

ALTER TABLE master.bill_amendment_text_diff
ADD CONSTRAINT bill_amendment_text_diff_bill_amend_fkey
FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version)
REFERENCES master.bill_amendment(bill_print_no, bill_session_year, bill_amend_version);
