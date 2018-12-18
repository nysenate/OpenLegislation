package gov.nysenate.openleg.dao.base;

/**
 * Enumeration of all the search indices.
 */
public enum SearchIndex
{
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

    String indexName;
    boolean primaryStore = false;

    SearchIndex(String indexName) {
        this.indexName = indexName;
    }

    SearchIndex(String indexName, boolean primaryStore) {
        this(indexName);
        this.primaryStore = primaryStore;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isPrimaryStore() {
        return primaryStore;
    }
}
