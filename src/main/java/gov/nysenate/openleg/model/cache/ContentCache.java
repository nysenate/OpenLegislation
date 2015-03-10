package gov.nysenate.openleg.model.cache;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum ContentCache
{
    BILL,
    AGENDA,
    CALENDAR,
    LAW,
    COMMITTEE,
    MEMBER,
    APIUSER,
    NOTIFICATION_SUBSCRIPTION
}