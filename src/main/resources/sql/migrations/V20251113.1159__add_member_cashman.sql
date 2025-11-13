-- Insert Assembly Member Michael Cashman
WITH p AS (
INSERT INTO public.person(first_name, last_name)
VALUES ('Michael', 'Cashman')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'CASHMAN', 2025, 115);
