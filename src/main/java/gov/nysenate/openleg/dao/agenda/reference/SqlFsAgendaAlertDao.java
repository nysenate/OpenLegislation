package gov.nysenate.openleg.dao.agenda.reference;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Range;
import com.google.common.io.Files;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.bill.reference.daybreak.SqlFsDaybreakDao;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommId;
import gov.nysenate.openleg.model.spotcheck.agenda.AgendaAlertInfoCommittee;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceId;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.FileIOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.agenda.reference.SqlAgendaAlertQuery.*;

@Repository
public class SqlFsAgendaAlertDao extends SqlBaseDao implements AgendaAlertDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlFsDaybreakDao.class);

    private static final Pattern agendaFilePattern =
            Pattern.compile("^agenda_alert-\\d{8}-[A-z\\._]+-[A-z]+-?\\d{8}T\\d{6}.html$");

//  This pattern parses both full and individual agenda alert filenames, but currently we can't reliably process full alerts
    private static final Pattern agendaFullFilePattern =
            Pattern.compile("^agenda_alert-\\d{8}-[A-z\\._-]*\\d{8}T\\d{6}.html$");

    @Autowired private Environment environment;

    private File incomingAgendaAlertDir;
    private File archiveAgendaAlertDir;

    @PostConstruct
    public void init() {
        incomingAgendaAlertDir = new File(environment.getStagingDir(), "alerts");
        archiveAgendaAlertDir = new File(environment.getArchiveDir(), "alerts");
    }

    /** {@inheritDoc} */
    @Override
    public List<File> getIncomingAgendaAlerts() throws IOException {
        List<File> agendaAlerts = FileIOUtils.safeListFiles(incomingAgendaAlertDir, null, false).stream()
                .filter(file -> agendaFullFilePattern.matcher(file.getName()).matches())
                .collect(Collectors.toList());
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
        Files.move(agendaAlert, new File(archiveAgendaAlertDir, agendaAlert.getName()));
    }

    /** {@inheritDoc} */
    @Override
    public AgendaAlertInfoCommittee getAgendaAlertInfoCommittee(AgendaAlertInfoCommId agendaCommInfoId) {
        AgendaAlertInfoCommRowHandler rowHandler = new AgendaAlertInfoCommRowHandler();
        jdbcNamed.query(SELECT_INFO_COMMITTEE_BY_ID.getSql(schema(), LimitOffset.ONE),
                getAgendaAlertInfoCommIdParams(agendaCommInfoId), rowHandler);
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
        deleteAAIC(aaic.getAgendaAlertInfoCommId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_INFO_COMMITTEE.getSql(schema()), getAgendaAlertInfoCommParams(aaic),
                keyHolder, new String[]{"id"});

        aaic.getItems().forEach(item -> insertAAICItem(item, keyHolder.getKey().intValue()));
    }

    /** {@inheritDoc} */
    @Override
    public void setAgendaAlertChecked(AgendaAlertInfoCommId agendaAlertId, boolean checked) {
        MapSqlParameterSource params = getAgendaAlertInfoCommIdParams(agendaAlertId);
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
     * @param aaicID AgendaAlertInfoCommId
     */
    private void deleteAAIC(AgendaAlertInfoCommId aaicID) {
        jdbcNamed.update(DELETE_INFO_COMMITTEE.getSql(schema()), getAgendaAlertInfoCommIdParams(aaicID));
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
                .collect(Collectors.toList());
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
        private Map<Integer, AgendaAlertInfoCommittee> committeeMeetingRefMap = new HashMap<>();

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

    private MapSqlParameterSource getAgendaAlertIdParams(AgendaAlertId alertId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("referenceDateTime", DateUtils.toDate(alertId.getReferenceDateTime()));
        params.addValue("weekOf", DateUtils.toDate(alertId.getWeekOf()));
        return params;
    }

    private MapSqlParameterSource getAgendaAlertInfoCommIdParams(AgendaAlertInfoCommId commId) {
        MapSqlParameterSource params = getAgendaAlertIdParams(commId);
        params.addValue("addendumId", commId.getAddendum().getValue());
        params.addValue("chamber", commId.getCommitteeId().getChamber().asSqlEnum());
        params.addValue("committeeName", commId.getCommitteeId().getName());
        return params;
    }

    private MapSqlParameterSource getAgendaAlertInfoCommParams(AgendaAlertInfoCommittee aaic) {
        MapSqlParameterSource params = getAgendaAlertInfoCommIdParams(aaic.getAgendaAlertInfoCommId());
        params.addValue("chair", aaic.getChair());
        params.addValue("location", aaic.getLocation());
        params.addValue("meetingDateTime", DateUtils.toDate(aaic.getMeetingDateTime()));
        params.addValue("notes", aaic.getNotes());
        return params;
    }

    private MapSqlParameterSource getAgendaInfoCommItemParams(AgendaInfoCommitteeItem aici, int aaicId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("alertInfoCommitteeId", aaicId);
        params.addValue("billPrintNo", aici.getBillId().getBasePrintNo());
        params.addValue("billSessionYear", aici.getBillId().getSession().getYear());
        params.addValue("billAmendVersion", aici.getBillId().getVersion().getValue());
        params.addValue("message", aici.getMessage());
        return params;
    }
}
