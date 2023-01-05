--Generates the remaining new session members, assuming incumbents kept their districts and shortnames.

WITH temp AS (
    SELECT m.id, MAX(session_year) AS max_year
    FROM public.session_member sm
    JOIN public.member m ON m.id = sm.member_id
    GROUP BY m.id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
SELECT m.id, sm.lbdc_short_name, 2023, sm.district_code FROM public.member m
    JOIN public.session_member sm ON m.id = sm.member_id
    JOIN temp ON temp.id = m.id
    WHERE incumbent = true AND session_year = temp.max_year AND alternate = false
ON CONFLICT DO NOTHING;
