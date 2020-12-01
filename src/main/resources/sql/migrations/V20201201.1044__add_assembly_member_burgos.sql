WITH p AS (
  INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
    VALUES ('Kenny Burgos', 'Kenny', '', 'Burgos', '', 'Assembly Member', '', 'no_image.jpg')
    RETURNING id
),
m AS (
  INSERT INTO public.member(person_id, chamber, incumbent, full_name)
    VALUES ((SELECT id from p), 'assembly', true, 'Kenny Burgos')
    RETURNING id
)

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
  VALUES ((SELECT id from m), 'BURGOS', 2019, 85);


-- CRESPO is no longer the incumbent.
UPDATE public.member
SET incumbent = false
WHERE id = 537;
