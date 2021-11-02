package gov.nysenate.openleg.legislation;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum CacheType {
    AGENDA(true),
    BILL(true),
    BILL_INFO(true),
    CALENDAR(false),
    COMMITTEE(false),
    LAW(true),
    NOTIFICATION(false),
    API_USER(false),
    FULL_MEMBER(false),
    SHORTNAME(false), // Session Member with a different key
    SESSION_MEMBER(false),
    // TODO: SHIRO only has size 2?
    SHIRO(false);

    // Whether the number of units refers to megabytes or entries.
    private final boolean isElementSize;

    CacheType(boolean isElementSize) {
        this.isElementSize = isElementSize;
    }

    public boolean isElementSize() {
        return isElementSize;
    }
}
