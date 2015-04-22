package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.*;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
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

@Repository
public class SqlCalendarDao extends SqlBaseDao implements CalendarDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarDao.class);

    /** {@inheritDoc} */
    @Override
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        ImmutableParams calParams = ImmutableParams.from(getCalendarIdParams(calendarId));
        // Get the base calendar
        Calendar calendar = jdbcNamed.queryForObject(SqlCalendarQuery.SELECT_CALENDAR.getSql(schema()), calParams, new CalendarRowMapper());
        // Get the supplementals
        calendar.setSupplementalMap(getCalSupplementals(calParams));
        // Get the active lists
        calendar.setActiveListMap(getActiveListMap(calParams));
        // Calendar is fully constructed
        return calendar;
    }

    /** {@inheritDoc} */
    @Override
    public CalendarActiveList getActiveList(CalendarActiveListId calendarActiveListId) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(getCalendarActiveListIdParams(calendarActiveListId));
        ActiveListRowHandler activeListRowHandler = new ActiveListRowHandler();
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LIST.getSql(schema()), params, activeListRowHandler);
        try {
            return activeListRowHandler.getActiveLists().get(0);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CalendarSupplemental getCalendarSupplemental(CalendarSupplementalId calendarSupplementalId) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(getCalendarSupplementalIdParams(calendarSupplementalId));
        CalendarSupRowHandler calendarSupRowHandler = new CalendarSupRowHandler();
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_SUP.getSql(schema()), params, calendarSupRowHandler);
        try {
            return calendarSupRowHandler.getCalendarSupplementals().get(0);
        }
        catch (IndexOutOfBoundsException ex) {
            throw new EmptyResultDataAccessException(1);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Range<Integer> getActiveYearRange() {
        if (getCalendarCount() == 0) {
            throw new EmptyResultDataAccessException("Cannot retrieve active year range as there are no stored calendars", 1);
        }
        return jdbc.queryForObject(SqlCalendarQuery.SELECT_CALENDAR_YEAR_RANGE.getSql(schema()),
                (rs, row) -> Range.closed(rs.getInt("min"), rs.getInt("max")));
    }

    /** {@inheritDoc} */
    @Override
    public int getCalendarCount() {
        return jdbc.queryForObject(SqlCalendarQuery.SELECT_TOTAL_COUNT.getSql(schema()), Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    public int getCalendarCount(int year) {
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.queryForObject(SqlCalendarQuery.SELECT_CALENDARS_COUNT.getSql(schema()), yearParam, Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    public int getActiveListCount(int year) {
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.queryForObject(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LIST_ID_COUNT.getSql(schema()), yearParam, Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    public int getCalendarSupplementalCount(int year) {
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.queryForObject(SqlCalendarQuery.SELECT_CALENDAR_SUP_ID_COUNT.getSql(schema()), yearParam, Integer.class);
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("calendar_no", calOrder);
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_IDS.getSql(schema(), orderBy, limitOffset),
                yearParam, new CalendarIdRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarActiveListId> getActiveListIds(int year, SortOrder sortOrder, LimitOffset limitOffset)
            throws DataAccessException {
        OrderBy orderBy = new OrderBy("calendar_no", sortOrder, "sequence_no", sortOrder);
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LIST_IDS.getSql(schema(), orderBy, limitOffset),
                yearParam, new CalendarActiveListIdRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<CalendarSupplementalId> getCalendarSupplementalIds(int year, SortOrder sortOrder, LimitOffset limitOffset)
            throws DataAccessException {
        OrderBy orderBy = new OrderBy("calendar_no", sortOrder, "sup_version", sortOrder);
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_SUP_IDS.getSql(schema(), orderBy, limitOffset),
                yearParam, new CalendarSupIdRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public void updateCalendar(Calendar calendar, SobiFragment fragment) throws DataAccessException {
        logger.trace("Updating calendar {} in database...", calendar);
        ImmutableParams calParams = ImmutableParams.from(getCalendarParams(calendar, fragment));
        // Update base calendar
        if (jdbcNamed.update(SqlCalendarQuery.UPDATE_CALENDAR.getSql(schema()), calParams) == 0) {
            jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR.getSql(schema()), calParams);
        }
        // Update the associated calendar supplementals
        updateCalSupplementals(calendar, fragment, calParams);
        // Update the associated active lists
        updateCalActiveLists(calendar, fragment, calParams);
    }

    /** --- Internal Methods --- */

    /**
     * Retrieves all the supplementals for a particular calendar.
     */
    private TreeMap<Version, CalendarSupplemental> getCalSupplementals(ImmutableParams calParams) {
        CalendarSupRowHandler calendarSupRowHandler = new CalendarSupRowHandler();
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_SUPS.getSql(schema()), calParams, calendarSupRowHandler);
        return calendarSupRowHandler.getCalendarSupplementals().stream()
                .collect(Collectors.toMap(CalendarSupplemental::getVersion, Function.identity(), (a,b) -> b, TreeMap::new));
    }

    /**
     * Retrieves the supplemental entries for a particular supplemental.
     */
    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getCalSupEntries(ImmutableParams supParams) {
        List<CalendarSupplementalEntry> entries =
            jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_SUP_ENTRIES.getSql(schema()), supParams, new CalendarSupEntryRowMapper());
        LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> sectionEntries = LinkedListMultimap.create();
        for (CalendarSupplementalEntry entry : entries) {
            sectionEntries.put(entry.getSectionType(), entry);
        }
        return sectionEntries;
    }

    /**
     * Updates the calendar supplementals. Entries belonging to supplementals that have not changed will not
     * be affected.
     */
    private void updateCalSupplementals(Calendar calendar, SobiFragment fragment, ImmutableParams calParams) {
        Map<Version, CalendarSupplemental> existingCalSupMap = getCalSupplementals(calParams);
        // Get the difference between the existing and current supplemental mappings
        MapDifference<Version, CalendarSupplemental> diff =
            Maps.difference(existingCalSupMap, calendar.getSupplementalMap());
        // Delete any supplementals that were not found in the current map or were different.
        Set<Version> deleteSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Version supVersion : deleteSupVersions) {
            ImmutableParams calSupParams = calParams.add(new MapSqlParameterSource("supVersion", supVersion.getValue()));
            jdbcNamed.update(SqlCalendarQuery.DELETE_CALENDAR_SUP.getSql(schema()), calSupParams);
        }
        // Insert any new or differing supplementals
        Set<Version> updateSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Version supVersion : updateSupVersions) {
            CalendarSupplemental sup = calendar.getSupplemental(supVersion);
            ImmutableParams supParams = ImmutableParams.from(getCalSupplementalParams(sup, fragment));
            jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_SUP.getSql(schema()), supParams);
            // Insert the calendar entries
            for (CalendarSupplementalEntry entry : sup.getSectionEntries().values()) {
                ImmutableParams entryParams = ImmutableParams.from(getCalSupEntryParams(sup, entry, fragment));
                jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_SUP_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /**
     * Retrieve the active list mappings for a specific calendar.
     */
    private TreeMap<Integer, CalendarActiveList> getActiveListMap(ImmutableParams calParams) {
        ActiveListRowHandler activeListRowHandler = new ActiveListRowHandler();
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LISTS.getSql(schema()), calParams, activeListRowHandler);
        return activeListRowHandler.getActiveLists().stream()
                .collect(Collectors.toMap(CalendarActiveList::getSequenceNo, Function.identity(), (a,b) -> b, TreeMap::new));
    }

    /**
     * Retrieve entries for a specific active list.
     */
    private List<CalendarEntry> getActiveListEntries(ImmutableParams activeListParams) {
        return jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LIST_ENTRIES.getSql(schema()), activeListParams,
                               new CalendarActiveListEntryRowMapper());
    }

    /**
     * Updates the calendar active lists. Entries belonging to active lists that have not changed will not
     * be affected.
     */
    private void updateCalActiveLists(Calendar calendar, SobiFragment fragment, ImmutableParams calParams) {
        Map<Integer, CalendarActiveList> existingActiveListMap = getActiveListMap(calParams);
        // Get the difference between the existing and current active list mappings.
        MapDifference<Integer, CalendarActiveList> diff =
                Maps.difference(existingActiveListMap, calendar.getActiveListMap());
        // Delete any active lists that were not found in the current map or were different.
        Set<Integer> deleteActListSeqs = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Integer actListSeq : deleteActListSeqs) {
            ImmutableParams activeListParams = calParams.add(new MapSqlParameterSource("sequenceNo", actListSeq));
            jdbcNamed.update(SqlCalendarQuery.DELETE_CALENDAR_ACTIVE_LIST.getSql(schema()), activeListParams);
        }
        // Insert any new or differing active lists
        Set<Integer> updateActListSeqs = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Integer actListSeq : updateActListSeqs) {
            CalendarActiveList actList = calendar.getActiveList(actListSeq);
            ImmutableParams actListParams = ImmutableParams.from(getCalActiveListParams(actList, fragment));
            jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_ACTIVE_LIST.getSql(schema()), actListParams);
            // Insert the active list entries
            for (CalendarEntry entry : actList.getEntries()) {
                ImmutableParams entryParams = ImmutableParams.from(getCalActiveListEntryParams(actList, entry, fragment));
                jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_ACTIVE_LIST_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /** --- Helper Classes --- */

    protected class CalendarRowMapper implements RowMapper<Calendar>
    {
        @Override
        public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
            Calendar calendar = new Calendar(new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")));
            setModPubDatesFromResultSet(calendar, rs);
            return calendar;
        }
    }

    protected class CalendarIdRowMapper implements RowMapper<CalendarId>
    {
        @Override
        public CalendarId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
        }
    }

    protected class CalendarSupRowMapper implements RowMapper<CalendarSupplemental>
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

    protected class CalendarSupIdRowMapper implements RowMapper<CalendarSupplementalId> {
        @Override
        public CalendarSupplementalId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarSupplementalId(rs.getInt("calendar_no"), rs.getInt("calendar_year"), Version.of(rs.getString("sup_version")));
        }
    }

    protected class CalendarSupEntryRowMapper implements RowMapper<CalendarSupplementalEntry>
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

    protected class CalendarSupRowHandler implements RowCallbackHandler
    {
        protected CalendarSupRowMapper calendarSupRowMapper;
        protected CalendarSupEntryRowMapper calendarSupEntryRowMapper;

        protected Map<Integer, CalendarSupplemental> resultMap;

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

    protected class CalendarActiveListRowMapper implements RowMapper<CalendarActiveList>
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

    protected class CalendarActiveListIdRowMapper implements RowMapper<CalendarActiveListId>
    {
        @Override
        public CalendarActiveListId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarActiveListId(rs.getInt("calendar_no"), rs.getInt("calendar_year"), rs.getInt("sequence_no"));
        }
    }

    protected class CalendarActiveListEntryRowMapper implements RowMapper<CalendarEntry>
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

    protected class ActiveListRowHandler implements RowCallbackHandler
    {
        protected CalendarActiveListRowMapper calendarActiveListRowMapper;
        protected CalendarActiveListEntryRowMapper calendarActiveListEntryRowMapper;

        protected Map<Integer, CalendarActiveList> resultMap;

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

    protected static MapSqlParameterSource getCalendarIdParams(CalendarId calendarId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendarId, params);
        return params;
    }

    protected static MapSqlParameterSource getCalendarActiveListIdParams(CalendarActiveListId calendarActiveListId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendarActiveListId, params);
        params.addValue("sequenceNo", calendarActiveListId.getSequenceNo());
        return params;
    }

    protected static MapSqlParameterSource getCalendarSupplementalIdParams(CalendarSupplementalId calendarSupplementalId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendarSupplementalId, params);
        params.addValue("supVersion", calendarSupplementalId.getVersion().getValue());
        return params;
    }

    protected static MapSqlParameterSource getCalendarParams(Calendar calendar, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendar.getId(), params);
        addModPubDateParams(calendar.getModifiedDateTime(), calendar.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalSupplementalParams(CalendarSupplemental sup, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(sup.getCalendarId(), params);
        params.addValue("supVersion", sup.getVersion().getValue());
        params.addValue("calendarDate", toDate(sup.getCalDate()));
        params.addValue("releaseDateTime", toDate(sup.getReleaseDateTime()));
        addModPubDateParams(sup.getModifiedDateTime(), sup.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalSupEntryParams(CalendarSupplemental sup, CalendarSupplementalEntry entry,
                                                                SobiFragment fragment) {
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
        addLastFragmentParam(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalActiveListParams(CalendarActiveList actList, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(actList.getCalendarId(), params);
        params.addValue("sequenceNo", actList.getSequenceNo());
        params.addValue("calendarDate", toDate(actList.getCalDate()));
        params.addValue("releaseDateTime", toDate(actList.getReleaseDateTime()));
        params.addValue("notes", actList.getNotes());
        addModPubDateParams(actList.getModifiedDateTime(), actList.getPublishedDateTime(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalActiveListEntryParams(CalendarActiveList actList,
                                                                       CalendarEntry entry, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(actList.getCalendarId(), params);
        params.addValue("sequenceNo", actList.getSequenceNo());
        params.addValue("billCalendarNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        addLastFragmentParam(fragment, params);
        return params;
    }

    protected static void addCalendarIdParams(CalendarId calendarId, MapSqlParameterSource params) {
        params.addValue("calendarNo", calendarId.getCalNo());
        params.addValue("year", calendarId.getYear());
    }

    protected static void addBillIdParams(BillId billId, MapSqlParameterSource params) {
        params.addValue("printNo", billId.getBasePrintNo());
        params.addValue("session", billId.getSession().getYear());
        params.addValue("amendVersion", billId.getVersion().getValue());
    }
}
