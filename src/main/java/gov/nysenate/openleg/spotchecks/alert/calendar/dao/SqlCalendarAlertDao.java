package gov.nysenate.openleg.spotchecks.alert.calendar.dao;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.calendar.Calendar;
import gov.nysenate.openleg.legislation.calendar.*;
import gov.nysenate.openleg.legislation.calendar.dao.SqlCalendarDao;
import gov.nysenate.openleg.spotchecks.alert.calendar.CalendarAlertFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.common.dao.CalendarParamUtils.*;
import static gov.nysenate.openleg.common.util.DateUtils.toDate;

/**
 * Responsible for database access of calendar alert references. These are similar to Calendar objects
 * but are based off LBDC Alert emails and used in the qa process.
 */
@Repository
public class SqlCalendarAlertDao extends SqlBaseDao implements CalendarAlertDao {
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarDao.class);

    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        ImmutableParams calParams = ImmutableParams.from(calendarIdParams(calendarId));
        Calendar calendar = jdbcNamed.queryForObject(SqlCalendarAlertQuery.SELECT_CALENDAR.getSql(schema()),
                calParams, new CalendarRowHandlers.CalendarRowMapper());
        populateCalendarFields(calendar);
        return calendar;
    }

    public List<CalendarId> getCalendarIds(int year, SortOrder calOrder, LimitOffset limitOffset) {
        OrderBy orderBy = new OrderBy("calendar_no", calOrder);
        ImmutableParams yearParam = ImmutableParams.from(new MapSqlParameterSource("year", year));
        return jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_IDS.getSql(schema(), orderBy, limitOffset),
                yearParam, new CalendarRowHandlers.CalendarIdRowMapper());
    }

    public void updateCalendar(Calendar calendar, CalendarAlertFile file) throws DataAccessException {
        logger.trace("Updating calendar {} in database...", calendar);
        ImmutableParams calParams = immutableParamsWithFile(getCalendarParams(calendar), file);
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
        List<Calendar> calendars = jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_RANGE.getSql(schema()),
                params, new CalendarRowHandlers.CalendarRowMapper());
        for (Calendar calendar : calendars) {
            populateCalendarFields(calendar);
        }
        return calendars;
    }

    @Override
    public void updateChecked(CalendarId id, boolean checked) {
        MapSqlParameterSource params  = calendarIdParams(id);
        params.addValue("checked", checked);
        jdbcNamed.update(SqlCalendarAlertQuery.UPDATE_CHECKED.getSql(schema()), params);
    }

    public void markProdAsChecked(CalendarId id) {
        MapSqlParameterSource params  = calendarIdParams(id);
        params.addValue("prodChecked", true);
        jdbcNamed.update(SqlCalendarAlertQuery.MARK_PROD_CHECKED.getSql(schema()), params);
    }

    public List<Calendar> getUnCheckedCalendarAlerts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("checked", false);
        List<Calendar> calendars = jdbcNamed.query(SqlCalendarAlertQuery.SELECT_UNCHECKED.getSql(schema()),
                params, new CalendarRowHandlers.CalendarRowMapper());
        for (Calendar calendar : calendars) {
            populateCalendarFields(calendar);
        }
        return calendars;
    }

    public List<Calendar> getProdUnCheckedCalendarAlerts() {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("prodChecked", false);
        List<Calendar> calendars = jdbcNamed.query(SqlCalendarAlertQuery.SELECT_PROD_UNCHECKED.getSql(schema()),
                params, new CalendarRowHandlers.CalendarRowMapper());
        for (Calendar calendar : calendars) {
            populateCalendarFields(calendar);
        }
        return calendars;
    }

    /** --- Internal Methods --- */

    private static ImmutableParams immutableParamsWithFile(MapSqlParameterSource params, CalendarAlertFile file) {
        return ImmutableParams.from(params.addValue("lastFile", file.getFile().getName()));
    }

    /**
     * Sets the supplemental map and active list map on the given calendar.
     * @param calendar
     */
    private void populateCalendarFields(Calendar calendar) {
        calendar.setSupplementalMap(getCalSupplementals(calendar.getId()));
        calendar.setActiveListMap(getActiveListMap(calendar.getId()));
    }
    /**
     * Retrieves all the supplementals for a particular calendar.
     */
    private EnumMap<Version, CalendarSupplemental> getCalSupplementals(CalendarId calendarId) {
        ImmutableParams calParams = ImmutableParams.from(calendarIdParams(calendarId));
        var calendarSupRowHandler = new CalendarRowHandlers.CalendarSupRowHandler(true);
        jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_SUPS.getSql(schema()), calParams, calendarSupRowHandler);
        return calendarSupRowHandler.getCalendarSupplementals().stream()
                .collect(Collectors.toMap(CalendarSupplemental::getVersion, Function.identity(),
                        (a, b) -> b, () -> new EnumMap<>(Version.class)));
    }

    /**
     * Updates the calendar supplementals. Entries belonging to supplementals that have not changed will not
     * be affected.
     */
    private void updateCalSupplementals(Calendar calendar, CalendarAlertFile file, ImmutableParams calParams) {
        Map<Version, CalendarSupplemental> existingCalSupMap = getCalSupplementals(calendar.getId());
        // Get the difference between the existing and current supplemental mappings
        MapDifference<Version, CalendarSupplemental> diff =
            Maps.difference(existingCalSupMap, calendar.getSupplementalMap());
        // Delete any supplementals that were not found in the current map or were different.
        Set<Version> deleteSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (Version supVersion : deleteSupVersions) {
            ImmutableParams calSupParams = calParams.add(new MapSqlParameterSource("supVersion", supVersion.toString()));
            jdbcNamed.update(SqlCalendarAlertQuery.DELETE_CALENDAR_SUP.getSql(schema()), calSupParams);
        }
        // Insert any new or differing supplementals
        Set<Version> updateSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (Version supVersion : updateSupVersions) {
            CalendarSupplemental sup = calendar.getSupplemental(supVersion);
            var supParams = immutableParamsWithFile(getCalSupplementalParams(sup), file);
            jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_SUP.getSql(schema()), supParams);
            // Insert the calendar entries
            for (CalendarSupplementalEntry entry : sup.getSectionEntries().values()) {
                var entryParams = immutableParamsWithFile(getCalSupEntryParams(sup, entry), file);
                jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_SUP_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /**
     * Retrieve the active list mappings for a specific calendar.
     */
    private TreeMap<Integer, CalendarActiveList> getActiveListMap(CalendarId calendarId) {
        ImmutableParams calParams = ImmutableParams.from(calendarIdParams(calendarId));
        var activeListRowHandler = new CalendarRowHandlers.ActiveListRowHandler(true);
        jdbcNamed.query(SqlCalendarAlertQuery.SELECT_CALENDAR_ACTIVE_LISTS.getSql(schema()), calParams, activeListRowHandler);
        return activeListRowHandler.getActiveLists().stream()
                .collect(Collectors.toMap(CalendarActiveList::getSequenceNo, Function.identity(), (a,b) -> b, TreeMap::new));
    }

    /**
     * Updates the calendar active lists. Entries belonging to active lists that have not changed will not
     * be affected.
     */
    private void updateCalActiveLists(Calendar calendar, CalendarAlertFile file, ImmutableParams calParams) {
        Map<Integer, CalendarActiveList> existingActiveListMap = getActiveListMap(calendar.getId());
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
            var actListParams = immutableParamsWithFile(getCalActiveListParams(actList), file);
            jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_ACTIVE_LIST.getSql(schema()), actListParams);
            // Insert the active list entries
            for (CalendarEntry entry : actList.getEntries()) {
                ImmutableParams entryParams = immutableParamsWithFile(getCalActiveListEntryParams(actList, entry), file);
                jdbcNamed.update(SqlCalendarAlertQuery.INSERT_CALENDAR_ACTIVE_LIST_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /** --- Param Source Methods --- */

    private static MapSqlParameterSource getDateRangeParams(LocalDateTime start, LocalDateTime end) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("startTime", toDate(start));
        params.addValue("endTime", toDate(end));
        return params;
    }
}
