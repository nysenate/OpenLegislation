-- Add assembly member Manny De Los Santos
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Manny De Los Santos', 'Manny', '', 'De Los Santos', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Manny De Los Santos')
    RETURNING id
    )

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'DE LOS SANTOS', 2021, 72);
