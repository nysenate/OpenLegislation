
SET SEARCH_PATH = master;

ALTER TABLE ONLY agenda_info_addendum
  DROP CONSTRAINT agenda_info_addendum_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_info_addendum
  ADD CONSTRAINT agenda_info_addendum_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);


ALTER TABLE ONLY agenda_info_committee_item
  DROP CONSTRAINT agenda_info_committee_item_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_info_committee_item
  ADD CONSTRAINT agenda_info_committee_item_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY agenda_info_committee
  DROP CONSTRAINT agenda_info_committee_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_info_committee
  ADD CONSTRAINT agenda_info_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY agenda
  DROP CONSTRAINT agenda_last_fragment_id_fkey ;
ALTER TABLE ONLY agenda
  ADD CONSTRAINT agenda_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY agenda_vote_addendum
  DROP CONSTRAINT agenda_vote_addendum_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_vote_addendum
  ADD CONSTRAINT agenda_vote_addendum_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY agenda_vote_committee_attend
  DROP CONSTRAINT agenda_vote_committee_attend_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_vote_committee_attend
  ADD CONSTRAINT agenda_vote_committee_attend_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);


ALTER TABLE ONLY agenda_vote_committee_attend
  DROP CONSTRAINT agenda_vote_committee_attend_session_member_id_fkey;
ALTER TABLE ONLY agenda_vote_committee_attend
  ADD CONSTRAINT agenda_vote_committee_attend_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);

ALTER TABLE ONLY agenda_vote_committee
  DROP CONSTRAINT agenda_vote_committee_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_vote_committee
  ADD CONSTRAINT agenda_vote_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);


ALTER TABLE ONLY agenda_vote_committee_vote
  DROP CONSTRAINT agenda_vote_committee_vote_last_fragment_id_fkey;
ALTER TABLE ONLY agenda_vote_committee_vote
  ADD CONSTRAINT agenda_vote_committee_vote_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_action
  DROP CONSTRAINT bill_amendment_action_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_action
  ADD CONSTRAINT bill_amendment_action_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);


ALTER TABLE ONLY bill_amendment_cosponsor
  DROP CONSTRAINT bill_amendment_cosponsor_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_cosponsor
  ADD CONSTRAINT bill_amendment_cosponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_cosponsor
  DROP CONSTRAINT bill_amendment_cosponsor_session_member_id_fkey1;
ALTER TABLE ONLY bill_amendment_cosponsor
  ADD CONSTRAINT bill_amendment_cosponsor_session_member_id_fkey1 FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);

ALTER TABLE ONLY bill_amendment
  DROP CONSTRAINT bill_amendment_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment
  ADD CONSTRAINT bill_amendment_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_multi_sponsor
  DROP CONSTRAINT bill_amendment_multi_sponsor_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_multi_sponsor
  ADD CONSTRAINT bill_amendment_multi_sponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_multi_sponsor
  DROP CONSTRAINT bill_amendment_multi_sponsor_member_id_fkey;
ALTER TABLE ONLY bill_amendment_multi_sponsor
  ADD CONSTRAINT bill_amendment_multi_sponsor_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);

ALTER TABLE ONLY bill_amendment_publish_status
  DROP CONSTRAINT bill_amendment_publish_status_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_publish_status
  ADD CONSTRAINT bill_amendment_publish_status_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_same_as
  DROP CONSTRAINT bill_amendment_same_as_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_same_as
  ADD CONSTRAINT bill_amendment_same_as_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_vote_info
  DROP CONSTRAINT bill_amendment_vote_info_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_vote_info
  ADD CONSTRAINT bill_amendment_vote_info_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_vote_roll
  DROP CONSTRAINT bill_amendment_vote_roll_last_fragment_id_fkey;
ALTER TABLE ONLY bill_amendment_vote_roll
  ADD CONSTRAINT bill_amendment_vote_roll_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_amendment_vote_roll
  DROP CONSTRAINT bill_amendment_vote_roll_session_member_id_fkey;
ALTER TABLE ONLY bill_amendment_vote_roll
  ADD CONSTRAINT bill_amendment_vote_roll_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);

ALTER TABLE ONLY bill_committee
  DROP CONSTRAINT bill_committee_last_fragment_id_fkey;
ALTER TABLE ONLY bill_committee
  ADD CONSTRAINT bill_committee_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill
  DROP CONSTRAINT bill_last_fragment_id_fkey;
ALTER TABLE ONLY bill
  ADD CONSTRAINT bill_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_milestone
  DROP CONSTRAINT bill_milestone_last_fragment_id_fkey;
ALTER TABLE ONLY bill_milestone
  ADD CONSTRAINT bill_milestone_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_previous_version
  DROP CONSTRAINT bill_previous_version_last_fragment_id_fkey;
ALTER TABLE ONLY bill_previous_version
  ADD CONSTRAINT bill_previous_version_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_sponsor
  DROP CONSTRAINT bill_sponsor_last_fragment_id_fkey;
ALTER TABLE ONLY bill_sponsor
  ADD CONSTRAINT bill_sponsor_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY bill_sponsor
  DROP CONSTRAINT bill_sponsor_session_member_id_fkey;
ALTER TABLE ONLY bill_sponsor
  ADD CONSTRAINT bill_sponsor_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);


ALTER TABLE ONLY bill_veto
  DROP CONSTRAINT bill_veto_last_fragment_id_fkey;
ALTER TABLE ONLY bill_veto
  ADD CONSTRAINT bill_veto_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY calendar_active_list
  DROP CONSTRAINT calendar_active_list_last_fragment_id_fkey;
ALTER TABLE ONLY calendar_active_list
  ADD CONSTRAINT calendar_active_list_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY calendar
  DROP CONSTRAINT calendar_last_fragment_id_fkey;
ALTER TABLE ONLY calendar
  ADD CONSTRAINT calendar_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY calendar_supplemental_entry
  DROP CONSTRAINT calendar_supplemental_entry_last_fragment_id_fkey;
ALTER TABLE ONLY calendar_supplemental_entry
  ADD CONSTRAINT calendar_supplemental_entry_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY calendar_supplemental
  DROP CONSTRAINT calendar_supplemental_last_fragment_id_fkey;
ALTER TABLE ONLY calendar_supplemental
  ADD CONSTRAINT calendar_supplemental_last_fragment_id_fkey FOREIGN KEY (last_fragment_id) REFERENCES sobi_fragment(fragment_id);

ALTER TABLE ONLY committee_member
  DROP CONSTRAINT committee_member_session_member_id_fkey;
ALTER TABLE ONLY committee_member
  ADD CONSTRAINT committee_member_session_member_id_fkey FOREIGN KEY (session_member_id) REFERENCES public.session_member(id);

SET SEARCH_PATH = public;

ALTER TABLE ONLY member
  DROP CONSTRAINT member_person_id_fkey;
ALTER TABLE ONLY member
  ADD CONSTRAINT member_person_id_fkey FOREIGN KEY (person_id) REFERENCES person(id);

ALTER TABLE ONLY session_member
  DROP CONSTRAINT session_member_member_id_fkey;
ALTER TABLE ONLY session_member
  ADD CONSTRAINT session_member_member_id_fkey FOREIGN KEY (member_id) REFERENCES member(id);


