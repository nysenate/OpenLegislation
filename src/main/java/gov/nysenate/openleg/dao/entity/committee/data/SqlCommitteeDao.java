package gov.nysenate.openleg.dao.entity.committee.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.entity.member.data.MemberDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static gov.nysenate.openleg.dao.entity.committee.data.SqlCommitteeQuery.*;

@Repository
public class SqlCommitteeDao extends SqlBaseDao implements CommitteeDao
{
    public static final Logger logger = LoggerFactory.getLogger(SqlCommitteeDao.class);

    @Autowired
    MemberDao memberDao;
    @Autowired
    MemberService memberService;

    /**
     * {@inheritDoc}
     * */
    @Override
    public Committee getCommittee(CommitteeId committeeId) throws DataAccessException {
        logger.debug("Looking up committee " + committeeId);
        MapSqlParameterSource params = getCommitteeSessionIdParams(
                new CommitteeSessionId(committeeId, SessionYear.current()));
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(SELECT_COMMITTEE_CURRENT_SQL.getSql(schema()), params, rowHandler);
        try {
            return rowHandler.getCommitteeList().get(0);
        }
        catch(IndexOutOfBoundsException e){
            throw new EmptyResultDataAccessException("Could not find committee in db: " + committeeId, 1, e);
        }
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public Committee getCommittee(CommitteeVersionId committeeVersionId) {
        logger.debug("Looking up committee " + committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(SELECT_COMMITTEE_AT_DATE_SQL.getSql(schema()), params, rowHandler);
        try {
            return rowHandler.getCommitteeList().get(0);
        }
        catch(IndexOutOfBoundsException e){
            throw new EmptyResultDataAccessException("Could not find committee version in db: " + committeeVersionId, 1, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeId> getCommitteeList() {
        return jdbcNamed.query(SELECT_COMMITTEE_ID.getSql(schema(),
                        new OrderBy("chamber", SortOrder.ASC, "name", SortOrder.ASC), LimitOffset.ALL),
                                                        new CommitteeIdRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SessionYear> getEligibleYears() throws DataAccessException {
        return jdbcNamed.query(SELECT_SESSION_YEARS.getSql(schema(), new OrderBy("session_year", SortOrder.ASC), LimitOffset.ALL),
                (rs, num) -> getSessionYearFromRs(rs, "session_year"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CommitteeSessionId> getAllSessionIds() throws DataAccessException {
        return jdbcNamed.query(SELECT_COMMITTEE_SESSION_IDS.getSql(schema(),
                new OrderBy("session_year", SortOrder.ASC), LimitOffset.ALL),
                new CommitteeSessionIdRowMapper());
    }

    /**
     * {@inheritDoc}
     * @param committeeSessionId
     */
    @Override
    public List<Committee> getCommitteeHistory(CommitteeSessionId committeeSessionId) throws DataAccessException {
        List<Committee> committeeHistory = selectCommitteeVersionHistory(committeeSessionId);
        if (committeeHistory.isEmpty()) {
            throw new EmptyResultDataAccessException("No committee history found for given committee id", 1);
        }
        return committeeHistory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCommittee(Committee committee, SobiFragment sobiFragment) {
        logger.info("Updating committee " + committee.getChamber() + " " + committee.getName());
        // Try to create a new committee
        if (insertCommittee(committee.getId())) {
            insertCommitteeVersion(committee, sobiFragment);
            updateCommitteeCurrentVersion(committee.getVersionId());
        }
        else {  // if that fails perform updates to an existing committee
            // delete all committee versions with a creation date after this one
            deleteFutureCommitteeVersions(committee.getVersionId());
            try {
                Committee existingCommittee = getCommittee(committee.getVersionId());
                updateExistingCommittee(committee, existingCommittee, sobiFragment);
            }
            catch (EmptyResultDataAccessException ex) { // No committee version exists for this session
                // Insert this committee as the first version of the session
                insertCommitteeVersion(committee, sobiFragment);
                updateCommitteeCurrentVersion(committee.getVersionId());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCommittee(CommitteeId committeeId) {
        logger.info("Deleting all records for " + committeeId);
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        jdbcNamed.update(DELETE_COMMITTEE.getSql(schema()),params);
    }

    /** --- Private Methods --- */

    /**
     * Tries to insert a new committee into the database from the given parameter
     * @param committeeId
     * @return true if a new committee was created, false if the committee already exists
     */
    private boolean insertCommittee(CommitteeId committeeId) {
        logger.debug("Creating new committee " + committeeId);
        // Create the committee
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        try {
            jdbcNamed.update(INSERT_COMMITTEE.getSql(schema()), params);
            logger.info("Created new committee " + committeeId);
        }
        catch(DuplicateKeyException e) {
            logger.debug("\tCommittee " + committeeId + " already exists");
            return false;
        }
        return true;
    }

    /**
     * Creates a record for a new version of a committee
     * @param committee
     */
    private void insertCommitteeVersion(Committee committee, SobiFragment sobiFragment){
        logger.debug("Inserting new version of " + committee.getVersionId());
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        addLastFragmentParam(sobiFragment, params);
        jdbcNamed.update(INSERT_COMMITTEE_VERSION.getSql(schema()), params);
        insertCommitteeMembers(committee);
    }

    /**
     * Inserts the committee members for a particular version of a committee
     * @param committee
     */
    private void insertCommitteeMembers(Committee committee){
        for (CommitteeMember committeeMember : committee.getMembers()) {
            MapSqlParameterSource params = getCommitteeMemberParams(committeeMember, committee.getVersionId());
            jdbcNamed.update(INSERT_COMMITTEE_MEMBER.getSql(schema()), params);
        }
    }

    /**
     * Gets committee versions fo a given comittee, within the specified dateRange
     * @param committeeSessionId
     * @return
     */
    private List<Committee> selectCommitteeVersionHistory(CommitteeSessionId committeeSessionId){
        MapSqlParameterSource params = getCommitteeSessionIdParams(committeeSessionId);
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(SELECT_COMMITTEE_VERSION_HISTORY.getSql(
                                                schema(), new OrderBy("created", SortOrder.DESC), LimitOffset.ALL),
                                            params, rowHandler);
        return rowHandler.getCommitteeList();
    }

    /**
     * Retrieves the committee version with the next lowest created date from the given committee version
     * @param committeeVersionId
     * @return
     */
    private Committee selectPreviousCommittee(CommitteeVersionId committeeVersionId){
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        CommitteeRowHandler rowHandler = new CommitteeRowHandler();
        jdbcNamed.query(SELECT_PREVIOUS_COMMITTEE_VERSION.getSql(schema()), params, rowHandler);
        try {
            return rowHandler.getCommitteeList().get(0);
        }
        catch(IndexOutOfBoundsException e){
            return null;
        }
    }

    /**
     * Modifies the record of an existing committee to create a new version of the committee
     * @param committee
     * @param sobiFragment
     */
    private void updateExistingCommittee(Committee committee, Committee existingCommittee, SobiFragment sobiFragment){
        logger.debug("Updating committee " + committee.getChamber() + " " + committee.getName() +
                     " published on " + committee.getPublishedDateTime());

        if (!committee.membersEquals(existingCommittee)) { // if there has been a change in membership
            logger.debug("\tMember discrepancy detected.. creating new version");
            // replace existing committee if they share the same creation date
            if (committee.getPublishedDateTime().equals(existingCommittee.getPublishedDateTime())) {
                deleteCommitteeVersion(existingCommittee.getVersionId());
                Committee previousCommittee = selectPreviousCommittee(existingCommittee.getVersionId());
                if (previousCommittee!=null && committee.membersEquals(previousCommittee)) {
                // Merge with previous committee if same membership
                    mergeCommittees(previousCommittee, committee, sobiFragment);
                    committee = previousCommittee;
                }
                else { // Create a new version of the committee
                    insertCommitteeVersion(committee, sobiFragment);
                }
            }
            else { // Create a new version of the committee and update reformed for existing committee
                insertCommitteeVersion(committee, sobiFragment);
                existingCommittee.setReformed(committee.getPublishedDateTime());
                updateCommitteeReformed(existingCommittee, sobiFragment);
            }

            // Update references
            updateCommitteeCurrentVersion(committee.getVersionId());
        }
        // If there has been a change in meeting protocol
        else if (!committee.meetingEquals(existingCommittee)) {
            logger.debug("\tMeeting discrepancy detected.. updating version");
            // Update the meeting information for the existing version
            existingCommittee.updateMeetingInfo(committee);
            updateCommitteeMeetingInfo(existingCommittee, sobiFragment);
        }
        else {
            logger.debug("\tNo changes detected, no updates performed");
        }

    }

    /**
     * Modifies the record of a given committee version update data relating to meetings
     * @param committee
     */
    private void updateCommitteeMeetingInfo(Committee committee, SobiFragment sobiFragment){
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        addLastFragmentParam(sobiFragment, params);
        jdbcNamed.update(UPDATE_COMMITTEE_MEETING_INFO.getSql(schema()), params);
    }

    /**
     * Sets the reformed date for a given commitee version
     * @param committee
     */
    private void updateCommitteeReformed(Committee committee, SobiFragment sobiFragment){
        logger.debug("updating reformed date for" + committee.getVersionId() + " to " + committee.getReformed());
        MapSqlParameterSource params = getCommitteeVersionIdParams(committee.getVersionId());
        addLastFragmentParam(sobiFragment, params);
        params.addValue("reformed", DateUtils.toDate(committee.getReformed()));
        jdbcNamed.update(UPDATE_COMMITTEE_VERSION_REFORMED.getSql(schema()), params);
    }

    /**
     * Updates the current version of a committee record to the version specified by the given committee
     * @param committeeVersionId
     */
    private void updateCommitteeCurrentVersion(CommitteeVersionId committeeVersionId) {
        logger.debug("updating current version of " + committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(UPDATE_COMMITTEE_CURRENT_VERSION.getSql(schema()), params);
    }

    /**
     * Given two committees, merges records for the second committee into the first creating a single version record
     * @param first
     * @param second
     */
    private void mergeCommittees(Committee first, Committee second, SobiFragment sobiFragment) {
        first.updateMeetingInfo(second);
        first.setReformed(second.getReformed());
        deleteCommitteeVersion(second.getVersionId());
        updateCommitteeMeetingInfo(first, sobiFragment);
        updateCommitteeReformed(first, sobiFragment);
        if(second.isCurrent()){
            updateCommitteeCurrentVersion(first.getVersionId());
        }
    }

    /**
     * Removes all committee versions for the given committee that occur after the given created date
     * @param committeeVersionId
     */
    protected void deleteFutureCommitteeVersions(CommitteeVersionId committeeVersionId) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(DELETE_COMMITTEE_VERSION_FUTURE.getSql(schema()), params);
    }

    /**
     * Removes the all entries for a given committee version
     * @param committeeVersionId
     */
    private void deleteCommitteeVersion(CommitteeVersionId committeeVersionId) {
        deleteCommitteeMembers(committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(DELETE_COMMITTEE_VERSION.getSql(schema()), params);
    }

    /**
     * Removes all committee member records for a given committee version
     * @param committeeVersionId
     */
    private void deleteCommitteeMembers(CommitteeVersionId committeeVersionId) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(DELETE_COMMITTEE_MEMBERS.getSql(schema()), params);
    }

    /** --- Row Mappers --- */

    protected class CommitteeIdRowMapper implements RowMapper<CommitteeId>
    {
        @Override
        public CommitteeId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommitteeId(
                    Chamber.getValue(rs.getString("chamber")),
                    rs.getString("name")
            );
        }
    }

    protected class CommitteeSessionIdRowMapper implements RowMapper<CommitteeSessionId>
    {
        @Override
        public CommitteeSessionId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommitteeSessionId(
                    Chamber.getValue(rs.getString("chamber")),
                    rs.getString("committee_name"),
                    getSessionYearFromRs(rs, "session_year")
            );
        }
    }

    protected class CommitteeVersionIdRowMapper implements RowMapper<CommitteeVersionId>
    {
        protected CommitteeSessionIdRowMapper sessionIdRowMapper = new CommitteeSessionIdRowMapper();

        @Override
        public CommitteeVersionId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CommitteeVersionId(
                    sessionIdRowMapper.mapRow(rs, rowNum),
                    getLocalDateTimeFromRs(rs, "created")
            );
        }
    }

    protected class CommitteeRowMapper implements RowMapper<Committee>
    {
        @Override
        public Committee mapRow(ResultSet rs, int i) throws SQLException {
            Committee committee = new Committee();
            committee.setName(rs.getString("committee_name"));
            committee.setChamber(Chamber.getValue(rs.getString("chamber")));
            committee.setPublishedDateTime(getLocalDateTimeFromRs(rs, "created"));
            LocalDateTime reformed = getLocalDateTimeFromRs(rs, "reformed");
            committee.setReformed(reformed.isBefore(LocalDateTime.now()) ? reformed : null);
            committee.setLocation(rs.getString("location"));
            committee.setMeetDay(StringUtils.isNotEmpty(rs.getString("meetday"))
                ? DayOfWeek.valueOf(rs.getString("meetday").toUpperCase()) : null);
            committee.setMeetTime(rs.getTime("meettime") != null ? rs.getTime("meettime").toLocalTime() : null);
            committee.setMeetAltWeek(rs.getBoolean("meetaltweek"));
            committee.setMeetAltWeekText(rs.getString("meetaltweektext"));
            committee.setSession(getSessionYearFromRs(rs, "session_year"));
            return committee;
        }
    }

    protected class CommitteeMemberRowMapper implements RowMapper<CommitteeMember>
    {
        @Override
        public CommitteeMember mapRow(ResultSet rs, int i) throws SQLException {
            CommitteeMember committeeMember = new CommitteeMember();
            committeeMember.setSequenceNo(rs.getInt("sequence_no"));
            int sessionMemberId = rs.getInt("session_member_id");
            try {
                committeeMember.setMember(memberService.getMemberBySessionId(sessionMemberId));
            }
            catch (MemberNotFoundEx memberNotFoundEx) {
                logger.error(String.valueOf(memberNotFoundEx));
            }
            committeeMember.setTitle(CommitteeMemberTitle.valueOfSqlEnum(rs.getString("title")));
            committeeMember.setMajority(rs.getBoolean("majority"));
            return committeeMember;
        }
    }

    protected class CommitteeRowHandler implements RowCallbackHandler
    {
        protected CommitteeRowMapper committeeRowMapper = new CommitteeRowMapper();
        protected CommitteeMemberRowMapper committeeMemberRowMapper = new CommitteeMemberRowMapper();
        protected CommitteeVersionIdRowMapper versionIdRowMapper = new CommitteeVersionIdRowMapper();

        protected Map<CommitteeVersionId, Committee> committeeMap;

        public CommitteeRowHandler() {
            committeeMap = new LinkedHashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            CommitteeVersionId versionId = versionIdRowMapper.mapRow(rs, rs.getRow());
            if (!committeeMap.containsKey(versionId)) {
                committeeMap.put(versionId, committeeRowMapper.mapRow(rs, rs.getRow()));
            }
            committeeMap.get(versionId).addMember(committeeMemberRowMapper.mapRow(rs, rs.getRow()));
        }

        public Map<CommitteeVersionId, Committee> getCommitteeMap() {
            return committeeMap;
        }

        public List<Committee> getCommitteeList() {
            return new ArrayList<>(committeeMap.values());
        }
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getCommitteeIdParams(CommitteeId cid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committeeName", cid.getName());
        params.addValue("chamber", cid.getChamber().asSqlEnum());
        return params;
    }

    private MapSqlParameterSource getCommitteeSessionIdParams(CommitteeSessionId csid) {
        MapSqlParameterSource params = getCommitteeIdParams(csid);
        params.addValue("sessionYear", csid.getSession().getYear());
        return params;
    }

    private MapSqlParameterSource getCommitteeVersionIdParams(CommitteeVersionId cvid) {
        MapSqlParameterSource params = getCommitteeSessionIdParams(cvid);
        params.addValue("referenceDate", DateUtils.toDate(cvid.getReferenceDate()));
        return params;
    }

    private MapSqlParameterSource getCommitteeVersionParams(Committee committee) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committee.getVersionId());
        params.addValue("location", committee.getLocation());
        params.addValue("meetday", committee.getMeetDay() != null ? committee.getMeetDay().toString() : null);
        params.addValue("meettime", DateUtils.toTime(committee.getMeetTime()));
        params.addValue("meetaltweek", committee.isMeetAltWeek());
        params.addValue("meetaltweektext", committee.getMeetAltWeekText());
        return params;
    }

    private MapSqlParameterSource getCommitteeMemberParams(CommitteeMember committeeMember, CommitteeVersionId cvid) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(cvid);
        params.addValue("session_member_id", committeeMember.getMember().getSessionMemberId());
        params.addValue("sequence_no", committeeMember.getSequenceNo());
        params.addValue("title", committeeMember.getTitle().asSqlEnum());
        params.addValue("majority", committeeMember.isMajority());
        return params;
    }
}
