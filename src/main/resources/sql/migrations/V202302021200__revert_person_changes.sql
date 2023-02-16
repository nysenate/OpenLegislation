--We need to add the name parts back in.
ALTER TABLE public.person
    DROP COLUMN alt_first_name,
    ADD COLUMN first_name varchar NOT NULL DEFAULT '',
    ADD COLUMN middle_name varchar NOT NULL DEFAULT '',
    ADD COLUMN last_name varchar NOT NULL DEFAULT '',
    ADD COLUMN suffix varchar NOT NULL DEFAULT '';

--Helps to split up names. Not that arrays are 1-indexed.
CREATE PROCEDURE split_up_names() LANGUAGE plpgsql AS $$
DECLARE name_split varchar[]; person_row RECORD;
BEGIN
    FOR person_row IN SELECT * FROM public.person
        LOOP
            name_split = regexp_split_to_array(person_row.full_name, ' ');
            IF name_split[3] SIMILAR TO 'Sr_?|Jr_?|[IV]+' THEN
                name_split[4] = name_split[3];
                name_split[3] = name_split[2];
                name_split[2] = '';
            END IF;
            IF name_split[3] IS NULL THEN
                name_split[3] = name_split[2];
                name_split[2] = '';
            END IF;
            IF name_split[4] IS NULL THEN
                name_split[4] = '';
            END IF;
            UPDATE public.person
            SET first_name = name_split[1], middle_name = name_split[2],
                last_name = name_split[3], suffix = name_split[4]
            WHERE full_name = person_row.full_name;
        END LOOP;
END;
$$;

CALL split_up_names();
DROP PROCEDURE split_up_names();
--These odd names need specific updates.
UPDATE public.person
SET first_name = 'Jo Anne', middle_name = ''
WHERE full_name = 'Jo Anne Simon';

UPDATE public.person
SET last_name = trim(middle_name || ' ' || last_name || ' ' || suffix), middle_name = '', suffix = ''
WHERE full_name = 'Carmen De La Rosa' OR
        full_name = 'Manny De Los Santos' OR
        full_name = 'Rodneyse Bichotte Hermelyn' OR
        full_name = 'Stacey Pheffer Amato';
--This ensures the full_name matches the name parts.
ALTER TABLE public.person
    DROP COLUMN full_name,
    ADD COLUMN full_name varchar GENERATED ALWAYS AS
        (regexp_replace(trim(first_name || ' ' || middle_name || ' ' || last_name || ' ' || suffix), ' {2,}', ' ')) STORED;
