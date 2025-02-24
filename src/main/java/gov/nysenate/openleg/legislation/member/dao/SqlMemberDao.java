package gov.nysenate.openleg.legislation.member.dao;

import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SqlMemberDao extends SqlBaseDao implements MemberDao {
    /**
     * {@inheritDoc}
     */
    @Override
    public FullMember getMemberById(int id) throws MemberNotFoundEx {
        MapSqlParameterSource params = new MapSqlParameterSource("memberId", id);
        List<SessionMember> memberList =
                jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(schema()), params, new MemberRowMapper());
        if (memberList.isEmpty()) {
            throw new MemberNotFoundEx(id, null);
        }
        return new FullMember(memberList);
    }

    @Override
    public int handlePersonChange(MemberChangeType dataType, Person person) {
        var params = new MapSqlParameterSource();
        if (dataType == MemberChangeType.CREATE) {
            // Prepare the parameters for the INSERT operation
                params.addValue("firstName", person.name().firstName())
                        .addValue("middleName", person.name().middleName())
                        .addValue("lastName", person.name().lastName())
                        .addValue("suffix", person.name().suffix())
                        .addValue("email", person.email())
                        .addValue("imgName", person.imgName());

            return jdbcNamed.queryForObject(SqlMemberQuery.CREATE_PERSON.getSql(), params, new SingleColumnRowMapper<>());
        }

        else if (dataType == MemberChangeType.UPDATE) {
            params.addValue("id", person.personId());

            // Fetch existing record
            List<Person> existingRecord = jdbcNamed.query(SqlMemberQuery.SELECT_BY_PERSON_ID.getSql(), params, new PersonRowMapper());

            if (existingRecord.isEmpty()) {
                throw new NoSuchElementException("Person with ID " + person.personId() + " does not exist.");
            }
            Person existingPerson = existingRecord.get(0);
            params.addValue("id", person.personId()).addValue("firstName",
                    (person.name().firstName() != null && !person.name().firstName().isEmpty())
                            ? person.name().firstName() : existingPerson.name().firstName()).addValue("lastName",
                    (person.name().lastName() != null && !person.name().lastName().isEmpty())
                            ? person.name().lastName() : existingPerson.name().lastName()).addValue("suffix",
                    (person.name().suffix() != null && !person.name().suffix().isEmpty())
                            ? person.name().suffix() : existingPerson.name().suffix()).addValue("email",
                    (person.email() != null && !person.email().isEmpty())
                            ? person.email() : existingPerson.email()).addValue("imgName",
                    (person.imgName() != null && !person.imgName().isEmpty())
                            ? person.imgName() : existingPerson.imgName()).addValue("middleName",
                    (person.name().middleName() != null && !person.name().middleName().isEmpty())
                            ?person.name().middleName() : existingPerson.name().middleName());
            jdbcNamed.update(SqlMemberQuery.UPDATE_PERSON.getSql(), params);
        }


        else if (dataType == MemberChangeType.DELETE) {
            params.addValue("id", person.personId());
            jdbcNamed.update(SqlMemberQuery.DELETE_PERSON.getSql(), params);
        }
        return 0;
    }


    @Override
    public int handleMemberChange(MemberChangeType dataType, Member member) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (dataType == MemberChangeType.CREATE) {
            params.addValue("personId", member.getPersonId())
                    .addValue("chamber", member.getChamber().name().toLowerCase())
                    .addValue("incumbent", member.isIncumbent());
            return jdbcNamed.queryForObject(SqlMemberQuery.CREATE_MEMBER.getSql(), params, new SingleColumnRowMapper<>());
        }
        else if (dataType == MemberChangeType.UPDATE) {
            params.addValue("memberId", member.getMemberId());
            List<SessionMember> exisitingRecord = jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(), params, new MemberRowMapper());
            if (exisitingRecord.isEmpty()) {
                throw new MemberNotFoundEx(member.getMemberId());
            }
            SessionMember existingMember = exisitingRecord.get(0);
            params.addValue("id", member.getMemberId())
                    .addValue("personId", member.getPerson().personId() != null ? member.getPerson().personId() : existingMember.getMember().getPerson().personId())
                    .addValue("chamber", member.getChamber().name().toLowerCase() != null ? member.getChamber().name().toLowerCase() : existingMember.getMember().getChamber().name().toLowerCase())
                    .addValue("incumbent", member.isIncumbent());
            jdbcNamed.update(SqlMemberQuery.UPDATE_MEMBER.getSql(), params);
        }
        else if (dataType == MemberChangeType.DELETE) {
            params.addValue("id", member.getMemberId());
            jdbcNamed.update(SqlMemberQuery.DELETE_MEMBER.getSql(), params);
        }
        return 0;
    }

    @Override
    public int handleSessionMemberChange(MemberChangeType dataType, SessionMember sessionMember) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        if (dataType == MemberChangeType.CREATE) {
            params.addValue("memberId", sessionMember.getMemberId())
                    .addValue("lbdcShortName", sessionMember.getLbdcShortName())
                    .addValue("sessionYear", sessionMember.getSessionYear().year())
                    .addValue("districtCode", sessionMember.getDistrictCode())
                    .addValue("alternate", sessionMember.isAlternate());
            return jdbcNamed.queryForObject(SqlMemberQuery.CREATE_SESSION_MEMBER.getSql(), params, new SingleColumnRowMapper<>());
        } else if (dataType == MemberChangeType.UPDATE) {
            params.addValue("memberId", sessionMember.getMember().getMemberId());
            List<SessionMember> exisitingRecord = jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_ID_SQL.getSql(), params, new MemberRowMapper());
            if (exisitingRecord.isEmpty()) {
                throw new MemberNotFoundEx(sessionMember.getSessionMemberId());
            }
            SessionMember existingMember = exisitingRecord.get(0);
            params.addValue("id", sessionMember.getSessionMemberId())
                    .addValue("memberId", sessionMember.getMember().getMemberId())
                    .addValue("lbdcShortName", sessionMember.getLbdcShortName() != null ? sessionMember.getLbdcShortName() : existingMember.getLbdcShortName())
                    .addValue("sessionYear", sessionMember.getSessionYear().year() != 0 ? sessionMember.getSessionYear().year(): existingMember.getSessionYear().year())
                    .addValue("districtCode", sessionMember.getDistrictCode() != null ? sessionMember.getDistrictCode() : existingMember.getDistrictCode())
                    .addValue("alternate", sessionMember.isAlternate());
            jdbcNamed.update(SqlMemberQuery.UPDATE_SESSION_MEMBER.getSql(), params);
        } else if (dataType == MemberChangeType.DELETE) {
            params.addValue("id", sessionMember.getSessionMemberId());
            jdbcNamed.update(SqlMemberQuery.DELETE_SESSION_MEMBER.getSql(), params);
        }
        return 0;
    }

    @Override
    public SessionMember getMemberBySessionId(int sessionMemberId) throws MemberNotFoundEx {
        ImmutableParams params = ImmutableParams.from(
                new MapSqlParameterSource().addValue("sessionMemberId", sessionMemberId));
        try {
            return jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_BY_SESSION_MEMBER_ID_SQL
                    .getSql(schema()), params, new MemberRowMapper()).get(0);
        } catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(sessionMemberId);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Since the short names used in the source data can be inconsistent (the short name can get modified
     * during the middle of a session year) we have a notion of an alternate short name. A member can only
     * have one primary short name mapping during a session year but can have multiple 'alternate' short names
     * to deal with edge cases in the data. This method will attempt to match the primary short name first
     * and if that fails tries to check for an alternate form. If both attempts fail the calling method will
     * have to handle a DataAccessException.
     */
    @Override
    public SessionMember getMemberByShortName(String lbdcShortName, SessionYear sessionYear,
                                              Chamber chamber) throws MemberNotFoundEx {
        var params = new MapSqlParameterSource("shortName", lbdcShortName.trim())
                .addValue("sessionYear", sessionYear.year())
                .addValue("chamber", chamber.name().toLowerCase())
                .addValue("alternate", false);
        try {
            return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema()),
                    params, new MemberRowMapper());
        } catch (EmptyResultDataAccessException ignored1) {
            params.addValue("alternate", true);
            try {
                return jdbcNamed.queryForObject(SqlMemberQuery.SELECT_MEMBER_BY_SHORTNAME_SESSION_SQL.getSql(schema(), LimitOffset.ONE),
                        params, new MemberRowMapper());
            } catch (EmptyResultDataAccessException ignored2) {
                throw new MemberNotFoundEx(lbdcShortName, sessionYear, chamber);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SessionMember> getAllSessionMembers(SortOrder sortOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("last_name", sortOrder);
        return jdbcNamed.query(SqlMemberQuery.SELECT_MEMBER_FRAGMENT.getSql(schema(), orderBy, limOff),
                new MapSqlParameterSource(), new MemberRowMapper());
    }

    @Override
    public Person getPersonByPersonId(int personId) {
        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", personId);
        try {
            List<Person> result = jdbcNamed.query(SqlMemberQuery.SELECT_BY_PERSON_ID.getSql(), params, new PersonRowMapper());
            if (!result.isEmpty()) {
                return result.get(0);
            } else {
                throw new MemberNotFoundEx(personId);
            }
        } catch (EmptyResultDataAccessException ex) {
            throw new MemberNotFoundEx(personId);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FullMember> getAllFullMembers() {
        return getAllSessionMembers(SortOrder.ASC, LimitOffset.ALL).stream()
                .collect(Collectors.groupingBy(sm -> sm.getMember().getMemberId(),
                        LinkedHashMap::new, Collectors.toList()))
                .values().stream().map(FullMember::new).toList();
    }

    /**
     * --- Helper classes ---
     */

    public static class PersonRowMapper implements RowMapper<Person> {
        @Override
        public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
            PersonName name = new PersonName(rs.getString("full_name"), rs.getString("first_name"), rs.getString("middle_name"), rs.getString("last_name"), rs.getString("suffix"));
            return new Person(
                    rs.getInt("id"),
                    name,
                    rs.getString("email"),
                    rs.getString("img_name")
            );
        }
    }



    public static class MemberRowMapper implements RowMapper<SessionMember> {
        @Override
        public SessionMember mapRow(ResultSet rs, int rowNum) throws SQLException {
            SessionMember sessionMember = new SessionMember();
            sessionMember.setSessionMemberId(rs.getInt("session_member_id"));
            sessionMember.setLbdcShortName(rs.getString("lbdc_short_name"));
            sessionMember.setSessionYear(getSessionYearFromRs(rs, "session_year"));
            sessionMember.setDistrictCode(rs.getInt("district_code"));
            sessionMember.setAlternate(rs.getBoolean("alternate"));

            PersonName name = new PersonName(rs.getString("full_name"), Chamber.getValue(rs.getString("most_recent_chamber")),
                    rs.getString("first_name"), rs.getString("middle_name"),
                    rs.getString("last_name"), rs.getString("suffix"));
            Person person = new Person(rs.getInt("person_id"), name,
                    rs.getString("email"), rs.getString("img_name"));
            Member member = new Member(person, rs.getInt("member_id"),
                    Chamber.getValue(rs.getString("chamber")), rs.getBoolean("incumbent"));
            sessionMember.setMember(member);
            return sessionMember;
        }
    }

    /**
     * --- Internal Methods ---
     */

    private MapSqlParameterSource getPersonParams(Person person) {
        PersonName name = person.name();
        return new MapSqlParameterSource()
                .addValue("personId", person.personId())
                .addValue("fullName", name.fullName())
                .addValue("firstName", name.firstName())
                .addValue("lastName", name.lastName())
                .addValue("middleName", name.middleName())
                .addValue("email", person.email())
                .addValue("prefix", name.prefix())
                .addValue("suffix", name.suffix())
                .addValue("img_name", person.imgName());
    }

    private MapSqlParameterSource getMemberParams(Member member) {
        return getPersonParams(member.getPerson())
                .addValue("memberId", member.getMemberId())
                .addValue("chamber", Optional.ofNullable(member.getChamber()).map(Chamber::asSqlEnum).orElse(null))
                .addValue("incumbent", member.isIncumbent())
                .addValue("fullName", member.getPerson().name().fullName());
    }

    private MapSqlParameterSource getSessionMemberParams(SessionMember sessionMember) {
        return getMemberParams(sessionMember.getMember())
                .addValue("sessionMemberId", sessionMember.getSessionMemberId())
                .addValue("lbdcShortName", sessionMember.getLbdcShortName())
                .addValue("sessionYear", Optional.ofNullable(sessionMember.getSessionYear()).map(SessionYear::year).orElse(null))
                .addValue("districtCode", sessionMember.getDistrictCode())
                .addValue("alternate", sessionMember.isAlternate());
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
