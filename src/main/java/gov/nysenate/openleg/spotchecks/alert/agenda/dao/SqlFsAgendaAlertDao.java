package gov.nysenate.openleg.spotchecks.alert.agenda.dao;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.spotchecks.alert.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.spotchecks.alert.agenda.AgendaMeetingWeekId;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReferenceId;
import org.apache.commons.io.FileUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.spotchecks.alert.agenda.dao.SqlAgendaAlertQuery.*;

@Repository
public class SqlFsAgendaAlertDao extends SqlBaseDao implements AgendaAlertDao {
    private static final Pattern agendaFilePattern =
            Pattern.compile("^agenda_alert-\\d{8}-[A-z._]+-[A-z]+-?\\d{8}T\\d{6}.html$");

    // This pattern parses both full and individual agenda alert filenames, but currently we can't reliably process full alerts
    private static final Pattern agendaFullFilePattern =
            Pattern.compile("^agenda_alert-\\d{8}-[A-z._-]*\\d{8}T\\d{6}.html$");

    private File incomingAgendaAlertDir;
    private File archiveAgendaAlertDir;

    @PostConstruct
    public void init() {
        this.incomingAgendaAlertDir = new File(environment.getStagingDir(), "alerts");
        this.archiveAgendaAlertDir = new File(environment.getArchiveDir(), "alerts");
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getIncomingAgendaAlerts() throws IOException {
        List<File> agendaAlerts = FileIOUtils.safeListFiles(incomingAgendaAlertDir, null, false).stream()
                .filter(file -> agendaFullFilePattern.matcher(file.getName()).matches())
                .toList();
        List<File> singleAgendaAlerts = new ArrayList<>();
        for (File alert : agendaAlerts) {
            if (agendaFilePattern.matcher(alert.getName()).matches()) {
                singleAgendaAlerts.add(alert);
            } else {
                // Archive the full agenda alerts
                archiveAgendaAlert(alert);
            }
        }
        return singleAgendaAlerts;
    }

    /** {@inheritDoc} */
    @Override
    public void archiveAgendaAlert(File agendaAlert) throws IOException {
        FileUtils.moveToDirectory(agendaAlert, archiveAgendaAlertDir, true);
    }

    /** {@inheritDoc} */
    @Override
    public AgendaAlertInfoCommittee getAgendaAlertInfoCommittee(AgendaMeetingWeekId meetingWeekId) {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        String sql = SELECT_INFO_COMMITTEE_BY_ID.getSql(schema(), LimitOffset.ONE);
        jdbcNamed.query(sql, getAgendaMeetingWeekIdParams(meetingWeekId), rowHandler);
        List<AgendaAlertInfoCommittee> result = rowHandler.getAlertInfoCommittees();
        if (result.size() == 1) {
            return result.get(0);
        }
        if (result.isEmpty()) {
            throw new EmptyResultDataAccessException(1);
        }
        throw new IncorrectResultSizeDataAccessException(1, result.size());
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaAlertInfoCommittee> getAgendaAlertReferences(Range<LocalDateTime> dateTimeRange) {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        jdbcNamed.query(SELECT_IN_RANGE.getSql(schema()), getDateTimeRangeParams(dateTimeRange), rowHandler);
        return rowHandler.getAlertInfoCommittees();
    }

    /** {@inheritDoc} */
    @Override
    public List<AgendaAlertInfoCommittee> getUncheckedAgendaAlertReferences() {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        jdbcNamed.query(SELECT_UNCHECKED.getSql(schema()), rowHandler);
        return rowHandler.getAlertInfoCommittees();
    }

    @Override
    public List<AgendaAlertInfoCommittee> getProdUncheckedAgendaAlertReferences() {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        jdbcNamed.query(SELECT_PROD_UNCHECKED.getSql(schema()), rowHandler);
        return groupAlertInfoCommittees(rowHandler.getAlertInfoCommittees());
    }

    @Override
    public List<AgendaAlertInfoCommittee> getProdAgendaAlertReferences(Range<LocalDateTime> dateTimeRange) {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        jdbcNamed.query(SELECT_IN_RANGE.getSql(schema()), getDateTimeRangeParams(dateTimeRange), rowHandler);
        return groupAlertInfoCommittees(rowHandler.getAlertInfoCommittees());
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaAlertInfoCommittee(AgendaAlertInfoCommittee aaic) {
        deleteAAIC(aaic.getAgendaMeetingWeekId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_INFO_COMMITTEE.getSql(schema()), getAgendaAlertInfoCommParams(aaic),
                keyHolder, new String[]{"id"});

        aaic.getItems().forEach(item -> insertAAICItem(item, keyHolder.getKey().intValue()));
    }

    /** {@inheritDoc} */
    @Override
    public void setAgendaAlertChecked(AgendaMeetingWeekId meetingWeekId, boolean checked) {
        MapSqlParameterSource params = getAgendaMeetingWeekIdParams(meetingWeekId);
        params.addValue("checked", checked);
        jdbcNamed.update(SET_INFO_COMMITTEE_CHECKED.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void setAgendaAlertProdChecked(AgendaAlertInfoCommittee alertInfoCommittee, boolean checked) {
        MapSqlParameterSource params = getAgendaAlertInfoCommParams(alertInfoCommittee)
                .addValue("checked", checked);
        jdbcNamed.update(SET_MEETING_PROD_CHECKED.getSql(schema()), params);
    }

    /** --- Internal Methods --- */

    /**
     * Deletes an AgendaAlertInfoCommittee
     * @param meetingWeekId {@link AgendaMeetingWeekId}
     */
    private void deleteAAIC(AgendaMeetingWeekId meetingWeekId) {
        jdbcNamed.update(DELETE_INFO_COMMITTEE.getSql(schema()), getAgendaMeetingWeekIdParams(meetingWeekId));
    }

    /**
     * Inserts an AgendaInfoCommitteeItem under the given AgendaAlertInfoCommittee row id
     * @param aici AgendaInfoCommitteeItem
     * @param aaicId int - row id for an AgendaAlertInfoCommittee
     */
    private void insertAAICItem(AgendaInfoCommitteeItem aici, int aaicId) {
        jdbcNamed.update(INSERT_INFO_COMMITTEE_ITEM.getSql(schema()), getAgendaInfoCommItemParams(aici, aaicId));
    }

    private List<AgendaAlertInfoCommittee> groupAlertInfoCommittees(List<AgendaAlertInfoCommittee> alertInfoCommittees) {
        Map<CommitteeId, ArrayListMultimap<LocalDate, AgendaAlertInfoCommittee>> aaicMap = new HashMap<>();
        alertInfoCommittees.forEach(aaic -> {
            if (!aaicMap.containsKey(aaic.getCommitteeId())) {
                aaicMap.put(aaic.getCommitteeId(), ArrayListMultimap.create());
            }
            aaicMap.get(aaic.getCommitteeId()).put(aaic.getMeetingDateTime().toLocalDate(), aaic);
        });
        return aaicMap.values().stream()
                .flatMap(multiMap -> multiMap.keySet().stream().map(multiMap::get))
                .filter(aaicList -> !aaicList.isEmpty())
                .map(aaicList -> aaicList.stream().reduce(null, AgendaAlertInfoCommittee::merge))
                .toList();
    }

    /** --- Row Mappers --- */

    private static final RowMapper<AgendaAlertInfoCommittee> agendaAlertInfoCommRowMapper = (rs, rowNum) -> {
        AgendaAlertInfoCommittee aaic = new AgendaAlertInfoCommittee();
        aaic.setReferenceId(new SpotCheckReferenceId(
                SpotCheckRefType.LBDC_AGENDA_ALERT,
                getLocalDateTimeFromRs(rs, "reference_date_time")
        ));
        aaic.setWeekOf(getLocalDateFromRs(rs, "week_of"));
        aaic.setAddendum(Version.of(rs.getString("addendum_id")));
        aaic.setCommitteeId(new CommitteeId(
                Chamber.getValue(rs.getString("chamber")),
                rs.getString("committee_name")
        ));
        aaic.setChair(rs.getString("chair"));
        aaic.setLocation(rs.getString("location"));
        aaic.setMeetingDateTime(getLocalDateTimeFromRs(rs, "meeting_date_time"));
        aaic.setNotes(rs.getString("notes"));
        return aaic;
    };

    private static final RowMapper<AgendaInfoCommitteeItem> agendaInfoCommItemRowMapper = (rs, rowNum) -> {
        AgendaInfoCommitteeItem item = new AgendaInfoCommitteeItem();
        item.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                rs.getString("bill_amend_version")));
        item.setMessage(rs.getString("message"));
        return item;
    };

    private static class AgendaAlertInfoCommRowHandler implements RowCallbackHandler {
        private final Map<Integer, AgendaAlertInfoCommittee> committeeMeetingRefMap = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            int aaicId = rs.getInt("id");
            int itemId = rs.getInt("alert_info_committee_id");
            if (!committeeMeetingRefMap.containsKey(aaicId)) {
                committeeMeetingRefMap.put(aaicId, agendaAlertInfoCommRowMapper.mapRow(rs, rs.getRow()));
            }
            if (itemId != 0) {
                committeeMeetingRefMap.get(aaicId).addInfoCommitteeItem(
                        agendaInfoCommItemRowMapper.mapRow(rs, rs.getRow()));
            }
        }

        public List<AgendaAlertInfoCommittee> getAlertInfoCommittees() {
            return new ArrayList<>(committeeMeetingRefMap.values());
        }
    }

    /** --- Parameter Mappers --- */

    private static MapSqlParameterSource getAgendaMeetingWeekIdParams(AgendaMeetingWeekId meetingWeekId) {
        return new MapSqlParameterSource()
                .addValue("year", meetingWeekId.year())
                .addValue("weekOf", DateUtils.toDate(meetingWeekId.weekOf()))
                .addValue("addendumId", meetingWeekId.addendum().toString())
                .addValue("chamber", meetingWeekId.committeeId().getChamber().asSqlEnum())
                .addValue("committeeName", meetingWeekId.committeeId().getName());
    }

    private static MapSqlParameterSource getAgendaAlertInfoCommParams(AgendaAlertInfoCommittee aaic) {
        return getAgendaMeetingWeekIdParams(aaic.getAgendaMeetingWeekId())
                .addValue("referenceDateTime", DateUtils.toDate(aaic.getReferenceId().getRefActiveDateTime()))
                .addValue("chair", aaic.getChair())
                .addValue("location", aaic.getLocation())
                .addValue("meetingDateTime", DateUtils.toDate(aaic.getMeetingDateTime()))
                .addValue("notes", aaic.getNotes());
    }

    private static MapSqlParameterSource getAgendaInfoCommItemParams(AgendaInfoCommitteeItem aici, int aaicId) {
        return new MapSqlParameterSource()
                .addValue("alertInfoCommitteeId", aaicId)
                .addValue("billPrintNo", aici.getBillId().getBasePrintNo())
                .addValue("billSessionYear", aici.getBillId().getSession().year())
                .addValue("billAmendVersion", aici.getBillId().getVersion().toString())
                .addValue("message", aici.getMessage());
    }
}
