package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.updates.UpdateContentType;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;

import static gov.nysenate.openleg.dao.calendar.data.SqlCalendarUpdatesQuery.*;

@Repository
public class SqlCalendarUpdatesDao extends SqlBaseDao implements CalendarUpdatesDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarUpdatesDao.class);

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateToken<CalendarId>> getUpdates(
        UpdateType updateType, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder, LimitOffset limitOffset) {

        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        String queryString = getSqlQuery(false, false, updateType, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateToken<CalendarId>> rowHandler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateTokenRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<CalendarId>> getDetailedUpdates(
        UpdateType updateType, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder, LimitOffset limitOffset) {

        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        String queryString = getSqlQuery(true, false, updateType, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateDigest<CalendarId>> rowHandler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateDigestRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<CalendarId>> getDetailedUpdatesForCalendar(
        UpdateType updateType, CalendarId calendarId, Range<LocalDateTime> dateTimeRange, SortOrder dateOrder,
        LimitOffset limitOffset) {

        MapSqlParameterSource params = getCalendarIdParams(calendarId, dateTimeRange);
        String queryString = getSqlQuery(true, true, updateType, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateDigest<CalendarId>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateDigestRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** --- Internal --- */

    /**
     * Generates the appropriate sql query based on the args, to remove code duplication.
     */
    private String getSqlQuery(boolean detail, boolean specificCalendar, UpdateType updateType, SortOrder sortOrder,
                               LimitOffset limOff) {
        String dateColumn = getDateColumnForUpdateType(updateType);
        OrderBy orderBy = getOrderByForUpdateType(updateType, sortOrder);
        String sqlQuery;
        if (specificCalendar) {
            sqlQuery = SELECT_UPDATE_DIGESTS_FOR_SPECIFIC_CALENDAR.getSql(schema(), orderBy, limOff);
        }
        else {
            sqlQuery = (detail) ? SELECT_CALENDAR_UPDATE_DIGESTS.getSql(schema(), orderBy, limOff)
                                : SELECT_CALENDAR_UPDATE_TOKENS.getSql(schema(), orderBy, limOff);
        }
        sqlQuery = queryReplace(sqlQuery, "dateColumn", dateColumn);
        return sqlQuery;
    }

    private static RowMapper<UpdateToken<CalendarId>> calendarUpdateTokenRowMapper = (rs, rowNum) ->
        new UpdateToken<>(
            new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")), UpdateContentType.CALENDAR,
            rs.getString("last_fragment_id"), getLocalDateTimeFromRs(rs, "last_published_date_time"),
            getLocalDateTimeFromRs(rs, "last_processed_date_time")
        );

    private static RowMapper<UpdateDigest<CalendarId>> calendarUpdateDigestRowMapper = (rs, rowNum) -> {

        UpdateDigest<CalendarId> digest = new UpdateDigest<>(calendarUpdateTokenRowMapper.mapRow(rs, rowNum));
        Map<String, String> data = getHstoreMap(rs, "data");
        digest.setFields(data);
        digest.setAction(rs.getString("action"));
        digest.setTable(rs.getString("table_name"));
        return digest;
    };

    private MapSqlParameterSource getCalendarIdParams(CalendarId calendarId, Range<LocalDateTime> dateTimeRange) {
        return getDateTimeRangeParams(dateTimeRange)
            .addValue("calendarNo", calendarId.getCalNo())
            .addValue("calendarYear", calendarId.getYear());
    }
}
