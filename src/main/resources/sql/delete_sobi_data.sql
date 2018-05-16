SET SEARCH_PATH = master;
-- Disable triggers for this session
SET SESSION_REPLICATION_ROLE = replica;

DELETE FROM agenda_vote_committee_vote;
DELETE FROM agenda_vote_committee_attend;
DELETE FROM agenda_vote_committee;
DELETE FROM agenda_vote_addendum;
DELETE FROM agenda_info_committee_item;
DELETE FROM agenda_info_committee;
DELETE FROM agenda_info_addendum;
DELETE FROM agenda;
DELETE FROM bill_veto;
DELETE FROM bill_sponsor_additional;
DELETE FROM bill_sponsor;
DELETE FROM bill_previous_version;
DELETE FROM bill_milestone;
DELETE FROM bill_committee;
DELETE FROM bill_approval;
DELETE FROM bill_amendment_vote_roll;
DELETE FROM bill_amendment_vote_info;
DELETE FROM bill_amendment_same_as;
DELETE FROM bill_amendment_publish_status;
DELETE FROM bill_amendment_multi_sponsor;
DELETE FROM bill_amendment_cosponsor;
DELETE FROM bill_amendment_action;
DELETE FROM bill_amendment;
DELETE FROM bill;
DELETE FROM calendar_supplemental_entry;
DELETE FROM calendar_supplemental;
DELETE FROM calendar_active_list_entry;
DELETE FROM calendar_active_list;
DELETE FROM calendar;
DELETE FROM sobi_fragment;
DELETE FROM sobi_file;

DELETE FROM calendar_change_log;
DELETE FROM bill_change_log;
DELETE FROM agenda_change_log;

VACUUM;

-- reenable triggers for this session
SET SESSION_REPLICATION_ROLE = default;
