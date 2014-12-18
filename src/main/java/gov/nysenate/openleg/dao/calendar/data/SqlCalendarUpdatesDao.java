package gov.nysenate.openleg.dao.calendar.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateTokenDigest;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

import static gov.nysenate.openleg.dao.calendar.data.SqlCalendarUpdatesQuery.*;

@Repository
public class SqlCalendarUpdatesDao extends SqlBaseDao implements CalendarUpdatesDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlCalendarUpdatesDao.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<UpdateToken<CalendarId>> calendarsUpdatedDuring(Range<LocalDateTime> dateTimeRange,
                                                                         SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        OrderBy orderBy = new OrderBy("latest_action_date_time", dateOrder);
        PaginatedRowHandler<UpdateToken<CalendarId>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", calendarUpdateTokenRowMapper);
        jdbcNamed.query(SELECT_CALENDAR_UPDATED_DURING.getSql(schema(), orderBy, limitOffset), params, rowHandler);
        return rowHandler.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UpdateDigest<CalendarId>> getUpdateDigests(CalendarId calendarId, Range<LocalDateTime> dateTimeRange,
                                                           SortOrder dateOrder) {
        MapSqlParameterSource params = getCalendarUpdateDigestsParams(calendarId, dateTimeRange);
        OrderBy orderBy = new OrderBy("action_date_time", dateOrder);
        return jdbcNamed.query(SELECT_UPDATES_FOR_CALENDAR.getSql(schema(), orderBy, LimitOffset.ALL),
                                params, calendarUpdateDigestRowMapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<UpdateTokenDigest<CalendarId>> getUpdateTokenDigests(Range<LocalDateTime> dateTimeRange,
                                                                              SortOrder dateOrder, LimitOffset limOff) {
        MapSqlParameterSource params = getDateTimeRangeParams(dateTimeRange);
        OrderBy orderBy = new OrderBy("action_date_time", dateOrder);
        StrSubstitutor limOffSubber = new StrSubstitutor(ImmutableMap.of(
                                                            "limit", Integer.toString(limOff.getLimit()),
                                                            "offset", Integer.toString(limOff.getOffsetStart()-1),
                                                            "sort_order", dateOrder.toString()));
        final String queryString = limOffSubber.replace(
                SELECT_UPDATE_DIGESTS_DURING.getSql(schema(), orderBy, LimitOffset.ALL));
        UpdateTokenDigestRowHandler rowHandler = new UpdateTokenDigestRowHandler(limOff);
        jdbcNamed.query(queryString, params, rowHandler);
        return rowHandler.getTokenDigests();
    }

    /** --- Internal --- */

    private static RowMapper<UpdateToken<CalendarId>> calendarUpdateTokenRowMapper = (rs, rowNum) ->
            new UpdateToken<>(
                    new CalendarId(
                            rs.getInt("calendar_no"),
                            rs.getInt("calendar_year")
                    ),
                    DateUtils.getLocalDateTime(rs.getTimestamp("latest_action_date_time"))
            );

    private static RowMapper<UpdateDigest<CalendarId>> calendarUpdateDigestRowMapper = (rs, rowNum) -> {
        Map<String, String> key = getHstoreMap(rs, "key");
        CalendarId id = new CalendarId( Integer.parseInt(key.remove("calendar_no")),
                                        Integer.parseInt(key.remove("calendar_year")));
        UpdateDigest<CalendarId> digest = new UpdateDigest<>(id,
                                            DateUtils.getLocalDateTime(rs.getTimestamp("action_date_time")));
        Map<String, String> data = getHstoreMap(rs, "data");
        data.putAll(key);

        digest.setUpdates(data);
        digest.setAction(rs.getString("action"));
        digest.setTable(rs.getString("table_name"));
        String fragmentId = rs.getString("sobi_fragment_id");
        digest.setSourceDataId(fragmentId);
        digest.setSourceDataDateTime(getLocalDateTimeFromSobiFragmentId(fragmentId));
        return digest;
    };

    private static class UpdateTokenDigestRowHandler implements RowCallbackHandler {
        private LimitOffset limOff;
        private Map<UpdateToken<CalendarId>, UpdateTokenDigest<CalendarId>> tokenDigests = new LinkedHashMap<>();
        private int totalCount = 0;

        public UpdateTokenDigestRowHandler(LimitOffset limOff) {
            this.limOff = limOff;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            if (totalCount == 0) {
                totalCount = rs.getInt("total_updated");
            }
            UpdateToken<CalendarId> updateToken = calendarUpdateTokenRowMapper.mapRow(rs, rs.getRow());
            UpdateDigest<CalendarId> updateDigest = calendarUpdateDigestRowMapper.mapRow(rs, rs.getRow());
            UpdateTokenDigest<CalendarId> updateTokenDigest = tokenDigests.get(updateToken);
            if (updateTokenDigest == null) {
                updateTokenDigest = new UpdateTokenDigest<CalendarId>(updateToken);
                tokenDigests.put(updateToken, updateTokenDigest);
            }
            updateTokenDigest.addUpdateDigest(updateDigest);
        }

        public PaginatedList<UpdateTokenDigest<CalendarId>> getTokenDigests() {
            return new PaginatedList<>(totalCount, limOff, new ArrayList<>(tokenDigests.values()));
        }
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
