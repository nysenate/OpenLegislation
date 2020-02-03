package gov.nysenate.openleg.dao.agenda.data;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.dao.common.BillVoteIdRowMapper;
import gov.nysenate.openleg.dao.common.BillVoteRowHandler;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.bill.BillVote;
import gov.nysenate.openleg.model.bill.BillVoteId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.entity.MemberNotFoundEx;
import gov.nysenate.openleg.model.entity.SessionMember;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.service.entity.member.data.MemberService;
import gov.nysenate.openleg.util.DateUtils;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.ImmutableMap.of;
import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlAgendaDao extends SqlBaseDao implements AgendaDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAgendaDao.class);

    @Autowired private MemberService memberService;
    @Autowired private SqlAgendaVoteAddendumDao voteAddendumDao;

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws DataAccessException {
        ImmutableParams agendaIdParams = ImmutableParams.from(getAgendaIdParams(agendaId));
        Agenda agenda =
            jdbcNamed.queryForObject(SqlAgendaQuery.SELECT_AGENDA_BY_ID.getSql(schema()), agendaIdParams, agendaRowMapper);
        // Set the info addenda
        agenda.setAgendaInfoAddenda(getAgendaInfoAddenda(agendaIdParams));
        // Set the vote addenda
        agenda.setAgendaVoteAddenda(voteAddendumDao.getAgendaVoteAddenda(agendaIdParams));
        return agenda;
    }

    @Override
    public Agenda getAgenda(LocalDate weekOf) throws DataAccessException {
        ImmutableParams agendaWeekOfParams = ImmutableParams.from(new MapSqlParameterSource("weekOf", toDate(weekOf)));
        Agenda agenda = jdbcNamed.queryForObject(SqlAgendaQuery.SELECT_AGENDA_BY_WEEK_OF.getSql(schema(), LimitOffset.ONE),
                                                 agendaWeekOfParams, agendaRowMapper);
        ImmutableParams agendaIdParams = ImmutableParams.from(getAgendaIdParams(agenda.getId()));
        // Set the info addenda
        agenda.setAgendaInfoAddenda(getAgendaInfoAddenda(agendaIdParams));
        // Set the vote addenda
        agenda.setAgendaVoteAddenda(voteAddendumDao.getAgendaVoteAddenda(agendaIdParams));
        return agenda;
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaId> getAgendaIds(int year, SortOrder idOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource("year", year);
        OrderBy orderBy = new OrderBy("agenda_no", idOrder);
        return jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDAS_BY_YEAR.getSql(schema(), orderBy, LimitOffset.ALL), params,
            (rs, rowNum) -> new AgendaId(rs.getInt("agenda_no"), rs.getInt("year")));
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgenda(Agenda agenda, LegDataFragment legDataFragment) throws DataAccessException {
        logger.debug("Persisting {} in database...", agenda);
        // Update the base agenda record
        MapSqlParameterSource agendaParams = getAgendaParams(agenda, legDataFragment);
        if (jdbcNamed.update(SqlAgendaQuery.UPDATE_AGENDA.getSql(schema()), agendaParams) == 0) {
            jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA.getSql(schema()), agendaParams);
        }
        // Update the info addenda
        updateAgendaInfoAddenda(agenda, legDataFragment);
        // Update the vote addenda
        voteAddendumDao.updateAgendaVoteAddenda(agenda, legDataFragment);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgenda(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agendaId, params);
        jdbcNamed.update(SqlAgendaQuery.DELETE_AGENDA.getSql(schema()), params);
    }

    /** --- Internal Methods --- */

    /**
     * Returns a map of agenda info addenda, keyed by addendum id, based on the agenda id parameters.
     */
    private Map<String, AgendaInfoAddendum> getAgendaInfoAddenda(ImmutableParams agendaParams) {
        List<AgendaInfoAddendum> infoAddenda =
            jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDA_INFO_ADDENDA.getSql(schema()), agendaParams, agendaInfoRowMapper);
        // Create a new map where the addenda are grouped by their id.
        Map<String, AgendaInfoAddendum> infoAddendaMap =
            new TreeMap<>(Maps.uniqueIndex(infoAddenda, AgendaInfoAddendum::getId));
        // Set the info committees for each addendum
        infoAddendaMap.forEach((id,addendum) -> {
            ImmutableParams agendaInfoParams = agendaParams.add(of("addendumId", id));
            addendum.setCommitteeInfoMap(getAgendaInfoCommittees(agendaInfoParams));
        });
        return infoAddendaMap;
    }

    /**
     * Returns a map of the agenda info committees with their associated items via the parameters
     * for an AgendaInfoAddendum.
     */
    private Map<CommitteeId, AgendaInfoCommittee> getAgendaInfoCommittees(ImmutableParams agendaInfoParams) {
        List<AgendaInfoCommittee> infoComms =
            jdbcNamed.query(SqlAgendaQuery.SELECT_AGENDA_INFO_COMMITTEES.getSql(schema()), agendaInfoParams, agendaInfoCommRowMapper);
        // Set the items for each info committee
        infoComms.forEach((infoComm) -> {
            CommitteeId cid = infoComm.getCommitteeId();
            ImmutableParams infoCommParams = agendaInfoParams.add(
                of("committeeName", cid.getName(), "committeeChamber", cid.getChamber().asSqlEnum()));
            infoComm.setItems(getAgendaInfoCommItems(infoCommParams));
        });
        return new TreeMap<>(Maps.uniqueIndex(infoComms, AgendaInfoCommittee::getCommitteeId));
    }

    /**
     * Returns all the committee items via the info committee parameters.
     */
    private List<AgendaInfoCommitteeItem> getAgendaInfoCommItems(ImmutableParams infoCommParams) {
        return jdbcNamed.query(
            SqlAgendaQuery.SELECT_AGENDA_INFO_COMM_ITEMS.getSql(schema()), infoCommParams, agendaInfoCommItemRowMapper);
    }

    /**
     * Delete any existing info addenda that have been modified and insert any new or modified info addenda.
     */
    private void updateAgendaInfoAddenda(Agenda agenda, LegDataFragment legDataFragment) {
        ImmutableParams agendaIdParams = ImmutableParams.from(getAgendaIdParams(agenda.getId()));
        // Compute the differences between the new and existing addenda
        Map<String, AgendaInfoAddendum> existingAddenda = getAgendaInfoAddenda(agendaIdParams);
        Map<String, AgendaInfoAddendum> currentAddenda = agenda.getAgendaInfoAddenda();
        MapDifference<String, AgendaInfoAddendum> diff = Maps.difference(existingAddenda, currentAddenda);
        // Delete any removed or modified addenda. This will cascade to all items stored within the addenda.
        Sets.union(diff.entriesOnlyOnLeft().keySet(), diff.entriesDiffering().keySet())
            .forEach(id -> {
                ImmutableParams addendumParams = agendaIdParams.add(of("addendumId", id));
                jdbcNamed.update(SqlAgendaQuery.DELETE_AGENDA_INFO_ADDENDUM.getSql(schema()), addendumParams);
            });
        // Update/insert any modified or new addenda
        Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet()).stream()
            .forEach(id -> insertAgendaInfoAddendum(currentAddenda.get(id), legDataFragment));
    }

    /**
     * Insert a record for the given AgendaInfoAddendum as well as the records for all the
     * AgendaInfoCommittees stored within this addendum.
     */
    private void insertAgendaInfoAddendum(AgendaInfoAddendum infoAddendum, LegDataFragment legDataFragment) {
        MapSqlParameterSource params = getAgendaInfoAddendumParams(infoAddendum, legDataFragment);
        jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_INFO_ADDENDUM.getSql(schema()), params);
        infoAddendum.getCommitteeInfoMap()
            .forEach((id, infoComm) -> insertAgendaInfoCommittee(infoAddendum, infoComm, legDataFragment));
     }

    /**
     * Insert a record for the given AgendaInfoCommittee as well as the records for all the
     * AgendaInfoCommitteeItems stored within this committee info object. The AgendaInfoAddendum is
     * needed since the AgendaInfoCommittee does not contain a reference to its parent.
     */
    private void insertAgendaInfoCommittee(AgendaInfoAddendum addendum, AgendaInfoCommittee infoComm, LegDataFragment fragment) {
        MapSqlParameterSource params = getAgendaInfoCommParams(addendum, infoComm, fragment);
        jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_INFO_COMMITTEE.getSql(schema()), params);
        infoComm.getItems().forEach(item -> {
            addAgendaInfoCommItemParams(item, params);
            jdbcNamed.update(SqlAgendaQuery.INSERT_AGENDA_INFO_COMM_ITEM.getSql(schema()), params);
        });
    }

    /** --- Row Mapper Instances --- */

    static RowMapper<AgendaId> agendaIdRowMapper = (rs, rowNum) ->
        new AgendaId(rs.getInt("agenda_no"), rs.getInt("year"));

    static RowMapper<Agenda> agendaRowMapper = (rs, rowNum) -> {
        Agenda agenda = new Agenda();
        agenda.setId(agendaIdRowMapper.mapRow(rs, rowNum));
        agenda.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        agenda.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        return agenda;
    };

    static RowMapper<AgendaInfoAddendum> agendaInfoRowMapper = (rs, rowNum) -> {
        AgendaInfoAddendum addendum = new AgendaInfoAddendum();
        addendum.setAgendaId(agendaIdRowMapper.mapRow(rs, rowNum));
        addendum.setId(rs.getString("addendum_id"));
        addendum.setWeekOf(getLocalDateFromRs(rs, "week_of"));
        addendum.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        addendum.setPublishedDateTime(DateUtils.getLocalDateTime(rs.getTimestamp("published_date_time")));
        return addendum;
    };

    static RowMapper<AgendaInfoCommittee> agendaInfoCommRowMapper = (rs, rowNum) -> {
        AgendaInfoCommittee infoComm = new AgendaInfoCommittee();
        infoComm.setAgendaId(
            new AgendaId(rs.getInt("agenda_no"), rs.getInt("year")));
        infoComm.setCommitteeId(
                new CommitteeId(Chamber.getValue(rs.getString("committee_chamber")), rs.getString("committee_name")));
        infoComm.setAddendum(Version.of(rs.getString("addendum_id")));
        infoComm.setChair(rs.getString("chair"));
        infoComm.setLocation(rs.getString("location"));
        infoComm.setMeetingDateTime(getLocalDateTimeFromRs(rs, "meeting_date_time"));
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
        voteComm.setMeetingDateTime(getLocalDateTimeFromRs(rs, "meeting_date_time"));
        voteComm.setChair(rs.getString("chair"));
        return voteComm;
    };

    /** --- Param Source Methods --- */

    static MapSqlParameterSource getAgendaIdParams(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agendaId, params);
        return params;
    }

    static MapSqlParameterSource getAgendaParams(Agenda agenda, LegDataFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agenda.getId(), params);
        addModPubDateParams(agenda.getModifiedDateTime(), agenda.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaInfoAddendumParams(AgendaInfoAddendum addendum, LegDataFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        params.addValue("weekOf", toDate(addendum.getWeekOf()));
        addModPubDateParams(addendum.getModifiedDateTime(), addendum.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    static MapSqlParameterSource getAgendaInfoCommParams(AgendaInfoAddendum addendum, AgendaInfoCommittee infoComm,
                                                         LegDataFragment fragment) {
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

    /**
     * Add AgendaInfoCommitteeItem parameters to the parameter map for the parent AgendaInfoCommittee. The insert
     * query will reference several columns that are already mapped via {@link #getAgendaInfoCommParams}.
     */
    static void addAgendaInfoCommItemParams(AgendaInfoCommitteeItem item, MapSqlParameterSource infoCommParams) {
        BillId billId = item.getBillId();
        infoCommParams.addValue("printNo", billId.getBasePrintNo());
        infoCommParams.addValue("session", billId.getSession().getYear());
        infoCommParams.addValue("amendVersion", billId.getVersion().toString());
        infoCommParams.addValue("message", item.getMessage());
    }

    /**
     * Adds columns that identify an agenda id.
     */
    static void addAgendaIdParams(AgendaId agendaId, MapSqlParameterSource params) {
        params.addValue("agendaNo", agendaId.getNumber());
        params.addValue("year", agendaId.getYear());
    }
}
