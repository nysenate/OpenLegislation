-- Adds 2019 budget pdfs to alternate pdf table

COPY master.bill_text_alternate_pdf (bill_session_year, bill_print_no, bill_amend_version, active, url_path) FROM stdin;
2019	S1500		t	/static/pdf/2019-S1500-A2000.pdf
2019	A2000		t	/static/pdf/2019-S1500-A2000.pdf
2019	S1501		t	/static/pdf/2019-S1501-A2001.pdf
2019	A2001		t	/static/pdf/2019-S1501-A2001.pdf
2019	S1502		t	/static/pdf/2019-S1502-A2002.pdf
2019	A2002		t	/static/pdf/2019-S1502-A2002.pdf
2019	S1503		t	/static/pdf/2019-S1503-A2003.pdf
2019	A2003		t	/static/pdf/2019-S1503-A2003.pdf
2019	S1504		t	/static/pdf/2019-S1504-A2004.pdf
2019	A2004		t	/static/pdf/2019-S1504-A2004.pdf
\.
