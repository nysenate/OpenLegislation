package gov.nysenate.openleg.dao.entity;

public enum SqlMemberQuery
{
    /** --- Memebr --- */

    SELECT_MEMBER_SQL(
        "SELECT * FROM session_member sm\n" +
        "JOIN member ON member.id = sm.member_id\n" +
        "JOIN person ON person.id = member.person_id\n" +
        "WHERE sm.short_name = :shortName AND sm.session_year = :sessionYear"
    );

    private String sql;

    SqlMemberQuery(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return this.sql;
    }
}
