
-- Add assembly member Jake Ryan Blumencranz
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Jake Ryan Blumencranz', 'Jake', 'Ryan', 'Blumencranz', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Jake Ryan Blumencranz')
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BLUMENCRANZ', 2021, 15);
