package gov.nysenate.openleg.search;

import gov.nysenate.openleg.common.dao.LimitOffset;

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
    MEMBER("members", LimitOffset.TWENTY_FIVE);

    private final String indexName;
    private final LimitOffset defaultLimitOffset;

    SearchIndex(String indexName, LimitOffset defaultLimitOffset) {
        this.indexName = indexName;
        this.defaultLimitOffset = defaultLimitOffset;
    }

    public String getName() {
        return indexName;
    }

    public LimitOffset getDefaultLimitOffset() {
        return defaultLimitOffset;
    }
}
