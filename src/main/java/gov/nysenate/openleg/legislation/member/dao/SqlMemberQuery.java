package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlMemberQuery implements BasicSqlQuery {
    SELECT_MEMBER_TABLE_FRAGMENT(
        "FROM " + SqlTable.SESSION_MEMBER + " sm\n" +
        "JOIN " + SqlTable.MEMBER + " m ON m.id = sm.member_id\n" +
        "JOIN " + SqlTable.PERSON + " p ON p.id = m.person_id\n"
    ),

    PERSON_FRAGMENT(
        "\np.id AS person_id, p.first_name, p.middle_name, p.last_name, p.suffix, p.img_name, p.email\n"
    ),

    SELECT_MEMBER_SELECT_FRAGMENT(
        "SELECT sm.id AS session_member_id, sm.member_id, sm.lbdc_short_name, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent," + PERSON_FRAGMENT.sql
    ),

    SELECT_MEMBER_FRAGMENT(
        SELECT_MEMBER_SELECT_FRAGMENT.sql + "\n" + SELECT_MEMBER_TABLE_FRAGMENT.sql +
                "JOIN mr ON mr.id = p.id"
    ),

    SELECT_MEMBER_BY_ID_SQL(
        SELECT_MEMBER_FRAGMENT.sql + " WHERE sm.member_id = :memberId"
    ),

    SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL(
        "SELECT smp.id AS session_member_id, smp.lbdc_short_name, sm.id, sm.member_id, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent," + PERSON_FRAGMENT.sql + "\n" +
        SELECT_MEMBER_TABLE_FRAGMENT.sql +
        "JOIN mr ON mr.id = p.id\n" +
        "JOIN " + SqlTable.SESSION_MEMBER + " smp ON smp.member_id = sm.member_id AND smp.session_year = sm.session_year\n" +
        "WHERE sm.id = :sessionMemberId"
    ),

    SELECT_MEMBER_BY_SHORTNAME_SQL(
        SELECT_MEMBER_FRAGMENT.sql + "\n" +
        //     We use the first 15 letters to compare due to how some source data is formatted.
        "WHERE substr(sm.lbdc_short_name, 1, 15) ILIKE substr(:shortName, 1, 15) AND m.chamber = :chamber::chamber " +
        "      AND sm.alternate = :alternate "
    ),

    SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL(
        SELECT_MEMBER_BY_SHORTNAME_SQL.sql + " AND sm.session_year = :sessionYear"
    ),

    CREATE_PERSON(
        "INSERT INTO " + SqlTable.PERSON + " (email, img_name, first_name, middle_name, last_name, suffix) " +
        "VALUES (:email, :imgName, :firstName, :middleName, :lastName, :suffix)" + "RETURNING id"
    ),

    SELECT_PERSON(
        "SELECT * FROM " + SqlTable.PERSON + " WHERE id = :id"
    ),

    SELECT_ALL_PERSONS_NO_MEMBER(
            "SELECT * FROM " + SqlTable.PERSON + "WHERE id NOT IN (SELECT person_id FROM " + SqlTable.MEMBER + ")"
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
