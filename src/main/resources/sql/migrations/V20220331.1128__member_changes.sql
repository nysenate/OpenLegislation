-- Joseph P. Addabbo Jr. is an incumbent
UPDATE public.member
    SET incumbent = true
    WHERE id = 384;

-- Assembly member Perry resigned
UPDATE public.member
SET incumbent = false
WHERE id = 511;

-- Add assembly member Brian A. Cunningham
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Brian Cunningham', 'Brian', '', 'Cunningham', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Brian Cunningham')
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'CUNNINGHAM', 2021, 43);
