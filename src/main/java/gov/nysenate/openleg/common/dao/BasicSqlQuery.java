package gov.nysenate.openleg.common.dao;

import static gov.nysenate.openleg.common.dao.SqlQueryUtils.*;

public interface BasicSqlQuery {
    /**
     * Return the sql query as is.
     */
    String getSql();

    /**
     * Overload of getSql(envSchema). Returns a sql string with a limit clause
     * appended to the end according to the supplied LimitOffset instance.
     */
    default String getSql(LimitOffset limitOffset) {
        return getSql() + getLimitOffsetClause(limitOffset);
    }

    /**
     * Overload of getSql(envSchema). Returns a sql string with an order by clause
     * appended to the end according to the supplied OrderBy instance.
     */
    default String getSql(OrderBy orderBy) {
        return getSql(orderBy, LimitOffset.ALL);
    }

    /**
     * Overload of getSql(envSchema, limitOffset). Returns a sql string with an
     * order by clause set according to the supplied OrderBy instance.
     */
    default String getSql(OrderBy orderBy, LimitOffset limitOffset) {
        return getSql() + getOrderByClause(orderBy) + getLimitOffsetClause(limitOffset);
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
                clause = String.format(" LIMIT %d", limitOffset.limit());
            }
            if (limitOffset.hasOffset()) {
                clause += String.format(" OFFSET %d", limitOffset.offsetStart() - 1);
            }
        }
        return clause;
    }
}
