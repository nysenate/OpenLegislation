CREATE INDEX IF NOT EXISTS daybreak_fragment_pending_processing_idx
    ON master.daybreak_fragment(pending_processing);

CREATE INDEX IF NOT EXISTS leg_data_fragment_leg_data_file_name_fragment_type_idx
    ON master.leg_data_fragment(leg_data_file_name, fragment_type);

CREATE INDEX IF NOT EXISTS leg_data_fragment_pending_processing_idx
    ON master.leg_data_fragment(pending_processing);

CREATE INDEX IF NOT EXISTS alert_supplemental_entry_reference_calendar_sup_id_idx
    ON master.alert_supplemental_entry_reference(calendar_sup_id);

CREATE INDEX IF NOT EXISTS agenda_vote_committee_attend_vote_committee_id_idx
    ON master.agenda_vote_committee_attend(vote_committee_id);

CREATE INDEX IF NOT EXISTS bill_scrape_file_is_archived_idx
    ON master.bill_scrape_file(is_archived);

CREATE INDEX IF NOT EXISTS agenda_info_committee_item_info_committee_id_idx
    ON master.agenda_info_committee_item(info_committee_id);

CREATE INDEX IF NOT EXISTS agenda_vote_committee_vote_vote_committee_id_idx
    ON master.agenda_vote_committee_vote(vote_committee_id);

CREATE INDEX IF NOT EXISTS agenda_alert_info_committee_item_alert_info_commitee_id_idx
    ON master.agenda_alert_info_committee_item(alert_info_committee_id);

CREATE INDEX IF NOT EXISTS transcript_modified_date_time_idx
    ON master.transcript(modified_date_time);

CREATE INDEX IF NOT EXISTS transcript_file_pending_processing_idx
    ON master.transcript_file(pending_processing);

CREATE INDEX IF NOT EXISTS bill_veto_session_year_bill_print_no_idx
    ON master.bill_veto(bill_session_year, bill_print_no);

CREATE INDEX IF NOT EXISTS session_member_session_year_member_id_alternate_idx
    ON public.session_member(session_year, member_id, alternate);
