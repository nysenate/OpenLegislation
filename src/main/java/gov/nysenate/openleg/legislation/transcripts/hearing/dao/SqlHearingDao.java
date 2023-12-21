package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.host.HearingHostDao;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.common.util.DateUtils.toTime;
import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.SqlHearingQuery.*;

@Repository
public class SqlHearingDao extends SqlBaseDao implements HearingDao {
    private final HearingHostDao hearingHostDao;

    @Autowired
    public SqlHearingDao(HearingHostDao hearingHostDao) {
        this.hearingHostDao = hearingHostDao;
    }

    /** {@inheritDoc} */
    @Override
    public List<HearingId> getHearingIds(SortOrder order, LimitOffset limOff) {
        return jdbcNamed.query(SELECT_HEARING_IDS.getSql(schema(), new OrderBy("id", order), limOff), ID_ROW_MAPPER);
    }

    /** {@inheritDoc} */
    @Override
    public Hearing getHearing(HearingId hearingId) throws EmptyResultDataAccessException {
        var params = new MapSqlParameterSource("id", hearingId.id());
        Hearing hearing = jdbcNamed.queryForObject(SELECT_HEARING_BY_ID.getSql(schema()), params, HEARING_ROW_MAPPER);
        if (hearing != null)
            hearing.setHosts(hearingHostDao.getHearingHosts(hearingId));
        return hearing;
    }

    /** {@inheritDoc} */
    @Override
    public Hearing getHearing(String filename) throws EmptyResultDataAccessException {
        return getHearing(getId(filename));
    }

    /** {@inheritDoc} */
    @Override
    public String getFilename(HearingId hearingId) throws EmptyResultDataAccessException {
        var params = new MapSqlParameterSource("id", hearingId.id());
        return jdbcNamed.queryForObject(SELECT_FILENAME_BY_ID.getSql(schema()), params, FILENAME_ROW_MAPPER);
    }

    private HearingId getId(String filename) throws EmptyResultDataAccessException {
        return new HearingId(jdbcNamed.queryForObject(SELECT_ID_BY_FILENAME.getSql(schema()),
                new MapSqlParameterSource("filename", filename), Integer.class));
    }

    /** {@inheritDoc} */
    @Override
    public void updateHearing(Hearing hearing) {
        if (jdbc.queryForList(SELECT_HEARING_IDS.getSql(schema()), Integer.class).isEmpty())
            jdbc.execute(RESET_ID.getSql(schema()));
        MapSqlParameterSource params = getHearingParams(hearing);
        boolean isNew = jdbcNamed.update(UPDATE_HEARING.getSql(schema()), params) == 0;
        if (isNew)
            jdbcNamed.update(INSERT_HEARING.getSql(schema()), params);
        var hearingId = getId(hearing.getFilename());
        if (!isNew)
            hearingHostDao.deleteHearingHosts(hearingId);
        hearingHostDao.updateHearingHosts(hearingId, hearing.getHosts());
        hearing.setId(hearingId);
    }

    /** {@inheritDoc} */
    @Override
    public List<Hearing> getHearings(Integer year) {
        var sqlQuery = year == null ? SELECT_HEARINGS : SELECT_HEARINGS_BY_YEAR;
        return jdbcNamed.query(sqlQuery.getSql(schema()),
                new MapSqlParameterSource("year", year), HEARING_ROW_MAPPER);
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<HearingUpdateToken> hearingsUpdatedDuring(Range<LocalDateTime> dateRange,
                                                                   SortOrder dateOrder,
                                                                   LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateRange);
        OrderBy orderBy = new OrderBy("modified_date_time", dateOrder);
        PaginatedRowHandler<HearingUpdateToken> handler = new PaginatedRowHandler<>(limOff, "total_updated",
                TOKEN_ROW_MAPPER);
        String sql = SqlQueryUtils.addOrderAndLimitOffset(SELECT_HEARING_UPDATES.getSql(schema()), orderBy, limOff);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getHearingParams(Hearing hearing) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Matches the filename regardless of file type.
        params.addValue("filenameRegex", hearing.getFilename().split("[.]")[0] + ".%");
        params.addValue("filename", hearing.getFilename());
        params.addValue("date", toDate(hearing.getDate()));
        params.addValue("title", hearing.getTitle());
        params.addValue("address", hearing.getAddress());
        params.addValue("text", hearing.getText());
        params.addValue("startTime", toTime(hearing.getStartTime()));
        params.addValue("endTime", toTime(hearing.getEndTime()));
        params.addValue("modifiedDateTime", toDate(LocalDateTime.now()));
        return params;
    }

    /** --- Row Mapper Instances --- */

    private static final RowMapper<Hearing> HEARING_ROW_MAPPER = (rs, rowNum) -> {
        Hearing hearing = new Hearing(rs.getString("filename"), rs.getString("text"),
                rs.getString("title"), rs.getString("address"), getLocalDateFromRs(rs, "date"),
                getLocalTimeFromRs(rs, "start_time"), getLocalTimeFromRs(rs, "end_time"));
        hearing.setId(new HearingId(rs.getInt("id")));
        hearing.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        hearing.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return hearing;
    };

    private static final RowMapper<String> FILENAME_ROW_MAPPER = (rs, rowNum) ->
        rs.getString("filename");

    private static final RowMapper<HearingId> ID_ROW_MAPPER = (rs, rowNum) ->
            new HearingId(rs.getInt("id"));

    private static final RowMapper<HearingUpdateToken> TOKEN_ROW_MAPPER = (rs, rowNum) ->
        new HearingUpdateToken(new HearingId(rs.getInt("id")),
                getLocalDateTimeFromRs(rs, "modified_date_time"));

}
