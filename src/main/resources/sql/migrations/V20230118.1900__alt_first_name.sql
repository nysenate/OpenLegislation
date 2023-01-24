ALTER TABLE public.person
ADD COLUMN alt_first_name VARCHAR NOT NULL DEFAULT '';

UPDATE public.person
SET alt_first_name = 'Jo Anne'
WHERE full_name = 'Jo Anne Simon';
