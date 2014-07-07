package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlQueryEnum;
import gov.nysenate.openleg.dao.base.SqlQueryUtils;
import gov.nysenate.openleg.dao.base.SqlTable;

public enum SqlMemberQuery implements SqlQueryEnum
{
    /** --- Member --- */

    SELECT_MEMBER_FRAGMENT(
        "SELECT sm.member_id, sm.lbdc_short_name, sm.session_year, sm.district_code, m.chamber, m.incumbent,\n" +
        "       p.id AS person_id, p.full_name, p.first_name, p.middle_name, p.last_name, p.suffix " +
        "FROM " + SqlTable.SESSION_MEMBER + " sm\n" +
        "JOIN " + SqlTable.MEMBER + " m ON m.id = sm.member_id\n" +
        "JOIN " + SqlTable.PERSON + " p ON p.id = m.person_id\n"
    ),
    SELECT_MEMBER_BY_ID_SQL(
        SELECT_MEMBER_FRAGMENT.sql + " WHERE sm.member_id = :memberId"
    ),
    SELECT_MEMBER_BY_ID_SESSION_SQL(
        SELECT_MEMBER_BY_ID_SQL.sql + " AND sm.session_year = :sessionYear"
    ),
    SELECT_MEMBER_BY_SHORTNAME_SQL(
        SELECT_MEMBER_FRAGMENT.sql + " WHERE sm.lbdc_short_name ILIKE :shortName AND m.chamber = :chamber::chamber "
    ),
    SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL(
         SELECT_MEMBER_BY_SHORTNAME_SQL.sql + " AND sm.session_year = :sessionYear"
    );

    private String sql;

    SqlMemberQuery(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql(String environmentSchema) {
        return SqlQueryUtils.getSqlWithSchema(sql, environmentSchema);
    }
}