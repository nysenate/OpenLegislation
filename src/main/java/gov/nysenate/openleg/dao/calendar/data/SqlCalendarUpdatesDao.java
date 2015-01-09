package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
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
    public PaginatedList<UpdateToken<CalendarId>> calendarsUpdatedDuring(UpdateType updateType, Range<LocalDateTime> dateTimeRange,
                                                                         SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        OrderBy orderBy = getDateColumnOrderBy(updateType, dateOrder);
        final String queryString = substituteDateColumn( updateType,
                SELECT_CALENDAR_UPDATED_DURING.getSql(schema(), orderBy, limitOffset));
        PaginatedRowHandler<UpdateToken<CalendarId>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateTokenRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<CalendarId>> getUpdateDigests(UpdateType updateType, CalendarId calendarId,
                                                                    Range<LocalDateTime> dateTimeRange,
                                                                    SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = getCalendarUpdateDigestsParams(calendarId, dateTimeRange);
        OrderBy orderBy = getDateColumnOrderBy(updateType, dateOrder);
        final String queryString = substituteDateColumn(updateType,
                SELECT_UPDATE_DIGESTS_FOR_CALENDAR.getSql(schema(), orderBy, LimitOffset.ALL));
        PaginatedRowHandler<UpdateDigest<CalendarId>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateDigestRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<CalendarId>> getUpdateDigests(UpdateType updateType,
                                                                    Range<LocalDateTime> dateTimeRange,
                                                                    SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        OrderBy orderBy = getDateColumnOrderBy(updateType, dateOrder);
        final String queryString = substituteDateColumn(updateType,
                SELECT_UPDATE_DIGESTS.getSql(schema(), orderBy, LimitOffset.ALL));
        PaginatedRowHandler<UpdateDigest<CalendarId>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateDigestRowMapper);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getList();
    }

    /** --- Internal --- */

    private static RowMapper<UpdateToken<CalendarId>> calendarUpdateTokenRowMapper = (rs, rowNum) ->
        new UpdateToken<>(
            new CalendarId(rs.getInt("calendar_no"), rs.getInt("calendar_year")),
            rs.getString("fragment_id"),
            DateUtils.getLocalDateTime(rs.getTimestamp("published_date_time")),
            DateUtils.getLocalDateTime(rs.getTimestamp("action_date_time"))
        );

    private static RowMapper<UpdateDigest<CalendarId>> calendarUpdateDigestRowMapper = (rs, rowNum) -> {
        Map<String, String> key = getHstoreMap(rs, "key");
        CalendarId id = new CalendarId( Integer.parseInt(key.remove("calendar_no")),
                                        Integer.parseInt(key.remove("calendar_year")));
        UpdateDigest<CalendarId> digest = new UpdateDigest<>(calendarUpdateTokenRowMapper.mapRow(rs, rowNum));
        Map<String, String> data = getHstoreMap(rs, "data");
        data.putAll(key);

        digest.setFields(data);
        digest.setAction(rs.getString("action"));
        digest.setTable(rs.getString("table_name"));
        return digest;
    };

    /**
     * Generates an OrderBy suitable for the given UpdateType
     */
    private OrderBy getDateColumnOrderBy(UpdateType updateType, SortOrder dateOrder) {
        if (updateType == UpdateType.PROCESSED_DATE) {
            return new OrderBy("action_date_time", dateOrder, "published_date_time", dateOrder);
        }
        return new OrderBy("published_date_time", dateOrder, "action_date_time", dateOrder);
    }

    /**
     * Adds the appropriate date column to the given query based on the given update type
     */
    private String substituteDateColumn(UpdateType updateType, String queryString) {
        Map<String, String> subMap = (updateType == UpdateType.PROCESSED_DATE)
                ? ImmutableMap.of("date_column", "action_date_time")
                : ImmutableMap.of("date_column", "published_date_time");
        return StrSubstitutor.replace(queryString, subMap);
    }

    private MapSqlParameterSource getDateTimeRangeParams(Range<LocalDateTime> dateTimeRange) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        return params;
    }

    private MapSqlParameterSource getCalendarUpdateDigestsParams(CalendarId calendarId, Range<LocalDateTime> dateTimeRange) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        params.addValue("calendarNo", calendarId.getCalNo());
        params.addValue("calendarYear", calendarId.getYear());
        return params;
    }
}
