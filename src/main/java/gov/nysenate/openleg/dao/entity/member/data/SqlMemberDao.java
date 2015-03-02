package gov.nysenate.openleg.dao.entity.member.data;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    public Map<SessionYear, Member> getMemberById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource("memberId", id);
        List<Member> memberList =
            jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(schema()), params, new MemberRowMapper());
        return getMemberSessionMap(memberList);
    }

    /** {@inheritDoc} */
    @Override
    public Member getMemberById(int id, SessionYear session) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("memberId", id);
        params.addValue("sessionYear", session.getYear());
        return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_ID_SESSION_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    @Override
    public Member getMemberBySessionId(int sessionMemberId) {
        ImmutableParams params = ImmutableParams.from(
                new MapSqlParameterSource().addValue("sessionMemberId", sessionMemberId));
        return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<SessionYear, Member> getMembersByShortName(String lbdcShortName, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName);
        params.addValue("chamber", chamber.name().toLowerCase());
        params.addValue("alternate", false);
        List<Member> members =
            jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SQL.getSql(schema()), params, new MemberRowMapper());
        return getMemberSessionMap(members);
    }

    /** {@inheritDoc}
     *
     *  Since the short names used in the source data can be inconsistent (the short name can get modified
     *  during the middle of a session year) we have a notion of an alternate short name. A member can only
     *  have one primary short name mapping during a session year but can have multiple 'alternate' short names
     *  to deal with edge cases in the data. This method will attempt to match the primary short name first
     *  and if that fails tries to check for an alternate form. If both attempts fail the calling method will
     *  have to handle a DataAccessException.
     */
    @Override
    public Member getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName.trim());
        params.addValue("sessionYear", sessionYear.getYear());
        params.addValue("chamber", chamber.name().toLowerCase());
        params.addValue("alternate", false);
        logger.trace("Fetching member {} ({}) from database...", lbdcShortName, sessionYear);
        try {
            return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema()), params, new MemberRowMapper());
        }
        catch (EmptyResultDataAccessException ex) {
            params.addValue("alternate", true);
            return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema(), LimitOffset.ONE),
                params, new MemberRowMapper());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void insertUnverifiedSessionMember(Member member) {
        insertUnverifiedMember(member);
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("memberId", member.getMemberId())
                .addValue("lbdcShortName", member.getLbdcShortName())
                .addValue("sessionYear", member.getSessionYear().getYear()));
        int sessionMemberId = jdbcNamed.queryForObject(
            SqlMemberQuery.INSERT_UNVERIFIED_SESSION_MEMBER_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
        member.setSessionMemberId(sessionMemberId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Member> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("last_name", sortOrder);
        return jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_FRAGMENT.getSql(schema(), orderBy, limOff),
                new MapSqlParameterSource(), new MemberRowMapper());
    }

    /** --- Helper classes --- */

    private static class MemberRowMapper implements RowMapper<Member>
    {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            Member member = new Member();
            member.setMemberId(rs.getInt("member_id"));
            member.setSessionMemberId(rs.getInt("session_member_id"));
            member.setLbdcShortName(rs.getString("lbdc_short_name"));
            member.setSessionYear(getSessionYearFromRs(rs, "session_year"));
            member.setDistrictCode(rs.getInt("district_code"));
            member.setChamber(Chamber.valueOf(rs.getString("chamber").toUpperCase()));
            member.setIncumbent(rs.getBoolean("incumbent"));
            member.setId(rs.getInt("person_id"));
            member.setFullName(rs.getString("full_name"));
            member.setFirstName(rs.getString("first_name"));
            member.setMiddleName(rs.getString("middle_name"));
            member.setLastName(rs.getString("last_name"));
            member.setSuffix(rs.getString("suffix"));
            member.setImgName(rs.getString("img_name"));
            return member;
        }
    }

    /** --- Internal Methods --- */

    /**
     * Converts a list of member objects referring to multiple session years into a
     * map keyed by the session year.
     *
     * @param members List<Member>
     * @return Map<SessionYear, Member>
     */
    private Map<SessionYear, Member> getMemberSessionMap(List<Member> members) {
        TreeMap<SessionYear, Member> memberMap = new TreeMap<>();
        members.forEach(m -> memberMap.put(m.getSessionYear(), m));
        return memberMap;
    }

    /**
     * Inserts a new, unverified person into the database
     * Sets the person id value of the passed in Person to the database id of the new person entry
     * @param person
     */
    private void insertUnverifiedPerson(Person person) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("fullName", person.getFullName())
                .addValue("firstInitial", person.getFirstName())
                .addValue("lastName", person.getLastName()));
        int personId = jdbcNamed.queryForObject(
            SqlMemberQuery.INSERT_UNVERIFIED_PERSON_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
        person.setId(personId);
    }

    /**
     * Inserts a new, unverified member into the database
     * Sets the member id value of the passed in Member to the database id of the new member entry
     * @param member
     * @return
     */
    private void insertUnverifiedMember(Member member) {
        insertUnverifiedPerson(member);
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("personId", member.getId())
                .addValue("chamber", member.getChamber().asSqlEnum())
                .addValue("incumbent", member.isIncumbent())
                .addValue("fullName", member.getFullName()));
        int memberId = jdbcNamed.queryForObject(
            SqlMemberQuery.INSERT_UNVERIFIED_MEMBER_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
        member.setMemberId(memberId);
    }
}
