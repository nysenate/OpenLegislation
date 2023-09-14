-- Adds new Assembly member Sam Berger
WITH p AS (
INSERT into public.person(email, img_name, first_name, middle_name, last_name, suffix)
VALUES ('', 'no_image.jpg', 'Sam', '', 'Berger', '')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BERGER', 2023, 27);
