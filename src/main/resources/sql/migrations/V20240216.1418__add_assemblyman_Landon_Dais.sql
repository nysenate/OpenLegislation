-- Adds new Assembly member Landon Dais
WITH p AS (
INSERT into public.person(email, img_name, first_name, middle_name, last_name, suffix)
VALUES ('', 'no_image.jpg', 'Landon', 'C.', 'Dais', '')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES ((SELECT id from p), 'assembly', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'DAIS', 2023, 77);
