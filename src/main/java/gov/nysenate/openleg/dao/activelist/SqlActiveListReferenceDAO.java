package gov.nysenate.openleg.dao.activelist;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.spotcheck.ActiveListSpotcheckReference;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kyle on 11/21/14.
 */
@Repository
public class SqlActiveListReferenceDAO extends SqlBaseDao implements ActiveListReferenceDAO {

    @Override
    public void addCalendarReference(ActiveListSpotcheckReference act) {
        MapSqlParameterSource params = getActiveListParams(act);
        KeyHolder key = new GeneratedKeyHolder();

        if (jdbcNamed.update(SqlActiveListReferenceQuery.UPDATE_ACTIVE_LIST.getSql(schema()), params, key,new String[] { "id" }) == 0){
            jdbcNamed.update(SqlActiveListReferenceQuery.INSERT_ACTIVE_LIST_REFERENCE.getSql(schema()), params, key,new String[] { "id" });
        }
        // use for adding new entries
        int alId = key.getKey().intValue();
        MapSqlParameterSource paramId = new MapSqlParameterSource();
        paramId.addValue("active_list_reference_id", alId);
        jdbcNamed.update(SqlActiveListReferenceQuery.DELETE_REFERENCE_ENTRIES.getSql(schema()), paramId);
        act.getEntries().forEach(entry -> addActiveListEntry(alId, entry));
        act.getEntries().stream()
                .map(CalendarEntry::getBillId)
                .collect(Collectors.toList());
    }

    void addActiveListEntry(int keyId, CalendarEntry entry){
        MapSqlParameterSource params = getEntryParams(keyId, entry);
        jdbcNamed.update(SqlActiveListReferenceQuery.INSERT_ACTIVE_LIST_REFERENCE_ENTRY.getSql(schema()), params);
    }

    @Override
    public ActiveListSpotcheckReference getCalendarReference(CalendarActiveListId cal, LocalDateTime time) {
        MapSqlParameterSource params = getActiveListIdParams(cal, time);
        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_ACTIVE_LIST.getSql(schema()), params, new ActiveRowMapper());
    }

    @Override
    public ActiveListSpotcheckReference getMostRecentReference(CalendarActiveListId cal) {
        MapSqlParameterSource params =getActiveListIdParams(cal);
        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_MOST_RECENT_REPORT.getSql(schema()), params, new ActiveRowMapper());
    }

    @Override
    public List<ActiveListSpotcheckReference> getMostRecentEachYear(int year) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("calendar_year", year);
        return jdbcNamed.query(SqlActiveListReferenceQuery.SELECT_MOST_RECENT_FROM_EACH_YEAR.getSql(schema()), params, new ActiveRowMapper());
    }

    //todo
    @Override
    public ActiveListSpotcheckReference getCurrentCalendar(CalendarActiveListId cal, Range<LocalDateTime> dateRange) throws DataAccessException {
        MapSqlParameterSource params= null;// = getActiveListIdParams(cal, dateRange);

        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_ACTIVE_LIST.getSql(schema()), params, new ActiveRowMapper());
    }

    List<CalendarEntry> getEntries(CalendarActiveListId cal, CalendarEntry entry){
        MapSqlParameterSource params = getEntryParams(cal, entry);
        return jdbcNamed.query(SqlActiveListReferenceQuery.SELECT_ACTIVE_LIST_REFERENCE_ENTRIES.getSql(schema()), params, new EntryRowMapper());
    }
    public MapSqlParameterSource getEntryParams(int keyId, CalendarEntry entry){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", entry.getBillId().getPrintNo());
        params.addValue("bill_session_no", entry.getBillId().getSession().getYear());
        params.addValue("bill_calendar_no", entry.getBillCalNo());
        params.addValue("active_list_reference_id", keyId);
        params.addValue("bill_amend_version", entry.getBillId().getVersion().getValue());
        params.addValue("bill_session_year", entry.getBillId().getSession().getYear());
        params.addValue("created_date_time", entry.getBillId().getSession());

        return params;
    }
    public MapSqlParameterSource getEntryParams(CalendarActiveListId cal, CalendarEntry entry){
        MapSqlParameterSource params = getActiveListIdParams(cal);
        params.addValue("bill_print_no", entry.getBillId().getPrintNo());
        params.addValue("bill_session_no", entry.getBillId().getSession().getYear());
        params.addValue("bill_calendar_no", entry.getBillCalNo());
        return params;
    }

     //todo create get range parameter method for use in getcurrentcalendar method

    public MapSqlParameterSource getActiveListIdParams(CalendarActiveListId cal, LocalDateTime time){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sequence_no", cal.getSequenceNo());
        params.addValue("calendar_year", cal.getYear());
        params.addValue("calendar_no", cal.getCalNo());
        params.addValue("reference_date", DateUtils.toDate(time));

        return params;
    }

    public MapSqlParameterSource getActiveListIdParams(CalendarActiveListId cal){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sequence_no", cal.getSequenceNo());
        params.addValue("calendar_year", cal.getYear());
        params.addValue("calendar_no", cal.getCalNo());

        return params;
    }

    public MapSqlParameterSource getActiveListParams( ActiveListSpotcheckReference activeList){
        MapSqlParameterSource params = getActiveListIdParams(new CalendarActiveListId(activeList.getCalendarId(), activeList.getSequenceNo()));
        params.addValue("calendar_date", DateUtils.toDate(activeList.getCalDate()));
        params.addValue("release_date_time", DateUtils.toDate(activeList.getReleaseDateTime()));
        params.addValue("reference_date", DateUtils.toDate(activeList.getReferenceDate()));
        return params;
    }

    private class ActiveRowMapper implements RowMapper<ActiveListSpotcheckReference>
    {
        @Override
        public ActiveListSpotcheckReference mapRow(ResultSet rs, int rowNum) throws SQLException {
            ActiveListSpotcheckReference activeList = new ActiveListSpotcheckReference();
            activeList.setCalDate(DateUtils.getLocalDate(rs.getTimestamp("calendar_date")));
            activeList.setCalendarId(new CalendarId(rs.getInt("calendar_year"), rs.getInt("calendar_no")));
            activeList.setReleaseDateTime(DateUtils.getLocalDateTime(rs.getTimestamp("release_date_time")));
            activeList.setReferenceDate(DateUtils.getLocalDateTime(rs.getTimestamp("reference_date")));
            activeList.setSequenceNo(rs.getInt("sequence_no"));
            //activeList.setEntries();

            return activeList;
        }
    }
    private class EntryRowMapper implements RowMapper<CalendarEntry>
    {
        @Override
        public CalendarEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
            CalendarEntry entry = new CalendarEntry();
            entry.setBillId(new BillId(rs.getString("bill_print_no"), rs.getInt("bill_session_year")));
            entry.setBillCalNo(rs.getInt("bill_calendar_no"));
            return entry;
        }
    }
}














