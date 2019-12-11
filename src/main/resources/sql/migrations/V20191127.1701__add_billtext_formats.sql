ALTER TABLE master.bill_amendment
    ADD COLUMN full_text_html5 text;

ALTER TABLE master.bill_amendment
    ADD COLUMN full_text_diff text;

COMMENT ON COLUMN master.bill_amendment.full_text_html5 IS 'HTML5 compliant version of full_text_html with default span classes ol-text-xxx.';
COMMENT ON COLUMN master.bill_amendment.full_text_diff IS 'diff-match-patch style text diff for full_text. JSON string stored.';