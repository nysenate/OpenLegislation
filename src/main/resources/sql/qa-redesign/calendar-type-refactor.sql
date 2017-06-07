
UPDATE master.spotcheck_mismatch
SET key = key || hstore('type', 'ALL')
WHERE content_type = 'CALENDAR'
AND NOT exist(key, 'type');
