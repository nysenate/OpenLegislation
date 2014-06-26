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
-- Name: lbdc; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA lbdc;


ALTER SCHEMA lbdc OWNER TO postgres;

--
-- Name: master; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA master;


ALTER SCHEMA master OWNER TO postgres;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: hstore; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS hstore WITH SCHEMA public;


--
-- Name: EXTENSION hstore; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION hstore IS 'data type for storing sets of (key, value) pairs';


SET search_path = master, pg_catalog;

--
-- Name: sobi_fragment_type; Type: TYPE; Schema: master; Owner: ash
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


ALTER TYPE master.sobi_fragment_type OWNER TO ash;

--
-- Name: data_updated(); Type: FUNCTION; Schema: master; Owner: postgres
--

CREATE FUNCTION data_updated() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
 IF tg_op = 'UPDATE' THEN
   INSERT INTO master.sobi_change_log(table_name, action, key, data, modified_date_time, sobi_fragment_id)
   VALUES (tg_table_name::text, 'u', hstore(ARRAY['print_no', NEW.print_no, 'session_year', NEW.session_year::character varying]), hstore(new.*) - hstore(old.*), current_timestamp, NEW.last_fragment_file_name	);
   RETURN NEW;
 ELSIF tg_op = 'DELETE' THEN
   INSERT INTO master.sobi_change_log(table_name, action, key, data, modified_date_time)
   VALUES (tg_table_name::text, 'd', hstore(ARRAY['print_no', OLD.print_no, 'session_year', OLD.session_year::character varying]), hstore(old.*), current_timestamp);
   RETURN OLD;
 ELSIF tg_op = 'INSERT' THEN
   INSERT INTO master.sobi_change_log(table_name, action,key, data, modified_date_time, sobi_fragment_id)
   VALUES (tg_table_name::text, 'i', hstore(ARRAY['print_no', NEW.print_no, 'session_year', NEW.session_year::character varying]), hstore(new.*), current_timestamp, NEW.last_fragment_file_name);
   RETURN NEW;
 END IF;
END;
$$;


ALTER FUNCTION master.data_updated() OWNER TO postgres;

SET search_path = public, pg_catalog;

--
-- Name: test_args(character varying[]); Type: FUNCTION; Schema: public; Owner: ash
--

CREATE FUNCTION test_args(exclude_cols character varying[]) RETURNS hstore
    LANGUAGE sql
    AS $$SELECT hstore(exclude_cols)$$;


ALTER FUNCTION public.test_args(exclude_cols character varying[]) OWNER TO ash;

--
-- Name: write_muwhahah(); Type: FUNCTION; Schema: public; Owner: ash
--

CREATE FUNCTION write_muwhahah() RETURNS trigger
    LANGUAGE plpgsql
    AS $$BEGIN
  NEW.text := 'muwhahahahha';
  RETURN NEW;
END$$;


ALTER FUNCTION public.write_muwhahah() OWNER TO ash;

SET search_path = master, pg_catalog;

--
-- Name: openleg_fts_config; Type: TEXT SEARCH CONFIGURATION; Schema: master; Owner: ash
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


ALTER TEXT SEARCH CONFIGURATION master.openleg_fts_config OWNER TO ash;

SET search_path = lbdc, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: bill_daybreak; Type: TABLE; Schema: lbdc; Owner: ash; Tablespace:
--

CREATE TABLE bill_daybreak (
    daybreak_id integer,
    base_bill_print_no character varying,
    session_year integer
);


ALTER TABLE lbdc.bill_daybreak OWNER TO ash;

--
-- Name: daybreak; Type: TABLE; Schema: lbdc; Owner: ash; Tablespace:
--

CREATE TABLE daybreak (
    id integer NOT NULL,
    snapshot_date_time timestamp without time zone
);


ALTER TABLE lbdc.daybreak OWNER TO ash;

--
-- Name: daybreak_id_seq; Type: SEQUENCE; Schema: lbdc; Owner: ash
--

CREATE SEQUENCE daybreak_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE lbdc.daybreak_id_seq OWNER TO ash;

