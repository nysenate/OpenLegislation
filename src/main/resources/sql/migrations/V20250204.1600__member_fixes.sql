UPDATE public.person
SET email = null
WHERE email = '';

UPDATE public.person
SET email = null
WHERE id NOT IN (
    SELECT person.id AS person_id FROM person
    JOIN public.member ON person.id = member.person_id
    WHERE incumbent = true
);
