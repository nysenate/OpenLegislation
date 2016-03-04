package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum ApiUserQuery implements BasicSqlQuery
{
    INSERT_API_USER(
        "INSERT INTO public." + SqlTable.API_USER +
        "(apikey, authenticated, num_requests, email_addr, org_name, users_name, reg_token)\n" +
        "VALUES (:apikey, :authenticated, :apiRequests, :email, :organizationName, :name, :registrationToken)"
    ),
    UPDATE_API_USER(
        "UPDATE public." + SqlTable.API_USER + "\n" +
        "SET apikey = :apikey, authenticated = :authenticated, num_requests = :apiRequests," +
            "users_name = :name, org_name = :organizationName, reg_token = :registrationToken" + "\n" +
        "WHERE email_addr = :email"
    ),
    SELECT_API_USERS(
        "SELECT u.apikey, u.authenticated, u.num_requests, u.email_addr, u.org_name, u.users_name, u.reg_token,\n" +
        "   r.role\n" +
        "FROM public." + SqlTable.API_USER + " u\n" +
        "LEFT JOIN public." + SqlTable.API_USER_ROLE + " r\n" +
        "   ON u.apikey = r.apikey\n"
    ),
    SELECT_BY_EMAIL(
        SELECT_API_USERS.getSql() + "WHERE u.email_addr ILIKE :email"
    ),
    SELECT_BY_KEY(
        SELECT_API_USERS.getSql() + "WHERE u.apikey = :apikey"
    ),
    SELECT_BY_TOKEN(
        SELECT_API_USERS.getSql() + "WHERE u.reg_token = :registrationToken"
    ),
    DELETE_USER(
        "DELETE FROM public." + SqlTable.API_USER + " WHERE email_addr = :email"
    ),

    INSERT_API_USER_ROLE(
        "INSERT INTO public." + SqlTable.API_USER_ROLE + "\n" +
        "       (apikey,  role)\n" +
        "VALUES(:apiKey, :role)"
    ),

    DELETE_API_USER_ROLE(
        "DELETE FROM public." + SqlTable.API_USER_ROLE + "\n" +
        "WHERE apikey = :apiKey AND role = :role"
    ),

    ;

    private String sql;
    ApiUserQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() { return this.sql; }
}