--
-- Name: daybreak_id_seq; Type: SEQUENCE OWNED BY; Schema: lbdc; Owner: ash
--

ALTER SEQUENCE daybreak_id_seq OWNED BY daybreak.id;


SET search_path = master, pg_catalog;

--
-- Name: action; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE action (
    id integer NOT NULL,
    date date,
    text text
);


ALTER TABLE master.action OWNER TO ash;

--
-- Name: action_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE action_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.action_id_seq OWNER TO ash;

--
-- Name: action_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE action_id_seq OWNED BY action.id;


--
-- Name: agenda; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda (
    agenda_no smallint NOT NULL
);


ALTER TABLE master.agenda OWNER TO ash;

--
-- Name: agenda_info_addendum; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_info_addendum (
    id character varying NOT NULL,
    agenda_no smallint NOT NULL,
    week_of date
);


ALTER TABLE master.agenda_info_addendum OWNER TO ash;

--
-- Name: agenda_info_committee; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_info_committee (
    agenda_info_addendum_id integer NOT NULL,
    committee_id integer NOT NULL,
    chair character varying,
    location character varying,
    meeting_date date,
    notes text
);


ALTER TABLE master.agenda_info_committee OWNER TO ash;

--
-- Name: agenda_info_committee_item; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_info_committee_item (
    id integer NOT NULL,
    agenda_addendum_id integer NOT NULL,
    committee_id character varying NOT NULL,
    bill_print_no character varying NOT NULL,
    bill_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    title text,
    message text
);


ALTER TABLE master.agenda_info_committee_item OWNER TO ash;

--
-- Name: agenda_info_committee_item_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE agenda_info_committee_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_info_committee_item_id_seq OWNER TO ash;

--
-- Name: agenda_info_committee_item_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE agenda_info_committee_item_id_seq OWNED BY agenda_info_committee_item.id;


--
-- Name: agenda_vote_action; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_action (
    id character varying(2),
    name character varying
);


ALTER TABLE master.agenda_vote_action OWNER TO ash;

--
-- Name: agenda_vote_addendum; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_addendum (
    id character varying NOT NULL,
    agenda_no smallint NOT NULL
);


ALTER TABLE master.agenda_vote_addendum OWNER TO ash;

--
-- Name: agenda_vote_committee; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_committee (
    id integer NOT NULL,
    agenda_vote_addendum_id character varying NOT NULL,
    agenda_no smallint NOT NULL,
    committee_id character varying NOT NULL,
    chair character varying,
    meet_date date
);


ALTER TABLE master.agenda_vote_committee OWNER TO ash;

--
-- Name: agenda_vote_committee_attend; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_committee_attend (
    id integer NOT NULL,
    agenda_vote_comm_id integer,
    name character varying,
    rank smallint,
    party character varying,
    attendance text
);


ALTER TABLE master.agenda_vote_committee_attend OWNER TO ash;

--
-- Name: agenda_vote_committee_attend_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE agenda_vote_committee_attend_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_attend_id_seq OWNER TO ash;

--
-- Name: agenda_vote_committee_attend_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE agenda_vote_committee_attend_id_seq OWNED BY agenda_vote_committee_attend.id;


--
-- Name: agenda_vote_committee_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE agenda_vote_committee_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_id_seq OWNER TO ash;

--
-- Name: agenda_vote_committee_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE agenda_vote_committee_id_seq OWNED BY agenda_vote_committee.id;


--
-- Name: agenda_vote_committee_item; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_committee_item (
    id integer NOT NULL,
    bill_print_no character varying,
    bill_year smallint,
    bill_amend_version character(1),
    agenda_vote_action_id character varying(2),
    refer_committee_id character varying,
    with_amd boolean DEFAULT false
);


ALTER TABLE master.agenda_vote_committee_item OWNER TO ash;

--
-- Name: agenda_vote_committee_item_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE agenda_vote_committee_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_item_id_seq OWNER TO ash;

--
-- Name: agenda_vote_committee_item_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE agenda_vote_committee_item_id_seq OWNED BY agenda_vote_committee_item.id;


