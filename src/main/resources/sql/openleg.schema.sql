--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: master; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA master;


ALTER SCHEMA master OWNER TO postgres;

--
-- Name: SCHEMA master; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA master IS 'Processed legislative data';


SET search_path = master, pg_catalog;

--
-- Name: cmt; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE cmt AS ENUM (
    'chair_person',
    'vicechair',
    'member'
);


ALTER TYPE master.cmt OWNER TO postgres;

--
-- Name: committee_member_title; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE committee_member_title AS ENUM (
    'chair_person',
    'vice_chair',
    'member'
);


ALTER TYPE master.committee_member_title OWNER TO postgres;

--
-- Name: daybreak_file_type; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE daybreak_file_type AS ENUM (
    'page_file',
    'senate_low',
    'assembly_low',
    'senate_high',
    'assembly_high'
);


ALTER TYPE master.daybreak_file_type OWNER TO postgres;

--
-- Name: sobi_fragment_type; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE sobi_fragment_type AS ENUM (
    'bill',
    'agenda',
    'agenda_vote',
    'calendar',
    'calendar_active',
    'committee',
    'annotation'
);


ALTER TYPE master.sobi_fragment_type OWNER TO postgres;

--
-- Name: sponsor_type; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE sponsor_type AS ENUM (
    'cosponsor',
    'multisponsor'
);


ALTER TYPE master.sponsor_type OWNER TO postgres;

--
-- Name: veto_type; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE veto_type AS ENUM (
    'standard',
    'line_item'
);


ALTER TYPE master.veto_type OWNER TO postgres;

--
-- Name: vote_code; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE vote_code AS ENUM (
    'aye',
    'nay',
    'exc',
    'abs',
    'abd',
    'ayewr'
);


ALTER TYPE master.vote_code OWNER TO postgres;

--
-- Name: vote_type; Type: TYPE; Schema: master; Owner: postgres
--

CREATE TYPE vote_type AS ENUM (
    'floor',
    'committee'
);


ALTER TYPE master.vote_type OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- Name: chamber; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE chamber AS ENUM (
    'assembly',
    'senate'
);


ALTER TYPE public.chamber OWNER TO postgres;

--
-- Name: committee_member_title; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE committee_member_title AS ENUM (
    'chair_person',
    'vice_chair',
    'member'
);


ALTER TYPE public.committee_member_title OWNER TO postgres;

SET search_path = master, pg_catalog;

--
-- Name: log_sobi_updates(); Type: FUNCTION; Schema: master; Owner: postgres
--

CREATE FUNCTION log_sobi_updates() RETURNS trigger
    LANGUAGE plpgsql
    AS $$DECLARE
  primary_keys text[];         -- The primary key column names
  old_primary_key_val hstore;  -- Previous primary key/value pairs
  primary_key_val hstore;      -- Primary key/value pairs
  old_values hstore;           -- Old record key/value pairs
  new_values hstore;           -- New record key/value pairs
  data_diff hstore;            -- The data values that have been updated
  ignored_columns text[];      -- Column names to exclude from data_diff
  fragment_id text := NULL;    -- The fragment id that caused the insert/update

BEGIN
  primary_keys := TG_ARGV;
  ignored_columns := array_cat(ARRAY['modified_date_time', 'last_fragment_id'],
                               primary_keys);

  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    primary_key_val := slice(hstore(NEW.*), primary_keys);
    new_values := delete(hstore(NEW.*), ignored_columns);
    fragment_id := NEW.last_fragment_id;

    -- Handle case where the primary key is modified
    IF TG_OP = 'UPDATE' THEN
       old_primary_key_val := slice(hstore(OLD.*), primary_keys);
       IF primary_key_val != old_primary_key_val THEN
         INSERT INTO master.sobi_change_log (table_name, action, key, data, sobi_fragment_id)
         VALUES (TG_TABLE_NAME, 'MODIFIED_PKEY', old_primary_key_val, ''::hstore, fragment_id);
         TG_OP := 'INSERT';
       END IF;
    END IF;
  ELSE
    primary_key_val := slice(hstore(OLD.*), primary_keys);
  END IF;

  IF TG_OP IN ('UPDATE', 'DELETE') THEN
    old_values := delete(hstore(OLD.*), ignored_columns);
  END IF;

  IF TG_OP = 'INSERT' THEN
    data_diff := new_values;
  ELSIF TG_OP = 'UPDATE' THEN
    data_diff := new_values - old_values;
  ELSE
    data_diff := old_values;
  END IF;

  -- Add the sobi change record only for inserts, deletes, and updates where the
  -- non-ignored values were actually changed.
  IF TG_OP IN ('INSERT','DELETE') OR data_diff != ''::hstore THEN
    INSERT INTO master.sobi_change_log (table_name, action, key, data, sobi_fragment_id)
    VALUES (TG_TABLE_NAME, TG_OP, primary_key_val, data_diff, fragment_id);
  END IF;

  IF TG_OP IN ('INSERT', 'UPDATE') THEN
    RETURN NEW;
  ELSE
    RETURN OLD;
  END IF;

END;$$;


ALTER FUNCTION master.log_sobi_updates() OWNER TO postgres;

--
-- Name: FUNCTION log_sobi_updates(); Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON FUNCTION log_sobi_updates() IS 'Logs any additions, modifications, or deletions made to the data in this table.';


SET search_path = public, pg_catalog;

--
-- Name: date_to_search_str(date); Type: FUNCTION; Schema: public; Owner: postgres
--

CREATE FUNCTION date_to_search_str(d date) RETURNS text
    LANGUAGE sql
    AS $$SELECT to_char(d, 'YYYY MM DD FMMM MON MONTH FMDD');$$;


ALTER FUNCTION public.date_to_search_str(d date) OWNER TO postgres;

--
-- Name: FUNCTION date_to_search_str(d date); Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON FUNCTION date_to_search_str(d date) IS 'Converts a date to a string that provides adequate representations of the month and days for use in full text search queries. ';


SET search_path = master, pg_catalog;

--
-- Name: openleg_fts_config; Type: TEXT SEARCH CONFIGURATION; Schema: master; Owner: postgres
--

CREATE TEXT SEARCH CONFIGURATION openleg_fts_config (
    PARSER = pg_catalog."default" );

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR asciiword WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR word WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR numword WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR email WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR url WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR host WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR sfloat WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR version WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR hword_numpart WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR hword_part WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR hword_asciipart WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR numhword WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR asciihword WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR hword WITH english_stem;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR url_path WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR file WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR "float" WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR "int" WITH simple;

ALTER TEXT SEARCH CONFIGURATION openleg_fts_config
    ADD MAPPING FOR uint WITH simple;


ALTER TEXT SEARCH CONFIGURATION master.openleg_fts_config OWNER TO postgres;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: active_list_reference; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE active_list_reference (
    sequence_no smallint NOT NULL,
    calendar_no smallint,
    calendar_year smallint,
    id integer NOT NULL,
    calendar_date date,
    release_date_time timestamp without time zone,
    reference_date timestamp without time zone
);


ALTER TABLE master.active_list_reference OWNER TO postgres;

--
-- Name: TABLE active_list_reference; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE active_list_reference IS 'Table containing spotcheck report for active lists';


