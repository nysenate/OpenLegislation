package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.service.entity.MemberService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

@Repository
public class SqlCommitteeDao extends SqlBaseDao implements CommitteeDao
{
    public static final Logger logger = LoggerFactory.getLogger(SqlCommitteeDao.class);

    @Autowired
    MemberDao memberDao;
    @Autowired
    MemberService memberService;

    /**
     * @inheritDoc
     * */
    @Override
    public Committee getCommittee(CommitteeId committeeId) throws DataAccessException {
        logger.debug("Looking up committee " + committeeId);
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        try {
            Committee committee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_COMMITTEE_CURRENT_SQL.getSql(schema()),
                                                           params, new CommitteeRowMapper());
            committee.setMembers(selectCommitteeMembers(committee.getVersionId()));
            return committee;
        }
        catch(Exception e){
            throw new EmptyResultDataAccessException("Could not find committee in db: " + committeeId, 1, e);
        }
    }

    /**
     * @inheritDoc
     * */
    @Override
    public Committee getCommittee(CommitteeVersionId committeeVersionId) {
        logger.debug("Looking up committee " + committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        Committee committee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_COMMITTEE_AT_DATE_SQL.getSql(schema()),
                params, new CommitteeRowMapper());
        committee.setMembers(selectCommitteeMembers(committee.getVersionId()));
        return committee;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Committee> getCommitteeList(Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chamber", chamber.asSqlEnum());
        List<Committee> allCommittees = jdbcNamed.query(SqlCommitteeQuery.SELECT_ALL_COMMITTEES.getSql(schema()),
                                                        params, new CommitteeRowMapper());
        for (Committee committee : allCommittees) {
            committee.setMembers(selectCommitteeMembers(committee.getVersionId()));
        }
        return allCommittees;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Committee> getCommitteeHistory(CommitteeId committeeId) {
        List<Committee> committeeHistory = selectAllCommitteeVersions(committeeId);
        for (Committee committee : committeeHistory) {
            committee.setMembers(selectCommitteeMembers(committee.getVersionId()));
        }
        Collections.sort(committeeHistory, Committee.BY_DATE);
        return committeeHistory;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateCommittee(Committee committee) {
        logger.info("Updating committee " + committee.getChamber() + " " + committee.getName());
        // Try to create a new committee
        if (insertCommittee(committee.getVersionId())) {
            insertCommitteeVersion(committee);
            updateCommitteeCurrentVersion(committee.getVersionId());
        }
        else {  // if that fails perform updates to an existing committee
            try {
                Committee existingCommittee = getCommittee(committee.getVersionId());
                updateExistingCommittee(committee, existingCommittee);
            }
            catch (EmptyResultDataAccessException ex) { // No committee version exists for this session
                // Insert this committee as the first version of the session
                insertCommitteeVersion(committee);
                updateCommitteeCurrentVersion(committee.getVersionId());
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteCommittee(CommitteeId committeeId) {
        logger.info("Deleting all records for " + committeeId);
        List<Committee> allCommitteeVersions = selectAllCommitteeVersions(committeeId);
        if (allCommitteeVersions != null) {
            for (Committee committee : allCommitteeVersions) {
                deleteCommitteeVersion(committee.getVersionId());
            }
        }
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE.getSql(schema()),params);
    }

    /** --- Private Methods --- */

    /**
     * Tries to insert a new committee into the database from the given parameter
     * @param committeeVersionId
     * @return true if a new committee was created, false if the committee already exists
     */
    private boolean insertCommittee(CommitteeVersionId committeeVersionId) {
        logger.debug("Creating new committee " + committeeVersionId);
        // Create the committee
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        try {
            jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE.getSql(schema()), params);
            logger.info("Created new committee " + committeeVersionId);
        }
        catch(DuplicateKeyException e) {
            logger.debug("\tCommittee " + committeeVersionId + " already exists");
            return false;
        }
        return true;
    }

    /**
     * Creates a record for a new version of a committee
     * @param committee
     */
    private void insertCommitteeVersion(Committee committee){
        logger.debug("Inserting new version of " + committee.getVersionId());
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE_VERSION.getSql(schema()), params);
        insertCommitteeMembers(committee);
    }

    /**
     * Inserts the committee members for a particular version of a committee
     * @param committee
     */
    private void insertCommitteeMembers(Committee committee){
        for (CommitteeMember committeeMember : committee.getMembers()) {
            MapSqlParameterSource params = getCommitteeMemberParams(committeeMember, committee.getVersionId());
            jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE_MEMBER.getSql(schema()), params);
        }
    }

    /**
     * Gets all committee members for a given committee version
     * @param committeeVersionId
     * @return
     */
    private List<CommitteeMember> selectCommitteeMembers(CommitteeVersionId committeeVersionId) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        return jdbcNamed.query(SqlCommitteeQuery.SELECT_COMMITTEE_MEMBERS.getSql(schema()),
                               params, new CommitteeMemberRowMapper());
    }

    /**
     * Gets all committee versions fo a given comittee
     * @param committeeId
     * @return
     */
    private List<Committee> selectAllCommitteeVersions(CommitteeId committeeId){
        MapSqlParameterSource params = getCommitteeIdParams(committeeId);
        return jdbcNamed.query(SqlCommitteeQuery.SELECT_ALL_COMMITTEE_VERSIONS.getSql(schema()),
                                            params, new CommitteeRowMapper());
    }

    /**
     * Retrieves the committee version with the next lowest created date from the given committee version
     * @param committeeVersionId
     * @return
     */
    private Committee selectPreviousCommittee(CommitteeVersionId committeeVersionId){
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        try {
            Committee previousCommittee =
                jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_PREVIOUS_COMMITTEE_VERSION.getSql(schema()),
                                         params, new CommitteeRowMapper());
            previousCommittee.setMembers(selectCommitteeMembers(previousCommittee.getVersionId()));
            return previousCommittee;
        }
        catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    /**
     * Retrieves the committee version with the next highest created date from the given committee version
     * @param committeeVersionId
     * @return
     */
    private Committee selectNextCommittee(CommitteeVersionId committeeVersionId){
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        Committee nextCommittee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_NEXT_COMMITTEE_VERSION.getSql(schema()),
                params, new CommitteeRowMapper());
        nextCommittee.setMembers(selectCommitteeMembers(nextCommittee.getVersionId()));
        return nextCommittee;
    }

    /**
     * Modifies the record of an existing committee to create a new version of the committee
     * @param committee
     */
    private void updateExistingCommittee(Committee committee, Committee existingCommittee){
        logger.debug("Updating committee " + committee.getChamber() + " " + committee.getName() +
                     " published on " + committee.getPublishedDateTime());

        if (!committee.membersEquals(existingCommittee)) { // if there has been a change in membership
            logger.debug("\tMember discrepancy detected.. creating new version");
            committee.setReformed(existingCommittee.getReformed());
            // replace existing committee if they share the same creation date
            if (committee.getPublishedDateTime().equals(existingCommittee.getPublishedDateTime())) {
                deleteCommitteeVersion(existingCommittee.getVersionId());
                Committee previousCommittee = selectPreviousCommittee(existingCommittee.getVersionId());
                if (previousCommittee!=null && committee.membersEquals(previousCommittee)) {
                // Merge with previous committee if same membership
                    mergeCommittees(previousCommittee, committee);
                    committee = previousCommittee;
                }
                else { // Create a new version of the committee
                    insertCommitteeVersion(committee);
                }
            }
            else { // Create a new version of the committee and update reformed for existing committee
                insertCommitteeVersion(committee);
                existingCommittee.setReformed(committee.getPublishedDateTime());
                updateCommitteeReformed(existingCommittee);
            }

            if (committee.isCurrent()) { // Update references
                updateCommitteeCurrentVersion(committee.getVersionId());
            }
            else {
                Committee nextCommittee = selectNextCommittee(committee.getVersionId());
                if (committee.membersEquals(nextCommittee)) { //  Merge with next committee if same membership
                    mergeCommittees(committee, nextCommittee);
                }
                else {
                    committee.setReformed(nextCommittee.getPublishedDateTime());
                    updateCommitteeReformed(committee);
                }
            }
        }
        // If there has been a change in meeting protocol
        else if (!committee.meetingEquals(existingCommittee)) {
            logger.debug("\tMeeting discrepancy detected.. updating version");
            // Update the meeting information for the existing version
            existingCommittee.updateMeetingInfo(committee);
            updateCommitteeMeetingInfo(existingCommittee);
        }
        else {
            logger.debug("\tNo changes detected, no updates performed");
        }

    }

    /**
     * Modifies the record of a given committee version update data relating to meetings
     * @param committee
     */
    private void updateCommitteeMeetingInfo(Committee committee){
        MapSqlParameterSource params = getCommitteeVersionParams(committee);
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_MEETING_INFO.getSql(schema()), params);
    }

    /**
     * Sets the reformed date for a given commitee version
     * @param committee
     */
    private void updateCommitteeReformed(Committee committee){
        logger.debug("updating reformed date for" + committee.getVersionId() + " to " + committee.getReformed());
        MapSqlParameterSource params = getCommitteeVersionIdParams(committee.getVersionId());
        params.addValue("reformed", committee.getReformed());
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_VERSION_REFORMED.getSql(schema()), params);
    }

    /**
     * Updates the current version of a committee record to the version specified by the given committee
     * @param committeeVersionId
     */
    private void updateCommitteeCurrentVersion(CommitteeVersionId committeeVersionId) {
        logger.debug("updating current version of " + committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_CURRENT_VERSION.getSql(schema()), params);
    }

    /**
     * Given two committees, merges records for the second committee into the first creating a single version record
     * @param first
     * @param second
     */
    private void mergeCommittees(Committee first, Committee second) {
        first.updateMeetingInfo(second);
        first.setReformed(second.getReformed());
        deleteCommitteeVersion(second.getVersionId());
        updateCommitteeReformed(first);
        if(second.isCurrent()){
            updateCommitteeCurrentVersion(first.getVersionId());
        }
    }

    /**
     * Removes the all entries for a given committee version
     * @param committeeVersionId
     */
    private void deleteCommitteeVersion(CommitteeVersionId committeeVersionId) {
        deleteCommitteeMembers(committeeVersionId);
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE_VERSION.getSql(schema()), params);
    }

    /**
     * Removes all committee member records for a given committee version
     * @param committeeVersionId
     */
    private void deleteCommitteeMembers(CommitteeVersionId committeeVersionId) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committeeVersionId);
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE_MEMBERS.getSql(schema()), params);
    }

    /** --- Row Mappers --- */

    private static class CommitteeRowMapper implements RowMapper<Committee>
    {
        @Override
        public Committee mapRow(ResultSet rs, int i) throws SQLException {
            Committee committee = new Committee();
            committee.setName(rs.getString("committee_name"));
            committee.setChamber(Chamber.getValue(rs.getString("chamber")));
            committee.setPublishedDateTime(getLocalDateTimeFromRs(rs, "created"));
            committee.setReformed(getLocalDateTimeFromRs(rs, "reformed"));
            committee.setLocation(rs.getString("location"));
            committee.setMeetDay(rs.getString("meetday"));
            committee.setMeetTime(rs.getTime("meettime"));
            committee.setMeetAltWeek(rs.getBoolean("meetaltweek"));
            committee.setMeetAltWeekText(rs.getString("meetaltweektext"));
            committee.setSession(getSessionYearFromRs(rs, "session_year"));
            return committee;
        }
    }

    private class CommitteeMemberRowMapper implements RowMapper<CommitteeMember>
    {
        @Override
        public CommitteeMember mapRow(ResultSet rs, int i) throws SQLException {
            CommitteeMember committeeMember = new CommitteeMember();
            committeeMember.setSequenceNo(rs.getInt("sequence_no"));
            int sessionMemberId = rs.getInt("session_member_id");
            SessionYear sessionYear = getSessionYearFromRs(rs, "session_year");
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

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getCommitteeIdParams(CommitteeId cid) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committeeName", cid.getName());
        params.addValue("chamber", cid.getChamber().asSqlEnum());
        return params;
    }

    private MapSqlParameterSource getCommitteeVersionIdParams(CommitteeVersionId cvid) {
        MapSqlParameterSource params = getCommitteeIdParams(cvid);
        params.addValue("sessionYear", cvid.getSession().getYear());
        params.addValue("referenceDate", DateUtils.toDate(cvid.getReferenceDate()));
        return params;
    }

    private MapSqlParameterSource getCommitteeVersionParams(Committee committee) {
        MapSqlParameterSource params = getCommitteeVersionIdParams(committee.getVersionId());
        params.addValue("location", committee.getLocation());
        params.addValue("meetday", committee.getMeetDay());
        params.addValue("meettime", committee.getMeetTime());
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
