package gov.nysenate.openleg.model.spotcheck;

/**
 * Enumeration of the types of sources that can provide data for QA purposes.
 */
public enum SpotCheckRefType
{
    LBDC_DAYBREAK,             // DayBreaks consist of a set of files sent by LBDC weekly
                               // which consist of a dump of basic information for all bills
                               // that are active in the current session.
    LBDC_FLOOR_CALENDAR,

    LBDC_ACTIVE_LIST,

    LBDC_AGENDA_ALERT,


}