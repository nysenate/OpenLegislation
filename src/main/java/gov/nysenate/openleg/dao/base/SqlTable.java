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
    BILL_AMENDMENT_VOTE          ("bill_amendment_vote"),
    BILL_MULTI_SPONSOR           ("bill_multi_sponsor"),
    BILL_PREVIOUS_VERSION        ("bill_previous_version"),

    CALENDAR                     ("calendar"),
    CALENDAR_ACTIVE_LIST         ("calendar_active_list"),
    CALENDAR_ACTIVE_LIST_ENTRY   ("calendar_active_list_entry"),
    CALENDAR_SUPPLEMENTAL        ("calendar_supplemental"),
    CALENDAR_SUPP_SECTION        ("calendar_supplemental_section"),
    CALENDAR_SUPP_SECTION_ENTRY  ("calendar_supplemental_section_entry"),

    COMMITTEE                    ("committee"),

    MEMBER                       ("member"),
    PERSON                       ("person"),
    SESSION_MEMBER               ("session_member"),

    SOBI_FILE                    ("sobi_file"),
    SOBI_FRAGMENT                ("sobi_fragment"),
    SOBI_CHANGE_LOG              ("sobi_change_log");

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
