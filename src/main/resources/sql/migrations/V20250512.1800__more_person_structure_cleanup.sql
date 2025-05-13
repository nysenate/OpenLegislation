ALTER TABLE public.person
DROP COLUMN full_name;

ALTER TABLE public.person
ADD CONSTRAINT has_name CHECK
    ( first_name IS NOT NULL AND first_name != '' AND last_name IS NOT NULL AND last_name != '' );