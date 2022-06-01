-- Add assembly member Monique Chandler-Waterman
WITH p AS (
INSERT into public.person(full_name, first_name, middle_name, last_name, email, prefix, suffix, img_name)
VALUES ('Monique Chandler-Waterman', 'Monique', '', 'Chandler-Waterman', '', 'Assembly Member', '', 'Monique Chandler-Waterman.jpeg')
    RETURNING id
    ),
    m AS (
INSERT INTO public.member(person_id, chamber, incumbent, full_name)
VALUES ((SELECT id from p), 'assembly', true, 'Monique Chandler-Waterman')
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'CHANDLER-WATERMAN', 2021, 58);
