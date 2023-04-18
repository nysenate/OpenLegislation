UPDATE public.person
SET full_name = 'Juan Ardila'
WHERE full_name = 'Juan Ardilla';

UPDATE public.session_member
SET lbdc_short_name = 'ARDILA'
WHERE lbdc_short_name = 'ARDILLA'
    AND session_year = 2023;