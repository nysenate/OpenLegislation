package gov.nysenate.openleg.legislation;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum CacheType {
    AGENDA, API_USER, BILL, BILL_INFO, CALENDAR, COMMITTEE, FULL_MEMBER, LAW,
    SESSION_MEMBER, SHORTNAME
}