--
-- Name: agenda_vote_committee_vote; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE agenda_vote_committee_vote (
    id integer NOT NULL,
    agenda_vote_comm_item_id integer,
    name character varying,
    party character varying,
    rank smallint,
    vote character varying
);


ALTER TABLE master.agenda_vote_committee_vote OWNER TO ash;

--
-- Name: agenda_vote_committee_vote_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE agenda_vote_committee_vote_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.agenda_vote_committee_vote_id_seq OWNER TO ash;

--
-- Name: agenda_vote_committee_vote_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE agenda_vote_committee_vote_id_seq OWNED BY agenda_vote_committee_vote.id;


--
-- Name: bill; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill (
    print_no character varying NOT NULL,
    session_year smallint NOT NULL,
    title text,
    law_section text,
    summary text,
    active_version character(1),
    sponsor_id character varying,
    active_year integer,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    last_fragment_file_name character varying,
    last_fragment_type character varying,
    previous_versions character varying[],
    law_code text
);


ALTER TABLE master.bill OWNER TO ash;

--
-- Name: TABLE bill; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill IS 'General information about a bill';


--
-- Name: bill_amendment; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_amendment (
    bill_print_no character varying NOT NULL,
    bill_session_year smallint NOT NULL,
    version character(1) NOT NULL,
    sponsor_memo text,
    act_clause text,
    full_text text,
    stricken boolean DEFAULT false,
    current_committee_id character varying,
    uni_bill boolean DEFAULT false,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    last_fragment_file_name character varying,
    last_fragment_type character varying
);


ALTER TABLE master.bill_amendment OWNER TO ash;

--
-- Name: TABLE bill_amendment; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_amendment IS 'Information specific to a bill amendment';


--
-- Name: bill_amendment_action; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_amendment_action (
    bill_print_no character varying NOT NULL,
    bill_session_year smallint NOT NULL,
    bill_amend_version character(1) NOT NULL,
    effect_date date,
    text text,
    modified_date_time timestamp without time zone,
    published_date_time timestamp without time zone,
    last_fragment_file_name character varying,
    last_fragment_type character varying,
    sequence_no smallint NOT NULL
);


ALTER TABLE master.bill_amendment_action OWNER TO ash;

--
-- Name: TABLE bill_amendment_action; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_amendment_action IS 'Actions that have been taken on an amendment';


--
-- Name: bill_amendment_cosponsor; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_amendment_cosponsor (
    print_no character varying NOT NULL,
    session_year smallint NOT NULL,
    version character(1) NOT NULL,
    cosponsor_id character varying NOT NULL
);


ALTER TABLE master.bill_amendment_cosponsor OWNER TO ash;

--
-- Name: TABLE bill_amendment_cosponsor; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_amendment_cosponsor IS 'Listing of co-sponsors for an amendment';


--
-- Name: bill_amendment_same_as; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_amendment_same_as (
    bill_print_no character varying,
    bill_session_year smallint,
    bill_amend_version character(1),
    same_as_bill_print_no character varying,
    same_as_session_year smallint,
    same_as_amend_version character(1)
);


ALTER TABLE master.bill_amendment_same_as OWNER TO ash;

--
-- Name: TABLE bill_amendment_same_as; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_amendment_same_as IS 'Same as bills for an amendment';


--
-- Name: bill_amendment_vote; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_amendment_vote (
    blah integer NOT NULL
);


ALTER TABLE master.bill_amendment_vote OWNER TO ash;

--
-- Name: TABLE bill_amendment_vote; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_amendment_vote IS 'Votes taken on the amendments';


--
-- Name: bill_amendment_vote_blah_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE bill_amendment_vote_blah_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.bill_amendment_vote_blah_seq OWNER TO ash;

--
-- Name: bill_amendment_vote_blah_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE bill_amendment_vote_blah_seq OWNED BY bill_amendment_vote.blah;


--
-- Name: bill_committee; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_committee (
    bill_print_no character varying NOT NULL,
    bill_session_year smallint NOT NULL,
    committee_id character varying NOT NULL
);


ALTER TABLE master.bill_committee OWNER TO ash;

