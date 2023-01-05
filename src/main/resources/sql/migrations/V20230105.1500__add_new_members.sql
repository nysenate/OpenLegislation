--These people switched fom the Assembly to the Senate, so they need new entries in the members table.

WITH m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
    VALUES ((SELECT id FROM public.person WHERE full_name = 'Nathalia Fernandez'), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'FERNANDEZ', 2023, 34);

WITH m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
    VALUES ((SELECT id FROM public.person WHERE full_name = 'Dean Murray'), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'MURRAY', 2023, 3);

WITH m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
    VALUES ((SELECT id FROM public.person WHERE full_name = 'Jake Ashby'), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'ASHBY', 2023, 23);

WITH m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
    VALUES ((SELECT id FROM public.person WHERE full_name = 'Mark Walczyk'), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'WALCZYK', 2023, 49);