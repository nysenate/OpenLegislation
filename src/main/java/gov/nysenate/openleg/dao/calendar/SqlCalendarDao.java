package gov.nysenate.openleg.dao.calendar;

import com.google.common.collect.*;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.*;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.nysenate.openleg.dao.calendar.SqlCalendarQuery.*;

@Repository
public class SqlCalendarDao extends SqlBaseDao implements CalendarDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarDao.class);

    /** {@inheritDoc} */
    @Override
    public Calendar getCalendar(CalendarId calendarId) throws DataAccessException {
        MapSqlParameterSource calParams = new MapSqlParameterSource();
        addCalendarIdParams(calendarId, calParams);
        Calendar calendar = jdbcNamed.queryForObject(SELECT_CALENDAR.getSql(schema()), calParams, new CalendarRowMapper());
        calendar.setSupplementalMap(getCalSupplementals(calParams));
        return calendar;
    }

    /** {@inheritDoc} */
    @Override
    public List<Calendar> getCalendars(int year, SortOrder dateOrder) throws DataAccessException {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void updateCalendar(Calendar calendar, SobiFragment sobiFragment) throws DataAccessException {
        logger.trace("Updating calendar {} in database...", calendar);
        MapSqlParameterSource calParams = getCalendarParams(calendar, sobiFragment);
        // Update basic calendar info
        if (jdbcNamed.update(UPDATE_CALENDAR.getSql(schema()), calParams) == 0) {
            jdbcNamed.update(INSERT_CALENDAR.getSql(schema()), calParams);
        }
        // Update the associated calendar supplementals
        updateCalSupplementals(calendar, sobiFragment, calParams);
    }

    /** --- Internal Methods --- */

    private TreeMap<String, CalendarSupplemental> getCalSupplementals(MapSqlParameterSource calParams) {
        List<CalendarSupplemental> calSupList =
                jdbcNamed.query(SELECT_CALENDAR_SUPS.getSql(schema()), calParams, new CalendarSupRowMapper());
        TreeMap<String, CalendarSupplemental> supMap = new TreeMap<>();
        for (CalendarSupplemental calSup : calSupList) {
            calParams.addValue("supVersion", calSup.getVersion());
            calSup.setSectionEntries(getCalSupEntries(calParams));
            supMap.put(calSup.getVersion(), calSup);
        }
        return supMap;
    }

    private LinkedListMultimap<CalendarSectionType, CalendarSupplementalEntry> getCalSupEntries(
            MapSqlParameterSource supParams) {
        return null;
    }

    private void updateCalSupplementals(Calendar calendar, SobiFragment sobiFragment, MapSqlParameterSource calParams) {
        Map<String, CalendarSupplemental> existingCalSupMap = getCalSupplementals(calParams);
        // Get the difference between the existing and current supplemental mappings
        MapDifference<String, CalendarSupplemental> diff =
            Maps.difference(existingCalSupMap, calendar.getSupplementalMap());
        // Delete any supplementals that were not found in the current map or were different.
        Set<String> deleteSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnLeft().keySet());
        for (String supVersion : deleteSupVersions) {
            calParams.addValue("supVersion", supVersion);
            jdbcNamed.update(DELETE_CALENDAR_SUP.getSql(schema()), calParams);
        }
        // Insert any new or differing supplementals
        Set<String> updateSupVersions = Sets.union(diff.entriesDiffering().keySet(), diff.entriesOnlyOnRight().keySet());
        for (String supVersion : updateSupVersions) {
            CalendarSupplemental sup = calendar.getSupplemental(supVersion);
            MapSqlParameterSource supParams = getCalSupplementalParams(sup, sobiFragment);
            jdbcNamed.update(INSERT_CALENDAR_SUP.getSql(schema()), supParams);
            // Insert the calendar entries
            for (CalendarSupplementalEntry entry : sup.getSectionEntries().values()) {
                MapSqlParameterSource entryParams = getCalSupEntryParams(entry);
                jdbcNamed.update(INSERT_CALENDAR_SUP_ENTRY.getSql(schema()), entryParams);
            }
        }
    }

    /** --- Helper Classes --- */

    protected class CalendarRowMapper implements RowMapper<Calendar>
    {
        @Override
        public Calendar mapRow(ResultSet rs, int rowNum) throws SQLException {
            Calendar calendar = new Calendar(new CalendarId(rs.getInt("calendar_no"), rs.getInt("year")));
            calendar.setModifiedDate(rs.getTimestamp("modified_date_time"));
            calendar.setPublishDate(rs.getTimestamp("published_date_time"));
            return calendar;
        }
    }

    protected class CalendarSupRowMapper implements RowMapper<CalendarSupplemental>
    {
        @Override
        public CalendarSupplemental mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarId calendarId = new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year"));
            String version = rs.getString("sup_version");
            Date calDate = rs.getDate("calendar_date");
            Date releaseDateTime = rs.getTimestamp("release_date_time");
            CalendarSupplemental calSup = new CalendarSupplemental(calendarId, version, calDate, releaseDateTime);
            calSup.setModifiedDate(rs.getTimestamp("modified_date_time"));
            calSup.setPublishDate(rs.getTimestamp("published_date_time"));
            return calSup;
        }
    }



    /** --- Param Source Methods --- */

    protected static MapSqlParameterSource getCalendarParams(Calendar calendar, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(calendar.getId(), params);
        addModPubDateParams(calendar.getModifiedDate(), calendar.getPublishDate(), params);
        addSobiFragmentParams(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalSupplementalParams(CalendarSupplemental sup, SobiFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addCalendarIdParams(sup.getCalendarId(), params);
        params.addValue("supVersion", sup.getVersion());
        params.addValue("calendarDate", sup.getCalDate());
        params.addValue("releaseDateTime", sup.getReleaseDateTime());
        addModPubDateParams(sup.getModifiedDate(), sup.getPublishDate(), params);
        addSobiFragmentParams(fragment, params);
        return params;
    }

    protected static MapSqlParameterSource getCalSupEntryParams(CalendarSupplementalEntry entry) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sectionCode", entry.getSectionType().getCode());
        params.addValue("billCalNo", entry.getBillCalNo());
        addBillIdParams(entry.getBillId(), params);
        BillId subBillId = entry.getSubBillId();
        params.addValue("subPrintNo", (subBillId != null) ? subBillId.getPrintNo() : null);
        params.addValue("subSession", (subBillId != null) ? subBillId.getSession() : null);
        params.addValue("subAmendVersion", (subBillId != null) ? subBillId.getVersion() : null);
        params.addValue("high", entry.getBillHigh());
        return params;
    }

    protected static void addCalendarIdParams(CalendarId calendarId, MapSqlParameterSource params) {
        params.addValue("calendarNo", calendarId.getCalNo());
        params.addValue("year", calendarId.getYear());
    }

    protected static void addBillIdParams(BillId billId, MapSqlParameterSource params) {
        params.addValue("printNo", billId.getPrintNo());
        params.addValue("session", billId.getSession());
        params.addValue("amendVersion", billId.getVersion());
    }
}
