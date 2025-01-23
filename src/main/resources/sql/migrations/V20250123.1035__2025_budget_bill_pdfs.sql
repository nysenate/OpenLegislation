INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S3000', 2025, '', true, '/static/pdf/2025-S3000-A3000.pdf'),
       ('A3000', 2025, '', true, '/static/pdf/2025-S3000-A3000.pdf'),
       ('S3001', 2025, '', true, '/static/pdf/2025-S3001-A3001.pdf'),
       ('A3001', 2025, '', true, '/static/pdf/2025-S3001-A3001.pdf'),
       ('S3002', 2025, '', true, '/static/pdf/2025-S3002-A3002.pdf'),
       ('A3002', 2025, '', true, '/static/pdf/2025-S3002-A3002.pdf'),
       ('S3003', 2025, '', true, '/static/pdf/2025-S3003-A3003.pdf'),
       ('A3003', 2025, '', true, '/static/pdf/2025-S3003-A3003.pdf'),
       ('S3004', 2025, '', true, '/static/pdf/2025-S3004-A3004.pdf'),
       ('A3004', 2025, '', true, '/static/pdf/2025-S3004-A3004.pdf')
    ON CONFLICT DO NOTHING
