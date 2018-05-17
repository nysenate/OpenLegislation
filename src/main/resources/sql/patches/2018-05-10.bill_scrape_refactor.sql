DROP TABLE IF EXISTS master.bill_text_reference;

CREATE TABLE IF NOT EXISTS master.bill_scrape_file (
  file_name text NOT NULL,
  file_path text NOT NULL,
  staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
  is_archived boolean DEFAULT false NOT NULL,
  is_pending_processing boolean DEFAULT true NOT NULL,
  CONSTRAINT bill_scrape_file_pk PRIMARY KEY(file_name)
);

ALTER TABLE master.bill_scrape_file OWNER TO postgres;

COMMENT ON COLUMN master.bill_scrape_file.file_path IS 'The directory where this file is located';

COMMENT ON COLUMN master.bill_scrape_file.staged_date_time IS 'The date time this entry was saved into the database.';

COMMENT ON COLUMN master.bill_scrape_file.is_pending_processing IS 'Indicates if this file is waiting to be processed by a spotcheck report.';