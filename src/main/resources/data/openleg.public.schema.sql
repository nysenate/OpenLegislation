--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

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

SET default_tablespace = '';

SET default_with_oids = false;

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
-- Name: member; Type: TABLE; Schema: public; Owner: ash; Tablespace: 
--

CREATE TABLE member (
    id integer NOT NULL,
    person_id integer NOT NULL,
    chamber chamber NOT NULL,
    incumbent boolean DEFAULT false,
    full_name character varying
);


ALTER TABLE public.member OWNER TO ash;

--
-- Name: COLUMN member.id; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN member.id IS 'Unique member id';


--
-- Name: COLUMN member.person_id; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN member.person_id IS 'Reference to the person id';


--
-- Name: COLUMN member.chamber; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN member.chamber IS 'Indicates if member is in senate or assembly';


--
-- Name: COLUMN member.incumbent; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN member.incumbent IS 'If true, member is currently in office';


--
-- Name: COLUMN member.full_name; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN member.full_name IS 'Full name of member listed for convenience';


--
-- Name: member_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.member_id_seq OWNER TO ash;

--
-- Name: member_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE member_id_seq OWNED BY member.id;


--
-- Name: member_person_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE member_person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.member_person_id_seq OWNER TO ash;

--
-- Name: member_person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE member_person_id_seq OWNED BY member.person_id;


--
-- Name: person; Type: TABLE; Schema: public; Owner: ash; Tablespace: 
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


ALTER TABLE public.person OWNER TO ash;

--
-- Name: COLUMN person.id; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.id IS 'Unique person id';


--
-- Name: COLUMN person.full_name; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.full_name IS 'Full name of person';


--
-- Name: COLUMN person.first_name; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.first_name IS 'First name of person';


--
-- Name: COLUMN person.middle_name; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.middle_name IS 'Middle name (or initial) of person';


--
-- Name: COLUMN person.last_name; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.last_name IS 'Last name of person';


--
-- Name: COLUMN person.email; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.email IS 'The email of the person';


--
-- Name: COLUMN person.prefix; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.prefix IS 'Prefix (Mr, Mrs, Senator, etc)';


--
-- Name: COLUMN person.suffix; Type: COMMENT; Schema: public; Owner: ash
--

COMMENT ON COLUMN person.suffix IS 'Suffix (Jr, Sr, etc)';


--
-- Name: person_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE person_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.person_id_seq OWNER TO ash;

--
-- Name: person_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE person_id_seq OWNED BY person.id;


--
-- Name: session_member; Type: TABLE; Schema: public; Owner: ash; Tablespace: 
--

CREATE TABLE session_member (
    id integer NOT NULL,
    member_id integer NOT NULL,
    lbdc_short_name character varying NOT NULL,
    session_year smallint NOT NULL,
    district_code smallint
);


ALTER TABLE public.session_member OWNER TO ash;

--
-- Name: session_member_id_seq; Type: SEQUENCE; Schema: public; Owner: ash
--

CREATE SEQUENCE session_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.session_member_id_seq OWNER TO ash;

--
-- Name: session_member_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ash
--

