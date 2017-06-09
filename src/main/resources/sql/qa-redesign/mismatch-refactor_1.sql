
CREATE TABLE master.spotcheck_mismatch_old
AS SELECT * FROM master.spotcheck_mismatch;

ALTER TABLE master.spotcheck_mismatch
DROP CONSTRAINT IF EXISTS spotcheck_mismatch_observation_id_fkey;

ALTER TABLE master.spotcheck_mismatch_issue_id
DROP CONSTRAINT IF EXISTS spotcheck_mismatch_issue_id_mismatch_id_fkey;

DROP TABLE master.spotcheck_mismatch;

CREATE TABLE master.spotcheck_mismatch (
  mismatch_id integer NOT NULL,
  key public.hstore NOT NULL,
  type text NOT NULL,
  report_id integer NOT NULL,
  datasource text NOT NULL,
  content_type text NOT NULL,
  reference_type text NOT NULL,
  status text NOT NULL,
  reference_data text NOT NULL,
  observed_data text NOT NULL,
  notes text,
  issue_ids text[] DEFAULT ARRAY[]::text[] NOT NULL,
  ignore_status text DEFAULT 'NOT_IGNORED' NOT NULL,
  report_date_time timestamp without time zone NOT NULL,
  observed_date_time timestamp without time zone NOT NULL,
  reference_active_date_time timestamp without time zone NOT NULL,
  created_date_time timestamp without time zone DEFAULT now() NOT NULL
);

ALTER TABLE ONLY master.spotcheck_mismatch OWNER TO postgres;

CREATE SEQUENCE master.spotcheck_mismatch_mismatch_id_seq
  START WITH 500000
  INCREMENT BY 1
  NO MINVALUE
  NO MAXVALUE
  CACHE 1;

ALTER TABLE master.spotcheck_mismatch ALTER COLUMN mismatch_id SET DEFAULT nextval('master.spotcheck_mismatch_mismatch_id_seq');
ALTER SEQUENCE master.spotcheck_mismatch_mismatch_id_seq OWNER TO postgres;

ALTER TABLE ONLY master.spotcheck_mismatch
  ADD CONSTRAINT spotcheck_mismatch_mismatch_id_pkey PRIMARY KEY (mismatch_id);

ALTER TABLE ONLY master.spotcheck_mismatch
  ADD CONSTRAINT spotcheck_mismatch_report_id_fkey FOREIGN KEY (report_id) REFERENCES master.spotcheck_report (id)
  ON UPDATE CASCADE ON DELETE CASCADE;


CREATE TEMP TABLE ref_map (datasource text, content_type text, reference_type text);
INSERT INTO ref_map(datasource, content_type, reference_type)
VALUES
('LBDC', 'BILL', 'LBDC_DAYBREAK'),
('LBDC', 'BILL', 'LBDC_SCRAPED_BILL'),
('LBDC', 'CALENDAR', 'LBDC_CALENDAR_ALERT'),
('LBDC', 'AGENDA', 'LBDC_AGENDA_ALERT'),
('NYSENATE', 'BILL', 'SENATE_SITE_BILLS'),
('NYSENATE', 'CALENDAR', 'SENATE_SITE_CALENDAR'),
('NYSENATE', 'AGENDA', 'SENATE_SITE_AGENDA')
;

CREATE TEMP TABLE ignore_level (ignore_string text, ignore_int integer);
INSERT INTO ignore_level (ignore_string, ignore_int)
VALUES
('NOT_IGNORED', -1),
('IGNORE_PERMANENTLY', 0),
('IGNORE_UNTIL_RESOLVED', 1),
('IGNORE_ONCE', 2)
;


INSERT INTO master.spotcheck_mismatch (
  report_id,
  mismatch_id,
  key,
  datasource,
  content_type,
  reference_type,
  type,
  status,
  reference_data,
  observed_data,
  notes,
  ignore_status,
  report_date_time,
  observed_date_time,
  reference_active_date_time,
  created_date_time
)
SELECT r.id as report_id,
  m.id as mismatch_id,
  o.key,
  (SELECT datasource from ref_map where ref_map.reference_type = o.reference_type) as datasource,
  (SELECT content_type from ref_map where ref_map.reference_type = o.reference_type) as type,
  o.reference_type,
  m.type as mismatch_type,
  m.status as status,
  m.reference_data,
  m.observed_data,
  m.notes,
  COALESCE ((SELECT ignore_string FROM ignore_level il WHERE il.ignore_int = ignore.ignore_level), 'NOT_IGNORED'),
  r.report_date_time,
  o.observed_date_time,
  o.reference_active_date as reference_active_date_time,
  o.created_date_time
FROM master.spotcheck_report r
  JOIN master.spotcheck_observation o
    ON o.report_id = r.id
  JOIN master.spotcheck_mismatch_old m
    ON m.observation_id = o.id
  LEFT JOIN master.spotcheck_mismatch_ignore ignore
    ON ignore.key = o.key AND ignore.reference_type = o.reference_type AND ignore.mismatch_type = m.type
  LEFT JOIN master.spotcheck_mismatch_issue_id issue
    ON issue.mismatch_id = m.id;

ANALYZE master.spotcheck_mismatch;

CREATE INDEX spotcheck_mismatch_datasource_content_type_ref_date_time_index
on master.spotcheck_mismatch (datasource, content_type, reference_active_date_time);
