package gov.nysenate.openleg.dao.agenda;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.agenda.*;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static gov.nysenate.openleg.dao.agenda.SqlAgendaQuery.*;
import static java.util.stream.Collectors.toMap;

@Repository
public class SqlAgendaDao extends SqlBaseDao implements AgendaDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlAgendaDao.class);

    /** {@inheritDoc} */
    @Override
    public Agenda getAgenda(AgendaId agendaId) throws DataAccessException {
        MapSqlParameterSource agendaIdParams = getAgendaIdParams(agendaId);
        Agenda agenda =
            jdbcNamed.queryForObject(SELECT_AGENDA_BY_ID.getSql(schema()), agendaIdParams, new AgendaRowMapper());
        // Set the info addenda
        agenda.setAgendaInfoAddenda(getAgendaInfoAddenda(agendaIdParams));
        // Set the vote addenda
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
            jdbcNamed.query(SELECT_AGENDA_INFO_ADDENDA.getSql(schema()), agendaParams, new AgendaInfoAddendumRowMapper());
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
            jdbcNamed.query(SELECT_AGENDA_INFO_COMMITTEES.getSql(schema()), agendaInfoParams,
                    new AgendaInfoCommitteeRowMapper());
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
            SELECT_AGENDA_INFO_COMM_ITEMS.getSql(schema()), infoCommParams, new AgendaInfoCommitteeItemRowMapper());
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

    /** --- Helper Classes --- */

    private static class AgendaIdRowMapper implements RowMapper<AgendaId>
    {
        @Override
        public AgendaId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new AgendaId(rs.getInt("agenda_no"), rs.getInt("year"));
        }
    }

    private static class AgendaRowMapper implements RowMapper<Agenda>
    {
        @Override
        public Agenda mapRow(ResultSet rs, int rowNum) throws SQLException {
            Agenda agenda = new Agenda();
            agenda.setId(new AgendaIdRowMapper().mapRow(rs, rowNum));
            agenda.setPublishedDateTime(getLocalDateTime(rs, "published_date_time"));
            agenda.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
            return agenda;
        }
    }

    private static class AgendaInfoAddendumRowMapper implements RowMapper<AgendaInfoAddendum>
    {
        @Override
        public AgendaInfoAddendum mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgendaInfoAddendum addendum = new AgendaInfoAddendum();
            addendum.setAgendaId(new AgendaIdRowMapper().mapRow(rs, rowNum));
            addendum.setId(rs.getString("addendum_id"));
            addendum.setWeekOf(getLocalDate(rs, "week_of"));
            addendum.setModifiedDateTime(getLocalDateTime(rs, "modified_date_time"));
            addendum.setPublishedDateTime(getLocalDateTime(rs.getTimestamp("published_date_time")));
            return addendum;
        }
    }

    private static class AgendaInfoCommitteeRowMapper implements RowMapper<AgendaInfoCommittee>
    {
        @Override
        public AgendaInfoCommittee mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgendaInfoCommittee infoComm = new AgendaInfoCommittee();
            infoComm.setCommitteeId(
                new CommitteeId(Chamber.getValue(rs.getString("committee_chamber")), rs.getString("committee_name")));
            infoComm.setChair(rs.getString("chair"));
            infoComm.setLocation(rs.getString("location"));
            infoComm.setMeetingDateTime(rs.getTimestamp("meeting_date_time").toLocalDateTime());
            infoComm.setNotes(rs.getString("notes"));
            return infoComm;
        }
    }

    private static class AgendaInfoCommitteeItemRowMapper implements RowMapper<AgendaInfoCommitteeItem>
    {
        @Override
        public AgendaInfoCommitteeItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem();
            item.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                      rs.getString("bill_amend_version")));
            item.setMessage(rs.getString("message"));
            return item;
        }
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getAgendaIdParams(AgendaId agendaId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agendaId, params);
        return params;
    }

    private static MapSqlParameterSource getAgendaParams(Agenda agenda, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(agenda.getId(), params);
        addModPubDateParams(agenda.getModifiedDateTime(), agenda.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getAgendaInfoAddendumParams(AgendaInfoAddendum addendum, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addAgendaIdParams(addendum.getAgendaId(), params);
        params.addValue("addendumId", addendum.getId());
        params.addValue("weekOf", toDate(addendum.getWeekOf()));
        addModPubDateParams(addendum.getModifiedDateTime(), addendum.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    private static MapSqlParameterSource getAgendaInfoCommParams(AgendaInfoAddendum addendum, AgendaInfoCommittee infoComm,
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

    /**
     * Add AgendaInfoCommitteeItem parameters to the parameter map for the parent AgendaInfoCommittee. This
     * is easier than creating a new parameter map because the insert query will reference several columns that
     * are already mapped via {@link #getAgendaInfoCommParams}.
     */
    private static void addAgendaInfoCommItemParams(AgendaInfoCommitteeItem item, MapSqlParameterSource infoCommParams) {
        BillId billId = item.getBillId();
        infoCommParams.addValue("printNo", billId.getBasePrintNo());
        infoCommParams.addValue("session", billId.getSession());
        infoCommParams.addValue("amendVersion", billId.getVersion());
        infoCommParams.addValue("message", item.getMessage());
    }

    /**
     * Adds columns that identify an agenda id.
     */
    private static void addAgendaIdParams(AgendaId agendaId, MapSqlParameterSource params) {
        params.addValue("agendaNo", agendaId.getNumber());
        params.addValue("year", agendaId.getYear());
    }
}
