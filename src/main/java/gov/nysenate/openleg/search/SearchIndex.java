package gov.nysenate.openleg.search;

import gov.nysenate.openleg.common.dao.LimitOffset;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of all the search indices.
 */
public enum SearchIndex {
    AGENDA("agendas", LimitOffset.ALL),
    BILL("bills", LimitOffset.TEN),
    CALENDAR("calendars", LimitOffset.ALL),
    COMMITTEE("committees", LimitOffset.ALL),
    TRANSCRIPT("transcripts", LimitOffset.TEN),
    HEARING("hearings", LimitOffset.TEN),
    LAW("laws", LimitOffset.TWENTY_FIVE),
    MEMBER("members", LimitOffset.TWENTY_FIVE),
    NOTIFICATION("notifications", true, LimitOffset.ALL),
    API_LOG("apilog", true, LimitOffset.FIFTY);

    private final String indexName;
    private final boolean primaryStore;
    private final LimitOffset defaultLimitOffset;

    SearchIndex(String indexName, LimitOffset defaultLimitOffset) {
        this(indexName, false, defaultLimitOffset);
    }

    SearchIndex(String indexName, boolean primaryStore, LimitOffset defaultLimitOffset) {
        this.indexName = indexName;
        this.primaryStore = primaryStore;
        this.defaultLimitOffset = defaultLimitOffset;
    }

    public static final Set<SearchIndex> nonPrimaryIndices = Arrays.stream(values())
            .filter(index -> !index.isPrimaryStore()).collect(Collectors.toSet());

    public String getName() {
        return indexName;
    }

    public boolean isPrimaryStore() {
        return primaryStore;
    }

    public LimitOffset getDefaultLimitOffset() {
        return defaultLimitOffset;
    }
}
