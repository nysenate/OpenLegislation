package gov.nysenate.openleg.dao.entity.member.data;

import gov.nysenate.openleg.dao.base.BasicSqlQuery;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlMemberQuery implements BasicSqlQuery
{
    /** --- Member --- */

    SELECT_MEMBER_SELECT_FRAGMENT(
        "SELECT sm.id AS session_member_id, sm.member_id, sm.lbdc_short_name, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent, p.id AS person_id, p.full_name, p.first_name, p.middle_name, p.last_name, p.suffix, " +
        "       p.img_name, p.verified"
    ),
    SELECT_MEMBER_TABLE_FRAGMENT(
        "FROM " + SqlTable.SESSION_MEMBER + " sm\n" +
        "JOIN " + SqlTable.MEMBER + " m ON m.id = sm.member_id\n" +
        "JOIN " + SqlTable.PERSON + " p ON p.id = m.person_id\n"
    ),
    SELECT_MEMBER_FRAGMENT(
        SELECT_MEMBER_SELECT_FRAGMENT.sql + "\n" + SELECT_MEMBER_TABLE_FRAGMENT.sql
    ),
    SELECT_MEMBER_BY_ID_SQL(
        SELECT_MEMBER_FRAGMENT.sql + " WHERE sm.member_id = :memberId"
    ),
    SELECT_MEMBER_BY_ID_SESSION_SQL(
        SELECT_MEMBER_BY_ID_SQL.sql + " AND sm.session_year = :sessionYear AND sm.alternate = FALSE"
    ),
    SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL(
        "SELECT smp.id AS session_member_id, smp.lbdc_short_name, sm.id, sm.member_id, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent, p.id AS person_id, p.full_name, p.first_name, p.middle_name, p.last_name, p.suffix, p.img_name, p.verified" + "\n" +
        SELECT_MEMBER_TABLE_FRAGMENT.sql +
        "JOIN " + SqlTable.SESSION_MEMBER + " smp ON smp.member_id = sm.member_id AND smp.session_year = sm.session_year AND smp.alternate = FALSE\n" +
        "WHERE sm.id = :sessionMemberId"
    ),
    SELECT_MEMBER_BY_SHORTNAME_SQL(
        SELECT_MEMBER_FRAGMENT.sql + "\n" +
        //     We use the first 15 letters to compare due to how some of the source data is formatted.
        "WHERE substr(sm.lbdc_short_name, 1, 15) ILIKE substr(:shortName, 1, 15) AND m.chamber = :chamber::chamber " +
        "      AND sm.alternate = :alternate "
    ),
    SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL(
         SELECT_MEMBER_BY_SHORTNAME_SQL.sql + " AND sm.session_year = :sessionYear"
    ),

    /** --- Member verification queries --- */

    SELECT_UNVERIFIED_MEMBERS_SQL(
        SELECT_MEMBER_FRAGMENT.sql +
        "WHERE p.verified = FALSE"
    ),

    UPDATE_PERSON_SQL(
        "UPDATE " + SqlTable.PERSON + "\n" +
        "SET full_name = :fullName, first_name = :firstName, middle_name = :middleName, last_name = :lastName,\n" +
        "       email = :email, prefix = :prefix, suffix = :suffix, verified = :verified, img_name = :img_name\n" +
        "WHERE id = :personId"
    ),
    INSERT_PERSON_SQL(
        "INSERT INTO " + SqlTable.PERSON + "\n" +
               "( full_name, first_name, middle_name, last_name, email,  prefix,  suffix,  verified)\n" +
        "VALUES (:fullName, :firstName, :middleName, :lastName, :email, :prefix, :suffix, :verified)\n" +
        "RETURNING id"
    ),

    UPDATE_MEMBER_SQL(
        "UPDATE " + SqlTable.MEMBER + "\n" +
        "SET person_id = :personId, chamber = :chamber::chamber, incumbent = :incumbent, full_name = :fullName\n" +
        "WHERE id = :memberId"
    ),
    INSERT_MEMBER_SQL(
        "INSERT INTO " + SqlTable.MEMBER + "\n" +
               "( person_id,      chamber,              incumbent,  full_name)\n" +
        "VALUES (:personId, CAST(:chamber AS chamber), :incumbent, :fullName)\n" +
        "RETURNING id"
    ),

    UPDATE_SESSION_MEMBER_SQL(
        "UPDATE " + SqlTable.SESSION_MEMBER + "\n" +
        "SET member_id = :memberId, lbdc_short_name = :lbdcShortName, session_year = :sessionYear,\n" +
        "       district_code = :districtCode, alternate = :alternate\n" +
        "WHERE id = :sessionMemberId"
    ),
    INSERT_SESSION_MEMBER_SQL(
        "INSERT INTO " + SqlTable.SESSION_MEMBER + "\n" +
               "( member_id, lbdc_short_name, session_year, district_code, alternate)\n" +
        "VALUES (:memberId, :lbdcShortName,  :sessionYear, :districtCode, :alternate)\n" +
        "RETURNING id"
    ),

    LINK_MEMBER_SQL(
        "UPDATE " + SqlTable.MEMBER + "\n" +
        "SET person_id = :personId\n" +
        "WHERE id = :memberId"
    ),
    LINK_SESSION_MEMBER_SQL(
        "UPDATE " + SqlTable.SESSION_MEMBER + "\n" +
        "SET member_id = :memberId\n" +
        "WHERE id = :sessionMemberId"
    ),

    DELETE_ORPHAN_MEMBERS_SQL(
        "DELETE FROM " + SqlTable.MEMBER + " m\n" +
        "USING (\n" +
        "   SELECT m.id\n" +
        "   FROM " + SqlTable.MEMBER + " m\n" +
        "   LEFT JOIN " + SqlTable.SESSION_MEMBER + " sm\n" +
        "       ON m.id = sm.member_id\n" +
        "   WHERE sm.id IS NULL\n" +
        ") AS orphan_member\n" +
        "WHERE m.id = orphan_member.id"
    ),

    DELETE_ORPHAN_PERSONS_SQL(
        "DELETE FROM " + SqlTable.PERSON + " p\n" +
        "USING (\n" +
        "   SELECT p.id\n" +
        "   FROM " + SqlTable.PERSON + " p\n" +
        "   LEFT JOIN " + SqlTable.MEMBER + " m\n" +
        "       ON p.id = m.person_id\n" +
        "   WHERE m.id IS NULL\n" +
        ") AS orphan_person\n" +
        "WHERE p.id = orphan_person.id"
    ),
    ;

    private String sql;

    SqlMemberQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return this.sql;
    }
}