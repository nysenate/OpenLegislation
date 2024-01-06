package gov.nysenate.openleg.spotchecks.scraping.lrs.agenda;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.SqlBaseDao;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.calendar.CalendarActiveListId;
import gov.nysenate.openleg.legislation.calendar.CalendarEntry;
import gov.nysenate.openleg.legislation.calendar.CalendarId;
import gov.nysenate.openleg.spotchecks.model.ActiveListSpotcheckReference;
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
import java.util.Objects;

/**
 * Created by kyle on 11/21/14.
 */
@Repository
public class SqlActiveListReferenceDAO extends SqlBaseDao implements ActiveListReferenceDAO {
    @Override
    public void addCalendarReference(ActiveListSpotcheckReference act) {
        MapSqlParameterSource params = getActiveListParams(act);
        KeyHolder key = new GeneratedKeyHolder();

        if (jdbcNamed.update(SqlActiveListReferenceQuery.UPDATE_ACTIVE_LIST.getSql(schema()), params, key, new String[] { "id" }) == 0){
            jdbcNamed.update(SqlActiveListReferenceQuery.INSERT_ACTIVE_LIST_REFERENCE.getSql(schema()), params, key, new String[] { "id" });
        }
        // use for adding new entries
        int alId = Objects.requireNonNull(key.getKey()).intValue();
        MapSqlParameterSource paramId = new MapSqlParameterSource();
        paramId.addValue("active_list_reference_id", alId);
        jdbcNamed.update(SqlActiveListReferenceQuery.DELETE_REFERENCE_ENTRIES.getSql(schema()), paramId);
        act.getEntries().forEach(entry -> addActiveListEntry(alId, entry));
    }

    private void addActiveListEntry(int keyId, CalendarEntry entry){
        MapSqlParameterSource params = getEntryParams(keyId, entry);
        jdbcNamed.update(SqlActiveListReferenceQuery.INSERT_ACTIVE_LIST_REFERENCE_ENTRY.getSql(schema()), params);
    }

    @Override
    public ActiveListSpotcheckReference getCalendarReference(CalendarActiveListId cal, LocalDateTime time) {
        var params = new MapSqlParameterSource()
                .addValue("sequence_no", cal.getSequenceNo())
                .addValue("calendar_year", cal.getYear())
                .addValue("calendar_no", cal.getCalNo())
                .addValue("reference_date", DateUtils.toDate(time));
        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_ACTIVE_LIST.getSql(schema()), params, new ActiveRowMapper());
    }

    @Override
    public ActiveListSpotcheckReference getMostRecentReference(CalendarActiveListId cal) {
        MapSqlParameterSource params = getActiveListIdParams(cal);
        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_MOST_RECENT_REPORT.getSql(schema()), params, new ActiveRowMapper());
    }

    @Override
    public List<ActiveListSpotcheckReference> getMostRecentEachYear(int year) {
        var params = new MapSqlParameterSource("calendar_year", year);
        return jdbcNamed.query(SqlActiveListReferenceQuery.SELECT_MOST_RECENT_FROM_EACH_YEAR.getSql(schema()), params, new ActiveRowMapper());
    }

    // TODO
    @Override
    public ActiveListSpotcheckReference getCurrentCalendar(CalendarActiveListId cal, Range<LocalDateTime> dateRange) throws DataAccessException {
        MapSqlParameterSource params= null;// = getActiveListIdParams(cal, dateRange);
        return jdbcNamed.queryForObject(SqlActiveListReferenceQuery.SELECT_ACTIVE_LIST.getSql(schema()), params, new ActiveRowMapper());
    }

    private static MapSqlParameterSource getEntryParams(int keyId, CalendarEntry entry) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("bill_print_no", entry.getBillId().getPrintNo());
        params.addValue("bill_session_no", entry.getBillId().getSession().year());
        params.addValue("bill_calendar_no", entry.getBillCalNo());
        params.addValue("active_list_reference_id", keyId);
        params.addValue("bill_amend_version", entry.getBillId().getVersion().toString());
        params.addValue("bill_session_year", entry.getBillId().getSession().year());
        params.addValue("created_date_time", entry.getBillId().getSession());

        return params;
    }

    private static MapSqlParameterSource getActiveListIdParams(CalendarActiveListId cal) {
        return new MapSqlParameterSource()
                .addValue("sequence_no", cal.getSequenceNo())
                .addValue("calendar_year", cal.getYear())
                .addValue("calendar_no", cal.getCalNo());
    }

    private static MapSqlParameterSource getActiveListParams(ActiveListSpotcheckReference activeList) {
        return getActiveListIdParams(new CalendarActiveListId(activeList.getCalendarId(), activeList.getSequenceNo()))
                .addValue("calendar_date", DateUtils.toDate(activeList.getCalDate()))
                .addValue("release_date_time", DateUtils.toDate(activeList.getReleaseDateTime()))
                .addValue("reference_date", DateUtils.toDate(activeList.getReferenceDate()));
    }

    private static class ActiveRowMapper implements RowMapper<ActiveListSpotcheckReference> {
        @Override
        public ActiveListSpotcheckReference mapRow(ResultSet rs, int rowNum) throws SQLException {
            ActiveListSpotcheckReference activeList = new ActiveListSpotcheckReference();
            activeList.setCalDate(DateUtils.getLocalDate(rs.getTimestamp("calendar_date")));
            activeList.setCalendarId(new CalendarId(rs.getInt("calendar_year"), rs.getInt("calendar_no")));
            activeList.setReleaseDateTime(DateUtils.getLocalDateTime(rs.getTimestamp("release_date_time")));
            activeList.setReferenceDate(DateUtils.getLocalDateTime(rs.getTimestamp("reference_date")));
            activeList.setSequenceNo(rs.getInt("sequence_no"));
            return activeList;
        }
    }
}
