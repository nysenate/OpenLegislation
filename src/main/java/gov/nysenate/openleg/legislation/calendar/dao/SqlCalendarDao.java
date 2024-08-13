package gov.nysenate.openleg.legislation.calendar.dao;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.common.dao.CalendarParamUtils.*;

@Repository
public class SqlCalendarDao extends SqlBaseDao implements CalendarDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarDao.class);

    /** {@inheritDoc} */
    @Override
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        var calParams = ImmutableParams.from(calendarIdParams(calendarId));
        // Get the base calendar
        Calendar calendar = jdbcNamed.queryForObject(SqlCalendarQuery.SELECT_CALENDAR.getSql(schema()),
                calParams, new CalendarRowHandlers.CalendarRowMapper());
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
        var params = ImmutableParams.from(getCalendarActiveListIdParams(calendarActiveListId));
        var activeListRowHandler = new CalendarRowHandlers.ActiveListRowHandler(false);
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
        var params = ImmutableParams.from(getCalendarSupplementalIdParams(calendarSupplementalId));
        var calendarSupRowHandler = new CalendarRowHandlers.CalendarSupRowHandler(false);
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
            return Range.closedOpen(0, 0);
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
                yearParam, new CalendarRowHandlers.CalendarIdRowMapper());
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
    public void updateCalendar(Calendar calendar, LegDataFragment fragment) throws DataAccessException {
        logger.trace("Updating calendar {} in database...", calendar);
        var calParams = immutableParamsWithFragment(getCalendarParams(calendar), fragment);
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

    private static ImmutableParams immutableParamsWithFragment(MapSqlParameterSource params, LegDataFragment fragment) {
        addLastFragmentParam(fragment, params);
        return ImmutableParams.from(params);
    }

    /**
     * Retrieves all the supplementals for a particular calendar.
     */
    private EnumMap<Version, CalendarSupplemental> getCalSupplementals(ImmutableParams calParams) {
        var calendarSupRowHandler = new CalendarRowHandlers.CalendarSupRowHandler(false);
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_SUPS.getSql(schema()), calParams, calendarSupRowHandler);
        return calendarSupRowHandler.getCalendarSupplementals().stream()
                .collect(Collectors.toMap(CalendarSupplemental::getVersion, Function.identity(), (a,b) -> b, () -> new EnumMap<>(Version.class)));
    }

    /**
     * Updates the calendar supplementals. Entries belonging to supplementals that have not changed will not
     * be affected.
     */
    private void updateCalSupplementals(Calendar calendar, LegDataFragment fragment, ImmutableParams calParams) {
        Map<Version, CalendarSupplemental> existingCalSupMap = getCalSupplementals(calParams);
        // Get the difference between the existing and current supplemental mappings
        MapDifference<Version, CalendarSupplemental> diff =
            Maps.difference(existingCalSupMap, calendar.getSupplementalMap());
        // Delete any supplementals that were not found in the current map or were different.
        Set<Version> deleteSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Version supVersion : deleteSupVersions) {
            ImmutableParams calSupParams = calParams.add(new MapSqlParameterSource("supVersion", supVersion.toString()));
            jdbcNamed.update(SqlCalendarQuery.DELETE_CALENDAR_SUP.getSql(schema()), calSupParams);
        }
        // Insert any new or differing supplementals
        Set<Version> updateSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Version supVersion : updateSupVersions) {
            CalendarSupplemental sup = calendar.getSupplemental(supVersion);
            var supParams = immutableParamsWithFragment(getCalSupplementalParams(sup), fragment);
            jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_SUP.getSql(schema()), supParams);
            // Insert the calendar entries
            for (CalendarSupplementalEntry entry : sup.getSectionEntries().values()) {
                MapSqlParameterSource params = CalendarParamUtils.getCalSupEntryParams(sup, entry);
                addLastFragmentParam(fragment, params);
                jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_SUP_ENTRY.getSql(schema()), ImmutableParams.from(params));
            }
        }
    }

    /**
     * Retrieve the active list mappings for a specific calendar.
     */
    private TreeMap<Integer, CalendarActiveList> getActiveListMap(ImmutableParams calParams) {
        var activeListRowHandler = new CalendarRowHandlers.ActiveListRowHandler(false);
        jdbcNamed.query(SqlCalendarQuery.SELECT_CALENDAR_ACTIVE_LISTS.getSql(schema()), calParams, activeListRowHandler);
        return activeListRowHandler.getActiveLists().stream()
                .collect(Collectors.toMap(CalendarActiveList::getSequenceNo, Function.identity(), (a,b) -> b, TreeMap::new));
    }

    /**
     * Updates the calendar active lists. Entries belonging to active lists that have not changed will not
     * be affected.
     */
    private void updateCalActiveLists(Calendar calendar, LegDataFragment fragment, ImmutableParams calParams) {
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
            var actListParams = immutableParamsWithFragment(getCalActiveListParams(actList), fragment);
            jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_ACTIVE_LIST.getSql(schema()), actListParams);
            // Insert the active list entries
            for (CalendarEntry entry : actList.getEntries()) {
                var entryParams = immutableParamsWithFragment(getCalActiveListEntryParams(actList, entry), fragment);
                jdbcNamed.update(SqlCalendarQuery.INSERT_CALENDAR_ACTIVE_LIST_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    private static class CalendarSupIdRowMapper implements RowMapper<CalendarSupplementalId> {
        @Override
        public CalendarSupplementalId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarSupplementalId(rs.getInt("calendar_no"), rs.getInt("calendar_year"), Version.of(rs.getString("sup_version")));
        }
    }

    private static class CalendarActiveListIdRowMapper implements RowMapper<CalendarActiveListId> {
        @Override
        public CalendarActiveListId mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CalendarActiveListId(rs.getInt("calendar_no"), rs.getInt("calendar_year"), rs.getInt("sequence_no"));
        }
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getCalendarActiveListIdParams(CalendarActiveListId calendarActiveListId) {
        return calendarIdParams(calendarActiveListId).addValue("sequenceNo", calendarActiveListId.getSequenceNo());
    }

    private static MapSqlParameterSource getCalendarSupplementalIdParams(CalendarSupplementalId calendarSupplementalId) {
        return calendarIdParams(calendarSupplementalId).addValue("supVersion", calendarSupplementalId.getVersion().toString());
    }
}
