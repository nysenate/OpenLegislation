-- Adds a designation for "dummy" law documents that are created by the parser but not specified in the law files.

SET SEARCH_PATH = master;

ALTER TABLE law_document ADD COLUMN IF NOT EXISTS dummy boolean NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN law_document.dummy IS
    'Indicates if this document was created by the parser but not specified in source files';

ALTER TABLE law_document ALTER COLUMN dummy DROP DEFAULT;

-- Current dummy docs have the "-ROOT" suffix
UPDATE master.law_document
SET dummy = TRUE
WHERE document_id LIKE '%-ROOT'
;
