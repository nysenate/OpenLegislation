package gov.nysenate.openleg.dao.entity.member.data;

import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.entity.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Repository("sqlMember")
public class SqlMemberDao extends SqlBaseDao implements MemberDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlMemberDao.class);

    /** --- Implemented Methods --- */

    /** {@inheritDoc} */
    @Override
    public TreeMultimap<SessionYear, SessionMember> getMemberById(int id) {
        MapSqlParameterSource params = new MapSqlParameterSource("memberId", id);
        List<SessionMember> memberList =
            jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(schema()), params, new MemberRowMapper());
        TreeMultimap<SessionYear, SessionMember> memberMap = TreeMultimap.create();
        memberList.forEach(member -> memberMap.put(member.getSessionYear(), member));
        return memberMap;
    }

    /** {@inheritDoc} */
    @Override
    public SessionMember getMemberById(int id, SessionYear session) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("memberId", id);
        params.addValue("sessionYear", session.getYear());
        return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_ID_SESSION_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    @Override
    public SessionMember getMemberBySessionId(int sessionMemberId) {
        ImmutableParams params = ImmutableParams.from(
                new MapSqlParameterSource().addValue("sessionMemberId", sessionMemberId));
        return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL.getSql(schema()), params, new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public Map<SessionYear, SessionMember> getMembersByShortName(String lbdcShortName, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName);
        params.addValue("chamber", chamber.name().toLowerCase());
        params.addValue("alternate", false);
        List<SessionMember> members =
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
    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear, Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("shortName", lbdcShortName.trim());
        params.addValue("sessionYear", sessionYear.getYear());
        params.addValue("chamber", chamber.name().toLowerCase());
        params.addValue("alternate", false);
        logger.trace("Fetching member {} ({}) from database...", lbdcShortName, sessionYear);
        try {
            return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema()),
                    params, new MemberRowMapper());
        }
        catch (EmptyResultDataAccessException ex) {
            params.addValue("alternate", true);
            return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema(), LimitOffset.ONE),
                params, new MemberRowMapper());
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<SessionMember> getAllMembers(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("last_name", sortOrder);
        return jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_FRAGMENT.getSql(schema(), orderBy, limOff),
                new MapSqlParameterSource(), new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SessionMember> getUnverifiedSessionMembers() {
        OrderBy order = new OrderBy("session_year", SortOrder.DESC, "lbdc_short_name", SortOrder.ASC);
        return jdbcNamed.query(SqlMemberQuery.SELECT_UNVERIFIED_MEMBERS_SQL.getSql(schema(), order),
                new MapSqlParameterSource(), new MemberRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public void updatePerson(Person person) {
        ImmutableParams params = ImmutableParams.from(getPersonParams(person));
        if (jdbcNamed.update(SqlMemberQuery.UPDATE_PERSON_SQL.getSql(schema()), params) == 0) {
            int personId = jdbcNamed.queryForObject(
                    SqlMemberQuery.INSERT_PERSON_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
            person.setPersonId(personId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateMember(SessionMember member) {
        ImmutableParams params = ImmutableParams.from(getMemberParams(member));
        if (jdbcNamed.update(SqlMemberQuery.UPDATE_MEMBER_SQL.getSql(schema()), params) == 0) {
            int memberId = jdbcNamed.queryForObject(
                    SqlMemberQuery.INSERT_MEMBER_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
            member.setMemberId(memberId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateSessionMember(SessionMember member) {
        ImmutableParams params = ImmutableParams.from(getMemberParams(member));
        if (jdbcNamed.update(SqlMemberQuery.UPDATE_SESSION_MEMBER_SQL.getSql(schema()), params) == 0) {
            int sessionMemberId = jdbcNamed.queryForObject(
                    SqlMemberQuery.INSERT_SESSION_MEMBER_SQL.getSql(schema()), params, new SingleColumnRowMapper<>());
            member.setSessionMemberId(sessionMemberId);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void linkMember(int memberId, int personId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("memberId", memberId)
                .addValue("personId", personId);
        jdbcNamed.update(SqlMemberQuery.LINK_MEMBER_SQL.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void linkSessionMember(int sessionMemberId, int memberId) {
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("sessionMemberId", sessionMemberId)
                .addValue("memberId", memberId);
        jdbcNamed.update(SqlMemberQuery.LINK_SESSION_MEMBER_SQL.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void clearOrphans() {
        jdbcNamed.update(SqlMemberQuery.DELETE_ORPHAN_MEMBERS_SQL.getSql(schema()), new MapSqlParameterSource());
        jdbcNamed.update(SqlMemberQuery.DELETE_ORPHAN_PERSONS_SQL.getSql(schema()), new MapSqlParameterSource());
    }

    /** --- Helper classes --- */

    private static class MemberRowMapper implements RowMapper<SessionMember>
    {
        @Override
        public SessionMember mapRow(ResultSet rs, int rowNum) throws SQLException {
            SessionMember member = new SessionMember();
            member.setMemberId(rs.getInt("member_id"));
            member.setSessionMemberId(rs.getInt("session_member_id"));
            member.setLbdcShortName(rs.getString("lbdc_short_name"));
            member.setSessionYear(getSessionYearFromRs(rs, "session_year"));
            member.setDistrictCode(rs.getInt("district_code"));
            member.setChamber(Chamber.valueOf(rs.getString("chamber").toUpperCase()));
            member.setIncumbent(rs.getBoolean("incumbent"));
            member.setPersonId(rs.getInt("person_id"));
            member.setFullName(rs.getString("full_name"));
            member.setFirstName(rs.getString("first_name"));
            member.setMiddleName(rs.getString("middle_name"));
            member.setLastName(rs.getString("last_name"));
            member.setSuffix(rs.getString("suffix"));
            member.setImgName(rs.getString("img_name"));
            member.setAlternate(rs.getBoolean("alternate"));
            member.setVerified(rs.getBoolean("verified"));
            return member;
        }
    }

    /** --- Internal Methods --- */

    private MapSqlParameterSource getPersonParams(Person person) {
        return new MapSqlParameterSource()
                .addValue("personId", person.getPersonId())
                .addValue("fullName", person.getFullName())
                .addValue("firstName", person.getFirstName())
                .addValue("lastName", person.getLastName())
                .addValue("middleName", person.getMiddleName())
                .addValue("email", person.getEmail())
                .addValue("prefix", person.getPrefix())
                .addValue("suffix", person.getSuffix())
                .addValue("img_name", person.getImgName())
                .addValue("verified", person.isVerified());
    }

    private MapSqlParameterSource getMemberParams(SessionMember member) {
        return getPersonParams(member)
                .addValue("memberId", member.getMemberId())
                .addValue("sessionMemberId", member.getSessionMemberId())
                .addValue("chamber", Optional.ofNullable(member.getChamber()).map(Chamber::asSqlEnum).orElse(null))
                .addValue("incumbent", member.isIncumbent())
                .addValue("fullName", member.getFullName())
                .addValue("lbdcShortName", member.getLbdcShortName())
                .addValue("sessionYear", Optional.ofNullable(member.getSessionYear()).map(SessionYear::getYear).orElse(null))
                .addValue("districtCode", member.getDistrictCode())
                .addValue("alternate", member.isAlternate());
    }

    /**
     * Converts a list of member objects referring to multiple session years into a
     * map keyed by the session year.
     *
     * @param members List<Member>
     * @return Map<SessionYear, Member>
     */
    private Map<SessionYear, SessionMember> getMemberSessionMap(List<SessionMember> members) {
        TreeMap<SessionYear, SessionMember> memberMap = new TreeMap<>();
        members.forEach(m -> memberMap.put(m.getSessionYear(), m));
        return memberMap;
    }
}
