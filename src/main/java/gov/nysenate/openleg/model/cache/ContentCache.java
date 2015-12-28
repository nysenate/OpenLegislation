package gov.nysenate.openleg.model.cache;

import com.google.common.collect.ImmutableSet;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum ContentCache
{
    BILL,
    BILL_INFO,
    AGENDA,
    CALENDAR,
    LAW,
    COMMITTEE,
    MEMBER,
    APIUSER,
    NOTIFICATION_SUBSCRIPTION;

    private static final ImmutableSet<ContentCache> allContentCaches = ImmutableSet.copyOf(ContentCache.values());

    public static ImmutableSet<ContentCache> getAllContentCaches() {
        return allContentCaches;
    }
}