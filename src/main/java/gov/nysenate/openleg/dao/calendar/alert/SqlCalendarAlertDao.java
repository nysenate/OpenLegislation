package gov.nysenate.openleg.dao.calendar.alert;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.dao.calendar.data.SqlCalendarDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.calendar.alert.CalendarAlertFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.util.DateUtils.toDate;

/**
 * Responsible for database access of calendar alert references. These are similar to Calendar objects
 * but are based off LBDC Alert emails and used in the qa process.
 */
@Repository
public class SqlCalendarAlertDao extends SqlBaseDao implements CalendarAlertDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarDao.class);

    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        ImmutableParams calParams = ImmutableParams.from(getCalendarIdParams(calendarId));
        Calendar calendar = jdbcNamed.queryForObject(SqlCalendarAlertQuery.SELECT_CALENDAR.getSql(schema()), calParams, CalendarRowMapper);
        return calendar;
    }

    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("calendar_no", calOrder);
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_IDS.getSql(schema(), orderBy, limitOffset),
                yearParam, new CalendarIdRowMapper());
    }

    public void updateCalendar(Calendar calendar, CalendarAlertFile file) throws DataAccessException {
        logger.trace("Updating calendar {} in database...", calendar);
        ImmutableParams calParams = ImmutableParams.from(getCalendarParams(calendar, file));
        // Update base calendar
        if (jdbcNamed.update(SqlCalendarAlertQuery.UPDATE_CALENDAR.getSql(schema()), calParams) == 0) {
            jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR.getSql(schema()), calParams);
        }
        // Update the associated calendar supplementals
        updateCalSupplementals(calendar, file, calParams);
        // Update the associated active lists
        updateCalActiveLists(calendar, file, calParams);
    }

    public List<Calendar> getCalendarAlertsByDateRange(LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = getDateRangeParams(start, end);
        List<Calendar> calendars = jdbcNamed.queryForObject(SqlCalendarAlertQuery.SELECT_CALENDAR_RANGE.getSql(schema()), params, new CalendarListRowMapper());
        return populateCalendars(calendars);
    }

    public void markAsChecked(CalendarId id) {
        MapSqlParameterSource params  = getCalendarIdParams(id);
        params.addValue("checked", true);
        jdbcNamed.update(SqlCalendarAlertQuery.MARK_CHECKED.getSql(schema()), params);
    }

    public void markProdAsChecked(CalendarId id) {
        MapSqlParameterSource params  = getCalendarIdParams(id);
        params.addValue("prodChecked", true);
        jdbcNamed.update(SqlCalendarAlertQuery.MARK_PROD_CHECKED.getSql(schema()), params);
    }

    public List<Calendar> getUnCheckedCalendarAlerts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checked", false);
        return jdbcNamed.query(SqlCalendarAlertQuery.SELECT_UNCHECKED.getSql(schema()), params, CalendarRowMapper);
    }

    public List<Calendar> getProdUnCheckedCalendarAlerts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("prodChecked", false);
        return jdbcNamed.query(SqlCalendarAlertQuery.SELECT_PROD_UNCHECKED.getSql(schema()), params, CalendarRowMapper);
    }

    /** --- Internal Methods --- */

    private List<Calendar> populateCalendars(List<Calendar> calendars) {
        for (Calendar cal: calendars) {
            ImmutableParams calParams = ImmutableParams.from(getCalendarIdParams(cal.getId()));
            cal.setSupplementalMap(getCalSupplementals(calParams));
            cal.setActiveListMap(getActiveListMap(calParams));
        }
        return calendars;
    }

    /**
     * Retrieves all the supplementals for a particular calendar.
     */
    private TreeMap<Version, CalendarSupplemental> getCalSupplementals(ImmutableParams calParams) {
        CalendarSupRowHandler calendarSupRowHandler = new CalendarSupRowHandler();
        jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_SUPS.getSql(schema()), calParams, calendarSupRowHandler);
        return calendarSupRowHandler.getCalendarSupplementals().stream()
                .collect(Collectors.toMap(CalendarSupplemental::getVersion, Function.identity(), (a, b) -> b, TreeMap::new));
    }

    /**
     * Updates the calendar supplementals. Entries belonging to supplementals that have not changed will not
     * be affected.
     */
    private void updateCalSupplementals(Calendar calendar, CalendarAlertFile file, ImmutableParams calParams) {
        Map<Version, CalendarSupplemental> existingCalSupMap = getCalSupplementals(calParams);
        // Get the difference between the existing and current supplemental mappings
        MapDifference<Version, CalendarSupplemental> diff =
            Maps.difference(existingCalSupMap, calendar.getSupplementalMap());
        // Delete any supplementals that were not found in the current map or were different.
        Set<Version> deleteSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Version supVersion : deleteSupVersions) {
            ImmutableParams calSupParams = calParams.add(new MapSqlParameterSource("supVersion", supVersion.getValue()));
            jdbcNamed.update(SqlCalendarAlertQuery.DELETE_CALENDAR_SUP.getSql(schema()), calSupParams);
        }
        // Insert any new or differing supplementals
        Set<Version> updateSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Version supVersion : updateSupVersions) {
            CalendarSupplemental sup = calendar.getSupplemental(supVersion);
            ImmutableParams supParams = ImmutableParams.from(getCalSupplementalParams(sup, file));
            jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_SUP.getSql(schema()), supParams);
            // Insert the calendar entries
            for (CalendarSupplementalEntry entry : sup.getSectionEntries().values()) {
                ImmutableParams entryParams = ImmutableParams.from(getCalSupEntryParams(sup, entry, file));
                jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_SUP_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /**
     * Retrieve the active list mappings for a specific calendar.
     */
    private TreeMap<Integer, CalendarActiveList> getActiveListMap(ImmutableParams calParams) {
        ActiveListRowHandler activeListRowHandler = new ActiveListRowHandler();
        jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_ACTIVE_LISTS.getSql(schema()), calParams, activeListRowHandler);
        return activeListRowHandler.getActiveLists().stream()
                .collect(Collectors.toMap(CalendarActiveList::getSequenceNo, Function.identity(), (a,b) -> b, TreeMap::new));
    }

    /**
     * Updates the calendar active lists. Entries belonging to active lists that have not changed will not
     * be affected.
     */
    private void updateCalActiveLists(Calendar calendar, CalendarAlertFile file, ImmutableParams calParams) {
        Map<Integer, CalendarActiveList> existingActiveListMap = getActiveListMap(calParams);
        // Get the difference between the existing and current active list mappings.
        MapDifference<Integer, CalendarActiveList> diff =
                Maps.difference(existingActiveListMap, calendar.getActiveListMap());
        // Delete any active lists that were not found in the current map or were different.
        Set<Integer> deleteActListSeqs = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Integer actListSeq : deleteActListSeqs) {
            ImmutableParams activeListParams = calParams.add(new MapSqlParameterSource("sequenceNo", actListSeq));
            jdbcNamed.update(SqlCalendarAlertQuery.DELETE_CALENDAR_ACTIVE_LIST.getSql(schema()), activeListParams);
        }
        // Insert any new or differing active lists
        Set<Integer> updateActListSeqs = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Integer actListSeq : updateActListSeqs) {
            CalendarActiveList actList = calendar.getActiveList(actListSeq);
            ImmutableParams actListParams = ImmutableParams.from(getCalActiveListParams(actList, file));
            jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_ACTIVE_LIST.getSql(schema()), actListParams);
            // Insert the active list entries
            for (CalendarEntry entry : actList.getEntries()) {
                ImmutableParams entryParams = ImmutableParams.from(getCalActiveListEntryParams(actList, entry, file));
                jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_ACTIVE_LIST_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /** --- Helper Classes --- */

    private RowMapper<Calendar> CalendarRowMapper = (rs, rowNum) -> {
        Calendar calendar = setCalendarIdFromResultSet(rs);
        setModPubDatesFromResultSet(calendar, rs);
        ImmutableParams calParams = ImmutableParams.from(getCalendarIdParams(calendar.getId()));
        calendar.setSupplementalMap(getCalSupplementals(calParams));
        calendar.setActiveListMap(getActiveListMap(calParams));
        return calendar;
    };

    private class CalendarListRowMapper implements RowMapper<List<Calendar>> {

        @Override
        public List<Calendar> mapRow(ResultSet rs, int rowNum) throws SQLException {
            List<Calendar> calendars = new ArrayList<>();
            while(rs.next()) {
                Calendar calendar = setCalendarIdFromResultSet(rs);
                setModPubDatesFromResultSet(calendar, rs);
                calendars.add(calendar);
            }
            return calendars;
        }
    }

    private Calendar setCalendarIdFromResultSet(ResultSet rs) throws SQLException {
        return new Calendar(new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")));
    }

    private class CalendarIdRowMapper implements RowMapper<CalendarId>
    {
        @Override
        public CalendarId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
        }
    }

    private class CalendarSupRowMapper implements RowMapper<CalendarSupplemental>
    {
        @Override
        public CalendarSupplemental mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarId calendarId = new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
            Version version = Version.of(rs.getString("sup_version"));
            LocalDate calDate = getLocalDateFromRs(rs, "calendar_date");
            LocalDateTime releaseDateTime = getLocalDateTimeFromRs(rs, "release_date_time");
            CalendarSupplemental calSup = new CalendarSupplemental(calendarId, version, calDate, releaseDateTime);
            setModPubDatesFromResultSet(calSup, rs);
            return calSup;
        }
    }

    private class CalendarSupEntryRowMapper implements RowMapper<CalendarSupplementalEntry>
    {
        @Override
        public CalendarSupplementalEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarSectionType sectionType = CalendarSectionType.valueOfCode(rs.getInt("section_code"));
            int billCalNo = rs.getInt("bill_calendar_no");
            BillId billId = new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                       rs.getString("bill_amend_version"));
            BillId subBillId = null;
            if (rs.getString("sub_bill_print_no") != null) {
                subBillId = new BillId(rs.getString("sub_bill_print_no"), rs.getInt("sub_bill_session_year"),
                                       rs.getString("sub_bill_amend_version"));
            }
            boolean high = rs.getBoolean("high");
            return new CalendarSupplementalEntry(billCalNo, sectionType, billId, subBillId, high);
        }
    }

    private class CalendarSupRowHandler implements RowCallbackHandler
    {
        private CalendarSupRowMapper calendarSupRowMapper;
        private CalendarSupEntryRowMapper calendarSupEntryRowMapper;

        private Map<Integer, CalendarSupplemental> resultMap;

        public CalendarSupRowHandler() {
            calendarSupRowMapper = new CalendarSupRowMapper();
            calendarSupEntryRowMapper = new CalendarSupEntryRowMapper();
            resultMap = new LinkedHashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer calSupId = rs.getInt("calendar_sup_id");
            if (!resultMap.containsKey(calSupId)) {
                resultMap.put(calSupId, calendarSupRowMapper.mapRow(rs, rs.getRow()));
            }
            resultMap.get(calSupId).addEntry(calendarSupEntryRowMapper.mapRow(rs, rs.getRow()));
        }

        public ArrayList<CalendarSupplemental> getCalendarSupplementals() {
            return new ArrayList<>(resultMap.values());
        }
    }

    private class CalendarActiveListRowMapper implements RowMapper<CalendarActiveList>
    {
        @Override
        public CalendarActiveList mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarActiveList activeList = new CalendarActiveList();
            activeList.setSequenceNo(rs.getInt("sequence_no"));
            activeList.setCalendarId(new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")));
            activeList.setCalDate(getLocalDateFromRs(rs, "calendar_date"));
            activeList.setReleaseDateTime(getLocalDateTimeFromRs(rs, "release_date_time"));
            setModPubDatesFromResultSet(activeList, rs);
            return activeList;
        }
    }

    private class CalendarActiveListEntryRowMapper implements RowMapper<CalendarEntry>
    {
        @Override
        public CalendarEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarEntry entry = new CalendarEntry();
            entry.setBillCalNo(rs.getInt("bill_calendar_no"));
            entry.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year"),
                                       rs.getString("bill_amend_version")));
            return entry;
        }
    }

    private class ActiveListRowHandler implements RowCallbackHandler
    {
        private CalendarActiveListRowMapper calendarActiveListRowMapper;
        private CalendarActiveListEntryRowMapper calendarActiveListEntryRowMapper;

        private Map<Integer, CalendarActiveList> resultMap;

        public ActiveListRowHandler() {
            calendarActiveListRowMapper = new CalendarActiveListRowMapper();
            calendarActiveListEntryRowMapper = new CalendarActiveListEntryRowMapper();
            resultMap = new LinkedHashMap<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            Integer calALId = rs.getInt("calendar_active_list_id");
            if (!resultMap.containsKey(calALId)) {
                resultMap.put(calALId, calendarActiveListRowMapper.mapRow(rs, rs.getRow()));
            }
            resultMap.get(calALId).addEntry(calendarActiveListEntryRowMapper.mapRow(rs, rs.getRow()));
        }

        public ArrayList<CalendarActiveList> getActiveLists() {
            return new ArrayList<>(resultMap.values());
        }
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getCalendarIdParams(CalendarId calendarId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendarId, params);
        return params;
    }

    private static MapSqlParameterSource getCalendarParams(Calendar calendar, CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendar.getId(), params);
        addModPubDateParams(calendar.getModifiedDateTime(), calendar.getPublishedDateTime(), params);
        addLastFile(file, params);
        return params;
    }

    private MapSqlParameterSource getDateRangeParams(LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startTime", toDate(start));
        params.addValue("endTime", toDate(end));
        return params;
    }

    private static MapSqlParameterSource getCalSupplementalParams(CalendarSupplemental sup, CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(sup.getCalendarId(), params);
        params.addValue("supVersion", sup.getVersion().getValue());
        params.addValue("calendarDate", toDate(sup.getCalDate()));
        params.addValue("releaseDateTime", toDate(sup.getReleaseDateTime()));
        addModPubDateParams(sup.getModifiedDateTime(), sup.getPublishedDateTime(), params);
        addLastFile(file, params);
        return params;
    }

    private static MapSqlParameterSource getCalSupEntryParams(CalendarSupplemental sup, CalendarSupplementalEntry entry,
                                                                CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(sup.getCalendarId(), params);
        params.addValue("supVersion", sup.getVersion().getValue());
        params.addValue("sectionCode", entry.getSectionType().getCode());
        params.addValue("billCalNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        BillId subBillId = entry.getSubBillId();
        params.addValue("subPrintNo", (subBillId != null) ? subBillId.getBasePrintNo() : null);
        params.addValue("subSession", (subBillId != null) ? subBillId.getSession().getYear() : null);
        params.addValue("subAmendVersion", (subBillId != null) ? subBillId.getVersion().getValue() : null);
        params.addValue("high", entry.getBillHigh());
        addLastFile(file, params);
        return params;
    }

    private static MapSqlParameterSource getCalActiveListParams(CalendarActiveList actList, CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(actList.getCalendarId(), params);
        params.addValue("sequenceNo", actList.getSequenceNo());
        params.addValue("calendarDate", toDate(actList.getCalDate()));
        params.addValue("releaseDateTime", toDate(actList.getReleaseDateTime()));
        params.addValue("notes", actList.getNotes());
        addModPubDateParams(actList.getModifiedDateTime(), actList.getPublishedDateTime(), params);
        addLastFile(file, params);
        return params;
    }

    private static MapSqlParameterSource getCalActiveListEntryParams(CalendarActiveList actList,
                                                                       CalendarEntry entry, CalendarAlertFile file) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(actList.getCalendarId(), params);
        params.addValue("sequenceNo", actList.getSequenceNo());
        params.addValue("billCalendarNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        addLastFile(file, params);
        return params;
    }

    private static void addCalendarIdParams(CalendarId calendarId, MapSqlParameterSource params) {
        params.addValue("calendarNo", calendarId.getCalNo());
        params.addValue("year", calendarId.getYear());
    }

    private static void addBillIdParams(BillId billId, MapSqlParameterSource params) {
        params.addValue("printNo", billId.getBasePrintNo());
        params.addValue("session", billId.getSession().getYear());
        params.addValue("amendVersion", billId.getVersion().getValue());
    }

    private static MapSqlParameterSource addLastFile(CalendarAlertFile file, MapSqlParameterSource params) {
        return params.addValue("lastFile", file.getFile().getName());
    }
}
