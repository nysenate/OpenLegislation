ALTER TABLE master.bill_amendment
    ADD COLUMN full_text_html5 text;

ALTER TABLE master.bill_amendment
    ADD COLUMN full_text_diff text;
