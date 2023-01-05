--Remove name fields that are no longer used.
ALTER TABLE public.person DROP COLUMN prefix;
ALTER TABLE public.person DROP COLUMN first_name;
ALTER TABLE public.person DROP COLUMN middle_name;
ALTER TABLE public.person DROP COLUMN last_name;
ALTER TABLE public.person DROP COLUMN suffix;
ALTER TABLE public.member DROP COLUMN full_name;
