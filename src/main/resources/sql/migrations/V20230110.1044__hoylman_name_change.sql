-- Updates for Hoylman shortname change
UPDATE
    public.session_member
SET alternate = true
WHERE member_id = 438
  AND lbdc_short_name = 'HOYLMAN'
  AND district_code = 47
  AND session_year = 2023;

INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
VALUES (438, 'HOYLMAN-SIGAL', 2023, 47);