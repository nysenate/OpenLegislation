package gov.nysenate.openleg.auth.user;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

enum ApiUserQuery implements BasicSqlQuery {
    INSERT_API_USER(
            "INSERT INTO public." + SqlTable.API_USER +
                    "(apikey, authenticated, num_requests, email_addr, org_name, users_name, reg_token)\n" +
                    "VALUES (:apikey, :authenticated, :apiRequests, :email, :organizationName, :name, :registrationToken)"
    ),
    UPDATE_API_USER(
            "UPDATE public." + SqlTable.API_USER + "\n" +
                    "SET authenticated = :authenticated, num_requests = :apiRequests, email_addr = :email, " +
                    "users_name = :name, org_name = :organizationName, reg_token = :registrationToken" + "\n" +
                    "WHERE apiKey = :apikey"
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
                    "WHERE apikey = :apiKey;"
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

    DELETE_ALL_API_USER_SUBSCRIPTIONS(
            "DELETE FROM public." + SqlTable.API_USER_SUBSCRIPTION + "\n" +
                    "WHERE apikey = :apiKey"
    ),

    SELECT_API_USERS_BY_SUBSCRIPTION(
            "SELECT a.apikey, a.authenticated, a.num_requests, a.email_addr, a.org_name, a.users_name, a.reg_token, " +
                    "array(SELECT r.role FROM public." + SqlTable.API_USER_ROLE + " r WHERE r.apikey = a.apikey) AS roles," +
                    "array(SELECT s.subscription_type FROM public." + SqlTable.API_USER_SUBSCRIPTION +
                    " s WHERE s.apikey = a.apikey) AS subscriptions\n" +
                    "FROM (SELECT *" + "\n" + "FROM public." + SqlTable.API_USER + " u NATURAL JOIN public." +
                    SqlTable.API_USER_SUBSCRIPTION + " s" + "\n" +
                    "WHERE u.apikey = s.apikey ) as a" + "\n" +
                    "WHERE a.subscription_type = :subscription_type::public.apiuser_subscription_type"
    );

    private final String sql;

    ApiUserQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
