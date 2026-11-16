-- Insert Senate Member Aber Kawas (SD12)
WITH p AS (
    INSERT INTO public.person(first_name, last_name, email)
    SELECT 'Aber', 'Kawas', 'kawas@nysenate.gov'
    WHERE NOT EXISTS (
        SELECT 1
        FROM public.session_member sm
        JOIN public.member m ON m.id = sm.member_id
        WHERE m.chamber = 'senate'
          AND sm.lbdc_short_name = 'KAWAS'
          AND sm.session_year = 2027
    )
    RETURNING id
),
m AS (
    INSERT INTO public.member(person_id, chamber, incumbent)
    SELECT id, 'senate', true FROM p
    RETURNING id
)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
SELECT id, 'KAWAS', 2027, 12 FROM m;