--
-- Name: TABLE bill_committee; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_committee IS 'Mapping of bills to committees';


--
-- Name: bill_multi_sponsor; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_multi_sponsor (
    bill_print_no character varying NOT NULL,
    bill_session_year smallint NOT NULL,
    sponsor character varying NOT NULL
);


ALTER TABLE master.bill_multi_sponsor OWNER TO ash;

--
-- Name: TABLE bill_multi_sponsor; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE bill_multi_sponsor IS 'Listing of multi-sponsors for a bill';


--
-- Name: bill_previous_version; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE bill_previous_version (
    bill_print_no character varying NOT NULL,
    bill_session_year smallint NOT NULL,
    prev_bill_print_no character varying,
    prev_session_year smallint
);


ALTER TABLE master.bill_previous_version OWNER TO ash;

--
-- Name: calendar; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar (
    calendar_no integer NOT NULL,
    year smallint NOT NULL
);


ALTER TABLE master.calendar OWNER TO ash;

--
-- Name: calendar_active_list; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar_active_list (
    id integer NOT NULL,
    active_list_no smallint,
    calendar_no smallint,
    calendar_year smallint,
    calendar_date date,
    release_date_time timestamp without time zone
);


ALTER TABLE master.calendar_active_list OWNER TO ash;

--
-- Name: calendar_active_list_entry; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar_active_list_entry (
    calendar_active_list_id smallint NOT NULL,
    bill_calendar_no smallint NOT NULL,
    bill_print_no character varying,
    bill_amend_version character(1),
    bill_session_year smallint
);


ALTER TABLE master.calendar_active_list_entry OWNER TO ash;

--
-- Name: calendar_active_list_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE calendar_active_list_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_active_list_id_seq OWNER TO ash;

--
-- Name: calendar_active_list_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE calendar_active_list_id_seq OWNED BY calendar_active_list.id;


--
-- Name: calendar_supplemental; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar_supplemental (
    id integer NOT NULL,
    calendar_no smallint NOT NULL,
    calendar_year smallint NOT NULL,
    sup_version character varying NOT NULL,
    calendar_date date,
    release_date_time timestamp without time zone
);


ALTER TABLE master.calendar_supplemental OWNER TO ash;

--
-- Name: calendar_supplemental_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE calendar_supplemental_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_supplemental_id_seq OWNER TO ash;

--
-- Name: calendar_supplemental_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE calendar_supplemental_id_seq OWNED BY calendar_supplemental.id;


--
-- Name: calendar_supplemental_section; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar_supplemental_section (
    calendar_sup_id integer NOT NULL,
    section_code smallint NOT NULL
);


ALTER TABLE master.calendar_supplemental_section OWNER TO ash;

--
-- Name: calendar_supplemental_section_entry; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE calendar_supplemental_section_entry (
    id integer NOT NULL,
    calendar_sup_id integer,
    sup_section_code smallint,
    bill_calendar_no smallint,
    bill_print_no character varying,
    bill_amend_version character(1),
    bill_session_year smallint,
    sub_bill_print_no character varying,
    sub_bill_amend_version character(1),
    sub_bill_session_year smallint,
    high boolean
);


ALTER TABLE master.calendar_supplemental_section_entry OWNER TO ash;

--
-- Name: COLUMN calendar_supplemental_section_entry.sub_bill_print_no; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN calendar_supplemental_section_entry.sub_bill_print_no IS 'The substituted bill''s print no, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_section_entry.sub_bill_amend_version; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN calendar_supplemental_section_entry.sub_bill_amend_version IS 'The substituted bill''s amendment version, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_section_entry.sub_bill_session_year; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN calendar_supplemental_section_entry.sub_bill_session_year IS 'The substituted bill''s session year, null if not substituted.';


--
-- Name: COLUMN calendar_supplemental_section_entry.high; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN calendar_supplemental_section_entry.high IS 'true if bill has not yet properly aged';


--
-- Name: calendar_supplemental_section_entry_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE calendar_supplemental_section_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.calendar_supplemental_section_entry_id_seq OWNER TO ash;

