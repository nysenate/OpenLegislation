package gov.nysenate.openleg.legislation;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum CacheType {
    AGENDA(true),
    BILL(true, false),
    BILL_INFO(true, false),
    CALENDAR(true),
    LAW(true),
    SESSION_MEMBER(true),
    SHORTNAME(true),

    API_USER(false, false),
    COMMITTEE(false),
    FULL_MEMBER(false),
    NOTIFICATION(false),
    SHIRO(false);

    // Whether the number of units refers to entries (true) or megabytes (false).
    private final boolean isSizedByEntries;
    private final boolean warmOnStart;

    CacheType(boolean isSizedByEntries) {
        this(isSizedByEntries, true);
    }

    CacheType(boolean isSizedByEntries, boolean warmOnStart) {
        this.isSizedByEntries = isSizedByEntries;
        this.warmOnStart = warmOnStart;
    }

    public boolean isSizedByEntries() {
        return isSizedByEntries;
    }

    public boolean isWarmOnStart() {
        return warmOnStart;
    }
}
