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
        "SELECT u.apikey, u.authenticated, u.num_requests, u.email_addr, u.org_name, u.users_name, u.reg_token, " +
        "array(SELECT r.role FROM public." + SqlTable.API_USER_ROLE + " r WHERE r.apikey = u.apikey) AS roles, " +
        "array(SELECT s.subscription_type FROM public." + SqlTable.API_USER_SUBSCRIPTION +
            " s WHERE s.apikey = u.apikey) AS subscriptions\n" +
        "FROM public." + SqlTable.API_USER + " u\n"
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


    INSERT_API_USER_ROLE(
        "INSERT INTO public." + SqlTable.API_USER_ROLE + "\n" +
        "       (apikey,  role)\n" +
        "VALUES(:apiKey, :role)"
    ),

    DELETE_API_USER_ROLE(
        "DELETE FROM public." + SqlTable.API_USER_ROLE + "\n" +
        "WHERE apikey = :apiKey AND role = :role"
    ),

    INSERT_API_USER_SUBSCRIPTION(
        "INSERT INTO public." + SqlTable.API_USER_SUBSCRIPTION + "\n" +
        "      (apikey, subscription_type)\n" +
        "VALUES(:apiKey, :subscription_type::public.apiuser_subscription_type)"
    ),

    DELETE_API_USER_SUBSCRIPTION(
        "DELETE FROM public." + SqlTable.API_USER_SUBSCRIPTION + "\n" +
        "WHERE apikey = :apiKey AND subscription_type = :subscription_type::public.apiuser_subscription_type"
    ),

    ;

    private String sql;
    ApiUserQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() { return this.sql; }
}
