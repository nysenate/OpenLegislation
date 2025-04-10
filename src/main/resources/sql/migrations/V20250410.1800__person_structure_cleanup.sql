--Generated functions need to be immutable.
CREATE OR REPLACE FUNCTION immutable_concat_ws(text, VARIADIC text[])
    RETURNS text
    LANGUAGE internal IMMUTABLE PARALLEL SAFE AS
'text_concat_ws';

ALTER TABLE public.person
    DROP COLUMN full_name,
    ADD COLUMN full_name varchar GENERATED ALWAYS AS
        (immutable_concat_ws(' ', first_name, middle_name, last_name, suffix)) STORED;

ALTER TABLE public.person
    ALTER COLUMN middle_name DROP NOT NULL,
    ALTER COLUMN suffix DROP NOT NULL;

UPDATE public.person
SET middle_name = NULL WHERE middle_name = '';

UPDATE public.person
SET suffix = NULL WHERE suffix = '';

CREATE OR REPLACE FUNCTION correct_strings()
    RETURNS TRIGGER
    LANGUAGE plpgsql AS
$$
BEGIN
    new.middle_name := NULLIF(trim(new.middle_name), '');
    new.suffix := NULLIF(trim(new.suffix), '');
    new.email := NULLIF(trim(new.email), '');
    new.img_name := NULLIF(new.img_name, '');
    new.img_name = COALESCE(new.img_name, 'no_image.jpg');
    return new;
END;
$$;

CREATE TRIGGER correct_strings_trigger
    BEFORE INSERT OR UPDATE ON public.person
    FOR EACH ROW EXECUTE PROCEDURE correct_strings();
