package gov.nysenate.openleg.common.dao;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common utility methods to be used by enums/classes that store sql queries.
 */
public final class SqlQueryUtils {
    private SqlQueryUtils() {}

    /**
     * Returns an ORDER BY sql clause using the supplied orderBy clause. Supports
     * multiple column orderings as specified in the OrderBy instance.
     *
     * @param orderBy OrderBy
     * @return String
     */
    static String getOrderByClause(OrderBy orderBy) {
        String clause = "";
        if (orderBy != null) {
            ImmutableMap<String, SortOrder> sortColumns = orderBy.getSortColumns();
            List<String> orderClauses = sortColumns.entrySet().stream().filter(entry -> entry.getValue() != SortOrder.NONE)
                    .map(entry -> entry.getKey() + " " + entry.getValue().name()).toList();
            if (!orderClauses.isEmpty()) {
                clause += " ORDER BY " + StringUtils.join(orderClauses, ", ");
            }
        }
        return clause;
    }
}
