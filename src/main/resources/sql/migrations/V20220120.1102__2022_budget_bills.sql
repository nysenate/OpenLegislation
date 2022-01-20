INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S8000', 2021, '', true, '/static/pdf/2022-S8000-A9000.pdf'),
       ('A9000', 2021, '', true, '/static/pdf/2022-S8000-A9000.pdf'),
       ('S8001', 2021, '', true, '/static/pdf/2022-S8001-A9001.pdf'),
       ('A9001', 2021, '', true, '/static/pdf/2022-S8001-A9001.pdf'),
       ('S8002', 2021, '', true, '/static/pdf/2022-S8002-A9002.pdf'),
       ('A9002', 2021, '', true, '/static/pdf/2022-S8002-A9002.pdf'),
       ('S8003', 2021, '', true, '/static/pdf/2022-S8003-A9003.pdf'),
       ('A9003', 2021, '', true, '/static/pdf/2022-S8003-A9003.pdf'),
       ('S8004', 2021, '', true, '/static/pdf/2022-S8004-A9004.pdf'),
       ('A9004', 2021, '', true, '/static/pdf/2022-S8004-A9004.pdf')
    ON CONFLICT DO NOTHING
