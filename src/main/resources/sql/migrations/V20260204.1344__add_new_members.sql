-- Insert Senate Member Erik Bottcher (SD47)
WITH p AS (
INSERT INTO public.person(first_name, last_name)
VALUES ('Erik', 'Bottcher')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BOTTCHER', 2025, 47);


-- Insert Senate Member Jeremy Zellner (SD61)
WITH p AS (
INSERT INTO public.person(first_name, last_name)
VALUES ('Jeremy', 'Zellner')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'ZELLNER', 2025, 61);


-- Insert Assembly Member Diana C. Moreno (AD36)
WITH p AS (
INSERT INTO public.person(first_name, last_name)
VALUES ('Diana', 'Moreno')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'MORENO', 2025, 36);


-- Insert Assembly Member Keith Powers (AD74)
WITH p AS (
INSERT INTO public.person(first_name, last_name)
VALUES ('Keith', 'Powers')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'POWERS', 2025, 74);