ALTER SEQUENCE session_member_id_seq OWNED BY session_member.id;


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


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY environment ALTER COLUMN id SET DEFAULT nextval('environment_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY member ALTER COLUMN id SET DEFAULT nextval('member_id_seq'::regclass);


--
-- Name: person_id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY member ALTER COLUMN person_id SET DEFAULT nextval('member_person_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY person ALTER COLUMN id SET DEFAULT nextval('person_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY session_member ALTER COLUMN id SET DEFAULT nextval('session_member_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY test_hstore ALTER COLUMN id SET DEFAULT nextval('test_hstore_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ash
--

ALTER TABLE ONLY test_trigger ALTER COLUMN id SET DEFAULT nextval('test_trigger_id_seq'::regclass);


--
-- Data for Name: environment; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY environment (id, schema, base_directory, staging_directory, working_directory, archive_directory, created_date_time, modified_date_time, active) FROM stdin;
1	master	/home/ash/Web/nysenate/data/openleg/latest_session_master	/home/ash/Web/nysenate/data/openleg/latest_session_master/work	/home/ash/Web/nysenate/data/openleg/latest_session_master/data	/home/ash/Web/nysenate/data/openleg/latest_session_master/processed	2014-05-27 16:49:46.014498	2014-05-27 16:49:46.014498	t
\.


--
-- Name: environment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('environment_id_seq', 1, true);


--
-- Data for Name: member; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY member (id, person_id, chamber, incumbent, full_name) FROM stdin;
454	276	senate	f	Hiram Monserrate
456	308	assembly	f	Michele Titus
457	309	assembly	f	Vivian Cook
458	310	assembly	f	Barbara Clark
459	311	assembly	f	Michael DenDekker
460	312	assembly	f	Jeffrion Aubry
461	299	assembly	f	Grace Meng
462	286	assembly	f	Andrew Raia
463	278	assembly	f	Marc Alessi 
464	279	assembly	f	Fred Thiele
465	280	assembly	f	L. Dean Murray
466	281	assembly	f	Steven Englebright
467	282	assembly	f	Ginny Fields 
468	283	assembly	f	Philip Ramos
469	284	assembly	f	Michael J. Fitzpatrick
471	287	assembly	f	James Conte
472	288	assembly	f	Robert Sweeney
473	289	assembly	f	Joseph Saladino
474	290	assembly	f	Charles Lavine
475	291	assembly	f	Robert Barra 
476	292	assembly	f	Michael Montesano
477	293	assembly	f	Michelle Schimel
478	294	assembly	f	Thomas McKevitt
479	295	assembly	f	Earlene Hooper
480	296	assembly	f	David McDonough
481	297	assembly	f	Harvey Weisenberg
482	298	assembly	f	Thomas Alfano 
483	300	assembly	f	Audrey Pheffer
484	301	assembly	f	David Weprin
485	302	assembly	f	Rory Lancman
486	303	assembly	f	Ann-Margaret Carrozza 
487	304	assembly	f	Nettie Mayersohn
488	305	assembly	f	Andrew Hevesi
489	306	assembly	f	William Scarborough
490	307	assembly	f	Margaret Markey
491	314	assembly	f	Catherine Nolan
492	315	assembly	f	Michael G. Miller
493	316	assembly	f	Inez Barron
494	317	assembly	f	Helene Weinstein
495	318	assembly	f	Rhoda Jacobs
496	319	assembly	f	Karim Camara
497	320	assembly	f	James F. Brennan
498	321	assembly	f	Steven Cymbrowitz
499	322	assembly	f	Alec Brook-Krasny
500	323	assembly	f	William Colton
501	324	assembly	f	Dov Hikind
502	325	assembly	f	Peter Abbate
503	326	assembly	f	Joseph Lentol
504	327	assembly	f	Felix Ortiz
505	328	assembly	f	Joan Millman
506	329	assembly	f	Vito Lopez
507	330	assembly	f	Darryl Towns
508	331	assembly	f	"William Boyland
509	332	assembly	f	Annette Robinson
510	333	assembly	f	Hakeem Jeffries
511	334	assembly	f	N. Nick Perry
512	335	assembly	f	Alan Maisel
513	336	assembly	f	Janele Hyer-Spencer 
514	337	assembly	f	Matthew Titone
515	338	assembly	f	Louis Tobacco
516	339	assembly	f	Michael Cusick
517	340	assembly	f	Sheldon Silver
518	341	assembly	f	Micah Kellner
519	342	assembly	f	Deborah Glick
520	343	assembly	f	Linda Rosenthal
521	344	assembly	f	Adam Clayton Powell IV 
522	345	assembly	f	Daniel O'Donnell
523	346	assembly	f	Keith L. T. Wright
524	347	assembly	f	Herman D. Farrell
525	349	assembly	f	Jonathan Bing
526	350	assembly	f	Brian Kavanagh
527	351	assembly	f	Richard Gottfried
528	352	assembly	f	Peter Rivera
529	353	assembly	f	Vanessa Gibson
530	354	assembly	f	Jose Rivera
531	355	assembly	f	Michael Benjamin 
532	356	assembly	f	Naomi Rivera
533	357	assembly	f	Jeffrey Dinowitz
534	358	assembly	f	Michael Benedetto
535	359	assembly	f	Carl Heastie
536	360	assembly	f	Carmen E. Arroyo
537	361	assembly	f	Marcos Crespo
538	362	assembly	f	Nelson Castro
539	363	assembly	f	J. Gary Pretlow
540	364	assembly	f	Amy Paulin
541	365	assembly	f	Robert Castelli
543	368	assembly	f	Richard Brodsky 
544	369	assembly	f	Mike Spano
545	370	assembly	f	"Kenneth Zebrowski
546	371	assembly	f	Ellen C. Jaffee
547	372	assembly	f	Nancy Calhoun
548	373	assembly	f	Ann Rabbitt
549	374	assembly	f	Aileen Gunther
550	376	assembly	f	Frank Skartados 
551	377	assembly	f	Kevin Cahill
552	378	assembly	f	Joel Miller
553	379	assembly	f	Marcus Molinaro
554	380	assembly	f	John McEneny
555	381	assembly	f	George Amedore
556	382	assembly	f	Ronald Canestrari
557	383	assembly	f	Clifford Crouch
558	384	assembly	f	Timothy P. Gordon 
559	385	assembly	f	Robert Reilly
560	386	assembly	f	James Tedisco
561	387	assembly	f	William Magee
562	388	assembly	f	Tony Jordan
563	389	assembly	f	Teresa Sayward
564	390	assembly	f	Janet Duprey
565	391	assembly	f	David Townsend 
566	392	assembly	f	RoAnn Destito
567	393	assembly	f	Marc Butler
568	394	assembly	f	Addie Jenne Russell
569	395	assembly	f	Joan Christensen 
570	396	assembly	f	William Magnarelli
571	397	assembly	f	"Albert A. Stirpe
572	398	assembly	f	Dierdre Scozzafava 
573	399	assembly	f	Gary Finch
574	400	assembly	f	William A. Barclay
575	401	assembly	f	Barbara Lifton
576	402	assembly	f	Donna Lupardo
577	403	assembly	f	Peter Lopez
578	404	assembly	f	Robert Oaks
579	405	assembly	f	Brian Kolb
580	406	assembly	f	Joseph Errigo 
581	407	assembly	f	Susan John 
582	408	assembly	f	Joseph Morelle
583	409	assembly	f	David Gantt
584	410	assembly	f	Bill Reilich
585	411	assembly	f	David Koon 
586	412	assembly	f	James Bacalles 
587	413	assembly	f	Tom O'Mara 
588	414	assembly	f	Francine DelMonte 
589	415	assembly	f	Stephen Hawley
590	416	assembly	f	Robin Schimminger
369	188	senate	t	John L. Sampson
370	189	senate	t	David Carlucci
371	190	senate	t	James L. Seward
431	250	senate	t	Cecilia Tkaczyk
401	220	senate	t	Liz Krueger
374	193	senate	t	William J. Larkin Jr.
375	194	senate	t	Bill Perkins
376	195	senate	t	John A. DeFrancisco
377	196	senate	t	Gustavo Rivera
432	251	senate	t	James Sanders Jr.
379	198	senate	t	Betty Little
380	199	senate	t	Ruth Hassell-Thompson
426	245	senate	t	Martin Malavé Dilan
383	202	senate	t	Michael Gianaris
385	204	senate	t	Adriano Espaillat
433	254	senate	t	Phil Boyle
373	192	senate	t	José M. Serrano
434	255	senate	t	George Latimer
435	256	senate	t	Kathleen A. Marchione
390	209	senate	t	Jeffrey D. Klein
429	248	senate	t	Timothy M. Kennedy
391	210	senate	t	Tony Avella
436	257	senate	t	Ted O'Brien
393	212	senate	t	Joseph A. Griffo
394	213	senate	t	Michael F. Nozzolio
418	237	senate	t	Hugh T. Farley
396	215	senate	t	Andrea Stewart-Cousins
397	216	senate	t	Jack M. Martins
398	217	senate	t	Mark Grisanti
399	218	senate	t	John J. Flanagan
400	219	senate	t	Toby Ann Stavisky
412	231	senate	t	Daniel L. Squadron
437	258	senate	t	Terry Gipson
403	222	senate	t	Diane J. Savino
404	223	senate	t	Lee M. Zeldin
405	224	senate	t	Malcolm A. Smith
406	225	senate	t	Michael H. Ranzenhofer
407	226	senate	t	Carl L Marcellino
408	227	senate	t	Greg Ball
409	228	senate	t	Andrew J Lanza
410	229	senate	t	Tom Libous
411	230	senate	t	George D. Maziarz
381	200	senate	t	Velmanette Montgomery
413	232	senate	t	John J. Bonacic
414	233	senate	t	Catharine Young
395	214	senate	t	David J. Valesky
416	235	senate	t	Kevin S. Parker
417	236	senate	t	Joseph E. Robach
372	191	senate	t	Neil D. Breslin
419	238	senate	t	Kenneth P. LaValle
378	197	senate	f	Shirley L. Huntley
382	201	senate	f	Eric Adams
386	205	senate	f	Owen H. Johnson
387	206	senate	f	Thomas K. Duane
388	207	senate	f	Suzi Oppenheimer
389	208	senate	f	Roy J. McDonald
392	211	senate	f	James S. Alesi
402	221	senate	f	Stephen M. Saland
420	239	senate	f	Carl Kruger
425	244	senate	f	Charles J. Fuschillo Jr.
441	263	senate	f	Thomas P. Morahan
442	264	senate	f	Pedro Espada Jr.
443	265	senate	f	George Onorato
444	266	senate	f	Eric T. Schneiderman
445	267	senate	f	Frank Padavan
446	268	senate	f	Craig M. Johnson
447	269	senate	f	Antoine M Thompson
448	270	senate	f	Brian X. Foley
449	271	senate	f	Vincent L. Leibell
450	272	senate	f	George Winner
451	273	senate	f	Darrel J. Aubertine
452	274	senate	f	Dale M. Volker
453	275	senate	f	William T. Stachowski
438	259	senate	t	Brad Hoylman
421	240	senate	t	Ruben Diaz
422	241	senate	t	Martin J. Golden
423	242	senate	t	Patty Ritchie
424	243	senate	t	Kemp Hannon
439	261	senate	t	Simcha Felder
427	246	senate	t	Patrick M. Gallivan
415	234	senate	t	Thomas F. O'Mara
430	249	senate	t	Dean G. Skelos
384	203	senate	t	Joseph P. Addabbo Jr.
428	247	senate	t	José Peralta
455	277	senate	f	David Storobin
591	417	assembly	f	Crystal Peoples-Stokes
592	418	assembly	f	Jane Corwin
593	419	assembly	f	Dennis H. Gabryszak
594	420	assembly	f	Sam Hoyt
595	421	assembly	f	Mark J.F. Schroeder
596	422	assembly	f	Jack Quinn III 
597	423	assembly	f	Daniel Burling
598	424	assembly	f	James Hayes
599	425	assembly	f	Joseph Giglio
600	426	assembly	f	William Parment 
601	227	assembly	f	Greg Ball
602	204	assembly	f	Adriano Espaillat
603	202	assembly	f	Michael Gianaris
604	255	assembly	f	George Latimer
605	427	assembly	f	Daniel P. Losquadro
606	428	assembly	f	Alfred C. Graf
607	429	assembly	f	Brian F. Curran
608	430	assembly	f	Edward Ra
609	431	assembly	f	Phillip Goldfeder
610	432	assembly	f	Edward Braunstein
611	433	assembly	f	Michael Simanowitz
612	434	assembly	f	Aravella Simotas
614	436	assembly	f	Rafael Espinal
635	457	assembly	f	Philip Palmesano
637	459	assembly	f	John Ceretto
638	460	assembly	f	Sean Ryan
639	461	assembly	f	Mickey Kearns
640	462	assembly	f	Kevin Smardz
641	463	assembly	f	Raymond Walter
642	464	assembly	f	Andrew Goodell
613	435	assembly	f	Francisco Moya
615	437	assembly	f	Nicole Malliotakis
616	438	assembly	f	Robert J. Rodriguez
617	439	assembly	f	Guillermo Linares
618	440	assembly	f	Dan Quart
619	441	assembly	f	Eric Stevenson
620	442	assembly	f	Mark Gjonaj
621	443	assembly	f	Sandy Galef
622	444	assembly	f	Thomas Abinanti
623	445	assembly	f	Shelley Mayer
624	446	assembly	f	Steve Katz
625	447	assembly	f	Didi Barrett
626	448	assembly	f	Steven McLaughlin
627	449	assembly	f	Claudia Tenney
628	450	assembly	f	Anthony Brindisi
629	451	assembly	f	Sam Roberts
630	452	assembly	f	Donald R. Miller
631	453	assembly	f	Kenneth Blankenbush
632	454	assembly	f	Sean T. Hanna
633	455	assembly	f	Harry B. Bronson
634	456	assembly	f	Mark C. Johns
636	458	assembly	f	Christopher Friend
470	254	assembly	f	Phil Boyle
643	247	assembly	f	José Peralta
644	465	assembly	f	Adam Bradley
645	466	assembly	f	Patricia Eddington
646	467	assembly	t	Keith L.T. Wright
647	468	assembly	f	Rob Walker
648	469	assembly	f	Anthony Seminerio
650	471	assembly	f	Ruben Diaz Jr.
651	472	assembly	t	Al Graf
652	473	assembly	f	Thomas Kirwan
653	475	assembly	f	Joseph Borelli
654	476	assembly	f	David Buchwald
655	477	assembly	f	Maritza Davila
656	478	assembly	f	David DiPietro
657	479	assembly	f	Patricia Fahy
658	480	assembly	f	Andrew R. Garbarino
659	481	assembly	f	Ron Kim
660	482	assembly	f	Kieran Michael Lalor
661	483	assembly	f	Chad A. Lupinacci
662	484	assembly	f	John T. McDonald III
663	485	assembly	f	Walter T. Mosley
664	486	assembly	f	Bill Nojay
665	487	assembly	f	Steven Otis
666	488	assembly	f	Anthony H. Palumbo
667	489	assembly	f	Victor M. Pichardo
668	490	assembly	f	Gabriela Rosa
669	491	assembly	f	Nily Rozic
670	492	assembly	f	Angelo Santabarbara
671	493	assembly	f	Luis R. Sepúlveda
672	494	assembly	f	James Skoufis
673	495	assembly	f	Michaelle C. Solages
674	496	assembly	f	Dan Stec
675	497	assembly	f	Phil Steck
\.


--
-- Name: member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('member_id_seq', 675, true);


--
-- Name: member_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('member_person_id_seq', 1, false);


--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY person (id, full_name, first_name, middle_name, last_name, email, prefix, suffix, verified) FROM stdin;
197	Shirley L. Huntley	Shirley	L.	Huntley	shuntley@nysenate.gov	Senator	\N	t
256	Kathleen A. Marchione	Kathleen	A.	Marchione	marchione@nysenate.gov	Senator	\N	t
257	Ted O'Brien	Ted	\N	O'Brien	obrien@nysenate.gov	Senator	\N	t
199	Ruth Hassell-Thompson	Ruth	\N	Hassell-Thompson	hassellt@senate.state.ny.us	Senator	\N	t
308	Michele Titus	Michele	\N	Titus	\N	Assembly Member	\N	t
189	David Carlucci	David	\N	Carlucci	carlucci@nysenate.gov	Senator	\N	t
204	Adriano Espaillat	Adriano	\N	Espaillat	espailla@nysenate.gov	Senator	\N	t
207	Suzi Oppenheimer	Suzi	\N	Oppenheimer	oppenhei@senate.state.ny.us	Senator	\N	t
214	David J. Valesky	David	J.	Valesky	valesky@senate.state.ny.us	Senator	\N	t
220	Liz Krueger	Liz	\N	Krueger	lkrueger@senate.state.ny.us	Senator	\N	t
250	Cecilia Tkaczyk	Cecilia	\N	Tkaczyk	tkaczyk@nysenate.gov	Senator	\N	t
259	Brad Hoylman	Brad	\N	Hoylman	hoylman@nysenate.gov	Senator	\N	t
270	Brian X. Foley	Brian	X.	Foley	bfoley@senate.state.ny.us	Senator	\N	t
241	Martin J. Golden	Martin	J.	Golden	golden@senate.state.ny.us	Senator	\N	t
227	Greg Ball	Greg	\N	Ball	gball@nysenate.gov	Senator	\N	t
229	Tom Libous	Tom	\N	Libous	senator@senatorlibous.com	Senator	\N	t
271	Vincent L. Leibell	Vincent	L.	Leibell	 leibell@senate.state.ny.us	Senator	\N	t
232	John J. Bonacic	John	J.	Bonacic	bonacic@nysenate.gov	Senator	\N	t
233	Catharine Young	Catharine	\N	Young	cyoung@senate.state.ny.us	Senator	\N	t
269	Antoine M Thompson	Antoine	\N	Thompson	athompso@senate.state.ny.us	Senator	\N	t
195	John A. DeFrancisco	John	A.	DeFrancisco	jdefranc@senate.state.ny.us	Senator	\N	t
219	Toby Ann Stavisky	Toby	\N	Stavisky	stavisky@senate.state.ny.us	Senator	\N	t
255	George Latimer	George	\N	Latimer	latimer@nysenate.gov	Senator	\N	t
242	Patty Ritchie	Patty	\N	Ritchie	ritchie@nysenate.gov	Senator	\N	t
309	Vivian Cook	Vivian	Cook	Cook	\N	Assembly Member	\N	t
202	Michael Gianaris	Michael	\N	Gianaris	gianaris@nysenate.gov	Senator	\N	t
251	James Sanders Jr.	James	\N	Sanders	sanders@nysenate.gov	Senator	Jr.	t
210	Tony Avella	Tony	\N	Avella	avella@nysenate.gov	Senator	\N	t
258	Terry Gipson	Terry	\N	Gipson	gipson@nysenate.gov	Senator	\N	t
239	Carl Kruger	Carl	\N	Kruger	kruger@senate.state.ny.us	Senator	\N	t
223	Lee M. Zeldin	Lee	M.	Zeldin	zeldin@nysenate.gov	Senator	\N	t
273	Darrel J. Aubertine	Darrel	J.	Aubertine	aubertin@senate.state.ny.us	Senator	\N	t
267	Frank Padavan	Frank	\N	Padavan	padavan@senate.state.ny.us	Senator	\N	t
228	Andrew J Lanza	Andrew	\N	Lanza	lanza@senate.state.ny.us	Senator	\N	t
235	Kevin S. Parker	Kevin	S.	Parker	parker@senate.state.ny.us	Senator	\N	t
254	Phil Boyle	Phil	\N	Boyle	pboyle@nysenate.gov	Senator	\N	t
191	Neil D. Breslin	Neil	D.	Breslin	breslin@senate.state.ny.us	Senator	\N	t
221	Stephen M. Saland	Stephen	M.	Saland	 saland@nysenate.gov	Senator	\N	t
198	Betty Little	Betty	\N	Little	little@senate.state.ny.us	Senator	\N	t
274	Dale M. Volker	Dale	M.	Volker	volker@senate.state.ny.us	Senator	\N	t
247	José Peralta	José	\N	Peralta	jperalta@nysenate.gov	Senator	\N	t
205	Owen H. Johnson	Owen	H.	Johnson	ojohnson@senate.state.ny.us	Senator	\N	t
206	Thomas K. Duane	Thomas	K.	Duane	duane@senate.state.ny.us	Senator	\N	t
310	Barbara Clark	Barbara	\N	Clark	\N	Assembly Member	\N	t
208	Roy J. McDonald	Roy	J.	McDonald	mcdonald@senate.state.ny.us	Senator	\N	t
211	James S. Alesi	James	S.	Alesi	alesi@senate.state.ny.us	Senator	\N	t
236	Joseph E. Robach	Joseph	E.	Robach	robach@nysenate.gov	Senator	\N	t
226	Carl L Marcellino	Carl	\N	Marcellino	marcelli@senate.state.ny.us	Senator	\N	t
196	Gustavo Rivera	Gustavo	\N	Rivera	grivera@nysenate.gov	Senator	\N	t
224	Malcolm A. Smith	Malcolm	A.	Smith	masmith@senate.state.ny.us 	Senator	\N	t
272	George Winner	George	\N	Winner	winner@senate.state.ny.us	Senator	\N	t
268	Craig M. Johnson	Craig	M.	Johnson	johnson@senate.state.ny.us	Senator	\N	t
263	Thomas P. Morahan	Thomas	P.	Morahan	district38@nysenate.gov	Senator	\N	t
190	James L. Seward	James	L.	Seward	seward@senate.state.ny.us	Senator	\N	t
243	Kemp Hannon	Kemp	\N	Hannon	hannon@nysenate.gov	Senator	\N	t
261	Simcha Felder	Simcha	\N	Felder	felder@nysenate.gov	Senator	\N	t
246	Patrick M. Gallivan	Patrick	M.	Gallivan	gallivan@nysenate.gov	Senator	\N	t
234	Thomas F. O'Mara	Thomas	F.	O'Mara	omara@nysenate.gov	Senator	\N	t
212	Joseph A. Griffo	Joseph	A.	Griffo	griffo@nysenate.gov	Senator	\N	t
225	Michael H. Ranzenhofer	Michael	H.	Ranzenhofer	ranz@senate.state.ny.us	Senator	\N	t
265	George Onorato	George	\N	Onorato	onorato@senate.state.ny.us	Senator	\N	t
311	Michael DenDekker	Michael	\N	DenDekker	\N	Assembly Member	\N	t
248	Timothy M. Kennedy	Timothy	M.	Kennedy	kennedy@nysenate.gov	Senator	\N	t
217	Mark Grisanti	Mark	\N	Grisanti	grisanti@nysenate.gov	Senator	\N	t
245	Martin Malavé Dilan	Martin	\N	Dilan	dilan@nysenate.gov	Senator	\N	t
275	William T. Stachowski	William	T.	Stachowski	stachows@senate.state.ny.us	Senator	\N	t
238	Kenneth P. LaValle	Kenneth	P.	LaValle	lavalle@nysenate.gov	Senator	\N	t
201	Eric Adams	Eric	\N	Adams	eadams@senate.state.ny.us	Senator	\N	t
216	Jack M. Martins	Jack	M.	Martins	martins@nysenate.gov	Senator	\N	t
194	Bill Perkins	Bill	\N	Perkins	perkins@senate.state.ny.us	Senator	\N	t
192	José M. Serrano	José	M.	Serrano	serrano@senate.state.ny.us	Senator	\N	t
312	Jeffrion Aubry	Jeffrion	\N	Aubry	\N	Assembly Member	\N	t
299	Grace Meng	Grace		Meng	\N	Assembly Member	\N	t
286	Andrew Raia	Andrew		Raia	\N	Assembly Member	\N	t
188	John L. Sampson	John	L.	Sampson	sampson@senate.state.ny.us	Senator	\N	t
200	Velmanette Montgomery	Velmanette	\N	Montgomery	montgome@senate.state.ny.us	Senator	\N	t
266	Eric T. Schneiderman	Eric	T.	Schneiderman	schneide@senate.state.ny.us	Senator	\N	t
209	Jeffrey D. Klein	Jeffrey	D.	Klein	jdklein@senate.state.ny.us	Senator	\N	t
213	Michael F. Nozzolio	Michael	F.	Nozzolio	nozzolio@senate.state.ny.us	Senator	\N	t
215	Andrea Stewart-Cousins	Andrea	\N	Stewart-Cousins	scousins@senate.state.ny.us	Senator	\N	t
218	John J. Flanagan	John	J.	Flanagan	flanagan@senate.state.ny.us	Senator	\N	t
222	Diane J. Savino	Diane	J.	Savino	savino@senate.state.ny.us	Senator	\N	t
230	George D. Maziarz	George	D.	Maziarz	maziarz@nysenate.gov	Senator	\N	t
231	Daniel L. Squadron	Daniel	L.	Squadron	squadron@nysenate.gov	Senator	\N	t
237	Hugh T. Farley	Hugh	T.	Farley	farley@senate.state.ny.us	Senator	\N	t
249	Dean G. Skelos	Dean	G.	Skelos	skelos@nysenate.gov	Senator	\N	t
193	William J. Larkin Jr.	William	J.	Larkin	larkin@senate.state.ny.us	Senator	Jr.	t
264	Pedro Espada Jr.	Pedro	\N	Espada	espada@senate.state.ny.us	Senator	Jr.	t
203	Joseph P. Addabbo Jr.	Joseph	P.	Addabbo	addabbo@senate.state.ny.us	Senator	Jr.	t
244	Charles J. Fuschillo Jr.	Charles	J.	Fuschillo	fuschill@senate.state.ny.us	Senator	Jr.	t
276	Hiram Monserrate	Hiram		Monserrate		Senator		t
277	David Storobin	David		Storobin		Senator		t
278	Marc Alessi 	Marc	\N	Alessi	\N	Assembly Member	\N	t
279	Fred Thiele	Fred	\N	Thiele	\N	Assembly Member	\N	t
280	L. Dean Murray	L.	Dean	Murray	\N	Assembly Member	\N	t
281	Steven Englebright	Steven	\N	Englebright	\N	Assembly Member	\N	t
282	Ginny Fields 	Ginny	\N	Fields	\N	Assembly Member	\N	t
283	Philip Ramos	Philip	\N	Ramos	\N	Assembly Member	\N	t
284	Michael J. Fitzpatrick	Michael	J.	Fitzpatrick	\N	Assembly Member	\N	t
285	Philip Boyle	Philip	\N	Boyle	\N	Assembly Member	\N	t
287	James Conte	James	\N	Conte	\N	Assembly Member	\N	t
288	Robert Sweeney	Robert	\N	Sweeney	\N	Assembly Member	\N	t
289	Joseph Saladino	Joseph	\N	Saladino	\N	Assembly Member	\N	t
290	Charles Lavine	Charles	\N	Lavine	\N	Assembly Member	\N	t
291	Robert Barra 	Robert	\N	Barra	\N	Assembly Member	\N	t
292	Michael Montesano	Michael	\N	Montesano	\N	Assembly Member	\N	t
293	Michelle Schimel	Michelle	\N	Schimel	\N	Assembly Member	\N	t
294	Thomas McKevitt	Thomas	\N	McKevitt	\N	Assembly Member	\N	t
295	Earlene Hooper	Earlene	\N	Hooper	\N	Assembly Member	\N	t
296	David McDonough	David	\N	McDonough	\N	Assembly Member	\N	t
297	Harvey Weisenberg	Harvey	\N	Weisenberg	\N	Assembly Member	\N	t
298	Thomas Alfano 	Thomas	\N	Alfano	\N	Assembly Member	\N	t
300	Audrey Pheffer	Audrey	\N	Pheffer	\N	Assembly Member	\N	t
301	David Weprin	David	\N	Weprin	\N	Assembly Member	\N	t
302	Rory Lancman	Rory	\N	Lancman	\N	Assembly Member	\N	t
303	Ann-Margaret Carrozza 	Ann-Margaret	\N	Carrozza	\N	Assembly Member	\N	t
304	Nettie Mayersohn	Nettie	\N	Mayersohn	\N	Assembly Member	\N	t
305	Andrew Hevesi	Andrew	\N	Hevesi	\N	Assembly Member	\N	t
306	William Scarborough	William	\N	Scarborough	\N	Assembly Member	\N	t
307	Margaret Markey	Margaret	\N	Markey	\N	Assembly Member	\N	t
314	Catherine Nolan	Catherine	\N	Nolan	\N	Assembly Member	\N	t
315	Michael G. Miller	Michael	G.	Miller	\N	Assembly Member	\N	t
316	Inez Barron	Inez	\N	Barron	\N	Assembly Member	\N	t
317	Helene Weinstein	Helene	\N	Weinstein	\N	Assembly Member	\N	t
318	Rhoda Jacobs	Rhoda	\N	Jacobs	\N	Assembly Member	\N	t
319	Karim Camara	Karim	\N	Camara	\N	Assembly Member	\N	t
320	James F. Brennan	James	F.	Brennan	\N	Assembly Member	\N	t
321	Steven Cymbrowitz	Steven	\N	Cymbrowitz	\N	Assembly Member	\N	t
322	Alec Brook-Krasny	Alec	\N	Brook-Krasny	\N	Assembly Member	\N	t
323	William Colton	William	\N	Colton	\N	Assembly Member	\N	t
324	Dov Hikind	Dov	\N	Hikind	\N	Assembly Member	\N	t
325	Peter Abbate	Peter	\N	Abbate	\N	Assembly Member	\N	t
326	Joseph Lentol	Joseph	\N	Lentol	\N	Assembly Member	\N	t
327	Felix Ortiz	Felix	\N	Ortiz	\N	Assembly Member	\N	t
328	Joan Millman	Joan	\N	Millman	\N	Assembly Member	\N	t
329	Vito Lopez	Vito	\N	Lopez	\N	Assembly Member	\N	t
330	Darryl Towns	Darryl	\N	Towns	\N	Assembly Member	\N	t
331	"William Boyland	"William	\N	Boyland	\N	Assembly Member	\N	t
332	Annette Robinson	Annette	\N	Robinson	\N	Assembly Member	\N	t
333	Hakeem Jeffries	Hakeem	\N	Jeffries	\N	Assembly Member	\N	t
334	N. Nick Perry	N.	Nick	Perry	\N	Assembly Member	\N	t
335	Alan Maisel	Alan	\N	Maisel	\N	Assembly Member	\N	t
336	Janele Hyer-Spencer 	Janele	\N	Hyer-Spencer	\N	Assembly Member	\N	t
337	Matthew Titone	Matthew	\N	Titone	\N	Assembly Member	\N	t
338	Louis Tobacco	Louis	\N	Tobacco	\N	Assembly Member	\N	t
339	Michael Cusick	Michael	\N	Cusick	\N	Assembly Member	\N	t
340	Sheldon Silver	Sheldon	\N	Silver	\N	Assembly Member	\N	t
341	Micah Kellner	Micah	\N	Kellner	\N	Assembly Member	\N	t
342	Deborah Glick	Deborah	\N	Glick	\N	Assembly Member	\N	t
343	Linda Rosenthal	Linda	\N	Rosenthal	\N	Assembly Member	\N	t
344	Adam Clayton Powell IV 	Adam	\N	Powell	\N	Assembly Member	IV	t
345	Daniel O'Donnell	Daniel	\N	O'Donnell	\N	Assembly Member	\N	t
346	Keith L. T. Wright	Keith	\N	T.	\N	Assembly Member	Wright	t
347	Herman D. Farrell	Herman	D.	Farrell	\N	Assembly Member	\N	t
349	Jonathan Bing	Jonathan	Bing	Bing	\N	Assembly Member	\N	t
350	Brian Kavanagh	Brian	\N	Kavanagh	\N	Assembly Member	\N	t
351	Richard Gottfried	Richard	\N	Gottfried	\N	Assembly Member	\N	t
352	Peter Rivera	Peter	\N	Rivera	\N	Assembly Member	\N	t
353	Vanessa Gibson	Vanessa	\N	Gibson	\N	Assembly Member	\N	t
354	Jose Rivera	Jose	\N	Rivera	\N	Assembly Member	\N	t
355	Michael Benjamin 	Michael	\N	Benjamin	\N	Assembly Member	\N	t
356	Naomi Rivera	Naomi	\N	Rivera	\N	Assembly Member	\N	t
357	Jeffrey Dinowitz	Jeffrey	\N	Dinowitz	\N	Assembly Member	\N	t
358	Michael Benedetto	Michael	\N	Benedetto	\N	Assembly Member	\N	t
359	Carl Heastie	Carl	\N	Heastie	\N	Assembly Member	\N	t
360	Carmen E. Arroyo	Carmen	E.	Arroyo	\N	Assembly Member	\N	t
361	Marcos Crespo	Marcos	\N	Crespo	\N	Assembly Member	\N	t
362	Nelson Castro	Nelson	\N	Castro	\N	Assembly Member	\N	t
363	J. Gary Pretlow	J.	Gary	Pretlow	\N	Assembly Member	\N	t
364	Amy Paulin	Amy	\N	Paulin	\N	Assembly Member	\N	t
365	Robert Castelli	Robert	\N	Castelli	\N	Assembly Member	\N	t
368	Richard Brodsky 	Richard	\N	Brodsky	\N	Assembly Member	\N	t
369	Mike Spano	Mike	\N	Spano	\N	Assembly Member	\N	t
370	"Kenneth Zebrowski	"Kenneth	\N	Zebrowski	\N	Assembly Member	\N	t
371	Ellen C. Jaffee	Ellen	C.	Jaffee	\N	Assembly Member	\N	t
372	Nancy Calhoun	Nancy	\N	Calhoun	\N	Assembly Member	\N	t
373	Ann Rabbitt	Ann	\N	Rabbitt	\N	Assembly Member	\N	t
374	Aileen Gunther	Aileen	\N	Gunther	\N	Assembly Member	\N	t
376	Frank Skartados 	Frank	\N	Skartados	\N	Assembly Member	\N	t
377	Kevin Cahill	Kevin	\N	Cahill	\N	Assembly Member	\N	t
378	Joel Miller	Joel	\N	Miller	\N	Assembly Member	\N	t
379	Marcus Molinaro	Marcus	\N	Molinaro	\N	Assembly Member	\N	t
380	John McEneny	John	\N	McEneny	\N	Assembly Member	\N	t
381	George Amedore	George	\N	Amedore	\N	Assembly Member	\N	t
382	Ronald Canestrari	Ronald	\N	Canestrari	\N	Assembly Member	\N	t
383	Clifford Crouch	Clifford	\N	Crouch	\N	Assembly Member	\N	t
384	Timothy P. Gordon 	Timothy	P.	Gordon	\N	Assembly Member	\N	t
385	Robert Reilly	Robert	\N	Reilly	\N	Assembly Member	\N	t
386	James Tedisco	James	\N	Tedisco	\N	Assembly Member	\N	t
387	William Magee	William	\N	Magee	\N	Assembly Member	\N	t
388	Tony Jordan	Tony	\N	Jordan	\N	Assembly Member	\N	t
389	Teresa Sayward	Teresa	\N	Sayward	\N	Assembly Member	\N	t
390	Janet Duprey	Janet	\N	Duprey	\N	Assembly Member	\N	t
391	David Townsend 	David	\N	Townsend	\N	Assembly Member	\N	t
392	RoAnn Destito	RoAnn	\N	Destito	\N	Assembly Member	\N	t
393	Marc Butler	Marc	\N	Butler	\N	Assembly Member	\N	t
394	Addie Jenne Russell	Addie	\N	Russell	\N	Assembly Member	\N	t
395	Joan Christensen 	Joan	\N	Christensen	\N	Assembly Member	\N	t
396	William Magnarelli	William	\N	Magnarelli	\N	Assembly Member	\N	t
397	"Albert A. Stirpe	"Albert	A.	Stirpe	\N	Assembly Member	\N	t
398	Dierdre Scozzafava 	Dierdre	\N	Scozzafava	\N	Assembly Member	\N	t
399	Gary Finch	Gary	\N	Finch	\N	Assembly Member	\N	t
400	William A. Barclay	William	A.	Barclay	\N	Assembly Member	\N	t
401	Barbara Lifton	Barbara	\N	Lifton	\N	Assembly Member	\N	t
402	Donna Lupardo	Donna	\N	Lupardo	\N	Assembly Member	\N	t
403	Peter Lopez	Peter	\N	Lopez	\N	Assembly Member	\N	t
404	Robert Oaks	Robert	Oaks	Oaks	\N	Assembly Member	\N	t
405	Brian Kolb	Brian	Kolb	Kolb	\N	Assembly Member	\N	t
406	Joseph Errigo 	Joseph	\N	Errigo	\N	Assembly Member	\N	t
407	Susan John 	Susan	John	John	\N	Assembly Member	\N	t
408	Joseph Morelle	Joseph	\N	Morelle	\N	Assembly Member	\N	t
409	David Gantt	David	\N	Gantt	\N	Assembly Member	\N	t
410	Bill Reilich	Bill	\N	Reilich	\N	Assembly Member	\N	t
411	David Koon 	David	Koon	Koon	\N	Assembly Member	\N	t
412	James Bacalles 	James	\N	Bacalles	\N	Assembly Member	\N	t
413	Tom O'Mara 	Tom	\N	O'Mara	\N	Assembly Member	\N	t
414	Francine DelMonte 	Francine	\N	DelMonte	\N	Assembly Member	\N	t
415	Stephen Hawley	Stephen	\N	Hawley	\N	Assembly Member	\N	t
416	Robin Schimminger	Robin	\N	Schimminger	\N	Assembly Member	\N	t
417	Crystal Peoples-Stokes	Crystal	\N	Peoples-Stokes	\N	Assembly Member	\N	t
418	Jane Corwin	Jane	\N	Corwin	\N	Assembly Member	\N	t
419	Dennis H. Gabryszak	Dennis	H.	Gabryszak	\N	Assembly Member	\N	t
420	Sam Hoyt	Sam	Hoyt	Hoyt	\N	Assembly Member	\N	t
421	Mark J.F. Schroeder	Mark	J.F.	Schroeder	\N	Assembly Member	\N	t
422	Jack Quinn III 	Jack	\N	Quinn	\N	Assembly Member	III	t
423	Daniel Burling	Daniel	\N	Burling	\N	Assembly Member	\N	t
424	James Hayes	James	\N	Hayes	\N	Assembly Member	\N	t
425	Joseph Giglio	Joseph	\N	Giglio	\N	Assembly Member	\N	t
426	William Parment 	William	\N	Parment	\N	Assembly Member	\N	t
427	Daniel P. Losquadro	Daniel	P.	Losquadro	\N	Assembly Member	\N	t
428	Alfred C. Graf	Alfred	C.	C.	\N	Assembly Member	\N	t
429	Brian F. Curran	Brian	F.	Curran	\N	Assembly Member	\N	t
430	Edward Ra	Edward	\N	Ra	\N	Assembly Member	\N	t
431	Phillip Goldfeder	Phillip	\N	Goldfeder	\N	Assembly Member	\N	t
432	Edward Braunstein	Edward	\N	Braunstein	\N	Assembly Member	\N	t
433	Michael Simanowitz	Michael	\N	Simanowitz	\N	Assembly Member	\N	t
434	Aravella Simotas	Aravella	\N	Simotas	\N	Assembly Member	\N	t
435	Francisco Moya	Francisco	\N	Moya	\N	Assembly Member	\N	t
436	Rafael Espinal	Rafael	\N	Espinal	\N	Assembly Member	\N	t
437	Nicole Malliotakis	Nicole	\N	Malliotakis	\N	Assembly Member	\N	t
438	Robert J. Rodriguez	Robert	J.	Rodriguez	\N	Assembly Member	\N	t
439	Guillermo Linares	Guillermo	\N	Linares	\N	Assembly Member	\N	t
440	Dan Quart	Dan	\N	Quart	\N	Assembly Member	\N	t
441	Eric Stevenson	Eric	\N	Stevenson	\N	Assembly Member	\N	t
442	Mark Gjonaj	Mark	\N	Gjonaj	\N	Assembly Member	\N	t
443	Sandy Galef	Sandy	\N	Galef	\N	Assembly Member	\N	t
444	Thomas Abinanti	Thomas	\N	Abinanti	\N	Assembly Member	\N	t
445	Shelley Mayer	Shelley	\N	Mayer	\N	Assembly Member	\N	t
446	Steve Katz	Steve	\N	Katz	\N	Assembly Member	\N	t
447	Didi Barrett	Didi	\N	Barrett	\N	Assembly Member	\N	t
448	Steven McLaughlin	Steven	\N	McLaughlin	\N	Assembly Member	\N	t
449	Claudia Tenney	Claudia	\N	Tenney	\N	Assembly Member	\N	t
450	Anthony Brindisi	Anthony	\N	Brindisi	\N	Assembly Member	\N	t
451	Sam Roberts	Sam	\N	Roberts	\N	Assembly Member	\N	t
452	Donald R. Miller	Donald	R.	Miller	\N	Assembly Member	\N	t
453	Kenneth Blankenbush	Kenneth	\N	Blankenbush	\N	Assembly Member	\N	t
454	Sean T. Hanna	Sean	T.	Hanna	\N	Assembly Member	\N	t
455	Harry B. Bronson	Harry	B.	Bronson	\N	Assembly Member	\N	t
456	Mark C. Johns	Mark	C.	Johns	\N	Assembly Member	\N	t
458	Christopher Friend	Christopher	\N	Friend	\N	Assembly Member	\N	t
457	Philip Palmesano	Philip	\N	Palmesano	\N	Assembly Member	\N	t
459	John Ceretto	John	\N	Ceretto	\N	Assembly Member	\N	t
460	Sean Ryan	Sean	\N	Ryan	\N	Assembly Member	\N	t
461	Mickey Kearns	Mickey	\N	Kearns	\N	Assembly Member	\N	t
462	Kevin Smardz	Kevin	\N	Smardz	\N	Assembly Member	\N	t
463	Raymond Walter	Raymond	\N	Walter	\N	Assembly Member	\N	t
464	Andrew Goodell	Andrew	\N	Goodell	\N	Assembly Member	\N	t
240	Ruben Diaz	Ruben	\N	Diaz	diaz@senate.state.ny.us	Senator	Sr	t
465	Adam Bradley	Adam		Bradley				t
466	Patricia Eddington	Patricia		Eddington				t
467	Keith L.T. Wright	Keith	L.T.	Wright				t
468	Rob Walker	Rob		Walker				t
469	Anthony Seminerio	Anthony		Seminerio				t
471	Ruben Diaz Jr.	Ruben		Diaz			Jr.	t
472	Al Graf	Al		Graf				t
473	Thomas Kirwan	Thomas		Kirwan				t
474	Joseph Borelli	Joseph		Borelli	\N	Assemblymember		t
475	Joseph Borelli	Joseph		Borelli	\N	Assemblymember		t
476	David Buchwald	David		Buchwald	\N	Assemblymember		t
477	Maritza Davila	Maritza		Davila	\N	Assemblymember		t
478	David DiPietro	David		DiPietro	\N	Assemblymember		t
479	Patricia Fahy	Patricia		Fahy	\N	Assemblymember		t
480	Andrew R. Garbarino	Andrew	R.	Garbarino	\N	Assemblymember		t
481	Ron Kim	Ron		Kim	\N	Assemblymember		t
482	Kieran Michael Lalor	Kieran	Michael	Lalor	\N	Assemblymember		t
483	Chad A. Lupinacci	Chad	A.	Lupinacci	\N	Assemblymember		t
484	John T. McDonald III	John	T.	McDonald	\N	Assemblymember	III	t
485	Walter T. Mosley	Walter	T.	Mosley	\N	Assemblymember		t
486	Bill Nojay	Bill		Nojay	\N	Assemblymember		t
487	Steven Otis	Steven		Otis	\N	Assemblymember		t
488	Anthony H. Palumbo	Anthony	H.	Palumbo	\N	Assemblymember		t
489	Victor M. Pichardo	Victor	M.	Pichardo	\N	Assemblymember		t
490	Gabriela Rosa	Gabriela		Rosa	\N	Assemblymember		t
491	Nily Rozic	Nily		Rozic	\N	Assemblymember		t
492	Angelo Santabarbara	Angelo		Santabarbara	\N	Assemblymember		t
493	Luis R. Sepúlveda	Luis	R.	Sepúlveda	\N	Assemblymember		t
494	James Skoufis	James		Skoufis	\N	Assemblymember		t
495	Michaelle C. Solages	Michaelle	C.	Solages	\N	Assemblymember		t
496	Dan Stec	Dan		Stec	\N	Assemblymember		t
497	Phil Steck	Phil		Steck	\N	Assemblymember		t
\.


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('person_id_seq', 497, true);


--
-- Data for Name: session_member; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY session_member (id, member_id, lbdc_short_name, session_year, district_code) FROM stdin;
1	369	SAMPSON	2009	19
2	441	MORAHAN	2009	38
3	371	SEWARD	2009	51
4	372	BRESLIN	2009	46
5	373	SERRANO	2009	28
6	374	LARKIN	2009	39
7	375	PERKINS	2009	30
8	376	DEFRANCISCO	2009	50
9	442	ESPADA	2009	33
10	378	HUNTLEY	2009	10
11	379	LITTLE	2009	45
12	380	HASSELL-THOMPSON	2009	36
13	381	MONTGOMERY	2009	18
14	382	ADAMS	2009	20
15	443	ONORATO	2009	12
16	384	ADDABBO	2009	15
17	444	SCHNEIDERMAN	2009	31
18	387	DUANE	2009	29
19	388	OPPENHEIMER	2009	37
20	389	MCDONALD	2009	43
21	390	KLEIN	2009	34
22	445	PADAVAN	2009	11
23	392	ALESI	2009	55
24	393	GRIFFO	2009	47
25	394	NOZZOLIO	2009	54
26	395	VALESKY	2009	49
27	396	STEWART-COUSINS	2009	35
28	447	THOMPSON	2009	60
29	399	FLANAGAN	2009	2
30	400	STAVISKY	2009	16
31	401	KRUEGER	2009	26
32	402	SALAND	2009	41
33	403	SAVINO	2009	23
34	448	FOLEY	2009	3
35	405	SMITH	2009	14
36	406	RANZENHOFER	2009	61
37	407	MARCELLINO	2009	5
38	449	LEIBELL	2009	40
39	409	LANZA	2009	24
40	410	LIBOUS	2009	52
41	411	MAZIARZ	2009	62
42	412	SQUADRON	2009	25
43	413	BONACIC	2009	42
44	414	YOUNG	2009	57
45	450	WINNER	2009	53
46	416	PARKER	2009	21
47	417	ROBACH	2009	56
48	418	FARLEY	2009	44
49	419	LAVALLE	2009	1
50	420	KRUGER	2009	27
51	421	DIAZ	2009	32
52	422	GOLDEN	2009	22
53	451	AUBERTINE	2009	48
54	424	HANNON	2009	6
55	425	FUSCHILLO	2009	8
56	426	DILAN	2009	17
57	452	VOLKER	2009	59
58	428	PERALTA	2009	13
59	453	STACHOWSKI	2009	58
60	430	SKELOS	2009	9
61	386	JOHNSON O	2009	4
62	446	JOHNSON C	2009	7
63	369	SAMPSON	2011	19
64	370	CARLUCCI	2011	38
65	371	SEWARD	2011	51
66	372	BRESLIN	2011	46
67	373	SERRANO	2011	28
68	374	LARKIN	2011	39
69	375	PERKINS	2011	30
70	376	DEFRANCISCO	2011	50
71	377	RIVERA	2011	33
72	378	HUNTLEY	2011	10
73	379	LITTLE	2011	45
74	380	HASSELL-THOMPSON	2011	36
75	381	MONTGOMERY	2011	18
76	382	ADAMS	2011	20
77	383	GIANARIS	2011	12
78	384	ADDABBO	2011	15
79	385	ESPAILLAT	2011	31
80	386	JOHNSON	2011	4
81	387	DUANE	2011	29
82	388	OPPENHEIMER	2011	37
83	389	MCDONALD	2011	43
84	390	KLEIN	2011	34
85	391	AVELLA	2011	11
86	392	ALESI	2011	55
87	393	GRIFFO	2011	47
88	394	NOZZOLIO	2011	54
89	395	VALESKY	2011	49
90	396	STEWART-COUSINS	2011	35
91	397	MARTINS	2011	7
92	398	GRISANTI	2011	60
93	399	FLANAGAN	2011	2
94	400	STAVISKY	2011	16
95	401	KRUEGER	2011	26
96	402	SALAND	2011	41
97	403	SAVINO	2011	23
98	404	ZELDIN	2011	3
99	405	SMITH	2011	14
100	406	RANZENHOFER	2011	61
101	407	MARCELLINO	2011	5
102	408	BALL	2011	40
103	409	LANZA	2011	24
104	410	LIBOUS	2011	52
105	411	MAZIARZ	2011	62
106	412	SQUADRON	2011	25
107	413	BONACIC	2011	42
108	414	YOUNG	2011	57
109	415	O'MARA	2011	53
110	416	PARKER	2011	21
111	417	ROBACH	2011	56
112	418	FARLEY	2011	44
113	419	LAVALLE	2011	1
114	420	KRUGER	2011	27
115	421	DIAZ	2011	32
116	422	GOLDEN	2011	22
117	423	RITCHIE	2011	48
118	424	HANNON	2011	6
119	425	FUSCHILLO	2011	8
120	426	DILAN	2011	17
121	427	GALLIVAN	2011	59
122	428	PERALTA	2011	13
123	429	KENNEDY	2011	58
124	430	SKELOS	2011	9
125	369	SAMPSON	2013	19
126	370	CARLUCCI	2013	38
127	371	SEWARD	2013	51
128	431	TKACZYK	2013	46
129	401	KRUEGER	2013	28
130	374	LARKIN	2013	39
131	375	PERKINS	2013	30
132	376	DEFRANCISCO	2013	50
133	377	RIVERA	2013	33
134	432	SANDERS	2013	10
135	379	LITTLE	2013	45
136	380	HASSELL-THOMPSON	2013	36
137	426	DILAN	2013	18
138	383	GIANARIS	2013	12
139	384	ADDABBO	2013	15
140	385	ESPAILLAT	2013	31
141	433	BOYLE	2013	4
142	373	SERRANO	2013	29
143	434	LATIMER	2013	37
144	435	MARCHIONE	2013	43
145	390	KLEIN	2013	34
146	429	KENNEDY	2013	63
147	391	AVELLA	2013	11
148	436	O'BRIEN	2013	55
149	393	GRIFFO	2013	47
150	394	NOZZOLIO	2013	54
151	418	FARLEY	2013	49
152	396	STEWART-COUSINS	2013	35
153	397	MARTINS	2013	7
154	398	GRISANTI	2013	60
155	399	FLANAGAN	2013	2
156	400	STAVISKY	2013	16
157	412	SQUADRON	2013	26
158	437	GIPSON	2013	41
159	403	SAVINO	2013	23
160	404	ZELDIN	2013	3
161	405	SMITH	2013	14
162	406	RANZENHOFER	2013	61
163	407	MARCELLINO	2013	5
164	408	BALL	2013	40
165	409	LANZA	2013	24
166	410	LIBOUS	2013	52
167	411	MAZIARZ	2013	62
168	381	MONTGOMERY	2013	25
169	413	BONACIC	2013	42
170	414	YOUNG	2013	57
171	395	VALESKY	2013	53
172	416	PARKER	2013	21
173	417	ROBACH	2013	56
174	372	BRESLIN	2013	44
175	419	LAVALLE	2013	1
176	438	HOYLMAN	2013	27
177	421	DIAZ	2013	32
178	422	GOLDEN	2013	22
179	423	RITCHIE	2013	48
180	424	HANNON	2013	6
181	439	FELDER	2013	17
182	427	GALLIVAN	2013	59
183	415	O'MARA	2013	58
184	430	SKELOS	2013	9
185	425	FUSCHILLO	2013	8
186	382	ADAMS	2013	20
187	428	PERALTA	2013	13
188	454	MONSERRATE	2009	13
189	455	STOROBIN	2011	27
190	463	ALESSI	2009	1
191	536	ARROYO	2009	84
192	518	KELLNER	2009	65
193	508	BOYLAND	2009	55
194	603	GIANARIS	2009	36
195	543	BRODSKY	2009	92
196	571	STIRPE	2009	121
197	479	HOOPER	2009	18
198	544	SPANO	2009	93
199	495	JACOBS	2009	42
200	553	MOLINARO	2009	103
201	513	HYER-SPENCER	2009	60
202	574	BARCLAY	2009	124
203	511	PERRY	2009	58
204	537	CRESPO	2009	85
205	510	JEFFRIES	2009	57
206	599	GIGLIO	2009	149
207	456	TITUS	2009	31
211	459	DENDEKKER	2009	34
212	520	ROSENTHAL	2009	67
213	477	SCHIMEL	2009	16
214	526	KAVANAGH	2009	74
215	473	SALADINO	2009	12
216	481	WEISENBERG	2009	20
217	535	HEASTIE	2009	83
218	516	CUSICK	2009	63
219	569	CHRISTENSEN	2009	119
220	525	BING	2009	73
221	579	KOLB	2009	129
222	533	DINOWITZ	2009	81
223	472	SWEENEY	2009	11
224	524	FARRELL	2009	71
225	457	COOK	2009	32
226	484	WEPRIN	2009	24
230	517	SILVER	2009	64
231	559	REILLY	2009	109
232	512	MAISEL	2009	59
233	499	BROOK-KRASNY	2009	46
234	578	OAKS	2009	128
235	471	CONTE	2009	10
236	505	MILLMAN	2009	52
237	500	COLTON	2009	47
238	507	TOWNS	2009	54
239	469	FITZPATRICK	2009	7
241	519	GLICK	2009	66
242	501	HIKIND	2009	48
243	458	CLARK	2009	33
244	589	HAWLEY	2009	139
245	540	PAULIN	2009	88
246	550	SKARTADOS	2009	100
247	573	FINCH	2009	123
248	487	MAYERSOHN	2009	27
249	521	POWELL	2009	68
250	482	ALFANO	2009	21
251	465	MURRAY	2009	3
252	555	AMEDORE	2009	105
253	467	FIELDS	2009	5
255	462	RAIA	2009	9
256	460	AUBRY	2009	35
257	597	BURLING	2009	147
258	593	GABRYSZAK	2009	143
259	529	GIBSON	2009	77
260	556	CANESTRARI	2009	106
263	489	SCARBOROUGH	2009	29
264	575	LIFTON	2009	125
265	567	BUTLER	2009	117
266	480	MCDONOUGH	2009	19
267	587	O'MARA	2009	137
268	590	SCHIMMINGER	2009	140
269	478	MCKEVITT	2009	17
270	538	CASTRO	2009	86
271	565	TOWNSEND	2009	115
272	490	MARKEY	2009	30
273	464	THIELE	2009	2
274	497	BRENNAN	2009	44
275	581	JOHN	2009	131
276	483	PHEFFER	2009	23
277	596	QUINN	2009	146
278	585	KOON	2009	135
279	583	GANTT	2009	133
280	534	BENEDETTO	2009	82
281	588	DELMONTE	2009	138
282	562	JORDAN	2009	112
283	547	CALHOUN	2009	96
284	563	SAYWARD	2009	113
285	592	CORWIN	2009	142
286	584	REILICH	2009	134
287	461	MENG	2009	22
288	498	CYMBROWITZ	2009	45
289	493	BARRON	2009	40
290	601	BALL	2009	99
291	514	TITONE	2009	61
292	576	LUPARDO	2009	126
293	595	SCHROEDER	2009	145
295	548	RABBITT	2009	97
296	503	LENTOL	2009	50
297	474	LAVINE	2009	13
298	509	ROBINSON	2009	56
299	486	CARROZZA	2009	26
300	568	RUSSELL	2009	118
301	539	PRETLOW	2009	87
302	604	LATIMER	2009	91
303	522	O'DONNELL	2009	69
304	580	ERRIGO	2009	130
305	488	HEVESI	2009	28
306	591	PEOPLES-STOKES	2009	141
307	570	MAGNARELLI	2009	120
308	475	BARRA	2009	14
309	560	TEDISCO	2009	110
310	476	MONTESANO	2009	15
311	485	LANCMAN	2009	25
312	602	ESPAILLAT	2009	72
262	577	LOPEZ P	2009	127
227	492	MILLER M	2009	38
228	552	MILLER J	2009	102
208	528	RIVERA P	2009	76
209	530	RIVERA J	2009	78
210	532	RIVERA N	2009	80
240	644	BRADLEY	2009	89
254	645	EDDINGTON	2009	3
294	646	WRIGHT	2009	70
313	582	MORELLE	2009	132
315	496	CAMARA	2009	43
317	549	GUNTHER	2009	98
319	515	TOBACCO	2009	62
321	527	GOTTFRIED	2009	75
323	466	ENGLEBRIGHT	2009	4
325	557	CROUCH	2009	107
327	470	BOYLE	2009	8
329	600	PARMENT	2009	150
331	504	ORTIZ	2009	51
333	494	WEINSTEIN	2009	41
335	531	BENJAMIN	2009	79
338	566	DESTITO	2009	116
339	468	RAMOS	2009	6
340	586	BACALLES	2009	136
342	554	MCENENY	2009	104
344	594	HOYT	2009	144
316	551	CAHILL	2009	101
318	546	JAFFEE	2009	95
320	545	ZEBROWSKI	2009	94
322	541	CASTELLI	2009	89
324	491	NOLAN	2009	37
326	564	DUPREY	2009	114
328	558	GORDON	2009	108
330	572	SCOZZAFAVA	2009	122
337	621	GALEF	2009	90
341	502	ABBATE	2009	49
343	598	HAYES	2009	148
345	561	MAGEE	2009	111
261	506	LOPEZ V	2009	53
332	643	PERALTA	2009	39
334	648	SEMINERIO	2009	38
314	647	WALKER	2009	15
346	650	DIAZ	2009	85
347	536	ARROYO	2011	84
348	518	KELLNER	2011	65
349	508	BOYLAND	2011	55
350	625	BARRETT	2011	103
351	627	TENNEY	2011	115
352	642	GOODELL	2011	150
353	479	HOOPER	2011	18
355	495	JACOBS	2011	42
357	632	HANNA	2011	130
358	574	BARCLAY	2011	124
359	537	CRESPO	2011	85
360	510	JEFFRIES	2011	57
361	511	PERRY	2011	58
362	599	GIGLIO	2011	149
363	456	TITUS	2011	31
364	607	CURRAN	2011	14
365	605	LOSQUADRO	2011	1
366	459	DENDEKKER	2011	34
367	526	KAVANAGH	2011	74
368	477	SCHIMEL	2011	16
369	520	ROSENTHAL	2011	67
370	473	SALADINO	2011	12
371	481	WEISENBERG	2011	20
372	535	HEASTIE	2011	83
373	516	CUSICK	2011	63
374	626	MCLAUGHLIN	2011	108
376	635	PALMESANO	2011	136
377	579	KOLB	2011	129
378	638	RYAN	2011	144
379	533	DINOWITZ	2011	81
380	472	SWEENEY	2011	11
381	524	FARRELL	2011	71
382	457	COOK	2011	32
383	517	SILVER	2011	64
384	484	WEPRIN	2011	24
385	628	BRINDISI	2011	116
386	559	REILLY	2011	109
387	512	MAISEL	2011	59
388	499	BROOK-KRASNY	2011	46
389	614	ESPINAL	2011	54
390	613	MOYA	2011	39
391	578	OAKS	2011	128
392	471	CONTE	2011	10
393	505	MILLMAN	2011	52
394	500	COLTON	2011	47
396	469	FITZPATRICK	2011	7
397	616	RODRIGUEZ	2011	68
398	519	GLICK	2011	66
399	501	HIKIND	2011	48
400	458	CLARK	2011	33
401	550	SKARTADOS	2011	100
402	633	BRONSON	2011	131
403	540	PAULIN	2011	88
404	589	HAWLEY	2011	139
405	573	FINCH	2011	123
407	637	CERETTO	2011	138
408	631	BLANKENBUSH	2011	122
409	641	WALTER	2011	148
410	465	MURRAY	2011	3
411	555	AMEDORE	2011	105
412	624	KATZ	2011	99
413	462	RAIA	2011	9
414	636	FRIEND	2011	137
415	460	AUBRY	2011	35
416	617	LINARES	2011	72
417	610	BRAUNSTEIN	2011	26
418	597	BURLING	2011	147
419	529	GIBSON	2011	77
420	593	GABRYSZAK	2011	143
421	609	GOLDFEDER	2011	23
422	556	CANESTRARI	2011	106
423	575	LIFTON	2011	125
424	489	SCARBOROUGH	2011	29
425	612	SIMOTAS	2011	36
426	567	BUTLER	2011	117
427	480	MCDONOUGH	2011	19
428	590	SCHIMMINGER	2011	140
429	478	MCKEVITT	2011	17
430	538	CASTRO	2011	86
431	618	QUART	2011	73
432	639	KEARNS	2011	145
433	490	MARKEY	2011	30
434	464	THIELE	2011	2
435	623	MAYER	2011	93
436	497	BRENNAN	2011	44
438	583	GANTT	2011	133
439	534	BENEDETTO	2011	82
440	562	JORDAN	2011	112
441	563	SAYWARD	2011	113
442	547	CALHOUN	2011	96
443	592	CORWIN	2011	142
444	584	REILICH	2011	134
445	461	MENG	2011	22
446	493	BARRON	2011	40
447	498	CYMBROWITZ	2011	45
448	514	TITONE	2011	61
449	576	LUPARDO	2011	126
452	634	JOHNS	2011	135
453	503	LENTOL	2011	50
454	548	RABBITT	2011	97
455	474	LAVINE	2011	13
456	509	ROBINSON	2011	56
457	629	ROBERTS	2011	119
458	615	MALLIOTAKIS	2011	60
459	568	RUSSELL	2011	118
460	539	PRETLOW	2011	87
461	604	LATIMER	2011	91
462	622	ABINANTI	2011	92
463	522	O'DONNELL	2011	69
464	488	HEVESI	2011	28
465	591	PEOPLES-STOKES	2011	141
466	570	MAGNARELLI	2011	120
467	560	TEDISCO	2011	110
468	485	LANCMAN	2011	25
469	476	MONTESANO	2011	15
470	582	MORELLE	2011	132
471	496	CAMARA	2011	43
472	551	CAHILL	2011	101
473	549	GUNTHER	2011	98
474	546	JAFFEE	2011	95
475	515	TOBACCO	2011	62
476	527	GOTTFRIED	2011	75
477	545	ZEBROWSKI	2011	94
478	466	ENGLEBRIGHT	2011	4
479	541	CASTELLI	2011	89
480	491	NOLAN	2011	37
481	564	DUPREY	2011	114
482	557	CROUCH	2011	107
375	525	BING	2011	73
450	646	WRIGHT	2011	70
395	507	TOWNS	2011	54
406	487	MAYERSOHN	2011	27
356	553	MOLINARO	2011	103
437	483	PHEFFER	2011	23
451	595	SCHROEDER	2011	145
354	544	SPANO	2011	93
483	470	BOYLE	2011	8
484	611	SIMANOWITZ	2011	27
485	504	ORTIZ	2011	51
486	640	SMARDZ	2011	146
487	494	WEINSTEIN	2011	41
489	621	GALEF	2011	90
490	468	RAMOS	2011	6
491	619	STEVENSON	2011	79
492	502	ABBATE	2011	49
494	554	MCENENY	2011	104
496	561	MAGEE	2011	111
497	608	RA	2011	21
498	506	LOPEZ V	2011	53
499	577	LOPEZ P	2011	127
500	492	MILLER M	2011	38
501	630	MILLER D	2011	121
502	552	MILLER J	2011	102
503	528	RIVERA P	2011	76
504	532	RIVERA N	2011	80
505	530	RIVERA J	2011	78
506	652	KIRWAN	2011	100
488	566	DESTITO	2011	116
493	598	HAYES	2011	148
495	594	HOYT	2011	144
507	528	RIVERA	2013	76
508	492	MILLER	2013	38
509	502	ABBATE	2013	49
510	622	ABINANTI	2013	92
511	536	ARROYO	2013	84
512	460	AUBRY	2013	35
513	574	BARCLAY	2013	124
514	625	BARRETT	2013	103
515	493	BARRON	2013	40
516	534	BENEDETTO	2013	82
517	631	BLANKENBUSH	2013	122
518	508	BOYLAND	2013	55
519	610	BRAUNSTEIN	2013	26
520	497	BRENNAN	2013	44
521	628	BRINDISI	2013	116
522	633	BRONSON	2013	131
523	499	BROOK-KRASNY	2013	46
524	567	BUTLER	2013	117
525	551	CAHILL	2013	101
526	496	CAMARA	2013	43
527	538	CASTRO	2013	86
528	637	CERETTO	2013	138
529	458	CLARK	2013	33
530	500	COLTON	2013	47
531	457	COOK	2013	32
532	592	CORWIN	2013	142
533	537	CRESPO	2013	85
534	557	CROUCH	2013	107
535	607	CURRAN	2013	14
536	516	CUSICK	2013	63
537	498	CYMBROWITZ	2013	45
538	459	DENDEKKER	2013	34
539	533	DINOWITZ	2013	81
540	564	DUPREY	2013	114
541	466	ENGLEBRIGHT	2013	4
542	614	ESPINAL	2013	54
543	524	FARRELL	2013	71
544	573	FINCH	2013	123
545	469	FITZPATRICK	2013	7
546	636	FRIEND	2013	137
547	593	GABRYSZAK	2013	143
548	621	GALEF	2013	90
549	583	GANTT	2013	133
550	529	GIBSON	2013	77
551	599	GIGLIO	2013	149
552	620	GJONAJ	2013	80
553	519	GLICK	2013	66
554	609	GOLDFEDER	2013	23
555	642	GOODELL	2013	150
556	527	GOTTFRIED	2013	75
558	549	GUNTHER	2013	98
559	589	HAWLEY	2013	139
560	535	HEASTIE	2013	83
561	488	HEVESI	2013	28
562	501	HIKIND	2013	48
563	479	HOOPER	2013	18
564	495	JACOBS	2013	42
565	546	JAFFEE	2013	95
566	634	JOHNS	2013	135
567	562	JORDAN	2013	112
568	624	KATZ	2013	99
569	526	KAVANAGH	2013	74
570	639	KEARNS	2013	145
571	518	KELLNER	2013	65
572	579	KOLB	2013	129
573	474	LAVINE	2013	13
574	503	LENTOL	2013	50
575	575	LIFTON	2013	125
576	605	LOSQUADRO	2013	1
577	576	LUPARDO	2013	126
578	561	MAGEE	2013	111
579	570	MAGNARELLI	2013	120
580	512	MAISEL	2013	59
581	615	MALLIOTAKIS	2013	60
582	490	MARKEY	2013	30
583	623	MAYER	2013	93
584	480	MCDONOUGH	2013	19
585	478	MCKEVITT	2013	17
586	626	MCLAUGHLIN	2013	108
587	505	MILLMAN	2013	52
588	476	MONTESANO	2013	15
589	582	MORELLE	2013	132
590	613	MOYA	2013	39
591	491	NOLAN	2013	37
592	522	O'DONNELL	2013	69
593	578	OAKS	2013	128
594	504	ORTIZ	2013	51
595	635	PALMESANO	2013	136
596	540	PAULIN	2013	88
597	591	PEOPLES-STOKES	2013	141
598	511	PERRY	2013	58
599	539	PRETLOW	2013	87
600	618	QUART	2013	73
601	608	RA	2013	21
602	548	RABBITT	2013	97
603	462	RAIA	2013	9
604	468	RAMOS	2013	6
605	584	REILICH	2013	134
606	629	ROBERTS	2013	119
607	509	ROBINSON	2013	56
608	616	RODRIGUEZ	2013	68
609	520	ROSENTHAL	2013	67
610	568	RUSSELL	2013	118
611	638	RYAN	2013	144
612	473	SALADINO	2013	12
613	489	SCARBOROUGH	2013	29
614	477	SCHIMEL	2013	16
615	590	SCHIMMINGER	2013	140
616	517	SILVER	2013	64
617	611	SIMANOWITZ	2013	27
618	612	SIMOTAS	2013	36
619	550	SKARTADOS	2013	100
620	619	STEVENSON	2013	79
622	472	SWEENEY	2013	11
623	560	TEDISCO	2013	110
624	627	TENNEY	2013	115
625	464	THIELE	2013	2
626	514	TITONE	2013	61
627	456	TITUS	2013	31
628	641	WALTER	2013	148
629	494	WEINSTEIN	2013	41
630	481	WEISENBERG	2013	20
631	484	WEPRIN	2013	24
633	545	ZEBROWSKI	2013	94
634	653	BORELLI	2013	62
635	654	BUCHWALD	2013	93
636	655	DAVILA	2013	53
637	656	DIPIETRO	2013	147
638	657	FAHY	2013	109
639	658	GARBARINO	2013	7
621	571	STIRPE	2013	127
641	660	LALOR	2013	105
642	661	LUPINACCI	2013	10
643	662	MCDONALD	2013	108
644	663	MOSLEY	2013	57
645	664	NOJAY	2013	133
646	665	OTIS	2013	91
647	666	PALUMBO	2013	2
648	667	PICHARDO	2013	86
649	668	ROSA	2013	72
650	669	ROZIC	2013	25
651	670	SANTABARBARA	2013	111
652	671	SEPULVEDA	2013	87
653	672	SKOUFIS	2013	99
654	673	SOLAGES	2013	22
655	674	STEC	2013	114
656	675	STECK	2013	110
640	659	KIM	2013	40
632	646	WRIGHT	2013	70
557	651	GRAF	2013	5
657	506	LOPEZ V	2013	53
658	577	LOPEZ P	2013	127
\.


--
-- Name: session_member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('session_member_id_seq', 658, true);


--
-- Data for Name: test_fts_billtext; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY test_fts_billtext (amendment_letter, full_text, print_no, full_text_vector) FROM stdin;
\.


--
-- Data for Name: test_hstore; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY test_hstore (id, store) FROM stdin;
\.


--
-- Name: test_hstore_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('test_hstore_id_seq', 7992, true);


--
-- Data for Name: test_trigger; Type: TABLE DATA; Schema: public; Owner: ash
--

COPY test_trigger (id, text) FROM stdin;
1	muwhahahahha
\.


--
-- Name: test_trigger_id_seq; Type: SEQUENCE SET; Schema: public; Owner: ash
--

SELECT pg_catalog.setval('test_trigger_id_seq', 1, true);


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


--
-- Name: member_pkey; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace: 
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_pkey PRIMARY KEY (id);


--
-- Name: person_pkey; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace: 
--

ALTER TABLE ONLY person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: session_member_member_id_lbdc_short_name_session_year_key; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace: 
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_member_id_lbdc_short_name_session_year_key UNIQUE (member_id, lbdc_short_name, session_year);


--
-- Name: session_member_pkey; Type: CONSTRAINT; Schema: public; Owner: ash; Tablespace: 
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_pkey PRIMARY KEY (id);


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


--
-- Name: insert_muwahaha; Type: TRIGGER; Schema: public; Owner: ash
--

CREATE TRIGGER insert_muwahaha BEFORE INSERT OR UPDATE ON test_trigger FOR EACH ROW EXECUTE PROCEDURE write_muwhahah();


--
-- Name: member_person_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ash
--

ALTER TABLE ONLY member
    ADD CONSTRAINT member_person_id_fkey FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: session_member_member_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ash
--

ALTER TABLE ONLY session_member
    ADD CONSTRAINT session_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES member(id) ON UPDATE CASCADE ON DELETE CASCADE;


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

