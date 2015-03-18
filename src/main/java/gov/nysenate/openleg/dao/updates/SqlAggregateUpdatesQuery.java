package gov.nysenate.openleg.dao.updates;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.updates.UpdateContentType;
import gov.nysenate.openleg.model.updates.UpdateReturnType;
import gov.nysenate.openleg.model.updates.UpdateType;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Map;
import java.util.Set;

public enum SqlAggregateUpdatesQuery implements BasicSqlQuery {

    SELECT_AGGREGATE_UPDATES(
        "SELECT *, COUNT(*) OVER () AS total_updated\n" +
        "FROM (\n" +
            "%s\n" +
        ") AS aggregate_query\n"
    ),

    /** --- Standard Table Templates --- */

    STANDARD_UPDATE_SUBQUERY(
        "\tSELECT %s AS id,\n" +            // id selector e.g. ARRAY['id_col1', id_val1, 'id_col2', id_val2, ...]
        "\t\t'%s' as content_type, %s\n" +  // content type, column replace string e.g. "${sobiColumns}"
        "\tFROM ${schema}.%s\n" +           // table name
        "\tWHERE ${dateColumn} BETWEEN :startDateTime AND :endDateTime"
    ),
    STANDARD_DIGEST_COLUMNS(
        "%s AS last_source_id, action_date_time AS last_processed_date_time, \n" +
        "\t\tpublished_date_time AS last_published_date_time,\n" +
        "\t\ttable_name, action"
    ),
    STANDARD_TOKEN_COLUMNS(
        "MAX(%s) AS last_source_id, MAX(action_date_time) AS last_processed_date_time, \n" +
        "\t\tMAX(published_date_time) AS last_published_date_time"
    ),
    SOBI_DIGEST_COLUMNS(
        String.format(STANDARD_DIGEST_COLUMNS.sql, "sobi_fragment_id")
    ),
    SOBI_DETAIL_DIGEST_COLUMNS(
        SOBI_DIGEST_COLUMNS.sql + ", hstore_to_array(data) AS data"
    ),
    SOBI_TOKEN_COLUMNS(
        String.format(STANDARD_TOKEN_COLUMNS.sql, "sobi_fragment_id")
    ),

    /** --- Agenda Update Subquery --- */

    AGENDA_UPDATE_SUBQUERY(
        String.format(STANDARD_UPDATE_SUBQUERY.sql,
            "ARRAY['agendaNumber', agenda_no::text, 'year', year::text]",
            "AGENDA", "${sobiColumns}", SqlTable.AGENDA_CHANGE_LOG)
    ),
    AGENDA_UPDATE_TOKEN_SUBQUERY(
        AGENDA_UPDATE_SUBQUERY.sql + "\n\tGROUP BY agenda_no, year"
    ),

    /** --- Bill Update Subquery --- */

    BILL_UPDATE_SUBQUERY(
        String.format(STANDARD_UPDATE_SUBQUERY.sql,
            "ARRAY['printNo', bill_print_no, 'session', bill_session_year::text]",
            "BILL", "${sobiColumns}", SqlTable.BILL_CHANGE_LOG)
    ),
    BILL_UPDATE_TOKEN_SUBQUERY(
        BILL_UPDATE_SUBQUERY.sql + "\n\tGROUP BY bill_print_no, bill_session_year"
    ),

    /** --- Calendar Update Subquery --- */

    CALENDAR_UPDATE_SUBQUERY(
        String.format(STANDARD_UPDATE_SUBQUERY.sql,
            "ARRAY['calNo', calendar_no::text, 'year', calendar_year::text]",
            "CALENDAR", "${sobiColumns}", SqlTable.CALENDAR_CHANGE_LOG)
    ),
    CALENDAR_UPDATE_TOKEN_SUBQUERY(
        CALENDAR_UPDATE_SUBQUERY.sql + "\n\tGROUP BY calendar_no, calendar_year"
    ),

    /** --- Law Update Subquery --- */

    LAW_DIGEST_COLUMNS(
        String.format(STANDARD_DIGEST_COLUMNS.sql, "law_file_name")
    ),
    LAW_DETAIL_DIGEST_COLUMNS(
        LAW_DIGEST_COLUMNS.sql + ", ARRAY[]::text[] AS data"
    ),
    LAW_TOKEN_COLUMNS(
        String.format(STANDARD_TOKEN_COLUMNS.sql, "law_file_name")
    ),

