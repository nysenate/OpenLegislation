UPDATE master.spotcheck_mismatch
SET type = 'AGENDA_BILLS'
WHERE type = 'AGENDA_VOTES'
;
