UPDATE public.person
SET full_name = 'Rodneyse Bichotte Hermelyn',
last_name = 'Bichotte Hermelyn'
WHERE id = 720;

UPDATE public.member
SET full_name = 'Rodneyse Bichotte Hermelyn'
WHERE id = 898;

UPDATE public.session_member
SET alternate = true
WHERE id = 1653;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (898, 'BICHOTTE HERMELYN', 2021, 42)
ON CONFLICT DO NOTHING;
