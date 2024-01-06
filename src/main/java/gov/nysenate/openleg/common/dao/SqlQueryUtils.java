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
     * Replaces the ${schema} placeholder in the given sql String with the given schema name.
     * This is mainly used for queries where the schema name can be user defined, e.g. the environment schema.
     *
     * @param sql String - A string that contains the ${schema} placeholders.
     * @param schema String - The name of the database schema
     * @return String
     */
    public static String getSqlWithSchema(String sql, String schema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", schema);
        return new StringSubstitutor(replaceMap).replace(sql);
    }

    /**
     * Adds support for a search schema as well.
     */
    public static String getSqlWithSchema(String sql, String dataSchema, String searchSchema) {
        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("schema", dataSchema);
        replaceMap.put("search_schema", searchSchema);
        return new StringSubstitutor(replaceMap).replace(sql);
    }

    /**
     * Overloaded to add LIMIT clause to getSqlWithSchema(sql, schema) output.
     */
    public static String getSqlWithSchema(String sql, String schema, LimitOffset limitOffset) {
        return getSqlWithSchema(sql, schema) + getLimitOffsetClause(limitOffset);
    }

    /**
     * Overloaded to add LIMIT clause to getSqlWithSchema(sql, dataSchema, searchSchema) output.
     */
    public static String getSqlWithSchema(String sql, String dataSchema, String searchSchema, LimitOffset limitOffset) {
        return getSqlWithSchema(sql, dataSchema, searchSchema) + getLimitOffsetClause(limitOffset);
    }

    /**
     * Overloaded to add LIMIT AND ORDER BY clause to getSqlWithSchema(sql, schema) output.
     */
    public static String getSqlWithSchema(String sql, String schema, OrderBy orderBy, LimitOffset limitOffset) {
        return getSqlWithSchema(sql, schema) + getOrderByClause(orderBy) + getLimitOffsetClause(limitOffset);
    }

    /**
     * Overloaded to add LIMIT AND ORDER BY clause to getSqlWithSchema(sql, dataSchema, searchSchema) output.
     */
    public static String getSqlWithSchema(String sql, String dataSchema, String searchSchema, OrderBy orderBy,
                                          LimitOffset limitOffset) {
        return getSqlWithSchema(sql, dataSchema, searchSchema) + getOrderByClause(orderBy) + getLimitOffsetClause(limitOffset);
    }

    /**
     * Add LIMIT AND ORDER BY clause to SQL statement.
     */
    public static String addOrderAndLimitOffset(String sql, OrderBy orderBy, LimitOffset limitOffset) {
        return sql + getOrderByClause(orderBy) + getLimitOffsetClause(limitOffset);
    }

    /**
     * Returns a LIMIT OFFSET sql clause using the supplied LimitOffset instance.
     * If neither the limit nor the offset is set an empty string will be returned.
     *
     * @param limitOffset LimitOffset
     * @return String
     */
    private static String getLimitOffsetClause(LimitOffset limitOffset) {
        String clause = "";
        if (limitOffset != null) {
            if (limitOffset.hasLimit()) {
                clause = String.format(" LIMIT %d", limitOffset.getLimit());
            }
            if (limitOffset.hasOffset()) {
                clause += String.format(" OFFSET %d", limitOffset.getOffsetStart() - 1);
            }
        }
        return clause;
    }

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
