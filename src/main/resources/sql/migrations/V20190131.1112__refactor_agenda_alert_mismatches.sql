-- This updates existing agenda alert mismatches, replacing key->agenda_no with key->week_of for issue #12223.

UPDATE master.spotcheck_mismatch
SET key = key || hstore('year', extract(year from reference_active_date_time::timestamp)::text)
WHERE reference_type = 'LBDC_AGENDA_ALERT'
  AND datasource = 'LBDC'
  AND key->'year' = '0';

-- Update agenda_no if it is -1
UPDATE master.spotcheck_mismatch
SET key = key || hstore('agenda_no', extract(week from reference_active_date_time::timestamp)::text)
WHERE reference_type = 'LBDC_AGENDA_ALERT'
  AND datasource = 'LBDC'
  AND key->'agenda_no' = '-1'
  AND reference_active_date_time < '2019-01-01';

-- Add week_of from agenda data
UPDATE master.spotcheck_mismatch mm
SET key = key || hstore('week_of', aia.week_of::text)
FROM master.agenda_info_addendum aia
WHERE mm.reference_type = 'LBDC_AGENDA_ALERT'
  AND mm.datasource = 'LBDC'
  AND aia.year::text = mm.key->'year'
  AND aia.agenda_no::text = mm.key->'agenda_no';

-- Delete agenda_no from key
UPDATE master.spotcheck_mismatch
SET key = key - 'agenda_no'::text
WHERE reference_type = 'LBDC_AGENDA_ALERT'
  AND datasource = 'LBDC';

-- Delete bad mismatches that could not be fixed.
DELETE FROM master.spotcheck_mismatch
WHERE reference_type = 'LBDC_AGENDA_ALERT'
  AND key->'week_of' is null;
