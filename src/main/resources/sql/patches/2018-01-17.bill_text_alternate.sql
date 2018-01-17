

-- Modify pdf files to include session in name
-- Add 2018 budget pdfs

SET SEARCH_PATH = master;

DELETE FROM bill_text_alternate_pdf;

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
2017	A9500		t	/static/pdf/2017-S7500-A9500.pdf
2017	S7501		t	/static/pdf/2017-S7501-A9501.pdf
2017	A9501		t	/static/pdf/2017-S7501-A9501.pdf
2017	S7502		t	/static/pdf/2017-S7502-A9502.pdf
2017	A9502		t	/static/pdf/2017-S7502-A9502.pdf
2017	S7503		t	/static/pdf/2017-S7503-A9503.pdf
2017	A9503		t	/static/pdf/2017-S7503-A9503.pdf
2017	S7504		t	/static/pdf/2017-S7504-A9504.pdf
2017	A9504		t	/static/pdf/2017-S7504-A9504.pdf
\.
