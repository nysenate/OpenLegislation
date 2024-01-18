INSERT INTO master.bill_text_alternate_pdf(bill_print_no, bill_session_year, bill_amend_version, active, url_path)
VALUES ('S8300', 2023, '', true, '/static/pdf/2024-S8300-A8800.pdf'),
       ('A8800', 2023, '', true, '/static/pdf/2024-S8300-A8800.pdf'),
       ('A8801', 2023, '', true, '/static/pdf/2024-S8301-A8801.pdf'),
       ('S8301', 2023, '', true, '/static/pdf/2024-S8301-A8801.pdf'),
       ('S8302', 2023, '', true, '/static/pdf/2024-S8302-A8802.pdf'),
       ('A8802', 2023, '', true, '/static/pdf/2024-S8302-A8802.pdf'),
       ('S8303', 2023, '', true, '/static/pdf/2024-S8303-A8803.pdf'),
       ('A8803', 2023, '', true, '/static/pdf/2024-S8303-A8803.pdf'),
       ('S8304', 2023, '', true, '/static/pdf/2024-S8304-A8804.pdf'),
       ('A8804', 2023, '', true, '/static/pdf/2024-S8304-A8804.pdf')
    ON CONFLICT DO NOTHING
