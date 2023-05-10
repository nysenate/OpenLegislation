package gov.nysenate.openleg.legislation.attendance;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.common.dao.BasicSqlQuery;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BillVoteType;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;

@Repository
public class SqlSenateVoteAttendanceDao extends SqlBaseDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlSenateVoteAttendanceDao.class);

    @Autowired
    private MemberService memberService;

    public SenateVoteAttendance getAttendance(VoteId voteId) {
        MapSqlParameterSource params = voteIdParams(voteId);
        SenateVoteAttendanceHandler handler = new SenateVoteAttendanceHandler(memberService);
        jdbcNamed.query(SqlSenateVoteAttendanceQuery.GET_ATTENDANCE.getSql(schema()), params, handler);
        return handler.getResults();
    }

    public void saveAttendance(SenateVoteAttendance attendance, String fragmentId) {
        SenateVoteAttendance previousAttendance = getAttendance(attendance.getVoteId());
        Map<Integer, SessionMember> prevRemoteMembers = createMemberMap(previousAttendance.getRemoteMembers());
        Map<Integer, SessionMember> newRemoteMembers = createMemberMap(attendance.getRemoteMembers());
        MapDifference<Integer, SessionMember> diff = Maps.difference(prevRemoteMembers, newRemoteMembers);

        // Delete members who are no longer on the remote attendance list.
        Map<Integer, SessionMember> toDelete = diff.entriesOnlyOnLeft();
        SenateVoteAttendance toDeleteAttendance = new SenateVoteAttendance(attendance);
        toDeleteAttendance.setRemoteMembers(toDelete.values());
        deleteAttendance(toDeleteAttendance, fragmentId);

        // Insert members who have been added to the remote attendance list.
        Map<Integer, SessionMember> toInsert = diff.entriesOnlyOnRight();
        SenateVoteAttendance toInsertAttendance = new SenateVoteAttendance(attendance);
        toInsertAttendance.setRemoteMembers(toInsert.values());
        insertAttendance(toInsertAttendance, fragmentId);

        // Update members who are still on the remote attendance list.
        Map<Integer, SessionMember> toUpdate = diff.entriesInCommon();
        SenateVoteAttendance toUpdateAttendance = new SenateVoteAttendance(attendance);
        toUpdateAttendance.setRemoteMembers(toUpdate.values());
        updateAttendance(toUpdateAttendance, fragmentId);
    }

    private void insertAttendance(SenateVoteAttendance attendance, String fragmentId) {
        String sql = SqlSenateVoteAttendanceQuery.INSERT_ATTENDANCE.getSql(schema());
        executeQuery(sql, attendance, fragmentId);
    }

    private void updateAttendance(SenateVoteAttendance attendance, String fragmentId) {
        String sql = SqlSenateVoteAttendanceQuery.UPDATE_ATTENDANCE.getSql(schema());
        executeQuery(sql, attendance, fragmentId);
    }

    private void deleteAttendance(SenateVoteAttendance attendance, String fragmentId) {
        String sql = SqlSenateVoteAttendanceQuery.DELETE_ATTENDANCE.getSql(schema());
        executeQuery(sql, attendance, fragmentId);
    }

    private void executeQuery(String sql, SenateVoteAttendance attendance, String fragmentId) {
        if (attendance.getRemoteMembers().isEmpty()) {
            return;
        }
        MapSqlParameterSource params = attendanceParams(attendance)
                .addValue("lastFragmentId", fragmentId);
        for (SessionMember sm : attendance.getRemoteMembers()) {
            params.addValue("sessionMemberId", sm.getSessionMemberId());
            jdbcNamed.update(sql, params);
        }
    }

    private Map<Integer, SessionMember> createMemberMap(List<SessionMember> members) {
        return members.stream()
                .collect(Collectors.toMap(SessionMember::getSessionMemberId, Function.identity()));
    }

    private MapSqlParameterSource voteIdParams(VoteId voteId) {
        return new MapSqlParameterSource()
                .addValue("voteDate", toDate(voteId.getVoteDate()))
                .addValue("sequenceNo", voteId.getSequenceNo())
                .addValue("voteType", voteId.getVoteType().name());
    }

    private MapSqlParameterSource attendanceParams(SenateVoteAttendance attendance) {
        return voteIdParams(attendance.getVoteId())
                .addValue("sessionYear", attendance.getSession().getYear())
                .addValue("modifiedDateTime", toDate(attendance.getModifiedDateTime()))
                .addValue("publishedDateTime", toDate(attendance.getPublishedDateTime()));
    }

    private enum SqlSenateVoteAttendanceQuery implements BasicSqlQuery {
        GET_ATTENDANCE("""
                SELECT (vote_date, sequence_no, vote_type, session_year, session_member_id,
                  published_date_time, modified_date_time)
                FROM {schema}.bill_vote_remote_attendance
                WHERE vote_date =  :voteDate
                  AND sequence_no = :sequenceNo
                  AND vote_type = :voteType
                """
        ),
        INSERT_ATTENDANCE("""
                INSERT INTO {schema}.bill_vote_remote_attendance(vote_date, sequence_no, vote_type, session_year,
                  session_member_id, published_date_time, modified_date_time, last_fragment_id)
                VALUES (:voteDate, :sequenceNo, :voteType, :sessionYear, :sessionMemberId, :publishedDateTime,
                  :modifiedDateTime, :lastFragmentId)
                """
        ),
        UPDATE_ATTENDANCE("""
                UPDATE {schema}.bill_vote_remote_attendance
                SET modified_date_time = :modifiedDateTime,
                  last_fragment_id = :lastFragmentId
                WHERE vote_date = :voteDate
                  AND sequence_no = :sequenceNo
                  AND vote_type = :voteType
                  AND session_member_id = :sessionMemberId
                """),
        DELETE_ATTENDANCE("""
                DELETE FROM {schema}.bill_vote_remote_attendance
                WHERE vote_date = :voteDate
                  AND sequence_no = :sequenceNo
                  AND vote_type = :voteType
                  AND session_member_id = :sessionMemberId
                """)
        ;

        private String sql;

        SqlSenateVoteAttendanceQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }
    }

    private class SenateVoteAttendanceHandler implements RowCallbackHandler {

        private VoteId voteId;
        private Set<Integer> sessionMemberIds = new HashSet<>();
        private SessionYear sessionYear;
        private LocalDateTime modifiedDateTime;
        private LocalDateTime publishedDateTime;
        private MemberService memberService;

        public SenateVoteAttendanceHandler(MemberService memberService) {
            this.memberService = memberService;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (voteId != null) {
                voteId = new VoteId(
                        getLocalDateFromRs(rs, "vote_date"),
                        rs.getInt("sequence_no"),
                        BillVoteType.valueOf(rs.getString("vote_type")));
            }
            if (sessionYear == null) {
                sessionYear = SessionYear.of(rs.getInt("session_year"));
            }
            if (modifiedDateTime == null) {
                modifiedDateTime = getLocalDateTimeFromRs(rs, "modified_date_time");
            }
            if (publishedDateTime == null) {
                publishedDateTime = getLocalDateTimeFromRs(rs, "published_date_time");
            }
            sessionMemberIds.add(rs.getInt("session_member_id"));
        }

        public SenateVoteAttendance getResults() {
            List<SessionMember> remoteMembers = sessionMemberIds.stream()
                    .map(id -> memberService.getSessionMemberById(id, sessionYear))
                    .collect(Collectors.toList());
            SenateVoteAttendance attendance = new SenateVoteAttendance(voteId, remoteMembers);
            attendance.setSession(sessionYear);
            attendance.setYear(voteId.getVoteDate().getYear());
            attendance.setModifiedDateTime(modifiedDateTime);
            attendance.setPublishedDateTime(publishedDateTime);
            return attendance;
        }
    }
}
