SET SEARCH_PATH = master;

ALTER TABLE bill_amendment
ADD COLUMN full_text_html text;

COMMENT ON COLUMN bill_amendment.full_text_html IS 'A marked up version of full text.';
