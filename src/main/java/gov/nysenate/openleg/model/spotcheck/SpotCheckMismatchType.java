package gov.nysenate.openleg.model.spotcheck;

/**
 * Enumeration of the different bill fields that we can check for data quality issues.
 */
public enum SpotCheckMismatchType
{
    /** --- General --- */

    REFERENCE_DATA_MISSING,
    OBSERVE_DATA_MISSING,

    /** --- Bill data mismatches --- */

    BILL_ACTION,
    BILL_ACTIVE_AMENDMENT,
    BILL_AMENDMENT_PUBLISH,
    BILL_COSPONSOR,
    BILL_FULLTEXT_PAGE_COUNT,
    BILL_FULL_TEXT,
    BILL_LAW_CODE,
    BILL_LAW_CODE_SUMMARY,
    BILL_LAW_SECTION,
    BILL_MEMO,
    BILL_MULTISPONSOR,
    BILL_SESSION_YEAR,
    BILL_SPONSOR,
    BILL_SPONSOR_MEMO,
    BILL_SAMEAS,
    BILL_SUMMARY,
    BILL_TITLE,


    /** --- Active List data mismatches --- */

    LIST_CAL_DATE,
    LIST_RELEASE_DATE_TIME,
    LIST_CALENDAR_MISMATCH,
    LIST_ENTRY_MISMATCH,



}