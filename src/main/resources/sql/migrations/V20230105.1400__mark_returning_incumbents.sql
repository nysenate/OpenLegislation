WITH temp AS
    (UPDATE public.member AS m
    SET incumbent = true
    FROM public.session_member sm
    WHERE m.id = sm.member_id AND
          sm.lbdc_short_name SIMILAR TO 'MARTINEZ|MARTINS'
    RETURNING m.id, sm.lbdc_short_name, 2023)
INSERT INTO public.session_member(member_id, lbdc_short_name, session_year)
SELECT * FROM temp;

UPDATE public.session_member
SET district_code = 4
WHERE lbdc_short_name = 'MARTINEZ' AND session_year = 2023;

UPDATE public.session_member
SET district_code = 7
WHERE lbdc_short_name = 'MARTINS' AND session_year = 2023;
