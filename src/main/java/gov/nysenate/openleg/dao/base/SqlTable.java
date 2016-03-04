package gov.nysenate.openleg.dao.base;

/**
 * A complete enumeration of all the sql tables referenced by the application.
 * The schemas for these tables must be determined via the DAO/Query layer that
 * utilizes these tables.
 */
public enum SqlTable
{
    ACTIVE_LIST_REFERENCE        ("active_list_reference"),
    ACTIVE_LIST_REFERENCE_ENTRY  ("active_list_reference_entry"),

    ADMIN                        ("adminuser"),

    AGENDA                       ("agenda"),
    AGENDA_INFO_ADDENDUM         ("agenda_info_addendum"),
    AGENDA_INFO_COMMITTEE        ("agenda_info_committee"),
    AGENDA_INFO_COMMITTEE_ITEM   ("agenda_info_committee_item"),
    AGENDA_VOTE_ACTION           ("agenda_vote_action"),
    AGENDA_VOTE_ADDENDUM         ("agenda_vote_addendum"),
    AGENDA_VOTE_COMMITTEE        ("agenda_vote_committee"),
    AGENDA_VOTE_COMMITTEE_ATTEND ("agenda_vote_committee_attend"),
    AGENDA_VOTE_COMMITTEE_ITEM   ("agenda_vote_committee_item"),
    AGENDA_VOTE_COMMITTEE_VOTE   ("agenda_vote_committee_vote"),

    AGENDA_ALERT_INFO_COMMITTEE  ("agenda_alert_info_committee"),
    AGENDA_ALERT_INFO_COMMITTEE_ITEM ("agenda_alert_info_committee_item"),

    AGENDA_CHANGE_LOG            ("agenda_change_log"),

    API_REQUEST                  ("request"),
    API_RESPONSE                 ("response"),
    API_USER                     ("apiuser"),
    API_USER_ROLE                ("apiuser_roles"),

    BILL                         ("bill"),
    BILL_AMENDMENT               ("bill_amendment"),
    BILL_AMENDMENT_ACTION        ("bill_amendment_action"),
    BILL_AMENDMENT_COSPONSOR     ("bill_amendment_cosponsor"),
    BILL_AMENDMENT_MULTISPONSOR  ("bill_amendment_multi_sponsor"),
    BILL_AMENDMENT_SAME_AS       ("bill_amendment_same_as"),
    BILL_AMENDMENT_PUBLISH_STATUS("bill_amendment_publish_status"),
    BILL_AMENDMENT_VOTE_INFO     ("bill_amendment_vote_info"),
    BILL_AMENDMENT_VOTE_ROLL     ("bill_amendment_vote_roll"),
    BILL_APPROVAL                ("bill_approval"),
    BILL_COMMITTEE               ("bill_committee"),
    BILL_MULTI_SPONSOR           ("bill_multi_sponsor"),
    BILL_MILESTONE               ("bill_milestone"),
    BILL_PREVIOUS_VERSION        ("bill_previous_version"),
    BILL_SPONSOR                 ("bill_sponsor"),
    BILL_ADDITIONAL_SPONSOR      ("bill_sponsor_additional"),
    BILL_VETO                    ("bill_veto"),
    BILL_TEXT_REFERENCE          ("bill_text_reference"),
    BILL_SCRAPE_QUEUE            ("bill_scrape_queue"),
    BILL_ALTERNATE_PDF           ("bill_text_alternate_pdf"),

    BILL_CHANGE_LOG              ("bill_change_log"),

    CALENDAR                     ("calendar"),
    CALENDAR_ACTIVE_LIST         ("calendar_active_list"),
    CALENDAR_ACTIVE_LIST_ENTRY   ("calendar_active_list_entry"),
    CALENDAR_SUPPLEMENTAL        ("calendar_supplemental"),
    CALENDAR_SUP_ENTRY           ("calendar_supplemental_entry"),

    ALERT_CALENDAR_FILE          ("alert_calendar_file"),
    ALERT_CALENDAR               ("alert_calendar_reference"),
    ALERT_CALENDAR_ACTIVE_LIST   ("alert_active_list_reference"),
    ALERT_CALENDAR_ACTIVE_LIST_ENTRY ("alert_active_list_entry_reference"),
    ALERT_CALENDAR_SUPPLEMENTAL  ("alert_supplemental_reference"),
    ALERT_CALENDAR_SUP_ENTRY     ("alert_supplemental_entry_reference"),

    CALENDAR_CHANGE_LOG          ("calendar_change_log"),

    COMMITTEE                    ("committee"),
    COMMITTEE_VERSION            ("committee_version"),
    COMMITTEE_MEMBER             ("committee_member"),

    DATA_PROCESS_RUN             ("data_process_run"),
    DATA_PROCESS_UNIT            ("data_process_run_unit"),

    DAYBREAK_REPORT              ("daybreak_report"),
    DAYBREAK_FILE                ("daybreak_file"),
    DAYBREAK_FRAGMENT            ("daybreak_fragment"),
    DAYBREAK_PAGE_FILE_ENTRY     ("daybreak_page_file_entry"),
    DAYBREAK_BILL                ("daybreak_bill"),
    DAYBREAK_BILL_ACTION         ("daybreak_bill_action"),
    DAYBREAK_BILL_AMENDMENT      ("daybreak_bill_amendment"),
    DAYBREAK_BILL_SPONSOR        ("daybreak_bill_sponsor"),

    FLOOR_CALENDAR_REFERENCE       ("floor_calendar_reference"),
    FLOOR_CALENDAR_REFERENCE_ENTRY ("floor_calendar_reference_entry"),

    LAW_FILE                     ("law_file"),
    LAW_INFO                     ("law_info"),
    LAW_DOCUMENT                 ("law_document"),
    LAW_TREE                     ("law_tree"),

    LAW_CHANGE_LOG               ("law_change_log"),

    NOTIFICATION                 ("notification"),
    NOTIFICATION_SUBSCRIPTION    ("notification_subscription"),
    NOTIFICATION_DIGEST_SUBSCRIPTION("notification_digest_subscription"),

    MEMBER                       ("member"),
    PERSON                       ("person"),
    SESSION_MEMBER               ("session_member"),

    SOBI_FILE                    ("sobi_file"),
    SOBI_FRAGMENT                ("sobi_fragment"),

    SPOTCHECK_REPORT             ("spotcheck_report"),
    SPOTCHECK_OBSERVATION        ("spotcheck_observation"),
    SPOTCHECK_MISMATCH           ("spotcheck_mismatch"),
    SPOTCHECK_MISMATCH_IGNORE    ("spotcheck_mismatch_ignore"),
    SPOTCHECK_MISMATCH_ISSUE_ID  ("spotcheck_mismatch_issue_id"),

    TRANSCRIPT                   ("transcript"),
    TRANSCRIPT_FILE              ("transcript_file"),

    PUBLIC_HEARING               ("public_hearing"),
    PUBLIC_HEARING_ATTENDANCE    ("public_hearing_attendance"),
    PUBLIC_HEARING_COMMITTEE     ("public_hearing_committee"),
    PUBLIC_HEARING_FILE          ("public_hearing_file");

    String tableName;

    SqlTable(String tableName) {
        this.tableName = tableName;
    }

    public String table(String schema) {
        return schema + "." + tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
