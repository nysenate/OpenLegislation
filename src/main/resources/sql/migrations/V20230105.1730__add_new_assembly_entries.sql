--Adds data for people who are new to the state legislature.

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Edward Flood') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'FLOOD', 2023, 4);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Steven Raga') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'RAGA', 2023, 30);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Juan Ardilla') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'ARDILLA', 2023, 37);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Michael Novakhov') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'NOVAKHOV', 2023, 45);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Lester Chang') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'CHANG', 2023, 49);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Samuel Pirozzolo') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'PIROZZOLO', 2023, 63);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Grace Lee') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'LEE', 2023, 65);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Alex Bores') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'BORES', 2023, 73);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Tony Simone') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'SIMONE', 2023, 75);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('George Alvarez') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'ALVAREZ', 2023, 78);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('John Zaccaro Jr.') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'ZACCARO', 2023, 80);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Maryjane Shimsky') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'SHIMSKY', 2023, 92);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Matthew Slater') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'SLATER', 2023, 94);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Dana Levenberg') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'LEVENBERG', 2023, 95);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('John W. McGowan') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'MCGOWAN', 2023, 97);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Christopher Eachus') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'EACHUS', 2023, 99);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Brian Maher') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'MAHER', 2023, 101);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Sarahana Shrestha') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'SHRESTHA', 2023, 103);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Anil Beephan Jr.') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'BEEPHAN', 2023, 105);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Scott H. Bendett') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'BENDETT', 2023, 107);

WITH p_id AS (INSERT into public.person(full_name) VALUES ('Scott Gray') RETURNING id),
     m_id AS (INSERT INTO public.member(person_id, chamber, incumbent)
         VALUES ((SELECT id FROM p_id), 'assembly', true) RETURNING id)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id FROM m_id), 'GRAY', 2023, 116);
