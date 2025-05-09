package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlMemberQuery implements BasicSqlQuery {
    CREATE_PERSON(
        "INSERT INTO " + SqlTable.PERSON + " (email, img_name, first_name, middle_name, last_name, suffix) " +
        "VALUES (:email, :imgName, :firstName, :middleName, :lastName, :suffix)" + "RETURNING id"
    ),

    SELECT_PERSON(
        "SELECT * FROM " + SqlTable.PERSON + " WHERE id = :id"
    ),

    SELECT_ALL_PERSONS_NO_MEMBER(
            "SELECT * FROM " + SqlTable.PERSON + " WHERE id NOT IN (SELECT person_id FROM " + SqlTable.MEMBER + ")"
    ),

    UPDATE_PERSON(
        "UPDATE " + SqlTable.PERSON +
        " SET email = :email, img_name = :imgName, first_name = :firstName, middle_name = :middleName, last_name = :lastName, suffix = :suffix" +
        " WHERE id = :id"
    ),

    DELETE_PERSON(
        "DELETE FROM " + SqlTable.PERSON + " WHERE id = :id"
    ),

    CREATE_MEMBER(
        "INSERT INTO " + SqlTable.MEMBER + "(person_id, chamber) VALUES (:personId, :chamber::chamber)" + " RETURNING id"
    ),

    SELECT_MEMBER(
            "SELECT * FROM " + SqlTable.MEMBER + " WHERE id = :id"
    ),

    SELECT_ALL_MEMBERS_NO_SESSION_MEMBER(
            "SELECT * FROM " + SqlTable.MEMBER + " WHERE id NOT IN (SELECT member_id FROM " + SqlTable.SESSION_MEMBER + ")"
    ),

    UPDATE_MEMBER(
        "UPDATE " + SqlTable.MEMBER +
        " SET person_id = :personId, chamber = :chamber::chamber, incumbent = :incumbent" +
        " WHERE id = :id"
    ),

    DELETE_MEMBER("DELETE FROM " + SqlTable.MEMBER + " WHERE id = :id"),

    SELECT_SESSION_MEMBERS(
            "SELECT * FROM public." + SqlTable.SESSION_MEMBER
    ),

    CREATE_SESSION_MEMBER("INSERT INTO " + SqlTable.SESSION_MEMBER + " (member_id, lbdc_short_name, session_year, district_code, alternate) " +
        "VALUES(:memberId, :lbdcShortName, :sessionYear, :districtCode, :alternate) RETURNING id"
    ),

    SELECT_SESSION_MEMBER(
            "SELECT * FROM " + SqlTable.SESSION_MEMBER + " WHERE id = :id"
    ),

    UPDATE_SESSION_MEMBER(
        "UPDATE " + SqlTable.SESSION_MEMBER +
        " SET member_id = :memberId, lbdc_short_name = :lbdcShortName, session_year = :sessionYear, district_code =:districtCode, alternate =:alternate" +
        " WHERE id = :id "),

    DELETE_SESSION_MEMBER("DELETE FROM " + SqlTable.SESSION_MEMBER + " WHERE id = :id");

    private final String sql;

    SqlMemberQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}
