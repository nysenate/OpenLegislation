package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlMemberDao extends SqlBaseDao implements MemberDao {
    private int handleEntityChange(MemberChangeType dataType, MapSqlParameterSource params, String createQuery, String updateQuery, String deleteQuery) {
        switch (dataType) {
            case CREATE:
                return jdbcNamed.queryForObject(createQuery, params, new SingleColumnRowMapper<>());
            case UPDATE:
                jdbcNamed.update(updateQuery, params);
                break;
            case DELETE:
                jdbcNamed.update(deleteQuery, params);
                break;
            default:
                throw new IllegalArgumentException("Invalid MemberChangeType");
        }
        return 0;
    }

    @Override
    public int handlePersonChange(MemberChangeType dataType, Person person) {
        var params = new MapSqlParameterSource();

        if (dataType != MemberChangeType.CREATE) {
            params.addValue("id", person.personId());
        }

        if (dataType != MemberChangeType.DELETE) {
            params.addValue("firstName", person.name().firstName())
                    .addValue("middleName", StringUtils.defaultIfEmpty(person.name().middleName(), ""))
                    .addValue("lastName", person.name().lastName())
                    .addValue("suffix", StringUtils.defaultIfEmpty(person.name().suffix(), ""))
                    .addValue("email", person.email())
                    .addValue("imgName", person.imgName());
        }

        return handleEntityChange(dataType, params,
                SqlMemberQuery.CREATE_PERSON.getSql(),
                SqlMemberQuery.UPDATE_PERSON.getSql(),
                SqlMemberQuery.DELETE_PERSON.getSql());
    }


    @Override
    public int handleMemberChange(MemberChangeType dataType, Member member) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (dataType != MemberChangeType.CREATE) {
            params.addValue("id", member.getMemberId());
        }
        if(dataType != MemberChangeType.DELETE) {
            params.addValue("chamber", member.getChamber().name().toLowerCase())
                    .addValue("incumbent", member.isIncumbent())
                    .addValue("personId", member.getPersonId());
        }

        return handleEntityChange(dataType, params,
                SqlMemberQuery.CREATE_MEMBER.getSql(),
                SqlMemberQuery.UPDATE_MEMBER.getSql(),
                SqlMemberQuery.DELETE_MEMBER.getSql());
    }

    @Override
    public int handleSessionMemberChange(MemberChangeType dataType, SessionMember sessionMember) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (dataType != MemberChangeType.CREATE) {
            params.addValue("id", sessionMember.getSessionMemberId());
        }

        if (dataType != MemberChangeType.DELETE) {
            params.addValue("memberId", sessionMember.getMemberId())
                    .addValue("lbdcShortName", sessionMember.getLbdcShortName())
                    .addValue("sessionYear", sessionMember.getSessionYear().year())
                    .addValue("districtCode", sessionMember.getDistrictCode())
                    .addValue("alternate", sessionMember.isAlternate());
        }


        return handleEntityChange(dataType, params,
                SqlMemberQuery.CREATE_SESSION_MEMBER.getSql(),
                SqlMemberQuery.UPDATE_SESSION_MEMBER.getSql(),
                SqlMemberQuery.DELETE_SESSION_MEMBER.getSql()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SessionMember> getAllSessionMembers() {
        return jdbcNamed.query(SqlMemberQuery.SELECT_SESSION_MEMBERS.getSql(schema()),
                new MapSqlParameterSource(), new SessionMemberRowMapper());
    }

    @Override
    public Person getPerson(int personId) {
        var params = new MapSqlParameterSource().addValue("id", personId);
        try {
            List<Person> result = jdbcNamed.query(SqlMemberQuery.SELECT_PERSON.getSql(), params, new PersonRowMapper("id"));
            if (!result.isEmpty()) {
                return result.get(0);
            }
        } catch (EmptyResultDataAccessException ignored) {}
        throw new NoSuchElementException("Person with ID " + personId + " does not exist.");
    }

    @Override
    public Member getMember(int memberId) {
        var params = new MapSqlParameterSource("id", memberId);
        try {
            // Execute query to fetch the Member based on the memberId
            List<Member> result = jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER.getSql(), params,
                    new MemberRowMapper());
            if (!result.isEmpty()) {
                return result.get(0); // Return the first result if it's not empty
            }
        } catch (EmptyResultDataAccessException ignored) {}
        throw new MemberNotFoundEx(memberId, null);
    }

    @Override
    public SessionMember getSessionMember(int sessionMemberId) throws MemberNotFoundEx {
        var params = new MapSqlParameterSource("id", sessionMemberId);
        try {
            List<SessionMember> result = jdbcNamed.query(SqlMemberQuery.SELECT_SESSION_MEMBER.getSql(), params,
                    new SessionMemberRowMapper());
            if (!result.isEmpty()) {
                return result.get(0); // Return the first result if it's not empty
            }
        } catch (EmptyResultDataAccessException ignored) {}
        throw new MemberNotFoundEx(sessionMemberId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullMember> getAllFullMembers() {
        List<FullMember> fullMembers = new ArrayList<>();
        getAllSessionMembers().stream()
                .collect(Collectors.groupingBy(sm -> sm.getMember().getMemberId(),
                        LinkedHashMap::new, Collectors.toList()))
                .values().forEach(smList -> fullMembers.add(new FullMember(smList)));

        jdbcNamed.query(SqlMemberQuery.SELECT_ALL_MEMBERS_NO_SESSION_MEMBER.getSql(), new MemberRowMapper())
                .forEach(member -> fullMembers.add(new FullMember(member)));

        jdbcNamed.query(SqlMemberQuery.SELECT_ALL_PERSONS_NO_MEMBER.getSql(), new PersonRowMapper("id"))
                .forEach(person -> fullMembers.add(new FullMember(person)));

        return fullMembers;
    }

    /**
     * --- Helper classes ---
     */
    private record PersonRowMapper(String personIdColName) implements RowMapper<Person> {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            var name = new PersonName(rs.getString("first_name"), rs.getString("middle_name"),
                    rs.getString("last_name"), rs.getString("suffix"));
            return new Person(rs.getInt(personIdColName), name,
                    rs.getString("email"), rs.getString("img_name"));
        }
    }

    private class MemberRowMapper implements RowMapper<Member> {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Member(getPerson(rs.getInt("person_id")), rs.getInt("id"),
                    Chamber.getValue(rs.getString("chamber")), rs.getBoolean("incumbent"));
        }
    }

    private class SessionMemberRowMapper implements RowMapper<SessionMember> {
        @Override
        public SessionMember mapRow(@Nonnull ResultSet rs, int rowNum) throws SQLException {
            var sessionMember = new SessionMember();
            sessionMember.setSessionMemberId(rs.getInt("id"));
            sessionMember.setLbdcShortName(rs.getString("lbdc_short_name"));
            sessionMember.setSessionYear(getSessionYearFromRs(rs, "session_year"));
            sessionMember.setDistrictCode(rs.getInt("district_code"));
            sessionMember.setAlternate(rs.getBoolean("alternate"));
            sessionMember.setMember(getMember(rs.getInt("member_id")));
            return sessionMember;
        }
    }
}
