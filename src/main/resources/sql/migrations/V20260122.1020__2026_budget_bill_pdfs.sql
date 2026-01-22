INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S9000', 2025, '', true, '/static/pdf/2026-S9000-A10000.pdf'),
       ('A10000', 2025, '', true, '/static/pdf/2026-S9000-A10000.pdf'),
       ('S9001', 2025, '', true, '/static/pdf/2026-S9001-A10001.pdf'),
       ('A10001', 2025, '', true, '/static/pdf/2026-S9001-A10001.pdf'),
       ('S9002', 2025, '', true, '/static/pdf/2026-S9002-A10002.pdf'),
       ('A10002', 2025, '', true, '/static/pdf/2026-S9002-A10002.pdf'),
       ('S9003', 2025, '', true, '/static/pdf/2026-S9003-A10003.pdf'),
       ('A10003', 2025, '', true, '/static/pdf/2026-S9003-A10003.pdf'),
       ('S9004', 2025, '', true, '/static/pdf/2026-S9004-A10004.pdf'),
       ('A10004', 2025, '', true, '/static/pdf/2026-S9004-A10004.pdf')
    ON CONFLICT DO NOTHING