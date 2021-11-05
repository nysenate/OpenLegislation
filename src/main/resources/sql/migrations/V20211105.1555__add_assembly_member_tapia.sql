WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Yudelka Tapia', 'Yudelka', '', 'Tapia', '', 'Assembly Member', '', 'Yudelka Tapia.jpg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Yudelka Tapia')
    RETURNING id
    )

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'TAPIA', 2019, 86);

