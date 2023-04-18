INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S4000', 2023, '', true, '/static/pdf/2023-S4000-A3000.pdf'),
       ('A3000', 2023, '', true, '/static/pdf/2023-S4000-A3000.pdf'),
       ('S4001', 2023, '', true, '/static/pdf/2023-S4001-A3001.pdf'),
       ('A3001', 2023, '', true, '/static/pdf/2023-S4001-A3001.pdf'),
       ('S4002', 2023, '', true, '/static/pdf/2023-S4002-A3002.pdf'),
       ('A3002', 2023, '', true, '/static/pdf/2023-S4002-A3002.pdf'),
       ('S4003', 2023, '', true, '/static/pdf/2023-S4003-A3003.pdf'),
       ('A3003', 2023, '', true, '/static/pdf/2023-S4003-A3003.pdf'),
       ('S4004', 2023, '', true, '/static/pdf/2023-S4004-A3004.pdf'),
       ('A3004', 2023, '', true, '/static/pdf/2023-S4004-A3004.pdf')
    ON CONFLICT DO NOTHING
