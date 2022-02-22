-- Corrects Tapia's session year to 2021.
UPDATE public.session_member
SET session_year = 2021
WHERE member_id = 1503
AND lbdc_short_name = 'TAPIA'
AND session_year = 2019;
