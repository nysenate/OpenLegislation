-- Fix error in update script which kept FAHY in the assembly instead of moving her to the senate.

-- Updates for ASSEMBLY district 46
DELETE FROM public.session_member
WHERE lbdc_short_name = 'FAHY'
  AND session_year = 2025;

UPDATE public.member
SET incumbent = true
WHERE id = 499;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (499, 'BROOK-KRASNY', 2025, 46);


-- Updates for SENATE district 46
DELETE FROM public.session_member
WHERE lbdc_short_name = 'BRESLIN'
  AND session_year = 2025;

UPDATE public.member
SET incumbent = false
WHERE id = 372;

-- Insert new Senate member record for Fahy
WITH m AS (
INSERT INTO public.member(person_id, chamber, incumbent)
VALUES (479, 'senate', true)
    RETURNING id
    )
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES ((SELECT id from m), 'FAHY', 2025, 46);


-- Fix member GRIFFIN, she is incumbent.
UPDATE public.member
SET incumbent = true
WHERE id = 1241;
