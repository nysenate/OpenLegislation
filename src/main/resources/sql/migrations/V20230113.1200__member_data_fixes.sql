--Some members were accidentally duplicated, and some middle names are not abbreviated correctly.
UPDATE public.person
SET full_name = trim(full_name);

UPDATE public.session_member
SET member_id = (SELECT m.id FROM public.member m
                    JOIN public.person p on p.id = m.person_id
                    WHERE p.full_name = 'Dean Murray' AND m.chamber = 'assembly')
WHERE session_year < 2013 AND lbdc_short_name = 'MURRAY';

UPDATE public.member
SET person_id = (SELECT id FROM public.person
                    WHERE full_name = 'Roxanne J Persaud')
WHERE person_id = (SELECT id FROM public.person
                    WHERE full_name = 'Roxanne Persaud');

UPDATE public.member
SET person_id = (SELECT id FROM public.person
                 WHERE full_name = 'Thomas F. O''Mara')
WHERE person_id = (SELECT id FROM public.person
                   WHERE full_name = 'Tom O''Mara');

DELETE FROM public.member
WHERE id = (SELECT m.id FROM public.member m
                JOIN public.person p on p.id = m.person_id
                WHERE p.full_name = 'L. Dean Murray');

DELETE FROM public.person
WHERE full_name = 'L. Dean Murray' OR
      full_name = 'Roxanne Persaud' OR
      full_name = 'Tom O''Mara';

UPDATE public.person
SET full_name = 'William Boyland'
WHERE full_name = '"William Boyland';

UPDATE public.person
SET full_name = 'MaryJane Shimsky'
WHERE full_name = 'Maryjane Shimsky';

UPDATE public.person
SET full_name = 'Nader Sayegh'
WHERE full_name = 'Nader sayegh';

UPDATE public.person
SET full_name = 'Andrew J. Lanza'
WHERE full_name = 'Andrew J Lanza';

UPDATE public.person
SET full_name = 'Carl L. Marcellino'
WHERE full_name = 'Carl L Marcellino';

UPDATE public.person
SET full_name = 'Antoine M. Thompson'
WHERE full_name = 'Antoine M Thompson';

UPDATE public.person
SET full_name = 'Roxanne J. Persaud'
WHERE full_name = 'Roxanne J Persaud';

UPDATE public.person
SET full_name = 'Fredrick J. Akshar II'
WHERE full_name = 'Fredrick J Akshar II';

UPDATE public.person
SET full_name = 'Ron Castorina Jr.'
WHERE full_name = 'Ron Castorina, Jr.';
