
--- Content types have been altered to be tied to the key type.
--- This script will adjust content types for some mismatches to reflect new types.

UPDATE master.spotcheck_mismatch
SET content_type = 'BILL_AMENDMENT'
WHERE reference_type = 'SENATE_SITE_BILLS'
;

UPDATE master.spotcheck_mismatch
SET content_type = 'AGENDA_WEEK'
WHERE reference_type = 'LBDC_AGENDA_ALERT'
;