    LAW_UPDATE_SUBQUERY(
        String.format(STANDARD_UPDATE_SUBQUERY.sql,
            "ARRAY['lawDocId', document_id, 'publishedDate', published_date_time::date::text]",
            "LAW", "${lawColumns}", SqlTable.LAW_CHANGE_LOG)
    ),
    LAW_UPDATE_TOKEN_SUBQUERY(
        String.format(STANDARD_UPDATE_SUBQUERY.sql,
            "ARRAY['lawId', law_id, 'publishedDate', MAX(published_date_time)::date::text]",
            "LAW", "${lawColumns}", SqlTable.LAW_CHANGE_LOG) + "\n\tGROUP BY law_id"
    ),
    ;

    protected String sql;

    SqlAggregateUpdatesQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    /**
     * Generates and returns a query string based on the given parameters
     *
     * @param schema String - The name of the master schema
     * @param limOff LimitOffset - Limit Offset for the query
     * @param order OrderBy - Ordering for the query
     * @param contentTypes Set<UpdateContentType> - The update content types to be retrieved
     * @param returnType UpdateReturnType - The desired update return type
     * @param updateType UpdateType - Determines which date column is used in the query
     * @return String - An aggregate updates query string
     */
    public static String buildQuery(String schema, LimitOffset limOff, SortOrder order,
                                    Set<UpdateContentType> contentTypes, UpdateReturnType returnType, UpdateType updateType) {
        OrderBy orderBy = new OrderBy(updateType == UpdateType.PROCESSED_DATE ? "last_processed_date_time" : "last_published_date_time", order);
        String aggregateQuery = String.format(
                SELECT_AGGREGATE_UPDATES.getSql(schema, orderBy, limOff),
                generateSubquery(contentTypes, returnType, schema));
        Map<String, String> replaceMap = ImmutableMap.<String, String>builder()
                .putAll(getColumnReplaceMap(returnType))
                .put("dateColumn", updateType == UpdateType.PROCESSED_DATE ? "action_date_time" : "published_date_time")
                .build();
        aggregateQuery = StrSubstitutor.replace(aggregateQuery, replaceMap);
        return aggregateQuery;
    }

    /**
     * Generates a subquery containing the union of updates queries for each represented content type
     */
    private static String generateSubquery(Set<UpdateContentType> contentTypes, UpdateReturnType returnType, String schema) {
        StringBuilder subqueryBuilder = new StringBuilder();
        boolean first = true;
        for (UpdateContentType contentType : contentTypes) {
            if (first) { first = false; }
            else { subqueryBuilder.append("\nUNION ALL\n"); }
            switch (contentType) {
                case AGENDA:
                    if (returnType == UpdateReturnType.TOKEN) {
                        subqueryBuilder.append(AGENDA_UPDATE_TOKEN_SUBQUERY.getSql(schema));
                    } else {
                        subqueryBuilder.append(AGENDA_UPDATE_SUBQUERY.getSql(schema));
                    } break;
                case BILL:
                    if (returnType == UpdateReturnType.TOKEN) {
                        subqueryBuilder.append(BILL_UPDATE_TOKEN_SUBQUERY.getSql(schema));
                    } else {
                        subqueryBuilder.append(BILL_UPDATE_SUBQUERY.getSql(schema));
                    } break;
                case CALENDAR:
                    if (returnType == UpdateReturnType.TOKEN) {
                        subqueryBuilder.append(CALENDAR_UPDATE_TOKEN_SUBQUERY.getSql(schema));
                    } else {
                        subqueryBuilder.append(CALENDAR_UPDATE_SUBQUERY.getSql(schema));
                    } break;
                case LAW:
                    if (returnType == UpdateReturnType.TOKEN) {
                        subqueryBuilder.append(LAW_UPDATE_TOKEN_SUBQUERY.getSql(schema));
                    } else {
                        subqueryBuilder.append(LAW_UPDATE_SUBQUERY.getSql(schema));
                    } break;
            }
        }
        return subqueryBuilder.toString();
    }

    /**
     * Returns a string subsitution map that can substitute in the correct columns based on the desired return type
     */
    private static Map<String, String> getColumnReplaceMap(UpdateReturnType returnType) {
        switch (returnType) {
            case TOKEN:
                return ImmutableMap.of("sobiColumns", SOBI_TOKEN_COLUMNS.sql, "lawColumns", LAW_TOKEN_COLUMNS.sql);
            case DIGEST:
                return ImmutableMap.of("sobiColumns", SOBI_DIGEST_COLUMNS.sql, "lawColumns", LAW_DIGEST_COLUMNS.sql);
            case DETAIL_DIGEST:
                return ImmutableMap.of("sobiColumns", SOBI_DETAIL_DIGEST_COLUMNS.sql, "lawColumns", LAW_DETAIL_DIGEST_COLUMNS.sql);
        }
        return ImmutableMap.of();
    }
}
