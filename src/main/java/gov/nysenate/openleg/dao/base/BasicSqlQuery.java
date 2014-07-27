package gov.nysenate.openleg.dao.base;

public interface BasicSqlQuery
{
    /**
     * Retrieve a formatted sql String with the envSchema value replaced where
     * applicable. This is needed for allowing configurable schema names.
     */
    public String getSql(String envSchema);

    /**
     * Overload of getSql(envSchema). Returns a sql string with a limit clause
     * appended to the end according to the supplied LimitOffset instance.
     */
    public String getSql(String envSchema, LimitOffset limitOffset);

    /**
     * Overload of getSql(envSchema, limitOffset). Returns a sql string with an
     * order by clause set according to the supplied OrderBy instance.
     */
    public String getSql(String envSchema, OrderBy orderBy, LimitOffset limitOffset);
}
