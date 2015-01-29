package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum ApiUserQuery implements BasicSqlQuery
{
    // Query Declarations
    INSERT_API_USER(
            "INSERT INTO public." + SqlTable.API_USER + "(apikey, authenticated, num_requests, email_addr, org_name, users_name)" + "\n" +
            "VALUES (:apikey, :authenticated, :apiRequests, :email, :name, :organizationName)"
    ),
    UPDATE_API_USER(
            "UPDATE public." + SqlTable.API_USER + "\n" +
            "SET apikey = :apikey, authenticated = :authenticated, num_requests = :apiRequests," +
                "users_name = :name, org_name = :organizationName" + "\n" +
            "WHERE email_addr = :email"
    ),
    SELECT_BY_EMAIL(
        "SELECT * FROM public." +SqlTable.API_USER+ " WHERE email_addr = :email"
    ),
    SELECT_BY_KEY(
        "SELECT * FROM public." +SqlTable.API_USER+ " WHERE apikey = :apikey"
    ),
    SELECT_BY_NAME(
            "SELECT * FROM public." +SqlTable.API_USER+ " WHERE users_name = :name"
    ),
    SELECT_BY_ORGANIZATION(
            "SELECT * FROM public." +SqlTable.API_USER+ " WHERE org_name = :organizationName"
    ),
    DELETE_USER(
        "DELETE FROM public." +SqlTable.API_USER+ " WHERE email_addr = :email"
    );

    private String sql;
    ApiUserQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() { return this.sql; }
}
