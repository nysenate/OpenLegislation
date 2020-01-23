INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S7500', 2019, '', true, '/static/pdf/2019-S7500-A9500.pdf'),
       ('A9500', 2019, '', true, '/static/pdf/2019-S7500-A9500.pdf'),
       ('S7501', 2019, '', true, '/static/pdf/2019-S7501-A9501.pdf'),
       ('A9501', 2019, '', true, '/static/pdf/2019-S7501-A9501.pdf'),
       ('S7502', 2019, '', true, '/static/pdf/2019-S7502-A9502.pdf'),
       ('A9502', 2019, '', true, '/static/pdf/2019-S7502-A9502.pdf'),
       ('S7503', 2019, '', true, '/static/pdf/2019-S7503-A9503.pdf'),
       ('A9503', 2019, '', true, '/static/pdf/2019-S7503-A9503.pdf'),
       ('S7504', 2019, '', true, '/static/pdf/2019-S7504-A9504.pdf'),
       ('A9504', 2019, '', true, '/static/pdf/2019-S7504-A9504.pdf')
ON CONFLICT DO NOTHING;