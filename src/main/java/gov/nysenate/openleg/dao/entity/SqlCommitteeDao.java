package gov.nysenate.openleg.dao.entity;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;


@Repository
public class SqlCommitteeDao extends SqlBaseDao implements CommitteeDao{

    public static final Logger logger = LoggerFactory.getLogger(SqlCommitteeDao.class);

    @Autowired
    MemberDao memberDao;

    /**
     * @inheritDoc
     * */
    @Override
    public Committee getCommittee(String name, Chamber chamber) {
        logger.debug("Looking up committee " + chamber + " " + name);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
        params.addValue("chamber", chamber.asSqlEnum());
        try {
            Committee committee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_COMMITTEE_CURRENT_SQL.getSql(schema()),
                    params, new CommitteeRowMapper());
            committee.setMembers(selectCommitteeMembers(committee));
            return committee;
        }
        catch(EmptyResultDataAccessException e){
            logger.error("Could not find committee in db: " + chamber + " " + name);
            return null;
        }
    }

    /**
     * @inheritDoc
     * */
    @Override
    public Committee getCommittee(String name, Chamber chamber, Date date) {
        logger.debug("Looking up committee " + chamber + " " + name + " at " + date);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", name);
        params.addValue("chamber", chamber.asSqlEnum());
        params.addValue("date", date);
        try {
            Committee committee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_COMMITTEE_AT_DATE_SQL.getSql(schema()),
                    params, new CommitteeRowMapper());
            committee.setMembers(selectCommitteeMembers(committee));
            return committee;
        }
        catch(EmptyResultDataAccessException e){
            logger.error("Could not find committee version in db: " + chamber + " " + name + " at " + date);
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Committee> getCommitteeList(Chamber chamber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("chamber", chamber.asSqlEnum());
        try{
            List<Committee> allCommittees = jdbcNamed.query(SqlCommitteeQuery.SELECT_ALL_COMMITTEES.getSql(schema()),
                                                            params, new CommitteeRowMapper());
            for(Committee committee : allCommittees){
                committee.setMembers(selectCommitteeMembers(committee));
            }
            return allCommittees;
        }
        catch(EmptyResultDataAccessException e){
            logger.error("Could not find committees for " + chamber );
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Committee> getCommitteeHistory(String name, Chamber chamber) {
        try {
            List<Committee> committeeHistory = selectAllCommitteeVersions(new Committee(name, chamber));
            for(Committee committee : committeeHistory){
                committee.setMembers(selectCommitteeMembers(committee));
            }
            Collections.sort(committeeHistory, Committee.BY_DATE);
            return committeeHistory;
        }
        catch(EmptyResultDataAccessException e){
            logger.error("Could not find committee in db: " + chamber + " " + name);
            return null;
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void updateCommittee(Committee committee) {
        logger.info("Updating committee " + committee.getChamber() + " " + committee.getName());
        // Try to create a new committee
        if (insertCommittee(committee)) {
            insertCommitteeVersion(committee);
            updateCommitteeCurrentVersion(committee);
        } else {  // if that fails perform updates to an existing committee
            Committee existingCommittee = getCommittee(committee.getName(), committee.getChamber(), committee.getPublishDate());
            updateExistingCommittee(committee, existingCommittee);
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteCommittee(Committee committee) {
        logger.info("Deleting all records for " + committee.getChamber() + " " + committee.getName());
        List<Committee> allCommitteeVersions = selectAllCommitteeVersions(committee);
        if(allCommitteeVersions!=null) {
            for (Committee committeeVersion : allCommitteeVersions) {
                deleteCommitteeVersion(committeeVersion);
            }
        }
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE.getSql(schema()),params);
    }

    /** --- Private Methods --- */

    /**
     * Tries to insert a new committee into the database from the given parameter
     * @param committee
     * @return true if a new committee was created, false if the committee already exists
     */
    private boolean insertCommittee(Committee committee){
        logger.debug("creating new committee " + committee.getChamber() + " " + committee.getName());
        // Create the committee
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        try {
            jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE.getSql(schema()), params);

            logger.info("created new committee " + committee.getChamber() + " " + committee.getName());
        }
        catch(DuplicateKeyException e){
            logger.debug("\tCommittee " + committee.getChamber() + " " + committee.getName() + " already exists");
            return false;
        }
        return true;
    }

    /**
     * Creates a record for a new version of a committee
     * @param committee
     */
    private void insertCommitteeVersion(Committee committee){
        logger.debug("inserting new version of " + committee.getChamber() + " " + committee.getName() + " " + committee.getPublishDate());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("location", committee.getLocation());
        params.addValue("meetday", committee.getMeetDay());
        params.addValue("meettime", committee.getMeetTime());
        params.addValue("meetaltweek", committee.isMeetAltWeek());
        params.addValue("meetaltweektext", committee.getMeetAltWeekText());
        params.addValue("created", committee.getPublishDate());
        jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE_VERSION.getSql(schema()), params);

        insertCommitteeMembers(committee);
    }

    /**
     * Inserts the committee members for a particular version of a committee
     * @param committee
     */
    private void insertCommitteeMembers(Committee committee){
        for(CommitteeMember committeeMember : committee.getMembers()){
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("committee_name", committee.getName());
            params.addValue("chamber", committee.getChamber().asSqlEnum());
            params.addValue("version_created", committee.getPublishDate());
            params.addValue("member_id", committeeMember.getMember().getMemberId());
            params.addValue("session_year", committee.getSession());
            params.addValue("sequence_no", committeeMember.getSequenceNo());
            params.addValue("title", committeeMember.getTitle().asSqlEnum());
            params.addValue("majority", committeeMember.isMajority());
            jdbcNamed.update(SqlCommitteeQuery.INSERT_COMMITTEE_MEMBER.getSql(schema()), params);
        }
    }

    /**
     * Gets all committee members for a given committee version
     * @param committee
     * @return
     */
    private List<CommitteeMember> selectCommitteeMembers(Committee committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("version_created", committee.getPublishDate());
        return jdbcNamed.query(SqlCommitteeQuery.SELECT_COMMITTEE_MEMBERS.getSql(schema()),
                               params, new CommitteeMemberRowMapper());
    }

    /**
     * Gets all committee versions fo a given comittee
     * @param committee
     * @return
     */
    private List<Committee> selectAllCommitteeVersions(Committee committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        return jdbcNamed.query(SqlCommitteeQuery.SELECT_ALL_COMMITTEE_VERSIONS.getSql(schema()),
                                            params, new CommitteeRowMapper());
    }

    /**
     * Retrieves the committee version with the next highest created date from the given committee version
     * @param committee
     * @return
     */
    private Committee selectNextCommittee(Committee committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("date", committee.getPublishDate());
        Committee nextCommittee = jdbcNamed.queryForObject(SqlCommitteeQuery.SELECT_NEXT_COMMITTEE_VERSION.getSql(schema()),
                params, new CommitteeRowMapper());
        nextCommittee.setMembers(selectCommitteeMembers(nextCommittee));
        return nextCommittee;
    }

    /**
     * Modifies the record of an existing committee to create a new version of the committee
     * @param committee
     */
    private void updateExistingCommittee(Committee committee, Committee existingCommittee){
        logger.debug("updating committee " + committee.getChamber() + " " + committee.getName() + " published on " + committee.getPublishDate());

        if(!committee.memberEquals(existingCommittee)){ // if there has been a change in membership
            logger.debug("\tmember discrepancy detected.. creating new version");
            // replace existing committee if they share the same creation date
            if(committee.getPublishDate().equals(existingCommittee.getPublishDate())){
                deleteCommitteeVersion(existingCommittee);
            }
            // Create a new version of the committee
            insertCommitteeVersion(committee);
            // Update references
            if(existingCommittee.isCurrent()){
                updateCommitteeCurrentVersion(committee);
            }
            else{
                Committee nextCommittee = selectNextCommittee(committee);
                if(committee.memberEquals(nextCommittee)){
                    mergeCommittees(committee, nextCommittee);
                }
                else {
                    committee.setReformed(nextCommittee.getPublishDate());
                    updateCommitteeReformed(committee);
                }
            }
            if(!committee.getPublishDate().equals(existingCommittee.getPublishDate())) {
                // If the existing committee was not replaced, update its reformed date
                existingCommittee.setReformed(committee.getPublishDate());
                updateCommitteeReformed(existingCommittee);
            }
        }
        else if(!committee.meetingEquals(existingCommittee)){   // if there has been a change in meeting protocol

            logger.debug("\tmeeting discrepancy detected.. updating version");
            // Update the meeting information for the existing version
            existingCommittee.updateMeetingInfo(committee);
            updateCommitteeMeetingInfo(existingCommittee);
        }
        else{
            logger.debug("\tNo changes detected, no updates performed");
        }

    }

    /**
     * Modifies the record of a given committee version update data relating to meetings
     * @param committee
     */
    private void updateCommitteeMeetingInfo(Committee committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("created", committee.getPublishDate());
        params.addValue("location", committee.getLocation());
        params.addValue("meetday", committee.getMeetDay());
        params.addValue("meettime", committee.getMeetTime());
        params.addValue("meetaltweek", committee.isMeetAltWeek());
        params.addValue("meetaltweektext", committee.getMeetAltWeekText());
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_MEETING_INFO.getSql(schema()), params);
    }

    /**
     * Sets the reformed date for a given commitee version
     * @param committee
     */
    private void updateCommitteeReformed(Committee committee){
        logger.debug("updating reformed date for" + committee.getChamber() + " " + committee.getName() + " " + committee.getPublishDate() + " to " + committee.getReformed());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("created", committee.getPublishDate());
        params.addValue("reformed", committee.getReformed());
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_VERSION_REFORMED.getSql(schema()), params);
    }

    /**
     * Updates the current version of a committee record to the version specified by the given committee
     * @param committee
     */
    private void updateCommitteeCurrentVersion(Committee committee){
        logger.debug("updating current version of " + committee.getChamber() + " " + committee.getName() + " to " + committee.getPublishDate());
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("current_version", committee.getPublishDate());
        params.addValue("session", committee.getSession());
        jdbcNamed.update(SqlCommitteeQuery.UPDATE_COMMITTEE_CURRENT_VERSION.getSql(schema()), params);
    }

    /**
     * Given two committees, merges records for the second committee into the first creating a single version record
     * @param first
     * @param second
     */
    private void mergeCommittees(Committee first, Committee second){
        first.updateMeetingInfo(second);
        first.setReformed(second.getReformed());
        deleteCommitteeVersion(second);
        updateCommitteeReformed(first);
        if(second.isCurrent()){
            updateCommitteeCurrentVersion(first);
        }
    }

    /**
     * Removes the all entries for a given committee version
     * @param committee
     */
    private void deleteCommitteeVersion(Committee committee){
        deleteCommitteeMembers(committee);

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("created", committee.getPublishDate());
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE_VERSION.getSql(schema()), params);
    }

    /**
     * Removes all committee member records for a given committee version
     * @param committee
     */
    private void deleteCommitteeMembers(Committee committee){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("committee_name", committee.getName());
        params.addValue("chamber", committee.getChamber().asSqlEnum());
        params.addValue("session_year", committee.getSession());
        params.addValue("version_created", committee.getPublishDate());
        jdbcNamed.update(SqlCommitteeQuery.DELETE_COMMITTEE_MEMBERS.getSql(schema()), params);
    }

    /** --- Row Mappers --- */

    private class CommitteeRowMapper implements RowMapper<Committee>
    {
        @Override
        public Committee mapRow(ResultSet rs, int i) throws SQLException {
            Committee committee = new Committee();

            committee.setName(rs.getString("committee_name"));
            committee.setChamber(Chamber.valueOfSqlEnum(rs.getString("chamber")));
            committee.setPublishDate(rs.getTimestamp("created"));
            committee.setReformed(rs.getDate("reformed"));
            committee.setLocation(rs.getString("location"));
            committee.setMeetDay(rs.getString("meetday"));
            committee.setMeetTime(rs.getTime("meettime"));
            committee.setMeetAltWeek(rs.getBoolean("meetaltweek"));
            committee.setMeetAltWeekText(rs.getString("meetaltweektext"));
            committee.setSession(rs.getInt("session_year"));
            return committee;
        }
    }

    private class CommitteeMemberRowMapper implements RowMapper<CommitteeMember>
    {
        @Override
        public CommitteeMember mapRow(ResultSet rs, int i) throws SQLException {
            CommitteeMember committeeMember = new CommitteeMember();
            committeeMember.setSequenceNo(rs.getInt("sequence_no"));
            int memberId = rs.getInt("member_id");
            int sessionYear = rs.getInt("session_year");
            committeeMember.setMember(memberDao.getMemberById(memberId,sessionYear));
            committeeMember.setTitle(CommitteeMemberTitle.valueOfSqlEnum(rs.getString("title")));
            committeeMember.setMajority(rs.getBoolean("majority"));
            return committeeMember;
        }
    }

}
