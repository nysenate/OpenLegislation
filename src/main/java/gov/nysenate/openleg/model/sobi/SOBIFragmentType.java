package gov.nysenate.openleg.model.sobi;

/**
 * SOBIFragments are constructed to delineate a SOBI file into components based on a
 * common entity type. The SOBIFragmentType enum lists all the available entities
 * that a SOBIFragment can represent.
 */
public enum SOBIFragmentType
{
    BILL,
    AGENDA,
    AGENDA_VOTE,
    CALENDAR,
    CALENDAR_ACTIVE,
    COMMITTEE,
    ANNOTATION
}
