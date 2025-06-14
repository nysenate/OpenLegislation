WITH p AS (
    INSERT INTO public.person(first_name, last_name)
    VALUES ('Sam', 'Sutton')
    RETURNING id
),
m AS (
    INSERT INTO public.member(person_id, chamber, incumbent)
    VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'SUTTON', 2025, 22);
