UPDATE public.session_member
SET alternate = true
WHERE id = 1780;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code, alternate)
VALUES (1474, 'FORREST', 2021, 57, false)
ON CONFLICT DO NOTHING;