--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = master, pg_catalog;

--
-- Data for Name: committee; Type: TABLE DATA; Schema: master; Owner: postgres
--

COPY committee (name, id, current_version, chamber, current_session, full_name) FROM stdin;
Aging	24	2014-03-13 14:48:16	senate	2013	\N
Agriculture	14	2014-02-28 11:25:44	senate	2013	\N
Alcoholism and Drug Abuse	12	2014-02-28 11:25:44	senate	2013	\N
Banks	30	2014-03-13 14:48:16	senate	2013	\N
Children and Families	27	2014-02-28 11:25:44	senate	2013	\N
Cities	25	2014-03-13 14:48:16	senate	2013	\N
Corporations, Authorities and Commissions	3	2014-02-28 11:25:44	senate	2013	\N
Crime Victims, Crime and Correction	33	2014-02-28 11:25:44	senate	2013	\N
Education	9	2014-03-13 14:48:16	senate	2013	\N
Cultural Affairs, Tourism, Parks and Recreation	20	2014-02-28 11:25:44	senate	2013	\N
Elections	32	2014-02-28 11:25:44	senate	2013	\N
Environmental Conservation	17	2014-03-13 14:48:16	senate	2013	\N
Ethics	4	2014-02-28 11:25:44	senate	2013	\N
Higher Education	23	2014-02-28 11:25:44	senate	2013	\N
Infrastructure and Capital Investment	58	2014-02-28 11:25:44	senate	2013	\N
Racing, Gaming and Wagering	7	2014-03-13 14:48:16	senate	2013	\N
Investigations and Government Operations	2	2014-02-28 11:25:44	senate	2013	\N
Labor	11	2014-02-28 11:25:44	senate	2013	\N
Local Government	22	2014-02-28 11:25:44	senate	2013	\N
Veterans, Homeland Security and Military Affairs	8	2014-03-13 14:48:16	senate	2013	\N
Mental Health and Developmental Disabilities	26	2014-02-28 11:25:44	senate	2013	\N
Social Services	13	2014-02-28 11:25:44	senate	2013	\N
Commerce, Economic Development and Small Business	19	2014-03-17 18:43:42	senate	2013	\N
Civil Service and Pensions	15	2014-03-03 17:09:09	senate	2013	\N
Codes	29	2014-03-03 17:09:09	senate	2013	\N
Consumer Protection	16	2014-03-03 17:09:09	senate	2013	\N
Energy and Telecommunications	31	2014-03-03 17:09:09	senate	2013	\N
Finance	21	2014-03-03 17:09:09	senate	2013	\N
Health	6	2014-03-03 17:09:09	senate	2013	\N
Housing, Construction and Community Development	28	2014-03-03 17:09:09	senate	2013	\N
Insurance	34	2014-03-03 17:09:09	senate	2013	\N
Judiciary	5	2014-03-03 17:09:09	senate	2013	\N
Rules	18	2014-03-03 17:09:09	senate	2013	\N
Transportation	10	2014-03-03 17:09:09	senate	2013	\N
New York City Education Subcommittee	63	2014-03-03 17:09:09	senate	2013	\N
\.


