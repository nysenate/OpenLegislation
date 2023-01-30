--Generates the remaining new session members. Copied code from an earlier file.

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

UPDATE public.session_member
SET district_code = 121
WHERE session_year = 2023 AND
      lbdc_short_name = 'ANGELINO';

UPDATE public.session_member
SET district_code = 122
WHERE session_year = 2023 AND
        lbdc_short_name = 'MILLER B';
