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
    CALENDAR(true),
    LAW(true),
    SESSION_MEMBER(true),
    SHORTNAME(true),

    API_USER(false),
    COMMITTEE(false),
    FULL_MEMBER(false),
    NOTIFICATION(false),
    SHIRO(false);

    // Whether the number of units refers to entries (true) or megabytes (false).
    private final boolean isSizedByEntries;

    CacheType(boolean isSizedByEntries) {
        this.isSizedByEntries = isSizedByEntries;
    }

    public boolean isSizedByEntries() {
        return isSizedByEntries;
    }
}
