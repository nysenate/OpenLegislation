package gov.nysenate.openleg.legislation.agenda.dao;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.agenda.*;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillVote;
import gov.nysenate.openleg.legislation.bill.BillVoteId;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.legislation.committee.MemberNotFoundEx;
import gov.nysenate.openleg.legislation.member.SessionMember;
import gov.nysenate.openleg.legislation.member.dao.MemberService;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.ImmutableMap.of;
import static gov.nysenate.openleg.common.util.DateUtils.toDate;

@Repository
public class SqlAgendaVoteAddendumDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlAgendaVoteAddendumDao.class);
    @Autowired
    private MemberService memberService;

    /**
     * Returns a map of agenda vote addenda, keyed by addendum id, based on the agenda id parameters.
     */
    protected Map<String, AgendaVoteAddendum> getAgendaVoteAddenda(ImmutableParams agendaParams) {
        List<AgendaVoteAddendum> voteAddenda =
                jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDA_VOTE_ADDENDA.getSql(schema()), agendaParams, agendaVoteRowMapper);
        // Create a new map where the addenda are grouped by their id.
        Map<String, AgendaVoteAddendum> voteAddendaMap =
                new TreeMap<>(Maps.uniqueIndex(voteAddenda, AgendaVoteAddendum::getId));
        // Set the info committees for each addendum
        voteAddendaMap.forEach((id, addendum) -> {
            ImmutableParams agendaVoteParams = agendaParams.add(of("addendumId", id));
            addendum.setCommitteeVoteMap(getAgendaVoteCommitteeMap(agendaVoteParams));
        });
        return voteAddendaMap;
    }

    /**
     * Returns a map of the agenda vote committees with their associated items via the parameters
     * for an AgendaVoteAddendum.
     */
    private Map<CommitteeId, AgendaVoteCommittee> getAgendaVoteCommitteeMap(ImmutableParams agendaVoteParams) {
        List<AgendaVoteCommittee> voteComms = jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDA_VOTE_COMMITTEES.getSql(schema()),
                agendaVoteParams, agendaVoteCommRowMapper);

        // Set the Attendance for each vote committee
        for (AgendaVoteCommittee voteComm : voteComms) {
            CommitteeId committeeId = voteComm.getCommitteeId();
            ImmutableParams voteCommParams = agendaVoteParams.add(
                    of("committeeName", committeeId.getName(), "committeeChamber", committeeId.getChamber().asSqlEnum()));

            // Set the attendance list for each vote committee
            voteComm.setAttendance(queryAttendance(voteCommParams));
            // Set the bills that were voted on
            voteComm.setVotedBills(queryAgendaVoteBills(voteCommParams));
        }
        return new TreeMap<>(Maps.uniqueIndex(voteComms, AgendaVoteCommittee::getCommitteeId));
    }

    private List<AgendaVoteAttendance> queryAttendance(ImmutableParams voteCommParams) {
        // Attendance list should be ordered by rank of members.
        final OrderBy rankOrderBy = new OrderBy("rank", SortOrder.ASC);

        List<AgendaVoteAttendance> voteAttendances = jdbcNamed.query(
                SqlAgendaQuery.SELECT_AGENDA_VOTE_ATTENDANCE.getSql(schema(), rankOrderBy, LimitOffset.ALL),
                voteCommParams, new AgendaVoteAttendanceRowMapper()
        );
        // Set full session member information
        for (AgendaVoteAttendance va : voteAttendances) {
            try {
                va.setMember(memberService.getSessionMemberBySessionId(va.getMember().getSessionMemberId()));
            } catch (MemberNotFoundEx memberNotFoundEx) {
                logger.info("Failed to map member for attendance listing.");
            }
        }
        return voteAttendances;
    }

    private Map<BillId, AgendaVoteBill> queryAgendaVoteBills(ImmutableParams voteCommParams) {
        AgendaCommVoteHandler agendaVoteHandler = new AgendaCommVoteHandler();
        jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDA_COMM_VOTES.getSql(schema()), voteCommParams, agendaVoteHandler);
        Map<BillId, AgendaVoteBill> billVotes = agendaVoteHandler.getAgendaVoteBills();
        // Fully populate SessionMember objects for each BillVote
        for (AgendaVoteBill agendaVoteBill : billVotes.values()) {
            for (SessionMember member: agendaVoteBill.billVote().getMemberVotes().values()) {
                try {
                    SessionMember fullMember = memberService.getSessionMemberBySessionId(member.getSessionMemberId());
                    member.updateFromOther(fullMember);
                } catch (MemberNotFoundEx memberNotFoundEx) {
                    logger.error("Failed to add member vote since member could not be found!", memberNotFoundEx);
                }
            }
        }
        return billVotes;
    }

    /**
     * Delete any existing vote addenda that have been modified and insert any new or modified vote addenda.
     */
    protected void updateAgendaVoteAddenda(Agenda agenda, LegDataFragment legDataFragment) {
        ImmutableParams agendaIdParams = ImmutableParams.from(getAgendaIdParams(agenda.getId()));
        // Compute the differences between the new and existing addenda
        Map<String, AgendaVoteAddendum> existingAddenda = this.getAgendaVoteAddenda(agendaIdParams);
        Map<String, AgendaVoteAddendum> currentAddenda = agenda.getAgendaVoteAddenda();
        MapDifference<String, AgendaVoteAddendum> diff = Maps.difference(existingAddenda, currentAddenda);
        // Delete any removed or modified addenda. This will cascade to all items stored within the addenda.
        Sets.union(diff.entriesOnlyOnLeft().keySet(), diff.entriesDiffering().keySet())
                .forEach(id -> {
                    ImmutableParams addendumParams = agendaIdParams.add(of("addendumId", id));
                    jdbcNamed.update(SqlAgendaQuery.DELETE_AGENDA_VOTE_ADDENDUM.getSql(schema()), addendumParams);
                });
        // Update/insert any modified or new addenda
        Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet())
                .forEach(id -> insertAgendaVoteAddendum(currentAddenda.get(id), legDataFragment));
    }

    /**
     * Insert a record for the given AgendaVoteAddendum as well as the records for all the
     * AgendaVoteCommittees stored within this addendum.
     */
    private void insertAgendaVoteAddendum(AgendaVoteAddendum voteAddendum, LegDataFragment legDataFragment) {
        MapSqlParameterSource params = getAgendaVoteAddendumParams(voteAddendum, legDataFragment);
        jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_VOTE_ADDENDUM.getSql(schema()), params);
        voteAddendum.getCommitteeVoteMap()
                .forEach((id, voteComm) -> insertAgendaVoteCommittee(voteAddendum, voteComm, legDataFragment));
    }

    /**
     * Insert a record for the given AgendaVoteAddendum as well as the records for all the
     * AgendaVoteAttendance and AgendaVoteBill items stored within this committee vote object.
     * The AgendaVoteAddendum is needed since the AgendaVoteCommittee does not contain a reference
     * to its parent.
     */
    private void insertAgendaVoteCommittee(AgendaVoteAddendum addendum, AgendaVoteCommittee voteComm,
                                           LegDataFragment fragment) {
        MapSqlParameterSource params = getAgendaVoteCommParams(addendum, voteComm, fragment);
        jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_VOTE_COMMITTEE.getSql(schema()), params);
        // Insert the attendance list
        voteComm.getAttendance().forEach(attend -> {
            addAgendaVoteAttendParams(attend, params);
            jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_VOTE_ATTENDANCE.getSql(schema()), params);
        });
        // Insert the committee bill vote info.
        // NOTE: The actual bill votes are assumed to have already been persisted via the bill data layer.
        // This insert will reference the existing vote but will not insert it if it doesn't exist.
        voteComm.getVotedBills().forEach((billId, voteBill) -> {
            addAgendaBillVoteParams(voteBill, params);
            jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_COMM_BILL_VOTES.getSql(schema()), params);
        });
    }

    static RowMapper<AgendaVoteAddendum> agendaVoteRowMapper = (rs, rowNum) -> {
        AgendaVoteAddendum addendum = new AgendaVoteAddendum();
        addendum.setAgendaId(new AgendaId(rs.getInt("agenda_no"), rs.getInt("year")));
        addendum.setId(rs.getString("addendum_id"));
        setModPubDatesFromResultSet(addendum, rs);
        return addendum;
    };

    static RowMapper<AgendaVoteCommittee> agendaVoteCommRowMapper = (rs, rowNum) -> {
        AgendaVoteCommittee voteComm = new AgendaVoteCommittee();
        voteComm.setCommitteeId(
                new CommitteeId(Chamber.getValue(rs.getString("committee_chamber")), rs.getString("committee_name")));
        voteComm.setMeetingDateTime(getLocalDateTimeFromRs(rs, "meeting_date_time"));
        voteComm.setChair(rs.getString("chair"));
        return voteComm;
    };

    static MapSqlParameterSource getAgendaIdParams(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("agendaNo", agendaId.getNumber());
        params.addValue("year", agendaId.getYear());
        return params;
    }

    static MapSqlParameterSource getAgendaVoteAddendumParams(AgendaVoteAddendum addendum, LegDataFragment fragment) {
        MapSqlParameterSource params = getAgendaIdParams(addendum.getAgendaId());
        params.addValue("addendumId", addendum.getId());
        addModPubDateParams(addendum.getModifiedDateTime(), addendum.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaVoteCommParams(AgendaVoteAddendum addendum, AgendaVoteCommittee voteComm,
                                                         LegDataFragment fragment) {
        MapSqlParameterSource params = getAgendaIdParams(addendum.getAgendaId());
        params.addValue("addendumId", addendum.getId());
        params.addValue("committeeName", voteComm.getCommitteeId().getName());
        params.addValue("committeeChamber", voteComm.getCommitteeId().getChamber().asSqlEnum());
        params.addValue("chair", voteComm.getChair());
        params.addValue("meetingDateTime", toDate(voteComm.getMeetingDateTime()));
        addLastFragmentParam(fragment, params);
        return params;
    }


    /**
     * Add AgendaVoteAttendance parameters to the parameter map for the parent AgendaVoteCommittee. The insert
     * query will reference several columns that are already mapped via {@link #getAgendaVoteCommParams}.
     */
    static void addAgendaVoteAttendParams(AgendaVoteAttendance attendance, MapSqlParameterSource voteCommParams) {
        voteCommParams.addValue("sessionMemberId", attendance.getMember().getSessionMemberId());
        voteCommParams.addValue("sessionYear", attendance.getMember().getSessionYear().year());
        voteCommParams.addValue("lbdcShortName", attendance.getMember().getLbdcShortName());
        voteCommParams.addValue("rank", attendance.getRank());
        voteCommParams.addValue("party", attendance.getParty());
        voteCommParams.addValue("attendStatus", attendance.getAttendStatus());
    }

    /**
     * Add AgendaVoteBill parameters to the parameter map for the parent AgendaVoteCommittee. The insert
     * query will reference several columns mapped via {@link #getAgendaVoteCommParams} and will also
     * lookup the existing bill vote info based on a few params.
     */
    static void addAgendaBillVoteParams(AgendaVoteBill voteBill, MapSqlParameterSource voteCommParams) {
        voteCommParams.addValue("voteAction", voteBill.voteAction().getCode());
        voteCommParams.addValue("referCommitteeName",
                (voteBill.referCommittee() != null) ? voteBill.referCommittee().getName() : null);
        voteCommParams.addValue("referCommitteeChamber",
                (voteBill.referCommittee() != null) ? voteBill.referCommittee().getChamber().asSqlEnum() : null);
        voteCommParams.addValue("withAmend", voteBill.isWithAmendment());

        BillVote billVote = voteBill.billVote();
        voteCommParams.addValue("billPrintNo", billVote.getBillId().getBasePrintNo());
        voteCommParams.addValue("sessionYear", billVote.getBillId().getSession().year());
        voteCommParams.addValue("amendVersion", billVote.getBillId().getVersion().toString());
        voteCommParams.addValue("voteDate", toDate(billVote.getVoteDate()));
        voteCommParams.addValue("sequenceNo", billVote.getSequenceNo());
        voteCommParams.addValue("voteType", billVote.getVoteType().name().toLowerCase());
    }

    static class AgendaCommVoteHandler implements RowCallbackHandler {
        private static final BillVoteIdRowMapper voteIdRowMapper = new BillVoteIdRowMapper();
        private final BillVoteRowHandler billVoteHandler;

        private final Map<BillVoteId, AgendaVoteBill> agendaVoteBillMap = new TreeMap<>();

        public AgendaCommVoteHandler() {
            this.billVoteHandler = new BillVoteRowHandler();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BillVoteId billVoteId = voteIdRowMapper.mapRow(rs, 1);

            // Add the base AgendaVoteBill object if it has not already been added.
            if (!agendaVoteBillMap.containsKey(billVoteId)) {
                CommitteeId referCommitteeId = null;
                String name = rs.getString("refer_committee_name");
                if (name != null) {
                    referCommitteeId = new CommitteeId(
                            Chamber.getValue(rs.getString("refer_committee_chamber")), name);
                }
                var agendaVoteAction = AgendaVoteAction.valueOfCode(rs.getString("vote_action"));
                AgendaVoteBill agendaVote = new AgendaVoteBill(agendaVoteAction, referCommitteeId,
                        rs.getBoolean("with_amendment"), null);
                agendaVoteBillMap.put(billVoteId, agendaVote);
            }

            // Have the vote handler process this row and accumulate the vote tallies
            billVoteHandler.processRow(rs);
        }

        public Map<BillId, AgendaVoteBill> getAgendaVoteBills() {
            // Get the resulting map from the bill vote handler.
            Map<BillVoteId, BillVote> billVoteMap = billVoteHandler.getBillVoteMap();

            // Create a new map containing the AgendaVoteBills with the BillVote embedded in them,
            // and keyed by the BillId of the vote.
            Map<BillId, AgendaVoteBill> agendaVoteBillMap = new TreeMap<>();
            this.agendaVoteBillMap.forEach((voteId, agendaVoteBill) -> {
                agendaVoteBillMap.put(voteId.getBillId(), agendaVoteBill
                        .withBillVote(billVoteMap.get(voteId)));
            });
            return agendaVoteBillMap;
        }
    }

    static class AgendaVoteAttendanceRowMapper implements RowMapper<AgendaVoteAttendance> {
        // return list of AgendaVoteAttendance objs, session members only have id populated.
        @Override
        public AgendaVoteAttendance mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgendaVoteAttendance attendance = new AgendaVoteAttendance();
            SessionMember member = new SessionMember();
            member.setSessionMemberId(rs.getInt("session_member_id"));
            attendance.setMember(member);
            attendance.setParty(rs.getString("party"));
            attendance.setRank(rs.getInt("rank"));
            attendance.setAttendStatus(rs.getString("attend_status"));
            return attendance;
        }
    }

}
