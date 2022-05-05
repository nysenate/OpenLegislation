-- Add assembly member Nikki Lucas
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Nikki Lucas', 'Nikki', '', 'Lucas', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Nikki Lucas')
    RETURNING id
    )

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'LUCAS', 2021, 60);
