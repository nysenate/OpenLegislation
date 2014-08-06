package gov.nysenate.openleg.dao.agenda;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.common.BillVoteIdRowMapper;
import gov.nysenate.openleg.dao.common.BillVoteRowHandler;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.MemberId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.service.entity.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gov.nysenate.openleg.dao.agenda.SqlAgendaQuery.*;
import static java.util.stream.Collectors.toMap;

@Repository
public class SqlAgendaDao extends SqlBaseDao implements AgendaDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAgendaDao.class);

    @Autowired
    MemberService memberService;

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws DataAccessException {
        MapSqlParameterSource agendaIdParams = getAgendaIdParams(agendaId);
        Agenda agenda =
            jdbcNamed.queryForObject(SELECT_AGENDA_BY_ID.getSql(schema()), agendaIdParams, agendaRowMapper);
        // Set the info addenda
        agenda.setAgendaInfoAddenda(getAgendaInfoAddenda(agendaIdParams));
        // Set the vote addenda
        agenda.setAgendaVoteAddenda(getAgendaVoteAddenda(agendaIdParams));
        return agenda;
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaId> getAgendaIds(int year, SortOrder idOrder) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgenda(Agenda agenda, SobiFragment sobiFragment) throws DataAccessException {
        logger.debug("Persisting {} in database...", agenda);
        // Update the base agenda record
        MapSqlParameterSource agendaParams = getAgendaParams(agenda, sobiFragment);
        if (jdbcNamed.update(UPDATE_AGENDA.getSql(schema()), agendaParams) == 0) {
            jdbcNamed.update(INSERT_AGENDA.getSql(schema()), agendaParams);
        }
        // Update the info addenda
        updateAgendaInfoAddenda(agenda, sobiFragment);
        // Update the vote addenda
        updateAgendaVoteAddenda(agenda, sobiFragment);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agendaId, params);
        jdbcNamed.update(DELETE_AGENDA.getSql(schema()), params);
    }

    /** --- Internal Methods --- */

    /**
     * Returns a map of agenda info addenda, keyed by addendum id, based on the agenda id parameters.
     */
    private Map<String, AgendaInfoAddendum> getAgendaInfoAddenda(MapSqlParameterSource agendaParams) {
        List<AgendaInfoAddendum> infoAddenda =
            jdbcNamed.query(SELECT_AGENDA_INFO_ADDENDA.getSql(schema()), agendaParams, agendaInfoRowMapper);
        // Create a new map where the addenda are grouped by their id.
        Map<String, AgendaInfoAddendum> infoAddendaMap =
            new TreeMap<>(Maps.uniqueIndex(infoAddenda, AgendaInfoAddendum::getId));
        // Set the info committees for each addendum
        infoAddendaMap.forEach((id,addendum) -> {
            agendaParams.addValue("addendumId", id);
            addendum.setCommitteeInfoMap(getAgendaInfoCommittees(agendaParams));
        });
        return infoAddendaMap;
    }

    /**
     * Returns a map of the agenda info committees with their associated items via the parameters
     * for an AgendaInfoAddendum.
     */
    private Map<CommitteeId, AgendaInfoCommittee> getAgendaInfoCommittees(MapSqlParameterSource agendaInfoParams) {
        List<AgendaInfoCommittee> infoComms =
            jdbcNamed.query(SELECT_AGENDA_INFO_COMMITTEES.getSql(schema()), agendaInfoParams, agendaInfoCommRowMapper);
        // Set the items for each info committee
        infoComms.forEach((infoComm) -> {
            agendaInfoParams.addValue("committeeName", infoComm.getCommitteeId().getName());
            agendaInfoParams.addValue("committeeChamber", infoComm.getCommitteeId().getChamber().asSqlEnum());
            infoComm.setItems(getAgendaInfoCommItems(agendaInfoParams));
        });
        return new TreeMap<>(Maps.uniqueIndex(infoComms, AgendaInfoCommittee::getCommitteeId));
    }

    /**
     * Returns all the committee items via the info committee parameters.
     */
    private List<AgendaInfoCommitteeItem> getAgendaInfoCommItems(MapSqlParameterSource infoCommParams) {
        return jdbcNamed.query(
            SELECT_AGENDA_INFO_COMM_ITEMS.getSql(schema()), infoCommParams, agendaInfoCommItemRowMapper);
    }

    /**
     * Returns a map of agenda vote addenda, keyed by addendum id, based on the agenda id parameters.
     */
    private Map<String, AgendaVoteAddendum> getAgendaVoteAddenda(MapSqlParameterSource agendaParams) {
        List<AgendaVoteAddendum> voteAddenda =
                jdbcNamed.query(SELECT_AGENDA_VOTE_ADDENDA.getSql(schema()), agendaParams, agendaVoteRowMapper);
        // Create a new map where the addenda are grouped by their id.
        Map<String, AgendaVoteAddendum> voteAddendaMap =
            new TreeMap<>(Maps.uniqueIndex(voteAddenda, AgendaVoteAddendum::getId));
        // Set the info committees for each addendum
        voteAddendaMap.forEach((id,addendum) -> {
            agendaParams.addValue("addendumId", id);
            addendum.setCommitteeVoteMap(getAgendaVoteCommitteeMap(agendaParams));
        });
        return voteAddendaMap;
    }

    /**
     * Returns a map of the agenda vote committees with their associated items via the parameters
     * for an AgendaVoteAddendum.
     */
    private Map<CommitteeId, AgendaVoteCommittee> getAgendaVoteCommitteeMap(MapSqlParameterSource agendaVoteParams) {
        List<AgendaVoteCommittee> voteComms =
            jdbcNamed.query(SELECT_AGENDA_VOTE_COMMITTEES.getSql(schema()), agendaVoteParams, agendaVoteCommRowMapper);

        voteComms.forEach((voteComm) -> {
            agendaVoteParams.addValue("committeeName", voteComm.getCommitteeId().getName());
            agendaVoteParams.addValue("committeeChamber", voteComm.getCommitteeId().getChamber().asSqlEnum());
            // Set the attendance list for each vote committee
            voteComm.setAttendance(
                jdbcNamed.query(SELECT_AGENDA_VOTE_ATTENDANCE.getSql(schema()), agendaVoteParams, agendaVoteAttendRowMapper));
            // Set the bills that were voted on
            AgendaCommVoteHandler agendaVoteHandler = new AgendaCommVoteHandler(memberService);
            jdbcNamed.query(SELECT_AGENDA_COMM_VOTES.getSql(schema()), agendaVoteParams, agendaVoteHandler);
            voteComm.setVotedBills(agendaVoteHandler.getAgendaVoteBills());
        });
        return new TreeMap<>(Maps.uniqueIndex(voteComms, AgendaVoteCommittee::getCommitteeId));
    }

    /**
     * Delete any existing info addenda that have been modified and insert any new or modified info addenda.
     */
    private void updateAgendaInfoAddenda(Agenda agenda, SobiFragment sobiFragment) {
        MapSqlParameterSource params = getAgendaIdParams(agenda.getId());
        // Compute the differences between the new and existing addenda
        Map<String, AgendaInfoAddendum> existingAddenda = getAgendaInfoAddenda(params);
        Map<String, AgendaInfoAddendum> currentAddenda = agenda.getAgendaInfoAddenda();
        MapDifference<String, AgendaInfoAddendum> diff = Maps.difference(existingAddenda, currentAddenda);
        // Delete any removed or modified addenda. This will cascade to all items stored within the addenda.
        Sets.union(diff.entriesOnlyOnLeft().keySet(), diff.entriesDiffering().keySet())
            .forEach(id -> {
                params.addValue("addendumId", id);
                jdbcNamed.update(DELETE_AGENDA_INFO_ADDENDUM.getSql(schema()), params);
            });
        // Update/insert any modified or new addenda
        Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet()).stream()
            .forEach(id -> insertAgendaInfoAddendum(currentAddenda.get(id), sobiFragment));
    }

    /**
     * Insert a record for the given AgendaInfoAddendum as well as the records for all the
     * AgendaInfoCommittees stored within this addendum.
     */
    private void insertAgendaInfoAddendum(AgendaInfoAddendum infoAddendum, SobiFragment sobiFragment) {
        MapSqlParameterSource params = getAgendaInfoAddendumParams(infoAddendum, sobiFragment);
        jdbcNamed.update(INSERT_AGENDA_INFO_ADDENDUM.getSql(schema()), params);
        infoAddendum.getCommitteeInfoMap()
            .forEach((id, infoComm) -> insertAgendaInfoCommittee(infoAddendum, infoComm, sobiFragment));
     }

    /**
     * Insert a record for the given AgendaInfoCommittee as well as the records for all the
     * AgendaInfoCommitteeItems stored within this committee info object. The AgendaInfoAddendum is
     * needed since the AgendaInfoCommittee does not contain a reference to its parent.
     */
    private void insertAgendaInfoCommittee(AgendaInfoAddendum addendum, AgendaInfoCommittee infoComm, SobiFragment fragment) {
        MapSqlParameterSource params = getAgendaInfoCommParams(addendum, infoComm, fragment);
        jdbcNamed.update(INSERT_AGENDA_INFO_COMMITTEE.getSql(schema()), params);
        infoComm.getItems().forEach(item -> {
            addAgendaInfoCommItemParams(item, params);
            jdbcNamed.update(INSERT_AGENDA_INFO_COMM_ITEM.getSql(schema()), params);
        });
    }

    /**
     * Delete any existing vote addenda that have been modified and insert any new or modified vote addenda.
     */
    private void updateAgendaVoteAddenda(Agenda agenda, SobiFragment sobiFragment) {
        MapSqlParameterSource params = getAgendaIdParams(agenda.getId());
        // Compute the differences between the new and existing addenda
        Map<String, AgendaVoteAddendum> existingAddenda = getAgendaVoteAddenda(params);
        Map<String, AgendaVoteAddendum> currentAddenda = agenda.getAgendaVoteAddenda();
        MapDifference<String, AgendaVoteAddendum> diff = Maps.difference(existingAddenda, currentAddenda);
        // Delete any removed or modified addenda. This will cascade to all items stored within the addenda.
        Sets.union(diff.entriesOnlyOnLeft().keySet(), diff.entriesDiffering().keySet())
            .forEach(id -> {
                params.addValue("addendumId", id);
                jdbcNamed.update(DELETE_AGENDA_VOTE_ADDENDUM.getSql(schema()), params);
            });
        // Update/insert any modified or new addenda
        Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet()).stream()
            .forEach(id -> insertAgendaVoteAddendum(currentAddenda.get(id), sobiFragment));
    }

    /**
     * Insert a record for the given AgendaVoteAddendum as well as the records for all the
     * AgendaVoteCommittees stored within this addendum.
     */
    private void insertAgendaVoteAddendum(AgendaVoteAddendum voteAddendum, SobiFragment sobiFragment) {
        MapSqlParameterSource params = getAgendaVoteAddendumParams(voteAddendum, sobiFragment);
        jdbcNamed.update(INSERT_AGENDA_VOTE_ADDENDUM.getSql(schema()), params);
        voteAddendum.getCommitteeVoteMap()
            .forEach((id, voteComm) -> insertAgendaVoteCommittee(voteAddendum, voteComm, sobiFragment));
    }

    /**
     * Insert a record for the given AgendaVoteAddendum as well as the records for all the
     * AgendaVoteAttendance and AgendaVoteBill items stored within this committee vote object.
     * The AgendaVoteAddendum is needed since the AgendaVoteCommittee does not contain a reference
     * to its parent.
     */
    private void insertAgendaVoteCommittee(AgendaVoteAddendum addendum, AgendaVoteCommittee voteComm,
                                           SobiFragment fragment) {
        MapSqlParameterSource params = getAgendaVoteCommParams(addendum, voteComm, fragment);
        jdbcNamed.update(INSERT_AGENDA_VOTE_COMMITTEE.getSql(schema()), params);
        // Insert the attendance list
        voteComm.getAttendance().forEach(attend -> {
            addAgendaVoteAttendParams(attend, params);
            jdbcNamed.update(INSERT_AGENDA_VOTE_ATTENDANCE.getSql(schema()), params);
        });
        // Insert the committee bill vote info.
        // NOTE: The actual bill votes are assumed to have already been persisted via the bill data layer.
        // This insert will reference the existing vote but will not insert it if it doesn't exist.
        voteComm.getVotedBills().forEach((billId, voteBill) -> {
            addAgendaBillVoteParams(voteBill, params);
            jdbcNamed.update(INSERT_AGENDA_COMM_BILL_VOTES.getSql(schema()), params);
        });
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<AgendaId> agendaIdRowMapper = (rs, rowNum) ->
        new AgendaId(rs.getInt("agenda_no"), rs.getInt("year"));

    static RowMapper<Agenda> agendaRowMapper = (rs, rowNum) -> {
        Agenda agenda = new Agenda();
        agenda.setId(agendaIdRowMapper.mapRow(rs, rowNum));
        agenda.setPublishedDateTime(getLocalDateTime(rs, "published_date_time"));
        agenda.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
        return agenda;
    };

    static RowMapper<AgendaInfoAddendum> agendaInfoRowMapper = (rs, rowNum) -> {
        AgendaInfoAddendum addendum = new AgendaInfoAddendum();
        addendum.setAgendaId(agendaIdRowMapper.mapRow(rs, rowNum));
        addendum.setId(rs.getString("addendum_id"));
        addendum.setWeekOf(getLocalDate(rs, "week_of"));
        addendum.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
        addendum.setPublishedDateTime(getLocalDateTime(rs.getTimestamp("published_date_time")));
        return addendum;
    };

    static RowMapper<AgendaInfoCommittee> agendaInfoCommRowMapper = (rs, rowNum) -> {
        AgendaInfoCommittee infoComm = new AgendaInfoCommittee();
        infoComm.setCommitteeId(
            new CommitteeId(Chamber.getValue(rs.getString("committee_chamber")), rs.getString("committee_name")));
        infoComm.setChair(rs.getString("chair"));
        infoComm.setLocation(rs.getString("location"));
        infoComm.setMeetingDateTime(getLocalDateTime(rs, "meeting_date_time"));
        infoComm.setNotes(rs.getString("notes"));
        return infoComm;
    };

    static RowMapper<AgendaInfoCommitteeItem> agendaInfoCommItemRowMapper = (rs, rowNum) -> {
        AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem();
        item.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                  rs.getString("bill_amend_version")));
        item.setMessage(rs.getString("message"));
        return item;
    };

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
        voteComm.setMeetingDateTime(getLocalDateTime(rs, "meeting_date_time"));
        voteComm.setChair(rs.getString("chair"));
        return voteComm;
    };

    static RowMapper<AgendaVoteAttendance> agendaVoteAttendRowMapper = (rs, rowNum) -> {
        AgendaVoteAttendance attendance = new AgendaVoteAttendance();
        attendance.setMemberId(
            new MemberId(rs.getInt("member_id"), rs.getInt("session_year"), rs.getString("lbdc_short_name"))
        );
        attendance.setParty(rs.getString("party"));
        attendance.setRank(rs.getInt("rank"));
        attendance.setAttendStatus(rs.getString("attend_status"));
        return attendance;
    };

    static class AgendaCommVoteHandler implements RowCallbackHandler
    {
        private static final BillVoteIdRowMapper voteIdRowMapper = new BillVoteIdRowMapper();
        private final BillVoteRowHandler billVoteHandler;

        private Map<BillVoteId, AgendaVoteBill> agendaVoteBillMap = new TreeMap<>();

        public AgendaCommVoteHandler(MemberService memberService) {
            this.billVoteHandler = new BillVoteRowHandler(memberService);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            BillVoteId billVoteId = voteIdRowMapper.mapRow(rs, 1);

            // Add the base AgendaVoteBill object if it has not already been added.
            if (!agendaVoteBillMap.containsKey(billVoteId)) {
                CommitteeId referCommitteeId = null;
                if (rs.getString("refer_committee_name") != null) {
                    referCommitteeId = new CommitteeId(
                        Chamber.getValue(rs.getString("refer_committee_chamber")), rs.getString("refer_committee_name"));
                }
                AgendaVoteBill agendaVote = new AgendaVoteBill(AgendaVoteAction.valueOfCode(rs.getString("vote_action")),
                                                               referCommitteeId, rs.getBoolean("with_amendment"));
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
                agendaVoteBill.setBillVote(billVoteMap.get(voteId));
                agendaVoteBillMap.put(voteId.getBillId(), agendaVoteBill);
            });
            return agendaVoteBillMap;
        }
    }

    /** --- Param Source Methods --- */

    static MapSqlParameterSource getAgendaIdParams(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agendaId, params);
        return params;
    }

    static MapSqlParameterSource getAgendaParams(Agenda agenda, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agenda.getId(), params);
        addModPubDateParams(agenda.getModifiedDateTime(), agenda.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaInfoAddendumParams(AgendaInfoAddendum addendum, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        params.addValue("weekOf", toDate(addendum.getWeekOf()));
        addModPubDateParams(addendum.getModifiedDateTime(), addendum.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaInfoCommParams(AgendaInfoAddendum addendum, AgendaInfoCommittee infoComm,
                                                         SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        params.addValue("committeeName", infoComm.getCommitteeId().getName());
        params.addValue("committeeChamber", infoComm.getCommitteeId().getChamber().asSqlEnum());
        params.addValue("chair", infoComm.getChair());
        params.addValue("location", infoComm.getLocation());
        params.addValue("meetingDateTime", toDate(infoComm.getMeetingDateTime()));
        params.addValue("notes", infoComm.getNotes());
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaVoteAddendumParams(AgendaVoteAddendum addendum, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        addModPubDateParams(addendum.getModifiedDateTime(), addendum.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaVoteCommParams(AgendaVoteAddendum addendum, AgendaVoteCommittee voteComm,
                                                         SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        params.addValue("committeeName", voteComm.getCommitteeId().getName());
        params.addValue("committeeChamber", voteComm.getCommitteeId().getChamber().asSqlEnum());
        params.addValue("chair", voteComm.getChair());
        params.addValue("meetingDateTime", toDate(voteComm.getMeetingDateTime()));
        addLastFragmentParam(fragment, params);
        return params;
    }

    /**
     * Add AgendaInfoCommitteeItem parameters to the parameter map for the parent AgendaInfoCommittee. The insert
     * query will reference several columns that are already mapped via {@link #getAgendaInfoCommParams}.
     */
    static void addAgendaInfoCommItemParams(AgendaInfoCommitteeItem item, MapSqlParameterSource infoCommParams) {
        BillId billId = item.getBillId();
        infoCommParams.addValue("printNo", billId.getBasePrintNo());
        infoCommParams.addValue("session", billId.getSession());
        infoCommParams.addValue("amendVersion", billId.getVersion());
        infoCommParams.addValue("message", item.getMessage());
    }

    /**
     * Add AgendaVoteAttendance parameters to the parameter map for the parent AgendaVoteCommittee. The insert
     * query will reference several columns that are already mapped via {@link #getAgendaVoteCommParams}.
     */
    static void addAgendaVoteAttendParams(AgendaVoteAttendance attendance, MapSqlParameterSource voteCommParams) {
        voteCommParams.addValue("memberId", attendance.getMemberId().getId());
        voteCommParams.addValue("sessionYear", attendance.getMemberId().getSessionYear());
        voteCommParams.addValue("lbdcShortName", attendance.getMemberId().getLbdcShortName());
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
        voteCommParams.addValue("voteAction", voteBill.getVoteAction().getCode());
        voteCommParams.addValue("referCommitteeName",
            (voteBill.getReferCommittee() != null) ? voteBill.getReferCommittee().getName() : null);
        voteCommParams.addValue("referCommitteeChamber",
            (voteBill.getReferCommittee() != null) ? voteBill.getReferCommittee().getChamber().asSqlEnum() : null);
        voteCommParams.addValue("withAmend", voteBill.isWithAmendment());

        BillVote billVote = voteBill.getBillVote();
        voteCommParams.addValue("billPrintNo", billVote.getBillId().getBasePrintNo());
        voteCommParams.addValue("sessionYear", billVote.getBillId().getSession());
        voteCommParams.addValue("amendVersion", billVote.getBillId().getVersion());
        voteCommParams.addValue("voteDate", toDate(billVote.getVoteDate()));
        voteCommParams.addValue("sequenceNo", billVote.getSequenceNo());
        voteCommParams.addValue("voteType", billVote.getVoteType().name().toLowerCase());
    }

    /**
     * Adds columns that identify an agenda id.
     */
    static void addAgendaIdParams(AgendaId agendaId, MapSqlParameterSource params) {
        params.addValue("agendaNo", agendaId.getNumber());
        params.addValue("year", agendaId.getYear());
    }
}
