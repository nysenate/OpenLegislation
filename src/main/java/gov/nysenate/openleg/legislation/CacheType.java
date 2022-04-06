package gov.nysenate.openleg.legislation;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
    SHORTNAME(true), // Session Member with a different key
    SESSION_MEMBER(false),
    SHIRO(false);

    private final static Set<String> ALL_TYPES = Arrays.stream(values()).map(Enum::name).collect(Collectors.toSet());
    // Whether the number of units refers to megabytes or entries.
    private final boolean isElementSize;

    CacheType(boolean isElementSize) {
        this.isElementSize = isElementSize;
    }

    public static Set<String> getAllTypes() {
        return ALL_TYPES;
    }

    public boolean isElementSize() {
        return isElementSize;
    }
}