--
-- Name: committee_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_id_seq', 349, true);


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
106	Room 410 LOB	MONDAY	f		13:30:00	2013	2014-03-03 17:09:09	infinity	Civil Service and Pensions	senate	\N
76	Room 410 LOB	MONDAY	f		13:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Civil Service and Pensions	senate	\N
107	Room 124 CAP	TUESDAY	f		09:00:00	2013	2014-03-03 17:09:09	infinity	Codes	senate	\N
77	Room 124 CAP	TUESDAY	f		09:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Codes	senate	\N
79	Room 801 LOB	MONDAY	f		13:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Consumer Protection	senate	\N
105	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-03-03 17:09:09	2014-03-13 14:48:16	Banks	senate	\N
86	Room 901 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:00:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Environmental Conservation	senate	\N
99	Room 510 LOB	TUESDAY	f		09:30:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Racing, Gaming and Wagering	senate	\N
103	Room 816 LOB	TUESDAY	f		13:30:00	2013	2014-02-28 11:25:44	2014-03-13 14:48:16	Veterans, Homeland Security and Military Affairs	senate	\N
78	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Commerce, Economic Development and Small Business	senate	\N
109	Room 801 LOB	MONDAY	f		13:00:00	2013	2014-03-03 17:09:09	infinity	Consumer Protection	senate	\N
83	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Education	senate	\N
111	Room 709 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:30:00	2013	2014-03-03 17:09:09	infinity	Energy and Telecommunications	senate	\N
95	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Judiciary	senate	\N
102	Room 124 CAP	TUESDAY	f		13:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Transportation	senate	\N
119		\N	f		\N	2013	2014-03-03 17:09:09	infinity	New York City Education Subcommittee	senate	\N
104		\N	f		\N	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	New York City Education Subcommittee	senate	\N
110	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-03-03 17:09:09	2014-03-13 14:48:16	Education	senate	\N
108	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-03-03 17:09:09	2014-03-17 18:43:42	Commerce, Economic Development and Small Business	senate	\N
85	Room 709 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	13:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Energy and Telecommunications	senate	\N
112	Room 124 CAP	TUESDAY	f		11:00:00	2013	2014-03-03 17:09:09	infinity	Finance	senate	\N
88	Room 124 CAP	TUESDAY	f		11:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Finance	senate	\N
113	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2013	2014-03-03 17:09:09	infinity	Health	senate	\N
89	Room 124 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:00:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Health	senate	\N
114	Room 308 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-03-03 17:09:09	infinity	Housing, Construction and Community Development	senate	\N
91	Room 308 LOB	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Housing, Construction and Community Development	senate	\N
115	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-03-03 17:09:09	infinity	Insurance	senate	\N
93	Room 124 CAP	MONDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Insurance	senate	\N
116	Room 123 CAP	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	11:30:00	2013	2014-03-03 17:09:09	infinity	Judiciary	senate	\N
117		\N	f		\N	2013	2014-03-03 17:09:09	infinity	Rules	senate	\N
100		\N	f		\N	2013	2014-02-28 11:25:44	2014-03-03 17:09:09	Rules	senate	\N
118	Room 124 CAP	TUESDAY	f		13:00:00	2013	2014-03-03 17:09:09	infinity	Transportation	senate	\N
120	Room 816 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:00:00	2013	2014-03-13 14:48:16	infinity	Aging	senate	\N
121	Room 611 LOB	WEDNESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:30:00	2013	2014-03-13 14:48:16	infinity	Banks	senate	\N
122	Room 916 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	12:30:00	2013	2014-03-13 14:48:16	infinity	Cities	senate	\N
123	Room 124 CAP	TUESDAY	f		10:00:00	2013	2014-03-13 14:48:16	infinity	Education	senate	\N
124	Room 901 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	09:00:00	2013	2014-03-13 14:48:16	infinity	Environmental Conservation	senate	\N
125	Room 510 LOB	TUESDAY	f		09:30:00	2013	2014-03-13 14:48:16	infinity	Racing, Gaming and Wagering	senate	\N
126	Room 816 LOB	TUESDAY	f		13:30:00	2013	2014-03-13 14:48:16	infinity	Veterans, Homeland Security and Military Affairs	senate	\N
127	Room 511 LOB	TUESDAY	t	\n* This committee will meet on alternate weeks pursuant to the notice of the Chairman *\n	10:30:00	2013	2014-03-17 18:43:42	infinity	Commerce, Economic Development and Small Business	senate	\N
\.


SET search_path = public, pg_catalog;

--
-- Data for Name: person; Type: TABLE DATA; Schema: public; Owner: postgres
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
498	Greene							f
499	Edward Hennessey	Edward	\N	Hennessey	\N	\N	\N	t
397	Albert A. Stirpe	Albert	A.	Stirpe	\N	Assembly Member	\N	t
500	WEISENBERGT	\N	\N	WEISENBERGT	\N	\N	\N	f
501	RIVERA	\N	\N	RIVERA	\N	\N	\N	f
502	PEOPLES-STOKE	\N	\N	PEOPLES-STOKE	\N	\N	\N	f
503	B GOTTFRIED	B	\N	GOTTFRIED	\N	\N	\N	f
504	RULES	\N	\N	RULES	\N	\N	\N	f
505	MAGNARELLIS	\N	\N	MAGNARELLIS	\N	\N	\N	f
506	PEOPLES-STOKE	\N	\N	PEOPLES-STOKE	\N	\N	\N	f
507	M WEINSTEIN	M	\N	WEINSTEIN	\N	\N	\N	f
508	CYMBROWITZSNY	\N	\N	CYMBROWITZSNY	\N	\N	\N	f
509	I ZEBROWSKI	I	\N	ZEBROWSKI	\N	\N	\N	f
510	E WEINSTEIN	E	\N	WEINSTEIN	\N	\N	\N	f
511	BRAUNSTEINWITZ	\N	\N	BRAUNSTEINWITZ	\N	\N	\N	f
512	BRAUNSTEINZ	\N	\N	BRAUNSTEINZ	\N	\N	\N	f
513	STRIPE	\N	\N	STRIPE	\N	\N	\N	f
514	PEOPLES-STOKE	\N	\N	PEOPLES-STOKE	\N	\N	\N	f
515	S O'DONNELL	S	\N	O'DONNELL	\N	\N	\N	f
516	T WEINSTEIN	T	\N	WEINSTEIN	\N	\N	\N	f
517	B O'DONNELL	B	\N	O'DONNELL	\N	\N	\N	f
518	O BENEDETTO	O	\N	BENEDETTO	\N	\N	\N	f
519	R BENEDETTO	R	\N	BENEDETTO	\N	\N	\N	f
520	L O'DONNELL	L	\N	O'DONNELL	\N	\N	\N	f
\.


