package gov.nysenate.openleg.dao.base;

public interface BasicSqlQuery
{
    /**
     * Return the sql query as is.
     */
    public String getSql();

    /**
     * Retrieve a formatted sql String with the envSchema value replaced where
     * applicable. This is needed for allowing configurable schema names.
     */
    public default String getSql(String envSchema) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), envSchema);
    }

    /**
     * Overload of getSql(envSchema). Returns a sql string with a limit clause
     * appended to the end according to the supplied LimitOffset instance.
     */
    public default String getSql(String envSchema, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), envSchema, limitOffset);
    }

    /**
     * Overload of getSql(envSchema). Returns a sql string with an order by clause
     * appended to the end according to the supplied OrderBy instance.
     */
    public default String getSql(String envSchema, OrderBy orderBy) {
        return getSql(envSchema, orderBy, LimitOffset.ALL);
    }

    /**
     * Overload of getSql(envSchema, limitOffset). Returns a sql string with an
     * order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(String envSchema, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), envSchema, orderBy, limitOffset);
    }
}
