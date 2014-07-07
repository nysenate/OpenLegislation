package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository("sqlMember")
public class SqlMemberDao extends SqlBaseDao implements MemberDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberDao.class);

    /* --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Member> getMemberById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource("memberId", id);
        List<Member> memberList =
            jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(schema()), params, new MemberRowMapper());
        return getMemberSessionMap(memberList);
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberById(int id, int session) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("memberId", id);
        params.addValue("sessionYear", session);
        return jdbcNamed.queryForObject(
            SqlMemberQuery.SELECT_MEMBER_BY_ID_SESSION_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<Integer, Member> getMembersByLBDCName(String lbdcShortName, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName);
        params.addValue("chamber", chamber.name().toLowerCase());
        List<Member> members =
            jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SQL.getSql(schema()), params, new MemberRowMapper());
        return getMemberSessionMap(members);
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberByLBDCName(String lbdcShortName, int sessionYear, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName.trim());
        params.addValue("sessionYear", (sessionYear % 2 == 0) ? sessionYear - 1 : sessionYear);
        params.addValue("chamber", chamber.name().toLowerCase());
        logger.debug("Fetching member {} ({}) from database...", lbdcShortName, sessionYear);
        return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public void updateMember(Member member) {

    }

    /** {@inheritDoc} */
    @Override
    public void deleteMember(Member member) {

    }

    /** --- Helper classes --- */

    private static class MemberRowMapper implements RowMapper<Member>
    {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            Member member = new Member();
            member.setMemberId(rs.getInt("member_id"));
            member.setLbdcShortName(rs.getString("lbdc_short_name"));
            member.setSessionYear(rs.getInt("session_year"));
            member.setDistrictCode(rs.getInt("district_code"));
            member.setChamber(Chamber.valueOf(rs.getString("chamber").toUpperCase()));
            member.setIncumbent(rs.getBoolean("incumbent"));
            member.setId(rs.getInt("person_id"));
            member.setFullName(rs.getString("full_name"));
            member.setFirstName(rs.getString("first_name"));
            member.setMiddleName(rs.getString("middle_name"));
            member.setLastName(rs.getString("last_name"));
            member.setSuffix(rs.getString("suffix"));
            return member;
        }
    }

    /** --- Internal Methods --- */

    /**
     * Converts a list of member objects referring to multiple session years into a
     * map keyed by the session year.
     * @param members List<Member>
     * @return Map<Integer, Member>
     */
    private Map<Integer, Member> getMemberSessionMap(List<Member> members) {
        TreeMap<Integer, Member> memberMap = new TreeMap<>();
        for (Member member : members) {
            memberMap.put(member.getSessionYear(), member);
        }
        return memberMap;
    }
}
