package gov.nysenate.openleg.legislation.committee.dao;

import gov.nysenate.openleg.common.dao.OrderBy;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.common.dao.LimitOffset.ALL;
import static gov.nysenate.openleg.common.dao.SortOrder.ASC;
import static gov.nysenate.openleg.common.dao.SortOrder.DESC;
import static gov.nysenate.openleg.legislation.committee.dao.SqlCommitteeQuery.*;

@Repository
public class SqlCommitteeDao extends SqlBaseDao implements CommitteeDao {
    public static final Logger logger = LoggerFactory.getLogger(SqlCommitteeDao.class);

    private final MemberService memberService;

    public SqlCommitteeDao(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Committee getCommittee(CommitteeVersionId committeeVersionId) throws EmptyResultDataAccessException {
        logger.debug("Looking up committee " + committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        OrderBy orderBy = new OrderBy("created", DESC);
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(
                SELECT_COMMITTEE_VERSION_FOR_DATE_SQL.getSql(schema(), orderBy),
                params,
                rowHandler);
        return rowHandler.getSingleCommittee();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeId> getCommitteeList() {
        return jdbcNamed.query(
                SELECT_COMMITTEE_IDS.getSql(schema(), new OrderBy("chamber", ASC, "name", ASC), ALL),
                new CommitteeIdRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeSessionId> getAllSessionIds() throws DataAccessException {
        return jdbcNamed.query(SELECT_COMMITTEE_SESSION_IDS.getSql(schema(),
                new OrderBy("session_year", ASC), ALL),
                new CommitteeSessionIdRowMapper());
    }

    /**
     * {@inheritDoc}
     * @param committeeSessionId
     */
    @Override
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId)
            throws EmptyResultDataAccessException {
        MapSqlParameterSource params = getCommitteeSessionIdParams(committeeSessionId);
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(SELECT_COMMITTEE_VERSION_HISTORY.getSql(schema(), new OrderBy("created", DESC), ALL),
                params, rowHandler);
        List<Committee> committeeHistory = rowHandler.getCommitteeList();
        if (committeeHistory.isEmpty()) {
            throw new EmptyResultDataAccessException("No committee history found for given committee id", 1);
        }
        return committeeHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateCommittee(Committee committee, LegDataFragment legDataFragment) {
        logger.info("Updating committee {} {}", committee.getChamber(), committee.getName());
        // Make sure that there is a record for the given committee name
        ensureCommitteeExists(committee.getId());

        // Delete this committee version if it already exists
        deleteCommitteeVersion(committee.getVersionId());

        // Get previous committee version.
        // Getting the version as of the argument comm. time accomplishes this
        // because any version with matching time was deleted in the last step.
        Committee prevVersion = null;
        try {
            prevVersion = getCommittee(committee.getVersionId());
        } catch (EmptyResultDataAccessException ignored) {}

        if (prevVersion == null || !committee.membersEquals(prevVersion)) {
            // If there was no previous version, or the previous version had different membership,
            // insert the committee as a new version.
            insertCommitteeVersion(committee, legDataFragment);
        } else if (!committee.meetingEquals(prevVersion)) {
            logger.info("UPDATING MEETING INFO for {} {}", committee.getName(), legDataFragment == null ? null : legDataFragment.getPublishedDateTime());
            // If the only difference from the prev version was meeting info,
            // just update the prev version's meeting info.
            prevVersion.updateMeetingInfo(committee);
            updateCommitteeMeetingInfo(prevVersion, legDataFragment);
        }
    }

    /* --- Private Methods --- */

    /**
     * Ensures that a committee record exists for the given id.
     * @param committeeId {@link CommitteeId}
     */
    private void ensureCommitteeExists(CommitteeId committeeId) {
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        boolean committeeExists = jdbcNamed.queryForObject(TEST_COMMITTEE_ID.getSql(schema()), params, Boolean.class);
        if (!committeeExists) {
            jdbcNamed.update(INSERT_COMMITTEE.getSql(schema()), getCommitteeIdParams(committeeId));
        }
    }

    /**
     * Creates a record for a new version of a committee.
     */
    private void insertCommitteeVersion(Committee committee, LegDataFragment legDataFragment){
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        addLastFragmentParam(legDataFragment, params);
        jdbcNamed.update(INSERT_COMMITTEE_VERSION.getSql(schema()), params);
        for (CommitteeMember committeeMember : committee.getMembers()) {
            params = getCommitteeMemberParams(committeeMember, committee.getVersionId());
            jdbcNamed.update(INSERT_COMMITTEE_MEMBER.getSql(schema()), params);
        }
    }

    /**
     * Modifies the record of a given committee version update data relating to meetings
     */
    private void updateCommitteeMeetingInfo(Committee committee, LegDataFragment legDataFragment){
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        addLastFragmentParam(legDataFragment, params);
        jdbcNamed.update(UPDATE_COMMITTEE_MEETING_INFO.getSql(schema()), params);
    }

    /**
     * Removes the all entries for a given committee version
     */
    private void deleteCommitteeVersion(CommitteeVersionId committeeVersionId) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(DELETE_COMMITTEE_MEMBERS.getSql(schema()), params);
        params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(DELETE_COMMITTEE_VERSION.getSql(schema()), params);
    }

    /* --- Row Mappers --- */

    private static class CommitteeIdRowMapper implements RowMapper<CommitteeId> {
        @Override
        public CommitteeId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommitteeId(
                    Chamber.getValue(rs.getString("chamber")),
                    rs.getString("name")
            );
        }
    }

    private static class CommitteeSessionIdRowMapper implements RowMapper<CommitteeSessionId> {
        @Override
        public CommitteeSessionId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommitteeSessionId(
                    Chamber.getValue(rs.getString("chamber")),
                    rs.getString("committee_name"),
                    getSessionYearFromRs(rs, "session_year")
            );
        }
    }

    private static class CommitteeRowMapper implements RowMapper<Committee> {
        @Override
        public Committee mapRow(ResultSet rs, int i) throws SQLException {
            Committee committee = new Committee();
            committee.setName(rs.getString("committee_name"));
            committee.setChamber(Chamber.getValue(rs.getString("chamber")));
            committee.setPublishedDateTime(getLocalDateTimeFromRs(rs, "created"));
            committee.setReformed(getLocalDateTimeFromRs(rs, "reformed"));
            committee.setLocation(rs.getString("location"));
            committee.setMeetDay(StringUtils.isNotEmpty(rs.getString("meetday"))
                    ? DayOfWeek.valueOf(rs.getString("meetday").toUpperCase()) : null);
            committee.setMeetTime(getLocalTimeFromRs(rs, "meettime"));
            committee.setMeetAltWeek(rs.getBoolean("meetaltweek"));
            committee.setMeetAltWeekText(rs.getString("meetaltweektext"));
            committee.setSession(getSessionYearFromRs(rs, "session_year"));
            return committee;
        }
    }

    private class CommitteeMemberRowMapper implements RowMapper<CommitteeMember> {
        @Override
        public CommitteeMember mapRow(ResultSet rs, int i) throws SQLException {
            CommitteeMember committeeMember = new CommitteeMember();
            committeeMember.setSequenceNo(rs.getInt("sequence_no"));
            int sessionMemberId = rs.getInt("session_member_id");
            committeeMember.setSessionMember(memberService.getSessionMemberBySessionId(sessionMemberId));
            if (committeeMember.getSessionMember().getMember().getMemberId() == 0) {
                logger.error("Could not retrieve session member {}", sessionMemberId);
            }
            committeeMember.setTitle(CommitteeMemberTitle.valueOfSqlEnum(rs.getString("title")));
            committeeMember.setMajority(rs.getBoolean("majority"));
            return committeeMember;
        }
    }

    private class CommitteeRowHandler implements RowCallbackHandler {
        private final CommitteeRowMapper committeeRowMapper = new CommitteeRowMapper();
        private final CommitteeMemberRowMapper committeeMemberRowMapper = new CommitteeMemberRowMapper();
        private final CommitteeSessionIdRowMapper sessionIdRowMapper = new CommitteeSessionIdRowMapper();

        private final Map<CommitteeVersionId, Committee> committeeMap = new LinkedHashMap<>();

        @Override
        public void processRow(@Nonnull ResultSet rs) throws SQLException {
            CommitteeVersionId versionId = new CommitteeVersionId(
                    sessionIdRowMapper.mapRow(rs, rs.getRow()),
                    getLocalDateTimeFromRs(rs, "created")
            );
            if (!committeeMap.containsKey(versionId)) {
                committeeMap.put(versionId, committeeRowMapper.mapRow(rs, rs.getRow()));
            }
            committeeMap.get(versionId).addMember(committeeMemberRowMapper.mapRow(rs, rs.getRow()));
        }

        List<Committee> getCommitteeList() {
            return new ArrayList<>(committeeMap.values());
        }

        /**
         * Get a single committee from the results, assuming the query returns exactly one committee.
         * @return {@link Committee}
         * @throws IncorrectResultSizeDataAccessException if more or less than one row was returned
         */
        Committee getSingleCommittee() throws IncorrectResultSizeDataAccessException {
            List<Committee> committeeList = this.getCommitteeList();
            if (committeeList.isEmpty()) {
                throw new EmptyResultDataAccessException(1);
            }
            if (committeeList.size() != 1) {
                throw new IncorrectResultSizeDataAccessException(1, committeeList.size());
            }
            return committeeList.get(0);
        }
    }

    /* --- Param Source Methods --- */

    private static MapSqlParameterSource getCommitteeIdParams(CommitteeId cid) {
        return new MapSqlParameterSource("committeeName", cid.getName())
                .addValue("chamber", cid.getChamber().asSqlEnum());
    }

    private static MapSqlParameterSource getCommitteeSessionIdParams(CommitteeSessionId csid) {
        return getCommitteeIdParams(csid).addValue("sessionYear", csid.getSession().year());
    }

    private static MapSqlParameterSource getCommitteeVersionIdParams(CommitteeVersionId cvid) {
        return getCommitteeSessionIdParams(cvid)
                .addValue("referenceDate", DateUtils.toDate(cvid.getReferenceDate()));
    }

    private static MapSqlParameterSource getCommitteeVersionParams(Committee committee) {
        return getCommitteeVersionIdParams(committee.getVersionId())
                .addValue("location", committee.getLocation())
                .addValue("meetday", committee.getMeetDay() != null ? committee.getMeetDay().toString() : null)
                .addValue("meettime", DateUtils.toTime(committee.getMeetTime()))
                .addValue("meetaltweek", committee.isMeetAltWeek())
                .addValue("meetaltweektext", committee.getMeetAltWeekText());
    }

    private static MapSqlParameterSource getCommitteeMemberParams(CommitteeMember committeeMember, CommitteeVersionId cvid) {
        return getCommitteeVersionIdParams(cvid)
                .addValue("session_member_id", committeeMember.getSessionMember().getSessionMemberId())
                .addValue("sequence_no", committeeMember.getSequenceNo())
                .addValue("title", committeeMember.getTitle().asSqlEnum())
                .addValue("majority", committeeMember.isMajority());
    }
}
