package gov.nysenate.openleg.search;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enumeration of all the search indices.
 */
public enum SearchIndex {
    BILL("bills"),
    AGENDA("agendas"),
    CALENDAR("calendars"),
    COMMITTEE("committees"),
    TRANSCRIPT("transcripts"),
    HEARING("hearings"),
    LAW("laws"),
    MEMBER("members"),
    NOTIFICATION("notifications", true),
    API_LOG("apilog", true)
    ;

    private final String indexName;
    private boolean primaryStore = false;

    SearchIndex(String indexName) {
        this.indexName = indexName;
    }

    SearchIndex(String indexName, boolean primaryStore) {
        this(indexName);
        this.primaryStore = primaryStore;
    }

    public static final Set<SearchIndex> nonPrimaryIndices = Arrays.stream(values())
            .filter(index -> !index.isPrimaryStore()).collect(Collectors.toSet());

    public String getIndexName() {
        return indexName;
    }

    public boolean isPrimaryStore() {
        return primaryStore;
    }
}
