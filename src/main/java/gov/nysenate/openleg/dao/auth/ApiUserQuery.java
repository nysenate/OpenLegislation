package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum ApiUserQuery implements BasicSqlQuery
{
    // Query Declarations
    INSERT_API_USER(
            "INSERT INTO public." + SqlTable.API_USER + "(apikey, authenticated, num_requests, email_addr)" + "\n" +
            "VALUES (:apikey, :authenticated, :apiRequests, :email)"
    ),
    SELECT_BY_EMAIL(
        "SELECT * FROM public." +SqlTable.API_USER+ " WHERE email_addr = :email"
    ),
    SELECT_BY_KEY(
        "SELECT * FROM public." +SqlTable.API_USER+ " WHERE apikey = :apikey"
    );

    private String sql;
    ApiUserQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() { return this.sql; }
}
