package gov.nysenate.openleg.spotchecks;

/**
 * An Enumeration of spotcheck mismatch fields that can be ordered by.
 *
 * <p>Provides a consistent user friendly constant which maps to
 * columns in SqlSpotCheckReportQuery queries.
 *
 * <p>Sql queries in SqlSpotCheckReportQuery return mismatch contentKey's
 * as an hstore value. To order by keys of an hstore you have to access
 * the key like: <code>key->'print_no'</code>.
 */
public enum MismatchOrderBy {
    /* Common fields */
    STATUS("status"),
    MISMATCH_TYPE("type"),
    DATASOURCE("datasource"),
    REFERENCE_TYPE("reference_type"),
    REFERENCE_DATE("reference_active_date_time"),
    OBSERVED_DATE("observed_date_time"),
    ISSUE("issue_ids"),

    /* Bill fields */
    PRINT_NO("key->'print_no'"),
    SESSION_YEAR("(key->'session_year')::smallint"),

    /* Calendar Fields */
    CAL_YEAR("(key->'year')::smallint"),
    CAL_NO("(key->'calNo')::smallint"),
    CAL_TYPE("key->'type'"),

    /* Agenda Fields */
    AGENDA_YEAR("(key->'year')::smallint"),
    AGENDA_NO("(key->'agendaNo')::smallint"),
    AGENDA_WEEK_OF("(key->'weekOf')::smallint"),
    AGENDA_COMMITTEE("key->'committee_name'"),

    /* Law Fields */
    LAW_CHAPTER("key->'law_chapter'"),
    LAW_LOC_ID("key->'location_id'"),
    ;

    private final String columnName;

    MismatchOrderBy(String colName) {
        this.columnName = colName;
    }

    public String getColumnName() {
        return columnName;
    }
}