--
-- Data for Name: member; Type: TABLE DATA; Schema: public; Owner: postgres
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
676	498	assembly	f	Greene
677	499	assembly	t	Edward Hennessey
678	500	assembly	f	WEISENBERGT
679	501	assembly	f	RIVERA
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
691	513	assembly	t	STRIPE
692	514	assembly	t	PEOPLES-STOKE
693	515	assembly	t	S O'DONNELL
694	516	assembly	t	T WEINSTEIN
695	517	assembly	t	B O'DONNELL
696	518	assembly	t	O BENEDETTO
697	519	assembly	t	R BENEDETTO
698	520	assembly	t	L O'DONNELL
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
507	528	RIVERA	2013	76	f
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
673	679	RIVERA	2009	\N	f
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
686	692	PEOPLES-STOKE	2013	\N	f
687	693	O'DONNELL S	2013	\N	f
688	694	WEINSTEIN T	2013	\N	f
689	695	O'DONNELL B	2013	\N	f
690	696	BENEDETTO O	2013	\N	f
691	697	BENEDETTO R	2013	\N	f
692	698	O'DONNELL L	2013	\N	f
\.


SET search_path = master, pg_catalog;

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
t	1414	2	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	126	senate
t	1415	3	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	149	senate
t	1416	4	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	183	senate
t	1417	5	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	179	senate
t	1418	6	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	173	senate
t	1419	7	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	155	senate
f	1420	8	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	172	senate
f	1421	9	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	146	senate
f	1422	10	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	137	senate
f	1423	11	member	Energy and Telecommunications	2014-03-03 17:09:09	2013	187	senate
t	1424	1	chair_person	Finance	2014-03-03 17:09:09	2013	132	senate
t	1425	2	member	Finance	2014-03-03 17:09:09	2013	169	senate
t	1426	3	member	Finance	2014-03-03 17:09:09	2013	151	senate
t	1427	4	member	Finance	2014-03-03 17:09:09	2013	155	senate
t	1428	5	member	Finance	2014-03-03 17:09:09	2013	178	senate
t	1429	6	member	Finance	2014-03-03 17:09:09	2013	149	senate
t	1430	7	member	Finance	2014-03-03 17:09:09	2013	154	senate
t	1431	8	member	Finance	2014-03-03 17:09:09	2013	180	senate
t	1432	9	member	Finance	2014-03-03 17:09:09	2013	165	senate
t	1433	10	member	Finance	2014-03-03 17:09:09	2013	130	senate
t	1434	11	member	Finance	2014-03-03 17:09:09	2013	175	senate
t	1435	12	member	Finance	2014-03-03 17:09:09	2013	135	senate
t	1436	13	member	Finance	2014-03-03 17:09:09	2013	163	senate
t	1437	14	member	Finance	2014-03-03 17:09:09	2013	150	senate
t	1438	15	member	Finance	2014-03-03 17:09:09	2013	183	senate
t	1439	16	member	Finance	2014-03-03 17:09:09	2013	162	senate
t	1440	17	member	Finance	2014-03-03 17:09:09	2013	173	senate
t	1441	18	member	Finance	2014-03-03 17:09:09	2013	159	senate
t	1442	19	member	Finance	2014-03-03 17:09:09	2013	127	senate
t	1443	20	member	Finance	2014-03-03 17:09:09	2013	170	senate
t	1444	21	member	Finance	2014-03-03 17:09:09	2013	171	senate
t	1445	22	member	Finance	2014-03-03 17:09:09	2013	153	senate
f	1446	23	member	Finance	2014-03-03 17:09:09	2013	129	senate
f	1447	24	member	Finance	2014-03-03 17:09:09	2013	177	senate
f	1448	25	member	Finance	2014-03-03 17:09:09	2013	137	senate
f	1449	26	member	Finance	2014-03-03 17:09:09	2013	133	senate
f	1450	27	member	Finance	2014-03-03 17:09:09	2013	138	senate
f	1451	28	member	Finance	2014-03-03 17:09:09	2013	174	senate
f	1452	29	member	Finance	2014-03-03 17:09:09	2013	168	senate
f	1453	30	member	Finance	2014-03-03 17:09:09	2013	172	senate
f	1454	31	member	Finance	2014-03-03 17:09:09	2013	187	senate
f	1455	32	member	Finance	2014-03-03 17:09:09	2013	131	senate
f	1456	33	member	Finance	2014-03-03 17:09:09	2013	156	senate
f	1457	34	member	Finance	2014-03-03 17:09:09	2013	157	senate
f	1458	35	member	Finance	2014-03-03 17:09:09	2013	146	senate
f	1459	36	member	Finance	2014-03-03 17:09:09	2013	140	senate
f	1460	37	member	Finance	2014-03-03 17:09:09	2013	136	senate
t	1461	1	chair_person	Health	2014-03-03 17:09:09	2013	180	senate
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
t	1328	1	chair_person	Banks	2014-03-03 17:09:09	2013	149	senate
t	1329	2	vice_chair	Banks	2014-03-03 17:09:09	2013	151	senate
t	1330	3	member	Banks	2014-03-03 17:09:09	2013	169	senate
t	1331	4	member	Banks	2014-03-03 17:09:09	2013	132	senate
t	1332	5	member	Banks	2014-03-03 17:09:09	2013	178	senate
t	1333	6	member	Banks	2014-03-03 17:09:09	2013	144	senate
t	1334	7	member	Banks	2014-03-03 17:09:09	2013	153	senate
t	1335	8	member	Banks	2014-03-03 17:09:09	2013	163	senate
t	1336	9	member	Banks	2014-03-03 17:09:09	2013	183	senate
t	1337	10	member	Banks	2014-03-03 17:09:09	2013	162	senate
t	1338	11	member	Banks	2014-03-03 17:09:09	2013	171	senate
t	1339	12	member	Banks	2014-03-03 17:09:09	2013	147	senate
f	1340	13	member	Banks	2014-03-03 17:09:09	2013	148	senate
f	1341	14	member	Banks	2014-03-03 17:09:09	2013	174	senate
f	1342	15	member	Banks	2014-03-03 17:09:09	2013	177	senate
f	1343	16	member	Banks	2014-03-03 17:09:09	2013	172	senate
f	1344	17	member	Banks	2014-03-03 17:09:09	2013	134	senate
f	1345	18	member	Banks	2014-03-03 17:09:09	2013	158	senate
t	1346	1	chair_person	Civil Service and Pensions	2014-03-03 17:09:09	2013	178	senate
t	1347	2	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	180	senate
t	1348	3	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	165	senate
t	1349	4	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	153	senate
t	1350	5	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	179	senate
t	1351	6	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	159	senate
t	1352	7	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	167	senate
f	1353	8	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	134	senate
f	1354	9	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	139	senate
f	1355	10	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	137	senate
f	1356	11	member	Civil Service and Pensions	2014-03-03 17:09:09	2013	131	senate
t	1357	1	chair_person	Codes	2014-03-03 17:09:09	2013	150	senate
t	1358	2	member	Codes	2014-03-03 17:09:09	2013	141	senate
t	1359	3	member	Codes	2014-03-03 17:09:09	2013	132	senate
t	1360	4	member	Codes	2014-03-03 17:09:09	2013	155	senate
t	1361	5	member	Codes	2014-03-03 17:09:09	2013	182	senate
t	1362	6	member	Codes	2014-03-03 17:09:09	2013	178	senate
t	1363	7	member	Codes	2014-03-03 17:09:09	2013	165	senate
t	1364	8	member	Codes	2014-03-03 17:09:09	2013	183	senate
t	1365	9	member	Codes	2014-03-03 17:09:09	2013	149	senate
t	1366	10	member	Codes	2014-03-03 17:09:09	2013	147	senate
f	1367	11	member	Codes	2014-03-03 17:09:09	2013	157	senate
f	1368	12	member	Codes	2014-03-03 17:09:09	2013	131	senate
f	1369	13	member	Codes	2014-03-03 17:09:09	2013	140	senate
f	1370	14	member	Codes	2014-03-03 17:09:09	2013	176	senate
f	1371	15	member	Codes	2014-03-03 17:09:09	2013	148	senate
f	1372	16	member	Codes	2014-03-03 17:09:09	2013	129	senate
t	1373	1	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	171	senate
t	1374	2	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	141	senate
t	1375	3	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	182	senate
t	1376	4	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	181	senate
t	1377	5	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	149	senate
t	1378	6	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	173	senate
t	1379	7	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	135	senate
f	1380	8	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	146	senate
f	1381	9	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	136	senate
f	1382	10	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	134	senate
f	1383	11	member	Commerce, Economic Development and Small Business	2014-03-03 17:09:09	2013	148	senate
t	1384	1	chair_person	Consumer Protection	2014-03-03 17:09:09	2013	160	senate
t	1385	2	member	Consumer Protection	2014-03-03 17:09:09	2013	141	senate
t	1386	3	member	Consumer Protection	2014-03-03 17:09:09	2013	135	senate
t	1387	4	member	Consumer Protection	2014-03-03 17:09:09	2013	144	senate
t	1388	5	member	Consumer Protection	2014-03-03 17:09:09	2013	167	senate
t	1389	6	member	Consumer Protection	2014-03-03 17:09:09	2013	159	senate
t	1390	7	member	Consumer Protection	2014-03-03 17:09:09	2013	173	senate
f	1391	8	member	Consumer Protection	2014-03-03 17:09:09	2013	176	senate
f	1392	9	member	Consumer Protection	2014-03-03 17:09:09	2013	142	senate
f	1393	10	member	Consumer Protection	2014-03-03 17:09:09	2013	158	senate
f	1394	11	member	Consumer Protection	2014-03-03 17:09:09	2013	143	senate
t	1395	1	chair_person	Education	2014-03-03 17:09:09	2013	155	senate
t	1396	2	member	Education	2014-03-03 17:09:09	2013	151	senate
t	1397	3	member	Education	2014-03-03 17:09:09	2013	165	senate
t	1398	4	member	Education	2014-03-03 17:09:09	2013	175	senate
t	1399	5	member	Education	2014-03-03 17:09:09	2013	135	senate
t	1400	6	member	Education	2014-03-03 17:09:09	2013	163	senate
t	1401	7	member	Education	2014-03-03 17:09:09	2013	167	senate
t	1402	8	member	Education	2014-03-03 17:09:09	2013	162	senate
t	1403	9	member	Education	2014-03-03 17:09:09	2013	173	senate
t	1404	10	member	Education	2014-03-03 17:09:09	2013	127	senate
t	1405	11	member	Education	2014-03-03 17:09:09	2013	171	senate
t	1406	12	member	Education	2014-03-03 17:09:09	2013	147	senate
f	1407	13	member	Education	2014-03-03 17:09:09	2013	143	senate
f	1409	15	member	Education	2014-03-03 17:09:09	2013	174	senate
f	1410	16	member	Education	2014-03-03 17:09:09	2013	168	senate
f	1411	17	member	Education	2014-03-03 17:09:09	2013	156	senate
f	1412	18	member	Education	2014-03-03 17:09:09	2013	128	senate
f	1408	14	member	Education	2014-03-03 17:09:09	2013	139	senate
t	1413	1	chair_person	Energy and Telecommunications	2014-03-03 17:09:09	2013	167	senate
t	1519	14	member	Judiciary	2014-03-03 17:09:09	2013	147	senate
f	1520	15	member	Judiciary	2014-03-03 17:09:09	2013	136	senate
f	1521	16	member	Judiciary	2014-03-03 17:09:09	2013	174	senate
f	1522	17	member	Judiciary	2014-03-03 17:09:09	2013	137	senate
f	1523	18	member	Judiciary	2014-03-03 17:09:09	2013	140	senate
f	1524	19	member	Judiciary	2014-03-03 17:09:09	2013	131	senate
f	1525	20	member	Judiciary	2014-03-03 17:09:09	2013	156	senate
f	1526	21	member	Judiciary	2014-03-03 17:09:09	2013	176	senate
f	1527	22	member	Judiciary	2014-03-03 17:09:09	2013	139	senate
f	1528	23	member	Judiciary	2014-03-03 17:09:09	2013	157	senate
t	1555	2	vice_chair	Transportation	2014-03-03 17:09:09	2013	163	senate
t	1557	4	member	Transportation	2014-03-03 17:09:09	2013	182	senate
t	1558	5	member	Transportation	2014-03-03 17:09:09	2013	130	senate
t	1559	6	member	Transportation	2014-03-03 17:09:09	2013	167	senate
t	1560	7	member	Transportation	2014-03-03 17:09:09	2013	150	senate
t	1561	8	member	Transportation	2014-03-03 17:09:09	2013	183	senate
t	1562	9	member	Transportation	2014-03-03 17:09:09	2013	162	senate
t	1563	10	member	Transportation	2014-03-03 17:09:09	2013	170	senate
t	1564	11	member	Transportation	2014-03-03 17:09:09	2013	160	senate
t	1565	12	member	Transportation	2014-03-03 17:09:09	2013	147	senate
f	1566	13	member	Transportation	2014-03-03 17:09:09	2013	137	senate
f	1567	14	member	Transportation	2014-03-03 17:09:09	2013	177	senate
f	1568	15	member	Transportation	2014-03-03 17:09:09	2013	146	senate
f	1569	16	member	Transportation	2014-03-03 17:09:09	2013	131	senate
f	1570	17	member	Transportation	2014-03-03 17:09:09	2013	157	senate
f	1571	18	member	Transportation	2014-03-03 17:09:09	2013	156	senate
f	1572	19	member	Transportation	2014-03-03 17:09:09	2013	158	senate
t	1573	1	chair_person	New York City Education Subcommittee	2014-03-03 17:09:09	2013	181	senate
t	1574	2	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	165	senate
t	1575	3	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	178	senate
t	1576	4	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	147	senate
f	1577	5	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	131	senate
f	1578	6	member	New York City Education Subcommittee	2014-03-03 17:09:09	2013	156	senate
t	1462	2	member	Health	2014-03-03 17:09:09	2013	164	senate
t	1463	3	member	Health	2014-03-03 17:09:09	2013	151	senate
t	1464	4	member	Health	2014-03-03 17:09:09	2013	181	senate
t	1465	5	member	Health	2014-03-03 17:09:09	2013	178	senate
t	1466	6	member	Health	2014-03-03 17:09:09	2013	130	senate
t	1467	7	member	Health	2014-03-03 17:09:09	2013	159	senate
t	1468	8	member	Health	2014-03-03 17:09:09	2013	127	senate
t	1469	9	member	Health	2014-03-03 17:09:09	2013	170	senate
t	1470	10	member	Health	2014-03-03 17:09:09	2013	153	senate
f	1471	11	member	Health	2014-03-03 17:09:09	2013	133	senate
f	1472	12	member	Health	2014-03-03 17:09:09	2013	168	senate
f	1473	13	member	Health	2014-03-03 17:09:09	2013	136	senate
f	1474	14	member	Health	2014-03-03 17:09:09	2013	187	senate
f	1475	15	member	Health	2014-03-03 17:09:09	2013	148	senate
f	1476	16	member	Health	2014-03-03 17:09:09	2013	176	senate
f	1477	17	member	Health	2014-03-03 17:09:09	2013	142	senate
t	1478	1	chair_person	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	170	senate
t	1479	2	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	169	senate
t	1480	3	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	141	senate
t	1481	4	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	182	senate
t	1482	5	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	150	senate
t	1483	6	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	147	senate
f	1484	7	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	140	senate
f	1485	8	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	177	senate
f	1486	9	member	Housing, Construction and Community Development	2014-03-03 17:09:09	2013	129	senate
t	1487	1	chair_person	Insurance	2014-03-03 17:09:09	2013	127	senate
t	1488	2	member	Insurance	2014-03-03 17:09:09	2013	126	senate
t	1489	3	member	Insurance	2014-03-03 17:09:09	2013	155	senate
t	1490	4	member	Insurance	2014-03-03 17:09:09	2013	178	senate
t	1491	5	member	Insurance	2014-03-03 17:09:09	2013	154	senate
t	1492	6	member	Insurance	2014-03-03 17:09:09	2013	165	senate
t	1493	7	member	Insurance	2014-03-03 17:09:09	2013	130	senate
t	1494	8	member	Insurance	2014-03-03 17:09:09	2013	175	senate
t	1495	9	member	Insurance	2014-03-03 17:09:09	2013	153	senate
t	1496	10	member	Insurance	2014-03-03 17:09:09	2013	183	senate
t	1497	11	member	Insurance	2014-03-03 17:09:09	2013	170	senate
t	1498	12	member	Insurance	2014-03-03 17:09:09	2013	147	senate
f	1499	13	member	Insurance	2014-03-03 17:09:09	2013	174	senate
f	1500	14	member	Insurance	2014-03-03 17:09:09	2013	140	senate
f	1501	15	member	Insurance	2014-03-03 17:09:09	2013	146	senate
f	1502	16	member	Insurance	2014-03-03 17:09:09	2013	172	senate
f	1503	17	member	Insurance	2014-03-03 17:09:09	2013	156	senate
f	1504	18	member	Insurance	2014-03-03 17:09:09	2013	148	senate
f	1505	19	member	Insurance	2014-03-03 17:09:09	2013	187	senate
t	1506	1	chair_person	Judiciary	2014-03-03 17:09:09	2013	169	senate
t	1507	2	member	Judiciary	2014-03-03 17:09:09	2013	132	senate
t	1508	3	member	Judiciary	2014-03-03 17:09:09	2013	155	senate
t	1509	4	member	Judiciary	2014-03-03 17:09:09	2013	180	senate
t	1510	5	member	Judiciary	2014-03-03 17:09:09	2013	165	senate
t	1511	6	member	Judiciary	2014-03-03 17:09:09	2013	175	senate
t	1512	7	member	Judiciary	2014-03-03 17:09:09	2013	135	senate
t	1513	8	member	Judiciary	2014-03-03 17:09:09	2013	150	senate
t	1514	9	member	Judiciary	2014-03-03 17:09:09	2013	183	senate
t	1515	10	member	Judiciary	2014-03-03 17:09:09	2013	159	senate
t	1516	11	member	Judiciary	2014-03-03 17:09:09	2013	162	senate
t	1517	12	member	Judiciary	2014-03-03 17:09:09	2013	160	senate
t	1518	13	member	Judiciary	2014-03-03 17:09:09	2013	154	senate
t	1529	1	chair_person	Rules	2014-03-03 17:09:09	2013	184	senate
t	1530	2	vice_chair	Rules	2014-03-03 17:09:09	2013	166	senate
t	1531	3	member	Rules	2014-03-03 17:09:09	2013	169	senate
t	1532	4	member	Rules	2014-03-03 17:09:09	2013	126	senate
t	1533	5	member	Rules	2014-03-03 17:09:09	2013	151	senate
t	1534	6	member	Rules	2014-03-03 17:09:09	2013	155	senate
t	1535	7	member	Rules	2014-03-03 17:09:09	2013	180	senate
t	1536	8	member	Rules	2014-03-03 17:09:09	2013	130	senate
t	1537	9	member	Rules	2014-03-03 17:09:09	2013	175	senate
t	1538	10	member	Rules	2014-03-03 17:09:09	2013	163	senate
t	1539	11	member	Rules	2014-03-03 17:09:09	2013	167	senate
t	1540	12	member	Rules	2014-03-03 17:09:09	2013	150	senate
t	1541	13	member	Rules	2014-03-03 17:09:09	2013	127	senate
t	1542	14	member	Rules	2014-03-03 17:09:09	2013	171	senate
t	1543	15	member	Rules	2014-03-03 17:09:09	2013	135	senate
f	1544	16	member	Rules	2014-03-03 17:09:09	2013	152	senate
f	1545	17	member	Rules	2014-03-03 17:09:09	2013	174	senate
f	1546	18	member	Rules	2014-03-03 17:09:09	2013	137	senate
f	1547	19	member	Rules	2014-03-03 17:09:09	2013	136	senate
f	1548	20	member	Rules	2014-03-03 17:09:09	2013	129	senate
f	1549	21	member	Rules	2014-03-03 17:09:09	2013	168	senate
f	1550	22	member	Rules	2014-03-03 17:09:09	2013	172	senate
f	1551	23	member	Rules	2014-03-03 17:09:09	2013	131	senate
f	1552	24	member	Rules	2014-03-03 17:09:09	2013	140	senate
f	1553	25	member	Rules	2014-03-03 17:09:09	2013	138	senate
t	1554	1	chair_person	Transportation	2014-03-03 17:09:09	2013	173	senate
t	1556	3	member	Transportation	2014-03-03 17:09:09	2013	126	senate
t	1579	1	chair_person	Aging	2014-03-13 14:48:16	2013	171	senate
t	1580	2	member	Aging	2014-03-13 14:48:16	2013	178	senate
t	1581	3	member	Aging	2014-03-13 14:48:16	2013	181	senate
t	1582	4	member	Aging	2014-03-13 14:48:16	2013	175	senate
t	1583	5	member	Aging	2014-03-13 14:48:16	2013	144	senate
t	1584	6	member	Aging	2014-03-13 14:48:16	2013	160	senate
f	1585	7	member	Aging	2014-03-13 14:48:16	2013	177	senate
f	1586	8	member	Aging	2014-03-13 14:48:16	2013	139	senate
f	1587	9	member	Aging	2014-03-13 14:48:16	2013	156	senate
f	1588	10	member	Aging	2014-03-13 14:48:16	2013	134	senate
t	1589	1	chair_person	Banks	2014-03-13 14:48:16	2013	149	senate
t	1590	2	vice_chair	Banks	2014-03-13 14:48:16	2013	151	senate
t	1591	3	member	Banks	2014-03-13 14:48:16	2013	169	senate
t	1592	4	member	Banks	2014-03-13 14:48:16	2013	132	senate
t	1593	5	member	Banks	2014-03-13 14:48:16	2013	178	senate
t	1594	6	member	Banks	2014-03-13 14:48:16	2013	144	senate
t	1595	7	member	Banks	2014-03-13 14:48:16	2013	153	senate
t	1596	8	member	Banks	2014-03-13 14:48:16	2013	163	senate
t	1597	9	member	Banks	2014-03-13 14:48:16	2013	183	senate
t	1598	10	member	Banks	2014-03-13 14:48:16	2013	162	senate
t	1599	11	member	Banks	2014-03-13 14:48:16	2013	171	senate
t	1600	12	member	Banks	2014-03-13 14:48:16	2013	147	senate
f	1601	13	member	Banks	2014-03-13 14:48:16	2013	148	senate
f	1602	14	member	Banks	2014-03-13 14:48:16	2013	174	senate
f	1603	15	member	Banks	2014-03-13 14:48:16	2013	177	senate
f	1604	16	member	Banks	2014-03-13 14:48:16	2013	172	senate
f	1605	17	member	Banks	2014-03-13 14:48:16	2013	134	senate
f	1606	18	member	Banks	2014-03-13 14:48:16	2013	158	senate
f	1607	19	member	Banks	2014-03-13 14:48:16	2013	146	senate
t	1608	1	chair_person	Cities	2014-03-13 14:48:16	2013	165	senate
t	1609	2	member	Cities	2014-03-13 14:48:16	2013	164	senate
t	1610	3	member	Cities	2014-03-13 14:48:16	2013	132	senate
t	1611	4	member	Cities	2014-03-13 14:48:16	2013	154	senate
f	1612	5	member	Cities	2014-03-13 14:48:16	2013	157	senate
t	1646	1	chair_person	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	169	senate
t	1647	2	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	141	senate
t	1651	6	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	150	senate
t	1652	7	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	162	senate
f	1653	8	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	139	senate
f	1654	9	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	143	senate
f	1655	10	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	134	senate
f	1656	11	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	128	senate
t	1657	1	chair_person	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	164	senate
t	1658	2	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	126	senate
t	1659	3	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	155	senate
t	1660	4	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	178	senate
t	1661	5	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	149	senate
t	1662	6	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	154	senate
t	1663	7	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	130	senate
t	1664	8	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	160	senate
f	1665	9	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	139	senate
f	1666	10	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	158	senate
f	1667	11	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	134	senate
f	1668	12	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	128	senate
f	1669	13	member	Veterans, Homeland Security and Military Affairs	2014-03-13 14:48:16	2013	143	senate
t	1670	1	chair_person	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	153	senate
t	1671	2	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	171	senate
t	1672	3	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	141	senate
f	1613	6	member	Cities	2014-03-13 14:48:16	2013	174	senate
t	1614	1	chair_person	Education	2014-03-13 14:48:16	2013	155	senate
t	1615	2	member	Education	2014-03-13 14:48:16	2013	151	senate
t	1616	3	member	Education	2014-03-13 14:48:16	2013	165	senate
t	1617	4	member	Education	2014-03-13 14:48:16	2013	175	senate
t	1618	5	member	Education	2014-03-13 14:48:16	2013	135	senate
t	1619	6	member	Education	2014-03-13 14:48:16	2013	163	senate
t	1620	7	member	Education	2014-03-13 14:48:16	2013	167	senate
t	1621	8	member	Education	2014-03-13 14:48:16	2013	162	senate
t	1622	9	member	Education	2014-03-13 14:48:16	2013	173	senate
t	1623	10	member	Education	2014-03-13 14:48:16	2013	127	senate
t	1624	11	member	Education	2014-03-13 14:48:16	2013	171	senate
t	1625	12	member	Education	2014-03-13 14:48:16	2013	147	senate
f	1626	13	member	Education	2014-03-13 14:48:16	2013	143	senate
f	1627	14	member	Education	2014-03-13 14:48:16	2013	139	senate
f	1628	15	member	Education	2014-03-13 14:48:16	2013	174	senate
f	1629	16	member	Education	2014-03-13 14:48:16	2013	168	senate
f	1630	17	member	Education	2014-03-13 14:48:16	2013	156	senate
f	1631	18	member	Education	2014-03-13 14:48:16	2013	128	senate
f	1632	19	member	Education	2014-03-13 14:48:16	2013	187	senate
t	1633	1	chair_person	Environmental Conservation	2014-03-13 14:48:16	2013	154	senate
t	1634	2	vice_chair	Environmental Conservation	2014-03-13 14:48:16	2013	147	senate
t	1635	3	member	Environmental Conservation	2014-03-13 14:48:16	2013	175	senate
t	1636	4	member	Environmental Conservation	2014-03-13 14:48:16	2013	135	senate
t	1637	5	member	Environmental Conservation	2014-03-13 14:48:16	2013	163	senate
t	1638	6	member	Environmental Conservation	2014-03-13 14:48:16	2013	167	senate
t	1639	7	member	Environmental Conservation	2014-03-13 14:48:16	2013	183	senate
t	1640	8	member	Environmental Conservation	2014-03-13 14:48:16	2013	170	senate
f	1641	9	member	Environmental Conservation	2014-03-13 14:48:16	2013	148	senate
f	1642	10	member	Environmental Conservation	2014-03-13 14:48:16	2013	140	senate
f	1643	11	member	Environmental Conservation	2014-03-13 14:48:16	2013	142	senate
f	1644	12	member	Environmental Conservation	2014-03-13 14:48:16	2013	143	senate
f	1645	13	member	Environmental Conservation	2014-03-13 14:48:16	2013	128	senate
t	1648	3	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	126	senate
t	1649	4	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	149	senate
t	1650	5	member	Racing, Gaming and Wagering	2014-03-13 14:48:16	2013	144	senate
t	1673	4	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	182	senate
t	1674	5	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	181	senate
t	1675	6	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	149	senate
t	1676	7	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	173	senate
t	1677	8	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	135	senate
f	1678	9	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	146	senate
f	1679	10	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	136	senate
f	1680	11	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	134	senate
f	1681	12	member	Commerce, Economic Development and Small Business	2014-03-17 18:43:42	2013	148	senate
\.


--
-- Name: committee_member_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_member_id_seq', 1681, true);


--
-- Name: committee_version_id_seq; Type: SEQUENCE SET; Schema: master; Owner: postgres
--

SELECT pg_catalog.setval('committee_version_id_seq', 127, true);


SET search_path = public, pg_catalog;

--
-- Name: member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('member_id_seq', 698, true);


--
-- Name: member_person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('member_person_id_seq', 1, false);


--
-- Name: person_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('person_id_seq', 520, true);


--
-- Name: session_member_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('session_member_id_seq', 692, true);


--
-- PostgreSQL database dump complete
--

