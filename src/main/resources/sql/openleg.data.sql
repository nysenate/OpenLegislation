--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.5
-- Dumped by pg_dump version 9.6.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, master, pg_catalog;

--
-- Data for Name: bill_sponsor_additional; Type: TABLE DATA; Schema: master; Owner: postgres
--

COPY bill_sponsor_additional (bill_print_no, bill_session_year, session_member_id, sequence_no, created_date_time, last_fragment_id) FROM stdin;
R314	2013	145	1	2015-03-03 16:30:28.199822	\N
J375	2013	184	1	2015-03-03 16:30:28.210309	\N
J375	2013	145	2	2015-03-03 16:30:28.21162	\N
R633	2013	145	1	2015-03-03 16:30:28.212107	\N
J694	2013	145	1	2015-03-03 16:30:28.21296	\N
J758	2013	184	1	2015-03-03 16:30:28.214068	\N
R818	2013	145	1	2015-03-03 16:30:28.21498	\N
J844	2013	145	1	2015-03-03 16:30:28.215516	\N
J860	2013	184	1	2015-03-03 16:30:28.216512	\N
J1608	2013	145	1	2015-03-03 16:30:28.21752	\N
J1608	2013	152	2	2015-03-03 16:30:28.219962	\N
J1938	2013	145	1	2015-03-03 16:30:28.221426	\N
J1938	2013	152	2	2015-03-03 16:30:28.222308	\N
J3100	2013	180	1	2015-03-03 16:30:28.222815	\N
S2107	2013	145	1	2015-03-03 16:30:28.223623	\N
S3953	2013	140	1	2015-03-03 16:30:28.224692	\N
S5441	2013	154	1	2015-03-03 16:30:28.225734	\N
S5441	2013	162	2	2015-03-03 16:30:28.226764	\N
S5441	2013	182	3	2015-03-03 16:30:28.227291	\N
S5656	2013	185	1	2015-03-03 16:30:28.228377	\N
S5657	2013	144	1	2015-03-03 16:30:28.229151	\N
S5657	2013	126	2	2015-03-03 16:30:28.230516	\N
S5683	2013	171	1	2015-03-03 16:30:28.231299	\N
J2885	2013	145	1	2015-03-03 16:30:28.232144	\N
J2885	2013	152	2	2015-03-03 16:30:28.23284	\N
J3307	2013	145	1	2015-03-03 16:30:28.233828	\N
J3307	2013	184	2	2015-03-03 16:30:28.234813	\N
J3743	2013	145	1	2015-03-03 16:30:28.235557	\N
J3743	2013	152	2	2015-03-03 16:30:28.236867	\N
J3908	2013	145	1	2015-03-03 16:30:28.237699	\N
J3908	2013	152	2	2015-03-03 16:30:28.238727	\N
R4036	2013	145	1	2015-03-03 16:30:28.239476	\N
S6966	2013	149	1	2015-03-03 16:30:28.240254	\N
J4904	2013	145	1	2015-03-03 16:30:28.241537	\N
J4904	2013	152	2	2015-03-03 16:30:28.242505	\N
J5165	2013	145	1	2015-03-03 16:30:28.243363	\N
\.


--
-- Data for Name: bill_text_alternate_pdf; Type: TABLE DATA; Schema: master; Owner: postgres
--
COPY bill_text_alternate_pdf (bill_session_year, bill_print_no, bill_amend_version, active, url_path) FROM stdin;
2015	S6400		t	/static/pdf/2015-S6400-A9000.pdf
2015	A9000		t	/static/pdf/2015-S6400-A9000.pdf
2015	S6401		t	/static/pdf/2015-S6401-A9001.pdf
2015	A9001		t	/static/pdf/2015-S6401-A9001.pdf
2015	S6402		t	/static/pdf/2015-S6402-A9002.pdf
2015	A9002		t	/static/pdf/2015-S6402-A9002.pdf
2015	S6403		t	/static/pdf/2015-S6403-A9003.pdf
2015	A9003		t	/static/pdf/2015-S6403-A9003.pdf
2015	S6404		t	/static/pdf/2015-S6404-A9004.pdf
2015	A9004		t	/static/pdf/2015-S6404-A9004.pdf
2017	A7067		t	/static/pdf/2017-A7067.pdf
2017	A7068		t	/static/pdf/2017-A7068.pdf
2017	S5491		t	/static/pdf/2017-S5491.pdf
2017	S5492		t	/static/pdf/2017-S5492.pdf
2017	S2000		t	/static/pdf/2017-S2000-A3000.pdf
2017	A3000		t	/static/pdf/2017-S2000-A3000.pdf
2017	S2001		t	/static/pdf/2017-S2001-A3001.pdf
2017	A3001		t	/static/pdf/2017-S2001-A3001.pdf
2017	S2002		t	/static/pdf/2017-S2002-A3002.pdf
2017	A3002		t	/static/pdf/2017-S2002-A3002.pdf
2017	S2003		t	/static/pdf/2017-S2003-A3003.pdf
2017	A3003		t	/static/pdf/2017-S2003-A3003.pdf
2017	S2004		t	/static/pdf/2017-S2004-A3004.pdf
2017	A3004		t	/static/pdf/2017-S2004-A3004.pdf
2017	S7500		t	/static/pdf/2017-S7500-A9500.pdf
2017	S9500		t	/static/pdf/2017-S7500-A9500.pdf
2017	S7501		t	/static/pdf/2017-S7501-A9501.pdf
2017	S9501		t	/static/pdf/2017-S7501-A9501.pdf
2017	S7502		t	/static/pdf/2017-S7502-A9502.pdf
2017	S9502		t	/static/pdf/2017-S7502-A9502.pdf
2017	S7503		t	/static/pdf/2017-S7503-A9503.pdf
2017	S9503		t	/static/pdf/2017-S7503-A9503.pdf
2017	S7504		t	/static/pdf/2017-S7504-A9504.pdf
2017	S9504		t	/static/pdf/2017-S7504-A9504.pdf
\.


--
-- Data for Name: committee; Type: TABLE DATA; Schema: master; Owner: postgres
--

COPY committee (name, id, current_version, chamber, current_session, full_name) FROM stdin;
Aging	24	2017-01-18 10:52:04	senate	2017	\N
Agriculture	14	2017-01-18 10:52:04	senate	2017	\N
Alcoholism and Drug Abuse	12	2017-01-18 10:52:04	senate	2017	\N
Children and Families	27	2017-01-18 10:52:04	senate	2017	\N
Commerce, Economic Development and Small Business	19	2017-01-18 10:52:04	senate	2017	\N
Cultural Affairs, Tourism, Parks and Recreation	20	2017-01-18 10:52:04	senate	2017	\N
Education	9	2017-01-18 10:52:04	senate	2017	\N
Elections	32	2017-01-18 10:52:04	senate	2017	\N
Energy and Telecommunications	31	2017-01-18 10:52:04	senate	2017	\N
Environmental Conservation	17	2017-01-18 10:52:04	senate	2017	\N
Ethics	4	2017-01-18 10:52:04	senate	2017	\N
Ethics and Internal Governance Committee	1865	2017-05-18 10:12:14	senate	2017	\N
Higher Education	23	2017-01-18 10:52:04	senate	2017	\N
Housing, Construction and Community Development	28	2017-01-18 10:52:04	senate	2017	\N
Infrastructure and Capital Investment	58	2017-01-18 10:52:04	senate	2017	\N
Mental Health and Developmental Disabilities	26	2017-01-18 10:52:04	senate	2017	\N
Racing, Gaming and Wagering	7	2017-01-18 10:52:04	senate	2017	\N
Veterans, Homeland Security and Military Affairs	8	2017-01-18 10:52:04	senate	2017	\N
New York City Education Subcommittee	63	2017-01-18 10:52:04	senate	2017	\N
Ethics and Internal Governance	1899	2017-05-18 10:57:17	senate	2017	\N
Codes	29	2017-08-25 14:31:55	senate	2017	\N
Corporations, Authorities and Commissions	3	2017-08-25 14:31:55	senate	2017	\N
Finance	21	2017-08-25 14:31:55	senate	2017	\N
Investigations and Government Operations	2	2017-08-25 14:31:55	senate	2017	\N
Rules	18	2017-08-25 14:31:55	senate	2017	\N
Social Services	13	2017-08-25 14:31:55	senate	2017	\N
Health	6	2017-02-07 15:16:10	senate	2017	\N
Civil Service and Pensions	15	2017-06-06 20:46:33	senate	2017	\N
Banks	30	2017-02-13 16:36:21	senate	2017	\N
Cities	25	2017-02-13 16:36:21	senate	2017	\N
Judiciary	5	2017-03-02 12:36:47	senate	2017	\N
Consumer Protection	16	2017-02-13 16:36:21	senate	2017	\N
Crime Victims, Crime and Correction	33	2017-02-13 16:36:21	senate	2017	\N
Insurance	34	2017-02-13 16:36:21	senate	2017	\N
Labor	11	2017-02-13 16:36:21	senate	2017	\N
Local Government	22	2017-02-14 09:46:16	senate	2017	\N
Transportation	10	2017-08-25 14:31:55	senate	2017	\N
\.


--
-- Name: committee_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_id_seq', 3015, true);


--
-- Data for Name: committee_version; Type: TABLE DATA; Schema: master; Owner: postgres
--

COPY committee_version (id, location, meetday, meetaltweek, meetaltweektext, meettime, session_year, created, reformed, committee_name, chamber, last_fragment_id) FROM stdin;
2	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Investigations and Government Operations	senate	\N
3	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Corporations, Authorities and Commissions	senate	\N
4	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Ethics	senate	\N
5	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Judiciary	senate	\N
6	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Health	senate	\N
7	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Racing, Gaming and Wagering	senate	\N
8	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Veterans, Homeland Security and Military Affairs	senate	\N
9	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Education	senate	\N
10	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Transportation	senate	\N
11	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Labor	senate	\N
12	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Alcoholism and Drug Abuse	senate	\N
13	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Social Services	senate	\N
14	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Agriculture	senate	\N
15	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Civil Service and Pensions	senate	\N
16	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Consumer Protection	senate	\N
17	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Environmental Conservation	senate	\N
18	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Rules	senate	\N
19	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Commerce, Economic Development and Small Business	senate	\N
20	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Cultural Affairs, Tourism, Parks and Recreation	senate	\N
21	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Finance	senate	\N
22	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Local Government	senate	\N
23	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Higher Education	senate	\N
24	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Aging	senate	\N
25	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Cities	senate	\N
26	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Mental Health and Developmental Disabilities	senate	\N
27	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Children and Families	senate	\N
28	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Housing, Construction and Community Development	senate	\N
29	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Codes	senate	\N
30	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Banks	senate	\N
31	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Energy and Telecommunications	senate	\N
32	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Elections	senate	\N
33	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Crime Victims, Crime and Correction	senate	\N
34	\N	\N	f	\N	\N	2011	2011-01-01 00:00:00	infinity	Insurance	senate	\N
57	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Aging	senate	\N
71	Room 411 LOB	TUESDAY	f		09:00:00	2013	2014-02-28 11:25:44	infinity	Agriculture	senate	\N
47	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Agriculture	senate	\N
72	Room 813 LOB	TUESDAY	f		09:00:00	2013	2014-02-28 11:25:44	infinity	Alcoholism and Drug Abuse	senate	\N
45	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Alcoholism and Drug Abuse	senate	\N
65	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Banks	senate	\N
74	Room 944 LOB	MONDAY	f		13:00:00	2013	2014-02-28 11:25:44	infinity	Children and Families	senate	\N
61	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Children and Families	senate	\N
59	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Cities	senate	\N
73	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Banks	senate	\N
70	Room 816 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Aging	senate	\N
75	Room 916 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Cities	senate	\N
48	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Civil Service and Pensions	senate	\N
64	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Codes	senate	\N
52	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Commerce, Economic Development and Small Business	senate	\N
49	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Consumer Protection	senate	\N
80	Room 801 LOB	MONDAY	f		13:30:00	2013	2014-02-28 11:25:44	infinity	Corporations, Authorities and Commissions	senate	\N
36	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Corporations, Authorities and Commissions	senate	\N
81	Room 123 CAP	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2013	2014-02-28 11:25:44	infinity	Crime Victims, Crime and Correction	senate	\N
68	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Crime Victims, Crime and Correction	senate	\N
82	Room 309 LOB	WEDNESDAY	f		09:00:00	2013	2014-02-28 11:25:44	infinity	Cultural Affairs, Tourism, Parks and Recreation	senate	\N
53	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Cultural Affairs, Tourism, Parks and Recreation	senate	\N
42	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Education	senate	\N
84	Room 813 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:00:00	2013	2014-02-28 11:25:44	infinity	Elections	senate	\N
67	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Elections	senate	\N
66	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Energy and Telecommunications	senate	\N
50	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Environmental Conservation	senate	\N
87		\N	f		\N	2013	2014-02-28 11:25:44	infinity	Ethics	senate	\N
37	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Ethics	senate	\N
54	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Finance	senate	\N
39	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Health	senate	\N
90	Room 807 LOB	TUESDAY	f		12:30:00	2013	2014-02-28 11:25:44	infinity	Higher Education	senate	\N
56	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Higher Education	senate	\N
62	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Housing, Construction and Community Development	senate	\N
92	Room 804 LOB	TUESDAY	f		12:00:00	2013	2014-02-28 11:25:44	infinity	Infrastructure and Capital Investment	senate	\N
58	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Infrastructure and Capital Investment	senate	\N
69	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Insurance	senate	\N
94	Room 810 LOB	TUESDAY	f		12:30:00	2013	2014-02-28 11:25:44	infinity	Investigations and Government Operations	senate	\N
35	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Investigations and Government Operations	senate	\N
38	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Judiciary	senate	\N
96	Room 511 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2013	2014-02-28 11:25:44	infinity	Labor	senate	\N
44	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Labor	senate	\N
97	Room 945 LOB	TUESDAY	f		13:00:00	2013	2014-02-28 11:25:44	infinity	Local Government	senate	\N
55	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Local Government	senate	\N
98	Room 816 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-02-28 11:25:44	infinity	Mental Health and Developmental Disabilities	senate	\N
60	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Mental Health and Developmental Disabilities	senate	\N
40	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Racing, Gaming and Wagering	senate	\N
51	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Rules	senate	\N
101	Room 946A LOB	TUESDAY	f		10:00:00	2013	2014-02-28 11:25:44	infinity	Social Services	senate	\N
46	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Social Services	senate	\N
43	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Transportation	senate	\N
41	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	Veterans, Homeland Security and Military Affairs	senate	\N
63	\N	\N	f	\N	\N	2013	2013-01-01 00:00:00	2014-02-28 11:25:44	New York City Education Subcommittee	senate	\N
76	Room 410 LOB	MONDAY	f		13:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Civil Service and Pensions	senate	\N
77	Room 124 CAP	TUESDAY	f		09:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Codes	senate	\N
79	Room 801 LOB	MONDAY	f		13:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Consumer Protection	senate	\N
86	Room 901 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:00:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Environmental Conservation	senate	\N
99	Room 510 LOB	TUESDAY	f		09:30:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Racing, Gaming and Wagering	senate	\N
103	Room 816 LOB	TUESDAY	f		13:30:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Veterans, Homeland Security and Military Affairs	senate	\N
78	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Commerce, Economic Development and Small Business	senate	\N
83	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Education	senate	\N
95	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Judiciary	senate	\N
102	Room 124 CAP	TUESDAY	f		13:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Transportation	senate	\N
104		\N	f		\N	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	New York City Education Subcommittee	senate	\N
85	Room 709 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Energy and Telecommunications	senate	\N
88	Room 124 CAP	TUESDAY	f		11:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Finance	senate	\N
89	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Health	senate	\N
91	Room 308 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Housing, Construction and Community Development	senate	\N
93	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Insurance	senate	\N
100		\N	f		\N	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Rules	senate	\N
128	Room 816 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2015	2015-01-22 16:58:51	infinity	Aging	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
129	Room 411 LOB	TUESDAY	f		09:00:00	2015	2015-01-22 16:58:51	infinity	Agriculture	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
130	Room 813 LOB	TUESDAY	f		09:00:00	2015	2015-01-22 16:58:51	infinity	Alcoholism and Drug Abuse	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
131	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2015	2015-01-22 16:58:51	infinity	Banks	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
132	Room 944 LOB	MONDAY	f		13:00:00	2015	2015-01-22 16:58:51	infinity	Children and Families	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
133	Room 916 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2015	2015-01-22 16:58:51	infinity	Cities	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
134	Room 410 LOB	MONDAY	f		13:30:00	2015	2015-01-22 16:58:51	infinity	Civil Service and Pensions	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
135	Room 124 CAP	TUESDAY	f		09:00:00	2015	2015-01-22 16:58:51	infinity	Codes	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
136	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2015	2015-01-22 16:58:51	infinity	Commerce, Economic Development and Small Business	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
137	Room 801 LOB	MONDAY	f		13:00:00	2015	2015-01-22 16:58:51	infinity	Consumer Protection	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
138	Room 801 LOB	MONDAY	f		13:30:00	2015	2015-01-22 16:58:51	infinity	Corporations, Authorities and Commissions	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
139	Room 123 CAP	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2015	2015-01-22 16:58:51	infinity	Crime Victims, Crime and Correction	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
140	Room 309 LOB	WEDNESDAY	f		09:00:00	2015	2015-01-22 16:58:51	infinity	Cultural Affairs, Tourism, Parks and Recreation	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
141	Room 124 CAP	TUESDAY	f		10:00:00	2015	2015-01-22 16:58:51	infinity	Education	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
142	Room 813 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:00:00	2015	2015-01-22 16:58:51	infinity	Elections	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
143	Room 709 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:30:00	2015	2015-01-22 16:58:51	infinity	Energy and Telecommunications	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
144	Room 901 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:00:00	2015	2015-01-22 16:58:51	infinity	Environmental Conservation	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
145		\N	f		\N	2015	2015-01-22 16:58:51	infinity	Ethics	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
146	Room 124 CAP	TUESDAY	f		11:00:00	2015	2015-01-22 16:58:51	infinity	Finance	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
147	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2015	2015-01-22 16:58:51	infinity	Health	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
148	Room 807 LOB	TUESDAY	f		12:30:00	2015	2015-01-22 16:58:51	infinity	Higher Education	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
149	Room 308 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2015	2015-01-22 16:58:51	infinity	Housing, Construction and Community Development	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
150	Room 804 LOB	TUESDAY	f		12:00:00	2015	2015-01-22 16:58:51	infinity	Infrastructure and Capital Investment	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
151	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2015	2015-01-22 16:58:51	infinity	Insurance	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
152	Room 810 LOB	TUESDAY	f		12:30:00	2015	2015-01-22 16:58:51	infinity	Investigations and Government Operations	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
153	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2015	2015-01-22 16:58:51	infinity	Judiciary	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
154	Room 511 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2015	2015-01-22 16:58:51	infinity	Labor	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
155	Room 945 LOB	TUESDAY	f		13:00:00	2015	2015-01-22 16:58:51	infinity	Local Government	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
156	Room 816 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2015	2015-01-22 16:58:51	infinity	Mental Health and Developmental Disabilities	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
157	Room 510 LOB	TUESDAY	f		09:30:00	2015	2015-01-22 16:58:51	infinity	Racing, Gaming and Wagering	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
158		\N	f		\N	2015	2015-01-22 16:58:51	infinity	Rules	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
159	Room 946A LOB	TUESDAY	f		10:00:00	2015	2015-01-22 16:58:51	infinity	Social Services	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
160	Room 124 CAP	TUESDAY	f		13:00:00	2015	2015-01-22 16:58:51	infinity	Transportation	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
161	Room 816 LOB	TUESDAY	f		13:30:00	2015	2015-01-22 16:58:51	infinity	Veterans, Homeland Security and Military Affairs	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
162		\N	f		\N	2015	2015-01-22 16:58:51	infinity	New York City Education Subcommittee	senate	SOBI.D150122.T165851.TXT-1-COMMITTEE
368	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-02-24 09:54:05	2017-03-02 12:36:47	Codes	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
380	Room 410 LOB	MONDAY	f		13:00:00	2017	2017-06-06 20:46:33	infinity	Civil Service and Pensions	senate	SOBI.D170606.T204633.TXT-1-COMMITTEE
381	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-08-25 14:31:55	infinity	Codes	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
374	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-03-02 12:36:47	2017-08-25 14:31:55	Codes	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
382	Room 610 LOB	TUESDAY	f		12:30:00	2017	2017-08-25 14:31:55	infinity	Corporations, Authorities and Commissions	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
255	Room 813 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2017	2017-01-18 10:52:04	infinity	Aging	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
289		\N	f		\N	2017	2017-01-18 10:52:04	infinity	New York City Education Subcommittee	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
257	Room 801 LOB	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	infinity	Alcoholism and Drug Abuse	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
258	Room 511 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Banks	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
288	Room 124 CAP	MONDAY	f		11:30:00	2017	2017-01-18 10:52:04	infinity	Veterans, Homeland Security and Military Affairs	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
264	Room 945 LOB	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Consumer Protection	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
369	Room 610 LOB	TUESDAY	f		12:30:00	2017	2017-02-24 09:54:05	2017-03-02 12:36:47	Corporations, Authorities and Commissions	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
372		\N	f		\N	2017	2017-02-24 09:54:05	2017-03-02 12:36:47	Rules	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
375	Room 610 LOB	TUESDAY	f		12:30:00	2017	2017-03-02 12:36:47	2017-08-25 14:31:55	Corporations, Authorities and Commissions	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
273	Room 124 CAP	TUESDAY	f		11:00:00	2017	2017-01-18 10:52:04	2017-02-24 09:54:05	Finance	senate	SOBI.D170224.T095405.TXT-1-COMMITTEE
285		\N	f		\N	2017	2017-01-18 10:52:04	2017-02-24 09:54:05	Rules	senate	SOBI.D170224.T095405.TXT-1-COMMITTEE
263	Room 813 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2017	2017-01-18 10:52:04	infinity	Commerce, Economic Development and Small Business	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
268	Room 810 LOB	TUESDAY	f		10:00:00	2017	2017-01-18 10:52:04	infinity	Education	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
269	Room 805 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:00:00	2017	2017-01-18 10:52:04	infinity	Elections	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
272		\N	f		\N	2017	2017-01-18 10:52:04	infinity	Ethics	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
275	Room 807 LOB	TUESDAY	f		12:30:00	2017	2017-01-18 10:52:04	infinity	Higher Education	senate	SOBI.D170118.T105204.TXT-1-COMMITTEE
383	Room 124 CAP	TUESDAY	f		11:00:00	2017	2017-08-25 14:31:55	infinity	Finance	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
260	Room 944 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Cities	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
262	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Codes	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
266	Room 123 CAP	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Crime Victims, Crime and Correction	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
385		\N	f		\N	2017	2017-08-25 14:31:55	infinity	Rules	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
386	Room 801 LOB	TUESDAY	f		10:30:00	2017	2017-08-25 14:31:55	infinity	Social Services	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
286	Room 801 LOB	TUESDAY	f		10:30:00	2017	2017-01-18 10:52:04	2017-08-25 14:31:55	Social Services	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
281	Room 123 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Labor	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
287	Room 804 LOB	TUESDAY	f		10:30:00	2017	2017-01-18 10:52:04	2017-01-30 16:39:21	Transportation	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
274	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2017	2017-01-18 10:52:04	2017-02-07 15:16:10	Health	senate	SOBI.D170207.T151610.TXT-1-COMMITTEE
278	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2017	2017-01-18 10:52:04	2017-02-07 15:16:10	Insurance	senate	SOBI.D170207.T151610.TXT-1-COMMITTEE
370	Room 124 CAP	TUESDAY	f		11:00:00	2017	2017-02-24 09:54:05	2017-03-02 12:36:47	Finance	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
377	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2017	2017-03-02 12:36:47	infinity	Judiciary	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
379	Room 410 LOB	MONDAY	f		13:00:00	2017	2017-06-06 18:26:24	2017-06-06 20:46:33	Civil Service and Pensions	senate	SOBI.D170606.T204633.TXT-1-COMMITTEE
376	Room 124 CAP	TUESDAY	f		11:00:00	2017	2017-03-02 12:36:47	2017-08-25 14:31:55	Finance	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
384	Room 816 LOB	TUESDAY	f		12:30:00	2017	2017-08-25 14:31:55	infinity	Investigations and Government Operations	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
378		\N	f		\N	2017	2017-03-02 12:36:47	2017-08-25 14:31:55	Rules	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
387	Room 803 LOB	TUESDAY	f		10:30:00	2017	2017-08-25 14:31:55	infinity	Transportation	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
321		\N	f		\N	2017	2017-05-18 10:12:14	infinity	Ethics and Internal Governance Committee	senate	SOBI.D170518.T101214.TXT-1-COMMITTEE
322		\N	f		\N	2017	2017-05-18 10:57:17	infinity	Ethics and Internal Governance	senate	SOBI.D170518.T105717.TXT-1-COMMITTEE
326	Room 410 LOB	MONDAY	f		13:30:00	2013	2014-03-03 17:09:09	infinity	Civil Service and Pensions	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
327	Room 124 CAP	TUESDAY	f		09:00:00	2013	2014-03-03 17:09:09	infinity	Codes	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
329	Room 801 LOB	MONDAY	f		13:00:00	2013	2014-03-03 17:09:09	infinity	Consumer Protection	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
331	Room 709 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:30:00	2013	2014-03-03 17:09:09	infinity	Energy and Telecommunications	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
332	Room 124 CAP	TUESDAY	f		11:00:00	2013	2014-03-03 17:09:09	infinity	Finance	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
333	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2013	2014-03-03 17:09:09	infinity	Health	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
334	Room 308 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-03-03 17:09:09	infinity	Housing, Construction and Community Development	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
335	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-03-03 17:09:09	infinity	Insurance	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
336	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-03-03 17:09:09	infinity	Judiciary	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
337		\N	f		\N	2013	2014-03-03 17:09:09	infinity	Rules	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
338	Room 124 CAP	TUESDAY	f		13:00:00	2013	2014-03-03 17:09:09	infinity	Transportation	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
339		\N	f		\N	2013	2014-03-03 17:09:09	infinity	New York City Education Subcommittee	senate	SOBI.D140303.T170909.TXT-1-COMMITTEE
340	Room 816 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2013	2014-03-13 14:48:16	infinity	Aging	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
343	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-03-13 14:48:16	infinity	Education	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
330	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-03-03 17:09:09	2014-03-13 14:48:16	Education	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
346	Room 816 LOB	TUESDAY	f		13:30:00	2013	2014-03-13 14:48:16	infinity	Veterans, Homeland Security and Military Affairs	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
256	Room 412 LOB	TUESDAY	f		09:00:00	2017	2017-01-18 10:52:04	infinity	Agriculture	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
259	Room 901 LOB	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	infinity	Children and Families	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
341	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-03-13 14:48:16	infinity	Banks	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
325	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-03-03 17:09:09	2014-03-13 14:48:16	Banks	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
342	Room 916 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-03-13 14:48:16	infinity	Cities	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
344	Room 901 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:00:00	2013	2014-03-13 14:48:16	infinity	Environmental Conservation	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
345	Room 510 LOB	TUESDAY	f		09:30:00	2013	2014-03-13 14:48:16	infinity	Racing, Gaming and Wagering	senate	SOBI.D140313.T144816.TXT-1-COMMITTEE
261	Room 410 LOB	MONDAY	f		13:00:00	2017	2017-01-18 10:52:04	2017-02-13 16:36:21	Civil Service and Pensions	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
270	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-01-18 10:52:04	infinity	Energy and Telecommunications	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
371	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2017	2017-02-24 09:54:05	2017-03-02 12:36:47	Judiciary	senate	SOBI.D170302.T123647.TXT-1-COMMITTEE
373	Room 803 LOB	TUESDAY	f		10:30:00	2017	2017-02-24 09:54:05	2017-08-25 14:31:55	Transportation	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
347	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-03-17 18:43:42	infinity	Commerce, Economic Development and Small Business	senate	SOBI.D140317.T184342.TXT-1-COMMITTEE
328	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-03-03 17:09:09	2014-03-17 18:43:42	Commerce, Economic Development and Small Business	senate	SOBI.D140317.T184342.TXT-1-COMMITTEE
265	Room 610 LOB	TUESDAY	f		12:30:00	2017	2017-01-18 10:52:04	2017-02-24 09:54:05	Corporations, Authorities and Commissions	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
267	Room 904 LOB	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	infinity	Cultural Affairs, Tourism, Parks and Recreation	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
276	Room 309 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:45:00	2017	2017-01-18 10:52:04	infinity	Housing, Construction and Community Development	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
277	Room 801 LOB	TUESDAY	f		12:00:00	2017	2017-01-18 10:52:04	infinity	Infrastructure and Capital Investment	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
282	Room 946A LOB	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	2017-02-13 16:36:21	Local Government	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
352	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Crime Victims, Crime and Correction	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
271	Room 307 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:00:00	2017	2017-01-18 10:52:04	infinity	Environmental Conservation	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
283	Room 816 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:00:00	2017	2017-01-18 10:52:04	infinity	Mental Health and Developmental Disabilities	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
279	Room 816 LOB	TUESDAY	f		12:30:00	2017	2017-01-18 10:52:04	2017-08-25 14:31:55	Investigations and Government Operations	senate	SOBI.D170825.T143155.TXT-1-COMMITTEE
280	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2017	2017-01-18 10:52:04	2017-02-24 09:54:05	Judiciary	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
284	Room 124 CAP	TUESDAY	f		09:30:00	2017	2017-01-18 10:52:04	infinity	Racing, Gaming and Wagering	senate	SOBI.D170130.T163921.TXT-1-COMMITTEE
360	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-02-13 16:36:21	2017-02-24 09:54:05	Codes	senate	SOBI.D170224.T095405.TXT-1-COMMITTEE
355	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2017	2017-02-07 15:16:10	infinity	Health	senate	SOBI.D170207.T151610.TXT-1-COMMITTEE
357	Room 915 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-02-13 16:36:21	infinity	Banks	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
348	Room 915 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Banks	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
358	Room 945 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-02-13 16:36:21	infinity	Cities	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
349	Room 945 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Cities	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
350	Room 124 CAP	TUESDAY	f		13:00:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Codes	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
361	Room 510 LOB	TUESDAY	f		10:00:00	2017	2017-02-13 16:36:21	infinity	Consumer Protection	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
351	Room 510 LOB	TUESDAY	f		10:00:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Consumer Protection	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
362	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2017	2017-02-13 16:36:21	infinity	Crime Victims, Crime and Correction	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
363	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2017	2017-02-13 16:36:21	infinity	Insurance	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
356	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2017	2017-02-07 15:16:10	2017-02-13 16:36:21	Insurance	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
364	Room 123 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2017	2017-02-13 16:36:21	infinity	Labor	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
353	Room 123 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2017	2017-01-30 16:39:21	2017-02-13 16:36:21	Labor	senate	SOBI.D170213.T163621.TXT-1-COMMITTEE
359	Room 410 LOB	MONDAY	f		13:00:00	2017	2017-02-13 16:36:21	2017-02-14 09:46:16	Civil Service and Pensions	senate	SOBI.D170214.T094616.TXT-1-COMMITTEE
367	Room 946A LOB	TUESDAY	f		09:30:00	2017	2017-02-14 09:46:16	infinity	Local Government	senate	SOBI.D170214.T094616.TXT-1-COMMITTEE
365	Room 946A LOB	TUESDAY	f		09:30:00	2017	2017-02-13 16:36:21	2017-02-14 09:46:16	Local Government	senate	SOBI.D170214.T094616.TXT-1-COMMITTEE
354	Room 803 LOB	TUESDAY	f		10:30:00	2017	2017-01-30 16:39:21	2017-02-24 09:54:05	Transportation	senate	SOBI.D170224.T095405.TXT-1-COMMITTEE
366	Room 410 LOB	MONDAY	f		13:00:00	2017	2017-02-14 09:46:16	2017-06-06 18:26:24	Civil Service and Pensions	senate	SOBI.D170606.T182624.TXT-2-COMMITTEE
\.


SET search_path = public, pg_catalog;

--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY person (id, full_name, first_name, middle_name, last_name, email, prefix, suffix, verified, img_name) FROM stdin;
197	Shirley L. Huntley	Shirley	L.	Huntley	shuntley@nysenate.gov	Senator	\N	t	no_image.jpg
257	Ted O'Brien	Ted	\N	O'Brien	obrien@nysenate.gov	Senator	\N	t	no_image.jpg
188	John L. Sampson	John	L.	Sampson	sampson@senate.state.ny.us	Senator	\N	t	369_john_l._sampson.jpg
207	Suzi Oppenheimer	Suzi	\N	Oppenheimer	oppenhei@senate.state.ny.us	Senator	\N	t	no_image.jpg
238	Kenneth P. LaValle	Kenneth	P.	LaValle	lavalle@nysenate.gov	Senator	\N	t	419_kenneth_p._lavalle.jpg
953	Stacey Pheffer Amato	Stacey		Pheffer Amato		Assembly Member		t	no_image.jpg
940	Clyde Vanel	Clyde		Vanel		Assembly Member		t	no_image.jpg
942	Billy Jones	Billy		Jones		Assembly Member		t	no_image.jpg
944	Robert C. Carroll	Robert	C.	Carroll	CarrollR@nyassembly.gov	Assembly Member		t	no_image.jpg
193	William J. Larkin Jr.	William	J.	Larkin	larkin@senate.state.ny.us	Senator	Jr.	t	374_william_j._larkin_jr..jpg
204	Adriano Espaillat	Adriano	\N	Espaillat	espailla@nysenate.gov	Senator	\N	t	385_adriano_espaillat.jpg
243	Kemp Hannon	Kemp	\N	Hannon	hannon@nysenate.gov	Senator	\N	t	424_kemp_hannon.jpg
212	Joseph A. Griffo	Joseph	A.	Griffo	griffo@nysenate.gov	Senator	\N	t	393_joseph_a._griffo.jpg
261	Simcha Felder	Simcha	\N	Felder	felder@nysenate.gov	Senator	\N	t	439_simcha_felder.jpg
250	Cecilia Tkaczyk	Cecilia	\N	Tkaczyk	tkaczyk@nysenate.gov	Senator	\N	t	no_image.jpg
232	John J. Bonacic	John	J.	Bonacic	bonacic@nysenate.gov	Senator	\N	t	413_john_j._bonacic.jpg
236	Joseph E. Robach	Joseph	E.	Robach	robach@nysenate.gov	Senator	\N	t	417_joseph_e._robach.jpg
199	Ruth Hassell-Thompson	Ruth	\N	Hassell-Thompson	hassellt@senate.state.ny.us	Senator	\N	t	380_ruth_hassell-thompson.jpg
945	Kevin M. Byrne	Kevin	M.	Byrne	ByrneK@nyassembly.gov	Assembly Member		t	no_image.jpg
949	Mary Beth Walsh	Mary Beth		Walsh	walshm@nyassembly.gov	Assembly Member		t	no_image.jpg
229	Tom Libous	Tom	\N	Libous	senator@senatorlibous.com	Senator	\N	t	410_tom_libous.jpg
950	Inez E. Dickens	Inez	E.	Dickens		Assembly Member		t	no_image.jpg
954	Yuh-Line Niou	Yuh-Line		Niou	niouy@nyassembly.gov	Assembly Member		t	no_image.jpg
270	Brian X. Foley	Brian	X.	Foley	bfoley@senate.state.ny.us	Senator	\N	t	no_image.jpg
227	Greg Ball	Greg	\N	Ball	gball@nysenate.gov	Senator	\N	t	no_image.jpg
271	Vincent L. Leibell	Vincent	L.	Leibell	 leibell@senate.state.ny.us	Senator	\N	t	no_image.jpg
269	Antoine M Thompson	Antoine	\N	Thompson	athompso@senate.state.ny.us	Senator	\N	t	no_image.jpg
258	Terry Gipson	Terry	\N	Gipson	gipson@nysenate.gov	Senator	\N	t	no_image.jpg
239	Carl Kruger	Carl	\N	Kruger	kruger@senate.state.ny.us	Senator	\N	t	no_image.jpg
223	Lee M. Zeldin	Lee	M.	Zeldin	zeldin@nysenate.gov	Senator	\N	t	no_image.jpg
273	Darrel J. Aubertine	Darrel	J.	Aubertine	aubertin@senate.state.ny.us	Senator	\N	t	no_image.jpg
927	Pamela J. Hunter	Pamela	J.	Hunter		Assembly Member		t	no_image.jpg
955	Monica P. Wallace	Monica	P.	Wallace		Assembly Member		t	no_image.jpg
956	Tremaine Wright	Tremaine		Wright	wrightt@nyassembly.gov	Assembly Member		t	no_image.jpg
958	Brian Benjamin	Brian		Benjamin		Senator		t	no_image.jpg
237	Hugh T. Farley	Hugh	T.	Farley	farley@senate.state.ny.us	Senator	\N	t	418_hugh_t._farley.jpg
209	Jeffrey D. Klein	Jeffrey	D.	Klein	jdklein@senate.state.ny.us	Senator	\N	t	390_jeffrey_d._klein.jpg
216	Jack M. Martins	Jack	M.	Martins	martins@nysenate.gov	Senator	\N	t	397_jack_m._martins.jpg
213	Michael F. Nozzolio	Michael	F.	Nozzolio	nozzolio@senate.state.ny.us	Senator	\N	t	394_michael_f._nozzolio.jpg
249	Dean G. Skelos	Dean	G.	Skelos	skelos@nysenate.gov	Senator	\N	t	430_dean_g._skelos.jpg
189	David Carlucci	David	\N	Carlucci	carlucci@nysenate.gov	Senator	\N	t	370_david_carlucci.jpg
226	Carl L Marcellino	Carl	\N	Marcellino	marcelli@senate.state.ny.us	Senator	\N	t	407_carl_l_marcellino.jpg
228	Andrew J Lanza	Andrew	\N	Lanza	lanza@senate.state.ny.us	Senator	\N	t	409_andrew_j_lanza.jpg
215	Andrea Stewart-Cousins	Andrea	\N	Stewart-Cousins	scousins@senate.state.ny.us	Senator	\N	t	396_andrea_stewart-cousins.jpg
218	John J. Flanagan	John	J.	Flanagan	flanagan@senate.state.ny.us	Senator	\N	t	399_john_j._flanagan.jpg
921	Chris Jacobs	Chris		Jacobs	Jacobs@nysenate.gov	Senator		t	no_image.jpg
922	Elaine Phillips	Elaine	\N	Phillips	EPhillips@nysenate.gov	Senator	\N	t	no_image.jpg
923	John E. Brooks	John	E.	Brooks	Brooks@nysenate.gov	Senator	\N	t	no_image.jpg
924	Jamaal Bailey	Jamaal	\N	Bailey	Bailey	Senator	\N	t	no_image.jpg
925	James Tedisco	James	\N	Tedisco	Tedisco@nysenate.gov	Senator	\N	t	no_image.jpg
937	Melissa Miller	Melissa		Miller		Assembly Member		t	no_image.jpg
951	Carmen De La Rosa	Carmen		De La Rosa		Assembly Member		t	no_image.jpg
952	Anthony D'Urso	Anthony		D'Urso		Assembly Member		t	no_image.jpg
266	Eric T. Schneiderman	Eric	T.	Schneiderman	schneide@senate.state.ny.us	Senator	\N	t	no_image.jpg
230	George D. Maziarz	George	D.	Maziarz	maziarz@nysenate.gov	Senator	\N	t	no_image.jpg
264	Pedro Espada Jr.	Pedro	\N	Espada	espada@senate.state.ny.us	Senator	Jr.	t	no_image.jpg
244	Charles J. Fuschillo Jr.	Charles	J.	Fuschillo	fuschill@senate.state.ny.us	Senator	Jr.	t	no_image.jpg
276	Hiram Monserrate	Hiram		Monserrate		Senator		t	no_image.jpg
277	David Storobin	David		Storobin		Senator		t	no_image.jpg
278	Marc Alessi 	Marc	\N	Alessi	\N	Assembly Member	\N	t	no_image.jpg
280	L. Dean Murray	L.	Dean	Murray	\N	Assembly Member	\N	t	no_image.jpg
282	Ginny Fields 	Ginny	\N	Fields	\N	Assembly Member	\N	t	no_image.jpg
911	Alicia Hyndman	Alicia		Hyndman		Assembly Member		t	no_image.jpg
957	Angelo J. Morinello	Angelo	J.	Morinello	morinelloa@nyassembly.gov	Assembly Member		t	no_image.jpg
947	Brian D. Miller	Brian	D.	Miller	millerb@nyassembly.gov	Assembly Member		t	no_image.jpg
362	Nelson Castro	Nelson	\N	Castro	\N	Assembly Member	\N	t	no_image.jpg
365	Robert Castelli	Robert	\N	Castelli	\N	Assembly Member	\N	t	no_image.jpg
298	Thomas Alfano 	Thomas	\N	Alfano	\N	Assembly Member	\N	t	no_image.jpg
368	Richard Brodsky 	Richard	\N	Brodsky	\N	Assembly Member	\N	t	no_image.jpg
369	Mike Spano	Mike	\N	Spano	\N	Assembly Member	\N	t	no_image.jpg
372	Nancy Calhoun	Nancy	\N	Calhoun	\N	Assembly Member	\N	t	no_image.jpg
373	Ann Rabbitt	Ann	\N	Rabbitt	\N	Assembly Member	\N	t	no_image.jpg
378	Joel Miller	Joel	\N	Miller	\N	Assembly Member	\N	t	no_image.jpg
379	Marcus Molinaro	Marcus	\N	Molinaro	\N	Assembly Member	\N	t	no_image.jpg
380	John McEneny	John	\N	McEneny	\N	Assembly Member	\N	t	no_image.jpg
381	George Amedore	George	\N	Amedore	\N	Assembly Member	\N	t	no_image.jpg
382	Ronald Canestrari	Ronald	\N	Canestrari	\N	Assembly Member	\N	t	no_image.jpg
384	Timothy P. Gordon 	Timothy	P.	Gordon	\N	Assembly Member	\N	t	no_image.jpg
385	Robert Reilly	Robert	\N	Reilly	\N	Assembly Member	\N	t	no_image.jpg
388	Tony Jordan	Tony	\N	Jordan	\N	Assembly Member	\N	t	no_image.jpg
389	Teresa Sayward	Teresa	\N	Sayward	\N	Assembly Member	\N	t	no_image.jpg
391	David Townsend 	David	\N	Townsend	\N	Assembly Member	\N	t	no_image.jpg
392	RoAnn Destito	RoAnn	\N	Destito	\N	Assembly Member	\N	t	no_image.jpg
395	Joan Christensen 	Joan	\N	Christensen	\N	Assembly Member	\N	t	no_image.jpg
398	Dierdre Scozzafava 	Dierdre	\N	Scozzafava	\N	Assembly Member	\N	t	no_image.jpg
403	Peter Lopez	Peter	\N	Lopez	\N	Assembly Member	\N	t	no_image.jpg
407	Susan John 	Susan	John	John	\N	Assembly Member	\N	t	no_image.jpg
410	Bill Reilich	Bill	\N	Reilich	\N	Assembly Member	\N	t	no_image.jpg
411	David Koon 	David	Koon	Koon	\N	Assembly Member	\N	t	no_image.jpg
412	James Bacalles 	James	\N	Bacalles	\N	Assembly Member	\N	t	no_image.jpg
413	Tom O'Mara 	Tom	\N	O'Mara	\N	Assembly Member	\N	t	no_image.jpg
414	Francine DelMonte 	Francine	\N	DelMonte	\N	Assembly Member	\N	t	no_image.jpg
419	Dennis H. Gabryszak	Dennis	H.	Gabryszak	\N	Assembly Member	\N	t	no_image.jpg
420	Sam Hoyt	Sam	Hoyt	Hoyt	\N	Assembly Member	\N	t	no_image.jpg
421	Mark J.F. Schroeder	Mark	J.F.	Schroeder	\N	Assembly Member	\N	t	no_image.jpg
422	Jack Quinn III 	Jack	\N	Quinn	\N	Assembly Member	III	t	no_image.jpg
423	Daniel Burling	Daniel	\N	Burling	\N	Assembly Member	\N	t	no_image.jpg
424	James Hayes	James	\N	Hayes	\N	Assembly Member	\N	t	no_image.jpg
426	William Parment 	William	\N	Parment	\N	Assembly Member	\N	t	no_image.jpg
898	Pamela Harris	Pamela		Harris		Assembly Member		t	no_image.jpg
452	Donald R. Miller	Donald	R.	Miller	\N	Assembly Member	\N	t	no_image.jpg
454	Sean T. Hanna	Sean	T.	Hanna	\N	Assembly Member	\N	t	no_image.jpg
462	Kevin Smardz	Kevin	\N	Smardz	\N	Assembly Member	\N	t	no_image.jpg
465	Adam Bradley	Adam		Bradley				t	no_image.jpg
466	Patricia Eddington	Patricia		Eddington				t	no_image.jpg
468	Rob Walker	Rob		Walker				t	no_image.jpg
469	Anthony Seminerio	Anthony		Seminerio				t	no_image.jpg
471	Ruben Diaz Jr.	Ruben		Diaz			Jr.	t	no_image.jpg
473	Thomas Kirwan	Thomas		Kirwan				t	no_image.jpg
490	Gabriela Rosa	Gabriela		Rosa	\N	Assemblymember		t	no_image.jpg
498	Greene							f	no_image.jpg
499	Edward Hennessey	Edward	\N	Hennessey	\N	\N	\N	t	no_image.jpg
500	WEISENBERGT	\N	\N	WEISENBERGT	\N	\N	\N	f	no_image.jpg
502	PEOPLES-STOKE	\N	\N	PEOPLES-STOKE	\N	\N	\N	f	no_image.jpg
503	B GOTTFRIED	B	\N	GOTTFRIED	\N	\N	\N	f	no_image.jpg
504	RULES	\N	\N	RULES	\N	\N	\N	f	no_image.jpg
505	MAGNARELLIS	\N	\N	MAGNARELLIS	\N	\N	\N	f	no_image.jpg
506	PEOPLES-STOKE	\N	\N	PEOPLES-STOKE	\N	\N	\N	f	no_image.jpg
507	M WEINSTEIN	M	\N	WEINSTEIN	\N	\N	\N	f	no_image.jpg
508	CYMBROWITZSNY	\N	\N	CYMBROWITZSNY	\N	\N	\N	f	no_image.jpg
509	I ZEBROWSKI	I	\N	ZEBROWSKI	\N	\N	\N	f	no_image.jpg
510	E WEINSTEIN	E	\N	WEINSTEIN	\N	\N	\N	f	no_image.jpg
511	BRAUNSTEINWITZ	\N	\N	BRAUNSTEINWITZ	\N	\N	\N	f	no_image.jpg
512	BRAUNSTEINZ	\N	\N	BRAUNSTEINZ	\N	\N	\N	f	no_image.jpg
513	STRIPE	\N	\N	STRIPE	\N	\N	\N	f	no_image.jpg
267	Frank Padavan	Frank	\N	Padavan	padavan@senate.state.ny.us	Senator	\N	t	no_image.jpg
221	Stephen M. Saland	Stephen	M.	Saland	 saland@nysenate.gov	Senator	\N	t	no_image.jpg
274	Dale M. Volker	Dale	M.	Volker	volker@senate.state.ny.us	Senator	\N	t	no_image.jpg
205	Owen H. Johnson	Owen	H.	Johnson	ojohnson@senate.state.ny.us	Senator	\N	t	no_image.jpg
206	Thomas K. Duane	Thomas	K.	Duane	duane@senate.state.ny.us	Senator	\N	t	no_image.jpg
650	Peter Lopez	Peter	\N	Lopez	\N	\N	\N	t	828_peter_lopez.jpg
918	Marisol Alcantara	Marisol		Alcantara	Alcantara@nysenate.gov	Senator		t	no_image.jpg
406	Joseph Errigo 	Joseph	\N	Errigo	\N	Assembly Member	\N	t	no_image.jpg
208	Roy J. McDonald	Roy	J.	McDonald	mcdonald@senate.state.ny.us	Senator	\N	t	no_image.jpg
211	James S. Alesi	James	S.	Alesi	alesi@senate.state.ny.us	Senator	\N	t	no_image.jpg
224	Malcolm A. Smith	Malcolm	A.	Smith	masmith@senate.state.ny.us 	Senator	\N	t	no_image.jpg
272	George Winner	George	\N	Winner	winner@senate.state.ny.us	Senator	\N	t	no_image.jpg
268	Craig M. Johnson	Craig	M.	Johnson	johnson@senate.state.ny.us	Senator	\N	t	no_image.jpg
263	Thomas P. Morahan	Thomas	P.	Morahan	district38@nysenate.gov	Senator	\N	t	no_image.jpg
265	George Onorato	George	\N	Onorato	onorato@senate.state.ny.us	Senator	\N	t	no_image.jpg
217	Mark Grisanti	Mark	\N	Grisanti	grisanti@nysenate.gov	Senator	\N	t	no_image.jpg
275	William T. Stachowski	William	T.	Stachowski	stachows@senate.state.ny.us	Senator	\N	t	no_image.jpg
201	Eric Adams	Eric	\N	Adams	eadams@senate.state.ny.us	Senator	\N	t	no_image.jpg
299	Grace Meng	Grace		Meng	\N	Assembly Member	\N	t	no_image.jpg
287	James Conte	James	\N	Conte	\N	Assembly Member	\N	t	no_image.jpg
288	Robert Sweeney	Robert	\N	Sweeney	\N	Assembly Member	\N	t	no_image.jpg
291	Robert Barra 	Robert	\N	Barra	\N	Assembly Member	\N	t	no_image.jpg
297	Harvey Weisenberg	Harvey	\N	Weisenberg	\N	Assembly Member	\N	t	no_image.jpg
300	Audrey Pheffer	Audrey	\N	Pheffer	\N	Assembly Member	\N	t	no_image.jpg
302	Rory Lancman	Rory	\N	Lancman	\N	Assembly Member	\N	t	no_image.jpg
303	Ann-Margaret Carrozza 	Ann-Margaret	\N	Carrozza	\N	Assembly Member	\N	t	no_image.jpg
304	Nettie Mayersohn	Nettie	\N	Mayersohn	\N	Assembly Member	\N	t	no_image.jpg
318	Rhoda Jacobs	Rhoda	\N	Jacobs	\N	Assembly Member	\N	t	no_image.jpg
328	Joan Millman	Joan	\N	Millman	\N	Assembly Member	\N	t	no_image.jpg
329	Vito Lopez	Vito	\N	Lopez	\N	Assembly Member	\N	t	no_image.jpg
330	Darryl Towns	Darryl	\N	Towns	\N	Assembly Member	\N	t	no_image.jpg
331	"William Boyland	"William	\N	Boyland	\N	Assembly Member	\N	t	no_image.jpg
333	Hakeem Jeffries	Hakeem	\N	Jeffries	\N	Assembly Member	\N	t	no_image.jpg
335	Alan Maisel	Alan	\N	Maisel	\N	Assembly Member	\N	t	no_image.jpg
336	Janele Hyer-Spencer 	Janele	\N	Hyer-Spencer	\N	Assembly Member	\N	t	no_image.jpg
338	Louis Tobacco	Louis	\N	Tobacco	\N	Assembly Member	\N	t	no_image.jpg
341	Micah Kellner	Micah	\N	Kellner	\N	Assembly Member	\N	t	no_image.jpg
344	Adam Clayton Powell IV 	Adam	\N	Powell	\N	Assembly Member	IV	t	no_image.jpg
349	Jonathan Bing	Jonathan	Bing	Bing	\N	Assembly Member	\N	t	no_image.jpg
353	Vanessa Gibson	Vanessa	\N	Gibson	\N	Assembly Member	\N	t	no_image.jpg
355	Michael Benjamin 	Michael	\N	Benjamin	\N	Assembly Member	\N	t	no_image.jpg
356	Naomi Rivera	Naomi	\N	Rivera	\N	Assembly Member	\N	t	no_image.jpg
319	Karim Camara	Karim	\N	Camara	\N	Assembly Member	\N	t	no_image.jpg
427	Daniel P. Losquadro	Daniel	P.	Losquadro	\N	Assembly Member	\N	t	no_image.jpg
436	Rafael Espinal	Rafael	\N	Espinal	\N	Assembly Member	\N	t	no_image.jpg
439	Guillermo Linares	Guillermo	\N	Linares	\N	Assembly Member	\N	t	no_image.jpg
441	Eric Stevenson	Eric	\N	Stevenson	\N	Assembly Member	\N	t	no_image.jpg
515	S O'DONNELL	S	\N	O'DONNELL	\N	\N	\N	f	no_image.jpg
516	T WEINSTEIN	T	\N	WEINSTEIN	\N	\N	\N	f	no_image.jpg
517	B O'DONNELL	B	\N	O'DONNELL	\N	\N	\N	f	no_image.jpg
518	O BENEDETTO	O	\N	BENEDETTO	\N	\N	\N	f	no_image.jpg
519	R BENEDETTO	R	\N	BENEDETTO	\N	\N	\N	f	no_image.jpg
520	L O'DONNELL	L	\N	O'DONNELL	\N	\N	\N	f	no_image.jpg
712	Susan Serino	Susan	\N	Serino	\N	\N	\N	t	890_susan_serino.jpg
713	Terrence P. Murphy	Terrence	P.	Murphy	\N	\N	\N	t	891_terrence_p._murphy.jpg
717	Michael Venditto	Michael	\N	Venditto	\N	\N	\N	t	895_michael_venditto.jpg
716	Marc Panepinto	Marc	\N	Panepinto	\N	\N	\N	t	894_marc_panepinto.jpg
451	Sam Roberts	Sam	\N	Roberts	\N	Assembly Member	\N	t	629_sam_roberts.jpg
464	Andrew Goodell	Andrew	\N	Goodell	\N	Assembly Member	\N	t	642_andrew_goodell.jpg
730	Karl Brabenec	Karl	\N	Brabenec	\N	\N	\N	t	908_karl_brabenec.jpg
354	Jose Rivera	Jose	\N	Rivera	\N	Assembly Member	\N	t	528_peter_rivera.jpg
485	Walter T. Mosley	Walter	T.	Mosley	\N	Assemblymember		t	663_walter_t._mosley.jpg
360	Carmen E. Arroyo	Carmen	E.	Arroyo	\N	Assembly Member	\N	t	536_carmen_e._arroyo.jpg
493	Luis R. Sepúlveda	Luis	R.	Sepúlveda	\N	Assemblymember		t	no_image.jpg
370	Kenneth Zebrowski	Kenneth	\N	Zebrowski	\N	Assembly Member	\N	t	545_"kenneth_zebrowski.jpg
425	Joseph Giglio	Joseph	\N	Giglio	\N	Assembly Member	\N	t	599_joseph_giglio.jpg
432	Edward Braunstein	Edward	\N	Braunstein	\N	Assembly Member	\N	t	610_edward_braunstein.jpg
317	Helene Weinstein	Helene	\N	Weinstein	\N	Assembly Member	\N	t	494_helene_weinstein.jpg
359	Carl Heastie	Carl	\N	Heastie	\N	Assembly Member	\N	t	535_carl_heastie.jpg
320	James F. Brennan	James	F.	Brennan	\N	Assembly Member	\N	t	497_james_f._brennan.jpg
322	Alec Brook-Krasny	Alec	\N	Brook-Krasny	\N	Assembly Member	\N	t	499_alec_brook-krasny.jpg
310	Barbara Clark	Barbara	\N	Clark	\N	Assembly Member	\N	t	458_barbara_clark.jpg
938	Jaime R. Williams	Jaime	R.	Williams		Assembly Member		t	no_image.jpg
418	Jane Corwin	Jane	\N	Corwin	\N	Assembly Member	\N	t	592_jane_corwin.jpg
394	Addie Jenne	Addie	\N	Jenne	\N	Assembly Member	\N	t	568_addie_jenne_russell.jpg
431	Phillip Goldfeder	Phillip	\N	Goldfeder	\N	Assembly Member	\N	t	609_phillip_goldfeder.jpg
324	Dov Hikind	Dov	\N	Hikind	\N	Assembly Member	\N	t	501_dov_hikind.jpg
326	Joseph Lentol	Joseph	\N	Lentol	\N	Assembly Member	\N	t	503_joseph_lentol.jpg
455	Harry B. Bronson	Harry	B.	Bronson	\N	Assembly Member	\N	t	633_harry_b._bronson.jpg
446	Steve Katz	Steve	\N	Katz	\N	Assembly Member	\N	t	624_steve_katz.jpg
458	Christopher Friend	Christopher	\N	Friend	\N	Assembly Member	\N	t	636_christopher_friend.jpg
461	Mickey Kearns	Mickey	\N	Kearns	\N	Assembly Member	\N	t	639_mickey_kearns.jpg
315	Michael G. Miller	Michael	G.	Miller	\N	Assembly Member	\N	t	492_michael_g._miller.jpg
337	Matthew Titone	Matthew	\N	Titone	\N	Assembly Member	\N	t	514_matthew_titone.jpg
401	Barbara Lifton	Barbara	\N	Lifton	\N	Assembly Member	\N	t	575_barbara_lifton.jpg
404	Robert Oaks	Robert	Oaks	Oaks	\N	Assembly Member	\N	t	578_robert_oaks.jpg
345	Daniel O'Donnell	Daniel	\N	O'Donnell	\N	Assembly Member	\N	t	522_daniel_o'donnell.jpg
283	Philip Ramos	Philip	\N	Ramos	\N	Assembly Member	\N	t	468_philip_ramos.jpg
284	Michael J. Fitzpatrick	Michael	J.	Fitzpatrick	\N	Assembly Member	\N	t	469_michael_j._fitzpatrick.jpg
476	David Buchwald	David		Buchwald	\N	Assemblymember		t	654_david_buchwald.jpg
293	Michelle Schimel	Michelle	\N	Schimel	\N	Assembly Member	\N	t	477_michelle_schimel.jpg
340	Sheldon Silver	Sheldon	\N	Silver	\N	Assembly Member	\N	t	517_sheldon_silver.jpg
477	Maritza Davila	Maritza		Davila	\N	Assemblymember		t	655_maritza_davila.jpg
449	Claudia Tenney	Claudia	\N	Tenney	\N	Assembly Member	\N	t	627_claudia_tenney.jpg
479	Patricia Fahy	Patricia		Fahy	\N	Assemblymember		t	657_patricia_fahy.jpg
475	Joseph Borelli	Joseph		Borelli	\N	Assemblymember		t	653_joseph_borelli.jpg
480	Andrew R. Garbarino	Andrew	R.	Garbarino	\N	Assemblymember		t	658_andrew_r._garbarino.jpg
459	John Ceretto	John	\N	Ceretto	\N	Assembly Member	\N	t	637_john_ceretto.jpg
483	Chad A. Lupinacci	Chad	A.	Lupinacci	\N	Assemblymember		t	661_chad_a._lupinacci.jpg
429	Brian F. Curran	Brian	F.	Curran	\N	Assembly Member	\N	t	607_brian_f._curran.jpg
311	Michael DenDekker	Michael	\N	DenDekker	\N	Assembly Member	\N	t	459_michael_dendekker.jpg
390	Janet Duprey	Janet	\N	Duprey	\N	Assembly Member	\N	t	564_janet_duprey.jpg
440	Dan Quart	Dan	\N	Quart	\N	Assembly Member	\N	t	618_dan_quart.jpg
383	Clifford Crouch	Clifford	\N	Crouch	\N	Assembly Member	\N	t	557_clifford_crouch.jpg
472	Al Graf	Al		Graf				t	651_al_graf.jpg
415	Stephen Hawley	Stephen	\N	Hawley	\N	Assembly Member	\N	t	589_stephen_hawley.jpg
290	Charles Lavine	Charles	\N	Lavine	\N	Assembly Member	\N	t	474_charles_lavine.jpg
453	Kenneth Blankenbush	Kenneth	\N	Blankenbush	\N	Assembly Member	\N	t	631_kenneth_blankenbush.jpg
363	J. Gary Pretlow	J.	Gary	Pretlow	\N	Assembly Member	\N	t	539_j._gary_pretlow.jpg
481	Ron Kim	Ron		Kim	\N	Assemblymember		t	659_ron_kim.jpg
434	Aravella Simotas	Aravella	\N	Simotas	\N	Assembly Member	\N	t	612_aravella_simotas.jpg
724	Latoya Joyner	Latoya	\N	Joyner	\N	\N	\N	t	902_latoya_joyner.jpg
314	Catherine Nolan	Catherine	\N	Nolan	\N	Assembly Member	\N	t	491_catherine_nolan.jpg
316	Inez Barron	Inez	\N	Barron	\N	Assembly Member	\N	t	493_inez_barron.jpg
442	Mark Gjonaj	Mark	\N	Gjonaj	\N	Assembly Member	\N	t	620_mark_gjonaj.jpg
387	William Magee	William	\N	Magee	\N	Assembly Member	\N	t	561_william_magee.jpg
486	Bill Nojay	Bill		Nojay	\N	Assemblymember		t	664_bill_nojay.jpg
457	Philip Palmesano	Philip	\N	Palmesano	\N	Assembly Member	\N	t	635_philip_palmesano.jpg
397	Albert A. Stirpe	Albert	A.	Stirpe	\N	Assembly Member	\N	t	571_albert_a._stirpe.jpg
332	Annette Robinson	Annette	\N	Robinson	\N	Assembly Member	\N	t	509_annette_robinson.jpg
306	William Scarborough	William	\N	Scarborough	\N	Assembly Member	\N	t	489_william_scarborough.jpg
721	Michael Blake	Michael	\N	Blake	\N	\N	\N	t	899_michael_blake.jpg
467	Keith L.T. Wright	Keith	L.T.	Wright				t	646_keith_l.t._wright.jpg
729	Jo Anne Simon	Jo Anne	\N	Simon	\N	\N	\N	t	907_jo_anne_simon.jpg
482	Kieran Michael Lalor	Kieran	Michael	Lalor	\N	Assemblymember		t	660_kieran_michael_lalor.jpg
489	Victor M. Pichardo	Victor	M.	Pichardo	\N	Assemblymember		t	667_victor_m._pichardo.jpg
494	James Skoufis	James		Skoufis	\N	Assemblymember		t	672_james_skoufis.jpg
430	Edward Ra	Edward	\N	Ra	\N	Assembly Member	\N	t	608_edward_ra.jpg
307	Margaret Markey	Margaret	\N	Markey	\N	Assembly Member	\N	t	490_margaret_markey.jpg
496	Dan Stec	Dan		Stec	\N	Assemblymember		t	674_dan_stec.jpg
386	James Tedisco	James	\N	Tedisco	\N	Assembly Member	\N	t	560_james_tedisco.jpg
435	Francisco Moya	Francisco	\N	Moya	\N	Assembly Member	\N	t	613_francisco_moya.jpg
448	Steven McLaughlin	Steven	\N	McLaughlin	\N	Assembly Member	\N	t	626_steven_mclaughlin.jpg
741	CANCEL	\N		CANCEL				f	\N
727	Roxanne Persaud	Roxanne	\N	Persaud	\N	\N	\N	t	905_roxanne_persaud.jpg
433	Michael Simanowitz	Michael	\N	Simanowitz	\N	Assembly Member	\N	t	611_michael_simanowitz.jpg
708	Carrie Woerner	Carrie	\N	Woerner	\N	\N	\N	t	886_carrie_woerner.jpg
733	Angela Wozniak	Angela	\N	Wozniak 	\N	\N	\N	t	911_angela_wozniak.jpg
726	Guillermo Linares	Guillermo	\N	Linares	\N	\N	\N	t	904_guillermo_linares.jpg
744	PEOPLES-STOKE	\N		PEOPLES-STOKE				f	\N
745	G GOTTFRIED	G		GOTTFRIED				f	\N
437	Nicole Malliotakis	Nicole	\N	Malliotakis	\N	Assembly Member	\N	t	615_nicole_malliotakis.jpg
456	Mark C. Johns	Mark	C.	Johns	\N	Assembly Member	\N	t	634_mark_c._johns.jpg
731	Peter Lawrence	Peter	\N	Lawrence	\N	\N	\N	t	909_peter_lawrence.jpg
492	Angelo Santabarbara	Angelo		Santabarbara	\N	Assemblymember		t	670_angelo_santabarbara.jpg
460	Sean Ryan	Sean	\N	Ryan	\N	Assembly Member	\N	t	638_sean_ryan.jpg
445	Shelley Mayer	Shelley	\N	Mayer	\N	Assembly Member	\N	t	623_shelley_mayer.jpg
495	Michaelle C. Solages	Michaelle	C.	Solages	\N	Assemblymember		t	673_michaelle_c._solages.jpg
948	Michael J. Norris	Michael	J.	Norris	norrism@nyassembly.gov	Assembly Member		t	no_image.jpg
400	William A. Barclay	William	A.	Barclay	\N	Assembly Member	\N	t	574_william_a._barclay.jpg
722	Erik Dilan	Erik	\N	Dilan	\N	\N	\N	t	900_erik_dilan.jpg
487	Steven Otis	Steven		Otis	\N	Assemblymember		t	665_steven_otis.jpg
728	Rebecca Seawright	Rebecca	\N	Seawright	\N	\N	\N	t	906_rebecca_seawright.jpg
488	Anthony H. Palumbo	Anthony	H.	Palumbo	\N	Assemblymember		t	666_anthony_h._palumbo.jpg
734	Diana Richardson	Diana	\N	Richardson	\N	Assembly Member	\N	t	\N
463	Raymond Walter	Raymond	\N	Walter	\N	Assembly Member	\N	t	641_raymond_walter.jpg
736	Roxanne J Persaud	Roxanne	J	Persaud	persaud@nysenate.gov			t	
240	Ruben Diaz	Ruben	\N	Diaz	diaz@nysenate.gov	Senator	Sr	t	421_ruben_diaz.jpg
720	Rodneyse Bichotte	Rodneyse	\N	Bichotte	\N	\N	\N	t	898_rodneyse_bichotte.jpg
723	Kimberly Jean-Pierre	Kimberly	\N	Jean-Pierre	\N	Assembly Member	\N	t	901_jean-pierre.jpg
484	John T. McDonald III	John	T.	McDonald	\N	Assemblymember	III	t	662_john_t._mcdonald_iii.jpg
294	Thomas McKevitt	Thomas	\N	McKevitt	\N	Assembly Member	\N	t	478_thomas_mckevitt.jpg
491	Nily Rozic	Nily		Rozic	\N	Assemblymember		t	669_nily_rozic.jpg
941	Brian Barnwell	Brian		Barnwell		Assembly Member		t	no_image.jpg
773	Pamela Helming	Pamela		Helming	Helming@nysenate.gov	Senator		t	no_image.jpg
829	Ron Castorina, Jr.	Ron		Castorina	castorinar@nyassembly.gov	Assembly Member	Jr.	t	no_image.jpg
256	Kathleen A. Marchione	Kathleen	A.	Marchione	marchione@nysenate.gov	Senator	\N	t	435_kathleen_a._marchione.jpg
259	Brad Hoylman	Brad	\N	Hoylman	hoylman@nysenate.gov	Senator	\N	t	438_brad_hoylman.jpg
393	Marc Butler	Marc	\N	Butler	\N	Assembly Member	\N	t	567_marc_butler.jpg
396	William Magnarelli	William	\N	Magnarelli	\N	Assembly Member	\N	t	570_william_magnarelli.jpg
399	Gary Finch	Gary	\N	Finch	\N	Assembly Member	\N	t	573_gary_finch.jpg
402	Donna Lupardo	Donna	\N	Lupardo	\N	Assembly Member	\N	t	576_donna_lupardo.jpg
405	Brian Kolb	Brian	Kolb	Kolb	\N	Assembly Member	\N	t	579_brian_kolb.jpg
279	Fred Thiele	Fred	\N	Thiele	\N	Assembly Member	\N	t	464_fred_thiele.jpg
408	Joseph Morelle	Joseph	\N	Morelle	\N	Assembly Member	\N	t	582_joseph_morelle.jpg
409	David Gantt	David	\N	Gantt	\N	Assembly Member	\N	t	583_david_gantt.jpg
281	Steven Englebright	Steven	\N	Englebright	\N	Assembly Member	\N	t	466_steven_englebright.jpg
286	Andrew Raia	Andrew		Raia	\N	Assembly Member	\N	t	462_andrew_raia.jpg
416	Robin Schimminger	Robin	\N	Schimminger	\N	Assembly Member	\N	t	590_robin_schimminger.jpg
289	Joseph Saladino	Joseph	\N	Saladino	\N	Assembly Member	\N	t	473_joseph_saladino.jpg
417	Crystal Peoples-Stokes	Crystal	\N	Peoples-Stokes	\N	Assembly Member	\N	t	591_crystal_peoples-stokes.jpg
292	Michael Montesano	Michael	\N	Montesano	\N	Assembly Member	\N	t	476_michael_montesano.jpg
295	Earlene Hooper	Earlene	\N	Hooper	\N	Assembly Member	\N	t	479_earlene_hooper.jpg
296	David McDonough	David	\N	McDonough	\N	Assembly Member	\N	t	480_david_mcdonough.jpg
301	David Weprin	David	\N	Weprin	\N	Assembly Member	\N	t	484_david_weprin.jpg
305	Andrew Hevesi	Andrew	\N	Hevesi	\N	Assembly Member	\N	t	488_andrew_hevesi.jpg
308	Michele Titus	Michele	\N	Titus	\N	Assembly Member	\N	t	456_michele_titus.jpg
309	Vivian Cook	Vivian	Cook	Cook	\N	Assembly Member	\N	t	457_vivian_cook.jpg
438	Robert J. Rodriguez	Robert	J.	Rodriguez	\N	Assembly Member	\N	t	616_robert_j._rodriguez.jpg
312	Jeffrion Aubry	Jeffrion	\N	Aubry	\N	Assembly Member	\N	t	460_jeffrion_aubry.jpg
443	Sandy Galef	Sandy	\N	Galef	\N	Assembly Member	\N	t	621_sandy_galef.jpg
444	Thomas Abinanti	Thomas	\N	Abinanti	\N	Assembly Member	\N	t	622_thomas_abinanti.jpg
190	James L. Seward	James	L.	Seward	seward@senate.state.ny.us	Senator	\N	t	371_james_l._seward.jpg
191	Neil D. Breslin	Neil	D.	Breslin	breslin@senate.state.ny.us	Senator	\N	t	372_neil_d._breslin.jpg
447	Didi Barrett	Didi	\N	Barrett	\N	Assembly Member	\N	t	625_didi_barrett.jpg
192	José M. Serrano	José	M.	Serrano	serrano@senate.state.ny.us	Senator	\N	t	373_jose_m._serrano.jpg
321	Steven Cymbrowitz	Steven	\N	Cymbrowitz	\N	Assembly Member	\N	t	498_steven_cymbrowitz.jpg
194	Bill Perkins	Bill	\N	Perkins	perkins@senate.state.ny.us	Senator	\N	t	375_bill_perkins.jpg
450	Anthony Brindisi	Anthony	\N	Brindisi	\N	Assembly Member	\N	t	628_anthony_brindisi.jpg
707	Robert Ortt	Robert	\N	Ortt	\N	\N	\N	t	885_robert_ortt.jpg
195	John A. DeFrancisco	John	A.	DeFrancisco	jdefranc@senate.state.ny.us	Senator	\N	t	376_john_a._defrancisco.jpg
323	William Colton	William	\N	Colton	\N	Assembly Member	\N	t	500_william_colton.jpg
196	Gustavo Rivera	Gustavo	\N	Rivera	grivera@nysenate.gov	Senator	\N	t	377_gustavo_rivera.jpg
709	Rich Funke	Rich	\N	Funke	\N	\N	\N	t	887_rich_funke.jpg
325	Peter Abbate	Peter	\N	Abbate	\N	Assembly Member	\N	t	502_peter_abbate.jpg
198	Betty Little	Betty	\N	Little	little@senate.state.ny.us	Senator	\N	t	379_betty_little.jpg
710	Leroy Comrie	Leroy	\N	Comrie	\N	\N	\N	t	888_leroy_comrie.jpg
327	Felix Ortiz	Felix	\N	Ortiz	\N	Assembly Member	\N	t	504_felix_ortiz.jpg
711	Jesse Hamilton	Jesse	\N	Hamilton	\N	\N	\N	t	889_jesse_hamilton.jpg
200	Velmanette Montgomery	Velmanette	\N	Montgomery	montgome@senate.state.ny.us	Senator	\N	t	381_velmanette_montgomery.jpg
202	Michael Gianaris	Michael	\N	Gianaris	gianaris@nysenate.gov	Senator	\N	t	383_michael_gianaris.jpg
714	George Amedore	George	\N	Amedore	\N	\N	\N	t	892_george_amedore.jpg
715	Thomas Croci	Thomas	\N	Croci	\N	\N	\N	t	893_thomas_croci.jpg
203	Joseph P. Addabbo Jr.	Joseph	P.	Addabbo	addabbo@senate.state.ny.us	Senator	Jr.	t	384_joseph_p._addabbo_jr..jpg
718	Latrice Walker	Latrice	\N	Walker	\N	\N	\N	t	896_latrice_walker.jpg
334	N. Nick Perry	N.	Nick	Perry	\N	Assembly Member	\N	t	511_n._nick_perry.jpg
210	Tony Avella	Tony	\N	Avella	avella@nysenate.gov	Senator	\N	t	391_tony_avella.jpg
339	Michael Cusick	Michael	\N	Cusick	\N	Assembly Member	\N	t	516_michael_cusick.jpg
725	Todd Kaminsky	Todd	\N	Kaminsky	\N	\N	\N	t	903_todd_kaminsky.jpg
214	David J. Valesky	David	J.	Valesky	valesky@senate.state.ny.us	Senator	\N	t	395_david_j._valesky.jpg
342	Deborah Glick	Deborah	\N	Glick	\N	Assembly Member	\N	t	519_deborah_glick.jpg
343	Linda Rosenthal	Linda	\N	Rosenthal	\N	Assembly Member	\N	t	520_linda_rosenthal.jpg
219	Toby Ann Stavisky	Toby	\N	Stavisky	stavisky@senate.state.ny.us	Senator	\N	t	400_toby_ann_stavisky.jpg
347	Herman D. Farrell	Herman	D.	Farrell	\N	Assembly Member	\N	t	524_herman_d._farrell.jpg
732	Dean Murray	Dean	\N	Murray	\N	\N	\N	t	910_dean_murray.jpg
220	Liz Krueger	Liz	\N	Krueger	lkrueger@senate.state.ny.us	Senator	\N	t	401_liz_krueger.jpg
222	Diane J. Savino	Diane	J.	Savino	savino@senate.state.ny.us	Senator	\N	t	403_diane_j._savino.jpg
350	Brian Kavanagh	Brian	\N	Kavanagh	\N	Assembly Member	\N	t	526_brian_kavanagh.jpg
478	David DiPietro	David		DiPietro	\N	Assembly Member		t	656_david_dipietro.jpg
735	Fredrick J Akshar II	Fredrick	J	Akshar	akshar@nysenate.gov		II	t	
225	Michael H. Ranzenhofer	Michael	H.	Ranzenhofer	ranz@senate.state.ny.us	Senator	\N	t	406_michael_h._ranzenhofer.jpg
358	Michael Benedetto	Michael	\N	Benedetto	\N	Assembly Member	\N	t	534_michael_benedetto.jpg
361	Marcos Crespo	Marcos	\N	Crespo	\N	Assembly Member	\N	t	537_marcos_crespo.jpg
364	Amy Paulin	Amy	\N	Paulin	\N	Assembly Member	\N	t	540_amy_paulin.jpg
242	Patty Ritchie	Patty	\N	Ritchie	ritchie@nysenate.gov	Senator	\N	t	423_patty_ritchie.jpg
374	Aileen Gunther	Aileen	\N	Gunther	\N	Assembly Member	\N	t	549_aileen_gunther.jpg
376	Frank Skartados 	Frank	\N	Skartados	\N	Assembly Member	\N	t	550_frank_skartados_.jpg
251	James Sanders Jr.	James	\N	Sanders	sanders@nysenate.gov	Senator	Jr.	t	432_james_sanders_jr..jpg
351	Richard Gottfried	Richard	\N	Gottfried	\N	Assembly Member	\N	t	527_richard_gottfried.jpg
352	Peter Rivera	Peter	\N	Rivera	\N	Assembly Member	\N	t	528_peter_rivera.jpg
357	Jeffrey Dinowitz	Jeffrey	\N	Dinowitz	\N	Assembly Member	\N	t	533_jeffrey_dinowitz.jpg
231	Daniel L. Squadron	Daniel	L.	Squadron	squadron@nysenate.gov	Senator	\N	t	412_daniel_l._squadron.jpg
234	Thomas F. O'Mara	Thomas	F.	O'Mara	omara@nysenate.gov	Senator	\N	t	415_thomas_f._o'mara.jpg
497	Phil Steck	Phil		Steck	\N	Assembly Member		t	675_phil_steck.jpg
371	Ellen C. Jaffee	Ellen	C.	Jaffee	\N	Assembly Member	\N	t	546_ellen_c._jaffee.jpg
246	Patrick M. Gallivan	Patrick	M.	Gallivan	gallivan@nysenate.gov	Senator	\N	t	427_patrick_m._gallivan.jpg
248	Timothy M. Kennedy	Timothy	M.	Kennedy	kennedy@nysenate.gov	Senator	\N	t	429_timothy_m._kennedy.jpg
254	Phil Boyle	Phil	\N	Boyle	pboyle@nysenate.gov	Senator	\N	t	433_phil_boyle.jpg
233	Catharine Young	Catharine	\N	Young	cyoung@senate.state.ny.us	Senator	\N	t	414_catharine_young.jpg
235	Kevin S. Parker	Kevin	S.	Parker	parker@senate.state.ny.us	Senator	\N	t	416_kevin_s._parker.jpg
241	Martin J. Golden	Martin	J.	Golden	golden@senate.state.ny.us	Senator	\N	t	422_martin_j._golden.jpg
245	Martin Malavé Dilan	Martin	\N	Dilan	dilan@nysenate.gov	Senator	\N	t	426_martin_malave_dilan.jpg
247	José Peralta	José	\N	Peralta	jperalta@nysenate.gov	Senator	\N	t	428_josé_peralta.jpg
377	Kevin Cahill	Kevin	\N	Cahill	\N	Assembly Member	\N	t	551_kevin_cahill.jpg
255	George Latimer	George	\N	Latimer	latimer@nysenate.gov	Senator	\N	t	434_george_latimer.jpg
959	Christine Pellegrino	Christine		Pellegrino	pellegrinoc@nyassembly.gov	Assembly Member		t	no_image.jpg
961	Daniel Rosenthal	Daniel	\N	Rosenthal	\N	Assembly Member		t	no_image.jpg
\.


--
-- Data for Name: member; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY member (id, person_id, chamber, incumbent, full_name) FROM stdin;
454	276	senate	f	Hiram Monserrate
492	315	assembly	t	Michael G. Miller
458	310	assembly	f	Barbara Clark
514	337	assembly	t	Matthew Titone
461	299	assembly	f	Grace Meng
578	404	assembly	t	Robert Oaks
463	278	assembly	f	Marc Alessi
522	345	assembly	t	Daniel O'Donnell
465	280	assembly	f	L. Dean Murray
459	311	assembly	t	Michael DenDekker
467	282	assembly	f	Ginny Fields
471	287	assembly	f	James Conte
472	288	assembly	f	Robert Sweeney
530	354	assembly	t	Jose Rivera
475	291	assembly	f	Robert Barra
468	283	assembly	t	Philip Ramos
477	293	assembly	f	Michelle Schimel
469	284	assembly	t	Michael J. Fitzpatrick
536	360	assembly	t	Carmen E. Arroyo
481	297	assembly	f	Harvey Weisenberg
482	298	assembly	f	Thomas Alfano
483	300	assembly	f	Audrey Pheffer
545	370	assembly	t	Kenneth Zebrowski
485	302	assembly	f	Rory Lancman
486	303	assembly	f	Ann-Margaret Carrozza
487	304	assembly	f	Nettie Mayersohn
557	383	assembly	t	Clifford Crouch
489	306	assembly	f	William Scarborough
490	307	assembly	f	Margaret Markey
495	318	assembly	f	Rhoda Jacobs
496	319	assembly	f	Karim Camara
497	320	assembly	f	James F. Brennan
561	387	assembly	t	William Magee
499	322	assembly	f	Alec Brook-Krasny
501	324	assembly	t	Dov Hikind
503	326	assembly	t	Joseph Lentol
571	397	assembly	t	Albert A. Stirpe
505	328	assembly	f	Joan Millman
506	329	assembly	f	Vito Lopez
507	330	assembly	f	Darryl Towns
508	331	assembly	f	"William Boyland
509	332	assembly	f	Annette Robinson
510	333	assembly	f	Hakeem Jeffries
575	401	assembly	t	Barbara Lifton
512	335	assembly	f	Alan Maisel
513	336	assembly	f	Janele Hyer-Spencer
515	338	assembly	f	Louis Tobacco
589	415	assembly	t	Stephen Hawley
517	340	assembly	f	Sheldon Silver
518	341	assembly	f	Micah Kellner
370	189	senate	t	David Carlucci
474	290	assembly	t	Charles Lavine
521	344	assembly	f	Adam Clayton Powell IV
539	363	assembly	t	J. Gary Pretlow
525	349	assembly	f	Jonathan Bing
574	400	assembly	t	William A. Barclay
1096	918	senate	t	Marisol Alcantara
1099	921	senate	t	Chris Jacobs
529	353	assembly	f	Vanessa Gibson
531	355	assembly	f	Michael Benjamin
532	356	assembly	f	Naomi Rivera
1100	922	senate	t	Elaine Phillips
1101	923	senate	t	John E. Brooks
1102	924	senate	t	Jamaal Bailey
538	362	assembly	f	Nelson Castro
1103	925	senate	t	James Tedisco
541	365	assembly	f	Robert Castelli
543	368	assembly	f	Richard Brodsky
544	369	assembly	f	Mike Spano
580	406	assembly	f	Joseph Errigo 
547	372	assembly	f	Nancy Calhoun
548	373	assembly	f	Ann Rabbitt
535	359	assembly	f	Carl Heastie
1120	942	assembly	f	Billy Jones
552	378	assembly	f	Joel Miller
553	379	assembly	f	Marcus Molinaro
554	380	assembly	f	John McEneny
555	381	assembly	f	George Amedore
556	382	assembly	f	Ronald Canestrari
558	384	assembly	f	Timothy P. Gordon
559	385	assembly	f	Robert Reilly
560	386	assembly	f	James Tedisco
562	388	assembly	f	Tony Jordan
563	389	assembly	f	Teresa Sayward
564	390	assembly	f	Janet Duprey
565	391	assembly	f	David Townsend
566	392	assembly	f	RoAnn Destito
569	395	assembly	f	Joan Christensen
1118	940	assembly	f	Clyde Vanel
572	398	assembly	f	Dierdre Scozzafava
1105	927	assembly	f	Pamela J. Hunter
1089	911	assembly	f	Alicia Hyndman
577	403	assembly	f	Peter Lopez
568	394	assembly	t	Addie Jenne
581	407	assembly	f	Susan John
584	410	assembly	f	Bill Reilich
585	411	assembly	f	David Koon
586	412	assembly	f	James Bacalles
587	413	assembly	f	Tom O'Mara
588	414	assembly	f	Francine DelMonte
369	188	senate	f	John L. Sampson
1115	937	assembly	t	Melissa Miller
491	314	assembly	t	Catherine Nolan
1076	898	assembly	f	Pamela Harris
493	316	assembly	t	Inez Barron
494	317	assembly	t	Helene Weinstein
478	294	assembly	t	Thomas McKevitt
390	209	senate	t	Jeffrey D. Klein
419	238	senate	t	Kenneth P. LaValle
417	236	senate	t	Joseph E. Robach
374	193	senate	t	William J. Larkin Jr.
439	261	senate	t	Simcha Felder
424	243	senate	t	Kemp Hannon
393	212	senate	t	Joseph A. Griffo
413	232	senate	t	John J. Bonacic
642	464	assembly	t	Andrew Goodell
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
455	277	senate	f	David Storobin
592	418	assembly	f	Jane Corwin
593	419	assembly	f	Dennis H. Gabryszak
594	420	assembly	f	Sam Hoyt
595	421	assembly	f	Mark J.F. Schroeder
596	422	assembly	f	Jack Quinn III
597	423	assembly	f	Daniel Burling
598	424	assembly	f	James Hayes
600	426	assembly	f	William Parment
601	227	assembly	f	Greg Ball
602	204	assembly	f	Adriano Espaillat
603	202	assembly	f	Michael Gianaris
604	255	assembly	f	George Latimer
605	427	assembly	f	Daniel P. Losquadro
609	431	assembly	f	Phillip Goldfeder
614	436	assembly	f	Rafael Espinal
637	459	assembly	f	John Ceretto
640	462	assembly	f	Kevin Smardz
617	439	assembly	f	Guillermo Linares
619	441	assembly	f	Eric Stevenson
624	446	assembly	f	Steve Katz
627	449	assembly	f	Claudia Tenney
629	451	assembly	f	Sam Roberts
630	452	assembly	f	Donald R. Miller
632	454	assembly	f	Sean T. Hanna
470	254	assembly	f	Phil Boyle
607	429	assembly	t	Brian F. Curran
608	430	assembly	t	Edward Ra
613	435	assembly	t	Francisco Moya
618	440	assembly	t	Dan Quart
633	455	assembly	t	Harry B. Bronson
635	457	assembly	t	Philip Palmesano
636	458	assembly	t	Christopher Friend
385	204	senate	f	Adriano Espaillat
380	199	senate	f	Ruth Hassell-Thompson
410	229	senate	f	Tom Libous
404	223	senate	f	Lee M. Zeldin
430	249	senate	f	Dean G. Skelos
418	237	senate	f	Hugh T. Farley
405	224	senate	f	Malcolm A. Smith
437	258	senate	f	Terry Gipson
436	257	senate	f	Ted O'Brien
408	227	senate	f	Greg Ball
398	217	senate	f	Mark Grisanti
431	250	senate	f	Cecilia Tkaczyk
397	216	senate	f	Jack M. Martins
411	230	senate	f	George D. Maziarz
394	213	senate	f	Michael F. Nozzolio
639	461	assembly	t	Mickey Kearns
626	448	assembly	t	Steven McLaughlin
611	433	assembly	t	Michael Simanowitz
599	425	assembly	t	Joseph Giglio
615	437	assembly	t	Nicole Malliotakis
634	456	assembly	t	Mark C. Johns
638	460	assembly	t	Sean Ryan
433	254	senate	t	Phil Boyle
623	445	assembly	t	Shelley Mayer
641	463	assembly	t	Raymond Walter
631	453	assembly	t	Kenneth Blankenbush
421	240	senate	t	Ruben Diaz
407	226	senate	t	Carl L Marcellino
409	228	senate	t	Andrew J Lanza
643	247	assembly	f	José Peralta
644	465	assembly	f	Adam Bradley
645	466	assembly	f	Patricia Eddington
647	468	assembly	f	Rob Walker
648	469	assembly	f	Anthony Seminerio
650	471	assembly	f	Ruben Diaz Jr.
652	473	assembly	f	Thomas Kirwan
653	475	assembly	f	Joseph Borelli
396	215	senate	t	Andrea Stewart-Cousins
664	486	assembly	f	Bill Nojay
668	490	assembly	f	Gabriela Rosa
676	498	assembly	f	Greene
678	500	assembly	f	WEISENBERGT
399	218	senate	t	John J. Flanagan
680	502	assembly	f	PEOPLES-STOKE
681	503	assembly	f	B GOTTFRIED
682	504	assembly	f	RULES
683	505	assembly	f	MAGNARELLIS
684	506	assembly	f	PEOPLES-STOKE
685	507	assembly	f	M WEINSTEIN
686	508	assembly	f	CYMBROWITZSNY
687	509	assembly	f	I ZEBROWSKI
688	510	assembly	f	E WEINSTEIN
689	511	assembly	f	BRAUNSTEINWITZ
690	512	assembly	f	BRAUNSTEINZ
1116	938	assembly	f	Jaime R. Williams
610	432	assembly	t	Edward Braunstein
612	434	assembly	t	Aravella Simotas
620	442	assembly	t	Mark Gjonaj
890	712	senate	t	Susan Serino
891	713	senate	t	Terrence P. Murphy
899	721	assembly	t	Michael Blake
646	467	assembly	t	Keith L.T. Wright
907	729	assembly	t	Jo Anne Simon
908	730	assembly	t	Karl Brabenec
654	476	assembly	t	David Buchwald
655	477	assembly	t	Maritza Davila
657	479	assembly	t	Patricia Fahy
658	480	assembly	t	Andrew R. Garbarino
660	482	assembly	t	Kieran Michael Lalor
661	483	assembly	t	Chad A. Lupinacci
663	485	assembly	t	Walter T. Mosley
904	726	assembly	f	Guillermo Linares
677	499	assembly	f	Edward Hennessey
696	518	assembly	f	O BENEDETTO
903	725	assembly	f	Todd Kaminsky
923	745	assembly	f	G GOTTFRIED
895	717	senate	f	Michael Venditto
911	733	assembly	f	Angela Wozniak
691	513	assembly	f	STRIPE
922	744	assembly	f	PEOPLES-STOKE
894	716	senate	f	Marc Panepinto
697	519	assembly	f	R BENEDETTO
693	515	assembly	f	S O'DONNELL
905	727	assembly	f	Roxanne Persaud
694	516	assembly	f	T WEINSTEIN
698	520	assembly	f	L O'DONNELL
695	517	assembly	f	B O'DONNELL
919	741	assembly	f	CANCEL
667	489	assembly	t	Victor M. Pichardo
671	493	assembly	t	Luis R. Sepúlveda
672	494	assembly	t	James Skoufis
674	496	assembly	t	Dan Stec
828	650	assembly	t	Peter Lopez
673	495	assembly	t	Michaelle C. Solages
886	708	assembly	t	Carrie Woerner
651	472	assembly	t	Al Graf
909	731	assembly	t	Peter Lawrence
670	492	assembly	t	Angelo Santabarbara
912	734	assembly	t	Diana Richardson
900	722	assembly	t	Erik Dilan
665	487	assembly	t	Steven Otis
906	728	assembly	t	Rebecca Seawright
666	488	assembly	t	Anthony H. Palumbo
914	736	senate	t	Roxanne J Persaud
951	773	senate	t	Pamela Helming
898	720	assembly	t	Rodneyse Bichotte
659	481	assembly	t	Ron Kim
901	723	assembly	t	Kimberly Jean-Pierre
902	724	assembly	t	Latoya Joyner
662	484	assembly	t	John T. McDonald III
669	491	assembly	t	Nily Rozic
583	409	assembly	t	David Gantt
456	308	assembly	t	Michele Titus
457	309	assembly	t	Vivian Cook
460	312	assembly	t	Jeffrion Aubry
590	416	assembly	t	Robin Schimminger
462	286	assembly	t	Andrew Raia
591	417	assembly	t	Crystal Peoples-Stokes
464	279	assembly	t	Fred Thiele
466	281	assembly	t	Steven Englebright
473	289	assembly	t	Joseph Saladino
476	292	assembly	t	Michael Montesano
479	295	assembly	t	Earlene Hooper
480	296	assembly	t	David McDonough
484	301	assembly	t	David Weprin
616	438	assembly	t	Robert J. Rodriguez
488	305	assembly	t	Andrew Hevesi
621	443	assembly	t	Sandy Galef
622	444	assembly	t	Thomas Abinanti
625	447	assembly	t	Didi Barrett
498	321	assembly	t	Steven Cymbrowitz
371	190	senate	t	James L. Seward
500	323	assembly	t	William Colton
628	450	assembly	t	Anthony Brindisi
372	191	senate	t	Neil D. Breslin
373	192	senate	t	José M. Serrano
885	707	senate	t	Robert Ortt
502	325	assembly	t	Peter Abbate
375	194	senate	t	Bill Perkins
887	709	senate	t	Rich Funke
504	327	assembly	t	Felix Ortiz
376	195	senate	t	John A. DeFrancisco
888	710	senate	t	Leroy Comrie
377	196	senate	t	Gustavo Rivera
889	711	senate	t	Jesse Hamilton
379	198	senate	t	Betty Little
892	714	senate	t	George Amedore
381	200	senate	t	Velmanette Montgomery
893	715	senate	t	Thomas Croci
511	334	assembly	t	N. Nick Perry
383	202	senate	t	Michael Gianaris
1129	951	assembly	t	Carmen De La Rosa
1130	952	assembly	t	Anthony D'Urso
1131	953	assembly	t	Stacey Pheffer Amato
1123	945	assembly	f	Kevin M. Byrne
1132	954	assembly	f	Yuh-Line Niou
1119	941	assembly	f	Brian Barnwell
1007	829	assembly	f	Ron Castorina, Jr.
1133	955	assembly	f	Monica P. Wallace
1134	956	assembly	t	Tremaine Wright
1135	957	assembly	f	Angelo J. Morinello
1136	958	senate	f	Brian Benjamin
1122	944	assembly	f	Robert C. Carroll
1125	947	assembly	f	Brian D. Miller
1126	948	assembly	f	Michael J. Norris
1127	949	assembly	f	Mary Beth Walsh
1128	950	assembly	f	Inez E. Dickens
896	718	assembly	t	Latrice Walker
384	203	senate	t	Joseph P. Addabbo Jr.
516	339	assembly	t	Michael Cusick
519	342	assembly	t	Deborah Glick
391	210	senate	t	Tony Avella
520	343	assembly	t	Linda Rosenthal
528	352	assembly	f	Peter Rivera
395	214	senate	t	David J. Valesky
524	347	assembly	t	Herman D. Farrell
910	732	assembly	t	Dean Murray
526	350	assembly	t	Brian Kavanagh
527	351	assembly	t	Richard Gottfried
400	219	senate	t	Toby Ann Stavisky
656	478	assembly	t	David DiPietro
401	220	senate	t	Liz Krueger
913	735	senate	t	Fredrick J Akshar II
403	222	senate	t	Diane J. Savino
533	357	assembly	t	Jeffrey Dinowitz
406	225	senate	t	Michael H. Ranzenhofer
918	725	senate	t	Todd Kaminsky
534	358	assembly	t	Michael Benedetto
537	361	assembly	t	Marcos Crespo
412	231	senate	t	Daniel L. Squadron
540	364	assembly	t	Amy Paulin
414	233	senate	t	Catharine Young
415	234	senate	t	Thomas F. O'Mara
416	235	senate	t	Kevin S. Parker
546	371	assembly	t	Ellen C. Jaffee
675	497	assembly	t	Phil Steck
549	374	assembly	t	Aileen Gunther
550	376	assembly	t	Frank Skartados 
422	241	senate	t	Martin J. Golden
423	242	senate	t	Patty Ritchie
551	377	assembly	t	Kevin Cahill
426	245	senate	t	Martin Malavé Dilan
427	246	senate	t	Patrick M. Gallivan
428	247	senate	t	José Peralta
429	248	senate	t	Timothy M. Kennedy
432	251	senate	t	James Sanders Jr.
434	255	senate	t	George Latimer
435	256	senate	t	Kathleen A. Marchione
438	259	senate	t	Brad Hoylman
567	393	assembly	t	Marc Butler
570	396	assembly	t	William Magnarelli
573	399	assembly	t	Gary Finch
576	402	assembly	t	Donna Lupardo
579	405	assembly	t	Brian Kolb
582	408	assembly	t	Joseph Morelle
1137	959	assembly	t	Christine Pellegrino
1139	961	assembly	t	Daniel Rosenthal
1140	350	senate	t	Brian Kavanagh
\.


--
-- Data for Name: session_member; Type: TABLE DATA; Schema: public; Owner: postgres
--

COPY session_member (id, member_id, lbdc_short_name, session_year, district_code, alternate) FROM stdin;
1	369	SAMPSON	2009	19	f
2	441	MORAHAN	2009	38	f
3	371	SEWARD	2009	51	f
4	372	BRESLIN	2009	46	f
5	373	SERRANO	2009	28	f
6	374	LARKIN	2009	39	f
7	375	PERKINS	2009	30	f
8	376	DEFRANCISCO	2009	50	f
9	442	ESPADA	2009	33	f
10	378	HUNTLEY	2009	10	f
11	379	LITTLE	2009	45	f
12	380	HASSELL-THOMPSON	2009	36	f
13	381	MONTGOMERY	2009	18	f
14	382	ADAMS	2009	20	f
15	443	ONORATO	2009	12	f
16	384	ADDABBO	2009	15	f
17	444	SCHNEIDERMAN	2009	31	f
18	387	DUANE	2009	29	f
19	388	OPPENHEIMER	2009	37	f
20	389	MCDONALD	2009	43	f
21	390	KLEIN	2009	34	f
22	445	PADAVAN	2009	11	f
23	392	ALESI	2009	55	f
24	393	GRIFFO	2009	47	f
25	394	NOZZOLIO	2009	54	f
26	395	VALESKY	2009	49	f
27	396	STEWART-COUSINS	2009	35	f
28	447	THOMPSON	2009	60	f
29	399	FLANAGAN	2009	2	f
30	400	STAVISKY	2009	16	f
31	401	KRUEGER	2009	26	f
32	402	SALAND	2009	41	f
33	403	SAVINO	2009	23	f
34	448	FOLEY	2009	3	f
35	405	SMITH	2009	14	f
36	406	RANZENHOFER	2009	61	f
37	407	MARCELLINO	2009	5	f
38	449	LEIBELL	2009	40	f
39	409	LANZA	2009	24	f
40	410	LIBOUS	2009	52	f
41	411	MAZIARZ	2009	62	f
42	412	SQUADRON	2009	25	f
43	413	BONACIC	2009	42	f
44	414	YOUNG	2009	57	f
45	450	WINNER	2009	53	f
46	416	PARKER	2009	21	f
47	417	ROBACH	2009	56	f
48	418	FARLEY	2009	44	f
49	419	LAVALLE	2009	1	f
50	420	KRUGER	2009	27	f
51	421	DIAZ	2009	32	f
52	422	GOLDEN	2009	22	f
53	451	AUBERTINE	2009	48	f
54	424	HANNON	2009	6	f
55	425	FUSCHILLO	2009	8	f
56	426	DILAN	2009	17	f
57	452	VOLKER	2009	59	f
58	428	PERALTA	2009	13	f
59	453	STACHOWSKI	2009	58	f
60	430	SKELOS	2009	9	f
61	386	JOHNSON O	2009	4	f
62	446	JOHNSON C	2009	7	f
63	369	SAMPSON	2011	19	f
64	370	CARLUCCI	2011	38	f
65	371	SEWARD	2011	51	f
66	372	BRESLIN	2011	46	f
67	373	SERRANO	2011	28	f
68	374	LARKIN	2011	39	f
69	375	PERKINS	2011	30	f
70	376	DEFRANCISCO	2011	50	f
71	377	RIVERA	2011	33	f
72	378	HUNTLEY	2011	10	f
73	379	LITTLE	2011	45	f
74	380	HASSELL-THOMPSON	2011	36	f
75	381	MONTGOMERY	2011	18	f
76	382	ADAMS	2011	20	f
77	383	GIANARIS	2011	12	f
78	384	ADDABBO	2011	15	f
79	385	ESPAILLAT	2011	31	f
80	386	JOHNSON	2011	4	f
81	387	DUANE	2011	29	f
82	388	OPPENHEIMER	2011	37	f
83	389	MCDONALD	2011	43	f
84	390	KLEIN	2011	34	f
85	391	AVELLA	2011	11	f
86	392	ALESI	2011	55	f
87	393	GRIFFO	2011	47	f
88	394	NOZZOLIO	2011	54	f
89	395	VALESKY	2011	49	f
90	396	STEWART-COUSINS	2011	35	f
91	397	MARTINS	2011	7	f
92	398	GRISANTI	2011	60	f
93	399	FLANAGAN	2011	2	f
94	400	STAVISKY	2011	16	f
95	401	KRUEGER	2011	26	f
96	402	SALAND	2011	41	f
97	403	SAVINO	2011	23	f
98	404	ZELDIN	2011	3	f
99	405	SMITH	2011	14	f
100	406	RANZENHOFER	2011	61	f
101	407	MARCELLINO	2011	5	f
102	408	BALL	2011	40	f
103	409	LANZA	2011	24	f
104	410	LIBOUS	2011	52	f
105	411	MAZIARZ	2011	62	f
106	412	SQUADRON	2011	25	f
107	413	BONACIC	2011	42	f
108	414	YOUNG	2011	57	f
109	415	O'MARA	2011	53	f
110	416	PARKER	2011	21	f
111	417	ROBACH	2011	56	f
112	418	FARLEY	2011	44	f
113	419	LAVALLE	2011	1	f
114	420	KRUGER	2011	27	f
115	421	DIAZ	2011	32	f
116	422	GOLDEN	2011	22	f
117	423	RITCHIE	2011	48	f
118	424	HANNON	2011	6	f
119	425	FUSCHILLO	2011	8	f
120	426	DILAN	2011	17	f
121	427	GALLIVAN	2011	59	f
122	428	PERALTA	2011	13	f
123	429	KENNEDY	2011	58	f
124	430	SKELOS	2011	9	f
125	369	SAMPSON	2013	19	f
126	370	CARLUCCI	2013	38	f
127	371	SEWARD	2013	51	f
128	431	TKACZYK	2013	46	f
129	401	KRUEGER	2013	28	f
130	374	LARKIN	2013	39	f
131	375	PERKINS	2013	30	f
132	376	DEFRANCISCO	2013	50	f
133	377	RIVERA	2013	33	f
134	432	SANDERS	2013	10	f
135	379	LITTLE	2013	45	f
136	380	HASSELL-THOMPSON	2013	36	f
137	426	DILAN	2013	18	f
138	383	GIANARIS	2013	12	f
139	384	ADDABBO	2013	15	f
140	385	ESPAILLAT	2013	31	f
141	433	BOYLE	2013	4	f
142	373	SERRANO	2013	29	f
143	434	LATIMER	2013	37	f
144	435	MARCHIONE	2013	43	f
145	390	KLEIN	2013	34	f
146	429	KENNEDY	2013	63	f
147	391	AVELLA	2013	11	f
148	436	O'BRIEN	2013	55	f
149	393	GRIFFO	2013	47	f
150	394	NOZZOLIO	2013	54	f
151	418	FARLEY	2013	49	f
152	396	STEWART-COUSINS	2013	35	f
153	397	MARTINS	2013	7	f
154	398	GRISANTI	2013	60	f
155	399	FLANAGAN	2013	2	f
156	400	STAVISKY	2013	16	f
157	412	SQUADRON	2013	26	f
158	437	GIPSON	2013	41	f
159	403	SAVINO	2013	23	f
160	404	ZELDIN	2013	3	f
161	405	SMITH	2013	14	f
162	406	RANZENHOFER	2013	61	f
163	407	MARCELLINO	2013	5	f
164	408	BALL	2013	40	f
165	409	LANZA	2013	24	f
166	410	LIBOUS	2013	52	f
167	411	MAZIARZ	2013	62	f
168	381	MONTGOMERY	2013	25	f
169	413	BONACIC	2013	42	f
170	414	YOUNG	2013	57	f
171	395	VALESKY	2013	53	f
172	416	PARKER	2013	21	f
173	417	ROBACH	2013	56	f
174	372	BRESLIN	2013	44	f
175	419	LAVALLE	2013	1	f
176	438	HOYLMAN	2013	27	f
177	421	DIAZ	2013	32	f
178	422	GOLDEN	2013	22	f
179	423	RITCHIE	2013	48	f
180	424	HANNON	2013	6	f
181	439	FELDER	2013	17	f
182	427	GALLIVAN	2013	59	f
183	415	O'MARA	2013	58	f
184	430	SKELOS	2013	9	f
185	425	FUSCHILLO	2013	8	f
186	382	ADAMS	2013	20	f
187	428	PERALTA	2013	13	f
188	454	MONSERRATE	2009	13	f
189	455	STOROBIN	2011	27	f
190	463	ALESSI	2009	1	f
191	536	ARROYO	2009	84	f
192	518	KELLNER	2009	65	f
193	508	BOYLAND	2009	55	f
194	603	GIANARIS	2009	36	f
195	543	BRODSKY	2009	92	f
196	571	STIRPE	2009	121	f
197	479	HOOPER	2009	18	f
198	544	SPANO	2009	93	f
199	495	JACOBS	2009	42	f
200	553	MOLINARO	2009	103	f
201	513	HYER-SPENCER	2009	60	f
202	574	BARCLAY	2009	124	f
203	511	PERRY	2009	58	f
204	537	CRESPO	2009	85	f
205	510	JEFFRIES	2009	57	f
206	599	GIGLIO	2009	149	f
207	456	TITUS	2009	31	f
211	459	DENDEKKER	2009	34	f
212	520	ROSENTHAL	2009	67	f
213	477	SCHIMEL	2009	16	f
214	526	KAVANAGH	2009	74	f
215	473	SALADINO	2009	12	f
216	481	WEISENBERG	2009	20	f
217	535	HEASTIE	2009	83	f
218	516	CUSICK	2009	63	f
219	569	CHRISTENSEN	2009	119	f
220	525	BING	2009	73	f
221	579	KOLB	2009	129	f
222	533	DINOWITZ	2009	81	f
223	472	SWEENEY	2009	11	f
224	524	FARRELL	2009	71	f
225	457	COOK	2009	32	f
226	484	WEPRIN	2009	24	f
230	517	SILVER	2009	64	f
231	559	REILLY	2009	109	f
232	512	MAISEL	2009	59	f
233	499	BROOK-KRASNY	2009	46	f
234	578	OAKS	2009	128	f
235	471	CONTE	2009	10	f
236	505	MILLMAN	2009	52	f
237	500	COLTON	2009	47	f
238	507	TOWNS	2009	54	f
239	469	FITZPATRICK	2009	7	f
241	519	GLICK	2009	66	f
242	501	HIKIND	2009	48	f
243	458	CLARK	2009	33	f
244	589	HAWLEY	2009	139	f
245	540	PAULIN	2009	88	f
246	550	SKARTADOS	2009	100	f
247	573	FINCH	2009	123	f
248	487	MAYERSOHN	2009	27	f
249	521	POWELL	2009	68	f
250	482	ALFANO	2009	21	f
251	465	MURRAY	2009	3	f
252	555	AMEDORE	2009	105	f
253	467	FIELDS	2009	5	f
255	462	RAIA	2009	9	f
256	460	AUBRY	2009	35	f
257	597	BURLING	2009	147	f
258	593	GABRYSZAK	2009	143	f
259	529	GIBSON	2009	77	f
260	556	CANESTRARI	2009	106	f
263	489	SCARBOROUGH	2009	29	f
264	575	LIFTON	2009	125	f
265	567	BUTLER	2009	117	f
266	480	MCDONOUGH	2009	19	f
267	587	O'MARA	2009	137	f
268	590	SCHIMMINGER	2009	140	f
269	478	MCKEVITT	2009	17	f
270	538	CASTRO	2009	86	f
271	565	TOWNSEND	2009	115	f
272	490	MARKEY	2009	30	f
273	464	THIELE	2009	2	f
274	497	BRENNAN	2009	44	f
275	581	JOHN	2009	131	f
276	483	PHEFFER	2009	23	f
277	596	QUINN	2009	146	f
278	585	KOON	2009	135	f
279	583	GANTT	2009	133	f
280	534	BENEDETTO	2009	82	f
281	588	DELMONTE	2009	138	f
282	562	JORDAN	2009	112	f
283	547	CALHOUN	2009	96	f
284	563	SAYWARD	2009	113	f
285	592	CORWIN	2009	142	f
286	584	REILICH	2009	134	f
287	461	MENG	2009	22	f
288	498	CYMBROWITZ	2009	45	f
289	493	BARRON	2009	40	f
290	601	BALL	2009	99	f
291	514	TITONE	2009	61	f
292	576	LUPARDO	2009	126	f
293	595	SCHROEDER	2009	145	f
295	548	RABBITT	2009	97	f
296	503	LENTOL	2009	50	f
297	474	LAVINE	2009	13	f
298	509	ROBINSON	2009	56	f
299	486	CARROZZA	2009	26	f
300	568	RUSSELL	2009	118	f
301	539	PRETLOW	2009	87	f
302	604	LATIMER	2009	91	f
303	522	O'DONNELL	2009	69	f
304	580	ERRIGO	2009	130	f
305	488	HEVESI	2009	28	f
306	591	PEOPLES-STOKES	2009	141	f
307	570	MAGNARELLI	2009	120	f
308	475	BARRA	2009	14	f
309	560	TEDISCO	2009	110	f
310	476	MONTESANO	2009	15	f
311	485	LANCMAN	2009	25	f
312	602	ESPAILLAT	2009	72	f
262	577	LOPEZ P	2009	127	f
227	492	MILLER M	2009	38	f
228	552	MILLER J	2009	102	f
208	528	RIVERA P	2009	76	f
209	530	RIVERA J	2009	78	f
210	532	RIVERA N	2009	80	f
240	644	BRADLEY	2009	89	f
254	645	EDDINGTON	2009	3	f
294	646	WRIGHT	2009	70	f
313	582	MORELLE	2009	132	f
315	496	CAMARA	2009	43	f
317	549	GUNTHER	2009	98	f
319	515	TOBACCO	2009	62	f
321	527	GOTTFRIED	2009	75	f
323	466	ENGLEBRIGHT	2009	4	f
325	557	CROUCH	2009	107	f
327	470	BOYLE	2009	8	f
329	600	PARMENT	2009	150	f
331	504	ORTIZ	2009	51	f
333	494	WEINSTEIN	2009	41	f
335	531	BENJAMIN	2009	79	f
338	566	DESTITO	2009	116	f
339	468	RAMOS	2009	6	f
340	586	BACALLES	2009	136	f
342	554	MCENENY	2009	104	f
344	594	HOYT	2009	144	f
316	551	CAHILL	2009	101	f
318	546	JAFFEE	2009	95	f
320	545	ZEBROWSKI	2009	94	f
322	541	CASTELLI	2009	89	f
324	491	NOLAN	2009	37	f
326	564	DUPREY	2009	114	f
328	558	GORDON	2009	108	f
330	572	SCOZZAFAVA	2009	122	f
337	621	GALEF	2009	90	f
341	502	ABBATE	2009	49	f
343	598	HAYES	2009	148	f
345	561	MAGEE	2009	111	f
261	506	LOPEZ V	2009	53	f
332	643	PERALTA	2009	39	f
334	648	SEMINERIO	2009	38	f
314	647	WALKER	2009	15	f
346	650	DIAZ	2009	85	f
347	536	ARROYO	2011	84	f
348	518	KELLNER	2011	65	f
349	508	BOYLAND	2011	55	f
350	625	BARRETT	2011	103	f
351	627	TENNEY	2011	115	f
352	642	GOODELL	2011	150	f
353	479	HOOPER	2011	18	f
355	495	JACOBS	2011	42	f
357	632	HANNA	2011	130	f
358	574	BARCLAY	2011	124	f
359	537	CRESPO	2011	85	f
360	510	JEFFRIES	2011	57	f
361	511	PERRY	2011	58	f
362	599	GIGLIO	2011	149	f
363	456	TITUS	2011	31	f
364	607	CURRAN	2011	14	f
365	605	LOSQUADRO	2011	1	f
366	459	DENDEKKER	2011	34	f
367	526	KAVANAGH	2011	74	f
368	477	SCHIMEL	2011	16	f
369	520	ROSENTHAL	2011	67	f
370	473	SALADINO	2011	12	f
371	481	WEISENBERG	2011	20	f
372	535	HEASTIE	2011	83	f
373	516	CUSICK	2011	63	f
374	626	MCLAUGHLIN	2011	108	f
376	635	PALMESANO	2011	136	f
377	579	KOLB	2011	129	f
378	638	RYAN	2011	144	f
379	533	DINOWITZ	2011	81	f
380	472	SWEENEY	2011	11	f
381	524	FARRELL	2011	71	f
382	457	COOK	2011	32	f
383	517	SILVER	2011	64	f
384	484	WEPRIN	2011	24	f
385	628	BRINDISI	2011	116	f
386	559	REILLY	2011	109	f
387	512	MAISEL	2011	59	f
388	499	BROOK-KRASNY	2011	46	f
389	614	ESPINAL	2011	54	f
390	613	MOYA	2011	39	f
391	578	OAKS	2011	128	f
392	471	CONTE	2011	10	f
393	505	MILLMAN	2011	52	f
394	500	COLTON	2011	47	f
396	469	FITZPATRICK	2011	7	f
397	616	RODRIGUEZ	2011	68	f
398	519	GLICK	2011	66	f
399	501	HIKIND	2011	48	f
400	458	CLARK	2011	33	f
401	550	SKARTADOS	2011	100	f
402	633	BRONSON	2011	131	f
403	540	PAULIN	2011	88	f
404	589	HAWLEY	2011	139	f
405	573	FINCH	2011	123	f
407	637	CERETTO	2011	138	f
408	631	BLANKENBUSH	2011	122	f
409	641	WALTER	2011	148	f
410	465	MURRAY	2011	3	f
411	555	AMEDORE	2011	105	f
412	624	KATZ	2011	99	f
413	462	RAIA	2011	9	f
414	636	FRIEND	2011	137	f
415	460	AUBRY	2011	35	f
416	617	LINARES	2011	72	f
417	610	BRAUNSTEIN	2011	26	f
418	597	BURLING	2011	147	f
419	529	GIBSON	2011	77	f
420	593	GABRYSZAK	2011	143	f
421	609	GOLDFEDER	2011	23	f
422	556	CANESTRARI	2011	106	f
423	575	LIFTON	2011	125	f
424	489	SCARBOROUGH	2011	29	f
425	612	SIMOTAS	2011	36	f
426	567	BUTLER	2011	117	f
427	480	MCDONOUGH	2011	19	f
428	590	SCHIMMINGER	2011	140	f
429	478	MCKEVITT	2011	17	f
430	538	CASTRO	2011	86	f
431	618	QUART	2011	73	f
432	639	KEARNS	2011	145	f
433	490	MARKEY	2011	30	f
434	464	THIELE	2011	2	f
435	623	MAYER	2011	93	f
436	497	BRENNAN	2011	44	f
438	583	GANTT	2011	133	f
439	534	BENEDETTO	2011	82	f
440	562	JORDAN	2011	112	f
441	563	SAYWARD	2011	113	f
442	547	CALHOUN	2011	96	f
443	592	CORWIN	2011	142	f
444	584	REILICH	2011	134	f
445	461	MENG	2011	22	f
446	493	BARRON	2011	40	f
447	498	CYMBROWITZ	2011	45	f
448	514	TITONE	2011	61	f
449	576	LUPARDO	2011	126	f
452	634	JOHNS	2011	135	f
453	503	LENTOL	2011	50	f
454	548	RABBITT	2011	97	f
455	474	LAVINE	2011	13	f
456	509	ROBINSON	2011	56	f
457	629	ROBERTS	2011	119	f
458	615	MALLIOTAKIS	2011	60	f
459	568	RUSSELL	2011	118	f
460	539	PRETLOW	2011	87	f
461	604	LATIMER	2011	91	f
462	622	ABINANTI	2011	92	f
463	522	O'DONNELL	2011	69	f
464	488	HEVESI	2011	28	f
465	591	PEOPLES-STOKES	2011	141	f
466	570	MAGNARELLI	2011	120	f
467	560	TEDISCO	2011	110	f
468	485	LANCMAN	2011	25	f
469	476	MONTESANO	2011	15	f
470	582	MORELLE	2011	132	f
471	496	CAMARA	2011	43	f
472	551	CAHILL	2011	101	f
473	549	GUNTHER	2011	98	f
474	546	JAFFEE	2011	95	f
475	515	TOBACCO	2011	62	f
476	527	GOTTFRIED	2011	75	f
477	545	ZEBROWSKI	2011	94	f
478	466	ENGLEBRIGHT	2011	4	f
479	541	CASTELLI	2011	89	f
480	491	NOLAN	2011	37	f
481	564	DUPREY	2011	114	f
482	557	CROUCH	2011	107	f
375	525	BING	2011	73	f
450	646	WRIGHT	2011	70	f
395	507	TOWNS	2011	54	f
406	487	MAYERSOHN	2011	27	f
356	553	MOLINARO	2011	103	f
437	483	PHEFFER	2011	23	f
451	595	SCHROEDER	2011	145	f
354	544	SPANO	2011	93	f
483	470	BOYLE	2011	8	f
484	611	SIMANOWITZ	2011	27	f
485	504	ORTIZ	2011	51	f
486	640	SMARDZ	2011	146	f
487	494	WEINSTEIN	2011	41	f
489	621	GALEF	2011	90	f
490	468	RAMOS	2011	6	f
491	619	STEVENSON	2011	79	f
492	502	ABBATE	2011	49	f
494	554	MCENENY	2011	104	f
496	561	MAGEE	2011	111	f
497	608	RA	2011	21	f
498	506	LOPEZ V	2011	53	f
499	577	LOPEZ P	2011	127	f
500	492	MILLER M	2011	38	f
501	630	MILLER D	2011	121	f
502	552	MILLER J	2011	102	f
503	528	RIVERA P	2011	76	f
504	532	RIVERA N	2011	80	f
505	530	RIVERA J	2011	78	f
506	652	KIRWAN	2011	100	f
488	566	DESTITO	2011	116	f
493	598	HAYES	2011	148	f
495	594	HOYT	2011	144	f
508	492	MILLER	2013	38	f
509	502	ABBATE	2013	49	f
510	622	ABINANTI	2013	92	f
511	536	ARROYO	2013	84	f
512	460	AUBRY	2013	35	f
513	574	BARCLAY	2013	124	f
514	625	BARRETT	2013	103	f
515	493	BARRON	2013	40	f
516	534	BENEDETTO	2013	82	f
517	631	BLANKENBUSH	2013	122	f
518	508	BOYLAND	2013	55	f
519	610	BRAUNSTEIN	2013	26	f
520	497	BRENNAN	2013	44	f
521	628	BRINDISI	2013	116	f
522	633	BRONSON	2013	131	f
523	499	BROOK-KRASNY	2013	46	f
524	567	BUTLER	2013	117	f
525	551	CAHILL	2013	101	f
526	496	CAMARA	2013	43	f
527	538	CASTRO	2013	86	f
528	637	CERETTO	2013	138	f
529	458	CLARK	2013	33	f
530	500	COLTON	2013	47	f
531	457	COOK	2013	32	f
532	592	CORWIN	2013	142	f
533	537	CRESPO	2013	85	f
534	557	CROUCH	2013	107	f
535	607	CURRAN	2013	14	f
536	516	CUSICK	2013	63	f
537	498	CYMBROWITZ	2013	45	f
538	459	DENDEKKER	2013	34	f
539	533	DINOWITZ	2013	81	f
540	564	DUPREY	2013	114	f
541	466	ENGLEBRIGHT	2013	4	f
542	614	ESPINAL	2013	54	f
543	524	FARRELL	2013	71	f
544	573	FINCH	2013	123	f
545	469	FITZPATRICK	2013	7	f
546	636	FRIEND	2013	137	f
547	593	GABRYSZAK	2013	143	f
548	621	GALEF	2013	90	f
549	583	GANTT	2013	133	f
550	529	GIBSON	2013	77	f
551	599	GIGLIO	2013	149	f
552	620	GJONAJ	2013	80	f
553	519	GLICK	2013	66	f
554	609	GOLDFEDER	2013	23	f
555	642	GOODELL	2013	150	f
556	527	GOTTFRIED	2013	75	f
558	549	GUNTHER	2013	98	f
559	589	HAWLEY	2013	139	f
560	535	HEASTIE	2013	83	f
561	488	HEVESI	2013	28	f
562	501	HIKIND	2013	48	f
563	479	HOOPER	2013	18	f
564	495	JACOBS	2013	42	f
565	546	JAFFEE	2013	95	f
566	634	JOHNS	2013	135	f
567	562	JORDAN	2013	112	f
568	624	KATZ	2013	99	f
569	526	KAVANAGH	2013	74	f
570	639	KEARNS	2013	145	f
571	518	KELLNER	2013	65	f
572	579	KOLB	2013	129	f
573	474	LAVINE	2013	13	f
574	503	LENTOL	2013	50	f
575	575	LIFTON	2013	125	f
576	605	LOSQUADRO	2013	1	f
577	576	LUPARDO	2013	126	f
578	561	MAGEE	2013	111	f
579	570	MAGNARELLI	2013	120	f
580	512	MAISEL	2013	59	f
581	615	MALLIOTAKIS	2013	60	f
582	490	MARKEY	2013	30	f
583	623	MAYER	2013	93	f
584	480	MCDONOUGH	2013	19	f
585	478	MCKEVITT	2013	17	f
586	626	MCLAUGHLIN	2013	108	f
587	505	MILLMAN	2013	52	f
588	476	MONTESANO	2013	15	f
589	582	MORELLE	2013	132	f
590	613	MOYA	2013	39	f
591	491	NOLAN	2013	37	f
592	522	O'DONNELL	2013	69	f
593	578	OAKS	2013	128	f
594	504	ORTIZ	2013	51	f
595	635	PALMESANO	2013	136	f
596	540	PAULIN	2013	88	f
597	591	PEOPLES-STOKES	2013	141	f
598	511	PERRY	2013	58	f
599	539	PRETLOW	2013	87	f
600	618	QUART	2013	73	f
601	608	RA	2013	21	f
602	548	RABBITT	2013	97	f
603	462	RAIA	2013	9	f
604	468	RAMOS	2013	6	f
605	584	REILICH	2013	134	f
606	629	ROBERTS	2013	119	f
607	509	ROBINSON	2013	56	f
608	616	RODRIGUEZ	2013	68	f
609	520	ROSENTHAL	2013	67	f
610	568	RUSSELL	2013	118	f
611	638	RYAN	2013	144	f
612	473	SALADINO	2013	12	f
613	489	SCARBOROUGH	2013	29	f
614	477	SCHIMEL	2013	16	f
615	590	SCHIMMINGER	2013	140	f
616	517	SILVER	2013	64	f
617	611	SIMANOWITZ	2013	27	f
618	612	SIMOTAS	2013	36	f
619	550	SKARTADOS	2013	100	f
620	619	STEVENSON	2013	79	f
622	472	SWEENEY	2013	11	f
623	560	TEDISCO	2013	110	f
624	627	TENNEY	2013	115	f
625	464	THIELE	2013	2	f
626	514	TITONE	2013	61	f
627	456	TITUS	2013	31	f
628	641	WALTER	2013	148	f
629	494	WEINSTEIN	2013	41	f
630	481	WEISENBERG	2013	20	f
631	484	WEPRIN	2013	24	f
633	545	ZEBROWSKI	2013	94	f
634	653	BORELLI	2013	62	f
635	654	BUCHWALD	2013	93	f
636	655	DAVILA	2013	53	f
637	656	DIPIETRO	2013	147	f
638	657	FAHY	2013	109	f
639	658	GARBARINO	2013	7	f
621	571	STIRPE	2013	127	f
641	660	LALOR	2013	105	f
642	661	LUPINACCI	2013	10	f
643	662	MCDONALD	2013	108	f
644	663	MOSLEY	2013	57	f
645	664	NOJAY	2013	133	f
646	665	OTIS	2013	91	f
647	666	PALUMBO	2013	2	f
648	667	PICHARDO	2013	86	f
649	668	ROSA	2013	72	f
650	669	ROZIC	2013	25	f
651	670	SANTABARBARA	2013	111	f
652	671	SEPULVEDA	2013	87	f
653	672	SKOUFIS	2013	99	f
654	673	SOLAGES	2013	22	f
655	674	STEC	2013	114	f
656	675	STECK	2013	110	f
640	659	KIM	2013	40	f
632	646	WRIGHT	2013	70	f
557	651	GRAF	2013	5	f
657	506	LOPEZ V	2013	53	f
658	577	LOPEZ P	2013	127	f
659	651	GRAF	2011	5	f
662	552	MILLER	2009	102	t
661	591	PEOPLES	2009	141	t
663	551	CAHILL	2007	101	f
664	676	GREENE	2009	0	f
665	484	WEPRIN D	2009	24	t
666	677	HENNESSEY	2013	3	f
667	677	HENNESSY	2013	3	t
668	380	HASSEL_THOMPSO	2009	36	t
669	380	HASSELL-THOMPSO	2011	36	t
670	380	HASSELL-THOMPSO	2013	36	t
671	386	JOHNSON	2009	4	t
672	678	WEISENBERGT	2009	\N	f
674	680	PEOPLES-STOKE	2009	\N	f
675	681	GOTTFRIED B	2009	\N	f
676	682	RULES	2009	\N	f
677	683	MAGNARELLIS	2011	\N	f
678	684	PEOPLES-STOKE	2011	\N	f
679	685	WEINSTEIN M	2011	\N	f
680	686	CYMBROWITZSNY	2011	\N	f
681	687	ZEBROWSKI I	2011	\N	f
682	688	WEINSTEIN E	2011	\N	f
683	689	BRAUNSTEINWITZ	2011	\N	f
684	690	BRAUNSTEINZ	2011	\N	f
685	691	STRIPE	2013	\N	f
687	693	O'DONNELL S	2013	\N	f
688	694	WEINSTEIN T	2013	\N	f
689	695	O'DONNELL B	2013	\N	f
690	696	BENEDETTO O	2013	\N	f
691	697	BENEDETTO R	2013	\N	f
692	698	O'DONNELL L	2013	\N	f
710	400	STAVISKY	2015	16	f
708	375	PERKINS	2015	30	f
728	396	STEWART-COUSINS	2015	35	f
702	393	GRIFFO	2015	47	f
697	422	GOLDEN	2015	22	f
704	371	SEWARD	2015	51	f
705	438	HOYLMAN	2015	27	f
725	395	VALESKY	2015	53	f
696	427	GALLIVAN	2015	59	f
707	401	KRUEGER	2015	28	f
701	423	RITCHIE	2015	48	f
715	412	SQUADRON	2015	26	f
695	397	MARTINS	2015	7	f
714	428	PERALTA	2015	13	f
694	419	LAVALLE	2015	1	f
713	416	PARKER	2015	21	f
719	421	DIAZ	2015	32	f
709	373	SERRANO	2015	29	f
703	407	MARCELLINO	2015	5	f
699	435	MARCHIONE	2015	43	f
698	374	LARKIN	2015	39	f
716	433	BOYLE	2015	4	f
700	376	DEFRANCISCO	2015	50	f
717	434	LATIMER	2015	37	f
718	385	ESPAILLAT	2015	31	f
730	409	LANZA	2015	24	f
706	383	GIANARIS	2015	12	f
712	429	KENNEDY	2015	63	f
722	426	DILAN	2015	18	f
721	370	CARLUCCI	2015	38	f
724	403	SAVINO	2015	23	f
737	625	BARRETT	2015	106	f
731	611	SIMANOWITZ	2015	27	f
758	502	ABBATE	2015	49	f
732	669	ROZIC	2015	25	f
770	622	ABINANTI	2015	92	f
733	568	RUSSELL	2015	116	f
741	638	RYAN	2015	149	f
753	479	HOOPER	2015	18	f
751	457	COOK	2015	32	f
757	504	ORTIZ	2015	51	f
755	462	RAIA	2015	12	f
771	498	CYMBROWITZ	2015	45	f
748	492	MILLER	2015	38	f
759	590	SCHIMMINGER	2015	140	f
769	627	TENNEY	2015	101	f
745	545	ZEBROWSKI	2015	96	f
739	657	FAHY	2015	109	f
736	540	PAULIN	2015	88	f
766	662	MCDONALD	2015	108	f
767	591	PEOPLES-STOKES	2015	141	f
764	549	GUNTHER	2015	100	f
762	460	AUBRY	2015	35	f
734	583	GANTT	2015	137	f
752	613	MOYA	2015	39	f
774	484	WEPRIN	2015	24	f
738	527	GOTTFRIED	2015	75	f
747	546	JAFFEE	2015	97	f
746	621	GALEF	2015	95	f
740	458	CLARK	2015	33	f
773	623	MAYER	2015	90	f
775	499	BROOK-KRASNY	2015	46	f
822	828	LOPEZ	2015	102	f
788	637	CERETTO	2015	145	f
818	641	WALTER	2015	146	f
783	536	ARROYO	2015	84	f
779	654	BUCHWALD	2015	93	f
799	628	BRINDISI	2015	119	f
807	570	MAGNARELLI	2015	129	f
803	561	MAGEE	2015	121	f
853	418	FARLEY	2015	49	f
852	394	NOZZOLIO	2015	54	f
854	410	LIBOUS	2015	52	f
850	406	RANZENHOFER	2015	61	f
851	439	FELDER	2015	17	f
855	414	YOUNG	2015	57	f
802	618	QUART	2015	73	f
809	473	SALADINO	2015	9	f
816	478	MCKEVITT	2015	17	f
778	496	CAMARA	2015	43	f
781	671	SEPULVEDA	2015	87	f
785	646	WRIGHT	2015	70	f
777	663	MOSLEY	2015	57	f
776	610	BRAUNSTEIN	2015	26	f
786	557	CROUCH	2015	122	f
806	674	STEC	2015	114	f
805	469	FITZPATRICK	2015	8	f
808	551	CAHILL	2015	103	f
782	526	KAVANAGH	2015	74	f
821	535	HEASTIE	2015	83	f
811	501	HIKIND	2015	48	f
817	608	RA	2015	19	f
787	672	SKOUFIS	2015	99	f
801	675	STECK	2015	110	f
804	509	ROBINSON	2015	56	f
815	574	BARCLAY	2015	120	f
813	612	SIMOTAS	2015	36	f
820	522	O'DONNELL	2015	69	f
812	534	BENEDETTO	2015	82	f
780	477	SCHIMEL	2015	16	f
784	497	BRENNAN	2015	44	f
800	575	LIFTON	2015	125	f
810	476	MONTESANO	2015	15	f
814	468	RAMOS	2015	6	f
847	633	BRONSON	2015	138	f
832	564	DUPREY	2015	115	f
849	655	DAVILA	2015	53	f
797	537	CRESPO	2015	85	f
825	615	MALLIOTAKIS	2015	64	f
793	474	LAVINE	2015	13	f
798	456	TITUS	2015	31	f
789	661	LUPINACCI	2015	10	f
790	480	MCDONOUGH	2015	14	f
846	592	CORWIN	2015	144	f
792	459	DENDEKKER	2015	34	f
796	651	GRAF	2015	5	f
794	629	ROBERTS	2015	128	f
795	519	GLICK	2015	66	f
848	634	JOHNS	2015	135	f
791	490	MARKEY	2015	30	f
831	642	GOODELL	2015	150	f
840	624	KATZ	2015	94	f
839	639	KEARNS	2015	142	f
833	666	PALUMBO	2015	2	f
835	616	RODRIGUEZ	2015	68	f
819	653	BORELLI	2015	62	f
830	571	STIRPE	2015	127	f
834	673	SOLAGES	2015	22	f
841	656	DIPIETRO	2015	147	f
836	503	LENTOL	2015	50	f
727	372	BRESLIN	2015	44	f
872	415	O'MARA	2015	58	f
877	430	SKELOS	2015	9	f
889	895	VENDITTO	2015	8	f
720	391	AVELLA	2015	11	f
726	384	ADDABBO	2015	15	f
884	890	SERINO	2015	41	f
887	893	CROCI	2015	3	f
871	424	HANNON	2015	6	f
886	892	AMEDORE	2015	46	f
878	399	FLANAGAN	2015	2	f
881	887	FUNKE	2015	55	f
856	379	LITTLE	2015	45	f
711	381	MONTGOMERY	2015	25	f
885	891	MURPHY	2015	40	f
879	885	ORTT	2015	62	f
723	377	RIVERA	2015	33	f
875	432	SANDERS	2015	10	f
693	380	HASSELL-THOMPSON	2015	36	f
882	888	COMRIE	2015	14	f
883	889	HAMILTON	2015	20	f
888	894	PANEPINTO	2015	60	f
874	369	SAMPSON	2015	19	f
876	390	KLEIN	2015	34	f
873	417	ROBACH	2015	56	f
729	413	BONACIC	2015	42	f
761	582	MORELLE	2015	136	f
894	900	DILAN	2015	54	f
868	578	OAKS	2015	130	f
897	903	KAMINSKY	2015	20	f
902	908	BRABENEC	2015	98	f
845	609	GOLDFEDER	2015	23	f
826	560	TEDISCO	2015	112	f
864	567	BUTLER	2015	118	f
827	607	CURRAN	2015	21	f
823	599	GIGLIO	2015	148	f
859	626	MCLAUGHLIN	2015	107	f
760	466	ENGLEBRIGHT	2015	4	f
860	659	KIM	2015	40	f
829	524	FARRELL	2015	71	f
842	658	GARBARINO	2015	7	f
844	491	NOLAN	2015	37	f
837	539	PRETLOW	2015	89	f
866	589	HAWLEY	2015	139	f
895	901	JEAN-PIERRE	2015	11	f
903	909	LAWRENCE	2015	134	f
865	636	FRIEND	2015	124	f
862	660	LALOR	2015	105	f
869	635	PALMESANO	2015	132	f
749	520	ROSENTHAL	2015	67	f
905	911	WOZNIAK	2015	143	f
863	631	BLANKENBUSH	2015	117	f
750	489	SCARBOROUGH	2015	29	f
899	905	PERSAUD	2015	59	f
861	620	GJONAJ	2015	80	f
828	573	FINCH	2015	126	f
870	493	BARRON	2015	60	f
867	664	NOJAY	2015	133	f
890	896	WALKER	2015	55	f
880	886	WOERNER	2015	113	f
891	517	SILVER	2015	65	f
898	904	LINARES	2015	72	f
765	576	LUPARDO	2015	123	f
901	907	SIMON	2015	52	f
904	910	MURRAY	2015	3	f
838	579	KOLB	2015	131	f
756	516	CUSICK	2015	63	f
763	500	COLTON	2015	47	f
742	550	SKARTADOS	2015	104	f
843	494	WEINSTEIN	2015	41	f
896	902	JOYNER	2015	77	f
743	514	TITONE	2015	61	f
772	533	DINOWITZ	2015	81	f
754	665	OTIS	2015	91	f
900	906	SEAWRIGHT	2015	76	f
735	511	PERRY	2015	58	f
893	899	BLAKE	2015	79	f
824	488	HEVESI	2015	28	f
768	464	THIELE	2015	1	f
892	898	BICHOTTE	2015	42	f
858	667	PICHARDO	2015	86	f
857	670	SANTABARBARA	2015	111	f
906	912	RICHARDSON	2015	43	f
686	591	PEOPLES-STOKE	2013	\N	t
907	913	AKSHAR	2015	52	f
908	914	PERSAUD	2015	19	f
913	919	CANCEL	2015	\N	f
916	922	PEOPLES-STOKE	2015	\N	f
917	923	GOTTFRIED G	2015	\N	f
912	918	KAMINSKY	2015	9	f
744	530	RIVERA	2015	78	f
915	1007	CASTORINA	2015	62	f
910	1089	HYNDMAN	2015	29	f
909	1105	HUNTER	2015	128	f
911	1076	HARRIS	2015	46	f
914	1116	WILLIAMS	2015	59	f
952	913	AKSHAR	2017	52	f
939	376	DEFRANCISCO	2017	50	f
1008	656	DIPIETRO	2017	147	f
979	573	FINCH	2017	126	f
953	887	FUNKE	2017	55	f
928	383	GIANARIS	2017	12	f
980	519	GLICK	2017	66	f
981	488	HEVESI	2017	28	f
929	429	KENNEDY	2017	63	f
970	576	LUPARDO	2017	123	f
930	428	PERALTA	2017	13	f
1000	462	RAIA	2017	12	f
949	423	RITCHIE	2017	48	f
975	616	RODRIGUEZ	2017	68	f
934	373	SERRANO	2017	29	f
976	550	SKARTADOS	2017	104	f
977	456	TITUS	2017	31	f
998	568	JENNE	2017	116	f
1001	1007	CASTORINA	2017	62	f
954	892	AMEDORE	2017	46	f
984	534	BENEDETTO	2017	82	f
985	516	CUSICK	2017	63	f
927	426	DILAN	2017	18	f
986	549	GUNTHER	2017	100	f
922	434	LATIMER	2017	37	f
947	379	LITTLE	2017	45	f
943	381	MONTGOMERY	2017	25	f
957	885	ORTT	2017	62	f
931	375	PERKINS	2017	30	f
926	377	RIVERA	2017	33	f
990	473	SALADINO	2017	9	f
932	432	SANDERS	2017	10	f
933	403	SAVINO	2017	23	f
950	371	SEWARD	2017	51	f
923	412	SQUADRON	2017	26	f
1009	464	THIELE	2017	1	f
951	395	VALESKY	2017	53	f
1010	419	LAVALLE	2017	1	f
993	622	ABINANTI	2017	92	f
996	625	BARRETT	2017	106	f
1003	628	BRINDISI	2017	119	f
958	551	CAHILL	2017	103	f
978	500	COLTON	2017	47	f
936	888	COMRIE	2017	14	f
994	537	CRESPO	2017	85	f
964	498	CYMBROWITZ	2017	45	f
992	533	DINOWITZ	2017	81	f
965	524	FARRELL	2017	71	f
987	621	GALEF	2017	95	f
966	583	GANTT	2017	137	f
938	422	GOLDEN	2017	22	f
967	527	GOTTFRIED	2017	75	f
937	889	HAMILTON	2017	20	f
960	479	HOOPER	2017	18	f
969	526	KAVANAGH	2017	74	f
988	579	KOLB	2017	131	f
935	401	KRUEGER	2017	28	f
971	570	MAGNARELLI	2017	129	f
989	476	MONTESANO	2017	15	f
1002	910	MURRAY	2017	3	f
941	415	O'MARA	2017	58	f
973	591	PEOPLES-STOKES	2017	141	f
961	511	PERRY	2017	58	f
948	406	RANZENHOFER	2017	61	f
982	520	ROSENTHAL	2017	67	t
1004	590	SCHIMMINGER	2017	140	f
924	400	STAVISKY	2017	16	f
999	896	WALKER	2017	55	f
974	530	RIVERA	2017	78	f
945	951	HELMING	2017	54	f
919	384	ADDABBO	2017	15	f
962	460	AUBRY	2017	35	f
920	391	AVELLA	2017	11	f
963	457	COOK	2017	32	f
955	893	CROCI	2017	3	f
956	427	GALLIVAN	2017	59	f
921	438	HOYLMAN	2017	27	f
918	918	KAMINSKY	2017	9	f
940	435	MARCHIONE	2017	43	f
925	416	PARKER	2017	21	f
995	484	WEPRIN	2017	24	f
942	414	YOUNG	2017	57	f
983	502	ABBATE	2017	49	f
944	433	BOYLE	2017	4	f
946	372	BRESLIN	2017	44	f
1007	567	BUTLER	2017	118	f
959	466	ENGLEBRIGHT	2017	4	f
968	546	JAFFEE	2017	97	f
1006	480	MCDONOUGH	2017	14	f
1005	582	MORELLE	2017	136	f
972	504	ORTIZ	2017	51	f
991	540	PAULIN	2017	88	f
997	675	STECK	2017	110	f
507	530	RIVERA	2013	76	f
1017	393	GRIFFO	2017	47	f
1012	374	LARKIN	2017	39	f
1013	439	FELDER	2017	17	f
1018	890	SERINO	2017	41	f
1011	424	HANNON	2017	6	f
1014	891	MURPHY	2017	40	f
1015	417	ROBACH	2017	56	f
1016	413	BONACIC	2017	42	f
1025	545	ZEBROWSKI	2017	96	f
1040	657	FAHY	2017	109	f
1048	613	MOYA	2017	39	f
1023	828	LOPEZ	2017	102	f
1043	536	ARROYO	2017	84	f
1039	654	BUCHWALD	2017	93	f
1019	561	MAGEE	2017	121	f
1047	618	QUART	2017	73	f
1042	671	SEPULVEDA	2017	87	f
1041	663	MOSLEY	2017	57	f
1031	557	CROUCH	2017	122	f
1057	674	STEC	2017	114	f
1056	469	FITZPATRICK	2017	8	f
1029	501	HIKIND	2017	48	f
1027	608	RA	2017	19	f
1021	672	SKOUFIS	2017	99	f
1046	522	O'DONNELL	2017	69	f
1045	575	LIFTON	2017	125	f
1035	468	RAMOS	2017	6	f
1028	633	BRONSON	2017	138	f
1026	655	DAVILA	2017	53	f
1036	661	LUPINACCI	2017	10	f
1034	459	DENDEKKER	2017	34	f
1032	642	GOODELL	2017	150	f
1022	639	KEARNS	2017	142	f
1053	571	STIRPE	2017	127	f
1044	503	LENTOL	2017	50	f
1054	578	OAKS	2017	130	f
1030	908	BRABENEC	2017	98	f
1049	607	CURRAN	2017	21	f
1052	658	GARBARINO	2017	7	f
1055	636	FRIEND	2017	124	f
1033	660	LALOR	2017	105	f
1038	635	PALMESANO	2017	132	f
1051	907	SIMON	2017	52	f
1020	514	TITONE	2017	61	f
1037	899	BLAKE	2017	79	f
1024	667	PICHARDO	2017	86	f
1066	611	SIMANOWITZ	2017	27	f
1068	638	RYAN	2017	149	f
1064	623	MAYER	2017	90	f
1071	615	MALLIOTAKIS	2017	64	f
1059	651	GRAF	2017	5	f
1060	634	JOHNS	2017	135	f
1069	673	SOLAGES	2017	22	f
1058	599	GIGLIO	2017	148	f
1065	626	MCLAUGHLIN	2017	107	f
1062	589	HAWLEY	2017	139	f
1061	909	LAWRENCE	2017	134	f
1067	886	WOERNER	2017	113	f
1063	670	SANTABARBARA	2017	111	f
1072	370	CARLUCCI	2017	38	f
1075	641	WALTER	2017	146	f
1077	574	BARCLAY	2017	120	f
1074	474	LAVINE	2017	13	f
1078	666	PALUMBO	2017	2	f
1080	900	DILAN	2017	54	f
1081	539	PRETLOW	2017	89	f
1076	631	BLANKENBUSH	2017	117	f
1073	665	OTIS	2017	91	f
1079	906	SEAWRIGHT	2017	76	f
1082	912	RICHARDSON	2017	43	f
1087	396	STEWART-COUSINS	2017	35	f
1085	421	DIAZ	2017	32	f
1088	407	MARCELLINO	2017	5	f
1084	409	LANZA	2017	24	f
1086	914	PERSAUD	2017	19	f
1091	390	KLEIN	2017	34	f
1089	399	FLANAGAN	2017	2	f
1090	1096	ALCANTARA	2017	31	f
1095	1099	JACOBS	2017	60	f
1096	1100	PHILLIPS	2017	7	f
1097	1101	BROOKS	2017	8	f
1098	1102	BAILEY	2017	36	f
1099	1103	TEDISCO	2017	49	f
1111	492	MILLER MG	2017	38	f
1112	1115	MILLER ML	2017	20	f
1108	669	ROZIC	2017	25	f
1100	662	MCDONALD	2017	108	f
1104	478	MCKEVITT	2017	17	f
1105	610	BRAUNSTEIN	2017	26	f
1107	612	SIMOTAS	2017	36	f
1110	659	KIM	2017	40	f
1094	491	NOLAN	2017	37	f
1102	901	JEAN-PIERRE	2017	11	f
1109	493	BARRON	2017	60	f
1093	494	WEINSTEIN	2017	41	f
1106	902	JOYNER	2017	77	f
1103	898	BICHOTTE	2017	42	f
1114	620	GJONAJ	2017	80	f
673	530	RIVERA	2009	78	t
1126	1129	DE LA ROSA	2017	72	f
1127	1130	D'URSO	2017	16	f
1121	580	ERRIGO	2017	133	f
1118	535	HEASTIE	2017	83	f
1128	1131	PHEFFER AMATO	2017	23	f
1083	1089	HYNDMAN	2017	29	f
1101	1105	HUNTER	2017	128	f
1115	1118	VANEL	2017	33	f
1117	1120	JONES	2017	115	f
1070	1076	HARRIS	2017	46	f
1113	1116	WILLIAMS	2017	59	f
1116	1119	BARNWELL	2017	30	f
1120	1123	BYRNE	2017	94	f
1129	1132	NIOU	2017	65	f
1119	1122	CARROLL	2017	44	f
1122	1125	MILLER B	2017	101	f
1123	1126	NORRIS	2017	144	f
1124	1127	WALSH	2017	112	f
1125	1128	DICKENS	2017	70	f
1130	1133	WALLACE	2017	143	f
1132	1135	MORINELLO	2017	145	f
1050	1134	WRIGHT	2017	56	f
1133	1136	BENJAMIN	2017	30	f
1134	1137	PELLEGRINO	2017	9	f
1135	520	ROSENTHAL L 2017	67	f
1136	1139	ROSENTHAL D 2017	27	f
1137	1140	KAVANAGH 2017	26	f
\.


SET search_path = public, master, pg_catalog;

--
-- Data for Name: committee_member; Type: TABLE DATA; Schema: master; Owner: postgres
--

COPY committee_member (majority, id, sequence_no, title, committee_name, version_created, session_year, session_member_id, chamber) FROM stdin;
f	2	1	chair_person	Investigations and Government Operations	2011-01-01 00:00:00	2011	101	senate
f	3	2	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	86	senate
f	4	3	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	116	senate
f	5	4	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	88	senate
f	6	5	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	98	senate
f	7	6	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	106	senate
f	8	7	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	115	senate
f	9	8	member	Investigations and Government Operations	2011-01-01 00:00:00	2011	122	senate
f	10	1	chair_person	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	100	senate
f	11	2	member	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	93	senate
f	12	3	member	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	68	senate
f	13	4	member	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	91	senate
f	14	5	member	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	69	senate
f	15	6	member	Corporations, Authorities and Commissions	2011-01-01 00:00:00	2011	106	senate
f	16	1	chair_person	Ethics	2011-01-01 00:00:00	2011	103	senate
f	17	2	member	Ethics	2011-01-01 00:00:00	2011	112	senate
f	18	3	member	Ethics	2011-01-01 00:00:00	2011	109	senate
f	19	4	member	Ethics	2011-01-01 00:00:00	2011	66	senate
f	20	5	member	Ethics	2011-01-01 00:00:00	2011	74	senate
f	21	1	chair_person	Judiciary	2011-01-01 00:00:00	2011	107	senate
f	22	2	member	Judiciary	2011-01-01 00:00:00	2011	70	senate
f	23	3	member	Judiciary	2011-01-01 00:00:00	2011	93	senate
f	24	4	member	Judiciary	2011-01-01 00:00:00	2011	119	senate
f	25	5	member	Judiciary	2011-01-01 00:00:00	2011	118	senate
f	26	6	member	Judiciary	2011-01-01 00:00:00	2011	103	senate
f	27	7	member	Judiciary	2011-01-01 00:00:00	2011	113	senate
f	28	8	member	Judiciary	2011-01-01 00:00:00	2011	73	senate
f	29	9	member	Judiciary	2011-01-01 00:00:00	2011	88	senate
f	30	10	member	Judiciary	2011-01-01 00:00:00	2011	109	senate
f	31	11	member	Judiciary	2011-01-01 00:00:00	2011	97	senate
f	32	12	member	Judiciary	2011-01-01 00:00:00	2011	99	senate
f	33	13	member	Judiciary	2011-01-01 00:00:00	2011	100	senate
f	34	14	member	Judiciary	2011-01-01 00:00:00	2011	98	senate
f	35	15	member	Judiciary	2011-01-01 00:00:00	2011	74	senate
f	36	16	member	Judiciary	2011-01-01 00:00:00	2011	76	senate
f	37	17	member	Judiciary	2011-01-01 00:00:00	2011	66	senate
f	38	18	member	Judiciary	2011-01-01 00:00:00	2011	120	senate
f	39	19	member	Judiciary	2011-01-01 00:00:00	2011	79	senate
f	40	20	member	Judiciary	2011-01-01 00:00:00	2011	77	senate
f	41	21	member	Judiciary	2011-01-01 00:00:00	2011	95	senate
f	42	22	member	Judiciary	2011-01-01 00:00:00	2011	69	senate
f	43	23	member	Judiciary	2011-01-01 00:00:00	2011	67	senate
f	44	24	member	Judiciary	2011-01-01 00:00:00	2011	106	senate
f	45	25	member	Judiciary	2011-01-01 00:00:00	2011	94	senate
f	46	1	chair_person	Health	2011-01-01 00:00:00	2011	118	senate
f	47	2	member	Health	2011-01-01 00:00:00	2011	102	senate
f	48	3	member	Health	2011-01-01 00:00:00	2011	112	senate
f	49	4	member	Health	2011-01-01 00:00:00	2011	119	senate
f	50	5	member	Health	2011-01-01 00:00:00	2011	116	senate
f	51	6	member	Health	2011-01-01 00:00:00	2011	68	senate
f	52	7	member	Health	2011-01-01 00:00:00	2011	97	senate
f	53	8	member	Health	2011-01-01 00:00:00	2011	65	senate
f	54	9	member	Health	2011-01-01 00:00:00	2011	108	senate
f	55	10	member	Health	2011-01-01 00:00:00	2011	71	senate
f	56	11	member	Health	2011-01-01 00:00:00	2011	75	senate
f	57	12	member	Health	2011-01-01 00:00:00	2011	63	senate
f	58	13	member	Health	2011-01-01 00:00:00	2011	74	senate
f	59	14	member	Health	2011-01-01 00:00:00	2011	76	senate
f	60	15	member	Health	2011-01-01 00:00:00	2011	122	senate
f	61	1	chair_person	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	107	senate
f	62	2	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	87	senate
f	63	3	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	83	senate
f	64	4	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	118	senate
f	65	5	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	88	senate
f	66	6	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	100	senate
f	67	7	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	76	senate
f	68	8	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	78	senate
f	69	9	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	72	senate
f	70	10	member	Racing, Gaming and Wagering	2011-01-01 00:00:00	2011	90	senate
f	71	1	chair_person	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	102	senate
f	72	2	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	93	senate
f	73	3	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	116	senate
f	74	4	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	87	senate
f	75	5	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	92	senate
f	76	6	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	68	senate
f	77	7	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	83	senate
f	78	8	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	98	senate
f	79	9	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	78	senate
f	80	10	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	64	senate
f	81	11	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	85	senate
f	82	12	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	84	senate
f	83	13	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	97	senate
f	84	14	member	Veterans, Homeland Security and Military Affairs	2011-01-01 00:00:00	2011	90	senate
f	85	1	chair_person	Education	2011-01-01 00:00:00	2011	93	senate
f	86	2	member	Education	2011-01-01 00:00:00	2011	112	senate
f	87	3	member	Education	2011-01-01 00:00:00	2011	103	senate
f	88	4	member	Education	2011-01-01 00:00:00	2011	113	senate
f	89	5	member	Education	2011-01-01 00:00:00	2011	101	senate
f	90	6	member	Education	2011-01-01 00:00:00	2011	105	senate
f	91	7	member	Education	2011-01-01 00:00:00	2011	100	senate
f	92	8	member	Education	2011-01-01 00:00:00	2011	111	senate
f	93	9	member	Education	2011-01-01 00:00:00	2011	96	senate
f	94	10	member	Education	2011-01-01 00:00:00	2011	65	senate
f	95	11	member	Education	2011-01-01 00:00:00	2011	82	senate
f	96	12	member	Education	2011-01-01 00:00:00	2011	78	senate
f	97	13	member	Education	2011-01-01 00:00:00	2011	85	senate
f	98	14	member	Education	2011-01-01 00:00:00	2011	66	senate
f	99	15	member	Education	2011-01-01 00:00:00	2011	72	senate
f	100	16	member	Education	2011-01-01 00:00:00	2011	75	senate
f	101	17	member	Education	2011-01-01 00:00:00	2011	67	senate
f	102	18	member	Education	2011-01-01 00:00:00	2011	94	senate
f	103	1	chair_person	Transportation	2011-01-01 00:00:00	2011	119	senate
f	104	2	member	Transportation	2011-01-01 00:00:00	2011	80	senate
f	105	3	member	Transportation	2011-01-01 00:00:00	2011	100	senate
f	106	4	member	Transportation	2011-01-01 00:00:00	2011	68	senate
f	107	5	member	Transportation	2011-01-01 00:00:00	2011	105	senate
f	108	6	member	Transportation	2011-01-01 00:00:00	2011	83	senate
f	109	7	member	Transportation	2011-01-01 00:00:00	2011	88	senate
f	110	8	member	Transportation	2011-01-01 00:00:00	2011	111	senate
f	111	9	member	Transportation	2011-01-01 00:00:00	2011	108	senate
f	112	10	member	Transportation	2011-01-01 00:00:00	2011	98	senate
f	113	11	member	Transportation	2011-01-01 00:00:00	2011	120	senate
f	114	12	member	Transportation	2011-01-01 00:00:00	2011	76	senate
f	115	13	member	Transportation	2011-01-01 00:00:00	2011	89	senate
f	116	14	member	Transportation	2011-01-01 00:00:00	2011	115	senate
f	117	15	member	Transportation	2011-01-01 00:00:00	2011	123	senate
f	118	16	member	Transportation	2011-01-01 00:00:00	2011	69	senate
f	119	17	member	Transportation	2011-01-01 00:00:00	2011	99	senate
f	120	18	member	Transportation	2011-01-01 00:00:00	2011	106	senate
f	121	19	member	Transportation	2011-01-01 00:00:00	2011	94	senate
f	122	1	chair_person	Labor	2011-01-01 00:00:00	2011	111	senate
f	123	2	member	Labor	2011-01-01 00:00:00	2011	86	senate
f	124	3	member	Labor	2011-01-01 00:00:00	2011	121	senate
f	125	4	member	Labor	2011-01-01 00:00:00	2011	92	senate
f	126	5	member	Labor	2011-01-01 00:00:00	2011	80	senate
f	127	6	member	Labor	2011-01-01 00:00:00	2011	101	senate
f	128	7	member	Labor	2011-01-01 00:00:00	2011	83	senate
f	129	8	member	Labor	2011-01-01 00:00:00	2011	91	senate
f	130	9	member	Labor	2011-01-01 00:00:00	2011	122	senate
f	131	10	member	Labor	2011-01-01 00:00:00	2011	78	senate
f	132	11	member	Labor	2011-01-01 00:00:00	2011	120	senate
f	133	12	member	Labor	2011-01-01 00:00:00	2011	77	senate
f	134	13	member	Labor	2011-01-01 00:00:00	2011	69	senate
f	135	14	member	Labor	2011-01-01 00:00:00	2011	71	senate
f	136	15	member	Labor	2011-01-01 00:00:00	2011	99	senate
f	137	16	member	Labor	2011-01-01 00:00:00	2011	70	senate
f	138	1	chair_person	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	84	senate
f	139	2	member	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	107	senate
f	140	3	member	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	118	senate
f	141	4	member	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	83	senate
f	142	5	member	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	72	senate
f	143	6	member	Alcoholism and Drug Abuse	2011-01-01 00:00:00	2011	66	senate
f	144	1	chair_person	Social Services	2011-01-01 00:00:00	2011	121	senate
f	145	2	member	Social Services	2011-01-01 00:00:00	2011	102	senate
f	146	3	member	Social Services	2011-01-01 00:00:00	2011	91	senate
f	147	4	member	Social Services	2011-01-01 00:00:00	2011	112	senate
f	148	5	member	Social Services	2011-01-01 00:00:00	2011	72	senate
f	149	6	member	Social Services	2011-01-01 00:00:00	2011	106	senate
f	150	1	chair_person	Agriculture	2011-01-01 00:00:00	2011	117	senate
f	151	2	member	Agriculture	2011-01-01 00:00:00	2011	121	senate
f	152	3	member	Agriculture	2011-01-01 00:00:00	2011	109	senate
f	153	4	member	Agriculture	2011-01-01 00:00:00	2011	100	senate
f	154	5	member	Agriculture	2011-01-01 00:00:00	2011	65	senate
f	155	6	member	Agriculture	2011-01-01 00:00:00	2011	108	senate
f	156	7	member	Agriculture	2011-01-01 00:00:00	2011	85	senate
f	157	8	member	Agriculture	2011-01-01 00:00:00	2011	123	senate
f	158	9	member	Agriculture	2011-01-01 00:00:00	2011	72	senate
f	159	10	member	Agriculture	2011-01-01 00:00:00	2011	89	senate
f	160	1	chair_person	Civil Service and Pensions	2011-01-01 00:00:00	2011	116	senate
f	161	2	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	119	senate
f	162	3	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	118	senate
f	163	4	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	103	senate
f	164	5	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	73	senate
f	165	6	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	91	senate
f	166	7	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	109	senate
f	167	8	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	78	senate
f	168	9	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	120	senate
f	169	10	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	69	senate
f	170	11	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	97	senate
f	171	12	member	Civil Service and Pensions	2011-01-01 00:00:00	2011	67	senate
f	172	1	chair_person	Consumer Protection	2011-01-01 00:00:00	2011	98	senate
f	173	2	member	Consumer Protection	2011-01-01 00:00:00	2011	102	senate
f	174	3	member	Consumer Protection	2011-01-01 00:00:00	2011	119	senate
f	175	4	member	Consumer Protection	2011-01-01 00:00:00	2011	73	senate
f	176	5	member	Consumer Protection	2011-01-01 00:00:00	2011	101	senate
f	177	6	member	Consumer Protection	2011-01-01 00:00:00	2011	109	senate
f	178	7	member	Consumer Protection	2011-01-01 00:00:00	2011	76	senate
f	179	8	member	Consumer Protection	2011-01-01 00:00:00	2011	74	senate
f	180	9	member	Consumer Protection	2011-01-01 00:00:00	2011	72	senate
f	181	10	member	Consumer Protection	2011-01-01 00:00:00	2011	106	senate
f	182	1	chair_person	Environmental Conservation	2011-01-01 00:00:00	2011	92	senate
f	183	2	member	Environmental Conservation	2011-01-01 00:00:00	2011	113	senate
f	184	3	member	Environmental Conservation	2011-01-01 00:00:00	2011	80	senate
f	185	4	member	Environmental Conservation	2011-01-01 00:00:00	2011	73	senate
f	186	5	member	Environmental Conservation	2011-01-01 00:00:00	2011	101	senate
f	187	6	member	Environmental Conservation	2011-01-01 00:00:00	2011	105	senate
f	188	7	member	Environmental Conservation	2011-01-01 00:00:00	2011	109	senate
f	189	8	member	Environmental Conservation	2011-01-01 00:00:00	2011	108	senate
f	190	9	member	Environmental Conservation	2011-01-01 00:00:00	2011	85	senate
f	191	10	member	Environmental Conservation	2011-01-01 00:00:00	2011	79	senate
f	192	11	member	Environmental Conservation	2011-01-01 00:00:00	2011	82	senate
f	193	12	member	Environmental Conservation	2011-01-01 00:00:00	2011	69	senate
f	194	13	member	Environmental Conservation	2011-01-01 00:00:00	2011	67	senate
f	195	14	member	Environmental Conservation	2011-01-01 00:00:00	2011	90	senate
f	196	1	chair_person	Rules	2011-01-01 00:00:00	2011	124	senate
f	197	2	vice_chair	Rules	2011-01-01 00:00:00	2011	104	senate
f	198	3	member	Rules	2011-01-01 00:00:00	2011	107	senate
f	199	4	member	Rules	2011-01-01 00:00:00	2011	64	senate
f	200	5	member	Rules	2011-01-01 00:00:00	2011	112	senate
f	201	6	member	Rules	2011-01-01 00:00:00	2011	93	senate
f	202	7	member	Rules	2011-01-01 00:00:00	2011	119	senate
f	203	8	member	Rules	2011-01-01 00:00:00	2011	118	senate
f	204	9	member	Rules	2011-01-01 00:00:00	2011	68	senate
f	205	10	member	Rules	2011-01-01 00:00:00	2011	113	senate
f	206	11	member	Rules	2011-01-01 00:00:00	2011	101	senate
f	207	12	member	Rules	2011-01-01 00:00:00	2011	105	senate
f	208	13	member	Rules	2011-01-01 00:00:00	2011	88	senate
f	209	14	member	Rules	2011-01-01 00:00:00	2011	65	senate
f	210	15	member	Rules	2011-01-01 00:00:00	2011	89	senate
f	211	16	member	Rules	2011-01-01 00:00:00	2011	90	senate
f	212	17	member	Rules	2011-01-01 00:00:00	2011	66	senate
f	213	18	member	Rules	2011-01-01 00:00:00	2011	120	senate
f	214	19	member	Rules	2011-01-01 00:00:00	2011	74	senate
f	215	20	member	Rules	2011-01-01 00:00:00	2011	95	senate
f	216	21	member	Rules	2011-01-01 00:00:00	2011	75	senate
f	217	22	member	Rules	2011-01-01 00:00:00	2011	110	senate
f	218	23	member	Rules	2011-01-01 00:00:00	2011	69	senate
f	219	24	member	Rules	2011-01-01 00:00:00	2011	79	senate
f	220	25	member	Rules	2011-01-01 00:00:00	2011	77	senate
f	221	1	chair_person	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	121	senate
f	222	2	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	119	senate
f	223	3	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	87	senate
f	224	4	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	80	senate
f	225	5	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	83	senate
f	226	6	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	111	senate
f	227	7	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	117	senate
f	228	8	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	123	senate
f	229	9	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	79	senate
f	230	10	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	74	senate
f	231	11	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	110	senate
f	232	12	member	Commerce, Economic Development and Small Business	2011-01-01 00:00:00	2011	90	senate
f	233	1	chair_person	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	73	senate
f	234	2	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	102	senate
f	235	3	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	121	senate
f	236	4	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	87	senate
f	237	5	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	92	senate
f	238	6	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	101	senate
f	239	7	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	107	senate
f	240	8	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	117	senate
f	241	9	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	67	senate
f	242	10	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	76	senate
f	243	11	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	81	senate
f	244	12	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	123	senate
f	245	13	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	84	senate
f	246	14	member	Cultural Affairs, Tourism, Parks and Recreation	2011-01-01 00:00:00	2011	89	senate
f	247	1	chair_person	Finance	2011-01-01 00:00:00	2011	70	senate
f	248	2	vice_chair	Finance	2011-01-01 00:00:00	2011	80	senate
f	249	3	member	Finance	2011-01-01 00:00:00	2011	86	senate
f	250	4	member	Finance	2011-01-01 00:00:00	2011	107	senate
f	251	5	member	Finance	2011-01-01 00:00:00	2011	112	senate
f	252	6	member	Finance	2011-01-01 00:00:00	2011	93	senate
f	253	7	member	Finance	2011-01-01 00:00:00	2011	119	senate
f	254	8	member	Finance	2011-01-01 00:00:00	2011	116	senate
f	255	9	member	Finance	2011-01-01 00:00:00	2011	118	senate
f	256	10	member	Finance	2011-01-01 00:00:00	2011	68	senate
f	257	11	member	Finance	2011-01-01 00:00:00	2011	113	senate
f	258	12	member	Finance	2011-01-01 00:00:00	2011	73	senate
f	259	13	member	Finance	2011-01-01 00:00:00	2011	88	senate
f	260	14	member	Finance	2011-01-01 00:00:00	2011	111	senate
f	261	15	member	Finance	2011-01-01 00:00:00	2011	65	senate
f	262	16	member	Finance	2011-01-01 00:00:00	2011	108	senate
f	263	17	member	Finance	2011-01-01 00:00:00	2011	101	senate
f	264	18	member	Finance	2011-01-01 00:00:00	2011	66	senate
f	265	19	member	Finance	2011-01-01 00:00:00	2011	115	senate
f	266	20	member	Finance	2011-01-01 00:00:00	2011	120	senate
f	267	21	member	Finance	2011-01-01 00:00:00	2011	81	senate
f	268	22	member	Finance	2011-01-01 00:00:00	2011	77	senate
f	269	23	member	Finance	2011-01-01 00:00:00	2011	95	senate
f	270	24	member	Finance	2011-01-01 00:00:00	2011	75	senate
f	271	25	member	Finance	2011-01-01 00:00:00	2011	82	senate
f	272	26	member	Finance	2011-01-01 00:00:00	2011	110	senate
f	273	27	member	Finance	2011-01-01 00:00:00	2011	122	senate
f	274	28	member	Finance	2011-01-01 00:00:00	2011	69	senate
f	275	29	member	Finance	2011-01-01 00:00:00	2011	71	senate
f	276	30	member	Finance	2011-01-01 00:00:00	2011	94	senate
f	277	31	member	Finance	2011-01-01 00:00:00	2011	90	senate
f	278	32	member	Finance	2011-01-01 00:00:00	2011	103	senate
f	279	33	member	Finance	2011-01-01 00:00:00	2011	87	senate
f	280	34	member	Finance	2011-01-01 00:00:00	2011	106	senate
f	281	1	chair_person	Local Government	2011-01-01 00:00:00	2011	91	senate
f	282	2	member	Local Government	2011-01-01 00:00:00	2011	102	senate
f	283	3	member	Local Government	2011-01-01 00:00:00	2011	73	senate
f	284	4	member	Local Government	2011-01-01 00:00:00	2011	83	senate
f	285	5	member	Local Government	2011-01-01 00:00:00	2011	117	senate
f	286	6	member	Local Government	2011-01-01 00:00:00	2011	90	senate
f	287	7	member	Local Government	2011-01-01 00:00:00	2011	84	senate
f	288	8	member	Local Government	2011-01-01 00:00:00	2011	82	senate
f	289	1	chair_person	Higher Education	2011-01-01 00:00:00	2011	113	senate
f	290	2	member	Higher Education	2011-01-01 00:00:00	2011	86	senate
f	291	3	member	Higher Education	2011-01-01 00:00:00	2011	93	senate
f	292	4	member	Higher Education	2011-01-01 00:00:00	2011	87	senate
f	293	5	member	Higher Education	2011-01-01 00:00:00	2011	92	senate
f	294	6	member	Higher Education	2011-01-01 00:00:00	2011	105	senate
f	295	7	member	Higher Education	2011-01-01 00:00:00	2011	117	senate
f	296	8	member	Higher Education	2011-01-01 00:00:00	2011	111	senate
f	297	9	member	Higher Education	2011-01-01 00:00:00	2011	65	senate
f	298	10	member	Higher Education	2011-01-01 00:00:00	2011	98	senate
f	299	11	member	Higher Education	2011-01-01 00:00:00	2011	94	senate
f	300	12	member	Higher Education	2011-01-01 00:00:00	2011	64	senate
f	301	13	member	Higher Education	2011-01-01 00:00:00	2011	123	senate
f	302	14	member	Higher Education	2011-01-01 00:00:00	2011	95	senate
f	303	15	member	Higher Education	2011-01-01 00:00:00	2011	82	senate
f	304	16	member	Higher Education	2011-01-01 00:00:00	2011	110	senate
f	305	17	member	Higher Education	2011-01-01 00:00:00	2011	71	senate
f	306	18	member	Higher Education	2011-01-01 00:00:00	2011	67	senate
f	307	1	chair_person	Aging	2011-01-01 00:00:00	2011	89	senate
f	308	2	member	Aging	2011-01-01 00:00:00	2011	102	senate
f	309	3	member	Aging	2011-01-01 00:00:00	2011	112	senate
f	310	4	member	Aging	2011-01-01 00:00:00	2011	116	senate
f	311	5	member	Aging	2011-01-01 00:00:00	2011	113	senate
f	312	6	member	Aging	2011-01-01 00:00:00	2011	111	senate
f	313	7	member	Aging	2011-01-01 00:00:00	2011	98	senate
f	314	8	member	Aging	2011-01-01 00:00:00	2011	115	senate
f	315	9	member	Aging	2011-01-01 00:00:00	2011	78	senate
f	316	10	member	Aging	2011-01-01 00:00:00	2011	85	senate
f	317	11	member	Aging	2011-01-01 00:00:00	2011	94	senate
f	318	12	member	Aging	2011-01-01 00:00:00	2011	74	senate
f	319	1	chair_person	Cities	2011-01-01 00:00:00	2011	103	senate
f	320	2	member	Cities	2011-01-01 00:00:00	2011	70	senate
f	321	3	member	Cities	2011-01-01 00:00:00	2011	92	senate
f	322	4	member	Cities	2011-01-01 00:00:00	2011	111	senate
f	323	5	member	Cities	2011-01-01 00:00:00	2011	85	senate
f	324	6	member	Cities	2011-01-01 00:00:00	2011	82	senate
f	325	1	chair_person	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	83	senate
f	326	2	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	102	senate
f	327	3	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	107	senate
f	328	4	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	118	senate
f	329	5	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	65	senate
f	330	6	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	98	senate
f	331	7	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	72	senate
f	332	8	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	81	senate
f	333	9	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	84	senate
f	334	10	member	Mental Health and Developmental Disabilities	2011-01-01 00:00:00	2011	122	senate
f	335	1	chair_person	Children and Families	2011-01-01 00:00:00	2011	97	senate
f	336	2	member	Children and Families	2011-01-01 00:00:00	2011	80	senate
f	337	3	member	Children and Families	2011-01-01 00:00:00	2011	96	senate
f	338	4	member	Children and Families	2011-01-01 00:00:00	2011	108	senate
f	339	5	member	Children and Families	2011-01-01 00:00:00	2011	75	senate
f	340	6	member	Children and Families	2011-01-01 00:00:00	2011	81	senate
f	341	1	chair_person	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	108	senate
f	342	2	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	107	senate
f	343	3	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	121	senate
f	344	4	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	92	senate
f	345	5	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	117	senate
f	346	6	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	79	senate
f	347	7	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	115	senate
f	348	8	member	Housing, Construction and Community Development	2011-01-01 00:00:00	2011	95	senate
f	349	1	chair_person	Codes	2011-01-01 00:00:00	2011	96	senate
f	350	2	member	Codes	2011-01-01 00:00:00	2011	70	senate
f	351	3	member	Codes	2011-01-01 00:00:00	2011	93	senate
f	352	4	member	Codes	2011-01-01 00:00:00	2011	119	senate
f	353	5	member	Codes	2011-01-01 00:00:00	2011	121	senate
f	354	6	member	Codes	2011-01-01 00:00:00	2011	116	senate
f	355	7	member	Codes	2011-01-01 00:00:00	2011	103	senate
f	356	8	member	Codes	2011-01-01 00:00:00	2011	88	senate
f	357	9	member	Codes	2011-01-01 00:00:00	2011	109	senate
f	358	10	member	Codes	2011-01-01 00:00:00	2011	77	senate
f	359	11	member	Codes	2011-01-01 00:00:00	2011	81	senate
f	360	12	member	Codes	2011-01-01 00:00:00	2011	79	senate
f	361	13	member	Codes	2011-01-01 00:00:00	2011	72	senate
f	362	14	member	Codes	2011-01-01 00:00:00	2011	110	senate
f	363	15	member	Codes	2011-01-01 00:00:00	2011	69	senate
f	364	16	member	Codes	2011-01-01 00:00:00	2011	106	senate
f	365	1	chair_person	Banks	2011-01-01 00:00:00	2011	87	senate
f	366	2	vice_chair	Banks	2011-01-01 00:00:00	2011	112	senate
f	367	3	member	Banks	2011-01-01 00:00:00	2011	107	senate
f	368	4	member	Banks	2011-01-01 00:00:00	2011	70	senate
f	369	5	member	Banks	2011-01-01 00:00:00	2011	121	senate
f	370	6	member	Banks	2011-01-01 00:00:00	2011	116	senate
f	371	7	member	Banks	2011-01-01 00:00:00	2011	80	senate
f	372	8	member	Banks	2011-01-01 00:00:00	2011	109	senate
f	373	9	member	Banks	2011-01-01 00:00:00	2011	101	senate
f	374	10	member	Banks	2011-01-01 00:00:00	2011	100	senate
f	375	11	member	Banks	2011-01-01 00:00:00	2011	99	senate
f	376	12	member	Banks	2011-01-01 00:00:00	2011	66	senate
f	377	13	member	Banks	2011-01-01 00:00:00	2011	115	senate
f	378	14	member	Banks	2011-01-01 00:00:00	2011	95	senate
f	379	15	member	Banks	2011-01-01 00:00:00	2011	97	senate
f	380	16	member	Banks	2011-01-01 00:00:00	2011	71	senate
f	381	17	member	Banks	2011-01-01 00:00:00	2011	89	senate
f	382	18	member	Banks	2011-01-01 00:00:00	2011	64	senate
f	383	19	member	Banks	2011-01-01 00:00:00	2011	85	senate
f	384	1	chair_person	Energy and Telecommunications	2011-01-01 00:00:00	2011	105	senate
f	385	2	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	86	senate
f	386	3	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	119	senate
f	387	4	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	109	senate
f	388	5	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	87	senate
f	389	6	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	117	senate
f	390	7	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	111	senate
f	391	8	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	110	senate
f	392	9	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	76	senate
f	393	10	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	77	senate
f	394	11	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	123	senate
f	395	12	member	Energy and Telecommunications	2011-01-01 00:00:00	2011	120	senate
f	396	1	chair_person	Elections	2011-01-01 00:00:00	2011	109	senate
f	397	2	member	Elections	2011-01-01 00:00:00	2011	102	senate
f	398	3	member	Elections	2011-01-01 00:00:00	2011	121	senate
f	399	4	member	Elections	2011-01-01 00:00:00	2011	100	senate
f	400	5	member	Elections	2011-01-01 00:00:00	2011	88	senate
f	401	6	member	Elections	2011-01-01 00:00:00	2011	78	senate
f	402	7	member	Elections	2011-01-01 00:00:00	2011	120	senate
f	403	8	member	Elections	2011-01-01 00:00:00	2011	64	senate
f	404	1	chair_person	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	88	senate
f	405	2	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	87	senate
f	406	3	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	121	senate
f	407	4	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	100	senate
f	408	5	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	73	senate
f	409	6	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	105	senate
f	410	7	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	117	senate
f	411	8	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	70	senate
f	412	9	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	71	senate
f	413	10	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	74	senate
f	414	11	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	123	senate
f	415	12	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	75	senate
f	416	13	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	122	senate
f	417	14	member	Crime Victims, Crime and Correction	2011-01-01 00:00:00	2011	79	senate
f	418	1	chair_person	Insurance	2011-01-01 00:00:00	2011	65	senate
f	419	2	member	Insurance	2011-01-01 00:00:00	2011	93	senate
f	420	3	member	Insurance	2011-01-01 00:00:00	2011	91	senate
f	421	4	member	Insurance	2011-01-01 00:00:00	2011	116	senate
f	422	5	member	Insurance	2011-01-01 00:00:00	2011	92	senate
f	423	6	member	Insurance	2011-01-01 00:00:00	2011	103	senate
f	424	7	member	Insurance	2011-01-01 00:00:00	2011	68	senate
f	425	8	member	Insurance	2011-01-01 00:00:00	2011	113	senate
f	426	9	member	Insurance	2011-01-01 00:00:00	2011	96	senate
f	427	10	member	Insurance	2011-01-01 00:00:00	2011	108	senate
f	428	11	member	Insurance	2011-01-01 00:00:00	2011	66	senate
f	429	12	member	Insurance	2011-01-01 00:00:00	2011	115	senate
f	430	13	member	Insurance	2011-01-01 00:00:00	2011	79	senate
f	431	14	member	Insurance	2011-01-01 00:00:00	2011	123	senate
f	432	15	member	Insurance	2011-01-01 00:00:00	2011	110	senate
f	433	16	member	Insurance	2011-01-01 00:00:00	2011	122	senate
f	434	17	member	Insurance	2011-01-01 00:00:00	2011	99	senate
f	435	18	member	Insurance	2011-01-01 00:00:00	2011	94	senate
f	436	1	chair_person	Investigations and Government Operations	2013-01-01 00:00:00	2013	163	senate
f	437	2	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	126	senate
f	438	3	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	178	senate
f	439	4	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	150	senate
f	440	5	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	183	senate
f	441	6	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	160	senate
f	442	7	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	176	senate
f	443	8	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	177	senate
f	444	9	member	Investigations and Government Operations	2013-01-01 00:00:00	2013	157	senate
f	445	1	chair_person	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	162	senate
f	446	2	member	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	155	senate
f	447	3	member	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	130	senate
f	449	5	member	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	131	senate
f	450	6	member	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	157	senate
f	451	1	chair_person	Ethics	2013-01-01 00:00:00	2013	141	senate
f	452	2	member	Ethics	2013-01-01 00:00:00	2013	151	senate
f	453	3	member	Ethics	2013-01-01 00:00:00	2013	165	senate
f	454	4	member	Ethics	2013-01-01 00:00:00	2013	138	senate
f	455	5	member	Ethics	2013-01-01 00:00:00	2013	133	senate
f	456	6	member	Ethics	2013-01-01 00:00:00	2013	174	senate
f	457	1	chair_person	Judiciary	2013-01-01 00:00:00	2013	169	senate
f	458	2	member	Judiciary	2013-01-01 00:00:00	2013	132	senate
f	459	3	member	Judiciary	2013-01-01 00:00:00	2013	155	senate
f	460	4	member	Judiciary	2013-01-01 00:00:00	2013	180	senate
f	461	5	member	Judiciary	2013-01-01 00:00:00	2013	165	senate
f	462	6	member	Judiciary	2013-01-01 00:00:00	2013	175	senate
f	463	7	member	Judiciary	2013-01-01 00:00:00	2013	135	senate
f	464	8	member	Judiciary	2013-01-01 00:00:00	2013	150	senate
f	465	9	member	Judiciary	2013-01-01 00:00:00	2013	183	senate
f	466	10	member	Judiciary	2013-01-01 00:00:00	2013	159	senate
f	467	11	member	Judiciary	2013-01-01 00:00:00	2013	162	senate
f	468	12	member	Judiciary	2013-01-01 00:00:00	2013	160	senate
f	469	13	member	Judiciary	2013-01-01 00:00:00	2013	136	senate
f	470	14	member	Judiciary	2013-01-01 00:00:00	2013	174	senate
f	471	15	member	Judiciary	2013-01-01 00:00:00	2013	140	senate
f	472	16	member	Judiciary	2013-01-01 00:00:00	2013	131	senate
f	473	17	member	Judiciary	2013-01-01 00:00:00	2013	156	senate
f	474	18	member	Judiciary	2013-01-01 00:00:00	2013	176	senate
f	482	3	member	Health	2013-01-01 00:00:00	2013	151	senate
f	483	4	member	Health	2013-01-01 00:00:00	2013	181	senate
f	484	5	member	Health	2013-01-01 00:00:00	2013	178	senate
f	485	6	member	Health	2013-01-01 00:00:00	2013	130	senate
f	486	7	member	Health	2013-01-01 00:00:00	2013	159	senate
f	487	8	member	Health	2013-01-01 00:00:00	2013	127	senate
f	488	9	member	Health	2013-01-01 00:00:00	2013	170	senate
f	489	10	member	Health	2013-01-01 00:00:00	2013	133	senate
f	490	11	member	Health	2013-01-01 00:00:00	2013	168	senate
f	491	12	member	Health	2013-01-01 00:00:00	2013	136	senate
f	492	13	member	Health	2013-01-01 00:00:00	2013	187	senate
f	493	14	member	Health	2013-01-01 00:00:00	2013	148	senate
f	494	15	member	Health	2013-01-01 00:00:00	2013	176	senate
f	495	16	member	Health	2013-01-01 00:00:00	2013	142	senate
f	496	17	member	Health	2013-01-01 00:00:00	2013	153	senate
f	497	1	chair_person	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	169	senate
f	498	2	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	141	senate
f	499	3	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	149	senate
f	500	4	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	144	senate
f	501	5	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	150	senate
f	502	6	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	162	senate
f	503	7	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	139	senate
f	504	8	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	143	senate
f	505	9	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	134	senate
f	506	10	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	126	senate
f	507	11	member	Racing, Gaming and Wagering	2013-01-01 00:00:00	2013	128	senate
f	508	1	chair_person	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	164	senate
f	509	2	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	126	senate
f	510	3	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	155	senate
f	511	4	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	178	senate
f	512	5	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	149	senate
f	513	6	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	154	senate
f	514	7	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	130	senate
f	515	8	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	160	senate
f	516	9	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	139	senate
f	517	10	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	158	senate
f	519	12	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	128	senate
f	553	14	member	Transportation	2013-01-01 00:00:00	2013	131	senate
f	554	15	member	Transportation	2013-01-01 00:00:00	2013	157	senate
f	560	2	member	Labor	2013-01-01 00:00:00	2013	164	senate
f	562	4	member	Labor	2013-01-01 00:00:00	2013	182	senate
f	564	6	member	Labor	2013-01-01 00:00:00	2013	180	senate
f	565	7	member	Labor	2013-01-01 00:00:00	2013	163	senate
f	566	8	member	Labor	2013-01-01 00:00:00	2013	144	senate
f	567	9	member	Labor	2013-01-01 00:00:00	2013	153	senate
f	568	10	member	Labor	2013-01-01 00:00:00	2013	173	senate
f	571	13	member	Labor	2013-01-01 00:00:00	2013	137	senate
f	576	2	member	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	169	senate
f	577	3	member	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	179	senate
f	580	6	member	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	172	senate
f	581	1	chair_person	Social Services	2013-01-01 00:00:00	2013	147	senate
f	582	2	member	Social Services	2013-01-01 00:00:00	2013	164	senate
f	584	4	member	Social Services	2013-01-01 00:00:00	2013	153	senate
f	585	5	member	Social Services	2013-01-01 00:00:00	2013	134	senate
f	586	6	member	Social Services	2013-01-01 00:00:00	2013	157	senate
f	587	1	chair_person	Agriculture	2013-01-01 00:00:00	2013	179	senate
f	588	2	member	Agriculture	2013-01-01 00:00:00	2013	182	senate
f	589	3	member	Agriculture	2013-01-01 00:00:00	2013	183	senate
f	590	4	member	Agriculture	2013-01-01 00:00:00	2013	162	senate
f	591	5	member	Agriculture	2013-01-01 00:00:00	2013	127	senate
f	593	7	member	Agriculture	2013-01-01 00:00:00	2013	171	senate
f	594	8	member	Agriculture	2013-01-01 00:00:00	2013	158	senate
f	595	9	member	Agriculture	2013-01-01 00:00:00	2013	168	senate
f	448	4	member	Corporations, Authorities and Commissions	2013-01-01 00:00:00	2013	153	senate
f	475	19	member	Judiciary	2013-01-01 00:00:00	2013	137	senate
f	476	20	member	Judiciary	2013-01-01 00:00:00	2013	139	senate
f	477	21	member	Judiciary	2013-01-01 00:00:00	2013	157	senate
f	478	22	member	Judiciary	2013-01-01 00:00:00	2013	154	senate
f	479	23	member	Judiciary	2013-01-01 00:00:00	2013	147	senate
f	480	1	chair_person	Health	2013-01-01 00:00:00	2013	180	senate
f	481	2	member	Health	2013-01-01 00:00:00	2013	164	senate
f	518	11	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	134	senate
f	520	13	member	Veterans, Homeland Security and Military Affairs	2013-01-01 00:00:00	2013	143	senate
f	521	1	chair_person	Education	2013-01-01 00:00:00	2013	155	senate
f	522	2	member	Education	2013-01-01 00:00:00	2013	151	senate
f	523	3	member	Education	2013-01-01 00:00:00	2013	165	senate
f	524	4	member	Education	2013-01-01 00:00:00	2013	175	senate
f	525	5	member	Education	2013-01-01 00:00:00	2013	163	senate
f	526	6	member	Education	2013-01-01 00:00:00	2013	167	senate
f	527	7	member	Education	2013-01-01 00:00:00	2013	162	senate
f	528	8	member	Education	2013-01-01 00:00:00	2013	173	senate
f	529	9	member	Education	2013-01-01 00:00:00	2013	135	senate
f	530	10	member	Education	2013-01-01 00:00:00	2013	127	senate
f	531	11	member	Education	2013-01-01 00:00:00	2013	171	senate
f	532	12	member	Education	2013-01-01 00:00:00	2013	143	senate
f	533	13	member	Education	2013-01-01 00:00:00	2013	139	senate
f	534	14	member	Education	2013-01-01 00:00:00	2013	174	senate
f	535	15	member	Education	2013-01-01 00:00:00	2013	168	senate
f	536	16	member	Education	2013-01-01 00:00:00	2013	156	senate
f	537	17	member	Education	2013-01-01 00:00:00	2013	128	senate
f	538	18	member	Education	2013-01-01 00:00:00	2013	147	senate
f	539	19	member	Education	2013-01-01 00:00:00	2013	187	senate
f	540	1	chair_person	Transportation	2013-01-01 00:00:00	2013	173	senate
f	541	2	member	Transportation	2013-01-01 00:00:00	2013	126	senate
f	542	3	member	Transportation	2013-01-01 00:00:00	2013	182	senate
f	543	4	member	Transportation	2013-01-01 00:00:00	2013	130	senate
f	544	5	member	Transportation	2013-01-01 00:00:00	2013	167	senate
f	545	6	member	Transportation	2013-01-01 00:00:00	2013	150	senate
f	546	7	member	Transportation	2013-01-01 00:00:00	2013	183	senate
f	547	8	member	Transportation	2013-01-01 00:00:00	2013	162	senate
f	548	9	member	Transportation	2013-01-01 00:00:00	2013	170	senate
f	549	10	member	Transportation	2013-01-01 00:00:00	2013	160	senate
f	550	11	member	Transportation	2013-01-01 00:00:00	2013	137	senate
f	551	12	member	Transportation	2013-01-01 00:00:00	2013	177	senate
f	552	13	member	Transportation	2013-01-01 00:00:00	2013	146	senate
f	555	16	member	Transportation	2013-01-01 00:00:00	2013	156	senate
f	556	17	member	Transportation	2013-01-01 00:00:00	2013	158	senate
f	557	18	member	Transportation	2013-01-01 00:00:00	2013	163	senate
f	558	19	member	Transportation	2013-01-01 00:00:00	2013	147	senate
f	559	1	chair_person	Labor	2013-01-01 00:00:00	2013	159	senate
f	561	3	member	Labor	2013-01-01 00:00:00	2013	132	senate
f	563	5	member	Labor	2013-01-01 00:00:00	2013	154	senate
f	569	11	member	Labor	2013-01-01 00:00:00	2013	187	senate
f	570	12	member	Labor	2013-01-01 00:00:00	2013	139	senate
f	572	14	member	Labor	2013-01-01 00:00:00	2013	131	senate
f	573	15	member	Labor	2013-01-01 00:00:00	2013	133	senate
f	574	16	member	Labor	2013-01-01 00:00:00	2013	134	senate
f	575	1	chair_person	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	141	senate
f	578	4	member	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	126	senate
f	579	5	member	Alcoholism and Drug Abuse	2013-01-01 00:00:00	2013	134	senate
f	583	3	member	Social Services	2013-01-01 00:00:00	2013	151	senate
f	592	6	member	Agriculture	2013-01-01 00:00:00	2013	170	senate
f	596	10	member	Agriculture	2013-01-01 00:00:00	2013	142	senate
f	597	11	member	Agriculture	2013-01-01 00:00:00	2013	128	senate
f	598	1	chair_person	Civil Service and Pensions	2013-01-01 00:00:00	2013	178	senate
f	622	3	member	Environmental Conservation	2013-01-01 00:00:00	2013	135	senate
f	623	4	member	Environmental Conservation	2013-01-01 00:00:00	2013	163	senate
f	626	7	member	Environmental Conservation	2013-01-01 00:00:00	2013	170	senate
f	627	8	member	Environmental Conservation	2013-01-01 00:00:00	2013	140	senate
f	630	11	member	Environmental Conservation	2013-01-01 00:00:00	2013	128	senate
f	631	12	member	Environmental Conservation	2013-01-01 00:00:00	2013	147	senate
f	635	3	member	Rules	2013-01-01 00:00:00	2013	126	senate
f	640	8	member	Rules	2013-01-01 00:00:00	2013	175	senate
f	642	10	member	Rules	2013-01-01 00:00:00	2013	163	senate
f	643	11	member	Rules	2013-01-01 00:00:00	2013	167	senate
f	644	12	member	Rules	2013-01-01 00:00:00	2013	150	senate
f	645	13	member	Rules	2013-01-01 00:00:00	2013	127	senate
f	647	15	member	Rules	2013-01-01 00:00:00	2013	152	senate
f	654	22	member	Rules	2013-01-01 00:00:00	2013	131	senate
f	655	23	member	Rules	2013-01-01 00:00:00	2013	140	senate
f	656	24	member	Rules	2013-01-01 00:00:00	2013	138	senate
f	657	25	member	Rules	2013-01-01 00:00:00	2013	135	senate
f	660	3	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	149	senate
f	662	5	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	141	senate
f	664	7	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	146	senate
f	667	10	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	148	senate
f	669	12	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	135	senate
f	670	1	chair_person	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	135	senate
f	671	2	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	169	senate
f	672	3	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	149	senate
f	673	4	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	154	senate
f	674	5	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	163	senate
f	675	6	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	144	senate
f	750	4	member	Aging	2013-01-01 00:00:00	2013	175	senate
f	599	2	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	179	senate
f	600	3	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	165	senate
f	601	4	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	180	senate
f	602	5	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	153	senate
f	603	6	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	159	senate
f	604	7	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	134	senate
f	605	8	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	139	senate
f	606	9	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	137	senate
f	607	10	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	131	senate
f	608	11	member	Civil Service and Pensions	2013-01-01 00:00:00	2013	167	senate
f	609	1	chair_person	Consumer Protection	2013-01-01 00:00:00	2013	160	senate
f	610	2	member	Consumer Protection	2013-01-01 00:00:00	2013	141	senate
f	611	3	member	Consumer Protection	2013-01-01 00:00:00	2013	135	senate
f	612	4	member	Consumer Protection	2013-01-01 00:00:00	2013	144	senate
f	613	5	member	Consumer Protection	2013-01-01 00:00:00	2013	167	senate
f	614	6	member	Consumer Protection	2013-01-01 00:00:00	2013	159	senate
f	615	7	member	Consumer Protection	2013-01-01 00:00:00	2013	176	senate
f	616	8	member	Consumer Protection	2013-01-01 00:00:00	2013	142	senate
f	617	9	member	Consumer Protection	2013-01-01 00:00:00	2013	158	senate
f	618	10	member	Consumer Protection	2013-01-01 00:00:00	2013	143	senate
f	619	11	member	Consumer Protection	2013-01-01 00:00:00	2013	173	senate
f	620	1	chair_person	Environmental Conservation	2013-01-01 00:00:00	2013	154	senate
f	621	2	member	Environmental Conservation	2013-01-01 00:00:00	2013	175	senate
f	624	5	member	Environmental Conservation	2013-01-01 00:00:00	2013	167	senate
f	625	6	member	Environmental Conservation	2013-01-01 00:00:00	2013	183	senate
f	628	9	member	Environmental Conservation	2013-01-01 00:00:00	2013	142	senate
f	629	10	member	Environmental Conservation	2013-01-01 00:00:00	2013	143	senate
f	632	13	member	Environmental Conservation	2013-01-01 00:00:00	2013	148	senate
f	633	1	chair_person	Rules	2013-01-01 00:00:00	2013	184	senate
f	634	2	member	Rules	2013-01-01 00:00:00	2013	169	senate
f	636	4	member	Rules	2013-01-01 00:00:00	2013	151	senate
f	637	5	member	Rules	2013-01-01 00:00:00	2013	155	senate
f	638	6	member	Rules	2013-01-01 00:00:00	2013	180	senate
f	639	7	member	Rules	2013-01-01 00:00:00	2013	130	senate
f	641	9	member	Rules	2013-01-01 00:00:00	2013	166	senate
f	646	14	member	Rules	2013-01-01 00:00:00	2013	171	senate
f	648	16	member	Rules	2013-01-01 00:00:00	2013	174	senate
f	649	17	member	Rules	2013-01-01 00:00:00	2013	137	senate
f	650	18	member	Rules	2013-01-01 00:00:00	2013	136	senate
f	651	19	member	Rules	2013-01-01 00:00:00	2013	129	senate
f	652	20	member	Rules	2013-01-01 00:00:00	2013	168	senate
f	653	21	member	Rules	2013-01-01 00:00:00	2013	172	senate
f	658	1	chair_person	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	153	senate
f	659	2	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	182	senate
f	661	4	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	173	senate
f	663	6	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	181	senate
f	665	8	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	136	senate
f	666	9	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	134	senate
f	668	11	member	Commerce, Economic Development and Small Business	2013-01-01 00:00:00	2013	171	senate
f	676	7	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	179	senate
f	677	8	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	142	senate
f	678	9	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	146	senate
f	679	10	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	176	senate
f	681	12	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	148	senate
f	682	1	chair_person	Finance	2013-01-01 00:00:00	2013	132	senate
f	683	2	member	Finance	2013-01-01 00:00:00	2013	169	senate
f	686	5	member	Finance	2013-01-01 00:00:00	2013	178	senate
f	687	6	member	Finance	2013-01-01 00:00:00	2013	180	senate
f	688	7	member	Finance	2013-01-01 00:00:00	2013	149	senate
f	689	8	member	Finance	2013-01-01 00:00:00	2013	130	senate
f	690	9	member	Finance	2013-01-01 00:00:00	2013	175	senate
f	691	10	member	Finance	2013-01-01 00:00:00	2013	135	senate
f	697	16	member	Finance	2013-01-01 00:00:00	2013	154	senate
f	698	17	member	Finance	2013-01-01 00:00:00	2013	165	senate
f	701	20	member	Finance	2013-01-01 00:00:00	2013	159	senate
f	702	21	member	Finance	2013-01-01 00:00:00	2013	129	senate
f	705	24	member	Finance	2013-01-01 00:00:00	2013	133	senate
f	706	25	member	Finance	2013-01-01 00:00:00	2013	138	senate
f	709	28	member	Finance	2013-01-01 00:00:00	2013	172	senate
f	710	29	member	Finance	2013-01-01 00:00:00	2013	187	senate
f	713	32	member	Finance	2013-01-01 00:00:00	2013	157	senate
f	714	33	member	Finance	2013-01-01 00:00:00	2013	146	senate
f	715	34	member	Finance	2013-01-01 00:00:00	2013	140	senate
f	716	35	member	Finance	2013-01-01 00:00:00	2013	136	senate
f	723	5	member	Local Government	2013-01-01 00:00:00	2013	179	senate
f	724	6	member	Local Government	2013-01-01 00:00:00	2013	158	senate
f	725	7	member	Local Government	2013-01-01 00:00:00	2013	143	senate
f	726	8	member	Local Government	2013-01-01 00:00:00	2013	148	senate
f	729	2	member	Higher Education	2013-01-01 00:00:00	2013	182	senate
f	730	3	member	Higher Education	2013-01-01 00:00:00	2013	155	senate
f	733	6	member	Higher Education	2013-01-01 00:00:00	2013	167	senate
f	734	7	member	Higher Education	2013-01-01 00:00:00	2013	179	senate
f	737	10	member	Higher Education	2013-01-01 00:00:00	2013	160	senate
f	738	11	member	Higher Education	2013-01-01 00:00:00	2013	171	senate
f	741	14	member	Higher Education	2013-01-01 00:00:00	2013	146	senate
f	742	15	member	Higher Education	2013-01-01 00:00:00	2013	129	senate
f	746	19	member	Higher Education	2013-01-01 00:00:00	2013	140	senate
f	747	1	chair_person	Aging	2013-01-01 00:00:00	2013	171	senate
f	748	2	member	Aging	2013-01-01 00:00:00	2013	181	senate
f	749	3	member	Aging	2013-01-01 00:00:00	2013	178	senate
f	680	11	member	Cultural Affairs, Tourism, Parks and Recreation	2013-01-01 00:00:00	2013	158	senate
f	684	3	member	Finance	2013-01-01 00:00:00	2013	151	senate
f	685	4	member	Finance	2013-01-01 00:00:00	2013	155	senate
f	692	11	member	Finance	2013-01-01 00:00:00	2013	150	senate
f	693	12	member	Finance	2013-01-01 00:00:00	2013	173	senate
f	694	13	member	Finance	2013-01-01 00:00:00	2013	127	senate
f	695	14	member	Finance	2013-01-01 00:00:00	2013	170	senate
f	696	15	member	Finance	2013-01-01 00:00:00	2013	163	senate
f	699	18	member	Finance	2013-01-01 00:00:00	2013	183	senate
f	700	19	member	Finance	2013-01-01 00:00:00	2013	162	senate
f	703	22	member	Finance	2013-01-01 00:00:00	2013	177	senate
f	704	23	member	Finance	2013-01-01 00:00:00	2013	137	senate
f	707	26	member	Finance	2013-01-01 00:00:00	2013	174	senate
f	708	27	member	Finance	2013-01-01 00:00:00	2013	168	senate
f	711	30	member	Finance	2013-01-01 00:00:00	2013	131	senate
f	712	31	member	Finance	2013-01-01 00:00:00	2013	156	senate
f	717	36	member	Finance	2013-01-01 00:00:00	2013	171	senate
f	718	37	member	Finance	2013-01-01 00:00:00	2013	153	senate
f	719	1	chair_person	Local Government	2013-01-01 00:00:00	2013	153	senate
f	720	2	member	Local Government	2013-01-01 00:00:00	2013	164	senate
f	721	3	member	Local Government	2013-01-01 00:00:00	2013	141	senate
f	722	4	member	Local Government	2013-01-01 00:00:00	2013	144	senate
f	727	9	member	Local Government	2013-01-01 00:00:00	2013	171	senate
f	728	1	chair_person	Higher Education	2013-01-01 00:00:00	2013	175	senate
f	731	4	member	Higher Education	2013-01-01 00:00:00	2013	149	senate
f	732	5	member	Higher Education	2013-01-01 00:00:00	2013	154	senate
f	735	8	member	Higher Education	2013-01-01 00:00:00	2013	173	senate
f	736	9	member	Higher Education	2013-01-01 00:00:00	2013	127	senate
f	739	12	member	Higher Education	2013-01-01 00:00:00	2013	159	senate
f	740	13	member	Higher Education	2013-01-01 00:00:00	2013	156	senate
f	743	16	member	Higher Education	2013-01-01 00:00:00	2013	172	senate
f	744	17	member	Higher Education	2013-01-01 00:00:00	2013	133	senate
f	745	18	member	Higher Education	2013-01-01 00:00:00	2013	142	senate
f	751	5	member	Aging	2013-01-01 00:00:00	2013	144	senate
f	752	6	member	Aging	2013-01-01 00:00:00	2013	160	senate
f	760	4	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	149	senate
f	764	8	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	146	senate
f	765	9	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	143	senate
f	766	1	chair_person	Cities	2013-01-01 00:00:00	2013	165	senate
f	767	2	member	Cities	2013-01-01 00:00:00	2013	132	senate
f	769	4	member	Cities	2013-01-01 00:00:00	2013	164	senate
f	771	6	member	Cities	2013-01-01 00:00:00	2013	157	senate
f	774	3	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	169	senate
f	776	5	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	180	senate
f	779	8	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	128	senate
f	780	9	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	129	senate
f	782	11	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	133	senate
f	784	2	member	Children and Families	2013-01-01 00:00:00	2013	169	senate
f	786	4	member	Children and Families	2013-01-01 00:00:00	2013	159	senate
f	788	6	member	Children and Families	2013-01-01 00:00:00	2013	128	senate
f	789	1	chair_person	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	170	senate
f	792	4	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	141	senate
f	794	6	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	140	senate
f	795	7	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	177	senate
f	796	8	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	129	senate
f	797	9	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	147	senate
f	798	1	chair_person	New York City Education Subcommittee	2013-01-01 00:00:00	2013	181	senate
f	801	4	member	New York City Education Subcommittee	2013-01-01 00:00:00	2013	131	senate
f	802	5	member	New York City Education Subcommittee	2013-01-01 00:00:00	2013	156	senate
f	805	2	member	Codes	2013-01-01 00:00:00	2013	132	senate
f	806	3	member	Codes	2013-01-01 00:00:00	2013	155	senate
f	808	5	member	Codes	2013-01-01 00:00:00	2013	178	senate
f	809	6	member	Codes	2013-01-01 00:00:00	2013	165	senate
f	810	7	member	Codes	2013-01-01 00:00:00	2013	183	senate
f	815	12	member	Codes	2013-01-01 00:00:00	2013	176	senate
f	816	13	member	Codes	2013-01-01 00:00:00	2013	148	senate
f	820	1	chair_person	Banks	2013-01-01 00:00:00	2013	149	senate
f	825	6	member	Banks	2013-01-01 00:00:00	2013	144	senate
f	826	7	member	Banks	2013-01-01 00:00:00	2013	183	senate
f	827	8	member	Banks	2013-01-01 00:00:00	2013	163	senate
f	828	9	member	Banks	2013-01-01 00:00:00	2013	162	senate
f	829	10	member	Banks	2013-01-01 00:00:00	2013	153	senate
f	830	11	member	Banks	2013-01-01 00:00:00	2013	171	senate
f	835	16	member	Banks	2013-01-01 00:00:00	2013	134	senate
f	836	17	member	Banks	2013-01-01 00:00:00	2013	158	senate
f	840	2	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	126	senate
f	841	3	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	183	senate
f	844	6	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	173	senate
f	845	7	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	172	senate
f	847	9	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	137	senate
f	852	3	member	Elections	2013-01-01 00:00:00	2013	182	senate
f	855	6	member	Elections	2013-01-01 00:00:00	2013	128	senate
f	856	7	member	Elections	2013-01-01 00:00:00	2013	137	senate
f	863	5	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	135	senate
f	864	6	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	167	senate
f	865	7	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	150	senate
f	866	8	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	179	senate
f	867	9	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	136	senate
f	870	12	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	187	senate
f	871	13	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	133	senate
f	753	7	member	Aging	2013-01-01 00:00:00	2013	177	senate
f	754	8	member	Aging	2013-01-01 00:00:00	2013	139	senate
f	755	9	member	Aging	2013-01-01 00:00:00	2013	156	senate
f	756	10	member	Aging	2013-01-01 00:00:00	2013	134	senate
f	757	1	chair_person	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	163	senate
f	758	2	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	173	senate
f	759	3	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	182	senate
f	761	5	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	154	senate
f	762	6	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	126	senate
f	763	7	member	Infrastructure and Capital Investment	2013-01-01 00:00:00	2013	176	senate
f	768	3	member	Cities	2013-01-01 00:00:00	2013	154	senate
f	770	5	member	Cities	2013-01-01 00:00:00	2013	174	senate
f	772	1	chair_person	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	126	senate
f	773	2	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	164	senate
f	775	4	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	181	senate
f	777	6	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	127	senate
f	778	7	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	160	senate
f	781	10	member	Mental Health and Developmental Disabilities	2013-01-01 00:00:00	2013	143	senate
f	783	1	chair_person	Children and Families	2013-01-01 00:00:00	2013	181	senate
f	785	3	member	Children and Families	2013-01-01 00:00:00	2013	170	senate
f	787	5	member	Children and Families	2013-01-01 00:00:00	2013	168	senate
f	790	2	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	169	senate
f	791	3	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	182	senate
f	793	5	member	Housing, Construction and Community Development	2013-01-01 00:00:00	2013	150	senate
f	799	2	member	New York City Education Subcommittee	2013-01-01 00:00:00	2013	165	senate
f	800	3	member	New York City Education Subcommittee	2013-01-01 00:00:00	2013	178	senate
f	803	6	member	New York City Education Subcommittee	2013-01-01 00:00:00	2013	147	senate
f	804	1	chair_person	Codes	2013-01-01 00:00:00	2013	150	senate
f	807	4	member	Codes	2013-01-01 00:00:00	2013	182	senate
f	811	8	member	Codes	2013-01-01 00:00:00	2013	141	senate
f	812	9	member	Codes	2013-01-01 00:00:00	2013	157	senate
f	813	10	member	Codes	2013-01-01 00:00:00	2013	131	senate
f	814	11	member	Codes	2013-01-01 00:00:00	2013	140	senate
f	817	14	member	Codes	2013-01-01 00:00:00	2013	129	senate
f	818	15	member	Codes	2013-01-01 00:00:00	2013	149	senate
f	819	16	member	Codes	2013-01-01 00:00:00	2013	147	senate
f	821	2	member	Banks	2013-01-01 00:00:00	2013	169	senate
f	822	3	member	Banks	2013-01-01 00:00:00	2013	132	senate
f	823	4	member	Banks	2013-01-01 00:00:00	2013	151	senate
f	824	5	member	Banks	2013-01-01 00:00:00	2013	178	senate
f	831	12	member	Banks	2013-01-01 00:00:00	2013	148	senate
f	832	13	member	Banks	2013-01-01 00:00:00	2013	174	senate
f	833	14	member	Banks	2013-01-01 00:00:00	2013	177	senate
f	834	15	member	Banks	2013-01-01 00:00:00	2013	172	senate
f	837	18	member	Banks	2013-01-01 00:00:00	2013	147	senate
f	838	19	member	Banks	2013-01-01 00:00:00	2013	146	senate
f	839	1	chair_person	Energy and Telecommunications	2013-01-01 00:00:00	2013	167	senate
f	842	4	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	149	senate
f	843	5	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	179	senate
f	846	8	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	146	senate
f	848	10	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	187	senate
f	849	11	member	Energy and Telecommunications	2013-01-01 00:00:00	2013	155	senate
f	850	1	chair_person	Elections	2013-01-01 00:00:00	2013	183	senate
f	851	2	member	Elections	2013-01-01 00:00:00	2013	164	senate
f	853	4	member	Elections	2013-01-01 00:00:00	2013	144	senate
f	854	5	member	Elections	2013-01-01 00:00:00	2013	150	senate
f	857	8	member	Elections	2013-01-01 00:00:00	2013	129	senate
f	858	9	member	Elections	2013-01-01 00:00:00	2013	159	senate
f	859	1	chair_person	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	182	senate
f	860	2	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	126	senate
f	861	3	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	132	senate
f	862	4	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	149	senate
f	868	10	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	168	senate
f	869	11	member	Crime Victims, Crime and Correction	2013-01-01 00:00:00	2013	176	senate
f	878	7	member	Insurance	2013-01-01 00:00:00	2013	130	senate
f	879	8	member	Insurance	2013-01-01 00:00:00	2013	175	senate
f	882	11	member	Insurance	2013-01-01 00:00:00	2013	170	senate
f	883	12	member	Insurance	2013-01-01 00:00:00	2013	174	senate
f	886	15	member	Insurance	2013-01-01 00:00:00	2013	172	senate
f	887	16	member	Insurance	2013-01-01 00:00:00	2013	156	senate
f	890	19	member	Insurance	2013-01-01 00:00:00	2013	147	senate
f	872	1	chair_person	Insurance	2013-01-01 00:00:00	2013	127	senate
f	873	2	member	Insurance	2013-01-01 00:00:00	2013	126	senate
f	874	3	member	Insurance	2013-01-01 00:00:00	2013	155	senate
f	875	4	member	Insurance	2013-01-01 00:00:00	2013	178	senate
f	876	5	member	Insurance	2013-01-01 00:00:00	2013	154	senate
f	877	6	member	Insurance	2013-01-01 00:00:00	2013	165	senate
f	880	9	member	Insurance	2013-01-01 00:00:00	2013	153	senate
f	881	10	member	Insurance	2013-01-01 00:00:00	2013	183	senate
f	884	13	member	Insurance	2013-01-01 00:00:00	2013	140	senate
f	885	14	member	Insurance	2013-01-01 00:00:00	2013	146	senate
f	888	17	member	Insurance	2013-01-01 00:00:00	2013	148	senate
f	889	18	member	Insurance	2013-01-01 00:00:00	2013	187	senate
t	891	1	chair_person	Aging	2014-02-28 11:25:44	2013	171	senate
t	892	2	member	Aging	2014-02-28 11:25:44	2013	178	senate
t	893	3	member	Aging	2014-02-28 11:25:44	2013	181	senate
t	894	4	member	Aging	2014-02-28 11:25:44	2013	175	senate
t	895	5	member	Aging	2014-02-28 11:25:44	2013	144	senate
t	896	6	member	Aging	2014-02-28 11:25:44	2013	160	senate
f	897	7	member	Aging	2014-02-28 11:25:44	2013	177	senate
f	898	8	member	Aging	2014-02-28 11:25:44	2013	139	senate
f	899	9	member	Aging	2014-02-28 11:25:44	2013	156	senate
t	900	1	chair_person	Agriculture	2014-02-28 11:25:44	2013	179	senate
t	901	2	member	Agriculture	2014-02-28 11:25:44	2013	182	senate
t	902	3	member	Agriculture	2014-02-28 11:25:44	2013	183	senate
t	903	4	member	Agriculture	2014-02-28 11:25:44	2013	162	senate
t	904	5	member	Agriculture	2014-02-28 11:25:44	2013	127	senate
t	905	6	member	Agriculture	2014-02-28 11:25:44	2013	171	senate
t	906	7	member	Agriculture	2014-02-28 11:25:44	2013	170	senate
f	907	8	member	Agriculture	2014-02-28 11:25:44	2013	158	senate
f	908	9	member	Agriculture	2014-02-28 11:25:44	2013	168	senate
f	909	10	member	Agriculture	2014-02-28 11:25:44	2013	142	senate
f	910	11	member	Agriculture	2014-02-28 11:25:44	2013	128	senate
t	911	1	chair_person	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	141	senate
t	912	2	member	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	169	senate
t	913	3	member	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	126	senate
t	914	4	member	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	179	senate
f	915	5	member	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	172	senate
f	916	6	member	Alcoholism and Drug Abuse	2014-02-28 11:25:44	2013	134	senate
t	917	1	chair_person	Banks	2014-02-28 11:25:44	2013	149	senate
t	918	2	vice_chair	Banks	2014-02-28 11:25:44	2013	151	senate
t	919	3	member	Banks	2014-02-28 11:25:44	2013	169	senate
t	920	4	member	Banks	2014-02-28 11:25:44	2013	132	senate
t	921	5	member	Banks	2014-02-28 11:25:44	2013	178	senate
t	922	6	member	Banks	2014-02-28 11:25:44	2013	144	senate
t	923	7	member	Banks	2014-02-28 11:25:44	2013	153	senate
t	924	8	member	Banks	2014-02-28 11:25:44	2013	163	senate
t	925	9	member	Banks	2014-02-28 11:25:44	2013	183	senate
t	926	10	member	Banks	2014-02-28 11:25:44	2013	162	senate
t	927	11	member	Banks	2014-02-28 11:25:44	2013	159	senate
t	928	12	member	Banks	2014-02-28 11:25:44	2013	171	senate
f	929	13	member	Banks	2014-02-28 11:25:44	2013	148	senate
f	930	14	member	Banks	2014-02-28 11:25:44	2013	174	senate
f	931	15	member	Banks	2014-02-28 11:25:44	2013	177	senate
f	932	16	member	Banks	2014-02-28 11:25:44	2013	172	senate
f	933	17	member	Banks	2014-02-28 11:25:44	2013	134	senate
f	934	18	member	Banks	2014-02-28 11:25:44	2013	158	senate
t	935	1	chair_person	Children and Families	2014-02-28 11:25:44	2013	181	senate
t	936	2	member	Children and Families	2014-02-28 11:25:44	2013	169	senate
t	937	3	member	Children and Families	2014-02-28 11:25:44	2013	159	senate
t	938	4	member	Children and Families	2014-02-28 11:25:44	2013	170	senate
f	939	5	member	Children and Families	2014-02-28 11:25:44	2013	168	senate
f	940	6	member	Children and Families	2014-02-28 11:25:44	2013	128	senate
t	941	1	chair_person	Cities	2014-02-28 11:25:44	2013	165	senate
t	942	2	member	Cities	2014-02-28 11:25:44	2013	164	senate
t	943	3	member	Cities	2014-02-28 11:25:44	2013	132	senate
t	944	4	member	Cities	2014-02-28 11:25:44	2013	154	senate
f	945	5	member	Cities	2014-02-28 11:25:44	2013	174	senate
t	946	1	chair_person	Civil Service and Pensions	2014-02-28 11:25:44	2013	178	senate
t	947	2	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	180	senate
t	948	3	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	165	senate
t	949	4	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	153	senate
t	950	5	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	179	senate
t	951	6	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	159	senate
f	952	7	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	134	senate
f	953	8	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	139	senate
f	954	9	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	137	senate
f	955	10	member	Civil Service and Pensions	2014-02-28 11:25:44	2013	131	senate
t	956	1	chair_person	Codes	2014-02-28 11:25:44	2013	150	senate
t	957	2	member	Codes	2014-02-28 11:25:44	2013	141	senate
t	958	3	member	Codes	2014-02-28 11:25:44	2013	132	senate
t	959	4	member	Codes	2014-02-28 11:25:44	2013	155	senate
t	960	5	member	Codes	2014-02-28 11:25:44	2013	182	senate
t	961	6	member	Codes	2014-02-28 11:25:44	2013	178	senate
t	962	7	member	Codes	2014-02-28 11:25:44	2013	165	senate
t	963	8	member	Codes	2014-02-28 11:25:44	2013	183	senate
f	964	9	member	Codes	2014-02-28 11:25:44	2013	157	senate
f	965	10	member	Codes	2014-02-28 11:25:44	2013	131	senate
f	966	11	member	Codes	2014-02-28 11:25:44	2013	140	senate
f	967	12	member	Codes	2014-02-28 11:25:44	2013	176	senate
f	968	13	member	Codes	2014-02-28 11:25:44	2013	148	senate
f	969	14	member	Codes	2014-02-28 11:25:44	2013	129	senate
t	970	1	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	171	senate
t	971	2	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	141	senate
t	972	3	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	182	senate
t	973	4	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	181	senate
t	974	5	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	149	senate
t	975	6	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	173	senate
f	976	7	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	146	senate
f	977	8	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	136	senate
f	978	9	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	134	senate
f	979	10	member	Commerce, Economic Development and Small Business	2014-02-28 11:25:44	2013	148	senate
t	980	1	chair_person	Consumer Protection	2014-02-28 11:25:44	2013	160	senate
t	981	2	member	Consumer Protection	2014-02-28 11:25:44	2013	141	senate
t	982	3	member	Consumer Protection	2014-02-28 11:25:44	2013	135	senate
t	983	4	member	Consumer Protection	2014-02-28 11:25:44	2013	144	senate
t	984	5	member	Consumer Protection	2014-02-28 11:25:44	2013	167	senate
t	985	6	member	Consumer Protection	2014-02-28 11:25:44	2013	159	senate
f	986	7	member	Consumer Protection	2014-02-28 11:25:44	2013	176	senate
f	987	8	member	Consumer Protection	2014-02-28 11:25:44	2013	142	senate
f	988	9	member	Consumer Protection	2014-02-28 11:25:44	2013	158	senate
f	989	10	member	Consumer Protection	2014-02-28 11:25:44	2013	143	senate
t	990	1	chair_person	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	162	senate
t	991	2	member	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	155	senate
t	992	3	member	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	130	senate
t	993	4	member	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	153	senate
f	994	5	member	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	131	senate
f	995	6	member	Corporations, Authorities and Commissions	2014-02-28 11:25:44	2013	157	senate
t	996	1	chair_person	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	182	senate
t	997	2	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	126	senate
t	998	3	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	132	senate
t	999	4	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	149	senate
t	1000	5	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	135	senate
t	1001	6	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	167	senate
t	1002	7	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	150	senate
t	1003	8	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	179	senate
f	1004	9	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	136	senate
f	1005	10	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	168	senate
f	1006	11	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	176	senate
f	1007	12	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	187	senate
f	1008	13	member	Crime Victims, Crime and Correction	2014-02-28 11:25:44	2013	133	senate
t	1009	1	chair_person	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	135	senate
t	1010	2	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	169	senate
t	1011	3	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	149	senate
t	1012	4	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	154	senate
t	1013	5	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	163	senate
t	1014	6	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	144	senate
t	1015	7	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	179	senate
f	1016	8	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	142	senate
f	1017	9	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	146	senate
f	1018	10	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	176	senate
f	1019	11	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	158	senate
f	1020	12	member	Cultural Affairs, Tourism, Parks and Recreation	2014-02-28 11:25:44	2013	148	senate
t	1021	1	chair_person	Education	2014-02-28 11:25:44	2013	155	senate
t	1022	2	member	Education	2014-02-28 11:25:44	2013	151	senate
t	1023	3	member	Education	2014-02-28 11:25:44	2013	165	senate
t	1024	4	member	Education	2014-02-28 11:25:44	2013	175	senate
t	1025	5	member	Education	2014-02-28 11:25:44	2013	135	senate
t	1026	6	member	Education	2014-02-28 11:25:44	2013	163	senate
t	1027	7	member	Education	2014-02-28 11:25:44	2013	167	senate
t	1028	8	member	Education	2014-02-28 11:25:44	2013	162	senate
t	1029	9	member	Education	2014-02-28 11:25:44	2013	173	senate
t	1030	10	member	Education	2014-02-28 11:25:44	2013	127	senate
t	1031	11	member	Education	2014-02-28 11:25:44	2013	171	senate
t	1032	12	member	Education	2014-02-28 11:25:44	2013	153	senate
f	1033	13	member	Education	2014-02-28 11:25:44	2013	143	senate
f	1034	14	member	Education	2014-02-28 11:25:44	2013	139	senate
f	1035	15	member	Education	2014-02-28 11:25:44	2013	174	senate
f	1036	16	member	Education	2014-02-28 11:25:44	2013	168	senate
f	1037	17	member	Education	2014-02-28 11:25:44	2013	156	senate
f	1038	18	member	Education	2014-02-28 11:25:44	2013	128	senate
t	1039	1	chair_person	Elections	2014-02-28 11:25:44	2013	183	senate
t	1040	2	member	Elections	2014-02-28 11:25:44	2013	164	senate
t	1041	3	member	Elections	2014-02-28 11:25:44	2013	182	senate
t	1042	4	member	Elections	2014-02-28 11:25:44	2013	144	senate
t	1043	5	member	Elections	2014-02-28 11:25:44	2013	150	senate
t	1044	6	member	Elections	2014-02-28 11:25:44	2013	159	senate
f	1045	7	member	Elections	2014-02-28 11:25:44	2013	128	senate
f	1046	8	member	Elections	2014-02-28 11:25:44	2013	137	senate
f	1047	9	member	Elections	2014-02-28 11:25:44	2013	129	senate
t	1048	1	chair_person	Energy and Telecommunications	2014-02-28 11:25:44	2013	167	senate
t	1049	2	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	126	senate
t	1050	3	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	149	senate
t	1051	4	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	183	senate
t	1052	5	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	179	senate
t	1053	6	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	173	senate
f	1054	7	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	172	senate
f	1055	8	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	146	senate
f	1056	9	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	137	senate
f	1057	10	member	Energy and Telecommunications	2014-02-28 11:25:44	2013	187	senate
t	1058	1	chair_person	Environmental Conservation	2014-02-28 11:25:44	2013	154	senate
t	1059	2	vice_chair	Environmental Conservation	2014-02-28 11:25:44	2013	147	senate
t	1060	3	member	Environmental Conservation	2014-02-28 11:25:44	2013	175	senate
t	1061	4	member	Environmental Conservation	2014-02-28 11:25:44	2013	135	senate
t	1062	5	member	Environmental Conservation	2014-02-28 11:25:44	2013	163	senate
t	1063	6	member	Environmental Conservation	2014-02-28 11:25:44	2013	167	senate
t	1064	7	member	Environmental Conservation	2014-02-28 11:25:44	2013	183	senate
t	1065	8	member	Environmental Conservation	2014-02-28 11:25:44	2013	170	senate
f	1066	9	member	Environmental Conservation	2014-02-28 11:25:44	2013	140	senate
f	1067	10	member	Environmental Conservation	2014-02-28 11:25:44	2013	142	senate
f	1068	11	member	Environmental Conservation	2014-02-28 11:25:44	2013	143	senate
f	1069	12	member	Environmental Conservation	2014-02-28 11:25:44	2013	128	senate
t	1112	1	chair_person	Health	2014-02-28 11:25:44	2013	180	senate
t	1113	2	member	Health	2014-02-28 11:25:44	2013	164	senate
t	1070	1	chair_person	Ethics	2014-02-28 11:25:44	2013	141	senate
t	1071	2	member	Ethics	2014-02-28 11:25:44	2013	165	senate
t	1072	3	member	Ethics	2014-02-28 11:25:44	2013	151	senate
f	1073	4	member	Ethics	2014-02-28 11:25:44	2013	138	senate
f	1074	5	member	Ethics	2014-02-28 11:25:44	2013	133	senate
f	1075	6	member	Ethics	2014-02-28 11:25:44	2013	174	senate
t	1076	1	chair_person	Finance	2014-02-28 11:25:44	2013	132	senate
t	1077	2	member	Finance	2014-02-28 11:25:44	2013	169	senate
t	1078	3	member	Finance	2014-02-28 11:25:44	2013	151	senate
t	1079	4	member	Finance	2014-02-28 11:25:44	2013	155	senate
t	1080	5	member	Finance	2014-02-28 11:25:44	2013	178	senate
t	1081	6	member	Finance	2014-02-28 11:25:44	2013	149	senate
t	1082	7	member	Finance	2014-02-28 11:25:44	2013	154	senate
t	1083	8	member	Finance	2014-02-28 11:25:44	2013	180	senate
t	1084	9	member	Finance	2014-02-28 11:25:44	2013	165	senate
t	1085	10	member	Finance	2014-02-28 11:25:44	2013	130	senate
t	1086	11	member	Finance	2014-02-28 11:25:44	2013	175	senate
t	1087	12	member	Finance	2014-02-28 11:25:44	2013	135	senate
t	1088	13	member	Finance	2014-02-28 11:25:44	2013	163	senate
t	1089	14	member	Finance	2014-02-28 11:25:44	2013	150	senate
t	1090	15	member	Finance	2014-02-28 11:25:44	2013	183	senate
t	1091	16	member	Finance	2014-02-28 11:25:44	2013	162	senate
t	1092	17	member	Finance	2014-02-28 11:25:44	2013	173	senate
t	1093	18	member	Finance	2014-02-28 11:25:44	2013	159	senate
t	1094	19	member	Finance	2014-02-28 11:25:44	2013	127	senate
t	1095	20	member	Finance	2014-02-28 11:25:44	2013	170	senate
t	1096	21	member	Finance	2014-02-28 11:25:44	2013	171	senate
f	1097	22	member	Finance	2014-02-28 11:25:44	2013	129	senate
f	1098	23	member	Finance	2014-02-28 11:25:44	2013	177	senate
f	1099	24	member	Finance	2014-02-28 11:25:44	2013	137	senate
f	1100	25	member	Finance	2014-02-28 11:25:44	2013	133	senate
f	1101	26	member	Finance	2014-02-28 11:25:44	2013	138	senate
f	1102	27	member	Finance	2014-02-28 11:25:44	2013	174	senate
f	1103	28	member	Finance	2014-02-28 11:25:44	2013	168	senate
f	1104	29	member	Finance	2014-02-28 11:25:44	2013	172	senate
f	1105	30	member	Finance	2014-02-28 11:25:44	2013	187	senate
f	1106	31	member	Finance	2014-02-28 11:25:44	2013	131	senate
f	1107	32	member	Finance	2014-02-28 11:25:44	2013	156	senate
f	1108	33	member	Finance	2014-02-28 11:25:44	2013	157	senate
f	1109	34	member	Finance	2014-02-28 11:25:44	2013	146	senate
f	1110	35	member	Finance	2014-02-28 11:25:44	2013	140	senate
f	1111	36	member	Finance	2014-02-28 11:25:44	2013	136	senate
t	1128	1	chair_person	Higher Education	2014-02-28 11:25:44	2013	175	senate
t	1129	2	member	Higher Education	2014-02-28 11:25:44	2013	155	senate
t	1130	3	member	Higher Education	2014-02-28 11:25:44	2013	182	senate
t	1131	4	member	Higher Education	2014-02-28 11:25:44	2013	149	senate
t	1132	5	member	Higher Education	2014-02-28 11:25:44	2013	154	senate
t	1133	6	member	Higher Education	2014-02-28 11:25:44	2013	167	senate
t	1134	7	member	Higher Education	2014-02-28 11:25:44	2013	179	senate
t	1135	8	member	Higher Education	2014-02-28 11:25:44	2013	173	senate
t	1136	9	member	Higher Education	2014-02-28 11:25:44	2013	159	senate
t	1137	10	member	Higher Education	2014-02-28 11:25:44	2013	127	senate
t	1138	11	member	Higher Education	2014-02-28 11:25:44	2013	171	senate
t	1139	12	member	Higher Education	2014-02-28 11:25:44	2013	160	senate
f	1140	13	member	Higher Education	2014-02-28 11:25:44	2013	156	senate
f	1141	14	member	Higher Education	2014-02-28 11:25:44	2013	146	senate
f	1142	15	member	Higher Education	2014-02-28 11:25:44	2013	129	senate
f	1143	16	member	Higher Education	2014-02-28 11:25:44	2013	172	senate
f	1144	17	member	Higher Education	2014-02-28 11:25:44	2013	133	senate
f	1145	18	member	Higher Education	2014-02-28 11:25:44	2013	142	senate
f	1146	19	member	Higher Education	2014-02-28 11:25:44	2013	140	senate
t	1147	1	chair_person	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	170	senate
t	1165	1	chair_person	Insurance	2014-02-28 11:25:44	2013	127	senate
t	1166	2	member	Insurance	2014-02-28 11:25:44	2013	126	senate
t	1167	3	member	Insurance	2014-02-28 11:25:44	2013	155	senate
t	1168	4	member	Insurance	2014-02-28 11:25:44	2013	178	senate
t	1169	5	member	Insurance	2014-02-28 11:25:44	2013	154	senate
t	1170	6	member	Insurance	2014-02-28 11:25:44	2013	165	senate
t	1171	7	member	Insurance	2014-02-28 11:25:44	2013	130	senate
t	1172	8	member	Insurance	2014-02-28 11:25:44	2013	175	senate
t	1173	9	member	Insurance	2014-02-28 11:25:44	2013	153	senate
t	1174	10	member	Insurance	2014-02-28 11:25:44	2013	183	senate
t	1175	11	member	Insurance	2014-02-28 11:25:44	2013	171	senate
t	1176	12	member	Insurance	2014-02-28 11:25:44	2013	170	senate
f	1177	13	member	Insurance	2014-02-28 11:25:44	2013	174	senate
f	1178	14	member	Insurance	2014-02-28 11:25:44	2013	140	senate
f	1179	15	member	Insurance	2014-02-28 11:25:44	2013	146	senate
f	1180	16	member	Insurance	2014-02-28 11:25:44	2013	172	senate
f	1181	17	member	Insurance	2014-02-28 11:25:44	2013	156	senate
f	1182	18	member	Insurance	2014-02-28 11:25:44	2013	148	senate
f	1183	19	member	Insurance	2014-02-28 11:25:44	2013	187	senate
t	1186	3	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	178	senate
t	1187	4	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	150	senate
t	1188	5	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	183	senate
t	1189	6	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	160	senate
f	1190	7	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	176	senate
f	1191	8	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	177	senate
f	1192	9	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	157	senate
t	1196	4	member	Judiciary	2014-02-28 11:25:44	2013	180	senate
t	1197	5	member	Judiciary	2014-02-28 11:25:44	2013	165	senate
t	1198	6	member	Judiciary	2014-02-28 11:25:44	2013	175	senate
t	1199	7	member	Judiciary	2014-02-28 11:25:44	2013	135	senate
t	1200	8	member	Judiciary	2014-02-28 11:25:44	2013	150	senate
t	1201	9	member	Judiciary	2014-02-28 11:25:44	2013	183	senate
t	1202	10	member	Judiciary	2014-02-28 11:25:44	2013	159	senate
t	1203	11	member	Judiciary	2014-02-28 11:25:44	2013	162	senate
t	1114	3	member	Health	2014-02-28 11:25:44	2013	151	senate
t	1115	4	member	Health	2014-02-28 11:25:44	2013	181	senate
t	1116	5	member	Health	2014-02-28 11:25:44	2013	178	senate
t	1117	6	member	Health	2014-02-28 11:25:44	2013	130	senate
t	1118	7	member	Health	2014-02-28 11:25:44	2013	159	senate
t	1119	8	member	Health	2014-02-28 11:25:44	2013	127	senate
t	1120	9	member	Health	2014-02-28 11:25:44	2013	170	senate
f	1121	10	member	Health	2014-02-28 11:25:44	2013	133	senate
f	1122	11	member	Health	2014-02-28 11:25:44	2013	168	senate
f	1123	12	member	Health	2014-02-28 11:25:44	2013	136	senate
f	1124	13	member	Health	2014-02-28 11:25:44	2013	187	senate
f	1125	14	member	Health	2014-02-28 11:25:44	2013	148	senate
f	1126	15	member	Health	2014-02-28 11:25:44	2013	176	senate
f	1127	16	member	Health	2014-02-28 11:25:44	2013	142	senate
t	1148	2	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	169	senate
t	1149	3	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	141	senate
t	1150	4	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	182	senate
t	1151	5	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	150	senate
t	1152	6	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	153	senate
f	1153	7	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	140	senate
f	1154	8	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	177	senate
f	1155	9	member	Housing, Construction and Community Development	2014-02-28 11:25:44	2013	129	senate
t	1156	1	chair_person	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	163	senate
t	1157	2	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	173	senate
t	1158	3	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	182	senate
t	1159	4	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	149	senate
t	1160	5	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	154	senate
t	1161	6	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	126	senate
f	1162	7	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	146	senate
f	1163	8	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	176	senate
f	1164	9	member	Infrastructure and Capital Investment	2014-02-28 11:25:44	2013	143	senate
t	1184	1	chair_person	Investigations and Government Operations	2014-02-28 11:25:44	2013	163	senate
t	1185	2	member	Investigations and Government Operations	2014-02-28 11:25:44	2013	126	senate
t	1193	1	chair_person	Judiciary	2014-02-28 11:25:44	2013	169	senate
t	1194	2	member	Judiciary	2014-02-28 11:25:44	2013	132	senate
t	1195	3	member	Judiciary	2014-02-28 11:25:44	2013	155	senate
f	1211	19	member	Judiciary	2014-02-28 11:25:44	2013	156	senate
f	1213	21	member	Judiciary	2014-02-28 11:25:44	2013	139	senate
f	1214	22	member	Judiciary	2014-02-28 11:25:44	2013	157	senate
t	1215	1	chair_person	Labor	2014-02-28 11:25:44	2013	159	senate
t	1216	2	member	Labor	2014-02-28 11:25:44	2013	164	senate
t	1217	3	member	Labor	2014-02-28 11:25:44	2013	132	senate
t	1218	4	member	Labor	2014-02-28 11:25:44	2013	182	senate
t	1219	5	member	Labor	2014-02-28 11:25:44	2013	154	senate
t	1220	6	member	Labor	2014-02-28 11:25:44	2013	180	senate
t	1221	7	member	Labor	2014-02-28 11:25:44	2013	163	senate
t	1222	8	member	Labor	2014-02-28 11:25:44	2013	144	senate
t	1223	9	member	Labor	2014-02-28 11:25:44	2013	153	senate
t	1224	10	member	Labor	2014-02-28 11:25:44	2013	173	senate
f	1225	11	member	Labor	2014-02-28 11:25:44	2013	187	senate
f	1226	12	member	Labor	2014-02-28 11:25:44	2013	139	senate
f	1227	13	member	Labor	2014-02-28 11:25:44	2013	137	senate
f	1228	14	member	Labor	2014-02-28 11:25:44	2013	131	senate
f	1229	15	member	Labor	2014-02-28 11:25:44	2013	133	senate
f	1230	16	member	Labor	2014-02-28 11:25:44	2013	134	senate
t	1253	3	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	126	senate
t	1254	4	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	149	senate
t	1255	5	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	144	senate
t	1256	6	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	150	senate
t	1257	7	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	162	senate
f	1258	8	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	139	senate
f	1259	9	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	143	senate
f	1260	10	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	134	senate
t	1266	6	member	Rules	2014-02-28 11:25:44	2013	155	senate
t	1267	7	member	Rules	2014-02-28 11:25:44	2013	180	senate
t	1268	8	member	Rules	2014-02-28 11:25:44	2013	130	senate
t	1269	9	member	Rules	2014-02-28 11:25:44	2013	175	senate
t	1270	10	member	Rules	2014-02-28 11:25:44	2013	163	senate
t	1271	11	member	Rules	2014-02-28 11:25:44	2013	167	senate
t	1272	12	member	Rules	2014-02-28 11:25:44	2013	150	senate
t	1273	13	member	Rules	2014-02-28 11:25:44	2013	127	senate
t	1274	14	member	Rules	2014-02-28 11:25:44	2013	171	senate
f	1275	15	member	Rules	2014-02-28 11:25:44	2013	152	senate
f	1276	16	member	Rules	2014-02-28 11:25:44	2013	174	senate
f	1277	17	member	Rules	2014-02-28 11:25:44	2013	137	senate
f	1278	18	member	Rules	2014-02-28 11:25:44	2013	136	senate
f	1279	19	member	Rules	2014-02-28 11:25:44	2013	129	senate
f	1280	20	member	Rules	2014-02-28 11:25:44	2013	168	senate
f	1281	21	member	Rules	2014-02-28 11:25:44	2013	172	senate
f	1282	22	member	Rules	2014-02-28 11:25:44	2013	131	senate
f	1283	23	member	Rules	2014-02-28 11:25:44	2013	140	senate
f	1284	24	member	Rules	2014-02-28 11:25:44	2013	138	senate
t	1285	1	chair_person	Social Services	2014-02-28 11:25:44	2013	147	senate
t	1297	7	member	Transportation	2014-02-28 11:25:44	2013	150	senate
t	1298	8	member	Transportation	2014-02-28 11:25:44	2013	183	senate
t	1299	9	member	Transportation	2014-02-28 11:25:44	2013	162	senate
t	1300	10	member	Transportation	2014-02-28 11:25:44	2013	170	senate
t	1301	11	member	Transportation	2014-02-28 11:25:44	2013	160	senate
t	1302	12	member	Transportation	2014-02-28 11:25:44	2013	153	senate
f	1303	13	member	Transportation	2014-02-28 11:25:44	2013	137	senate
f	1304	14	member	Transportation	2014-02-28 11:25:44	2013	177	senate
f	1305	15	member	Transportation	2014-02-28 11:25:44	2013	146	senate
f	1306	16	member	Transportation	2014-02-28 11:25:44	2013	131	senate
f	1307	17	member	Transportation	2014-02-28 11:25:44	2013	157	senate
t	1204	12	member	Judiciary	2014-02-28 11:25:44	2013	160	senate
t	1205	13	member	Judiciary	2014-02-28 11:25:44	2013	126	senate
f	1206	14	member	Judiciary	2014-02-28 11:25:44	2013	136	senate
f	1207	15	member	Judiciary	2014-02-28 11:25:44	2013	174	senate
f	1208	16	member	Judiciary	2014-02-28 11:25:44	2013	137	senate
f	1209	17	member	Judiciary	2014-02-28 11:25:44	2013	140	senate
f	1210	18	member	Judiciary	2014-02-28 11:25:44	2013	131	senate
f	1212	20	member	Judiciary	2014-02-28 11:25:44	2013	176	senate
t	1231	1	chair_person	Local Government	2014-02-28 11:25:44	2013	153	senate
t	1232	2	member	Local Government	2014-02-28 11:25:44	2013	164	senate
t	1233	3	member	Local Government	2014-02-28 11:25:44	2013	141	senate
t	1234	4	member	Local Government	2014-02-28 11:25:44	2013	144	senate
t	1235	5	member	Local Government	2014-02-28 11:25:44	2013	179	senate
t	1236	6	member	Local Government	2014-02-28 11:25:44	2013	171	senate
f	1237	7	member	Local Government	2014-02-28 11:25:44	2013	158	senate
f	1238	8	member	Local Government	2014-02-28 11:25:44	2013	143	senate
f	1239	9	member	Local Government	2014-02-28 11:25:44	2013	148	senate
t	1240	1	chair_person	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	126	senate
t	1241	2	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	164	senate
t	1242	3	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	169	senate
t	1243	4	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	181	senate
t	1244	5	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	180	senate
t	1245	6	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	127	senate
t	1246	7	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	160	senate
f	1247	8	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	128	senate
f	1248	9	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	129	senate
f	1249	10	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	143	senate
f	1250	11	member	Mental Health and Developmental Disabilities	2014-02-28 11:25:44	2013	133	senate
t	1251	1	chair_person	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	169	senate
t	1252	2	member	Racing, Gaming and Wagering	2014-02-28 11:25:44	2013	141	senate
t	1261	1	chair_person	Rules	2014-02-28 11:25:44	2013	184	senate
t	1262	2	vice_chair	Rules	2014-02-28 11:25:44	2013	166	senate
t	1263	3	member	Rules	2014-02-28 11:25:44	2013	169	senate
t	1264	4	member	Rules	2014-02-28 11:25:44	2013	126	senate
t	1265	5	member	Rules	2014-02-28 11:25:44	2013	151	senate
t	1286	2	member	Social Services	2014-02-28 11:25:44	2013	164	senate
t	1287	3	member	Social Services	2014-02-28 11:25:44	2013	151	senate
t	1288	4	member	Social Services	2014-02-28 11:25:44	2013	153	senate
f	1289	5	member	Social Services	2014-02-28 11:25:44	2013	134	senate
f	1290	6	member	Social Services	2014-02-28 11:25:44	2013	157	senate
t	1291	1	chair_person	Transportation	2014-02-28 11:25:44	2013	173	senate
t	1292	2	vice_chair	Transportation	2014-02-28 11:25:44	2013	163	senate
t	1293	3	member	Transportation	2014-02-28 11:25:44	2013	126	senate
t	1294	4	member	Transportation	2014-02-28 11:25:44	2013	182	senate
t	1295	5	member	Transportation	2014-02-28 11:25:44	2013	130	senate
t	1296	6	member	Transportation	2014-02-28 11:25:44	2013	167	senate
f	1308	18	member	Transportation	2014-02-28 11:25:44	2013	156	senate
f	1309	19	member	Transportation	2014-02-28 11:25:44	2013	158	senate
t	1310	1	chair_person	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	164	senate
t	1311	2	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	126	senate
t	1312	3	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	155	senate
t	1313	4	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	178	senate
t	1314	5	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	149	senate
t	1315	6	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	154	senate
t	1316	7	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	130	senate
t	1317	8	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	160	senate
f	1318	9	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	139	senate
f	1319	10	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	158	senate
f	1320	11	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	134	senate
f	1321	12	member	Veterans, Homeland Security and Military Affairs	2014-02-28 11:25:44	2013	128	senate
t	1322	1	chair_person	New York City Education Subcommittee	2014-02-28 11:25:44	2013	181	senate
t	1323	2	member	New York City Education Subcommittee	2014-02-28 11:25:44	2013	165	senate
t	1324	3	member	New York City Education Subcommittee	2014-02-28 11:25:44	2013	178	senate
t	1325	4	member	New York City Education Subcommittee	2014-02-28 11:25:44	2013	159	senate
f	1326	5	member	New York City Education Subcommittee	2014-02-28 11:25:44	2013	131	senate
f	1327	6	member	New York City Education Subcommittee	2014-02-28 11:25:44	2013	156	senate
t	1682	1	chair_person	Aging	2015-01-22 16:58:51	2015	884	senate
t	1683	2	member	Aging	2015-01-22 16:58:51	2015	851	senate
t	1684	3	member	Aging	2015-01-22 16:58:51	2015	881	senate
t	1685	4	member	Aging	2015-01-22 16:58:51	2015	697	senate
t	1686	5	member	Aging	2015-01-22 16:58:51	2015	694	senate
t	1687	6	member	Aging	2015-01-22 16:58:51	2015	699	senate
t	1688	7	member	Aging	2015-01-22 16:58:51	2015	725	senate
f	1689	8	member	Aging	2015-01-22 16:58:51	2015	719	senate
f	1690	9	member	Aging	2015-01-22 16:58:51	2015	726	senate
f	1691	10	member	Aging	2015-01-22 16:58:51	2015	705	senate
f	1692	11	member	Aging	2015-01-22 16:58:51	2015	709	senate
t	1693	1	chair_person	Agriculture	2015-01-22 16:58:51	2015	701	senate
t	1694	2	member	Agriculture	2015-01-22 16:58:51	2015	696	senate
t	1695	3	member	Agriculture	2015-01-22 16:58:51	2015	872	senate
t	1696	4	member	Agriculture	2015-01-22 16:58:51	2015	850	senate
t	1697	5	member	Agriculture	2015-01-22 16:58:51	2015	704	senate
t	1698	6	member	Agriculture	2015-01-22 16:58:51	2015	725	senate
t	1699	7	member	Agriculture	2015-01-22 16:58:51	2015	855	senate
f	1700	8	member	Agriculture	2015-01-22 16:58:51	2015	888	senate
f	1701	9	member	Agriculture	2015-01-22 16:58:51	2015	883	senate
f	1702	10	member	Agriculture	2015-01-22 16:58:51	2015	711	senate
f	1703	11	member	Agriculture	2015-01-22 16:58:51	2015	709	senate
t	1704	1	chair_person	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	886	senate
t	1705	2	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	729	senate
t	1706	3	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	721	senate
t	1707	4	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	887	senate
t	1708	5	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	701	senate
f	1709	6	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	713	senate
f	1710	7	member	Alcoholism and Drug Abuse	2015-01-22 16:58:51	2015	693	senate
t	1711	1	chair_person	Banks	2015-01-22 16:58:51	2015	724	senate
t	1712	2	vice_chair	Banks	2015-01-22 16:58:51	2015	853	senate
t	1713	3	member	Banks	2015-01-22 16:58:51	2015	886	senate
t	1714	4	member	Banks	2015-01-22 16:58:51	2015	720	senate
t	1715	5	member	Banks	2015-01-22 16:58:51	2015	729	senate
t	1716	6	member	Banks	2015-01-22 16:58:51	2015	700	senate
t	1717	7	member	Banks	2015-01-22 16:58:51	2015	697	senate
t	1718	8	member	Banks	2015-01-22 16:58:51	2015	703	senate
t	1719	9	member	Banks	2015-01-22 16:58:51	2015	699	senate
t	1720	10	member	Banks	2015-01-22 16:58:51	2015	695	senate
t	1721	11	member	Banks	2015-01-22 16:58:51	2015	885	senate
t	1722	12	member	Banks	2015-01-22 16:58:51	2015	872	senate
f	1723	13	member	Banks	2015-01-22 16:58:51	2015	883	senate
f	1724	14	member	Banks	2015-01-22 16:58:51	2015	727	senate
f	1725	15	member	Banks	2015-01-22 16:58:51	2015	719	senate
f	1726	16	member	Banks	2015-01-22 16:58:51	2015	712	senate
f	1727	17	member	Banks	2015-01-22 16:58:51	2015	717	senate
f	1728	18	member	Banks	2015-01-22 16:58:51	2015	713	senate
f	1729	19	member	Banks	2015-01-22 16:58:51	2015	875	senate
t	1730	1	chair_person	Children and Families	2015-01-22 16:58:51	2015	851	senate
t	1731	2	member	Children and Families	2015-01-22 16:58:51	2015	729	senate
t	1732	3	member	Children and Families	2015-01-22 16:58:51	2015	724	senate
t	1733	4	member	Children and Families	2015-01-22 16:58:51	2015	884	senate
t	1734	5	member	Children and Families	2015-01-22 16:58:51	2015	855	senate
f	1735	6	member	Children and Families	2015-01-22 16:58:51	2015	711	senate
f	1736	7	member	Children and Families	2015-01-22 16:58:51	2015	709	senate
t	1737	1	chair_person	Cities	2015-01-22 16:58:51	2015	730	senate
t	1738	2	member	Cities	2015-01-22 16:58:51	2015	700	senate
t	1739	3	member	Cities	2015-01-22 16:58:51	2015	881	senate
t	1740	4	member	Cities	2015-01-22 16:58:51	2015	879	senate
t	1741	5	member	Cities	2015-01-22 16:58:51	2015	724	senate
f	1742	6	member	Cities	2015-01-22 16:58:51	2015	715	senate
f	1743	7	member	Cities	2015-01-22 16:58:51	2015	714	senate
t	1744	1	chair_person	Civil Service and Pensions	2015-01-22 16:58:51	2015	697	senate
t	1755	1	chair_person	Codes	2015-01-22 16:58:51	2015	852	senate
t	1756	2	vice_chair	Codes	2015-01-22 16:58:51	2015	730	senate
t	1757	3	member	Codes	2015-01-22 16:58:51	2015	716	senate
t	1758	4	member	Codes	2015-01-22 16:58:51	2015	700	senate
t	1759	5	member	Codes	2015-01-22 16:58:51	2015	878	senate
t	1760	6	member	Codes	2015-01-22 16:58:51	2015	696	senate
t	1761	7	member	Codes	2015-01-22 16:58:51	2015	697	senate
t	1762	8	member	Codes	2015-01-22 16:58:51	2015	702	senate
t	1763	9	member	Codes	2015-01-22 16:58:51	2015	872	senate
t	1764	10	member	Codes	2015-01-22 16:58:51	2015	724	senate
f	1765	11	member	Codes	2015-01-22 16:58:51	2015	715	senate
f	1766	12	member	Codes	2015-01-22 16:58:51	2015	718	senate
f	1767	13	member	Codes	2015-01-22 16:58:51	2015	883	senate
f	1768	14	member	Codes	2015-01-22 16:58:51	2015	707	senate
f	1769	15	member	Codes	2015-01-22 16:58:51	2015	888	senate
f	1770	16	member	Codes	2015-01-22 16:58:51	2015	708	senate
t	1771	1	chair_person	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	716	senate
t	1772	2	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	851	senate
t	1773	3	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	696	senate
t	1774	4	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	702	senate
t	1775	5	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	856	senate
t	1776	6	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	873	senate
t	1777	7	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	725	senate
f	1778	8	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	712	senate
f	1779	9	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	883	senate
f	1780	10	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	693	senate
f	1781	11	member	Commerce, Economic Development and Small Business	2015-01-22 16:58:51	2015	875	senate
t	1782	1	chair_person	Consumer Protection	2015-01-22 16:58:51	2015	889	senate
t	1803	4	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	856	senate
t	1804	5	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	852	senate
t	1805	6	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	701	senate
t	1806	7	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	724	senate
t	1807	8	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	889	senate
f	1808	9	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	693	senate
f	1809	10	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	711	senate
f	1810	11	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	714	senate
f	1811	12	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	708	senate
f	1812	13	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	723	senate
t	1813	1	chair_person	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	856	senate
t	1814	2	vice_chair	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	720	senate
t	1815	3	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	729	senate
t	1816	4	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	702	senate
t	1817	5	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	703	senate
t	1818	6	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	699	senate
t	1819	7	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	701	senate
t	1820	8	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	884	senate
f	1821	9	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	709	senate
f	1822	10	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	705	senate
f	1823	11	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	712	senate
f	1824	12	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	713	senate
f	1825	13	member	Cultural Affairs, Tourism, Parks and Recreation	2015-01-22 16:58:51	2015	875	senate
t	1826	1	chair_person	Education	2015-01-22 16:58:51	2015	878	senate
t	1827	2	member	Education	2015-01-22 16:58:51	2015	720	senate
t	1828	3	member	Education	2015-01-22 16:58:51	2015	853	senate
t	1829	4	member	Education	2015-01-22 16:58:51	2015	730	senate
t	1830	5	member	Education	2015-01-22 16:58:51	2015	694	senate
t	1831	6	member	Education	2015-01-22 16:58:51	2015	856	senate
t	1745	2	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	887	senate
t	1746	3	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	730	senate
t	1747	4	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	695	senate
t	1748	5	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	879	senate
t	1749	6	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	701	senate
t	1750	7	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	724	senate
f	1751	8	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	875	senate
f	1752	9	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	726	senate
f	1753	10	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	882	senate
f	1754	11	member	Civil Service and Pensions	2015-01-22 16:58:51	2015	722	senate
t	1783	2	member	Consumer Protection	2015-01-22 16:58:51	2015	886	senate
t	1784	3	member	Consumer Protection	2015-01-22 16:58:51	2015	716	senate
t	1785	4	member	Consumer Protection	2015-01-22 16:58:51	2015	856	senate
t	1786	5	member	Consumer Protection	2015-01-22 16:58:51	2015	699	senate
t	1787	6	member	Consumer Protection	2015-01-22 16:58:51	2015	873	senate
t	1788	7	member	Consumer Protection	2015-01-22 16:58:51	2015	724	senate
f	1789	8	member	Consumer Protection	2015-01-22 16:58:51	2015	882	senate
f	1790	9	member	Consumer Protection	2015-01-22 16:58:51	2015	717	senate
f	1791	10	member	Consumer Protection	2015-01-22 16:58:51	2015	714	senate
f	1792	11	member	Consumer Protection	2015-01-22 16:58:51	2015	709	senate
t	1793	1	chair_person	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	850	senate
t	1794	2	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	878	senate
t	1795	3	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	698	senate
t	1796	4	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	695	senate
t	1797	5	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	879	senate
f	1798	6	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	708	senate
f	1799	7	member	Corporations, Authorities and Commissions	2015-01-22 16:58:51	2015	715	senate
t	1800	1	chair_person	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	696	senate
t	1801	2	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	700	senate
t	1802	3	member	Crime Victims, Crime and Correction	2015-01-22 16:58:51	2015	702	senate
t	1866	2	vice_chair	Environmental Conservation	2015-01-22 16:58:51	2015	720	senate
t	1867	3	member	Environmental Conservation	2015-01-22 16:58:51	2015	881	senate
t	1868	4	member	Environmental Conservation	2015-01-22 16:58:51	2015	694	senate
t	1897	16	member	Finance	2015-01-22 16:58:51	2015	872	senate
t	1898	17	member	Finance	2015-01-22 16:58:51	2015	850	senate
t	1899	18	member	Finance	2015-01-22 16:58:51	2015	701	senate
t	1900	19	member	Finance	2015-01-22 16:58:51	2015	873	senate
t	1901	20	member	Finance	2015-01-22 16:58:51	2015	724	senate
t	1902	21	member	Finance	2015-01-22 16:58:51	2015	704	senate
t	1903	22	member	Finance	2015-01-22 16:58:51	2015	725	senate
t	1904	23	member	Finance	2015-01-22 16:58:51	2015	855	senate
f	1916	35	member	Finance	2015-01-22 16:58:51	2015	723	senate
f	1917	36	member	Finance	2015-01-22 16:58:51	2015	715	senate
f	1918	37	member	Finance	2015-01-22 16:58:51	2015	710	senate
t	1919	1	chair_person	Health	2015-01-22 16:58:51	2015	871	senate
t	1925	7	member	Health	2015-01-22 16:58:51	2015	856	senate
t	1926	8	member	Health	2015-01-22 16:58:51	2015	695	senate
t	1955	1	chair_person	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	855	senate
t	2050	2	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	721	senate
t	2051	3	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	851	senate
t	2052	4	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	871	senate
t	2053	5	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	885	senate
t	2054	6	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	884	senate
t	2055	7	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	704	senate
f	2056	8	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	883	senate
f	2057	9	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	707	senate
f	2058	10	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	723	senate
f	2059	11	member	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	709	senate
t	2060	1	chair_person	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	729	senate
t	2061	2	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	716	senate
t	2062	3	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	721	senate
t	2063	4	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	702	senate
t	2064	5	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	699	senate
t	2065	6	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	852	senate
t	2066	7	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	850	senate
f	2067	8	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	726	senate
f	2068	9	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	882	senate
f	2069	10	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	717	senate
f	2070	11	member	Racing, Gaming and Wagering	2015-01-22 16:58:51	2015	875	senate
t	2071	1	chair_person	Rules	2015-01-22 16:58:51	2015	877	senate
t	1832	7	member	Education	2015-01-22 16:58:51	2015	703	senate
t	1833	8	member	Education	2015-01-22 16:58:51	2015	850	senate
t	1834	9	member	Education	2015-01-22 16:58:51	2015	873	senate
t	1835	10	member	Education	2015-01-22 16:58:51	2015	884	senate
t	1836	11	member	Education	2015-01-22 16:58:51	2015	704	senate
t	1837	12	member	Education	2015-01-22 16:58:51	2015	725	senate
f	1838	13	member	Education	2015-01-22 16:58:51	2015	717	senate
f	1839	14	member	Education	2015-01-22 16:58:51	2015	726	senate
f	1840	15	member	Education	2015-01-22 16:58:51	2015	727	senate
f	1841	16	member	Education	2015-01-22 16:58:51	2015	883	senate
f	1842	17	member	Education	2015-01-22 16:58:51	2015	711	senate
f	1843	18	member	Education	2015-01-22 16:58:51	2015	714	senate
f	1844	19	member	Education	2015-01-22 16:58:51	2015	710	senate
t	1845	1	chair_person	Elections	2015-01-22 16:58:51	2015	881	senate
t	1846	2	member	Elections	2015-01-22 16:58:51	2015	886	senate
t	1847	3	member	Elections	2015-01-22 16:58:51	2015	720	senate
t	1848	4	member	Elections	2015-01-22 16:58:51	2015	696	senate
t	1849	5	member	Elections	2015-01-22 16:58:51	2015	699	senate
t	1850	6	member	Elections	2015-01-22 16:58:51	2015	852	senate
f	1851	7	member	Elections	2015-01-22 16:58:51	2015	882	senate
f	1852	8	member	Elections	2015-01-22 16:58:51	2015	722	senate
f	1853	9	member	Elections	2015-01-22 16:58:51	2015	707	senate
t	1854	1	chair_person	Energy and Telecommunications	2015-01-22 16:58:51	2015	702	senate
t	1855	2	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	721	senate
t	1856	3	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	887	senate
t	1857	4	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	878	senate
t	1858	5	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	872	senate
t	1859	6	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	701	senate
t	1860	7	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	873	senate
f	1861	8	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	713	senate
f	1862	9	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	722	senate
f	1863	10	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	883	senate
f	1864	11	member	Energy and Telecommunications	2015-01-22 16:58:51	2015	712	senate
t	1865	1	chair_person	Environmental Conservation	2015-01-22 16:58:51	2015	872	senate
t	1869	5	member	Environmental Conservation	2015-01-22 16:58:51	2015	856	senate
t	1870	6	member	Environmental Conservation	2015-01-22 16:58:51	2015	703	senate
t	1871	7	member	Environmental Conservation	2015-01-22 16:58:51	2015	879	senate
t	1872	8	member	Environmental Conservation	2015-01-22 16:58:51	2015	855	senate
f	1873	9	member	Environmental Conservation	2015-01-22 16:58:51	2015	705	senate
f	1874	10	member	Environmental Conservation	2015-01-22 16:58:51	2015	726	senate
f	1875	11	member	Environmental Conservation	2015-01-22 16:58:51	2015	718	senate
f	1876	12	member	Environmental Conservation	2015-01-22 16:58:51	2015	717	senate
f	1877	13	member	Environmental Conservation	2015-01-22 16:58:51	2015	709	senate
t	1878	1	chair_person	Ethics	2015-01-22 16:58:51	2015	720	senate
t	1879	2	member	Ethics	2015-01-22 16:58:51	2015	730	senate
t	1880	3	member	Ethics	2015-01-22 16:58:51	2015	853	senate
t	1881	4	member	Ethics	2015-01-22 16:58:51	2015	885	senate
t	1882	1	chair_person	Finance	2015-01-22 16:58:51	2015	700	senate
t	1883	2	member	Finance	2015-01-22 16:58:51	2015	729	senate
t	1884	3	member	Finance	2015-01-22 16:58:51	2015	853	senate
t	1885	4	member	Finance	2015-01-22 16:58:51	2015	878	senate
t	1886	5	member	Finance	2015-01-22 16:58:51	2015	697	senate
t	1887	6	member	Finance	2015-01-22 16:58:51	2015	702	senate
t	1888	7	member	Finance	2015-01-22 16:58:51	2015	696	senate
t	1889	8	member	Finance	2015-01-22 16:58:51	2015	871	senate
t	1890	9	member	Finance	2015-01-22 16:58:51	2015	730	senate
t	1891	10	member	Finance	2015-01-22 16:58:51	2015	698	senate
t	1892	11	member	Finance	2015-01-22 16:58:51	2015	694	senate
t	1893	12	member	Finance	2015-01-22 16:58:51	2015	856	senate
t	1894	13	member	Finance	2015-01-22 16:58:51	2015	703	senate
t	1895	14	member	Finance	2015-01-22 16:58:51	2015	695	senate
t	1896	15	member	Finance	2015-01-22 16:58:51	2015	852	senate
f	1905	24	vice_chair	Finance	2015-01-22 16:58:51	2015	707	senate
f	1906	25	member	Finance	2015-01-22 16:58:51	2015	727	senate
f	1907	26	member	Finance	2015-01-22 16:58:51	2015	719	senate
f	1908	27	member	Finance	2015-01-22 16:58:51	2015	722	senate
f	1909	28	member	Finance	2015-01-22 16:58:51	2015	718	senate
f	1910	29	member	Finance	2015-01-22 16:58:51	2015	693	senate
f	1911	30	member	Finance	2015-01-22 16:58:51	2015	712	senate
f	1912	31	member	Finance	2015-01-22 16:58:51	2015	711	senate
f	1913	32	member	Finance	2015-01-22 16:58:51	2015	713	senate
f	1914	33	member	Finance	2015-01-22 16:58:51	2015	714	senate
f	1915	34	member	Finance	2015-01-22 16:58:51	2015	708	senate
t	1920	2	vice_chair	Health	2015-01-22 16:58:51	2015	725	senate
t	1921	3	member	Health	2015-01-22 16:58:51	2015	853	senate
t	1922	4	member	Health	2015-01-22 16:58:51	2015	851	senate
t	1923	5	member	Health	2015-01-22 16:58:51	2015	697	senate
t	1924	6	member	Health	2015-01-22 16:58:51	2015	698	senate
t	1927	9	member	Health	2015-01-22 16:58:51	2015	885	senate
t	1928	10	member	Health	2015-01-22 16:58:51	2015	704	senate
t	1929	11	member	Health	2015-01-22 16:58:51	2015	855	senate
f	1930	12	vice_chair	Health	2015-01-22 16:58:51	2015	723	senate
f	1931	13	member	Health	2015-01-22 16:58:51	2015	693	senate
f	1932	14	member	Health	2015-01-22 16:58:51	2015	705	senate
f	1933	15	member	Health	2015-01-22 16:58:51	2015	711	senate
f	1934	16	member	Health	2015-01-22 16:58:51	2015	888	senate
f	1935	17	member	Health	2015-01-22 16:58:51	2015	710	senate
t	1936	1	chair_person	Higher Education	2015-01-22 16:58:51	2015	694	senate
t	1937	2	member	Higher Education	2015-01-22 16:58:51	2015	887	senate
t	1938	3	member	Higher Education	2015-01-22 16:58:51	2015	878	senate
t	1939	4	member	Higher Education	2015-01-22 16:58:51	2015	881	senate
t	1940	5	member	Higher Education	2015-01-22 16:58:51	2015	696	senate
t	1941	6	member	Higher Education	2015-01-22 16:58:51	2015	702	senate
t	1942	7	member	Higher Education	2015-01-22 16:58:51	2015	879	senate
t	1943	8	member	Higher Education	2015-01-22 16:58:51	2015	701	senate
t	1944	9	member	Higher Education	2015-01-22 16:58:51	2015	873	senate
t	1945	10	member	Higher Education	2015-01-22 16:58:51	2015	884	senate
t	1946	11	member	Higher Education	2015-01-22 16:58:51	2015	704	senate
t	1947	12	member	Higher Education	2015-01-22 16:58:51	2015	725	senate
f	1948	13	member	Higher Education	2015-01-22 16:58:51	2015	710	senate
f	1949	14	member	Higher Education	2015-01-22 16:58:51	2015	727	senate
f	1950	15	member	Higher Education	2015-01-22 16:58:51	2015	718	senate
f	1951	16	member	Higher Education	2015-01-22 16:58:51	2015	707	senate
f	1952	17	member	Higher Education	2015-01-22 16:58:51	2015	713	senate
f	1953	18	member	Higher Education	2015-01-22 16:58:51	2015	714	senate
f	1954	19	member	Higher Education	2015-01-22 16:58:51	2015	723	senate
t	1956	2	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	720	senate
t	1957	3	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	729	senate
t	1958	4	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	716	senate
t	1959	5	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	696	senate
t	1960	6	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	852	senate
f	1961	7	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	718	senate
f	1962	8	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	707	senate
f	1963	9	member	Housing, Construction and Community Development	2015-01-22 16:58:51	2015	888	senate
t	1964	1	chair_person	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	703	senate
t	1965	2	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	721	senate
t	1966	3	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	887	senate
t	1967	4	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	696	senate
t	1968	5	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	702	senate
t	1969	6	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	873	senate
f	1970	7	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	712	senate
f	1971	8	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	882	senate
f	1972	9	member	Infrastructure and Capital Investment	2015-01-22 16:58:51	2015	722	senate
t	1973	1	chair_person	Insurance	2015-01-22 16:58:51	2015	704	senate
t	1974	2	member	Insurance	2015-01-22 16:58:51	2015	720	senate
t	1975	3	member	Insurance	2015-01-22 16:58:51	2015	721	senate
t	1976	4	member	Insurance	2015-01-22 16:58:51	2015	878	senate
t	1977	5	member	Insurance	2015-01-22 16:58:51	2015	697	senate
t	1978	6	member	Insurance	2015-01-22 16:58:51	2015	730	senate
t	1979	7	member	Insurance	2015-01-22 16:58:51	2015	698	senate
t	1980	8	member	Insurance	2015-01-22 16:58:51	2015	694	senate
t	1981	9	member	Insurance	2015-01-22 16:58:51	2015	695	senate
t	1982	10	member	Insurance	2015-01-22 16:58:51	2015	872	senate
t	1983	11	member	Insurance	2015-01-22 16:58:51	2015	889	senate
t	1984	12	member	Insurance	2015-01-22 16:58:51	2015	855	senate
f	1985	13	member	Insurance	2015-01-22 16:58:51	2015	727	senate
f	1986	14	member	Insurance	2015-01-22 16:58:51	2015	718	senate
f	1987	15	member	Insurance	2015-01-22 16:58:51	2015	712	senate
f	1988	16	member	Insurance	2015-01-22 16:58:51	2015	717	senate
f	1989	17	member	Insurance	2015-01-22 16:58:51	2015	888	senate
f	1990	18	member	Insurance	2015-01-22 16:58:51	2015	713	senate
f	1991	19	member	Insurance	2015-01-22 16:58:51	2015	875	senate
t	1992	1	chair_person	Investigations and Government Operations	2015-01-22 16:58:51	2015	703	senate
t	1993	2	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	721	senate
t	1994	3	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	697	senate
t	1995	4	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	872	senate
t	1996	5	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	885	senate
t	1997	6	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	852	senate
f	1998	7	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	705	senate
f	1999	8	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	719	senate
f	2000	9	member	Investigations and Government Operations	2015-01-22 16:58:51	2015	715	senate
t	2001	1	chair_person	Judiciary	2015-01-22 16:58:51	2015	729	senate
t	2002	2	member	Judiciary	2015-01-22 16:58:51	2015	720	senate
t	2003	3	member	Judiciary	2015-01-22 16:58:51	2015	700	senate
t	2004	4	member	Judiciary	2015-01-22 16:58:51	2015	878	senate
t	2005	5	member	Judiciary	2015-01-22 16:58:51	2015	871	senate
t	2006	6	member	Judiciary	2015-01-22 16:58:51	2015	730	senate
t	2007	7	member	Judiciary	2015-01-22 16:58:51	2015	694	senate
t	2008	8	member	Judiciary	2015-01-22 16:58:51	2015	856	senate
t	2009	9	member	Judiciary	2015-01-22 16:58:51	2015	852	senate
t	2010	10	member	Judiciary	2015-01-22 16:58:51	2015	872	senate
t	2011	11	member	Judiciary	2015-01-22 16:58:51	2015	850	senate
t	2012	12	member	Judiciary	2015-01-22 16:58:51	2015	724	senate
t	2013	13	member	Judiciary	2015-01-22 16:58:51	2015	884	senate
t	2014	14	member	Judiciary	2015-01-22 16:58:51	2015	889	senate
f	2015	15	vice_chair	Judiciary	2015-01-22 16:58:51	2015	693	senate
f	2016	16	member	Judiciary	2015-01-22 16:58:51	2015	727	senate
f	2017	17	member	Judiciary	2015-01-22 16:58:51	2015	882	senate
f	2018	18	member	Judiciary	2015-01-22 16:58:51	2015	719	senate
f	2019	19	member	Judiciary	2015-01-22 16:58:51	2015	722	senate
f	2020	20	member	Judiciary	2015-01-22 16:58:51	2015	718	senate
f	2021	21	member	Judiciary	2015-01-22 16:58:51	2015	705	senate
f	2022	22	member	Judiciary	2015-01-22 16:58:51	2015	708	senate
f	2023	23	member	Judiciary	2015-01-22 16:58:51	2015	710	senate
t	2024	1	chair_person	Labor	2015-01-22 16:58:51	2015	695	senate
t	2025	2	member	Labor	2015-01-22 16:58:51	2015	700	senate
t	2026	3	member	Labor	2015-01-22 16:58:51	2015	696	senate
t	2027	4	member	Labor	2015-01-22 16:58:51	2015	871	senate
t	2028	5	member	Labor	2015-01-22 16:58:51	2015	703	senate
t	2029	6	member	Labor	2015-01-22 16:58:51	2015	699	senate
t	2030	7	member	Labor	2015-01-22 16:58:51	2015	885	senate
t	2031	8	member	Labor	2015-01-22 16:58:51	2015	873	senate
t	2032	9	member	Labor	2015-01-22 16:58:51	2015	724	senate
t	2033	10	member	Labor	2015-01-22 16:58:51	2015	889	senate
f	2034	11	member	Labor	2015-01-22 16:58:51	2015	714	senate
f	2035	12	member	Labor	2015-01-22 16:58:51	2015	726	senate
f	2036	13	member	Labor	2015-01-22 16:58:51	2015	722	senate
f	2037	14	member	Labor	2015-01-22 16:58:51	2015	708	senate
f	2038	15	member	Labor	2015-01-22 16:58:51	2015	723	senate
f	2039	16	member	Labor	2015-01-22 16:58:51	2015	875	senate
t	2040	1	chair_person	Local Government	2015-01-22 16:58:51	2015	699	senate
t	2041	2	member	Local Government	2015-01-22 16:58:51	2015	716	senate
t	2042	3	member	Local Government	2015-01-22 16:58:51	2015	885	senate
t	2043	4	member	Local Government	2015-01-22 16:58:51	2015	879	senate
t	2044	5	member	Local Government	2015-01-22 16:58:51	2015	701	senate
t	2045	6	member	Local Government	2015-01-22 16:58:51	2015	725	senate
f	2046	7	member	Local Government	2015-01-22 16:58:51	2015	888	senate
f	2047	8	member	Local Government	2015-01-22 16:58:51	2015	705	senate
f	2048	9	member	Local Government	2015-01-22 16:58:51	2015	717	senate
t	2049	1	chair_person	Mental Health and Developmental Disabilities	2015-01-22 16:58:51	2015	879	senate
t	2072	2	vice_chair	Rules	2015-01-22 16:58:51	2015	854	senate
t	2073	3	member	Rules	2015-01-22 16:58:51	2015	729	senate
t	2074	4	member	Rules	2015-01-22 16:58:51	2015	721	senate
t	2075	5	member	Rules	2015-01-22 16:58:51	2015	853	senate
t	2076	6	member	Rules	2015-01-22 16:58:51	2015	878	senate
t	2077	7	member	Rules	2015-01-22 16:58:51	2015	871	senate
t	2078	8	member	Rules	2015-01-22 16:58:51	2015	698	senate
t	2079	9	member	Rules	2015-01-22 16:58:51	2015	694	senate
t	2080	10	member	Rules	2015-01-22 16:58:51	2015	856	senate
t	2081	11	member	Rules	2015-01-22 16:58:51	2015	703	senate
t	2082	12	member	Rules	2015-01-22 16:58:51	2015	852	senate
t	2083	13	member	Rules	2015-01-22 16:58:51	2015	704	senate
t	2084	14	member	Rules	2015-01-22 16:58:51	2015	855	senate
t	2085	15	member	Rules	2015-01-22 16:58:51	2015	725	senate
f	2086	16	vice_chair	Rules	2015-01-22 16:58:51	2015	728	senate
f	2087	17	member	Rules	2015-01-22 16:58:51	2015	727	senate
f	2088	18	member	Rules	2015-01-22 16:58:51	2015	722	senate
f	2089	19	member	Rules	2015-01-22 16:58:51	2015	718	senate
f	2090	20	member	Rules	2015-01-22 16:58:51	2015	706	senate
f	2091	21	member	Rules	2015-01-22 16:58:51	2015	693	senate
f	2092	22	member	Rules	2015-01-22 16:58:51	2015	707	senate
f	2093	23	member	Rules	2015-01-22 16:58:51	2015	711	senate
f	2094	24	member	Rules	2015-01-22 16:58:51	2015	713	senate
f	2095	25	member	Rules	2015-01-22 16:58:51	2015	708	senate
t	2096	1	chair_person	Social Services	2015-01-22 16:58:51	2015	721	senate
t	2097	2	member	Social Services	2015-01-22 16:58:51	2015	853	senate
t	2098	3	member	Social Services	2015-01-22 16:58:51	2015	694	senate
t	2099	4	member	Social Services	2015-01-22 16:58:51	2015	856	senate
t	2100	5	member	Social Services	2015-01-22 16:58:51	2015	695	senate
f	2101	6	member	Social Services	2015-01-22 16:58:51	2015	711	senate
f	2102	7	member	Social Services	2015-01-22 16:58:51	2015	715	senate
t	2103	1	chair_person	Transportation	2015-01-22 16:58:51	2015	873	senate
t	2104	2	vice_chair	Transportation	2015-01-22 16:58:51	2015	703	senate
t	2105	3	member	Transportation	2015-01-22 16:58:51	2015	720	senate
t	2106	4	member	Transportation	2015-01-22 16:58:51	2015	696	senate
t	2107	5	member	Transportation	2015-01-22 16:58:51	2015	698	senate
t	2108	6	member	Transportation	2015-01-22 16:58:51	2015	695	senate
t	2109	7	member	Transportation	2015-01-22 16:58:51	2015	852	senate
t	2110	8	member	Transportation	2015-01-22 16:58:51	2015	872	senate
t	2111	9	member	Transportation	2015-01-22 16:58:51	2015	850	senate
t	2112	10	member	Transportation	2015-01-22 16:58:51	2015	701	senate
t	2113	11	member	Transportation	2015-01-22 16:58:51	2015	725	senate
t	2114	12	member	Transportation	2015-01-22 16:58:51	2015	855	senate
f	2115	13	member	Transportation	2015-01-22 16:58:51	2015	722	senate
f	2116	14	member	Transportation	2015-01-22 16:58:51	2015	719	senate
f	2117	15	member	Transportation	2015-01-22 16:58:51	2015	712	senate
f	2118	16	member	Transportation	2015-01-22 16:58:51	2015	888	senate
f	2119	17	member	Transportation	2015-01-22 16:58:51	2015	708	senate
f	2120	18	member	Transportation	2015-01-22 16:58:51	2015	715	senate
f	2121	19	member	Transportation	2015-01-22 16:58:51	2015	710	senate
t	2122	1	chair_person	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	887	senate
t	2123	2	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	886	senate
t	2124	3	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	721	senate
t	2125	4	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	878	senate
t	2126	5	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	697	senate
t	2127	6	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	702	senate
t	2128	7	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	698	senate
t	2129	8	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	879	senate
f	2130	9	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	726	senate
f	2131	10	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	882	senate
f	2132	11	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	888	senate
f	2133	12	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	875	senate
f	2134	13	member	Veterans, Homeland Security and Military Affairs	2015-01-22 16:58:51	2015	709	senate
t	2135	1	chair_person	New York City Education Subcommittee	2015-01-22 16:58:51	2015	851	senate
t	2136	2	member	New York City Education Subcommittee	2015-01-22 16:58:51	2015	720	senate
t	2137	3	member	New York City Education Subcommittee	2015-01-22 16:58:51	2015	697	senate
t	2138	4	member	New York City Education Subcommittee	2015-01-22 16:58:51	2015	730	senate
f	2139	5	member	New York City Education Subcommittee	2015-01-22 16:58:51	2015	708	senate
f	2140	6	member	New York City Education Subcommittee	2015-01-22 16:58:51	2015	710	senate
t	5017	1	chair_person	Health	2017-02-07 15:16:10	2017	1011	senate
t	5019	3	member	Health	2017-02-07 15:16:10	2017	949	senate
f	5031	15	member	Health	2017-02-07 15:16:10	2017	943	senate
f	5033	17	member	Health	2017-02-07 15:16:10	2017	924	senate
t	5034	1	chair_person	Insurance	2017-02-07 15:16:10	2017	950	senate
t	5018	2	vice_chair	Health	2017-02-07 15:16:10	2017	951	senate
t	5020	4	member	Health	2017-02-07 15:16:10	2017	1013	senate
t	5022	6	member	Health	2017-02-07 15:16:10	2017	1095	senate
t	5024	8	member	Health	2017-02-07 15:16:10	2017	947	senate
t	5026	10	member	Health	2017-02-07 15:16:10	2017	950	senate
f	5028	12	member	Health	2017-02-07 15:16:10	2017	926	senate
f	5030	14	member	Health	2017-02-07 15:16:10	2017	918	senate
t	5043	10	member	Insurance	2017-02-07 15:16:10	2017	941	senate
t	5046	13	member	Insurance	2017-02-07 15:16:10	2017	1018	senate
f	5048	15	member	Insurance	2017-02-07 15:16:10	2017	1097	senate
f	5050	17	member	Insurance	2017-02-07 15:16:10	2017	922	senate
f	5052	19	member	Insurance	2017-02-07 15:16:10	2017	925	senate
t	5188	6	member	Local Government	2017-02-14 09:46:16	2017	949	senate
f	5190	8	member	Local Government	2017-02-14 09:46:16	2017	918	senate
t	5021	5	member	Health	2017-02-07 15:16:10	2017	938	senate
t	5023	7	member	Health	2017-02-07 15:16:10	2017	1012	senate
t	5025	9	member	Health	2017-02-07 15:16:10	2017	1014	senate
t	5027	11	member	Health	2017-02-07 15:16:10	2017	1099	senate
f	5029	13	member	Health	2017-02-07 15:16:10	2017	921	senate
f	5032	16	member	Health	2017-02-07 15:16:10	2017	1086	senate
f	5067	14	member	Banks	2017-02-13 16:36:21	2017	932	senate
f	5071	18	member	Banks	2017-02-13 16:36:21	2017	922	senate
t	5192	1	chair_person	Codes	2017-02-24 09:54:05	2017	1084	senate
t	5194	3	member	Codes	2017-02-24 09:54:05	2017	952	senate
t	5196	5	member	Codes	2017-02-24 09:54:05	2017	955	senate
t	5198	7	member	Codes	2017-02-24 09:54:05	2017	938	senate
t	5250	2	member	Judiciary	2017-02-24 09:54:05	2017	954	senate
t	5252	4	member	Judiciary	2017-02-24 09:54:05	2017	944	senate
t	5255	7	member	Judiciary	2017-02-24 09:54:05	2017	1011	senate
t	5258	10	member	Judiciary	2017-02-24 09:54:05	2017	1010	senate
t	5261	13	member	Judiciary	2017-02-24 09:54:05	2017	948	senate
f	5264	16	member	Judiciary	2017-02-24 09:54:05	2017	921	senate
f	5268	20	member	Judiciary	2017-02-24 09:54:05	2017	927	senate
t	5273	3	member	Rules	2017-02-24 09:54:05	2017	1016	senate
t	5276	6	member	Rules	2017-02-24 09:54:05	2017	1011	senate
t	5279	9	member	Rules	2017-02-24 09:54:05	2017	1010	senate
t	5282	12	member	Rules	2017-02-24 09:54:05	2017	948	senate
t	5285	15	member	Rules	2017-02-24 09:54:05	2017	950	senate
f	5288	18	member	Rules	2017-02-24 09:54:05	2017	946	senate
f	5291	21	member	Rules	2017-02-24 09:54:05	2017	935	senate
f	5294	24	member	Rules	2017-02-24 09:54:05	2017	923	senate
t	5296	2	vice_chair	Transportation	2017-02-24 09:54:05	2017	941	senate
t	5300	6	member	Transportation	2017-02-24 09:54:05	2017	1017	senate
t	5304	10	member	Transportation	2017-02-24 09:54:05	2017	948	senate
t	5307	13	member	Transportation	2017-02-24 09:54:05	2017	942	senate
f	5309	15	member	Transportation	2017-02-24 09:54:05	2017	1085	senate
f	5311	17	member	Transportation	2017-02-24 09:54:05	2017	929	senate
f	5313	19	member	Transportation	2017-02-24 09:54:05	2017	924	senate
t	5035	2	member	Insurance	2017-02-07 15:16:10	2017	938	senate
t	5037	4	member	Insurance	2017-02-07 15:16:10	2017	952	senate
t	5039	6	member	Insurance	2017-02-07 15:16:10	2017	1084	senate
t	5041	8	member	Insurance	2017-02-07 15:16:10	2017	1010	senate
t	5044	11	member	Insurance	2017-02-07 15:16:10	2017	1096	senate
f	5047	14	member	Insurance	2017-02-07 15:16:10	2017	946	senate
f	5049	16	member	Insurance	2017-02-07 15:16:10	2017	929	senate
f	5051	18	member	Insurance	2017-02-07 15:16:10	2017	926	senate
f	5053	20	member	Insurance	2017-02-07 15:16:10	2017	932	senate
t	5055	2	member	Banks	2017-02-13 16:36:21	2017	952	senate
t	5057	4	member	Banks	2017-02-13 16:36:21	2017	1016	senate
t	5059	6	member	Banks	2017-02-13 16:36:21	2017	1095	senate
t	5061	8	member	Banks	2017-02-13 16:36:21	2017	940	senate
t	5063	10	member	Banks	2017-02-13 16:36:21	2017	941	senate
t	5065	12	member	Banks	2017-02-13 16:36:21	2017	933	senate
f	5069	16	member	Banks	2017-02-13 16:36:21	2017	1085	senate
t	5074	2	member	Cities	2017-02-13 16:36:21	2017	952	senate
t	5076	4	member	Cities	2017-02-13 16:36:21	2017	953	senate
f	5078	6	member	Cities	2017-02-13 16:36:21	2017	1086	senate
t	5081	2	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	955	senate
t	5083	4	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	957	senate
t	5085	6	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	949	senate
f	5087	8	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	934	senate
f	5089	10	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	1086	senate
t	5093	4	member	Codes	2017-02-13 16:36:21	2017	944	senate
t	5095	6	member	Codes	2017-02-13 16:36:21	2017	956	senate
t	5098	9	member	Codes	2017-02-13 16:36:21	2017	941	senate
f	5101	12	member	Codes	2017-02-13 16:36:21	2017	923	senate
f	5105	16	member	Codes	2017-02-13 16:36:21	2017	931	senate
t	5131	2	member	Insurance	2017-02-13 16:36:21	2017	938	senate
t	5134	5	member	Insurance	2017-02-13 16:36:21	2017	1095	senate
t	5137	8	member	Insurance	2017-02-13 16:36:21	2017	1010	senate
t	5139	10	member	Insurance	2017-02-13 16:36:21	2017	941	senate
f	5143	14	member	Insurance	2017-02-13 16:36:21	2017	946	senate
f	5145	16	member	Insurance	2017-02-13 16:36:21	2017	922	senate
t	5150	2	member	Labor	2017-02-13 16:36:21	2017	952	senate
t	5154	6	member	Labor	2017-02-13 16:36:21	2017	1014	senate
t	5155	7	member	Labor	2017-02-13 16:36:21	2017	957	senate
t	5157	9	member	Labor	2017-02-13 16:36:21	2017	1015	senate
f	5159	11	member	Labor	2017-02-13 16:36:21	2017	932	senate
f	5161	13	member	Labor	2017-02-13 16:36:21	2017	927	senate
t	5164	1	chair_person	Local Government	2017-02-13 16:36:21	2017	940	senate
t	5166	3	member	Local Government	2017-02-13 16:36:21	2017	1072	senate
t	5168	5	member	Local Government	2017-02-13 16:36:21	2017	957	senate
f	5170	7	member	Local Government	2017-02-13 16:36:21	2017	1097	senate
t	5314	1	chair_person	Codes	2017-03-02 12:36:47	2017	1084	senate
t	5316	3	member	Codes	2017-03-02 12:36:47	2017	952	senate
t	5319	6	member	Codes	2017-03-02 12:36:47	2017	956	senate
t	5333	4	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	1012	senate
f	5335	6	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	923	senate
t	5338	2	vice_chair	Finance	2017-03-02 12:36:47	2017	933	senate
t	5340	4	member	Finance	2017-03-02 12:36:47	2017	1016	senate
t	5344	8	member	Finance	2017-03-02 12:36:47	2017	956	senate
t	5347	11	member	Finance	2017-03-02 12:36:47	2017	1011	senate
t	5349	13	member	Finance	2017-03-02 12:36:47	2017	1012	senate
t	5351	15	member	Finance	2017-03-02 12:36:47	2017	947	senate
t	5354	18	member	Finance	2017-03-02 12:36:47	2017	941	senate
t	5357	21	member	Finance	2017-03-02 12:36:47	2017	949	senate
f	5361	25	member	Finance	2017-03-02 12:36:47	2017	935	senate
f	5364	28	member	Finance	2017-03-02 12:36:47	2017	936	senate
f	5367	31	member	Finance	2017-03-02 12:36:47	2017	918	senate
f	5370	34	member	Finance	2017-03-02 12:36:47	2017	925	senate
f	5373	37	member	Finance	2017-03-02 12:36:47	2017	924	senate
t	5375	2	member	Judiciary	2017-03-02 12:36:47	2017	954	senate
t	5378	5	member	Judiciary	2017-03-02 12:36:47	2017	955	senate
t	5381	8	member	Judiciary	2017-03-02 12:36:47	2017	1095	senate
t	5384	11	member	Judiciary	2017-03-02 12:36:47	2017	941	senate
t	5398	2	vice_chair	Rules	2017-03-02 12:36:47	2017	939	senate
t	5401	5	member	Rules	2017-03-02 12:36:47	2017	1017	senate
t	5404	8	member	Rules	2017-03-02 12:36:47	2017	1012	senate
t	5407	11	member	Rules	2017-03-02 12:36:47	2017	1088	senate
t	5409	13	member	Rules	2017-03-02 12:36:47	2017	1015	senate
t	5412	16	member	Rules	2017-03-02 12:36:47	2017	951	senate
f	5415	19	member	Rules	2017-03-02 12:36:47	2017	927	senate
f	5418	22	member	Rules	2017-03-02 12:36:47	2017	935	senate
f	5420	24	member	Rules	2017-03-02 12:36:47	2017	925	senate
t	5036	3	member	Insurance	2017-02-07 15:16:10	2017	937	senate
t	5038	5	member	Insurance	2017-02-07 15:16:10	2017	1095	senate
t	5040	7	member	Insurance	2017-02-07 15:16:10	2017	1012	senate
t	5042	9	member	Insurance	2017-02-07 15:16:10	2017	1014	senate
t	5045	12	member	Insurance	2017-02-07 15:16:10	2017	933	senate
t	5173	2	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	955	senate
t	5175	4	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	957	senate
t	5177	6	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	949	senate
f	5179	8	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	934	senate
f	5181	10	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	1086	senate
t	5183	1	chair_person	Local Government	2017-02-14 09:46:16	2017	940	senate
t	5185	3	member	Local Government	2017-02-14 09:46:16	2017	1072	senate
t	5187	5	member	Local Government	2017-02-14 09:46:16	2017	957	senate
f	5191	9	member	Local Government	2017-02-14 09:46:16	2017	922	senate
t	5315	2	vice_chair	Codes	2017-03-02 12:36:47	2017	933	senate
t	5318	5	member	Codes	2017-03-02 12:36:47	2017	955	senate
t	5321	8	member	Codes	2017-03-02 12:36:47	2017	1017	senate
t	5323	10	member	Codes	2017-03-02 12:36:47	2017	930	senate
f	5325	12	member	Codes	2017-03-02 12:36:47	2017	923	senate
f	5327	14	member	Codes	2017-03-02 12:36:47	2017	918	senate
f	5329	16	member	Codes	2017-03-02 12:36:47	2017	934	senate
t	5387	14	member	Judiciary	2017-03-02 12:36:47	2017	933	senate
f	5391	18	member	Judiciary	2017-03-02 12:36:47	2017	946	senate
f	5394	21	member	Judiciary	2017-03-02 12:36:47	2017	918	senate
f	5396	23	member	Judiciary	2017-03-02 12:36:47	2017	924	senate
t	5399	3	member	Rules	2017-03-02 12:36:47	2017	1016	senate
t	5403	7	member	Rules	2017-03-02 12:36:47	2017	1084	senate
t	5405	9	member	Rules	2017-03-02 12:36:47	2017	1010	senate
t	5411	15	member	Rules	2017-03-02 12:36:47	2017	950	senate
f	5414	18	member	Rules	2017-03-02 12:36:47	2017	946	senate
f	5417	21	member	Rules	2017-03-02 12:36:47	2017	929	senate
f	5419	23	member	Rules	2017-03-02 12:36:47	2017	943	senate
t	3557	1	chair_person	Aging	2017-01-18 10:52:04	2017	1018	senate
t	3558	2	member	Aging	2017-01-18 10:52:04	2017	1090	senate
t	3559	3	member	Aging	2017-01-18 10:52:04	2017	1013	senate
t	3560	4	member	Aging	2017-01-18 10:52:04	2017	953	senate
t	3561	5	member	Aging	2017-01-18 10:52:04	2017	938	senate
t	3562	6	member	Aging	2017-01-18 10:52:04	2017	1010	senate
t	3563	7	member	Aging	2017-01-18 10:52:04	2017	940	senate
f	3564	8	member	Aging	2017-01-18 10:52:04	2017	1085	senate
f	3565	9	member	Aging	2017-01-18 10:52:04	2017	919	senate
f	3566	10	member	Aging	2017-01-18 10:52:04	2017	1097	senate
f	3567	11	member	Aging	2017-01-18 10:52:04	2017	934	senate
t	3568	1	chair_person	Agriculture	2017-01-18 10:52:04	2017	949	senate
t	3569	2	member	Agriculture	2017-01-18 10:52:04	2017	953	senate
t	3570	3	member	Agriculture	2017-01-18 10:52:04	2017	956	senate
t	3571	4	member	Agriculture	2017-01-18 10:52:04	2017	941	senate
t	3572	5	member	Agriculture	2017-01-18 10:52:04	2017	948	senate
t	3573	6	member	Agriculture	2017-01-18 10:52:04	2017	950	senate
t	3574	7	member	Agriculture	2017-01-18 10:52:04	2017	951	senate
f	3575	8	member	Agriculture	2017-01-18 10:52:04	2017	1097	senate
f	3576	9	member	Agriculture	2017-01-18 10:52:04	2017	936	senate
f	3577	10	member	Agriculture	2017-01-18 10:52:04	2017	943	senate
t	3579	1	chair_person	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	954	senate
t	3588	3	member	Banks	2017-01-18 10:52:04	2017	954	senate
t	3590	5	member	Banks	2017-01-18 10:52:04	2017	945	senate
t	3591	6	member	Banks	2017-01-18 10:52:04	2017	1095	senate
t	3593	8	member	Banks	2017-01-18 10:52:04	2017	940	senate
f	3598	13	member	Banks	2017-01-18 10:52:04	2017	932	senate
f	3600	15	member	Banks	2017-01-18 10:52:04	2017	946	senate
f	3603	18	member	Banks	2017-01-18 10:52:04	2017	922	senate
t	3606	2	member	Children and Families	2017-01-18 10:52:04	2017	1016	senate
t	3612	1	chair_person	Cities	2017-01-18 10:52:04	2017	1013	senate
t	3614	3	member	Cities	2017-01-18 10:52:04	2017	920	senate
t	3616	5	member	Cities	2017-01-18 10:52:04	2017	1095	senate
t	3621	3	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	1084	senate
t	3625	7	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	933	senate
f	3642	13	member	Codes	2017-01-18 10:52:04	2017	921	senate
f	3643	14	member	Codes	2017-01-18 10:52:04	2017	918	senate
f	3644	15	member	Codes	2017-01-18 10:52:04	2017	935	senate
f	3653	8	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	929	senate
f	3655	10	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	1086	senate
t	3658	2	member	Consumer Protection	2017-01-18 10:52:04	2017	954	senate
t	3660	4	member	Consumer Protection	2017-01-18 10:52:04	2017	953	senate
t	3661	5	member	Consumer Protection	2017-01-18 10:52:04	2017	937	senate
t	3662	6	member	Consumer Protection	2017-01-18 10:52:04	2017	945	senate
t	3663	7	member	Consumer Protection	2017-01-18 10:52:04	2017	1015	senate
f	3664	8	member	Consumer Protection	2017-01-18 10:52:04	2017	936	senate
f	3665	9	member	Consumer Protection	2017-01-18 10:52:04	2017	922	senate
f	3667	11	member	Consumer Protection	2017-01-18 10:52:04	2017	934	senate
t	3669	2	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	1090	senate
t	3671	4	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	1012	senate
t	3675	1	chair_person	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	956	senate
t	3677	3	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	1017	senate
t	3683	9	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	1099	senate
f	3684	10	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	1098	senate
t	3734	6	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	957	senate
f	3739	11	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	929	senate
t	3743	4	member	Environmental Conservation	2017-01-18 10:52:04	2017	1010	senate
f	3752	13	member	Environmental Conservation	2017-01-18 10:52:04	2017	922	senate
t	3756	4	member	Ethics	2017-01-18 10:52:04	2017	1084	senate
t	3758	6	member	Ethics	2017-01-18 10:52:04	2017	951	senate
f	3760	8	member	Ethics	2017-01-18 10:52:04	2017	926	senate
t	3763	2	vice_chair	Finance	2017-01-18 10:52:04	2017	933	senate
t	3765	4	member	Finance	2017-01-18 10:52:04	2017	1016	senate
t	3767	6	member	Finance	2017-01-18 10:52:04	2017	1072	senate
t	3769	8	member	Finance	2017-01-18 10:52:04	2017	956	senate
t	3771	10	member	Finance	2017-01-18 10:52:04	2017	1017	senate
t	3773	12	member	Finance	2017-01-18 10:52:04	2017	1084	senate
t	3775	14	member	Finance	2017-01-18 10:52:04	2017	1010	senate
t	3777	16	member	Finance	2017-01-18 10:52:04	2017	1088	senate
t	3779	18	member	Finance	2017-01-18 10:52:04	2017	941	senate
t	3781	20	member	Finance	2017-01-18 10:52:04	2017	949	senate
t	3783	22	member	Finance	2017-01-18 10:52:04	2017	950	senate
f	3785	24	member	Finance	2017-01-18 10:52:04	2017	935	senate
f	3787	26	member	Finance	2017-01-18 10:52:04	2017	936	senate
f	3789	28	member	Finance	2017-01-18 10:52:04	2017	927	senate
f	3791	30	member	Finance	2017-01-18 10:52:04	2017	929	senate
f	3793	32	member	Finance	2017-01-18 10:52:04	2017	925	senate
f	3795	34	member	Finance	2017-01-18 10:52:04	2017	931	senate
f	3797	36	member	Finance	2017-01-18 10:52:04	2017	923	senate
t	3799	1	chair_person	Health	2017-01-18 10:52:04	2017	1011	senate
t	3800	2	vice_chair	Health	2017-01-18 10:52:04	2017	951	senate
t	3801	3	member	Health	2017-01-18 10:52:04	2017	1013	senate
t	3803	5	member	Health	2017-01-18 10:52:04	2017	1095	senate
t	3804	6	member	Health	2017-01-18 10:52:04	2017	1012	senate
t	3805	7	member	Health	2017-01-18 10:52:04	2017	947	senate
t	3807	9	member	Health	2017-01-18 10:52:04	2017	950	senate
t	3808	10	member	Health	2017-01-18 10:52:04	2017	1099	senate
f	3809	11	member	Health	2017-01-18 10:52:04	2017	926	senate
f	3811	13	member	Health	2017-01-18 10:52:04	2017	918	senate
f	3814	16	member	Health	2017-01-18 10:52:04	2017	924	senate
t	3815	1	chair_person	Higher Education	2017-01-18 10:52:04	2017	1010	senate
t	3817	3	member	Higher Education	2017-01-18 10:52:04	2017	955	senate
t	3819	5	member	Higher Education	2017-01-18 10:52:04	2017	956	senate
t	3821	7	member	Higher Education	2017-01-18 10:52:04	2017	945	senate
t	3823	9	member	Higher Education	2017-01-18 10:52:04	2017	949	senate
t	3825	11	member	Higher Education	2017-01-18 10:52:04	2017	1018	senate
f	3829	15	member	Higher Education	2017-01-18 10:52:04	2017	946	senate
f	3830	16	member	Higher Education	2017-01-18 10:52:04	2017	935	senate
f	3832	18	member	Higher Education	2017-01-18 10:52:04	2017	930	senate
t	3834	1	chair_person	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	947	senate
t	3836	3	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	1016	senate
t	3838	5	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	956	senate
t	3839	6	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	942	senate
f	3841	8	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	1097	senate
f	3842	9	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	935	senate
t	3843	1	chair_person	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	1096	senate
t	3872	2	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	1072	senate
t	3874	4	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	1084	senate
t	3876	6	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	1096	senate
f	3878	8	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	1085	senate
t	3881	2	member	Judiciary	2017-01-18 10:52:04	2017	954	senate
f	3578	11	member	Agriculture	2017-01-18 10:52:04	2017	934	senate
t	3580	2	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	1016	senate
t	3582	4	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	1096	senate
f	3584	6	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	925	senate
t	3586	1	chair_person	Banks	2017-01-18 10:52:04	2017	937	senate
t	3587	2	member	Banks	2017-01-18 10:52:04	2017	952	senate
t	3589	4	member	Banks	2017-01-18 10:52:04	2017	1016	senate
t	3597	12	member	Banks	2017-01-18 10:52:04	2017	1099	senate
f	3627	9	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	919	senate
f	3629	11	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	924	senate
t	3638	9	member	Codes	2017-01-18 10:52:04	2017	941	senate
f	3640	11	member	Codes	2017-01-18 10:52:04	2017	923	senate
f	3654	9	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	1098	senate
f	3656	11	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	932	senate
t	3657	1	chair_person	Consumer Protection	2017-01-18 10:52:04	2017	1072	senate
t	3659	3	member	Consumer Protection	2017-01-18 10:52:04	2017	944	senate
f	3666	10	member	Consumer Protection	2017-01-18 10:52:04	2017	930	senate
f	3673	6	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	931	senate
t	3679	5	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	945	senate
t	3681	7	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	947	senate
f	3686	12	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	930	senate
t	3696	9	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	1018	senate
f	3697	10	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	934	senate
f	3698	11	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	921	senate
f	3700	13	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	1086	senate
t	3707	7	member	Education	2017-01-18 10:52:04	2017	1010	senate
t	3709	9	member	Education	2017-01-18 10:52:04	2017	948	senate
t	3711	11	member	Education	2017-01-18 10:52:04	2017	1018	senate
f	3713	13	member	Education	2017-01-18 10:52:04	2017	922	senate
f	3715	15	member	Education	2017-01-18 10:52:04	2017	946	senate
f	3717	17	member	Education	2017-01-18 10:52:04	2017	943	senate
f	3719	19	member	Education	2017-01-18 10:52:04	2017	924	senate
t	3721	2	member	Elections	2017-01-18 10:52:04	2017	954	senate
t	3723	4	member	Elections	2017-01-18 10:52:04	2017	956	senate
t	3725	6	member	Elections	2017-01-18 10:52:04	2017	1099	senate
f	3727	8	member	Elections	2017-01-18 10:52:04	2017	927	senate
t	3729	1	chair_person	Energy and Telecommunications	2017-01-18 10:52:04	2017	1017	senate
t	3731	3	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	955	senate
t	3581	3	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	1072	senate
t	3583	5	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	949	senate
f	3585	7	member	Alcoholism and Drug Abuse	2017-01-18 10:52:04	2017	918	senate
t	3592	7	member	Banks	2017-01-18 10:52:04	2017	1088	senate
t	3594	9	member	Banks	2017-01-18 10:52:04	2017	1014	senate
t	3595	10	member	Banks	2017-01-18 10:52:04	2017	941	senate
t	3596	11	member	Banks	2017-01-18 10:52:04	2017	933	senate
f	3599	14	member	Banks	2017-01-18 10:52:04	2017	1098	senate
f	3601	16	member	Banks	2017-01-18 10:52:04	2017	1085	senate
f	3602	17	member	Banks	2017-01-18 10:52:04	2017	929	senate
f	3604	19	member	Banks	2017-01-18 10:52:04	2017	925	senate
t	3605	1	chair_person	Children and Families	2017-01-18 10:52:04	2017	920	senate
t	3607	3	member	Children and Families	2017-01-18 10:52:04	2017	945	senate
t	3608	4	member	Children and Families	2017-01-18 10:52:04	2017	933	senate
t	3609	5	member	Children and Families	2017-01-18 10:52:04	2017	1018	senate
f	3610	6	member	Children and Families	2017-01-18 10:52:04	2017	943	senate
f	3611	7	member	Children and Families	2017-01-18 10:52:04	2017	1086	senate
t	3613	2	member	Cities	2017-01-18 10:52:04	2017	952	senate
t	3615	4	member	Cities	2017-01-18 10:52:04	2017	953	senate
f	3617	6	member	Cities	2017-01-18 10:52:04	2017	1086	senate
f	3618	7	member	Cities	2017-01-18 10:52:04	2017	930	senate
t	3619	1	chair_person	Civil Service and Pensions	2017-01-18 10:52:04	2017	938	senate
t	3620	2	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	955	senate
t	3622	4	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	957	senate
t	3623	5	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	1096	senate
t	3624	6	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	949	senate
f	3626	8	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	932	senate
f	3628	10	member	Civil Service and Pensions	2017-01-18 10:52:04	2017	1086	senate
t	3630	1	chair_person	Codes	2017-01-18 10:52:04	2017	1084	senate
t	3631	2	vice_chair	Codes	2017-01-18 10:52:04	2017	933	senate
t	3632	3	member	Codes	2017-01-18 10:52:04	2017	952	senate
t	3633	4	member	Codes	2017-01-18 10:52:04	2017	944	senate
t	3634	5	member	Codes	2017-01-18 10:52:04	2017	955	senate
t	3635	6	member	Codes	2017-01-18 10:52:04	2017	956	senate
t	3636	7	member	Codes	2017-01-18 10:52:04	2017	938	senate
t	3637	8	member	Codes	2017-01-18 10:52:04	2017	1017	senate
t	3639	10	member	Codes	2017-01-18 10:52:04	2017	1099	senate
f	3641	12	member	Codes	2017-01-18 10:52:04	2017	1098	senate
f	3645	16	member	Codes	2017-01-18 10:52:04	2017	931	senate
t	3646	1	chair_person	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	944	senate
t	3647	2	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	1013	senate
t	3648	3	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	953	senate
t	3649	4	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	956	senate
t	3650	5	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	1017	senate
t	3651	6	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	937	senate
t	3652	7	member	Commerce, Economic Development and Small Business	2017-01-18 10:52:04	2017	1015	senate
t	3668	1	chair_person	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	948	senate
t	3670	3	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	954	senate
t	3672	5	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	957	senate
f	3674	7	member	Corporations, Authorities and Commissions	2017-01-18 10:52:04	2017	923	senate
t	3676	2	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	952	senate
t	3678	4	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	937	senate
t	3680	6	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	1095	senate
t	3682	8	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	949	senate
f	3685	11	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	943	senate
f	3687	13	member	Crime Victims, Crime and Correction	2017-01-18 10:52:04	2017	926	senate
t	3688	1	chair_person	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	953	senate
t	3689	2	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	1090	senate
t	3690	3	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	920	senate
t	3691	4	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	945	senate
t	3692	5	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	947	senate
t	3693	6	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	1088	senate
t	3694	7	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	940	senate
t	3695	8	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	949	senate
f	3699	12	member	Cultural Affairs, Tourism, Parks and Recreation	2017-01-18 10:52:04	2017	929	senate
t	3701	1	chair_person	Education	2017-01-18 10:52:04	2017	1088	senate
t	3702	2	vice_chair	Education	2017-01-18 10:52:04	2017	956	senate
t	3703	3	member	Education	2017-01-18 10:52:04	2017	920	senate
t	3704	4	member	Education	2017-01-18 10:52:04	2017	955	senate
t	3705	5	member	Education	2017-01-18 10:52:04	2017	937	senate
t	3706	6	member	Education	2017-01-18 10:52:04	2017	1084	senate
t	3708	8	member	Education	2017-01-18 10:52:04	2017	947	senate
t	3710	10	member	Education	2017-01-18 10:52:04	2017	1015	senate
t	3712	12	member	Education	2017-01-18 10:52:04	2017	950	senate
f	3714	14	member	Education	2017-01-18 10:52:04	2017	919	senate
f	3716	16	member	Education	2017-01-18 10:52:04	2017	1097	senate
f	3718	18	member	Education	2017-01-18 10:52:04	2017	930	senate
t	3720	1	chair_person	Elections	2017-01-18 10:52:04	2017	952	senate
t	3722	3	member	Elections	2017-01-18 10:52:04	2017	920	senate
t	3724	5	member	Elections	2017-01-18 10:52:04	2017	940	senate
f	3726	7	member	Elections	2017-01-18 10:52:04	2017	936	senate
f	3728	9	member	Elections	2017-01-18 10:52:04	2017	935	senate
t	3730	2	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	1072	senate
t	3732	4	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	947	senate
t	3733	5	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	941	senate
t	3735	7	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	949	senate
f	3736	8	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	925	senate
f	3738	10	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	927	senate
t	3740	1	chair_person	Environmental Conservation	2017-01-18 10:52:04	2017	941	senate
t	3741	2	member	Environmental Conservation	2017-01-18 10:52:04	2017	920	senate
t	3745	6	member	Environmental Conservation	2017-01-18 10:52:04	2017	1088	senate
t	3748	9	member	Environmental Conservation	2017-01-18 10:52:04	2017	1099	senate
f	3750	11	member	Environmental Conservation	2017-01-18 10:52:04	2017	919	senate
t	3753	1	chair_person	Ethics	2017-01-18 10:52:04	2017	1096	senate
t	3755	3	member	Ethics	2017-01-18 10:52:04	2017	955	senate
f	3761	9	member	Ethics	2017-01-18 10:52:04	2017	1086	senate
t	3802	4	member	Health	2017-01-18 10:52:04	2017	938	senate
t	3806	8	member	Health	2017-01-18 10:52:04	2017	1014	senate
f	3810	12	member	Health	2017-01-18 10:52:04	2017	921	senate
f	3813	15	member	Health	2017-01-18 10:52:04	2017	1086	senate
f	3827	13	member	Higher Education	2017-01-18 10:52:04	2017	924	senate
t	3844	2	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	1090	senate
t	3846	4	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	1013	senate
t	3848	6	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	1015	senate
f	3850	8	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	936	senate
t	3871	1	chair_person	Investigations and Government Operations	2017-01-18 10:52:04	2017	1014	senate
t	3873	3	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	938	senate
t	3875	5	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	941	senate
f	3877	7	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	921	senate
f	3879	9	member	Investigations and Government Operations	2017-01-18 10:52:04	2017	923	senate
t	3889	10	member	Judiciary	2017-01-18 10:52:04	2017	1010	senate
f	3895	16	member	Judiciary	2017-01-18 10:52:04	2017	921	senate
f	3899	20	member	Judiciary	2017-01-18 10:52:04	2017	927	senate
t	3904	2	member	Labor	2017-01-18 10:52:04	2017	952	senate
t	3910	8	member	Labor	2017-01-18 10:52:04	2017	949	senate
t	3912	10	member	Labor	2017-01-18 10:52:04	2017	933	senate
t	3919	2	member	Local Government	2017-01-18 10:52:04	2017	944	senate
t	3922	5	member	Local Government	2017-01-18 10:52:04	2017	957	senate
t	3928	2	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1072	senate
t	3931	5	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1011	senate
f	3934	8	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1097	senate
f	3936	10	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	935	senate
t	3938	1	chair_person	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	1016	senate
t	3940	3	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	944	senate
t	3942	5	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	940	senate
t	3984	4	member	Transportation	2017-01-18 10:52:04	2017	955	senate
t	3986	6	member	Transportation	2017-01-18 10:52:04	2017	1017	senate
t	3988	8	member	Transportation	2017-01-18 10:52:04	2017	1088	senate
t	3991	11	member	Transportation	2017-01-18 10:52:04	2017	951	senate
f	3994	14	member	Transportation	2017-01-18 10:52:04	2017	1085	senate
f	3996	16	member	Transportation	2017-01-18 10:52:04	2017	929	senate
f	3737	9	member	Energy and Telecommunications	2017-01-18 10:52:04	2017	936	senate
t	3744	5	member	Environmental Conservation	2017-01-18 10:52:04	2017	947	senate
t	3746	7	member	Environmental Conservation	2017-01-18 10:52:04	2017	957	senate
t	3747	8	member	Environmental Conservation	2017-01-18 10:52:04	2017	1096	senate
f	3749	10	member	Environmental Conservation	2017-01-18 10:52:04	2017	918	senate
f	3751	12	member	Environmental Conservation	2017-01-18 10:52:04	2017	921	senate
t	3754	2	member	Ethics	2017-01-18 10:52:04	2017	1072	senate
t	3757	5	member	Ethics	2017-01-18 10:52:04	2017	933	senate
f	3759	7	member	Ethics	2017-01-18 10:52:04	2017	928	senate
t	3762	1	chair_person	Finance	2017-01-18 10:52:04	2017	942	senate
t	3764	3	member	Finance	2017-01-18 10:52:04	2017	920	senate
t	3766	5	member	Finance	2017-01-18 10:52:04	2017	944	senate
t	3768	7	member	Finance	2017-01-18 10:52:04	2017	1013	senate
t	3770	9	member	Finance	2017-01-18 10:52:04	2017	938	senate
t	3772	11	member	Finance	2017-01-18 10:52:04	2017	1011	senate
t	3774	13	member	Finance	2017-01-18 10:52:04	2017	1012	senate
t	3776	15	member	Finance	2017-01-18 10:52:04	2017	947	senate
t	3778	17	member	Finance	2017-01-18 10:52:04	2017	940	senate
t	3780	19	member	Finance	2017-01-18 10:52:04	2017	948	senate
t	3782	21	member	Finance	2017-01-18 10:52:04	2017	1015	senate
t	3784	23	member	Finance	2017-01-18 10:52:04	2017	951	senate
f	3786	25	member	Finance	2017-01-18 10:52:04	2017	946	senate
f	3788	27	member	Finance	2017-01-18 10:52:04	2017	1085	senate
f	3790	29	member	Finance	2017-01-18 10:52:04	2017	918	senate
f	3792	31	member	Finance	2017-01-18 10:52:04	2017	943	senate
f	3794	33	member	Finance	2017-01-18 10:52:04	2017	930	senate
f	3796	35	member	Finance	2017-01-18 10:52:04	2017	926	senate
f	3798	37	member	Finance	2017-01-18 10:52:04	2017	924	senate
f	3812	14	member	Health	2017-01-18 10:52:04	2017	943	senate
t	3845	3	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	955	senate
t	3847	5	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	956	senate
f	3849	7	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	929	senate
f	3851	9	member	Infrastructure and Capital Investment	2017-01-18 10:52:04	2017	927	senate
t	3852	1	chair_person	Insurance	2017-01-18 10:52:04	2017	950	senate
t	3854	3	member	Insurance	2017-01-18 10:52:04	2017	937	senate
t	3856	5	member	Insurance	2017-01-18 10:52:04	2017	1084	senate
t	3858	7	member	Insurance	2017-01-18 10:52:04	2017	1010	senate
t	3860	9	member	Insurance	2017-01-18 10:52:04	2017	941	senate
t	3862	11	member	Insurance	2017-01-18 10:52:04	2017	933	senate
f	3864	13	member	Insurance	2017-01-18 10:52:04	2017	946	senate
f	3866	15	member	Insurance	2017-01-18 10:52:04	2017	929	senate
f	3868	17	member	Insurance	2017-01-18 10:52:04	2017	926	senate
f	3870	19	member	Insurance	2017-01-18 10:52:04	2017	932	senate
t	3883	4	member	Judiciary	2017-01-18 10:52:04	2017	944	senate
t	3887	8	member	Judiciary	2017-01-18 10:52:04	2017	1095	senate
f	3925	8	member	Local Government	2017-01-18 10:52:04	2017	1097	senate
t	3944	7	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	951	senate
f	3947	10	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	922	senate
f	3970	22	member	Rules	2017-01-18 10:52:04	2017	943	senate
f	3972	24	member	Rules	2017-01-18 10:52:04	2017	931	senate
t	3974	1	chair_person	Social Services	2017-01-18 10:52:04	2017	1099	senate
t	3976	3	member	Social Services	2017-01-18 10:52:04	2017	954	senate
t	3978	5	member	Social Services	2017-01-18 10:52:04	2017	1010	senate
f	3980	7	member	Social Services	2017-01-18 10:52:04	2017	923	senate
t	3981	1	chair_person	Transportation	2017-01-18 10:52:04	2017	1015	senate
t	3982	2	vice_chair	Transportation	2017-01-18 10:52:04	2017	941	senate
t	3983	3	member	Transportation	2017-01-18 10:52:04	2017	920	senate
t	3985	5	member	Transportation	2017-01-18 10:52:04	2017	956	senate
t	3987	7	member	Transportation	2017-01-18 10:52:04	2017	1012	senate
t	3989	9	member	Transportation	2017-01-18 10:52:04	2017	948	senate
f	3993	13	member	Transportation	2017-01-18 10:52:04	2017	927	senate
f	3997	17	member	Transportation	2017-01-18 10:52:04	2017	931	senate
f	3999	19	member	Transportation	2017-01-18 10:52:04	2017	924	senate
f	4017	5	member	New York City Education Subcommittee	2017-01-18 10:52:04	2017	931	senate
t	3742	3	member	Environmental Conservation	2017-01-18 10:52:04	2017	953	senate
t	3816	2	member	Higher Education	2017-01-18 10:52:04	2017	1090	senate
t	3818	4	member	Higher Education	2017-01-18 10:52:04	2017	953	senate
t	3820	6	member	Higher Education	2017-01-18 10:52:04	2017	1017	senate
t	3822	8	member	Higher Education	2017-01-18 10:52:04	2017	957	senate
t	3824	10	member	Higher Education	2017-01-18 10:52:04	2017	1015	senate
t	3826	12	member	Higher Education	2017-01-18 10:52:04	2017	950	senate
f	3828	14	member	Higher Education	2017-01-18 10:52:04	2017	1098	senate
f	3831	17	member	Higher Education	2017-01-18 10:52:04	2017	925	senate
f	3833	19	member	Higher Education	2017-01-18 10:52:04	2017	926	senate
t	3835	2	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	920	senate
t	3837	4	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	944	senate
f	3840	7	member	Housing, Construction and Community Development	2017-01-18 10:52:04	2017	1098	senate
t	3853	2	member	Insurance	2017-01-18 10:52:04	2017	938	senate
t	3855	4	member	Insurance	2017-01-18 10:52:04	2017	1095	senate
t	3857	6	member	Insurance	2017-01-18 10:52:04	2017	1012	senate
t	3859	8	member	Insurance	2017-01-18 10:52:04	2017	1014	senate
t	3861	10	member	Insurance	2017-01-18 10:52:04	2017	1096	senate
t	3863	12	member	Insurance	2017-01-18 10:52:04	2017	1018	senate
f	3865	14	member	Insurance	2017-01-18 10:52:04	2017	1097	senate
f	3867	16	member	Insurance	2017-01-18 10:52:04	2017	922	senate
f	3869	18	member	Insurance	2017-01-18 10:52:04	2017	925	senate
t	3880	1	chair_person	Judiciary	2017-01-18 10:52:04	2017	1016	senate
t	3882	3	member	Judiciary	2017-01-18 10:52:04	2017	920	senate
t	3884	5	member	Judiciary	2017-01-18 10:52:04	2017	955	senate
t	3886	7	member	Judiciary	2017-01-18 10:52:04	2017	1011	senate
t	3888	9	member	Judiciary	2017-01-18 10:52:04	2017	1084	senate
t	3890	11	member	Judiciary	2017-01-18 10:52:04	2017	941	senate
t	3892	13	member	Judiciary	2017-01-18 10:52:04	2017	948	senate
t	3894	15	member	Judiciary	2017-01-18 10:52:04	2017	1018	senate
f	3896	17	member	Judiciary	2017-01-18 10:52:04	2017	1098	senate
f	3898	19	member	Judiciary	2017-01-18 10:52:04	2017	1085	senate
f	3900	21	member	Judiciary	2017-01-18 10:52:04	2017	918	senate
f	3902	23	member	Judiciary	2017-01-18 10:52:04	2017	924	senate
t	3903	1	chair_person	Labor	2017-01-18 10:52:04	2017	1090	senate
t	3905	3	member	Labor	2017-01-18 10:52:04	2017	956	senate
t	3907	5	member	Labor	2017-01-18 10:52:04	2017	1088	senate
t	3909	7	member	Labor	2017-01-18 10:52:04	2017	957	senate
t	3911	9	member	Labor	2017-01-18 10:52:04	2017	1015	senate
f	3913	11	member	Labor	2017-01-18 10:52:04	2017	930	senate
f	3915	13	member	Labor	2017-01-18 10:52:04	2017	927	senate
f	3917	15	member	Labor	2017-01-18 10:52:04	2017	932	senate
t	3918	1	chair_person	Local Government	2017-01-18 10:52:04	2017	940	senate
t	3920	3	member	Local Government	2017-01-18 10:52:04	2017	1072	senate
f	3924	7	member	Local Government	2017-01-18 10:52:04	2017	918	senate
f	3946	9	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	936	senate
f	3948	11	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	932	senate
t	3954	6	member	Rules	2017-01-18 10:52:04	2017	1011	senate
t	3956	8	member	Rules	2017-01-18 10:52:04	2017	1012	senate
t	3957	9	member	Rules	2017-01-18 10:52:04	2017	1010	senate
t	3958	10	member	Rules	2017-01-18 10:52:04	2017	947	senate
t	3959	11	member	Rules	2017-01-18 10:52:04	2017	1088	senate
t	3961	13	member	Rules	2017-01-18 10:52:04	2017	1015	senate
t	3963	15	member	Rules	2017-01-18 10:52:04	2017	950	senate
f	3965	17	member	Rules	2017-01-18 10:52:04	2017	1087	senate
f	3967	19	member	Rules	2017-01-18 10:52:04	2017	927	senate
f	3969	21	member	Rules	2017-01-18 10:52:04	2017	935	senate
f	3973	25	member	Rules	2017-01-18 10:52:04	2017	923	senate
t	4000	1	chair_person	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	955	senate
t	4002	3	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	1072	senate
t	4004	5	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	938	senate
t	4006	7	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	1012	senate
t	4008	9	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	957	senate
f	4010	11	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	936	senate
f	4012	13	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	932	senate
t	4013	1	chair_person	New York City Education Subcommittee	2017-01-18 10:52:04	2017	1013	senate
t	3885	6	member	Judiciary	2017-01-18 10:52:04	2017	937	senate
t	3891	12	member	Judiciary	2017-01-18 10:52:04	2017	1096	senate
t	3893	14	member	Judiciary	2017-01-18 10:52:04	2017	933	senate
f	3897	18	member	Judiciary	2017-01-18 10:52:04	2017	946	senate
f	3901	22	member	Judiciary	2017-01-18 10:52:04	2017	931	senate
t	3906	4	member	Labor	2017-01-18 10:52:04	2017	1011	senate
t	3908	6	member	Labor	2017-01-18 10:52:04	2017	1014	senate
f	3914	12	member	Labor	2017-01-18 10:52:04	2017	919	senate
f	3916	14	member	Labor	2017-01-18 10:52:04	2017	926	senate
t	3921	4	member	Local Government	2017-01-18 10:52:04	2017	1014	senate
t	3923	6	member	Local Government	2017-01-18 10:52:04	2017	949	senate
f	3926	9	member	Local Government	2017-01-18 10:52:04	2017	922	senate
t	3927	1	chair_person	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	957	senate
t	3929	3	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1013	senate
t	3930	4	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	937	senate
t	3932	6	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1014	senate
t	3933	7	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1018	senate
f	3935	9	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	1098	senate
f	3937	11	member	Mental Health and Developmental Disabilities	2017-01-18 10:52:04	2017	926	senate
t	3939	2	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	952	senate
t	3941	4	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	1017	senate
t	3943	6	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	948	senate
f	3945	8	member	Racing, Gaming and Wagering	2017-01-18 10:52:04	2017	919	senate
t	3949	1	chair_person	Rules	2017-01-18 10:52:04	2017	1089	senate
t	3950	2	vice_chair	Rules	2017-01-18 10:52:04	2017	939	senate
t	3951	3	member	Rules	2017-01-18 10:52:04	2017	1016	senate
t	3952	4	member	Rules	2017-01-18 10:52:04	2017	1072	senate
t	3953	5	member	Rules	2017-01-18 10:52:04	2017	1017	senate
t	3955	7	member	Rules	2017-01-18 10:52:04	2017	1084	senate
t	3960	12	member	Rules	2017-01-18 10:52:04	2017	948	senate
t	3962	14	member	Rules	2017-01-18 10:52:04	2017	933	senate
t	3964	16	member	Rules	2017-01-18 10:52:04	2017	951	senate
f	3966	18	member	Rules	2017-01-18 10:52:04	2017	946	senate
f	3968	20	member	Rules	2017-01-18 10:52:04	2017	928	senate
f	3971	23	member	Rules	2017-01-18 10:52:04	2017	925	senate
t	3975	2	member	Social Services	2017-01-18 10:52:04	2017	1090	senate
t	3977	4	member	Social Services	2017-01-18 10:52:04	2017	945	senate
f	3979	6	member	Social Services	2017-01-18 10:52:04	2017	1086	senate
t	3990	10	member	Transportation	2017-01-18 10:52:04	2017	949	senate
t	3992	12	member	Transportation	2017-01-18 10:52:04	2017	942	senate
f	3995	15	member	Transportation	2017-01-18 10:52:04	2017	918	senate
f	3998	18	member	Transportation	2017-01-18 10:52:04	2017	923	senate
t	4001	2	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	954	senate
t	4003	4	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	1013	senate
t	4005	6	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	1095	senate
t	4007	8	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	940	senate
f	4009	10	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	919	senate
f	4011	12	member	Veterans, Homeland Security and Military Affairs	2017-01-18 10:52:04	2017	918	senate
t	4014	2	member	New York City Education Subcommittee	2017-01-18 10:52:04	2017	920	senate
t	4015	3	member	New York City Education Subcommittee	2017-01-18 10:52:04	2017	938	senate
t	4016	4	member	New York City Education Subcommittee	2017-01-18 10:52:04	2017	1084	senate
f	4018	6	member	New York City Education Subcommittee	2017-01-18 10:52:04	2017	924	senate
t	5054	1	chair_person	Banks	2017-02-13 16:36:21	2017	937	senate
t	5056	3	member	Banks	2017-02-13 16:36:21	2017	954	senate
t	5058	5	member	Banks	2017-02-13 16:36:21	2017	945	senate
t	5060	7	member	Banks	2017-02-13 16:36:21	2017	1088	senate
t	5062	9	member	Banks	2017-02-13 16:36:21	2017	1014	senate
t	5064	11	member	Banks	2017-02-13 16:36:21	2017	930	senate
t	5066	13	member	Banks	2017-02-13 16:36:21	2017	1099	senate
f	5068	15	member	Banks	2017-02-13 16:36:21	2017	946	senate
f	5070	17	member	Banks	2017-02-13 16:36:21	2017	929	senate
f	5072	19	member	Banks	2017-02-13 16:36:21	2017	925	senate
t	5075	3	member	Cities	2017-02-13 16:36:21	2017	920	senate
t	5077	5	member	Cities	2017-02-13 16:36:21	2017	1095	senate
f	5079	7	member	Cities	2017-02-13 16:36:21	2017	934	senate
f	5088	9	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	919	senate
t	5091	2	vice_chair	Codes	2017-02-13 16:36:21	2017	933	senate
t	5094	5	member	Codes	2017-02-13 16:36:21	2017	955	senate
t	5097	8	member	Codes	2017-02-13 16:36:21	2017	1017	senate
t	5099	10	member	Codes	2017-02-13 16:36:21	2017	930	senate
f	5103	14	member	Codes	2017-02-13 16:36:21	2017	918	senate
t	5106	1	chair_person	Consumer Protection	2017-02-13 16:36:21	2017	1072	senate
t	5110	5	member	Consumer Protection	2017-02-13 16:36:21	2017	937	senate
f	5115	10	member	Consumer Protection	2017-02-13 16:36:21	2017	922	senate
t	5119	3	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	1017	senate
t	5121	5	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	945	senate
t	5123	7	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	947	senate
f	5127	11	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	921	senate
t	5130	1	chair_person	Insurance	2017-02-13 16:36:21	2017	950	senate
t	5133	4	member	Insurance	2017-02-13 16:36:21	2017	952	senate
t	5135	6	member	Insurance	2017-02-13 16:36:21	2017	1084	senate
t	5138	9	member	Insurance	2017-02-13 16:36:21	2017	1014	senate
t	5141	12	member	Insurance	2017-02-13 16:36:21	2017	933	senate
f	5144	15	member	Insurance	2017-02-13 16:36:21	2017	1097	senate
f	5147	18	member	Insurance	2017-02-13 16:36:21	2017	925	senate
t	5149	1	chair_person	Labor	2017-02-13 16:36:21	2017	1090	senate
t	5151	3	member	Labor	2017-02-13 16:36:21	2017	956	senate
t	5153	5	member	Labor	2017-02-13 16:36:21	2017	1088	senate
t	5156	8	member	Labor	2017-02-13 16:36:21	2017	949	senate
f	5162	14	member	Labor	2017-02-13 16:36:21	2017	929	senate
t	5165	2	member	Local Government	2017-02-13 16:36:21	2017	944	senate
t	5167	4	member	Local Government	2017-02-13 16:36:21	2017	1014	senate
t	5169	6	member	Local Government	2017-02-13 16:36:21	2017	949	senate
f	5171	8	member	Local Government	2017-02-13 16:36:21	2017	922	senate
t	5073	1	chair_person	Cities	2017-02-13 16:36:21	2017	1013	senate
t	5080	1	chair_person	Civil Service and Pensions	2017-02-13 16:36:21	2017	938	senate
t	5082	3	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	1084	senate
t	5084	5	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	1096	senate
t	5086	7	member	Civil Service and Pensions	2017-02-13 16:36:21	2017	933	senate
t	5090	1	chair_person	Codes	2017-02-13 16:36:21	2017	1084	senate
t	5092	3	member	Codes	2017-02-13 16:36:21	2017	952	senate
t	5096	7	member	Codes	2017-02-13 16:36:21	2017	938	senate
t	5100	11	member	Codes	2017-02-13 16:36:21	2017	1099	senate
f	5102	13	member	Codes	2017-02-13 16:36:21	2017	921	senate
f	5104	15	member	Codes	2017-02-13 16:36:21	2017	935	senate
t	5107	2	member	Consumer Protection	2017-02-13 16:36:21	2017	954	senate
t	5109	4	member	Consumer Protection	2017-02-13 16:36:21	2017	953	senate
t	5112	7	member	Consumer Protection	2017-02-13 16:36:21	2017	1015	senate
f	5114	9	member	Consumer Protection	2017-02-13 16:36:21	2017	936	senate
t	5118	2	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	952	senate
t	5120	4	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	937	senate
t	5122	6	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	1095	senate
t	5124	8	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	949	senate
f	5126	10	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	1098	senate
f	5129	13	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	926	senate
t	5152	4	member	Labor	2017-02-13 16:36:21	2017	1011	senate
t	5158	10	member	Labor	2017-02-13 16:36:21	2017	933	senate
f	5160	12	member	Labor	2017-02-13 16:36:21	2017	919	senate
f	5163	15	member	Labor	2017-02-13 16:36:21	2017	926	senate
t	5172	1	chair_person	Civil Service and Pensions	2017-02-14 09:46:16	2017	938	senate
t	5176	5	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	1096	senate
t	5108	3	member	Consumer Protection	2017-02-13 16:36:21	2017	944	senate
t	5111	6	member	Consumer Protection	2017-02-13 16:36:21	2017	945	senate
f	5113	8	member	Consumer Protection	2017-02-13 16:36:21	2017	1097	senate
f	5116	11	member	Consumer Protection	2017-02-13 16:36:21	2017	934	senate
t	5117	1	chair_person	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	956	senate
t	5125	9	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	1099	senate
f	5128	12	member	Crime Victims, Crime and Correction	2017-02-13 16:36:21	2017	943	senate
t	5132	3	member	Insurance	2017-02-13 16:36:21	2017	937	senate
t	5136	7	member	Insurance	2017-02-13 16:36:21	2017	1012	senate
t	5140	11	member	Insurance	2017-02-13 16:36:21	2017	1096	senate
t	5142	13	member	Insurance	2017-02-13 16:36:21	2017	1018	senate
f	5146	17	member	Insurance	2017-02-13 16:36:21	2017	926	senate
f	5148	19	member	Insurance	2017-02-13 16:36:21	2017	932	senate
t	5197	6	member	Codes	2017-02-24 09:54:05	2017	956	senate
t	5202	11	member	Codes	2017-02-24 09:54:05	2017	1099	senate
f	5204	13	member	Codes	2017-02-24 09:54:05	2017	921	senate
f	5206	15	member	Codes	2017-02-24 09:54:05	2017	935	senate
t	5208	2	member	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	1090	senate
t	5210	4	member	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	1012	senate
t	5213	1	chair_person	Finance	2017-02-24 09:54:05	2017	942	senate
t	5216	4	member	Finance	2017-02-24 09:54:05	2017	1016	senate
t	5219	7	member	Finance	2017-02-24 09:54:05	2017	1013	senate
t	5222	10	member	Finance	2017-02-24 09:54:05	2017	1017	senate
t	5225	13	member	Finance	2017-02-24 09:54:05	2017	1012	senate
t	5228	16	member	Finance	2017-02-24 09:54:05	2017	1088	senate
t	5231	19	member	Finance	2017-02-24 09:54:05	2017	930	senate
t	5234	22	member	Finance	2017-02-24 09:54:05	2017	1015	senate
f	5237	25	member	Finance	2017-02-24 09:54:05	2017	935	senate
f	5240	28	member	Finance	2017-02-24 09:54:05	2017	1085	senate
f	5243	31	member	Finance	2017-02-24 09:54:05	2017	929	senate
f	5248	36	member	Finance	2017-02-24 09:54:05	2017	924	senate
t	5249	1	chair_person	Judiciary	2017-02-24 09:54:05	2017	1016	senate
t	5253	5	member	Judiciary	2017-02-24 09:54:05	2017	955	senate
t	5256	8	member	Judiciary	2017-02-24 09:54:05	2017	1095	senate
t	5259	11	member	Judiciary	2017-02-24 09:54:05	2017	941	senate
t	5262	14	member	Judiciary	2017-02-24 09:54:05	2017	933	senate
f	5265	17	member	Judiciary	2017-02-24 09:54:05	2017	1098	senate
f	5267	19	member	Judiciary	2017-02-24 09:54:05	2017	1085	senate
f	5270	22	member	Judiciary	2017-02-24 09:54:05	2017	924	senate
t	5272	2	vice_chair	Rules	2017-02-24 09:54:05	2017	939	senate
t	5275	5	member	Rules	2017-02-24 09:54:05	2017	1017	senate
t	5278	8	member	Rules	2017-02-24 09:54:05	2017	1012	senate
t	5280	10	member	Rules	2017-02-24 09:54:05	2017	947	senate
t	5283	13	member	Rules	2017-02-24 09:54:05	2017	1015	senate
t	5286	16	member	Rules	2017-02-24 09:54:05	2017	951	senate
f	5290	20	member	Rules	2017-02-24 09:54:05	2017	928	senate
f	5293	23	member	Rules	2017-02-24 09:54:05	2017	925	senate
t	5297	3	member	Transportation	2017-02-24 09:54:05	2017	920	senate
t	5299	5	member	Transportation	2017-02-24 09:54:05	2017	956	senate
t	5301	7	member	Transportation	2017-02-24 09:54:05	2017	1012	senate
t	5303	9	member	Transportation	2017-02-24 09:54:05	2017	930	senate
t	5306	12	member	Transportation	2017-02-24 09:54:05	2017	951	senate
f	5308	14	member	Transportation	2017-02-24 09:54:05	2017	927	senate
f	5310	16	member	Transportation	2017-02-24 09:54:05	2017	918	senate
f	5312	18	member	Transportation	2017-02-24 09:54:05	2017	923	senate
t	5174	3	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	1084	senate
t	5178	7	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	933	senate
f	5180	9	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	919	senate
f	5182	11	member	Civil Service and Pensions	2017-02-14 09:46:16	2017	932	senate
t	5184	2	member	Local Government	2017-02-14 09:46:16	2017	944	senate
t	5186	4	member	Local Government	2017-02-14 09:46:16	2017	1014	senate
f	5189	7	member	Local Government	2017-02-14 09:46:16	2017	1097	senate
t	5193	2	vice_chair	Codes	2017-02-24 09:54:05	2017	933	senate
t	5195	4	member	Codes	2017-02-24 09:54:05	2017	944	senate
t	5199	8	member	Codes	2017-02-24 09:54:05	2017	1017	senate
t	5200	9	member	Codes	2017-02-24 09:54:05	2017	941	senate
t	5201	10	member	Codes	2017-02-24 09:54:05	2017	930	senate
f	5203	12	member	Codes	2017-02-24 09:54:05	2017	923	senate
f	5205	14	member	Codes	2017-02-24 09:54:05	2017	918	senate
t	5207	1	chair_person	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	948	senate
t	5209	3	member	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	954	senate
t	5211	5	member	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	957	senate
f	5212	6	member	Corporations, Authorities and Commissions	2017-02-24 09:54:05	2017	923	senate
t	5214	2	vice_chair	Finance	2017-02-24 09:54:05	2017	933	senate
t	5215	3	member	Finance	2017-02-24 09:54:05	2017	920	senate
t	5217	5	member	Finance	2017-02-24 09:54:05	2017	944	senate
t	5218	6	member	Finance	2017-02-24 09:54:05	2017	1072	senate
t	5220	8	member	Finance	2017-02-24 09:54:05	2017	956	senate
t	5221	9	member	Finance	2017-02-24 09:54:05	2017	938	senate
t	5223	11	member	Finance	2017-02-24 09:54:05	2017	1011	senate
t	5224	12	member	Finance	2017-02-24 09:54:05	2017	1084	senate
t	5226	14	member	Finance	2017-02-24 09:54:05	2017	1010	senate
t	5227	15	member	Finance	2017-02-24 09:54:05	2017	947	senate
t	5229	17	member	Finance	2017-02-24 09:54:05	2017	940	senate
t	5230	18	member	Finance	2017-02-24 09:54:05	2017	941	senate
t	5232	20	member	Finance	2017-02-24 09:54:05	2017	948	senate
t	5233	21	member	Finance	2017-02-24 09:54:05	2017	949	senate
t	5235	23	member	Finance	2017-02-24 09:54:05	2017	950	senate
t	5236	24	member	Finance	2017-02-24 09:54:05	2017	951	senate
f	5238	26	member	Finance	2017-02-24 09:54:05	2017	946	senate
f	5239	27	member	Finance	2017-02-24 09:54:05	2017	936	senate
f	5241	29	member	Finance	2017-02-24 09:54:05	2017	927	senate
f	5242	30	member	Finance	2017-02-24 09:54:05	2017	918	senate
f	5244	32	member	Finance	2017-02-24 09:54:05	2017	943	senate
f	5245	33	member	Finance	2017-02-24 09:54:05	2017	925	senate
f	5246	34	member	Finance	2017-02-24 09:54:05	2017	926	senate
f	5247	35	member	Finance	2017-02-24 09:54:05	2017	923	senate
t	5251	3	member	Judiciary	2017-02-24 09:54:05	2017	920	senate
t	5254	6	member	Judiciary	2017-02-24 09:54:05	2017	937	senate
t	5257	9	member	Judiciary	2017-02-24 09:54:05	2017	1084	senate
t	5260	12	member	Judiciary	2017-02-24 09:54:05	2017	1096	senate
t	5263	15	member	Judiciary	2017-02-24 09:54:05	2017	1018	senate
f	5266	18	member	Judiciary	2017-02-24 09:54:05	2017	946	senate
f	5269	21	member	Judiciary	2017-02-24 09:54:05	2017	918	senate
t	5271	1	chair_person	Rules	2017-02-24 09:54:05	2017	1089	senate
t	5274	4	member	Rules	2017-02-24 09:54:05	2017	1072	senate
t	5277	7	member	Rules	2017-02-24 09:54:05	2017	1084	senate
t	5281	11	member	Rules	2017-02-24 09:54:05	2017	1088	senate
t	5284	14	member	Rules	2017-02-24 09:54:05	2017	933	senate
f	5287	17	member	Rules	2017-02-24 09:54:05	2017	1087	senate
f	5289	19	member	Rules	2017-02-24 09:54:05	2017	927	senate
f	5292	22	member	Rules	2017-02-24 09:54:05	2017	943	senate
t	5295	1	chair_person	Transportation	2017-02-24 09:54:05	2017	1015	senate
t	5298	4	member	Transportation	2017-02-24 09:54:05	2017	955	senate
t	5302	8	member	Transportation	2017-02-24 09:54:05	2017	1088	senate
t	5305	11	member	Transportation	2017-02-24 09:54:05	2017	949	senate
t	5317	4	member	Codes	2017-03-02 12:36:47	2017	944	senate
t	5320	7	member	Codes	2017-03-02 12:36:47	2017	938	senate
t	5322	9	member	Codes	2017-03-02 12:36:47	2017	941	senate
t	5324	11	member	Codes	2017-03-02 12:36:47	2017	1099	senate
f	5326	13	member	Codes	2017-03-02 12:36:47	2017	921	senate
f	5328	15	member	Codes	2017-03-02 12:36:47	2017	935	senate
t	5330	1	chair_person	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	948	senate
t	5331	2	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	1090	senate
t	5332	3	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	954	senate
t	5334	5	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	957	senate
f	5336	7	member	Corporations, Authorities and Commissions	2017-03-02 12:36:47	2017	936	senate
t	5337	1	chair_person	Finance	2017-03-02 12:36:47	2017	942	senate
t	5339	3	member	Finance	2017-03-02 12:36:47	2017	920	senate
t	5341	5	member	Finance	2017-03-02 12:36:47	2017	944	senate
t	5342	6	member	Finance	2017-03-02 12:36:47	2017	1072	senate
t	5343	7	member	Finance	2017-03-02 12:36:47	2017	1013	senate
t	5345	9	member	Finance	2017-03-02 12:36:47	2017	938	senate
t	5346	10	member	Finance	2017-03-02 12:36:47	2017	1017	senate
t	5348	12	member	Finance	2017-03-02 12:36:47	2017	1084	senate
t	5350	14	member	Finance	2017-03-02 12:36:47	2017	1010	senate
t	5352	16	member	Finance	2017-03-02 12:36:47	2017	1088	senate
t	5353	17	member	Finance	2017-03-02 12:36:47	2017	940	senate
t	5355	19	member	Finance	2017-03-02 12:36:47	2017	930	senate
t	5356	20	member	Finance	2017-03-02 12:36:47	2017	948	senate
t	5358	22	member	Finance	2017-03-02 12:36:47	2017	1015	senate
t	5359	23	member	Finance	2017-03-02 12:36:47	2017	950	senate
t	5360	24	member	Finance	2017-03-02 12:36:47	2017	951	senate
f	5362	26	member	Finance	2017-03-02 12:36:47	2017	1098	senate
f	5363	27	member	Finance	2017-03-02 12:36:47	2017	946	senate
f	5365	29	member	Finance	2017-03-02 12:36:47	2017	1085	senate
f	5366	30	member	Finance	2017-03-02 12:36:47	2017	927	senate
f	5368	32	member	Finance	2017-03-02 12:36:47	2017	929	senate
f	5369	33	member	Finance	2017-03-02 12:36:47	2017	943	senate
f	5371	35	member	Finance	2017-03-02 12:36:47	2017	926	senate
f	5372	36	member	Finance	2017-03-02 12:36:47	2017	923	senate
t	5374	1	chair_person	Judiciary	2017-03-02 12:36:47	2017	1016	senate
t	5376	3	member	Judiciary	2017-03-02 12:36:47	2017	920	senate
t	5377	4	member	Judiciary	2017-03-02 12:36:47	2017	944	senate
t	5379	6	member	Judiciary	2017-03-02 12:36:47	2017	937	senate
t	5383	10	member	Judiciary	2017-03-02 12:36:47	2017	1010	senate
t	5385	12	member	Judiciary	2017-03-02 12:36:47	2017	1096	senate
f	5389	16	member	Judiciary	2017-03-02 12:36:47	2017	921	senate
f	5392	19	member	Judiciary	2017-03-02 12:36:47	2017	1085	senate
t	5397	1	chair_person	Rules	2017-03-02 12:36:47	2017	1089	senate
t	5400	4	member	Rules	2017-03-02 12:36:47	2017	1072	senate
t	5402	6	member	Rules	2017-03-02 12:36:47	2017	1011	senate
t	5406	10	member	Rules	2017-03-02 12:36:47	2017	947	senate
t	5408	12	member	Rules	2017-03-02 12:36:47	2017	948	senate
t	5410	14	member	Rules	2017-03-02 12:36:47	2017	933	senate
f	5413	17	member	Rules	2017-03-02 12:36:47	2017	1087	senate
f	5416	20	member	Rules	2017-03-02 12:36:47	2017	928	senate
f	5421	25	member	Rules	2017-03-02 12:36:47	2017	923	senate
t	5380	7	member	Judiciary	2017-03-02 12:36:47	2017	1011	senate
t	5382	9	member	Judiciary	2017-03-02 12:36:47	2017	1084	senate
t	5386	13	member	Judiciary	2017-03-02 12:36:47	2017	948	senate
t	5388	15	member	Judiciary	2017-03-02 12:36:47	2017	1018	senate
f	5390	17	member	Judiciary	2017-03-02 12:36:47	2017	1098	senate
f	5393	20	member	Judiciary	2017-03-02 12:36:47	2017	927	senate
f	5395	22	member	Judiciary	2017-03-02 12:36:47	2017	943	senate
t	4523	1	chair_person	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	1096	senate
t	4524	2	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	1072	senate
t	4525	3	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	955	senate
t	4526	4	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	1084	senate
t	4527	5	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	933	senate
t	4528	6	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	951	senate
f	4529	7	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	928	senate
f	4530	8	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	926	senate
f	4531	9	member	Ethics and Internal Governance Committee	2017-05-18 10:12:14	2017	1086	senate
t	4532	1	chair_person	Ethics and Internal Governance	2017-05-18 10:57:17	2017	1096	senate
t	4533	2	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	1072	senate
t	4534	3	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	955	senate
t	4535	4	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	1084	senate
t	4536	5	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	933	senate
t	4537	6	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	951	senate
f	4538	7	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	928	senate
f	4539	8	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	926	senate
f	4540	9	member	Ethics and Internal Governance	2017-05-18 10:57:17	2017	1086	senate
t	4564	1	chair_person	Banks	2014-03-03 17:09:09	2013	149	senate
t	4565	2	vice_chair	Banks	2014-03-03 17:09:09	2013	151	senate
t	4566	3	member	Banks	2014-03-03 17:09:09	2013	169	senate
t	4567	4	member	Banks	2014-03-03 17:09:09	2013	132	senate
t	4568	5	member	Banks	2014-03-03 17:09:09	2013	178	senate
t	4569	6	member	Banks	2014-03-03 17:09:09	2013	144	senate
t	4570	7	member	Banks	2014-03-03 17:09:09	2013	153	senate
t	4571	8	member	Banks	2014-03-03 17:09:09	2013	163	senate
t	4572	9	member	Banks	2014-03-03 17:09:09	2013	183	senate
t	4573	10	member	Banks	2014-03-03 17:09:09	2013	162	senate
t	4574	11	member	Banks	2014-03-03 17:09:09	2013	171	senate
t	4575	12	member	Banks	2014-03-03 17:09:09	2013	147	senate
f	4576	13	member	Banks	2014-03-03 17:09:09	2013	148	senate
f	4577	14	member	Banks	2014-03-03 17:09:09	2013	174	senate
f	4578	15	member	Banks	2014-03-03 17:09:09	2013	177	senate
f	4579	16	member	Banks	2014-03-03 17:09:09	2013	172	senate
f	4580	17	member	Banks	2014-03-03 17:09:09	2013	134	senate
f	4581	18	member	Banks	2014-03-03 17:09:09	2013	158	senate
t	4582	1	chair_person	Civil Service and Pensions	2014-03-03 17:09:09	2013	178	senate
t	4583	2	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	180	senate
t	4584	3	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	165	senate
t	4585	4	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	153	senate
t	4586	5	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	179	senate
t	4587	6	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	159	senate
t	4588	7	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	167	senate
f	4589	8	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	134	senate
f	4590	9	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	139	senate
f	4591	10	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	137	senate
f	4592	11	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	131	senate
t	4593	1	chair_person	Codes	2014-03-03 17:09:09	2013	150	senate
t	4594	2	member	Codes	2014-03-03 17:09:09	2013	141	senate
t	4595	3	member	Codes	2014-03-03 17:09:09	2013	132	senate
t	4596	4	member	Codes	2014-03-03 17:09:09	2013	155	senate
t	4597	5	member	Codes	2014-03-03 17:09:09	2013	182	senate
t	4598	6	member	Codes	2014-03-03 17:09:09	2013	178	senate
t	4599	7	member	Codes	2014-03-03 17:09:09	2013	165	senate
f	4603	11	member	Codes	2014-03-03 17:09:09	2013	157	senate
f	4605	13	member	Codes	2014-03-03 17:09:09	2013	140	senate
f	4607	15	member	Codes	2014-03-03 17:09:09	2013	148	senate
t	4609	1	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	171	senate
t	4611	3	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	182	senate
t	4615	7	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	135	senate
f	4618	10	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	134	senate
t	4622	3	member	Consumer Protection	2014-03-03 17:09:09	2013	135	senate
t	4632	2	member	Education	2014-03-03 17:09:09	2013	151	senate
t	4636	6	member	Education	2014-03-03 17:09:09	2013	163	senate
t	4639	9	member	Education	2014-03-03 17:09:09	2013	173	senate
t	4642	12	member	Education	2014-03-03 17:09:09	2013	147	senate
f	4645	15	member	Education	2014-03-03 17:09:09	2013	174	senate
f	4648	18	member	Education	2014-03-03 17:09:09	2013	128	senate
t	4650	2	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	126	senate
t	4652	4	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	183	senate
t	4654	6	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	173	senate
f	4656	8	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	172	senate
f	4659	11	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	187	senate
t	4661	2	member	Finance	2014-03-03 17:09:09	2013	169	senate
t	4665	6	member	Finance	2014-03-03 17:09:09	2013	149	senate
t	4668	9	member	Finance	2014-03-03 17:09:09	2013	165	senate
t	4671	12	member	Finance	2014-03-03 17:09:09	2013	135	senate
t	4674	15	member	Finance	2014-03-03 17:09:09	2013	183	senate
t	4677	18	member	Finance	2014-03-03 17:09:09	2013	159	senate
t	4681	22	member	Finance	2014-03-03 17:09:09	2013	153	senate
f	4685	26	member	Finance	2014-03-03 17:09:09	2013	133	senate
f	4691	32	member	Finance	2014-03-03 17:09:09	2013	131	senate
f	4695	36	member	Finance	2014-03-03 17:09:09	2013	140	senate
t	4698	2	member	Health	2014-03-03 17:09:09	2013	164	senate
t	4701	5	member	Health	2014-03-03 17:09:09	2013	178	senate
t	4703	7	member	Health	2014-03-03 17:09:09	2013	159	senate
t	4706	10	member	Health	2014-03-03 17:09:09	2013	153	senate
f	4710	14	member	Health	2014-03-03 17:09:09	2013	187	senate
f	4712	16	member	Health	2014-03-03 17:09:09	2013	176	senate
t	4724	2	member	Insurance	2014-03-03 17:09:09	2013	126	senate
t	4728	6	member	Insurance	2014-03-03 17:09:09	2013	165	senate
t	4732	10	member	Insurance	2014-03-03 17:09:09	2013	183	senate
t	4734	12	member	Insurance	2014-03-03 17:09:09	2013	147	senate
f	4736	14	member	Insurance	2014-03-03 17:09:09	2013	140	senate
f	4740	18	member	Insurance	2014-03-03 17:09:09	2013	148	senate
t	4743	2	member	Judiciary	2014-03-03 17:09:09	2013	132	senate
t	4745	4	member	Judiciary	2014-03-03 17:09:09	2013	180	senate
t	4747	6	member	Judiciary	2014-03-03 17:09:09	2013	175	senate
t	4749	8	member	Judiciary	2014-03-03 17:09:09	2013	150	senate
t	4751	10	member	Judiciary	2014-03-03 17:09:09	2013	159	senate
t	4753	12	member	Judiciary	2014-03-03 17:09:09	2013	160	senate
t	4755	14	member	Judiciary	2014-03-03 17:09:09	2013	147	senate
f	4757	16	member	Judiciary	2014-03-03 17:09:09	2013	174	senate
f	4759	18	member	Judiciary	2014-03-03 17:09:09	2013	140	senate
f	4761	20	member	Judiciary	2014-03-03 17:09:09	2013	156	senate
f	4763	22	member	Judiciary	2014-03-03 17:09:09	2013	139	senate
t	4791	2	vice_chair	Transportation	2014-03-03 17:09:09	2013	163	senate
t	4795	6	member	Transportation	2014-03-03 17:09:09	2013	167	senate
t	4797	8	member	Transportation	2014-03-03 17:09:09	2013	183	senate
t	4800	11	member	Transportation	2014-03-03 17:09:09	2013	160	senate
f	4803	14	member	Transportation	2014-03-03 17:09:09	2013	177	senate
f	4806	17	member	Transportation	2014-03-03 17:09:09	2013	157	senate
t	4809	1	chair_person	New York City Education Subcommittee	2014-03-03 17:09:09	2013	181	senate
f	4813	5	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	131	senate
t	5422	1	chair_person	Civil Service and Pensions	2017-06-06 18:26:24	2017	938	senate
t	5426	5	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	1096	senate
t	5428	7	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	933	senate
f	5432	11	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	1086	senate
t	5434	1	chair_person	Civil Service and Pensions	2017-06-06 20:46:33	2017	938	senate
t	5438	5	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	1096	senate
t	5440	7	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	933	senate
f	5444	11	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	932	senate
t	5445	1	chair_person	Codes	2017-08-25 14:31:55	2017	1084	senate
t	5449	5	member	Codes	2017-08-25 14:31:55	2017	955	senate
t	5451	7	member	Codes	2017-08-25 14:31:55	2017	938	senate
t	5454	10	member	Codes	2017-08-25 14:31:55	2017	930	senate
f	5457	13	member	Codes	2017-08-25 14:31:55	2017	918	senate
f	5459	15	member	Codes	2017-08-25 14:31:55	2017	934	senate
t	5462	3	member	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	954	senate
f	5465	6	member	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	936	senate
t	5467	2	vice_chair	Finance	2017-08-25 14:31:55	2017	933	senate
t	5469	4	member	Finance	2017-08-25 14:31:55	2017	1016	senate
t	5474	9	member	Finance	2017-08-25 14:31:55	2017	938	senate
t	5478	13	member	Finance	2017-08-25 14:31:55	2017	1012	senate
t	5480	15	member	Finance	2017-08-25 14:31:55	2017	947	senate
t	5482	17	member	Finance	2017-08-25 14:31:55	2017	940	senate
t	5486	21	member	Finance	2017-08-25 14:31:55	2017	949	senate
t	5488	23	member	Finance	2017-08-25 14:31:55	2017	950	senate
f	5491	26	member	Finance	2017-08-25 14:31:55	2017	1098	senate
f	5494	29	member	Finance	2017-08-25 14:31:55	2017	1085	senate
f	5497	32	member	Finance	2017-08-25 14:31:55	2017	929	senate
f	5500	35	member	Finance	2017-08-25 14:31:55	2017	926	senate
t	5502	1	chair_person	Investigations and Government Operations	2017-08-25 14:31:55	2017	1014	senate
t	5506	5	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	941	senate
t	5511	2	vice_chair	Rules	2017-08-25 14:31:55	2017	939	senate
t	5513	4	member	Rules	2017-08-25 14:31:55	2017	1072	senate
t	5515	6	member	Rules	2017-08-25 14:31:55	2017	1011	senate
t	4600	8	member	Codes	2014-03-03 17:09:09	2013	183	senate
t	4602	10	member	Codes	2014-03-03 17:09:09	2013	147	senate
f	4608	16	member	Codes	2014-03-03 17:09:09	2013	129	senate
t	4612	4	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	181	senate
t	4614	6	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	173	senate
f	4617	9	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	136	senate
f	4619	11	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	148	senate
t	4621	2	member	Consumer Protection	2014-03-03 17:09:09	2013	141	senate
t	4624	5	member	Consumer Protection	2014-03-03 17:09:09	2013	167	senate
f	4627	8	member	Consumer Protection	2014-03-03 17:09:09	2013	176	senate
f	4630	11	member	Consumer Protection	2014-03-03 17:09:09	2013	143	senate
t	4634	4	member	Education	2014-03-03 17:09:09	2013	175	senate
t	4637	7	member	Education	2014-03-03 17:09:09	2013	167	senate
t	4641	11	member	Education	2014-03-03 17:09:09	2013	171	senate
f	4644	14	member	Education	2014-03-03 17:09:09	2013	139	senate
f	4647	17	member	Education	2014-03-03 17:09:09	2013	156	senate
t	4651	3	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	149	senate
t	4655	7	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	155	senate
f	4657	9	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	146	senate
t	4662	3	member	Finance	2014-03-03 17:09:09	2013	151	senate
t	4664	5	member	Finance	2014-03-03 17:09:09	2013	178	senate
t	4666	7	member	Finance	2014-03-03 17:09:09	2013	154	senate
t	4669	10	member	Finance	2014-03-03 17:09:09	2013	130	senate
t	4673	14	member	Finance	2014-03-03 17:09:09	2013	150	senate
t	4675	16	member	Finance	2014-03-03 17:09:09	2013	162	senate
t	4679	20	member	Finance	2014-03-03 17:09:09	2013	170	senate
f	4683	24	member	Finance	2014-03-03 17:09:09	2013	177	senate
f	4687	28	member	Finance	2014-03-03 17:09:09	2013	174	senate
f	4689	30	member	Finance	2014-03-03 17:09:09	2013	172	senate
f	4693	34	member	Finance	2014-03-03 17:09:09	2013	157	senate
f	4696	37	member	Finance	2014-03-03 17:09:09	2013	136	senate
t	4715	2	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	169	senate
t	4717	4	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	182	senate
t	4719	6	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	147	senate
f	4722	9	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	129	senate
t	4744	3	member	Judiciary	2014-03-03 17:09:09	2013	155	senate
t	4746	5	member	Judiciary	2014-03-03 17:09:09	2013	165	senate
t	4748	7	member	Judiciary	2014-03-03 17:09:09	2013	135	senate
t	4750	9	member	Judiciary	2014-03-03 17:09:09	2013	183	senate
t	4754	13	member	Judiciary	2014-03-03 17:09:09	2013	154	senate
f	4756	15	member	Judiciary	2014-03-03 17:09:09	2013	136	senate
f	4758	17	member	Judiciary	2014-03-03 17:09:09	2013	137	senate
f	4760	19	member	Judiciary	2014-03-03 17:09:09	2013	131	senate
f	4762	21	member	Judiciary	2014-03-03 17:09:09	2013	176	senate
f	4764	23	member	Judiciary	2014-03-03 17:09:09	2013	157	senate
t	4767	3	member	Rules	2014-03-03 17:09:09	2013	169	senate
t	4770	6	member	Rules	2014-03-03 17:09:09	2013	155	senate
t	4773	9	member	Rules	2014-03-03 17:09:09	2013	175	senate
t	4776	12	member	Rules	2014-03-03 17:09:09	2013	150	senate
t	4779	15	member	Rules	2014-03-03 17:09:09	2013	135	senate
f	4782	18	member	Rules	2014-03-03 17:09:09	2013	137	senate
f	4785	21	member	Rules	2014-03-03 17:09:09	2013	168	senate
f	4789	25	member	Rules	2014-03-03 17:09:09	2013	138	senate
t	4790	1	chair_person	Transportation	2014-03-03 17:09:09	2013	173	senate
t	4793	4	member	Transportation	2014-03-03 17:09:09	2013	182	senate
t	4796	7	member	Transportation	2014-03-03 17:09:09	2013	150	senate
t	4799	10	member	Transportation	2014-03-03 17:09:09	2013	170	senate
f	4802	13	member	Transportation	2014-03-03 17:09:09	2013	137	senate
f	4805	16	member	Transportation	2014-03-03 17:09:09	2013	131	senate
f	4808	19	member	Transportation	2014-03-03 17:09:09	2013	158	senate
t	4811	3	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	178	senate
f	4814	6	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	156	senate
t	5423	2	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	955	senate
t	5425	4	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	957	senate
t	5427	6	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	949	senate
f	5429	8	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	1133	senate
f	5431	10	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	919	senate
t	5435	2	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	955	senate
t	5437	4	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	957	senate
t	5446	2	vice_chair	Codes	2017-08-25 14:31:55	2017	933	senate
t	5448	4	member	Codes	2017-08-25 14:31:55	2017	944	senate
t	5452	8	member	Codes	2017-08-25 14:31:55	2017	1017	senate
f	5456	12	member	Codes	2017-08-25 14:31:55	2017	921	senate
t	5461	2	member	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	1090	senate
t	5464	5	member	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	957	senate
t	5470	5	member	Finance	2017-08-25 14:31:55	2017	944	senate
t	5472	7	member	Finance	2017-08-25 14:31:55	2017	1013	senate
t	5476	11	member	Finance	2017-08-25 14:31:55	2017	1011	senate
t	5481	16	member	Finance	2017-08-25 14:31:55	2017	1088	senate
t	5483	18	member	Finance	2017-08-25 14:31:55	2017	941	senate
t	5485	20	member	Finance	2017-08-25 14:31:55	2017	948	senate
t	5489	24	member	Finance	2017-08-25 14:31:55	2017	951	senate
f	5493	28	member	Finance	2017-08-25 14:31:55	2017	936	senate
f	5495	30	member	Finance	2017-08-25 14:31:55	2017	927	senate
f	5499	34	member	Finance	2017-08-25 14:31:55	2017	925	senate
t	5504	3	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	938	senate
f	5508	7	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	921	senate
t	5510	1	chair_person	Rules	2017-08-25 14:31:55	2017	1089	senate
t	5514	5	member	Rules	2017-08-25 14:31:55	2017	1017	senate
t	5518	9	member	Rules	2017-08-25 14:31:55	2017	1010	senate
t	5522	13	member	Rules	2017-08-25 14:31:55	2017	1015	senate
t	5524	15	member	Rules	2017-08-25 14:31:55	2017	950	senate
f	5526	17	member	Rules	2017-08-25 14:31:55	2017	1087	senate
t	4601	9	member	Codes	2014-03-03 17:09:09	2013	149	senate
f	4604	12	member	Codes	2014-03-03 17:09:09	2013	131	senate
f	4606	14	member	Codes	2014-03-03 17:09:09	2013	176	senate
t	4610	2	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	141	senate
t	4613	5	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	149	senate
f	4616	8	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	146	senate
t	4620	1	chair_person	Consumer Protection	2014-03-03 17:09:09	2013	160	senate
t	4626	7	member	Consumer Protection	2014-03-03 17:09:09	2013	173	senate
f	4629	10	member	Consumer Protection	2014-03-03 17:09:09	2013	158	senate
t	4631	1	chair_person	Education	2014-03-03 17:09:09	2013	155	senate
t	4633	3	member	Education	2014-03-03 17:09:09	2013	165	senate
t	4635	5	member	Education	2014-03-03 17:09:09	2013	135	senate
t	4638	8	member	Education	2014-03-03 17:09:09	2013	162	senate
t	4640	10	member	Education	2014-03-03 17:09:09	2013	127	senate
f	4643	13	member	Education	2014-03-03 17:09:09	2013	143	senate
f	4646	16	member	Education	2014-03-03 17:09:09	2013	168	senate
t	4649	1	chair_person	Energy and Telecommunications	2014-03-03 17:09:09	2013	167	senate
t	4653	5	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	179	senate
f	4658	10	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	137	senate
t	4699	3	member	Health	2014-03-03 17:09:09	2013	151	senate
t	4705	9	member	Health	2014-03-03 17:09:09	2013	170	senate
f	4708	12	member	Health	2014-03-03 17:09:09	2013	168	senate
f	4711	15	member	Health	2014-03-03 17:09:09	2013	148	senate
t	4714	1	chair_person	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	170	senate
t	4716	3	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	141	senate
f	4720	7	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	140	senate
t	4726	4	member	Insurance	2014-03-03 17:09:09	2013	178	senate
t	4730	8	member	Insurance	2014-03-03 17:09:09	2013	175	senate
t	4733	11	member	Insurance	2014-03-03 17:09:09	2013	170	senate
f	4738	16	member	Insurance	2014-03-03 17:09:09	2013	172	senate
t	4766	2	vice_chair	Rules	2014-03-03 17:09:09	2013	166	senate
t	4769	5	member	Rules	2014-03-03 17:09:09	2013	151	senate
t	4772	8	member	Rules	2014-03-03 17:09:09	2013	130	senate
t	4775	11	member	Rules	2014-03-03 17:09:09	2013	167	senate
t	4778	14	member	Rules	2014-03-03 17:09:09	2013	171	senate
f	4781	17	member	Rules	2014-03-03 17:09:09	2013	174	senate
f	4784	20	member	Rules	2014-03-03 17:09:09	2013	129	senate
f	4786	22	member	Rules	2014-03-03 17:09:09	2013	172	senate
f	4788	24	member	Rules	2014-03-03 17:09:09	2013	140	senate
t	4810	2	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	165	senate
t	4812	4	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	147	senate
t	5424	3	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	1084	senate
f	5430	9	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	934	senate
f	5433	12	member	Civil Service and Pensions	2017-06-06 18:26:24	2017	932	senate
t	5436	3	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	1084	senate
f	5441	8	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	1133	senate
f	5443	10	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	1086	senate
t	5447	3	member	Codes	2017-08-25 14:31:55	2017	952	senate
t	5450	6	member	Codes	2017-08-25 14:31:55	2017	956	senate
t	5453	9	member	Codes	2017-08-25 14:31:55	2017	941	senate
t	5471	6	member	Finance	2017-08-25 14:31:55	2017	1072	senate
t	5479	14	member	Finance	2017-08-25 14:31:55	2017	1010	senate
t	5484	19	member	Finance	2017-08-25 14:31:55	2017	930	senate
t	5487	22	member	Finance	2017-08-25 14:31:55	2017	1015	senate
f	5490	25	member	Finance	2017-08-25 14:31:55	2017	935	senate
f	5492	27	member	Finance	2017-08-25 14:31:55	2017	946	senate
f	5496	31	member	Finance	2017-08-25 14:31:55	2017	918	senate
f	5498	33	member	Finance	2017-08-25 14:31:55	2017	943	senate
f	5501	36	member	Finance	2017-08-25 14:31:55	2017	924	senate
f	5509	8	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	1085	senate
t	5512	3	member	Rules	2017-08-25 14:31:55	2017	1016	senate
t	5516	7	member	Rules	2017-08-25 14:31:55	2017	1084	senate
t	5520	11	member	Rules	2017-08-25 14:31:55	2017	1088	senate
t	5523	14	member	Rules	2017-08-25 14:31:55	2017	933	senate
t	5525	16	member	Rules	2017-08-25 14:31:55	2017	951	senate
f	5527	18	member	Rules	2017-08-25 14:31:55	2017	946	senate
f	5529	20	member	Rules	2017-08-25 14:31:55	2017	928	senate
f	5531	22	member	Rules	2017-08-25 14:31:55	2017	935	senate
f	5533	24	member	Rules	2017-08-25 14:31:55	2017	925	senate
t	5535	2	member	Social Services	2017-08-25 14:31:55	2017	1090	senate
f	5553	14	member	Transportation	2017-08-25 14:31:55	2017	927	senate
f	5555	16	member	Transportation	2017-08-25 14:31:55	2017	918	senate
t	4623	4	member	Consumer Protection	2014-03-03 17:09:09	2013	144	senate
t	4625	6	member	Consumer Protection	2014-03-03 17:09:09	2013	159	senate
f	4628	9	member	Consumer Protection	2014-03-03 17:09:09	2013	142	senate
t	4660	1	chair_person	Finance	2014-03-03 17:09:09	2013	132	senate
t	4663	4	member	Finance	2014-03-03 17:09:09	2013	155	senate
t	4667	8	member	Finance	2014-03-03 17:09:09	2013	180	senate
t	4670	11	member	Finance	2014-03-03 17:09:09	2013	175	senate
t	4672	13	member	Finance	2014-03-03 17:09:09	2013	163	senate
t	4676	17	member	Finance	2014-03-03 17:09:09	2013	173	senate
t	4678	19	member	Finance	2014-03-03 17:09:09	2013	127	senate
t	4680	21	member	Finance	2014-03-03 17:09:09	2013	171	senate
f	4682	23	member	Finance	2014-03-03 17:09:09	2013	129	senate
f	4684	25	member	Finance	2014-03-03 17:09:09	2013	137	senate
f	4686	27	member	Finance	2014-03-03 17:09:09	2013	138	senate
f	4688	29	member	Finance	2014-03-03 17:09:09	2013	168	senate
f	4690	31	member	Finance	2014-03-03 17:09:09	2013	187	senate
f	4692	33	member	Finance	2014-03-03 17:09:09	2013	156	senate
f	4694	35	member	Finance	2014-03-03 17:09:09	2013	146	senate
t	4697	1	chair_person	Health	2014-03-03 17:09:09	2013	180	senate
t	4700	4	member	Health	2014-03-03 17:09:09	2013	181	senate
t	4702	6	member	Health	2014-03-03 17:09:09	2013	130	senate
t	4704	8	member	Health	2014-03-03 17:09:09	2013	127	senate
f	4707	11	member	Health	2014-03-03 17:09:09	2013	133	senate
f	4709	13	member	Health	2014-03-03 17:09:09	2013	136	senate
f	4713	17	member	Health	2014-03-03 17:09:09	2013	142	senate
t	4718	5	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	150	senate
f	4721	8	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	177	senate
t	4723	1	chair_person	Insurance	2014-03-03 17:09:09	2013	127	senate
t	4725	3	member	Insurance	2014-03-03 17:09:09	2013	155	senate
t	4727	5	member	Insurance	2014-03-03 17:09:09	2013	154	senate
t	4729	7	member	Insurance	2014-03-03 17:09:09	2013	130	senate
t	4731	9	member	Insurance	2014-03-03 17:09:09	2013	153	senate
f	4735	13	member	Insurance	2014-03-03 17:09:09	2013	174	senate
f	4737	15	member	Insurance	2014-03-03 17:09:09	2013	146	senate
f	4739	17	member	Insurance	2014-03-03 17:09:09	2013	156	senate
f	4741	19	member	Insurance	2014-03-03 17:09:09	2013	187	senate
t	4742	1	chair_person	Judiciary	2014-03-03 17:09:09	2013	169	senate
t	4752	11	member	Judiciary	2014-03-03 17:09:09	2013	162	senate
t	4765	1	chair_person	Rules	2014-03-03 17:09:09	2013	184	senate
t	4768	4	member	Rules	2014-03-03 17:09:09	2013	126	senate
t	4771	7	member	Rules	2014-03-03 17:09:09	2013	180	senate
t	4774	10	member	Rules	2014-03-03 17:09:09	2013	163	senate
t	4777	13	member	Rules	2014-03-03 17:09:09	2013	127	senate
f	4780	16	member	Rules	2014-03-03 17:09:09	2013	152	senate
f	4783	19	member	Rules	2014-03-03 17:09:09	2013	136	senate
f	4787	23	member	Rules	2014-03-03 17:09:09	2013	131	senate
t	4792	3	member	Transportation	2014-03-03 17:09:09	2013	126	senate
t	4794	5	member	Transportation	2014-03-03 17:09:09	2013	130	senate
t	4798	9	member	Transportation	2014-03-03 17:09:09	2013	162	senate
t	4801	12	member	Transportation	2014-03-03 17:09:09	2013	147	senate
f	4804	15	member	Transportation	2014-03-03 17:09:09	2013	146	senate
f	4807	18	member	Transportation	2014-03-03 17:09:09	2013	156	senate
t	4815	1	chair_person	Aging	2014-03-13 14:48:16	2013	171	senate
t	4816	2	member	Aging	2014-03-13 14:48:16	2013	178	senate
t	4817	3	member	Aging	2014-03-13 14:48:16	2013	181	senate
t	4818	4	member	Aging	2014-03-13 14:48:16	2013	175	senate
t	4819	5	member	Aging	2014-03-13 14:48:16	2013	144	senate
t	4820	6	member	Aging	2014-03-13 14:48:16	2013	160	senate
f	4821	7	member	Aging	2014-03-13 14:48:16	2013	177	senate
f	4822	8	member	Aging	2014-03-13 14:48:16	2013	139	senate
f	4823	9	member	Aging	2014-03-13 14:48:16	2013	156	senate
f	4824	10	member	Aging	2014-03-13 14:48:16	2013	134	senate
t	4825	1	chair_person	Banks	2014-03-13 14:48:16	2013	149	senate
t	4826	2	vice_chair	Banks	2014-03-13 14:48:16	2013	151	senate
t	4827	3	member	Banks	2014-03-13 14:48:16	2013	169	senate
t	4828	4	member	Banks	2014-03-13 14:48:16	2013	132	senate
t	4829	5	member	Banks	2014-03-13 14:48:16	2013	178	senate
t	4830	6	member	Banks	2014-03-13 14:48:16	2013	144	senate
t	4831	7	member	Banks	2014-03-13 14:48:16	2013	153	senate
t	4832	8	member	Banks	2014-03-13 14:48:16	2013	163	senate
t	4833	9	member	Banks	2014-03-13 14:48:16	2013	183	senate
t	4834	10	member	Banks	2014-03-13 14:48:16	2013	162	senate
t	4835	11	member	Banks	2014-03-13 14:48:16	2013	171	senate
t	4836	12	member	Banks	2014-03-13 14:48:16	2013	147	senate
f	4837	13	member	Banks	2014-03-13 14:48:16	2013	148	senate
f	4838	14	member	Banks	2014-03-13 14:48:16	2013	174	senate
f	4839	15	member	Banks	2014-03-13 14:48:16	2013	177	senate
f	4840	16	member	Banks	2014-03-13 14:48:16	2013	172	senate
f	4841	17	member	Banks	2014-03-13 14:48:16	2013	134	senate
f	4842	18	member	Banks	2014-03-13 14:48:16	2013	158	senate
f	4843	19	member	Banks	2014-03-13 14:48:16	2013	146	senate
t	4844	1	chair_person	Cities	2014-03-13 14:48:16	2013	165	senate
t	4845	2	member	Cities	2014-03-13 14:48:16	2013	164	senate
t	4846	3	member	Cities	2014-03-13 14:48:16	2013	132	senate
t	4847	4	member	Cities	2014-03-13 14:48:16	2013	154	senate
f	4848	5	member	Cities	2014-03-13 14:48:16	2013	157	senate
f	4849	6	member	Cities	2014-03-13 14:48:16	2013	174	senate
t	4850	1	chair_person	Education	2014-03-13 14:48:16	2013	155	senate
t	4851	2	member	Education	2014-03-13 14:48:16	2013	151	senate
t	4852	3	member	Education	2014-03-13 14:48:16	2013	165	senate
t	4853	4	member	Education	2014-03-13 14:48:16	2013	175	senate
t	4854	5	member	Education	2014-03-13 14:48:16	2013	135	senate
t	4855	6	member	Education	2014-03-13 14:48:16	2013	163	senate
t	4856	7	member	Education	2014-03-13 14:48:16	2013	167	senate
t	4857	8	member	Education	2014-03-13 14:48:16	2013	162	senate
t	4858	9	member	Education	2014-03-13 14:48:16	2013	173	senate
t	4859	10	member	Education	2014-03-13 14:48:16	2013	127	senate
t	4860	11	member	Education	2014-03-13 14:48:16	2013	171	senate
t	4861	12	member	Education	2014-03-13 14:48:16	2013	147	senate
f	4862	13	member	Education	2014-03-13 14:48:16	2013	143	senate
f	4863	14	member	Education	2014-03-13 14:48:16	2013	139	senate
f	4864	15	member	Education	2014-03-13 14:48:16	2013	174	senate
f	4865	16	member	Education	2014-03-13 14:48:16	2013	168	senate
f	4868	19	member	Education	2014-03-13 14:48:16	2013	187	senate
t	4871	3	member	Environmental Conservation	2014-03-13 14:48:16	2013	175	senate
t	4873	5	member	Environmental Conservation	2014-03-13 14:48:16	2013	163	senate
t	4875	7	member	Environmental Conservation	2014-03-13 14:48:16	2013	183	senate
f	4877	9	member	Environmental Conservation	2014-03-13 14:48:16	2013	148	senate
f	4879	11	member	Environmental Conservation	2014-03-13 14:48:16	2013	142	senate
t	4886	5	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	144	senate
f	4889	8	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	139	senate
f	4892	11	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	128	senate
t	4895	3	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	155	senate
t	4898	6	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	154	senate
f	4901	9	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	139	senate
f	4904	12	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	128	senate
t	5439	6	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	949	senate
f	5442	9	member	Civil Service and Pensions	2017-06-06 20:46:33	2017	919	senate
t	5455	11	member	Codes	2017-08-25 14:31:55	2017	1099	senate
f	5458	14	member	Codes	2017-08-25 14:31:55	2017	935	senate
t	5460	1	chair_person	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	948	senate
t	5463	4	member	Corporations, Authorities and Commissions	2017-08-25 14:31:55	2017	1012	senate
t	5466	1	chair_person	Finance	2017-08-25 14:31:55	2017	942	senate
t	5468	3	member	Finance	2017-08-25 14:31:55	2017	920	senate
t	5473	8	member	Finance	2017-08-25 14:31:55	2017	956	senate
t	5475	10	member	Finance	2017-08-25 14:31:55	2017	1017	senate
t	5477	12	member	Finance	2017-08-25 14:31:55	2017	1084	senate
t	5503	2	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	1072	senate
t	5505	4	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	1084	senate
t	5507	6	member	Investigations and Government Operations	2017-08-25 14:31:55	2017	1096	senate
t	5538	5	member	Social Services	2017-08-25 14:31:55	2017	1010	senate
t	5541	2	vice_chair	Transportation	2017-08-25 14:31:55	2017	941	senate
t	5544	5	member	Transportation	2017-08-25 14:31:55	2017	956	senate
t	5547	8	member	Transportation	2017-08-25 14:31:55	2017	1088	senate
t	5550	11	member	Transportation	2017-08-25 14:31:55	2017	949	senate
f	5557	18	member	Transportation	2017-08-25 14:31:55	2017	924	senate
f	4866	17	member	Education	2014-03-13 14:48:16	2013	156	senate
t	4894	2	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	126	senate
t	4897	5	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	149	senate
t	4899	7	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	130	senate
f	4903	11	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	134	senate
t	4919	2	member	Banks	2017-01-30 16:39:21	2017	952	senate
t	4921	4	member	Banks	2017-01-30 16:39:21	2017	1016	senate
t	4923	6	member	Banks	2017-01-30 16:39:21	2017	1095	senate
t	4929	12	member	Banks	2017-01-30 16:39:21	2017	933	senate
f	4931	14	member	Banks	2017-01-30 16:39:21	2017	932	senate
f	4935	18	member	Banks	2017-01-30 16:39:21	2017	929	senate
t	4941	4	member	Cities	2017-01-30 16:39:21	2017	953	senate
t	5517	8	member	Rules	2017-08-25 14:31:55	2017	1012	senate
t	5519	10	member	Rules	2017-08-25 14:31:55	2017	947	senate
t	5521	12	member	Rules	2017-08-25 14:31:55	2017	948	senate
f	5528	19	member	Rules	2017-08-25 14:31:55	2017	927	senate
f	5530	21	member	Rules	2017-08-25 14:31:55	2017	929	senate
t	5537	4	member	Social Services	2017-08-25 14:31:55	2017	945	senate
f	5539	6	member	Social Services	2017-08-25 14:31:55	2017	1086	senate
t	5540	1	chair_person	Transportation	2017-08-25 14:31:55	2017	1015	senate
t	5543	4	member	Transportation	2017-08-25 14:31:55	2017	955	senate
t	5546	7	member	Transportation	2017-08-25 14:31:55	2017	1012	senate
t	5549	10	member	Transportation	2017-08-25 14:31:55	2017	948	senate
t	5552	13	member	Transportation	2017-08-25 14:31:55	2017	942	senate
f	5554	15	member	Transportation	2017-08-25 14:31:55	2017	1085	senate
f	5556	17	member	Transportation	2017-08-25 14:31:55	2017	929	senate
f	4867	18	member	Education	2014-03-13 14:48:16	2013	128	senate
t	4869	1	chair_person	Environmental Conservation	2014-03-13 14:48:16	2013	154	senate
f	4881	13	member	Environmental Conservation	2014-03-13 14:48:16	2013	128	senate
t	4883	2	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	141	senate
t	4885	4	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	149	senate
t	4888	7	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	162	senate
f	4890	9	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	143	senate
f	5532	23	member	Rules	2017-08-25 14:31:55	2017	943	senate
t	5534	1	chair_person	Social Services	2017-08-25 14:31:55	2017	1099	senate
t	5536	3	member	Social Services	2017-08-25 14:31:55	2017	954	senate
t	5542	3	member	Transportation	2017-08-25 14:31:55	2017	920	senate
t	5545	6	member	Transportation	2017-08-25 14:31:55	2017	1017	senate
t	5548	9	member	Transportation	2017-08-25 14:31:55	2017	930	senate
t	5551	12	member	Transportation	2017-08-25 14:31:55	2017	951	senate
t	4870	2	vice_chair	Environmental Conservation	2014-03-13 14:48:16	2013	147	senate
t	4872	4	member	Environmental Conservation	2014-03-13 14:48:16	2013	135	senate
t	4874	6	member	Environmental Conservation	2014-03-13 14:48:16	2013	167	senate
t	4876	8	member	Environmental Conservation	2014-03-13 14:48:16	2013	170	senate
f	4878	10	member	Environmental Conservation	2014-03-13 14:48:16	2013	140	senate
f	4880	12	member	Environmental Conservation	2014-03-13 14:48:16	2013	143	senate
t	4882	1	chair_person	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	169	senate
t	4884	3	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	126	senate
t	4887	6	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	150	senate
f	4891	10	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	134	senate
t	4893	1	chair_person	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	164	senate
t	4896	4	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	178	senate
t	4900	8	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	160	senate
f	4902	10	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	158	senate
f	4905	13	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	143	senate
t	4906	1	chair_person	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	153	senate
t	4907	2	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	171	senate
t	4908	3	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	141	senate
t	4909	4	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	182	senate
t	4910	5	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	181	senate
t	4911	6	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	149	senate
t	4912	7	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	173	senate
t	4913	8	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	135	senate
f	4914	9	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	146	senate
f	4915	10	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	136	senate
f	4916	11	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	134	senate
f	4917	12	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	148	senate
t	4918	1	chair_person	Banks	2017-01-30 16:39:21	2017	937	senate
t	4920	3	member	Banks	2017-01-30 16:39:21	2017	954	senate
t	4922	5	member	Banks	2017-01-30 16:39:21	2017	945	senate
t	4924	7	member	Banks	2017-01-30 16:39:21	2017	1088	senate
t	4925	8	member	Banks	2017-01-30 16:39:21	2017	940	senate
t	4926	9	member	Banks	2017-01-30 16:39:21	2017	1014	senate
t	4927	10	member	Banks	2017-01-30 16:39:21	2017	941	senate
t	4928	11	member	Banks	2017-01-30 16:39:21	2017	930	senate
t	4930	13	member	Banks	2017-01-30 16:39:21	2017	1099	senate
f	4932	15	member	Banks	2017-01-30 16:39:21	2017	1098	senate
f	4933	16	member	Banks	2017-01-30 16:39:21	2017	946	senate
f	4934	17	member	Banks	2017-01-30 16:39:21	2017	1085	senate
f	4936	19	member	Banks	2017-01-30 16:39:21	2017	922	senate
f	4937	20	member	Banks	2017-01-30 16:39:21	2017	925	senate
t	4938	1	chair_person	Cities	2017-01-30 16:39:21	2017	1013	senate
t	4939	2	member	Cities	2017-01-30 16:39:21	2017	952	senate
t	4940	3	member	Cities	2017-01-30 16:39:21	2017	920	senate
t	4942	5	member	Cities	2017-01-30 16:39:21	2017	1095	senate
f	4943	6	member	Cities	2017-01-30 16:39:21	2017	1086	senate
t	4944	1	chair_person	Codes	2017-01-30 16:39:21	2017	1084	senate
t	4945	2	vice_chair	Codes	2017-01-30 16:39:21	2017	933	senate
t	4946	3	member	Codes	2017-01-30 16:39:21	2017	952	senate
t	4947	4	member	Codes	2017-01-30 16:39:21	2017	944	senate
t	4948	5	member	Codes	2017-01-30 16:39:21	2017	955	senate
t	4949	6	member	Codes	2017-01-30 16:39:21	2017	956	senate
t	4950	7	member	Codes	2017-01-30 16:39:21	2017	938	senate
t	4951	8	member	Codes	2017-01-30 16:39:21	2017	1017	senate
t	4952	9	member	Codes	2017-01-30 16:39:21	2017	941	senate
t	4953	10	member	Codes	2017-01-30 16:39:21	2017	930	senate
t	4954	11	member	Codes	2017-01-30 16:39:21	2017	1099	senate
f	4955	12	member	Codes	2017-01-30 16:39:21	2017	923	senate
f	4956	13	member	Codes	2017-01-30 16:39:21	2017	1098	senate
f	4957	14	member	Codes	2017-01-30 16:39:21	2017	921	senate
f	4958	15	member	Codes	2017-01-30 16:39:21	2017	918	senate
f	4959	16	member	Codes	2017-01-30 16:39:21	2017	935	senate
f	4960	17	member	Codes	2017-01-30 16:39:21	2017	931	senate
t	4961	1	chair_person	Consumer Protection	2017-01-30 16:39:21	2017	1072	senate
t	4962	2	member	Consumer Protection	2017-01-30 16:39:21	2017	954	senate
t	4963	3	member	Consumer Protection	2017-01-30 16:39:21	2017	944	senate
t	4964	4	member	Consumer Protection	2017-01-30 16:39:21	2017	953	senate
t	4965	5	member	Consumer Protection	2017-01-30 16:39:21	2017	937	senate
t	4966	6	member	Consumer Protection	2017-01-30 16:39:21	2017	945	senate
t	4967	7	member	Consumer Protection	2017-01-30 16:39:21	2017	1015	senate
f	4968	8	member	Consumer Protection	2017-01-30 16:39:21	2017	936	senate
f	4969	9	member	Consumer Protection	2017-01-30 16:39:21	2017	922	senate
f	4970	10	member	Consumer Protection	2017-01-30 16:39:21	2017	934	senate
t	4971	1	chair_person	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	956	senate
t	4972	2	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	952	senate
t	4973	3	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	1017	senate
t	4974	4	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	937	senate
t	4975	5	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	945	senate
t	4976	6	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	1095	senate
t	4977	7	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	947	senate
t	4978	8	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	949	senate
t	4979	9	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	1099	senate
f	4980	10	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	1098	senate
f	4981	11	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	943	senate
f	4982	12	member	Crime Victims, Crime and Correction	2017-01-30 16:39:21	2017	926	senate
t	4983	1	chair_person	Labor	2017-01-30 16:39:21	2017	1090	senate
t	4984	2	member	Labor	2017-01-30 16:39:21	2017	952	senate
t	4987	5	member	Labor	2017-01-30 16:39:21	2017	1088	senate
t	4990	8	member	Labor	2017-01-30 16:39:21	2017	949	senate
f	4993	11	member	Labor	2017-01-30 16:39:21	2017	919	senate
f	4996	14	member	Labor	2017-01-30 16:39:21	2017	932	senate
t	4997	1	chair_person	Transportation	2017-01-30 16:39:21	2017	1015	senate
t	4999	3	member	Transportation	2017-01-30 16:39:21	2017	920	senate
t	5002	6	member	Transportation	2017-01-30 16:39:21	2017	1017	senate
t	5005	9	member	Transportation	2017-01-30 16:39:21	2017	930	senate
t	5008	12	member	Transportation	2017-01-30 16:39:21	2017	951	senate
f	5011	15	member	Transportation	2017-01-30 16:39:21	2017	1085	senate
f	5013	17	member	Transportation	2017-01-30 16:39:21	2017	929	senate
f	5016	20	member	Transportation	2017-01-30 16:39:21	2017	924	senate
t	4985	3	member	Labor	2017-01-30 16:39:21	2017	956	senate
t	4988	6	member	Labor	2017-01-30 16:39:21	2017	1014	senate
t	4991	9	member	Labor	2017-01-30 16:39:21	2017	1015	senate
f	4994	12	member	Labor	2017-01-30 16:39:21	2017	927	senate
t	4998	2	vice_chair	Transportation	2017-01-30 16:39:21	2017	941	senate
t	5001	5	member	Transportation	2017-01-30 16:39:21	2017	956	senate
t	5004	8	member	Transportation	2017-01-30 16:39:21	2017	1088	senate
t	5007	11	member	Transportation	2017-01-30 16:39:21	2017	949	senate
f	5010	14	member	Transportation	2017-01-30 16:39:21	2017	927	senate
f	5014	18	member	Transportation	2017-01-30 16:39:21	2017	931	senate
t	4986	4	member	Labor	2017-01-30 16:39:21	2017	1011	senate
t	4989	7	member	Labor	2017-01-30 16:39:21	2017	957	senate
t	4992	10	member	Labor	2017-01-30 16:39:21	2017	933	senate
f	4995	13	member	Labor	2017-01-30 16:39:21	2017	926	senate
t	5000	4	member	Transportation	2017-01-30 16:39:21	2017	955	senate
t	5003	7	member	Transportation	2017-01-30 16:39:21	2017	1012	senate
t	5006	10	member	Transportation	2017-01-30 16:39:21	2017	948	senate
t	5009	13	member	Transportation	2017-01-30 16:39:21	2017	942	senate
f	5012	16	member	Transportation	2017-01-30 16:39:21	2017	918	senate
f	5015	19	member	Transportation	2017-01-30 16:39:21	2017	923	senate
\.


--
-- Name: committee_member_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_member_id_seq', 5557, true);


--
-- Name: committee_version_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_version_id_seq', 387, true);


SET search_path = public, pg_catalog;

--
-- Name: member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('member_id_seq', 1137, true);


--
-- Name: member_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('member_person_id_seq', 1, false);


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('person_id_seq', 959, true);


--
-- Name: session_member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('session_member_id_seq', 1134, true);


--
-- PostgreSQL database dump complete
--

