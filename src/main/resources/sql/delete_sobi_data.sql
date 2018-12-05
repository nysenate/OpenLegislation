SET SEARCH_PATH = master;
-- Disable triggers for this session
SET SESSION_REPLICATION_ROLE = replica;

TRUNCATE sobi_file CASCADE;
TRUNCATE agenda CASCADE;
TRUNCATE bill CASCADE;
TRUNCATE calendar CASCADE;

TRUNCATE calendar_change_log;
TRUNCATE bill_change_log;
TRUNCATE agenda_change_log;

-- Delete committees and committee versions generated by SOBI data, leaving only those initialized from openleg.data.sql
-- TODO change this when committee data model has been fixed
DELETE FROM master.committee_version WHERE last_fragment_id IS NOT NULL OR created > '2013-01-01';
UPDATE master.committee_version SET reformed = 'infinity';

DELETE FROM master.committee c
WHERE NOT EXISTS(SELECT 1
                 FROM master.committee_version v
                 WHERE c.chamber = v.chamber AND c.name = v.committee_name);

-- Delete orphan committee members
-- This shouldn't be necessary due to cascading fkey constraints, but orphans have been a problem..
DELETE FROM master.committee_member m
WHERE NOT EXISTS(SELECT 1
                 FROM master.committee_version v
                 WHERE m.chamber = v.chamber
                   AND m.committee_name = v.committee_name
                   AND m.session_year = v.session_year
                   AND m.version_created = v.created);


UPDATE master.committee
SET current_session = 2013, current_version = '2013-01-01';

VACUUM;

-- reenable triggers
SET SESSION_REPLICATION_ROLE = default;