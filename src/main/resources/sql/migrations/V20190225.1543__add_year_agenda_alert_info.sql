-- Add and initialize year column
ALTER TABLE IF EXISTS master.agenda_alert_info_committee
ADD COLUMN IF NOT EXISTS year smallint;

UPDATE master.agenda_alert_info_committee
SET year = date_part('year', reference_date_time);

ALTER TABLE IF EXISTS master.agenda_alert_info_committee
ALTER COLUMN year SET NOT NULL;

-- Remove duplicate rows
DELETE FROM master.agenda_alert_info_committee
WHERE id IN (
  SELECT a.id FROM master.agenda_alert_info_committee a
    INNER JOIN (
    SELECT MAX(id) as max_id, week_of, addendum_id, chamber, committee_name
    FROM master.agenda_alert_info_committee
    GROUP BY (week_of, addendum_id, chamber, committee_name)
    having count(week_of) > 1) dups
  ON a.week_of = dups.week_of AND a.addendum_id = dups.addendum_id
  AND a.chamber = dups.chamber AND a.committee_name = dups.committee_name
  WHERE a.id != dups.max_id);

-- Drop old unique constraint
ALTER TABLE IF EXISTS master.agenda_alert_info_committee
DROP CONSTRAINT IF EXISTS agenda_alert_info_committee_reference_date_time_week_of_add_key;

-- Add new unique constraint
ALTER TABLE IF EXISTS master.agenda_alert_info_committee
ADD CONSTRAINT agenda_alert_year_week_of_chamber_addendum_committee_key
UNIQUE(year, week_of, chamber, addendum_id, committee_name);