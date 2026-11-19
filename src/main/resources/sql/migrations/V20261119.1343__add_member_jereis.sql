-- Insert Assembly Member Frank Jereis (AD90)
WITH p AS (
    INSERT INTO public.person(first_name, last_name)
    SELECT 'Frank', 'Jereis'
    WHERE NOT EXISTS (
        SELECT 1
        FROM public.session_member sm
        JOIN public.member m ON m.id = sm.member_id
        WHERE m.chamber = 'assembly'
          AND sm.lbdc_short_name = 'JEREIS'
          AND sm.session_year = 2027
    )
    RETURNING id
),
m AS (
    INSERT INTO public.member(person_id, chamber, incumbent)
    SELECT id, 'assembly', true FROM p
    RETURNING id
)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
SELECT id, 'JEREIS', 2027, 90 FROM m;
