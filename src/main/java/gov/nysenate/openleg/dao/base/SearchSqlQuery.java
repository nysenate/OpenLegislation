package gov.nysenate.openleg.dao.base;

public interface SearchSqlQuery
{
    /**
     * Return the sql query as is.
     */
    public String getSql();

    /**
     * Retrieve a formatted sql String with the dataSchema and searchSchema values
     * replaced where applicable. This is needed for allowing configurable schema names.
     */
    public default String getSql(String dataSchema, String searchSchema) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), dataSchema, searchSchema);
    }

    /**
     * Overload of getSql(dataSchema, searchSchema). Returns a sql string with a limit clause
     * appended to the end according to the supplied LimitOffset instance.
     */
    public default String getSql(String dataSchema, String searchSchema, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), dataSchema, searchSchema, limitOffset);
    }

    /**
     * Overload of getSql(dataSchema, searchSchema, limitOffset). Returns a sql string with an
     * order by clause set according to the supplied OrderBy instance.
     */
    public default String getSql(String dataSchema, String searchSchema, OrderBy orderBy, LimitOffset limitOffset) {
        return SqlQueryUtils.getSqlWithSchema(getSql(), dataSchema, searchSchema, orderBy, limitOffset);
    }
}