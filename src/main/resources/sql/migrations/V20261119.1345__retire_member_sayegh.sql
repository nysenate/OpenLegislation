-- Mark retired Assembly Member Nader Sayegh (AD90) as no longer incumbent.
-- Fails if the member is missing or ambiguous instead of skipping silently.
DO $$
DECLARE
    sayegh_member_id integer;
BEGIN
    SELECT m.id INTO STRICT sayegh_member_id
    FROM public.member m
    JOIN public.session_member sm ON sm.member_id = m.id
    WHERE m.chamber = 'assembly'
      AND sm.lbdc_short_name = 'SAYEGH'
      AND sm.session_year = 2025
      AND sm.district_code = 90;

    UPDATE public.member
    SET incumbent = false
    WHERE id = sayegh_member_id;
END $$;
