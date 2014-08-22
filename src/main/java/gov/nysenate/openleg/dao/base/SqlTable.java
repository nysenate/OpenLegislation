package gov.nysenate.openleg.dao.base;

/**
 * A complete enumeration of all the sql tables referenced by the application.
 * The schemas for these tables must be determined via the DAO/Query layer that
 * utilizes these tables.
 */
public enum SqlTable
{
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

    BILL                         ("bill"),
    BILL_SPONSOR                 ("bill_sponsor"),
    BILL_COMMITTEE               ("bill_committee"),
    BILL_AMENDMENT               ("bill_amendment"),
    BILL_AMENDMENT_ACTION        ("bill_amendment_action"),
    BILL_AMENDMENT_COSPONSOR     ("bill_amendment_cosponsor"),
    BILL_AMENDMENT_MULTISPONSOR  ("bill_amendment_multi_sponsor"),
    BILL_AMENDMENT_SAME_AS       ("bill_amendment_same_as"),
    BILL_AMENDMENT_PUBLISH_STATUS("bill_amendment_publish_status"),
    BILL_AMENDMENT_VOTE_INFO     ("bill_amendment_vote_info"),
    BILL_AMENDMENT_VOTE_ROLL     ("bill_amendment_vote_roll"),
    BILL_MULTI_SPONSOR           ("bill_multi_sponsor"),
    BILL_PREVIOUS_VERSION        ("bill_previous_version"),
    BILL_VETO                    ("bill_veto"),

    CALENDAR                     ("calendar"),
    CALENDAR_ACTIVE_LIST         ("calendar_active_list"),
    CALENDAR_ACTIVE_LIST_ENTRY   ("calendar_active_list_entry"),
    CALENDAR_SUPPLEMENTAL        ("calendar_supplemental"),
    CALENDAR_SUP_ENTRY           ("calendar_supplemental_entry"),

    COMMITTEE                    ("committee"),
    COMMITTEE_VERSION            ("committee_version"),
    COMMITTEE_MEMBER             ("committee_member"),

    DAYBREAK_REPORT              ("daybreak_report"),
    DAYBREAK_FILE                ("daybreak_file"),
    DAYBREAK_FRAGMENT            ("daybreak_fragment"),
    DAYBREAK_PAGE_FILE_ENTRY     ("daybreak_page_file_entry"),
    DAYBREAK_BILL                ("daybreak_bill"),
    DAYBREAK_BILL_ACTION         ("daybreak_bill_action"),
    DAYBREAK_BILL_AMENDMENT      ("daybreak_bill_amendment"),
    DAYBREAK_BILL_SPONSOR        ("daybreak_bill_sponsor"),

    MEMBER                       ("member"),
    PERSON                       ("person"),
    SESSION_MEMBER               ("session_member"),

    SOBI_FILE                    ("sobi_file"),
    SOBI_FRAGMENT                ("sobi_fragment"),
    SOBI_CHANGE_LOG              ("sobi_change_log"),

    SPOTCHECK_REPORT             ("spotcheck_report"),
    SPOTCHECK_OBSERVATION        ("spotcheck_observation"),
    SPOTCHECK_MISMATCH           ("spotcheck_mismatch");

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
}
