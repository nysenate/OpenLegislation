--Sets up Assembly info to automatically generate session members.

UPDATE public.member AS m
SET incumbent = true
FROM public.session_member sm
WHERE m.id = sm.member_id AND
        sm.lbdc_short_name SIMILAR TO
        'BROOK-KRASNY|CURRAN|CARROLL|DICKENS|HEASTIE|HUNTER|HYNDMAN|JONES|MILLER B|MORINELLO|NORRIS|ROSENTHAL D|TAYLOR|VANEL|WALLACE|WALSH|WILLIAMS';

UPDATE public.member AS m
SET incumbent = false
FROM public.session_member sm
WHERE m.id = sm.member_id AND
      m.chamber = 'assembly' AND
      sm.session_year = 2021 AND
      sm.district_code::varchar(255) SIMILAR TO
      '4|21|30|37|45|46|49|63|65|73|75|78|80|92|94|95|97|99|103|105|107|116|121';

--Some session members were incorrectly generated.
DELETE FROM public.session_member
WHERE member_id IN
      (SELECT id FROM public.member WHERE chamber = 'assembly') AND
    session_year = 2023;