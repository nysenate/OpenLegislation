package gov.nysenate.openleg.auth.admin;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;

import static gov.nysenate.openleg.common.dao.SqlTable.ADMIN_USER;

enum AdminUserQuery implements BasicSqlQuery {
    INSERT_ADMIN(
            "INSERT INTO public." + ADMIN_USER + "\n" +
            "        ( username,  password,  master,  active)\n" +
            " VALUES (:username, :password, :master, :active)"
    ),
    SELECT_ALL(
            "SELECT * FROM public." + ADMIN_USER
    ),
    SELECT_BY_NAME(
            "SELECT * FROM public." + ADMIN_USER + " WHERE username = :username"
    ),
    DELETE_BY_NAME(
            "DELETE FROM public." + ADMIN_USER + " WHERE username = :username"
    ),
    UPDATE_ADMIN(
            "UPDATE public." + ADMIN_USER + "\n" +
            "SET password = :password, master = :master, active = :active\n"+
            "WHERE username = :username"
    );

    @Override
    public String getSql() { return this.sql; }

    private final String sql;
    AdminUserQuery(String sql) {
        this.sql = sql;
    }
}
