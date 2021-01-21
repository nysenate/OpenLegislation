INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES
('A3000', 2021, '', true, '/static/pdf/2021-S2500-A3000.pdf'),
('S2500', 2021, '', true, '/static/pdf/2021-S2500-A3000.pdf'),
('A3001', 2021, '', true, '/static/pdf/2021-S2501-A3001.pdf'),
('S2501', 2021, '', true, '/static/pdf/2021-S2501-A3001.pdf'),
('A3002', 2021, '', true, '/static/pdf/2021-S2502-A3002.pdf'),
('S2502', 2021, '', true, '/static/pdf/2021-S2502-A3002.pdf'),
('A3003', 2021, '', true, '/static/pdf/2021-S2503-A3003.pdf'),
('S2503', 2021, '', true, '/static/pdf/2021-S2503-A3003.pdf'),
('A3004', 2021, '', true, '/static/pdf/2021-S2504-A3004.pdf'),
('S2504', 2021, '', true, '/static/pdf/2021-S2504-A3004.pdf'),
('A3011', 2021, '', true, '/static/pdf/2021-S2511-A3011.pdf'),
('S2511', 2021, '', true, '/static/pdf/2021-S2511-A3011.pdf')
ON CONFLICT DO NOTHING;