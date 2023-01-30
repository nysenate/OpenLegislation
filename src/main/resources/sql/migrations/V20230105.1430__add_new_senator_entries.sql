--Adds people new to the state Senate in 2023, as well as their corresponding member and session_member information.

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Jessica Scarcella-Spanton') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'SPANTON', 2023, 23);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Kristen Gonzalez') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'GONZALEZ', 2023, 59);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Iwen Chu') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'CHU', 2023, 17);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Lea Webb') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'WEBB', 2023, 52);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Steve Rhoads') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'RHOADS', 2023, 5);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Patricia Canzoneri-Fitzpatrick') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'CANZONERI-FITZPATRICK', 2023, 9);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('William Weber') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'WEBER', 2023, 38);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Robert Rolison') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'senate', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'ROLISON', 2023, 39);