--
-- Name: calendar_supplemental_section_entry_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE calendar_supplemental_section_entry_id_seq OWNED BY calendar_supplemental_section_entry.id;


--
-- Name: committee; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE committee (
    id character varying NOT NULL,
    name character varying NOT NULL
);


ALTER TABLE master.committee OWNER TO ash;

--
-- Name: person; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE person (
    id character varying NOT NULL,
    short_name character varying NOT NULL,
    title character varying,
    first_name character varying,
    mid_name character varying,
    last_name character varying,
    session_year smallint NOT NULL,
    branch character varying
);


ALTER TABLE master.person OWNER TO ash;

--
-- Name: sobi_change_log; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE sobi_change_log (
    id integer NOT NULL,
    table_name character varying NOT NULL,
    action character varying NOT NULL,
    key public.hstore,
    data public.hstore,
    modified_date_time timestamp without time zone NOT NULL,
    sobi_fragment_id character varying
);


ALTER TABLE master.sobi_change_log OWNER TO ash;

--
-- Name: TABLE sobi_change_log; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE sobi_change_log IS 'Change log for all entities utilizing SOBI files as the primary data source';


--
-- Name: sobi_change_log_id_seq; Type: SEQUENCE; Schema: master; Owner: ash
--

CREATE SEQUENCE sobi_change_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE master.sobi_change_log_id_seq OWNER TO ash;

--
-- Name: sobi_change_log_id_seq; Type: SEQUENCE OWNED BY; Schema: master; Owner: ash
--

ALTER SEQUENCE sobi_change_log_id_seq OWNED BY sobi_change_log.id;


--
-- Name: sobi_file; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE sobi_file (
    file_name character varying(128) NOT NULL,
    published_date_time timestamp without time zone NOT NULL,
    processed_date_time timestamp without time zone,
    processed_count smallint DEFAULT 0,
    pending_processing boolean DEFAULT false,
    staged_date_time timestamp without time zone
);


ALTER TABLE master.sobi_file OWNER TO ash;

--
-- Name: TABLE sobi_file; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE sobi_file IS 'Listing of all SOBI files';


--
-- Name: COLUMN sobi_file.processed_count; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN sobi_file.processed_count IS 'Number of times this sobi has been processed';


--
-- Name: COLUMN sobi_file.pending_processing; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON COLUMN sobi_file.pending_processing IS 'True if SOBI is awaiting processing';


--
-- Name: sobi_fragment; Type: TABLE; Schema: master; Owner: ash; Tablespace:
--

CREATE TABLE sobi_fragment (
    sobi_file_name character varying NOT NULL,
    fragment_file_name character varying NOT NULL,
    published_date_time timestamp without time zone,
    sobi_fragment_type character varying NOT NULL,
    file_counter smallint,
    text text
);


ALTER TABLE master.sobi_fragment OWNER TO ash;

--
-- Name: TABLE sobi_fragment; Type: COMMENT; Schema: master; Owner: ash
--

COMMENT ON TABLE sobi_fragment IS 'Listing of all SOBI fragments';


SET search_path = public, pg_catalog;

--
-- Name: environment; Type: TABLE; Schema: public; Owner: ash; Tablespace:
--

CREATE TABLE environment (
    id integer NOT NULL,
    schema character varying NOT NULL,
    base_directory character varying NOT NULL,
    staging_directory character varying NOT NULL,
    working_directory character varying NOT NULL,
    archive_directory character varying NOT NULL,
    created_date_time timestamp without time zone NOT NULL,
    modified_date_time timestamp without time zone NOT NULL,
    active boolean NOT NULL
);


ALTER TABLE public.environment OWNER TO ash;

--
-- Name: environment_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE environment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.environment_id_seq OWNER TO ash;

--
-- Name: environment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE environment_id_seq OWNED BY environment.id;


--
-- Name: test_fts_billtext; Type: TABLE; Schema: public; Owner: ash; Tablespace:
--

CREATE TABLE test_fts_billtext (
    amendment_letter character(1),
    full_text text NOT NULL,
    print_no character varying(20) NOT NULL,
    full_text_vector tsvector
);