--
-- Name: active_list_reference_entry; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE active_list_reference_entry (
    active_list_reference_id smallint NOT NULL,
    bill_calendar_no smallint NOT NULL,
    bill_print_no text,
    bill_amend_version character(1),
    bill_session_year smallint,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.active_list_reference_entry OWNER TO postgres;

--
-- Name: TABLE active_list_reference_entry; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE active_list_reference_entry IS 'Bill contained in an active list';


--
-- Name: active_list_reference_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE active_list_reference_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.active_list_reference_id_seq OWNER TO postgres;

--
-- Name: active_list_reference_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE active_list_reference_id_seq OWNED BY active_list_reference.id;


--
-- Name: agenda; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda (
    agenda_no smallint NOT NULL,
    year smallint NOT NULL,
    published_date_time timestamp without time zone,
    modified_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.agenda OWNER TO postgres;

--
-- Name: TABLE agenda; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda IS 'Listing of all senate agendas';


--
-- Name: agenda_info_addendum; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_info_addendum (
    agenda_no smallint NOT NULL,
    year smallint NOT NULL,
    addendum_id text NOT NULL,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text,
    week_of date
);


ALTER TABLE master.agenda_info_addendum OWNER TO postgres;

--
-- Name: TABLE agenda_info_addendum; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_info_addendum IS 'Info addendum listings for agendas ';


--
-- Name: agenda_info_committee; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_info_committee (
    id integer NOT NULL,
    agenda_no smallint NOT NULL,
    year smallint NOT NULL,
    addendum_id text NOT NULL,
    committee_name public.citext NOT NULL,
    committee_chamber public.chamber NOT NULL,
    chair text,
    location text,
    meeting_date_time timestamp without time zone NOT NULL,
    notes text,
    last_fragment_id text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.agenda_info_committee OWNER TO postgres;

--
-- Name: TABLE agenda_info_committee; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_info_committee IS 'Committee info sent via the info addenda';


--
-- Name: agenda_info_committee_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE agenda_info_committee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_info_committee_id_seq OWNER TO postgres;

--
-- Name: agenda_info_committee_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE agenda_info_committee_id_seq OWNED BY agenda_info_committee.id;


--
-- Name: agenda_info_committee_item; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_info_committee_item (
    id integer NOT NULL,
    info_committee_id integer NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    message text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.agenda_info_committee_item OWNER TO postgres;

--
-- Name: TABLE agenda_info_committee_item; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_info_committee_item IS 'Bills on the agenda for a given committee info';


--
-- Name: agenda_info_committee_item_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE agenda_info_committee_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_info_committee_item_id_seq OWNER TO postgres;

--
-- Name: agenda_info_committee_item_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE agenda_info_committee_item_id_seq OWNED BY agenda_info_committee_item.id;


--
-- Name: agenda_vote_addendum; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_vote_addendum (
    agenda_no smallint NOT NULL,
    year smallint NOT NULL,
    addendum_id character varying NOT NULL,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.agenda_vote_addendum OWNER TO postgres;

--
-- Name: TABLE agenda_vote_addendum; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_vote_addendum IS 'Vote addendum listings for agendas';


--
-- Name: agenda_vote_commitee_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE agenda_vote_commitee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_commitee_id_seq OWNER TO postgres;

--
-- Name: agenda_vote_committee; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_vote_committee (
    id integer DEFAULT nextval('agenda_vote_commitee_id_seq'::regclass) NOT NULL,
    agenda_no smallint NOT NULL,
    year smallint NOT NULL,
    addendum_id text NOT NULL,
    committee_name public.citext NOT NULL,
    committee_chamber public.chamber NOT NULL,
    chair text,
    meeting_date_time timestamp without time zone,
    last_fragment_id text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.agenda_vote_committee OWNER TO postgres;

--
-- Name: TABLE agenda_vote_committee; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_vote_committee IS 'Committee info sent via the vote addenda';


--
-- Name: agenda_vote_committee_attend; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_vote_committee_attend (
    id integer NOT NULL,
    vote_committee_id integer,
    session_member_id integer NOT NULL,
    session_year smallint NOT NULL,
    lbdc_short_name text NOT NULL,
    rank smallint NOT NULL,
    party text,
    attend_status text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.agenda_vote_committee_attend OWNER TO postgres;

--
-- Name: TABLE agenda_vote_committee_attend; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_vote_committee_attend IS 'Attendance roll for committee vote meeting';


--
-- Name: agenda_vote_committee_attend_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE agenda_vote_committee_attend_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_attend_id_seq OWNER TO postgres;

--
-- Name: agenda_vote_committee_attend_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE agenda_vote_committee_attend_id_seq OWNED BY agenda_vote_committee_attend.id;


--
-- Name: agenda_vote_committee_vote; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE agenda_vote_committee_vote (
    id integer NOT NULL,
    vote_committee_id integer,
    vote_action text NOT NULL,
    vote_info_id integer,
    refer_committee_name public.citext,
    refer_committee_chamber public.chamber,
    with_amendment boolean DEFAULT false NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.agenda_vote_committee_vote OWNER TO postgres;

--
-- Name: TABLE agenda_vote_committee_vote; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE agenda_vote_committee_vote IS 'List of committee vote details';


--
-- Name: agenda_vote_committee_vote_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE agenda_vote_committee_vote_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_vote_id_seq OWNER TO postgres;

--
-- Name: agenda_vote_committee_vote_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE agenda_vote_committee_vote_id_seq OWNED BY agenda_vote_committee_vote.id;


--
-- Name: bill; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    title text,
    summary text,
    active_version character(1) NOT NULL,
    active_year integer,
    status text,
    status_date date,
    program_info text,
    program_info_num integer,
    sub_bill_print_no text,
    committee_name public.citext,
    bill_cal_no smallint,
    committee_chamber public.chamber,
    created_date_time timestamp without time zone DEFAULT now(),
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    last_fragment_id text
);


ALTER TABLE master.bill OWNER TO postgres;

--
-- Name: TABLE bill; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill IS 'General information about a bill';


--
-- Name: COLUMN bill.bill_print_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.bill_print_no IS 'The base print no (e.g S1234)';


--
-- Name: COLUMN bill.bill_session_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.bill_session_year IS 'The session year this bill was active in';


--
-- Name: COLUMN bill.title; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.title IS 'The title of the bill';


--
-- Name: COLUMN bill.summary; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.summary IS 'A summary of the bill';


--
-- Name: COLUMN bill.active_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.active_version IS 'The amendment version that is currently active';


--
-- Name: COLUMN bill.active_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.active_year IS 'The actual year the bill was introduced in';


--
-- Name: COLUMN bill.status; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.status IS 'The current status of the bill';


--
-- Name: COLUMN bill.status_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.status_date IS 'The date of the action that updated the status';


--
-- Name: COLUMN bill.program_info; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.program_info IS 'The program this bill was introduced for ';


--
-- Name: COLUMN bill.program_info_num; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.program_info_num IS 'An integer provided along with the program info';


--
-- Name: COLUMN bill.sub_bill_print_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.sub_bill_print_no IS 'Reference to the substituted bill''s print no';


--
-- Name: COLUMN bill.committee_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.committee_name IS 'The current committee (if applicable)';


--
-- Name: COLUMN bill.bill_cal_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.bill_cal_no IS 'The bill''s current floor calendar number (if applicable)';


--
-- Name: COLUMN bill.committee_chamber; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.committee_chamber IS 'Current committee''s chamber';


--
-- Name: COLUMN bill.created_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.created_date_time IS 'The date/time when this bill record was inserted';


--
-- Name: COLUMN bill.modified_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.modified_date_time IS 'Last date/time when this bill was modified';


--
-- Name: COLUMN bill.published_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.published_date_time IS 'Date/time when this bill became published';


--
-- Name: COLUMN bill.last_fragment_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill.last_fragment_id IS 'Reference to the last sobi fragment that caused an update';


--
-- Name: bill_amendment; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    sponsor_memo text,
    act_clause text,
    full_text text,
    stricken boolean DEFAULT false,
    uni_bill boolean DEFAULT false,
    law_code text,
    law_section text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment OWNER TO postgres;

--
-- Name: TABLE bill_amendment; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment IS 'Information specific to a bill amendment';


--
-- Name: COLUMN bill_amendment.law_code; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment.law_code IS 'Specifies the sections/chapters of laws that are affected';


--
-- Name: COLUMN bill_amendment.law_section; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment.law_section IS 'The primary section of law this bill affects';


--
-- Name: bill_amendment_action; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_action (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    effect_date date,
    text text,
    sequence_no smallint NOT NULL,
    chamber public.chamber NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_action OWNER TO postgres;

--
-- Name: TABLE bill_amendment_action; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_action IS 'Actions that have been taken on an amendment';


--
-- Name: bill_amendment_cosponsor; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_cosponsor (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    session_member_id integer NOT NULL,
    sequence_no smallint NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_cosponsor OWNER TO postgres;

--
-- Name: TABLE bill_amendment_cosponsor; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_cosponsor IS 'Listing of co-sponsors for an amendment';


--
-- Name: bill_amendment_multi_sponsor; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_multi_sponsor (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    session_member_id integer NOT NULL,
    sequence_no smallint NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_multi_sponsor OWNER TO postgres;

--
-- Name: TABLE bill_amendment_multi_sponsor; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_multi_sponsor IS 'Listing of multi-sponsors for a bill';


--
-- Name: bill_amendment_publish_status; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_publish_status (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    published boolean NOT NULL,
    effect_date_time timestamp without time zone DEFAULT now() NOT NULL,
    override boolean DEFAULT false NOT NULL,
    notes text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_publish_status OWNER TO postgres;

--
-- Name: TABLE bill_amendment_publish_status; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_publish_status IS 'Stores the latest published dates for bill amendments.';


--
-- Name: COLUMN bill_amendment_publish_status.bill_print_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.bill_print_no IS 'Bill print no';


--
-- Name: COLUMN bill_amendment_publish_status.bill_session_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.bill_session_year IS 'Bill session year';


--
-- Name: COLUMN bill_amendment_publish_status.bill_amend_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.bill_amend_version IS 'The version should not need a foreign key because publish statuses can be set prior to creating an amendment';


--
-- Name: COLUMN bill_amendment_publish_status.published; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.published IS 'True if the amendment is published';


--
-- Name: COLUMN bill_amendment_publish_status.effect_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.effect_date_time IS 'The date when the published status was changed';


--
-- Name: COLUMN bill_amendment_publish_status.override; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.override IS 'Set this to true when setting the published status manually';


--
-- Name: COLUMN bill_amendment_publish_status.notes; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.notes IS 'Notes describing the source of this published status';


--
-- Name: COLUMN bill_amendment_publish_status.created_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.created_date_time IS 'Date/time when this record was created';


--
-- Name: COLUMN bill_amendment_publish_status.last_fragment_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_publish_status.last_fragment_id IS 'The fragment that triggered this status';


--
-- Name: bill_amendment_same_as; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_same_as (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    same_as_bill_print_no text NOT NULL,
    same_as_session_year smallint NOT NULL,
    same_as_amend_version character(1) NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_same_as OWNER TO postgres;

--
-- Name: TABLE bill_amendment_same_as; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_same_as IS 'Same as bills for an amendment';


--
-- Name: bill_amendment_vote_info; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_vote_info (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    vote_date timestamp without time zone NOT NULL,
    sequence_no smallint,
    id integer NOT NULL,
    vote_type vote_type NOT NULL,
    committee_name text,
    committee_chamber public.chamber,
    published_date_time timestamp without time zone,
    modified_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_vote_info OWNER TO postgres;

--
-- Name: TABLE bill_amendment_vote_info; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_vote_info IS 'Contains basic info about votes that have been taken on a bill.';


--
-- Name: COLUMN bill_amendment_vote_info.bill_print_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_vote_info.bill_print_no IS 'The print no of the bill that was voted on';


--
-- Name: COLUMN bill_amendment_vote_info.bill_session_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_vote_info.bill_session_year IS 'The session year of the bill that was voted on';


--
-- Name: COLUMN bill_amendment_vote_info.bill_amend_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_vote_info.bill_amend_version IS 'The amendment version of the bill that was voted on';


--
-- Name: COLUMN bill_amendment_vote_info.committee_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_vote_info.committee_name IS 'If this is a committee vote, the name of the committee that voted';


--
-- Name: COLUMN bill_amendment_vote_info.committee_chamber; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_amendment_vote_info.committee_chamber IS 'If this is a committee vote, the chamber of the committee that voted';


--
-- Name: bill_amendment_vote_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE bill_amendment_vote_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.bill_amendment_vote_id_seq OWNER TO postgres;

--
-- Name: bill_amendment_vote_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE bill_amendment_vote_id_seq OWNED BY bill_amendment_vote_info.id;


--
-- Name: bill_amendment_vote_roll; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_amendment_vote_roll (
    vote_id integer NOT NULL,
    session_member_id integer NOT NULL,
    member_short_name text NOT NULL,
    session_year smallint NOT NULL,
    vote_code vote_code NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_amendment_vote_roll OWNER TO postgres;

--
-- Name: TABLE bill_amendment_vote_roll; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_amendment_vote_roll IS 'Contains a list of member votes';


--
-- Name: bill_approval; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_approval (
    approval_number integer NOT NULL,
    year integer NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    bill_amend_version character(1) NOT NULL,
    chapter integer,
    signer text,
    memo_text text NOT NULL,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_approval OWNER TO postgres;

--
-- Name: TABLE bill_approval; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_approval IS 'Approval Messages from the governor';


--
-- Name: bill_committee; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_committee (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    committee_name public.citext NOT NULL,
    committee_chamber public.chamber NOT NULL,
    action_date timestamp without time zone NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_committee OWNER TO postgres;

--
-- Name: TABLE bill_committee; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_committee IS 'Mapping of bills to committees';


--
-- Name: COLUMN bill_committee.action_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN bill_committee.action_date IS 'The date that the committee acted on the bill';


--
-- Name: bill_milestone; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_milestone (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    status public.citext NOT NULL,
    rank smallint NOT NULL,
    action_sequence_no smallint NOT NULL,
    date date NOT NULL,
    committee_name public.citext,
    committee_chamber public.chamber,
    cal_no smallint,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_milestone OWNER TO postgres;

--
-- Name: TABLE bill_milestone; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_milestone IS 'Listing of legislative milestones';


--
-- Name: bill_previous_version; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_previous_version (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    prev_bill_print_no text NOT NULL,
    prev_bill_session_year smallint NOT NULL,
    prev_amend_version text NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_previous_version OWNER TO postgres;

--
-- Name: TABLE bill_previous_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_previous_version IS 'Listing of this bill in previous session years';


--
-- Name: bill_sponsor; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_sponsor (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    session_member_id integer,
    budget_bill boolean DEFAULT false,
    rules_sponsor boolean DEFAULT false,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_sponsor OWNER TO postgres;

--
-- Name: TABLE bill_sponsor; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_sponsor IS 'Mapping of bill to sponsor';


--
-- Name: bill_sponsor_additional; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_sponsor_additional (
    bill_print_no text NOT NULL,
    bill_session_year smallint NOT NULL,
    session_member_id integer NOT NULL,
    sequence_no smallint,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.bill_sponsor_additional OWNER TO postgres;

--
-- Name: TABLE bill_sponsor_additional; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_sponsor_additional IS 'Contains additional sponsor mappings for special cases';


--
-- Name: bill_veto; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE bill_veto (
    veto_number integer NOT NULL,
    year integer NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    page integer,
    line_start integer,
    line_end integer,
    chapter integer,
    signer text,
    date date,
    memo_text text NOT NULL,
    type veto_type NOT NULL,
    modified_date_time timestamp without time zone NOT NULL,
    published_date_time timestamp without time zone NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.bill_veto OWNER TO postgres;

--
-- Name: TABLE bill_veto; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE bill_veto IS 'Veto Messages from the governor';


--
-- Name: bill_veto_year_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE bill_veto_year_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.bill_veto_year_seq OWNER TO postgres;

--
-- Name: calendar; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE calendar (
    calendar_no integer NOT NULL,
    calendar_year smallint NOT NULL,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.calendar OWNER TO postgres;

--
-- Name: TABLE calendar; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE calendar IS 'Calendar listings ';


--
-- Name: COLUMN calendar.calendar_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar.calendar_no IS 'Calendar number for a session day';


--
-- Name: COLUMN calendar.calendar_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar.calendar_year IS 'The year for this calendar';


--
-- Name: calendar_active_list; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE calendar_active_list (
    id integer NOT NULL,
    sequence_no smallint,
    calendar_no smallint,
    calendar_year smallint,
    calendar_date date,
    release_date_time timestamp without time zone,
    notes text,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.calendar_active_list OWNER TO postgres;

--
-- Name: TABLE calendar_active_list; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE calendar_active_list IS 'Listing of all calendar active lists';


--
-- Name: calendar_active_list_entry; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE calendar_active_list_entry (
    calendar_active_list_id smallint NOT NULL,
    bill_calendar_no smallint NOT NULL,
    bill_print_no text,
    bill_amend_version character(1),
    bill_session_year smallint,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.calendar_active_list_entry OWNER TO postgres;

--
-- Name: TABLE calendar_active_list_entry; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE calendar_active_list_entry IS 'Entries for each calendar active list';


--
-- Name: calendar_active_list_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE calendar_active_list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_active_list_id_seq OWNER TO postgres;

--
-- Name: calendar_active_list_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE calendar_active_list_id_seq OWNED BY calendar_active_list.id;


--
-- Name: calendar_supplemental; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE calendar_supplemental (
    id integer NOT NULL,
    calendar_no smallint NOT NULL,
    calendar_year smallint NOT NULL,
    sup_version text NOT NULL,
    calendar_date date,
    release_date_time timestamp without time zone,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.calendar_supplemental OWNER TO postgres;

--
-- Name: TABLE calendar_supplemental; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE calendar_supplemental IS 'Calendar entries are published through supplementals';


--
-- Name: calendar_supplemental_entry; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE calendar_supplemental_entry (
    id integer NOT NULL,
    calendar_sup_id integer,
    section_code smallint,
    bill_calendar_no smallint,
    bill_print_no text,
    bill_amend_version character(1),
    bill_session_year smallint,
    sub_bill_print_no text,
    sub_bill_amend_version character(1),
    sub_bill_session_year smallint,
    high boolean,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.calendar_supplemental_entry OWNER TO postgres;

--
-- Name: TABLE calendar_supplemental_entry; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE calendar_supplemental_entry IS 'These entries indentify bills that have been added to a calendar';


--
-- Name: COLUMN calendar_supplemental_entry.sub_bill_print_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar_supplemental_entry.sub_bill_print_no IS 'The substituted bill''s print no, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_entry.sub_bill_amend_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar_supplemental_entry.sub_bill_amend_version IS 'The substituted bill''s amendment version, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_entry.sub_bill_session_year; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar_supplemental_entry.sub_bill_session_year IS 'The substituted bill''s session year, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_entry.high; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN calendar_supplemental_entry.high IS 'True if bill has not yet properly aged';


--
-- Name: calendar_supplemental_entry_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE calendar_supplemental_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_supplemental_entry_id_seq OWNER TO postgres;

--
-- Name: calendar_supplemental_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE calendar_supplemental_entry_id_seq OWNED BY calendar_supplemental_entry.id;


--
-- Name: calendar_supplemental_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE calendar_supplemental_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_supplemental_id_seq OWNER TO postgres;

--
-- Name: calendar_supplemental_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE calendar_supplemental_id_seq OWNED BY calendar_supplemental.id;


--
-- Name: committee; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE committee (
    name public.citext NOT NULL,
    id integer NOT NULL,
    current_version timestamp without time zone DEFAULT '-infinity'::timestamp without time zone NOT NULL,
    chamber public.chamber NOT NULL,
    current_session integer DEFAULT 0 NOT NULL,
    full_name text
);


ALTER TABLE master.committee OWNER TO postgres;

--
-- Name: TABLE committee; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE committee IS 'Basic committee information for both senate and assembly';


--
-- Name: COLUMN committee.full_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN committee.full_name IS 'The full name of the committee, may be different from what LBDC uses.';


--
-- Name: committee_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE committee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.committee_id_seq OWNER TO postgres;

--
-- Name: committee_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE committee_id_seq OWNED BY committee.id;


--
-- Name: committee_member; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE committee_member (
    majority boolean NOT NULL,
    id integer NOT NULL,
    sequence_no integer NOT NULL,
    title public.committee_member_title DEFAULT 'member'::public.committee_member_title NOT NULL,
    committee_name public.citext NOT NULL,
    version_created timestamp without time zone NOT NULL,
    session_year integer NOT NULL,
    session_member_id integer NOT NULL,
    chamber public.chamber NOT NULL
);


ALTER TABLE master.committee_member OWNER TO postgres;

--
-- Name: TABLE committee_member; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE committee_member IS 'Membership details for committees';


--
-- Name: COLUMN committee_member.majority; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN committee_member.majority IS 'true = Majority, false = Minority';


--
-- Name: committee_member_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE committee_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.committee_member_id_seq OWNER TO postgres;

--
-- Name: committee_member_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE committee_member_id_seq OWNED BY committee_member.id;


--
-- Name: committee_version; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE committee_version (
    id integer NOT NULL,
    location text,
    meetday text,
    meetaltweek boolean,
    meetaltweektext text,
    meettime time without time zone,
    session_year integer NOT NULL,
    created timestamp without time zone NOT NULL,
    reformed timestamp without time zone DEFAULT 'infinity'::timestamp without time zone NOT NULL,
    committee_name public.citext NOT NULL,
    chamber public.chamber NOT NULL,
    last_fragment_id text
);


ALTER TABLE master.committee_version OWNER TO postgres;

--
-- Name: TABLE committee_version; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE committee_version IS 'Committee details';


--
-- Name: COLUMN committee_version.created; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN committee_version.created IS 'The date that this version of the committee was created';


--
-- Name: COLUMN committee_version.reformed; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN committee_version.reformed IS 'The date that this version of the committee was reformed';


--
-- Name: COLUMN committee_version.last_fragment_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN committee_version.last_fragment_id IS 'Reference to the sobi fragment that last updated this record';


--
-- Name: committee_version_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE committee_version_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.committee_version_id_seq OWNER TO postgres;

--
-- Name: committee_version_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE committee_version_id_seq OWNED BY committee_version.id;


--
-- Name: data_process_run_unit; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE data_process_run_unit (
    process_id integer NOT NULL,
    source_type text NOT NULL,
    source_id text NOT NULL,
    action text NOT NULL,
    id integer NOT NULL,
    start_date_time timestamp without time zone NOT NULL,
    end_date_time timestamp without time zone,
    errors text,
    messages text
);


ALTER TABLE master.data_process_run_unit OWNER TO postgres;

--
-- Name: TABLE data_process_run_unit; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE data_process_run_unit IS 'Detailed logs pertaining to each data process';


--
-- Name: data_process_log_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE data_process_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.data_process_log_id_seq OWNER TO postgres;

--
-- Name: data_process_log_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE data_process_log_id_seq OWNED BY data_process_run_unit.id;


--
-- Name: data_process_run; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE data_process_run (
    id integer NOT NULL,
    process_start_date_time timestamp without time zone NOT NULL,
    process_end_date_time timestamp without time zone,
    invoked_by text NOT NULL,
    exceptions text
);


ALTER TABLE master.data_process_run OWNER TO postgres;

--
-- Name: TABLE data_process_run; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE data_process_run IS 'Keeps track of data processing runs';


--
-- Name: daybreak_bill; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_bill (
    report_date date NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    active_version character(1) NOT NULL,
    title text,
    sponsor text,
    summary text,
    law_section text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.daybreak_bill OWNER TO postgres;

--
-- Name: TABLE daybreak_bill; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_bill IS 'General bill information sent via the daybreaks';


--
-- Name: daybreak_bill_action; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_bill_action (
    report_date date NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    action_date date NOT NULL,
    chamber public.chamber NOT NULL,
    text text NOT NULL,
    sequence_no integer NOT NULL
);


ALTER TABLE master.daybreak_bill_action OWNER TO postgres;

--
-- Name: TABLE daybreak_bill_action; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_bill_action IS 'Bill actions sent via the daybreaks';


--
-- Name: daybreak_bill_action_sequence_no_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE daybreak_bill_action_sequence_no_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.daybreak_bill_action_sequence_no_seq OWNER TO postgres;

--
-- Name: daybreak_bill_action_sequence_no_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE daybreak_bill_action_sequence_no_seq OWNED BY daybreak_bill_action.sequence_no;


--
-- Name: daybreak_bill_amendment; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_bill_amendment (
    report_date date NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    version character(1) NOT NULL,
    publish_date date,
    page_count integer,
    same_as text
);


ALTER TABLE master.daybreak_bill_amendment OWNER TO postgres;

--
-- Name: TABLE daybreak_bill_amendment; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_bill_amendment IS 'Bill amendment details such as page counts, sent via daybreaks';


--
-- Name: daybreak_bill_sponsor; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_bill_sponsor (
    report_date date NOT NULL,
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    type sponsor_type NOT NULL,
    member_short_name text NOT NULL
);


ALTER TABLE master.daybreak_bill_sponsor OWNER TO postgres;

--
-- Name: TABLE daybreak_bill_sponsor; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_bill_sponsor IS 'The sponsor for each daybreak bill';


--
-- Name: daybreak_file; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_file (
    report_date date NOT NULL,
    filename text NOT NULL,
    is_archived boolean DEFAULT false NOT NULL,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    type daybreak_file_type NOT NULL
);


ALTER TABLE master.daybreak_file OWNER TO postgres;

--
-- Name: TABLE daybreak_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_file IS 'Listing of all daybreak files';


--
-- Name: daybreak_fragment; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_fragment (
    bill_print_no text NOT NULL,
    bill_session_year integer NOT NULL,
    report_date date NOT NULL,
    filename text NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    processed_date_time timestamp without time zone,
    processed_count integer DEFAULT 0 NOT NULL,
    pending_processing boolean DEFAULT true NOT NULL,
    bill_active_version character(1),
    fragment_text text NOT NULL,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.daybreak_fragment OWNER TO postgres;

--
-- Name: TABLE daybreak_fragment; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_fragment IS 'A daybreak fragment is the content from a daybreak file for a single bill';


--
-- Name: daybreak_page_file_entry; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_page_file_entry (
    report_date date NOT NULL,
    bill_session_year integer NOT NULL,
    senate_bill_print_no text,
    senate_bill_version character(1),
    assembly_bill_print_no text,
    assembly_bill_version character(1),
    bill_publish_date date NOT NULL,
    page_count integer NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    filename text NOT NULL
);


ALTER TABLE master.daybreak_page_file_entry OWNER TO postgres;

--
-- Name: TABLE daybreak_page_file_entry; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_page_file_entry IS 'The daybreak page file';


--
-- Name: daybreak_report; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE daybreak_report (
    report_date date NOT NULL,
    processed boolean DEFAULT false NOT NULL,
    checked boolean DEFAULT false NOT NULL
);


ALTER TABLE master.daybreak_report OWNER TO postgres;

--
-- Name: TABLE daybreak_report; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE daybreak_report IS 'Indicates which set of daybreaks reports have been generated for';


--
-- Name: law_change_log; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE law_change_log (
    action text
);


ALTER TABLE master.law_change_log OWNER TO postgres;

--
-- Name: law_document; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE law_document (
    document_id text NOT NULL,
    published_date date NOT NULL,
    law_id text NOT NULL,
    location_id text NOT NULL,
    document_type text NOT NULL,
    document_type_id text NOT NULL,
    text text NOT NULL,
    title text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    law_file_name text
);


ALTER TABLE master.law_document OWNER TO postgres;

--
-- Name: TABLE law_document; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE law_document IS 'All law documents';


--
-- Name: COLUMN law_document.document_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.document_id IS 'Unique id assigned to each individual document';


--
-- Name: COLUMN law_document.published_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.published_date IS 'The published date of the law document';


--
-- Name: COLUMN law_document.law_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.law_id IS 'Three letter law id (e.g. ABC, EDN, etc)';


--
-- Name: COLUMN law_document.location_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.location_id IS 'The document id without the law id prefix';


--
-- Name: COLUMN law_document.document_type; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.document_type IS 'The type of document (Article, Section, etc)';


--
-- Name: COLUMN law_document.document_type_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.document_type_id IS 'Id specific to the document type,(e.g if location_id = ''A1'', this id will be ''1'') ';


--
-- Name: COLUMN law_document.text; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.text IS 'The text body of this law document';


--
-- Name: COLUMN law_document.title; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.title IS 'Extracted title associated with this document';


--
-- Name: COLUMN law_document.created_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.created_date_time IS 'Date/time this record was created';


--
-- Name: COLUMN law_document.law_file_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_document.law_file_name IS 'Reference to the originating law file';


--
-- Name: law_file; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE law_file (
    file_name text NOT NULL,
    published_date_time timestamp without time zone,
    processed_date_time timestamp without time zone,
    processed_count smallint DEFAULT 0 NOT NULL,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    pending_processing boolean DEFAULT true NOT NULL,
    archived boolean DEFAULT false NOT NULL
);


ALTER TABLE master.law_file OWNER TO postgres;

--
-- Name: TABLE law_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE law_file IS 'Listing of all source law files';


--
-- Name: COLUMN law_file.file_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.file_name IS 'The name of the law source file';


--
-- Name: COLUMN law_file.published_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.published_date_time IS 'The date/time this law file is effective on';


--
-- Name: COLUMN law_file.processed_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.processed_date_time IS 'The last date/time this law file was processed';


--
-- Name: COLUMN law_file.processed_count; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.processed_count IS 'The number of time this law file has been processed';


--
-- Name: COLUMN law_file.staged_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.staged_date_time IS 'The date/time this law file was recorded into the database';


--
-- Name: COLUMN law_file.pending_processing; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.pending_processing IS 'Indicates if this law file is waiting to be processed';


--
-- Name: COLUMN law_file.archived; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_file.archived IS 'Indicates if this law file has been moved to the archive directory';


--
-- Name: law_info; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE law_info (
    law_id text NOT NULL,
    chapter_id text NOT NULL,
    law_type public.citext NOT NULL,
    name text,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.law_info OWNER TO postgres;

--
-- Name: TABLE law_info; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE law_info IS 'Basic information about the law chapters';


--
-- Name: law_tree; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE law_tree (
    law_id text NOT NULL,
    published_date date NOT NULL,
    doc_id text NOT NULL,
    doc_published_date date NOT NULL,
    parent_doc_id text,
    parent_doc_published_date date,
    is_root boolean DEFAULT false NOT NULL,
    sequence_no smallint NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    law_file text
);


ALTER TABLE master.law_tree OWNER TO postgres;

--
-- Name: TABLE law_tree; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE law_tree IS 'Contains the structure of a law at a given published date';


--
-- Name: COLUMN law_tree.law_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.law_id IS 'Reference to the three letter law id';


--
-- Name: COLUMN law_tree.published_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.published_date IS 'Date this law tree is effective from';


--
-- Name: COLUMN law_tree.doc_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.doc_id IS 'Reference to the law document id';


--
-- Name: COLUMN law_tree.doc_published_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.doc_published_date IS 'Reference to the law document''s published date';


--
-- Name: COLUMN law_tree.parent_doc_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.parent_doc_id IS 'Reference to the parent law document id';


--
-- Name: COLUMN law_tree.parent_doc_published_date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.parent_doc_published_date IS 'Reference to the parent law document''s published date';


--
-- Name: COLUMN law_tree.is_root; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.is_root IS 'Indicates if this is the root node for the given law id / published date';


--
-- Name: COLUMN law_tree.sequence_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.sequence_no IS 'The sequence number is an easy way to keep track of the order of nodes';


--
-- Name: COLUMN law_tree.created_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.created_date_time IS 'Date/time this record was created';


--
-- Name: COLUMN law_tree.law_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN law_tree.law_file IS 'Reference to the source law file';


--
-- Name: sobi_fragment; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE sobi_fragment (
    sobi_file_name text NOT NULL,
    fragment_id text NOT NULL,
    published_date_time timestamp without time zone,
    fragment_type public.citext NOT NULL,
    text text,
    sequence_no smallint NOT NULL,
    processed_count smallint DEFAULT 0 NOT NULL,
    processed_date_time timestamp without time zone,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    pending_processing boolean NOT NULL,
    manual_fix boolean DEFAULT false NOT NULL,
    manual_fix_notes text
);


ALTER TABLE master.sobi_fragment OWNER TO postgres;

--
-- Name: TABLE sobi_fragment; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE sobi_fragment IS 'Listing of all Sobi fragments which are extracted from Sobi files.';


--
-- Name: COLUMN sobi_fragment.sobi_file_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.sobi_file_name IS 'The name of the originating Sobi file';


--
-- Name: COLUMN sobi_fragment.fragment_id; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.fragment_id IS 'A unique id for this fragment';


--
-- Name: COLUMN sobi_fragment.published_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.published_date_time IS 'The date this fragment was published';


--
-- Name: COLUMN sobi_fragment.fragment_type; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.fragment_type IS 'The type of data this fragment contains';


--
-- Name: COLUMN sobi_fragment.text; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.text IS 'The text body of the fragment';


--
-- Name: COLUMN sobi_fragment.sequence_no; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.sequence_no IS 'Preserves the order in which fragments are found in a Sobi file';


--
-- Name: COLUMN sobi_fragment.processed_count; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.processed_count IS 'The number of times this fragment has been processed';


--
-- Name: COLUMN sobi_fragment.processed_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.processed_date_time IS 'The last date/time this fragment was processed';


--
-- Name: COLUMN sobi_fragment.staged_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.staged_date_time IS 'The date/time when this fragment was recorded into the database';


--
-- Name: COLUMN sobi_fragment.pending_processing; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.pending_processing IS 'Indicates if the fragment is waiting to be processed';


--
-- Name: COLUMN sobi_fragment.manual_fix; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.manual_fix IS 'Indicates if the contents of the fragment were altered manually';


--
-- Name: COLUMN sobi_fragment.manual_fix_notes; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_fragment.manual_fix_notes IS 'Description of any manual changes made (if applicable)';


--
-- Name: psf; Type: VIEW; Schema: master; Owner: postgres
--

CREATE VIEW psf AS
 SELECT sobi_fragment.sobi_file_name,
    sobi_fragment.fragment_id,
    sobi_fragment.published_date_time,
    sobi_fragment.fragment_type,
    sobi_fragment.text,
    sobi_fragment.sequence_no,
    sobi_fragment.processed_count,
    sobi_fragment.processed_date_time,
    sobi_fragment.staged_date_time,
    sobi_fragment.pending_processing
   FROM sobi_fragment
  WHERE (sobi_fragment.pending_processing = true);


ALTER TABLE master.psf OWNER TO postgres;

--
-- Name: VIEW psf; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON VIEW psf IS 'Pending Sobi Fragments';


--
-- Name: public_hearing; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE public_hearing (
    filename text NOT NULL,
    title text,
    address text,
    text text NOT NULL,
    date date NOT NULL,
    start_time time without time zone,
    end_time time without time zone,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL,
    published_date_time timestamp without time zone DEFAULT now() NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.public_hearing OWNER TO postgres;

--
-- Name: TABLE public_hearing; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE public_hearing IS 'Listing of all processed public hearings';


--
-- Name: COLUMN public_hearing.filename; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.filename IS 'The name of the file containing this public hearing''s info.';


--
-- Name: COLUMN public_hearing.title; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.title IS 'The title of the public hearing.';


--
-- Name: COLUMN public_hearing.address; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.address IS 'The address of this public hearing.';


--
-- Name: COLUMN public_hearing.text; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.text IS 'The raw text of this public hearing.';


--
-- Name: COLUMN public_hearing.date; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.date IS 'The date of the public hearing';


--
-- Name: COLUMN public_hearing.start_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.start_time IS 'Time the public hearing started.';


--
-- Name: COLUMN public_hearing.end_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing.end_time IS 'Time the public hearing ended.';


--
-- Name: public_hearing_attendance; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE public_hearing_attendance (
    session_member_id integer NOT NULL,
    filename text NOT NULL
);


ALTER TABLE master.public_hearing_attendance OWNER TO postgres;

--
-- Name: COLUMN public_hearing_attendance.filename; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_attendance.filename IS 'Filename of the public hearing.';


--
-- Name: public_hearing_committee; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE public_hearing_committee (
    committee_name public.citext NOT NULL,
    committee_chamber public.chamber NOT NULL,
    filename text NOT NULL
);


ALTER TABLE master.public_hearing_committee OWNER TO postgres;

--
-- Name: COLUMN public_hearing_committee.committee_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_committee.committee_name IS 'The committee, Task Force, or other group holding a public hearing.';


--
-- Name: COLUMN public_hearing_committee.committee_chamber; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_committee.committee_chamber IS 'The chamber of the committee';


--
-- Name: COLUMN public_hearing_committee.filename; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_committee.filename IS 'The public hearing filename.';


--
-- Name: public_hearing_file; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE public_hearing_file (
    filename text NOT NULL,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    processed_date_time timestamp without time zone,
    processed_count smallint DEFAULT 0 NOT NULL,
    pending_processing boolean DEFAULT true NOT NULL,
    archived boolean DEFAULT false NOT NULL
);


ALTER TABLE master.public_hearing_file OWNER TO postgres;

--
-- Name: TABLE public_hearing_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE public_hearing_file IS 'Listing of all public hearing files';


--
-- Name: COLUMN public_hearing_file.filename; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.filename IS 'The name of the public hearing file.';


--
-- Name: COLUMN public_hearing_file.staged_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.staged_date_time IS 'The date time this public hearing was recorded into the database.';


--
-- Name: COLUMN public_hearing_file.processed_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.processed_date_time IS 'The date time this public hearing file was processed.';


--
-- Name: COLUMN public_hearing_file.processed_count; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.processed_count IS 'The number of times this public hearing file has been processed.';


--
-- Name: COLUMN public_hearing_file.pending_processing; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.pending_processing IS 'Indicates if this public hearing file is waiting to be processed';


--
-- Name: COLUMN public_hearing_file.archived; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN public_hearing_file.archived IS 'Indicates if this public hearing file has been moved to the archive directory.';


--
-- Name: sobi_change_log; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE sobi_change_log (
    id integer NOT NULL,
    table_name text NOT NULL,
    action text NOT NULL,
    key public.hstore NOT NULL,
    data public.hstore NOT NULL,
    action_date_time timestamp without time zone DEFAULT now() NOT NULL,
    sobi_fragment_id text
);


ALTER TABLE master.sobi_change_log OWNER TO postgres;

--
-- Name: TABLE sobi_change_log; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE sobi_change_log IS 'Change log for all entities that utilize Sobi files as the primary data source';


--
-- Name: sobi_change_log_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE sobi_change_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.sobi_change_log_id_seq OWNER TO postgres;

--
-- Name: sobi_change_log_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE sobi_change_log_id_seq OWNED BY sobi_change_log.id;


--
-- Name: sobi_file; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE sobi_file (
    file_name text NOT NULL,
    published_date_time timestamp without time zone NOT NULL,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    encoding text,
    archived boolean DEFAULT false NOT NULL
);


ALTER TABLE master.sobi_file OWNER TO postgres;

--
-- Name: TABLE sobi_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE sobi_file IS 'Listing of all Sobi files';


--
-- Name: COLUMN sobi_file.file_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_file.file_name IS 'The name of the sobi file';


--
-- Name: COLUMN sobi_file.published_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_file.published_date_time IS 'The published date which is typically derived from the file name';


--
-- Name: COLUMN sobi_file.staged_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_file.staged_date_time IS 'The date/time when this file was recorded into the database';


--
-- Name: COLUMN sobi_file.encoding; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_file.encoding IS 'The character encoding used in the file';


--
-- Name: COLUMN sobi_file.archived; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN sobi_file.archived IS 'Indicates if the file has been moved into the archive location';


--
-- Name: sobi_fragment_process_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE sobi_fragment_process_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.sobi_fragment_process_id_seq OWNER TO postgres;

--
-- Name: sobi_fragment_process_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE sobi_fragment_process_id_seq OWNED BY data_process_run.id;


--
-- Name: spotcheck_mismatch; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE spotcheck_mismatch (
    id integer NOT NULL,
    observation_id integer NOT NULL,
    type text NOT NULL,
    status text NOT NULL,
    reference_data text NOT NULL,
    observed_data text NOT NULL,
    notes text
);


ALTER TABLE master.spotcheck_mismatch OWNER TO postgres;

--
-- Name: TABLE spotcheck_mismatch; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE spotcheck_mismatch IS 'Listing of all spot check mismatches ';


--
-- Name: spotcheck_mismatch_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE spotcheck_mismatch_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.spotcheck_mismatch_id_seq OWNER TO postgres;

--
-- Name: spotcheck_mismatch_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE spotcheck_mismatch_id_seq OWNED BY spotcheck_mismatch.id;


--
-- Name: spotcheck_observation; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE spotcheck_observation (
    id integer NOT NULL,
    report_id integer NOT NULL,
    reference_type text NOT NULL,
    reference_active_date timestamp without time zone NOT NULL,
    key public.hstore NOT NULL,
    observed_date_time timestamp without time zone NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE master.spotcheck_observation OWNER TO postgres;

--
-- Name: TABLE spotcheck_observation; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE spotcheck_observation IS 'Spot check observations associate a report to a specific piece of content which may have mismatches';


--
-- Name: spotcheck_observation_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE spotcheck_observation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.spotcheck_observation_id_seq OWNER TO postgres;

--
-- Name: spotcheck_observation_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE spotcheck_observation_id_seq OWNED BY spotcheck_observation.id;


--
-- Name: spotcheck_report; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE spotcheck_report (
    id integer NOT NULL,
    report_date_time timestamp without time zone NOT NULL,
    reference_type text NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    reference_date_time timestamp without time zone
);


ALTER TABLE master.spotcheck_report OWNER TO postgres;

--
-- Name: TABLE spotcheck_report; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE spotcheck_report IS 'Listing of all spot check reports that have been run';


--
-- Name: spotcheck_report_id_seq; Type: SEQUENCE; Schema: master; Owner: postgres
--

CREATE SEQUENCE spotcheck_report_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.spotcheck_report_id_seq OWNER TO postgres;

--
-- Name: spotcheck_report_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: postgres
--

ALTER SEQUENCE spotcheck_report_id_seq OWNED BY spotcheck_report.id;


--
-- Name: transcript; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE transcript (
    session_type text NOT NULL,
    date_time timestamp without time zone NOT NULL,
    location text NOT NULL,
    text text NOT NULL,
    modified_date_time timestamp without time zone DEFAULT now() NOT NULL,
    published_date_time timestamp without time zone DEFAULT now() NOT NULL,
    created_date_time timestamp without time zone DEFAULT now() NOT NULL,
    transcript_filename text NOT NULL
);


ALTER TABLE master.transcript OWNER TO postgres;

--
-- Name: TABLE transcript; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE transcript IS 'Content and meta data of transcripts';


--
-- Name: COLUMN transcript.session_type; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.session_type IS 'The active session type when this transcript was recorded.';


--
-- Name: COLUMN transcript.date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.date_time IS 'The date and time the session represented by this transcript was held.';


--
-- Name: COLUMN transcript.location; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.location IS 'Location of the session represented by this transcript.';


--
-- Name: COLUMN transcript.text; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.text IS 'The text of the transcript.';


--
-- Name: COLUMN transcript.created_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.created_date_time IS 'The date time this transcript was inserted.';


--
-- Name: COLUMN transcript.transcript_filename; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript.transcript_filename IS 'This transcripts original file.';


--
-- Name: transcript_file; Type: TABLE; Schema: master; Owner: postgres; Tablespace: 
--

CREATE TABLE transcript_file (
    file_name text NOT NULL,
    processed_date_time timestamp without time zone,
    processed_count smallint DEFAULT 0 NOT NULL,
    staged_date_time timestamp without time zone DEFAULT now() NOT NULL,
    pending_processing boolean DEFAULT true NOT NULL,
    archived boolean DEFAULT false NOT NULL
);


ALTER TABLE master.transcript_file OWNER TO postgres;

--
-- Name: TABLE transcript_file; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON TABLE transcript_file IS 'Listing of all transcript files';


--
-- Name: COLUMN transcript_file.file_name; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.file_name IS 'The name of the transcript file.';


--
-- Name: COLUMN transcript_file.processed_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.processed_date_time IS 'The last date/time this transcript file was processed.';


--
-- Name: COLUMN transcript_file.processed_count; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.processed_count IS 'The number of time this transcript file has been processed.';


--
-- Name: COLUMN transcript_file.staged_date_time; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.staged_date_time IS 'The date/time this transcript file was recorded into the database';


--
-- Name: COLUMN transcript_file.pending_processing; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.pending_processing IS 'Indicates if this transcript file is waiting to be processed.';


--
-- Name: COLUMN transcript_file.archived; Type: COMMENT; Schema: master; Owner: postgres
--

COMMENT ON COLUMN transcript_file.archived IS 'Indicates if this transcript file has been moved to the archive directory.';


SET search_path = public, pg_catalog;

--
-- Name: adminuser; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE adminuser (
    username text NOT NULL,
    password text NOT NULL,
    permissions_level integer DEFAULT 0,
    active boolean DEFAULT false,
    created_date_time timestamp with time zone DEFAULT now(),
    modified_date_time timestamp with time zone DEFAULT now()
);


ALTER TABLE public.adminuser OWNER TO postgres;

--
-- Name: TABLE adminuser; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE adminuser IS 'Registered admin users';


--
-- Name: COLUMN adminuser.username; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.username IS 'Username';


--
-- Name: COLUMN adminuser.password; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.password IS 'Encrypted form of the admin''s password';


--
-- Name: COLUMN adminuser.permissions_level; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.permissions_level IS 'Permissions level';


--
-- Name: COLUMN adminuser.active; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.active IS 'Whether or not this admin has activated their account.';


--
-- Name: COLUMN adminuser.created_date_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.created_date_time IS 'The date that this admin account was created.';


--
-- Name: COLUMN adminuser.modified_date_time; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN adminuser.modified_date_time IS 'When this account was last modified.';


--
-- Name: apiuser; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE apiuser (
    apikey character varying(32) NOT NULL,
    authenticated boolean DEFAULT false,
    num_requests numeric,
    email_addr text NOT NULL
);


ALTER TABLE public.apiuser OWNER TO postgres;

--
-- Name: TABLE apiuser; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE apiuser IS 'Registered API users';


--
-- Name: COLUMN apiuser.apikey; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN apiuser.apikey IS 'The user''s API Key, 32 Characters in length';


--
-- Name: COLUMN apiuser.authenticated; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN apiuser.authenticated IS 'Whether or not the user has authenticated their email';


--
-- Name: COLUMN apiuser.num_requests; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN apiuser.num_requests IS 'The total number of requests made by this user';


--
-- Name: COLUMN apiuser.email_addr; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN apiuser.email_addr IS 'The email address that the user registered with';


--
-- Name: member; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE member (
    id integer NOT NULL,
    person_id integer NOT NULL,
    chamber chamber NOT NULL,
    incumbent boolean DEFAULT false,
    full_name character varying
);


ALTER TABLE public.member OWNER TO postgres;

--
-- Name: TABLE member; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE member IS 'Listing of all NYS senate/assembly members';


--
-- Name: COLUMN member.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN member.id IS 'Unique member id';


--
-- Name: COLUMN member.person_id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN member.person_id IS 'Reference to the person id';


--
-- Name: COLUMN member.chamber; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN member.chamber IS 'Indicates if member is in senate or assembly';


--
-- Name: COLUMN member.incumbent; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN member.incumbent IS 'If true, member is currently in office';


--
-- Name: COLUMN member.full_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN member.full_name IS 'Full name of member listed for convenience';


--
-- Name: member_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.member_id_seq OWNER TO postgres;

--
-- Name: member_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE member_id_seq OWNED BY member.id;


--
-- Name: member_person_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE member_person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.member_person_id_seq OWNER TO postgres;

--
-- Name: member_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE member_person_id_seq OWNED BY member.person_id;


--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE person (
    id integer NOT NULL,
    full_name character varying,
    first_name character varying,
    middle_name character varying,
    last_name character varying,
    email character varying,
    prefix character varying,
    suffix character varying,
    verified boolean DEFAULT true
);


ALTER TABLE public.person OWNER TO postgres;

--
-- Name: TABLE person; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE person IS 'Basic personal data for all NYS senate/assembly members';


--
-- Name: COLUMN person.id; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.id IS 'Unique person id';


--
-- Name: COLUMN person.full_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.full_name IS 'Full name of person';


--
-- Name: COLUMN person.first_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.first_name IS 'First name of person';


--
-- Name: COLUMN person.middle_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.middle_name IS 'Middle name (or initial) of person';


--
-- Name: COLUMN person.last_name; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.last_name IS 'Last name of person';


--
-- Name: COLUMN person.email; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.email IS 'The email of the person';


--
-- Name: COLUMN person.prefix; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.prefix IS 'Prefix (Mr, Mrs, Senator, etc)';


--
-- Name: COLUMN person.suffix; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON COLUMN person.suffix IS 'Suffix (Jr, Sr, etc)';


--
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.person_id_seq OWNER TO postgres;

--
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE person_id_seq OWNED BY person.id;


--
-- Name: session_member; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE session_member (
    id integer NOT NULL,
    member_id integer NOT NULL,
    lbdc_short_name character varying NOT NULL,
    session_year smallint NOT NULL,
    district_code smallint,
    alternate boolean DEFAULT false
);


ALTER TABLE public.session_member OWNER TO postgres;

--
-- Name: TABLE session_member; Type: COMMENT; Schema: public; Owner: postgres
--

COMMENT ON TABLE session_member IS 'Links LBDC short names to members for each session';


--
-- Name: session_member_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE session_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.session_member_id_seq OWNER TO postgres;

--
-- Name: session_member_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE session_member_id_seq OWNED BY session_member.id;


SET search_path = master, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY active_list_reference ALTER COLUMN id SET DEFAULT nextval('active_list_reference_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee ALTER COLUMN id SET DEFAULT nextval('agenda_info_committee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee_item ALTER COLUMN id SET DEFAULT nextval('agenda_info_committee_item_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_attend ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_attend_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_vote ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_vote_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_info ALTER COLUMN id SET DEFAULT nextval('bill_amendment_vote_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_active_list ALTER COLUMN id SET DEFAULT nextval('calendar_active_list_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental ALTER COLUMN id SET DEFAULT nextval('calendar_supplemental_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental_entry ALTER COLUMN id SET DEFAULT nextval('calendar_supplemental_entry_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee ALTER COLUMN id SET DEFAULT nextval('committee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_member ALTER COLUMN id SET DEFAULT nextval('committee_member_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_version ALTER COLUMN id SET DEFAULT nextval('committee_version_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY data_process_run ALTER COLUMN id SET DEFAULT nextval('sobi_fragment_process_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY data_process_run_unit ALTER COLUMN id SET DEFAULT nextval('data_process_log_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY sobi_change_log ALTER COLUMN id SET DEFAULT nextval('sobi_change_log_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY spotcheck_mismatch ALTER COLUMN id SET DEFAULT nextval('spotcheck_mismatch_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY spotcheck_observation ALTER COLUMN id SET DEFAULT nextval('spotcheck_observation_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY spotcheck_report ALTER COLUMN id SET DEFAULT nextval('spotcheck_report_id_seq'::regclass);


SET search_path = public, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY member ALTER COLUMN id SET DEFAULT nextval('member_id_seq'::regclass);


--
-- Name: person_id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY member ALTER COLUMN person_id SET DEFAULT nextval('member_person_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY person ALTER COLUMN id SET DEFAULT nextval('person_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session_member ALTER COLUMN id SET DEFAULT nextval('session_member_id_seq'::regclass);


SET search_path = master, pg_catalog;

--
-- Name: active_list_reference_entry_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY active_list_reference_entry
    ADD CONSTRAINT active_list_reference_entry_pkey PRIMARY KEY (active_list_reference_id, bill_calendar_no);


--
-- Name: active_list_reference_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY active_list_reference
    ADD CONSTRAINT active_list_reference_pkey PRIMARY KEY (id);


--
-- Name: active_list_reference_sequence_no_calendar_no_calendar_year_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY active_list_reference
    ADD CONSTRAINT active_list_reference_sequence_no_calendar_no_calendar_year_key UNIQUE (sequence_no, calendar_no, calendar_year, reference_date);


--
-- Name: agenda_info_addendum_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_info_addendum
    ADD CONSTRAINT agenda_info_addendum_pkey PRIMARY KEY (agenda_no, year, addendum_id);


--
-- Name: agenda_info_committee_agenda_no_year_addendum_id_committee__key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_info_committee
    ADD CONSTRAINT agenda_info_committee_agenda_no_year_addendum_id_committee__key UNIQUE (agenda_no, year, addendum_id, committee_name, committee_chamber);


--
-- Name: agenda_info_committee_item_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_info_committee_item
    ADD CONSTRAINT agenda_info_committee_item_pkey PRIMARY KEY (id);


--
-- Name: agenda_info_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_info_committee
    ADD CONSTRAINT agenda_info_committee_pkey PRIMARY KEY (id);


--
-- Name: agenda_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda
    ADD CONSTRAINT agenda_pkey PRIMARY KEY (agenda_no, year);


--
-- Name: agenda_vote_addendum_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_vote_addendum
    ADD CONSTRAINT agenda_vote_addendum_pkey PRIMARY KEY (agenda_no, year, addendum_id);


--
-- Name: agenda_vote_committee_agenda_no_year_addendum_id_committee__key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_agenda_no_year_addendum_id_committee__key UNIQUE (agenda_no, year, addendum_id, committee_name, committee_chamber);


--
-- Name: agenda_vote_committee_attend_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_vote_committee_attend
    ADD CONSTRAINT agenda_vote_committee_attend_pkey PRIMARY KEY (id);


--
-- Name: agenda_vote_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_pkey PRIMARY KEY (id);


--
-- Name: agenda_vote_committee_vote_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY agenda_vote_committee_vote
    ADD CONSTRAINT agenda_vote_committee_vote_pkey PRIMARY KEY (id);


--
-- Name: bill_amendment_action_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_action
    ADD CONSTRAINT bill_amendment_action_pkey PRIMARY KEY (bill_print_no, bill_session_year, sequence_no);


--
-- Name: bill_amendment_cosponsor_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_pkey PRIMARY KEY (bill_print_no, bill_session_year, bill_amend_version, session_member_id);


--
-- Name: bill_amendment_multi_sponsor_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_multi_sponsor
    ADD CONSTRAINT bill_amendment_multi_sponsor_pkey PRIMARY KEY (bill_print_no, bill_session_year, bill_amend_version, session_member_id);


--
-- Name: bill_amendment_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_pkey PRIMARY KEY (bill_print_no, bill_session_year, bill_amend_version);


--
-- Name: bill_amendment_publish_status_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_publish_status
    ADD CONSTRAINT bill_amendment_publish_status_pkey PRIMARY KEY (bill_print_no, bill_session_year, bill_amend_version);


--
-- Name: bill_amendment_same_as_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_same_as
    ADD CONSTRAINT bill_amendment_same_as_pkey PRIMARY KEY (bill_print_no, bill_session_year, bill_amend_version, same_as_bill_print_no, same_as_session_year, same_as_amend_version);


--
-- Name: bill_amendment_vote_info_bill_print_no_bill_session_year_bi_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_vote_info
    ADD CONSTRAINT bill_amendment_vote_info_bill_print_no_bill_session_year_bi_key UNIQUE (bill_print_no, bill_session_year, bill_amend_version, vote_date, vote_type, sequence_no);


--
-- Name: bill_amendment_vote_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_vote_info
    ADD CONSTRAINT bill_amendment_vote_pkey PRIMARY KEY (id);


--
-- Name: bill_amendment_vote_roll_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_amendment_vote_roll
    ADD CONSTRAINT bill_amendment_vote_roll_pkey PRIMARY KEY (vote_id, session_member_id, session_year, vote_code);


--
-- Name: bill_approval_approval_number_year_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_approval_number_year_key UNIQUE (approval_number, year);


--
-- Name: bill_approval_bill_print_no_session_year_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_bill_print_no_session_year_key UNIQUE (bill_print_no, bill_session_year);


--
-- Name: bill_approval_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_pkey PRIMARY KEY (year, approval_number);


--
-- Name: bill_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_committee
    ADD CONSTRAINT bill_committee_pkey PRIMARY KEY (bill_print_no, bill_session_year, committee_name, committee_chamber, action_date);


--
-- Name: bill_milestone_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_milestone
    ADD CONSTRAINT bill_milestone_pkey PRIMARY KEY (bill_print_no, bill_session_year, status);


--
-- Name: bill_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill
    ADD CONSTRAINT bill_pkey PRIMARY KEY (bill_print_no, bill_session_year);


--
-- Name: bill_previous_version_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_previous_version
    ADD CONSTRAINT bill_previous_version_pkey PRIMARY KEY (bill_print_no, bill_session_year, prev_bill_print_no, prev_bill_session_year, prev_amend_version);


--
-- Name: bill_sponsor_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_sponsor
    ADD CONSTRAINT bill_sponsor_pkey PRIMARY KEY (bill_print_no, bill_session_year);


--
-- Name: bill_sponsor_special_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_sponsor_additional
    ADD CONSTRAINT bill_sponsor_special_pkey PRIMARY KEY (bill_print_no, bill_session_year, session_member_id);


--
-- Name: bill_veto_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY bill_veto
    ADD CONSTRAINT bill_veto_pkey PRIMARY KEY (veto_number, year);


--
-- Name: calendar_active_list_calendar_no_calendar_year_sequence_no_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_calendar_no_calendar_year_sequence_no_key UNIQUE (calendar_no, calendar_year, sequence_no);


--
-- Name: calendar_active_list_entry_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_active_list_entry
    ADD CONSTRAINT calendar_active_list_entry_pkey PRIMARY KEY (calendar_active_list_id, bill_calendar_no);


--
-- Name: calendar_active_list_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_pkey PRIMARY KEY (id);


--
-- Name: calendar_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (calendar_no, calendar_year);


--
-- Name: calendar_supplemental_calendar_no_calendar_year_sup_version_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_supplemental
    ADD CONSTRAINT calendar_supplemental_calendar_no_calendar_year_sup_version_key UNIQUE (calendar_no, calendar_year, sup_version);


--
-- Name: calendar_supplemental_entry_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_supplemental_entry
    ADD CONSTRAINT calendar_supplemental_entry_pkey PRIMARY KEY (id);


--
-- Name: calendar_supplemental_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY calendar_supplemental
    ADD CONSTRAINT calendar_supplemental_pkey PRIMARY KEY (id);


--
-- Name: committee_member_chamber_committee_name_version_created_seq_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_chamber_committee_name_version_created_seq_key UNIQUE (chamber, committee_name, version_created, sequence_no);


--
-- Name: committee_member_chamber_committee_name_version_created_ses_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_chamber_committee_name_version_created_ses_key UNIQUE (chamber, committee_name, version_created, session_member_id);


--
-- Name: committee_member_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_pkey PRIMARY KEY (id);


--
-- Name: committee_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee
    ADD CONSTRAINT committee_pkey PRIMARY KEY (name, chamber);


--
-- Name: committee_version_committee_name_chamber_session_year_creat_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee_version
    ADD CONSTRAINT committee_version_committee_name_chamber_session_year_creat_key UNIQUE (committee_name, chamber, session_year, created);


--
-- Name: committee_version_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY committee_version
    ADD CONSTRAINT committee_version_pkey PRIMARY KEY (id);


--
-- Name: data_process_log_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_process_run_unit
    ADD CONSTRAINT data_process_log_pkey PRIMARY KEY (id);


--
-- Name: daybreak_bill_action_report_date_bill_print_no_bill_session_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_bill_action
    ADD CONSTRAINT daybreak_bill_action_report_date_bill_print_no_bill_session_key UNIQUE (report_date, bill_print_no, bill_session_year, sequence_no);


--
-- Name: daybreak_bill_amendment_report_date_bill_print_no_bill_sess_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_bill_amendment
    ADD CONSTRAINT daybreak_bill_amendment_report_date_bill_print_no_bill_sess_key UNIQUE (report_date, bill_print_no, bill_session_year, version);


--
-- Name: daybreak_bill_report_date_bill_print_no_bill_session_year_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_bill
    ADD CONSTRAINT daybreak_bill_report_date_bill_print_no_bill_session_year_key UNIQUE (report_date, bill_print_no, bill_session_year);


--
-- Name: daybreak_bill_sponsor_report_date_bill_print_no_bill_sessio_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_bill_sponsor
    ADD CONSTRAINT daybreak_bill_sponsor_report_date_bill_print_no_bill_sessio_key UNIQUE (report_date, bill_print_no, bill_session_year, member_short_name, type);


--
-- Name: daybreak_file_report_date_filename_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_file
    ADD CONSTRAINT daybreak_file_report_date_filename_key UNIQUE (report_date, filename);


--
-- Name: daybreak_fragment_bill_print_no_bill_session_year_report_da_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_fragment
    ADD CONSTRAINT daybreak_fragment_bill_print_no_bill_session_year_report_da_key UNIQUE (bill_print_no, bill_session_year, report_date);


--
-- Name: daybreak_page_file_entry_report_date_bill_session_year_asse_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_page_file_entry
    ADD CONSTRAINT daybreak_page_file_entry_report_date_bill_session_year_asse_key UNIQUE (report_date, bill_session_year, assembly_bill_print_no, assembly_bill_version);


--
-- Name: daybreak_page_file_entry_report_date_bill_session_year_sena_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_page_file_entry
    ADD CONSTRAINT daybreak_page_file_entry_report_date_bill_session_year_sena_key UNIQUE (report_date, bill_session_year, senate_bill_print_no, senate_bill_version);


--
-- Name: daybreak_report_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY daybreak_report
    ADD CONSTRAINT daybreak_report_pkey PRIMARY KEY (report_date);


--
-- Name: law_chapter_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY law_info
    ADD CONSTRAINT law_chapter_pkey PRIMARY KEY (law_id);


--
-- Name: law_document_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY law_document
    ADD CONSTRAINT law_document_pkey PRIMARY KEY (document_id, published_date);


--
-- Name: law_file_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY law_file
    ADD CONSTRAINT law_file_pkey PRIMARY KEY (file_name);


--
-- Name: law_tree_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY law_tree
    ADD CONSTRAINT law_tree_pkey PRIMARY KEY (law_id, published_date, doc_id, doc_published_date);


--
-- Name: public_hearing_attendance_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY public_hearing_attendance
    ADD CONSTRAINT public_hearing_attendance_pkey PRIMARY KEY (session_member_id, filename);


--
-- Name: public_hearing_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY public_hearing_committee
    ADD CONSTRAINT public_hearing_committee_pkey PRIMARY KEY (committee_name, filename, committee_chamber);


--
-- Name: public_hearing_file_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY public_hearing_file
    ADD CONSTRAINT public_hearing_file_pkey PRIMARY KEY (filename);


--
-- Name: public_hearing_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY public_hearing
    ADD CONSTRAINT public_hearing_pkey PRIMARY KEY (filename);


--
-- Name: sobi_change_log_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sobi_change_log
    ADD CONSTRAINT sobi_change_log_pkey PRIMARY KEY (id);


--
-- Name: sobi_fragment_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sobi_fragment
    ADD CONSTRAINT sobi_fragment_pkey PRIMARY KEY (fragment_id);


--
-- Name: sobi_fragment_process_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY data_process_run
    ADD CONSTRAINT sobi_fragment_process_pkey PRIMARY KEY (id);


--
-- Name: sobi_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY sobi_file
    ADD CONSTRAINT sobi_pkey PRIMARY KEY (file_name);


--
-- Name: spotcheck_mismatch_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spotcheck_mismatch
    ADD CONSTRAINT spotcheck_mismatch_pkey PRIMARY KEY (id);


--
-- Name: spotcheck_observation_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spotcheck_observation
    ADD CONSTRAINT spotcheck_observation_pkey PRIMARY KEY (id);


--
-- Name: spotcheck_observation_report_id_reference_type_key_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spotcheck_observation
    ADD CONSTRAINT spotcheck_observation_report_id_reference_type_key_key UNIQUE (report_id, reference_type, key);


--
-- Name: spotcheck_report_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spotcheck_report
    ADD CONSTRAINT spotcheck_report_pkey PRIMARY KEY (id);


--
-- Name: spotcheck_report_report_date_time_reference_type_key; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY spotcheck_report
    ADD CONSTRAINT spotcheck_report_report_date_time_reference_type_key UNIQUE (report_date_time, reference_type);


--
-- Name: transcript_file_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY transcript_file
    ADD CONSTRAINT transcript_file_pkey PRIMARY KEY (file_name);


--
-- Name: transcript_pkey; Type: CONSTRAINT; Schema: master; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY transcript
    ADD CONSTRAINT transcript_pkey PRIMARY KEY (transcript_filename);


SET search_path = public, pg_catalog;

--
-- Name: adminuser_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY adminuser
    ADD CONSTRAINT adminuser_pkey PRIMARY KEY (username);


--
-- Name: apiuser_email_addr_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY apiuser
    ADD CONSTRAINT apiuser_email_addr_key UNIQUE (email_addr);


--
-- Name: apiuser_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY apiuser
    ADD CONSTRAINT apiuser_pkey PRIMARY KEY (apikey);


--
-- Name: member_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_pkey PRIMARY KEY (id);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: session_member_member_id_lbdc_short_name_session_year_key; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_member_id_lbdc_short_name_session_year_key UNIQUE (member_id, lbdc_short_name, session_year);


--
-- Name: session_member_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_pkey PRIMARY KEY (id);


SET search_path = master, pg_catalog;

--
-- Name: agenda_info_committee_item_bill_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX agenda_info_committee_item_bill_idx ON agenda_info_committee_item USING btree (bill_print_no, bill_session_year, bill_amend_version);


--
-- Name: bill_session_year_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX bill_session_year_idx ON bill USING btree (bill_session_year);


--
-- Name: calendar_supplemental_entry_bill_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX calendar_supplemental_entry_bill_idx ON calendar_supplemental_entry USING btree (bill_print_no, bill_amend_version, bill_session_year);


--
-- Name: data_process_run_start_date_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX data_process_run_start_date_idx ON data_process_run USING btree (process_start_date_time);


--
-- Name: data_process_run_unit_start_date_time_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX data_process_run_unit_start_date_time_idx ON data_process_run_unit USING btree (start_date_time);


--
-- Name: daybreak_sponsor_pkey; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX daybreak_sponsor_pkey ON daybreak_bill_sponsor USING btree (report_date, bill_print_no, bill_session_year, type, member_short_name);


--
-- Name: sobi_change_log_action_date_time_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX sobi_change_log_action_date_time_idx ON sobi_change_log USING btree (action_date_time);


--
-- Name: sobi_change_log_fragment_id_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX sobi_change_log_fragment_id_idx ON sobi_change_log USING btree (sobi_fragment_id);


--
-- Name: sobi_change_log_keygin; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX sobi_change_log_keygin ON sobi_change_log USING gin (key);


--
-- Name: sobi_change_log_table_name_idx; Type: INDEX; Schema: master; Owner: postgres; Tablespace: 
--

CREATE INDEX sobi_change_log_table_name_idx ON sobi_change_log USING btree (table_name);


--
-- Name: log_agenda_info_addendum_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_info_addendum_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_info_addendum FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('agenda_no', 'year', 'addendum_id');


--
-- Name: log_agenda_info_committee_item_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_info_committee_item_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_info_committee_item FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id');


--
-- Name: log_agenda_info_committee_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_info_committee_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_info_committee FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('agenda_no', 'year', 'addendum_id', 'committee_name', 'committee_chamber');


--
-- Name: log_agenda_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('agenda_no', 'year');


--
-- Name: log_agenda_vote_addendum_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_vote_addendum_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_vote_addendum FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('agenda_no', 'year', 'addendum_id');


--
-- Name: log_agenda_vote_committee_attend_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_vote_committee_attend_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_vote_committee_attend FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id');


--
-- Name: log_agenda_vote_committee_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_vote_committee_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_vote_committee FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('agenda_no', 'year', 'addendum_id', 'committee_name', 'committee_chamber');


--
-- Name: log_agenda_vote_committee_vote_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_agenda_vote_committee_vote_updates BEFORE INSERT OR DELETE OR UPDATE ON agenda_vote_committee_vote FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id');


--
-- Name: log_bill_amendment_action_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_action_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_action FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'sequence_no');


--
-- Name: log_bill_amendment_cosponsor_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_cosponsor_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_cosponsor FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'bill_amend_version', 'member_id');


--
-- Name: log_bill_amendment_multisponsor_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_multisponsor_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_multi_sponsor FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'bill_amend_version', 'member_id');


--
-- Name: log_bill_amendment_publish_status_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_publish_status_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_publish_status FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'bill_amend_version');


--
-- Name: log_bill_amendment_same_as_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_same_as_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_same_as FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'bill_amend_version');


--
-- Name: log_bill_amendment_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'version');


--
-- Name: log_bill_amendment_vote_info_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_vote_info_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_vote_info FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'bill_amend_version');


--
-- Name: log_bill_amendment_vote_roll_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_amendment_vote_roll_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_amendment_vote_roll FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('vote_id', 'member_id', 'session_year', 'vote_code');


--
-- Name: log_bill_approval_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_approval_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_approval FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'approval_number');


--
-- Name: log_bill_committee_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_committee_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_committee FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'committee_name', 'committee_chamber', 'action_date');


--
-- Name: log_bill_previous_version_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_previous_version_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_previous_version FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year');


--
-- Name: log_bill_sponsor_additional_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_sponsor_additional_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_sponsor_additional FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year');


--
-- Name: log_bill_sponsor_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_sponsor_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_sponsor FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year');


--
-- Name: log_bill_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_updates BEFORE INSERT OR DELETE OR UPDATE ON bill FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year');


--
-- Name: log_bill_veto_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_bill_veto_updates BEFORE INSERT OR DELETE OR UPDATE ON bill_veto FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('bill_print_no', 'bill_session_year', 'veto_number');


--
-- Name: log_calendar_active_list_entry_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_calendar_active_list_entry_updates BEFORE INSERT OR DELETE OR UPDATE ON calendar_active_list_entry FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('calendar_active_list_id', 'bill_calendar_no');


--
-- Name: log_calendar_active_list_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_calendar_active_list_updates BEFORE INSERT OR DELETE OR UPDATE ON calendar_active_list FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id', 'calendar_no', 'calendar_year', 'sequence_no');


--
-- Name: log_calendar_supplemental_entry_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_calendar_supplemental_entry_updates BEFORE INSERT OR DELETE OR UPDATE ON calendar_supplemental_entry FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id', 'calendar_sup_id');


--
-- Name: log_calendar_supplemental_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_calendar_supplemental_updates BEFORE INSERT OR DELETE OR UPDATE ON calendar_supplemental FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('id', 'calendar_no', 'calendar_year', 'sup_version');


--
-- Name: log_calendar_updates; Type: TRIGGER; Schema: master; Owner: postgres
--

CREATE TRIGGER log_calendar_updates BEFORE INSERT OR DELETE OR UPDATE ON calendar FOR EACH ROW EXECUTE PROCEDURE log_sobi_updates('calendar_no', 'calendar_year');


--
-- Name: active_list_reference_entry_calendar_active_list_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY active_list_reference_entry
    ADD CONSTRAINT active_list_reference_entry_calendar_active_list_id_fkey FOREIGN KEY (active_list_reference_id) REFERENCES active_list_reference(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_addendum_agenda_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_addendum
    ADD CONSTRAINT agenda_info_addendum_agenda_no_fkey FOREIGN KEY (agenda_no, year) REFERENCES agenda(agenda_no, year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_addendum_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_addendum
    ADD CONSTRAINT agenda_info_addendum_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_committee_agenda_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee
    ADD CONSTRAINT agenda_info_committee_agenda_no_fkey FOREIGN KEY (agenda_no, year, addendum_id) REFERENCES agenda_info_addendum(agenda_no, year, addendum_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_committee_item_info_committee_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee_item
    ADD CONSTRAINT agenda_info_committee_item_info_committee_id_fkey FOREIGN KEY (info_committee_id) REFERENCES agenda_info_committee(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_committee_item_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee_item
    ADD CONSTRAINT agenda_info_committee_item_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_info_committee_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_info_committee
    ADD CONSTRAINT agenda_info_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda
    ADD CONSTRAINT agenda_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_addendum_agenda_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_addendum
    ADD CONSTRAINT agenda_vote_addendum_agenda_no_fkey FOREIGN KEY (agenda_no, year) REFERENCES agenda(agenda_no, year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_addendum_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_addendum
    ADD CONSTRAINT agenda_vote_addendum_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_agenda_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_agenda_no_fkey FOREIGN KEY (agenda_no, year, addendum_id) REFERENCES agenda_vote_addendum(agenda_no, year, addendum_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_attend_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_attend
    ADD CONSTRAINT agenda_vote_committee_attend_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_attend_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_attend
    ADD CONSTRAINT agenda_vote_committee_attend_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_attend_vote_committee_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_attend
    ADD CONSTRAINT agenda_vote_committee_attend_vote_committee_id_fkey FOREIGN KEY (vote_committee_id) REFERENCES agenda_vote_committee(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_vote_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_vote
    ADD CONSTRAINT agenda_vote_committee_vote_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_vote_refer_committee_name_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_vote
    ADD CONSTRAINT agenda_vote_committee_vote_refer_committee_name_fkey FOREIGN KEY (refer_committee_name, refer_committee_chamber) REFERENCES committee(name, chamber) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_vote_vote_committee_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_vote
    ADD CONSTRAINT agenda_vote_committee_vote_vote_committee_id_fkey FOREIGN KEY (vote_committee_id) REFERENCES agenda_vote_committee(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: agenda_vote_committee_vote_vote_info_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY agenda_vote_committee_vote
    ADD CONSTRAINT agenda_vote_committee_vote_vote_info_id_fkey FOREIGN KEY (vote_info_id) REFERENCES bill_amendment_vote_info(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_action_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_action
    ADD CONSTRAINT bill_amendment_action_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_action_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_action
    ADD CONSTRAINT bill_amendment_action_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_cosponsor_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_cosponsor_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_cosponsor_session_member_id_fkey1; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_session_member_id_fkey1 FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_multi_sponsor_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_multi_sponsor
    ADD CONSTRAINT bill_amendment_multi_sponsor_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_multi_sponsor_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_multi_sponsor
    ADD CONSTRAINT bill_amendment_multi_sponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_multi_sponsor_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_multi_sponsor
    ADD CONSTRAINT bill_amendment_multi_sponsor_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_publish_status_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_publish_status
    ADD CONSTRAINT bill_amendment_publish_status_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_publish_status_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_publish_status
    ADD CONSTRAINT bill_amendment_publish_status_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_same_as_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_same_as
    ADD CONSTRAINT bill_amendment_same_as_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_same_as_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_same_as
    ADD CONSTRAINT bill_amendment_same_as_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_vote_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_info
    ADD CONSTRAINT bill_amendment_vote_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_vote_info_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_info
    ADD CONSTRAINT bill_amendment_vote_info_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_vote_roll_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_roll
    ADD CONSTRAINT bill_amendment_vote_roll_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_vote_roll_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_roll
    ADD CONSTRAINT bill_amendment_vote_roll_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_vote_roll_vote_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_amendment_vote_roll
    ADD CONSTRAINT bill_amendment_vote_roll_vote_id_fkey FOREIGN KEY (vote_id) REFERENCES bill_amendment_vote_info(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_approval_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year);


--
-- Name: bill_approval_bill_print_no_fkey1; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_bill_print_no_fkey1 FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, bill_amend_version);


--
-- Name: bill_approval_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_approval
    ADD CONSTRAINT bill_approval_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);


--
-- Name: bill_committee_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_committee
    ADD CONSTRAINT bill_committee_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_committee_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_committee
    ADD CONSTRAINT bill_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill
    ADD CONSTRAINT bill_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_milestone_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_milestone
    ADD CONSTRAINT bill_milestone_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_milestone_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_milestone
    ADD CONSTRAINT bill_milestone_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_previous_version_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_previous_version
    ADD CONSTRAINT bill_previous_version_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_previous_version_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_previous_version
    ADD CONSTRAINT bill_previous_version_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_sponsor_additional_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_sponsor_additional
    ADD CONSTRAINT bill_sponsor_additional_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_sponsor_additional_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_sponsor_additional
    ADD CONSTRAINT bill_sponsor_additional_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_sponsor_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_sponsor
    ADD CONSTRAINT bill_sponsor_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_sponsor_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_sponsor
    ADD CONSTRAINT bill_sponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_sponsor_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_sponsor
    ADD CONSTRAINT bill_sponsor_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_veto_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_veto
    ADD CONSTRAINT bill_veto_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(bill_print_no, bill_session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_veto_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY bill_veto
    ADD CONSTRAINT bill_veto_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_active_list_calendar_number_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_calendar_number_fkey FOREIGN KEY (calendar_no, calendar_year) REFERENCES calendar(calendar_no, calendar_year);


--
-- Name: calendar_active_list_entry_calendar_active_list_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_active_list_entry
    ADD CONSTRAINT calendar_active_list_entry_calendar_active_list_id_fkey FOREIGN KEY (calendar_active_list_id) REFERENCES calendar_active_list(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_active_list_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar
    ADD CONSTRAINT calendar_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_supplemental_calendar_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental
    ADD CONSTRAINT calendar_supplemental_calendar_no_fkey FOREIGN KEY (calendar_no, calendar_year) REFERENCES calendar(calendar_no, calendar_year);


--
-- Name: calendar_supplemental_entry_calendar_sup_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental_entry
    ADD CONSTRAINT calendar_supplemental_entry_calendar_sup_id_fkey FOREIGN KEY (calendar_sup_id) REFERENCES calendar_supplemental(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_supplemental_entry_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental_entry
    ADD CONSTRAINT calendar_supplemental_entry_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: calendar_supplemental_last_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY calendar_supplemental
    ADD CONSTRAINT calendar_supplemental_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: committee_member_chamber_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_chamber_fkey FOREIGN KEY (chamber, committee_name, session_year, version_created) REFERENCES committee_version(chamber, committee_name, session_year, created) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: committee_member_chamber_fkey1; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_chamber_fkey1 FOREIGN KEY (chamber, committee_name) REFERENCES committee(chamber, name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: committee_member_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_member
    ADD CONSTRAINT committee_member_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: committee_version_chamber_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY committee_version
    ADD CONSTRAINT committee_version_chamber_fkey FOREIGN KEY (chamber, committee_name) REFERENCES committee(chamber, name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: data_process_log_process_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY data_process_run_unit
    ADD CONSTRAINT data_process_log_process_id_fkey FOREIGN KEY (process_id) REFERENCES data_process_run(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: daybreak_bill_action_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_bill_action
    ADD CONSTRAINT daybreak_bill_action_report_date_fkey FOREIGN KEY (report_date, bill_print_no, bill_session_year) REFERENCES daybreak_bill(report_date, bill_print_no, bill_session_year);


--
-- Name: daybreak_bill_amendment_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_bill_amendment
    ADD CONSTRAINT daybreak_bill_amendment_report_date_fkey FOREIGN KEY (report_date, bill_print_no, bill_session_year) REFERENCES daybreak_bill(report_date, bill_print_no, bill_session_year);


--
-- Name: daybreak_bill_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_bill
    ADD CONSTRAINT daybreak_bill_report_date_fkey FOREIGN KEY (report_date, bill_print_no, bill_session_year) REFERENCES daybreak_fragment(report_date, bill_print_no, bill_session_year);


--
-- Name: daybreak_bill_sponsors_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_bill_sponsor
    ADD CONSTRAINT daybreak_bill_sponsors_report_date_fkey FOREIGN KEY (report_date, bill_print_no, bill_session_year) REFERENCES daybreak_bill(report_date, bill_print_no, bill_session_year);


--
-- Name: daybreak_fragment_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_fragment
    ADD CONSTRAINT daybreak_fragment_report_date_fkey FOREIGN KEY (report_date, filename) REFERENCES daybreak_file(report_date, filename);


--
-- Name: daybreak_page_file_entry_report_date_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY daybreak_page_file_entry
    ADD CONSTRAINT daybreak_page_file_entry_report_date_fkey FOREIGN KEY (report_date, filename) REFERENCES daybreak_file(report_date, filename);


--
-- Name: law_document_law_file_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY law_document
    ADD CONSTRAINT law_document_law_file_id_fkey FOREIGN KEY (law_file_name) REFERENCES law_file(file_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: law_tree_doc_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY law_tree
    ADD CONSTRAINT law_tree_doc_id_fkey FOREIGN KEY (doc_id, doc_published_date) REFERENCES law_document(document_id, published_date) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: law_tree_law_file_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY law_tree
    ADD CONSTRAINT law_tree_law_file_fkey FOREIGN KEY (law_file) REFERENCES law_file(file_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: law_tree_law_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY law_tree
    ADD CONSTRAINT law_tree_law_id_fkey FOREIGN KEY (law_id) REFERENCES law_info(law_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: law_tree_parent_doc_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY law_tree
    ADD CONSTRAINT law_tree_parent_doc_id_fkey FOREIGN KEY (parent_doc_id, parent_doc_published_date) REFERENCES law_document(document_id, published_date) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: public_hearing_attendance_filename_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY public_hearing_attendance
    ADD CONSTRAINT public_hearing_attendance_filename_fkey FOREIGN KEY (filename) REFERENCES public_hearing(filename);


--
-- Name: public_hearing_attendance_session_member_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY public_hearing_attendance
    ADD CONSTRAINT public_hearing_attendance_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);


--
-- Name: public_hearing_committee_filename_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY public_hearing_committee
    ADD CONSTRAINT public_hearing_committee_filename_fkey FOREIGN KEY (filename) REFERENCES public_hearing(filename);


--
-- Name: public_hearing_filename_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY public_hearing
    ADD CONSTRAINT public_hearing_filename_fkey FOREIGN KEY (filename) REFERENCES public_hearing_file(filename);


--
-- Name: sobi_change_log_sobi_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY sobi_change_log
    ADD CONSTRAINT sobi_change_log_sobi_fragment_id_fkey FOREIGN KEY (sobi_fragment_id) REFERENCES sobi_fragment(fragment_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sobi_fragment_sobi_file_name_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY sobi_fragment
    ADD CONSTRAINT sobi_fragment_sobi_file_name_fkey FOREIGN KEY (sobi_file_name) REFERENCES sobi_file(file_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: spotcheck_mismatch_observation_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY spotcheck_mismatch
    ADD CONSTRAINT spotcheck_mismatch_observation_id_fkey FOREIGN KEY (observation_id) REFERENCES spotcheck_observation(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: spotcheck_observation_spotcheck_report_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY spotcheck_observation
    ADD CONSTRAINT spotcheck_observation_spotcheck_report_id_fkey FOREIGN KEY (report_id) REFERENCES spotcheck_report(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: transcript_transcript_file_fkey; Type: FK CONSTRAINT; Schema: master; Owner: postgres
--

ALTER TABLE ONLY transcript
    ADD CONSTRAINT transcript_transcript_file_fkey FOREIGN KEY (transcript_filename) REFERENCES transcript_file(file_name);


SET search_path = public, pg_catalog;

--
-- Name: member_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_person_id_fkey FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: session_member_member_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES member(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: master; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA master FROM PUBLIC;
REVOKE ALL ON SCHEMA master FROM postgres;
GRANT ALL ON SCHEMA master TO postgres;


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

