package gov.nysenate.openleg.dao.base;

/**
 * Enumeration of all the search indices.
 */
public enum SearchIndex
{
    BILL("bills"),
    AGENDA("agendas"),
    CALENDAR("calendars"),
    TRANSCRIPT("transcripts"),
    HEARING("hearings"),
    LAW("laws")
    ;

    String indexName;

    SearchIndex(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexName() {
        return indexName;
    }
}