ALTER TABLE public.test_fts_billtext OWNER TO ash;

--
-- Name: test_hstore; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

CREATE TABLE test_hstore (
    id integer NOT NULL,
    store hstore
);


ALTER TABLE public.test_hstore OWNER TO postgres;

--
-- Name: test_hstore_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE test_hstore_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.test_hstore_id_seq OWNER TO postgres;

--
-- Name: test_hstore_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE test_hstore_id_seq OWNED BY test_hstore.id;


--
-- Name: test_trigger; Type: TABLE; Schema: public; Owner: ash; Tablespace:
--

CREATE TABLE test_trigger (
    id integer NOT NULL,
    text character varying
);


ALTER TABLE public.test_trigger OWNER TO ash;

--
-- Name: test_trigger_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE test_trigger_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.test_trigger_id_seq OWNER TO ash;

--
-- Name: test_trigger_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE test_trigger_id_seq OWNED BY test_trigger.id;


SET search_path = lbdc, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: lbdc; Owner: ash
--

ALTER TABLE ONLY daybreak ALTER COLUMN id SET DEFAULT nextval('daybreak_id_seq'::regclass);


SET search_path = master, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY action ALTER COLUMN id SET DEFAULT nextval('action_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_info_committee_item ALTER COLUMN id SET DEFAULT nextval('agenda_info_committee_item_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_vote_committee ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_vote_committee_attend ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_attend_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_vote_committee_item ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_item_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_vote_committee_vote ALTER COLUMN id SET DEFAULT nextval('agenda_vote_committee_vote_id_seq'::regclass);


--
-- Name: blah; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment_vote ALTER COLUMN blah SET DEFAULT nextval('bill_amendment_vote_blah_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY calendar_active_list ALTER COLUMN id SET DEFAULT nextval('calendar_active_list_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY calendar_supplemental ALTER COLUMN id SET DEFAULT nextval('calendar_supplemental_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY calendar_supplemental_section_entry ALTER COLUMN id SET DEFAULT nextval('calendar_supplemental_section_entry_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: master; Owner: ash
--

ALTER TABLE ONLY sobi_change_log ALTER COLUMN id SET DEFAULT nextval('sobi_change_log_id_seq'::regclass);


SET search_path = public, pg_catalog;

--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY environment ALTER COLUMN id SET DEFAULT nextval('environment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_hstore ALTER COLUMN id SET DEFAULT nextval('test_hstore_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY test_trigger ALTER COLUMN id SET DEFAULT nextval('test_trigger_id_seq'::regclass);


SET search_path = lbdc, pg_catalog;

--
-- Name: daybreak_snapshot_date_time_key; Type: CONSTRAINT; Schema: lbdc; Owner: ash; Tablespace:
--

ALTER TABLE ONLY daybreak
    ADD CONSTRAINT daybreak_snapshot_date_time_key UNIQUE (snapshot_date_time);


SET search_path = master, pg_catalog;

--
-- Name: action_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY action
    ADD CONSTRAINT action_pkey PRIMARY KEY (id);


--
-- Name: agenda_info_addendum_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_info_addendum
    ADD CONSTRAINT agenda_info_addendum_pkey PRIMARY KEY (id, agenda_no);


--
-- Name: agenda_info_committee_item_agenda_addendum_id_committee_id__key; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_info_committee_item
    ADD CONSTRAINT agenda_info_committee_item_agenda_addendum_id_committee_id__key UNIQUE (agenda_addendum_id, committee_id, bill_print_no, bill_year, bill_amend_version);


--
-- Name: agenda_info_committee_item_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_info_committee_item
    ADD CONSTRAINT agenda_info_committee_item_pkey PRIMARY KEY (id);


--
-- Name: agenda_info_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_info_committee
    ADD CONSTRAINT agenda_info_committee_pkey PRIMARY KEY (agenda_info_addendum_id, committee_id);


--
-- Name: agenda_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda
    ADD CONSTRAINT agenda_pkey PRIMARY KEY (agenda_no);


--
-- Name: agenda_vote_addendum_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_vote_addendum
    ADD CONSTRAINT agenda_vote_addendum_pkey PRIMARY KEY (id, agenda_no);


--
-- Name: agenda_vote_committee_agenda_vote_addendum_id_agenda_number_key; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_agenda_vote_addendum_id_agenda_number_key UNIQUE (agenda_vote_addendum_id, agenda_no, committee_id);


--
-- Name: agenda_vote_committee_attend_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_vote_committee_attend
    ADD CONSTRAINT agenda_vote_committee_attend_pkey PRIMARY KEY (id);


--
-- Name: agenda_vote_committee_item_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_vote_committee_item
    ADD CONSTRAINT agenda_vote_committee_item_pkey PRIMARY KEY (id);


--
-- Name: agenda_vote_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY agenda_vote_committee
    ADD CONSTRAINT agenda_vote_committee_pkey PRIMARY KEY (id);


--
-- Name: bill_amendment_cosponsor_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_pkey PRIMARY KEY (print_no, session_year, version, cosponsor_id);


--
-- Name: bill_amendment_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_pkey PRIMARY KEY (bill_print_no, bill_session_year, version);


--
-- Name: bill_multi_sponsor_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY bill_multi_sponsor
    ADD CONSTRAINT bill_multi_sponsor_pkey PRIMARY KEY (bill_print_no, bill_session_year, sponsor);


--
-- Name: bill_past_committee_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY bill_committee
    ADD CONSTRAINT bill_past_committee_pkey PRIMARY KEY (bill_print_no, bill_session_year, committee_id);


--
-- Name: bill_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY bill
    ADD CONSTRAINT bill_pkey PRIMARY KEY (print_no, session_year);


--
-- Name: calendar_active_list_entry_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY calendar_active_list_entry
    ADD CONSTRAINT calendar_active_list_entry_pkey PRIMARY KEY (calendar_active_list_id, bill_calendar_no);


--
-- Name: calendar_active_list_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_pkey PRIMARY KEY (id);


--
-- Name: calendar_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY calendar
    ADD CONSTRAINT calendar_pkey PRIMARY KEY (calendar_no, year);


--
-- Name: committee_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY committee
    ADD CONSTRAINT committee_pkey PRIMARY KEY (id);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: person_short_name_session_year_key; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_short_name_session_year_key UNIQUE (short_name, session_year);


--
-- Name: sobi_fragment_fragment_file_name_sobi_fragment_type_key; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY sobi_fragment
    ADD CONSTRAINT sobi_fragment_fragment_file_name_sobi_fragment_type_key UNIQUE (fragment_file_name, sobi_fragment_type);


--
-- Name: sobi_fragment_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY sobi_fragment
    ADD CONSTRAINT sobi_fragment_pkey PRIMARY KEY (fragment_file_name);


--
-- Name: sobi_pkey; Type: CONSTRAINT; Schema: master; Owner: ash; Tablespace:
--

ALTER TABLE ONLY sobi_file
    ADD CONSTRAINT sobi_pkey PRIMARY KEY (file_name);


SET search_path = public, pg_catalog;

--
-- Name: environment_pkey; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace:
--

ALTER TABLE ONLY environment
    ADD CONSTRAINT environment_pkey PRIMARY KEY (id);


--
-- Name: environment_schema_key; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace:
--

ALTER TABLE ONLY environment
    ADD CONSTRAINT environment_schema_key UNIQUE (schema);


SET search_path = master, pg_catalog;

--
-- Name: sobi_change_log_keygin; Type: INDEX; Schema: master; Owner: ash; Tablespace:
--

CREATE INDEX sobi_change_log_keygin ON sobi_change_log USING gin (key);


SET search_path = public, pg_catalog;

--
-- Name: test_gin_index; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX test_gin_index ON test_hstore USING btree (((store -> 'print_no'::text)));


--
-- Name: test_gin_index_2; Type: INDEX; Schema: public; Owner: postgres; Tablespace:
--

CREATE INDEX test_gin_index_2 ON test_hstore USING gin (store);


--
-- Name: textsearch_idx; Type: INDEX; Schema: public; Owner: ash; Tablespace:
--

CREATE INDEX textsearch_idx ON test_fts_billtext USING gin (full_text_vector);


SET search_path = master, pg_catalog;

--
-- Name: log_bill_changes; Type: TRIGGER; Schema: master; Owner: ash
--

CREATE TRIGGER log_bill_changes BEFORE INSERT OR DELETE OR UPDATE ON bill FOR EACH ROW EXECUTE PROCEDURE data_updated();


SET search_path = public, pg_catalog;

--
-- Name: insert_muwahaha; Type: TRIGGER; Schema: public; Owner: ash
--

CREATE TRIGGER insert_muwahaha BEFORE INSERT OR UPDATE ON test_trigger FOR EACH ROW EXECUTE PROCEDURE write_muwhahah();


SET search_path = master, pg_catalog;

--
-- Name: agenda_info_addendum_agenda_number_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY agenda_info_addendum
    ADD CONSTRAINT agenda_info_addendum_agenda_number_fkey FOREIGN KEY (agenda_no) REFERENCES agenda(agenda_no);


--
-- Name: bill_amendment_action_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment_action
    ADD CONSTRAINT bill_amendment_action_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(print_no, session_year) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_amendment_cosponsor_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment_cosponsor
    ADD CONSTRAINT bill_amendment_cosponsor_print_no_fkey FOREIGN KEY (print_no, session_year, version) REFERENCES bill_amendment(bill_print_no, bill_session_year, version);


--
-- Name: bill_amendment_current_committee_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_current_committee_fkey FOREIGN KEY (current_committee_id) REFERENCES committee(id);


--
-- Name: bill_amendment_last_fragment_file_name_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment
    ADD CONSTRAINT bill_amendment_last_fragment_file_name_fkey FOREIGN KEY (last_fragment_file_name, last_fragment_type) REFERENCES sobi_fragment(fragment_file_name, sobi_fragment_type) ON UPDATE SET NULL ON DELETE SET NULL;


--
-- Name: bill_amendment_same_as_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_amendment_same_as
    ADD CONSTRAINT bill_amendment_same_as_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year, bill_amend_version) REFERENCES bill_amendment(bill_print_no, bill_session_year, version) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: bill_last_fragment_file_name_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill
    ADD CONSTRAINT bill_last_fragment_file_name_fkey FOREIGN KEY (last_fragment_file_name, last_fragment_type) REFERENCES sobi_fragment(fragment_file_name, sobi_fragment_type) ON UPDATE SET NULL ON DELETE SET NULL;


--
-- Name: bill_past_committee_bill_print_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY bill_committee
    ADD CONSTRAINT bill_past_committee_bill_print_no_fkey FOREIGN KEY (bill_print_no, bill_session_year) REFERENCES bill(print_no, session_year);


--
-- Name: calendar_active_list_calendar_number_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY calendar_active_list
    ADD CONSTRAINT calendar_active_list_calendar_number_fkey FOREIGN KEY (calendar_no, calendar_year) REFERENCES calendar(calendar_no, year);


--
-- Name: calendar_supplemental_calendar_no_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY calendar_supplemental
    ADD CONSTRAINT calendar_supplemental_calendar_no_fkey FOREIGN KEY (calendar_no, calendar_year) REFERENCES calendar(calendar_no, year);


--
-- Name: sobi_change_log_sobi_fragment_id_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY sobi_change_log
    ADD CONSTRAINT sobi_change_log_sobi_fragment_id_fkey FOREIGN KEY (sobi_fragment_id) REFERENCES sobi_fragment(fragment_file_name) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: sobi_fragment_sobi_file_name_fkey; Type: FK CONSTRAINT; Schema: master; Owner: ash
--

ALTER TABLE ONLY sobi_fragment
    ADD CONSTRAINT sobi_fragment_sobi_file_name_fkey FOREIGN KEY (sobi_file_name) REFERENCES sobi_file(file_name) ON UPDATE CASCADE ON DELETE CASCADE;


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
