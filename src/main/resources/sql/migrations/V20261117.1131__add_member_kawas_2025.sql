-- Add Senate Member Aber Kawas (SD12) to the 2025 session.
-- Reuses the member created by V20261116.1643__add_member_kawas.sql and
-- fails if that member is missing or ambiguous instead of skipping silently.
DO $$
DECLARE
    kawas_member_id integer;
BEGIN
    SELECT m.id INTO STRICT kawas_member_id
    FROM public.member m
    JOIN public.session_member sm ON sm.member_id = m.id
    WHERE m.chamber = 'senate'
      AND sm.lbdc_short_name = 'KAWAS'
      AND sm.session_year = 2027;

    INSERT INTO public.session_member(member_id, lbdc_short_name, session_year, district_code)
    VALUES (kawas_member_id, 'KAWAS', 2025, 12)
    ON CONFLICT (member_id, lbdc_short_name, session_year) DO NOTHING;
END $$;
