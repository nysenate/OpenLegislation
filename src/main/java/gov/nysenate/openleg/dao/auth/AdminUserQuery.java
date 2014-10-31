package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum AdminUserQuery implements BasicSqlQuery
{
    INSERT_ADMIN(
            "INSERT INTO public." + SqlTable.ADMIN + " VALUES (:username, :password, :privilegeLevel)"
    ),
    SELECT_BY_NAME(
            "SELECT * FROM public." + SqlTable.ADMIN + " WHERE username = :username"
    ),
    DELETE_BY_NAME(
            "DELETE FROM public." +SqlTable.ADMIN + " WHERE username = :username"
    ),
    DELETE_BY_LEVEL(
            "DELETE FROM public." +SqlTable.ADMIN + " WHERE permissions_level = :privilegeLevel"
    ),
    UPDATE_ADMIN(
            "UPDATE public." +SqlTable.ADMIN+ " SET password = :password, permissions_level = :privilegeLevel" +"\n"+
                "WHERE username = :username"
    );

    @Override
    public String getSql() { return this.sql; }

    private String sql;
    AdminUserQuery(String sql) {
        this.sql = sql;
    }
}
