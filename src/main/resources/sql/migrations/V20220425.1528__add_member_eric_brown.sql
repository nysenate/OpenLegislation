
-- Add assembly member Eric Brown
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Eric Brown', 'Eric', '', 'Brown', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Eric Brown')
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'BROWN E', 2021, 20);


-- Keith Brown's short name was updated to 'BROWN K'
UPDATE public.session_member
SET alternate = true
WHERE id = 1765;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (1461, 'BROWN K', 2021, 12);
