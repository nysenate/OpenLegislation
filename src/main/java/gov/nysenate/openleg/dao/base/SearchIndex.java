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
    NOTIFICATION("notifications"),
    API_LOG("apilog")
    ;

    String indexName;

    SearchIndex(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }
}
