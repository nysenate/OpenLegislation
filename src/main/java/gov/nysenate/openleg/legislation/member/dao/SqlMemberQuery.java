package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlTable;

public enum SqlMemberQuery implements BasicSqlQuery
{
    /** --- Member --- */

    SELECT_MEMBER_TABLE_FRAGMENT(
        "FROM " + SqlTable.SESSION_MEMBER + " sm\n" +
        "JOIN " + SqlTable.MEMBER + " m ON m.id = sm.member_id\n" +
        "JOIN " + SqlTable.PERSON + " p ON p.id = m.person_id\n"
    ),

    SELECT_MOST_RECENT_DATA(
        "SELECT DISTINCT ON (p.id) p.id, m.chamber AS most_recent_chamber, sm.lbdc_short_name AS most_recent_shortname\n" +
            SELECT_MEMBER_TABLE_FRAGMENT.sql + "ORDER BY p.id, sm.session_year DESC, sm.alternate ASC"
    ),

    SELECT_MEMBER_SELECT_FRAGMENT(
        "WITH mr AS (" + SELECT_MOST_RECENT_DATA.sql + ")\n" +
        "SELECT sm.id AS session_member_id, sm.member_id, sm.lbdc_short_name, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent, p.id AS person_id, p.full_name, p.alt_first_name," +
        "       p.img_name, p.email, mr.most_recent_chamber, mr.most_recent_shortname"
    ),

    SELECT_MEMBER_FRAGMENT(
        SELECT_MEMBER_SELECT_FRAGMENT.sql + "\n" + SELECT_MEMBER_TABLE_FRAGMENT.sql +
        "JOIN mr ON mr.id = p.id"
    ),
    SELECT_MEMBER_BY_PERSON_ID_SQL(
            SELECT_MEMBER_FRAGMENT.sql + " WHERE p.id = :personId"
    ),
    SELECT_MEMBER_BY_ID_SQL(
        SELECT_MEMBER_FRAGMENT.sql + " WHERE sm.member_id = :memberId"
    ),
    SELECT_MEMBER_BY_ID_SESSION_SQL(
        SELECT_MEMBER_BY_ID_SQL.sql + " AND sm.session_year = :sessionYear AND sm.alternate = FALSE"
    ),
    SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL(
        "WITH mr AS (" + SELECT_MOST_RECENT_DATA.sql +")\n" +
        "SELECT smp.id AS session_member_id, smp.lbdc_short_name, sm.id, sm.member_id, sm.session_year, sm.district_code, sm.alternate,\n" +
        "       m.chamber, m.incumbent,\n" +
        "       p.id AS person_id, p.full_name, p.alt_first_name,\n" +
        "       p.img_name, p.email, mr.most_recent_chamber, mr.most_recent_shortname\n" +
        SELECT_MEMBER_TABLE_FRAGMENT.sql +
        "JOIN mr ON mr.id = p.id\n" +
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
