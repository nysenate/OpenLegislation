package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.dao.host.HearingHostDao;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.common.util.DateUtils.toTime;
import static gov.nysenate.openleg.legislation.transcripts.hearing.dao.SqlPublicHearingQuery.*;

@Repository
public class SqlPublicHearingDao extends SqlBaseDao implements PublicHearingDao {
    @Autowired
    private HearingHostDao hearingHostDao;

    /** {@inheritDoc} */
    @Override
    public List<PublicHearingId> getPublicHearingIds(SortOrder order, LimitOffset limOff) {
        return jdbcNamed.query(SELECT_HEARING_IDS.getSql(schema(), new OrderBy("id", order), limOff), ID_ROW_MAPPER);
    }

    /** {@inheritDoc} */
    @Override
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws EmptyResultDataAccessException {
        var params = new MapSqlParameterSource("id", publicHearingId.id());
        PublicHearing publicHearing = jdbcNamed.queryForObject(SELECT_HEARING_BY_ID.getSql(schema()), params, HEARING_ROW_MAPPER);
        if (publicHearing != null)
            publicHearing.setHosts(hearingHostDao.getHearingHosts(publicHearingId));
        return publicHearing;
    }

    /** {@inheritDoc} */
    @Override
    public PublicHearing getPublicHearing(String filename) throws EmptyResultDataAccessException {
        return getPublicHearing(getId(filename));
    }

    /** {@inheritDoc} */
    @Override
    public String getFilename(PublicHearingId publicHearingId) throws EmptyResultDataAccessException {
        var params = new MapSqlParameterSource("id", publicHearingId.id());
        return jdbcNamed.queryForObject(SELECT_FILENAME_BY_ID.getSql(schema()), params, FILENAME_ROW_MAPPER);
    }

    private PublicHearingId getId(String filename) throws EmptyResultDataAccessException {
        return new PublicHearingId(jdbcNamed.queryForObject(SELECT_ID_BY_FILENAME.getSql(schema()),
                new MapSqlParameterSource("filename", filename), Integer.class));
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearing(PublicHearing publicHearing) {
        if (jdbc.queryForList(SELECT_HEARING_IDS.getSql(schema()), Integer.class).isEmpty())
            jdbc.execute(RESET_ID.getSql(schema()));
        MapSqlParameterSource params = getPublicHearingParams(publicHearing);
        boolean isNew = jdbcNamed.update(UPDATE_HEARING.getSql(schema()), params) == 0;
        if (isNew)
            jdbcNamed.update(INSERT_HEARING.getSql(schema()), params);
        var hearingId = getId(publicHearing.getFilename());
        if (!isNew)
            hearingHostDao.deleteHearingHosts(hearingId);
        hearingHostDao.updateHearingHosts(hearingId, publicHearing.getHosts());
        publicHearing.setId(hearingId);
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<PublicHearingUpdateToken> publicHearingsUpdatedDuring(Range<LocalDateTime> dateRange,
                                                                               SortOrder dateOrder,
                                                                               LimitOffset limOff) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateRange);
        OrderBy orderBy = new OrderBy("modified_date_time", dateOrder);
        PaginatedRowHandler<PublicHearingUpdateToken> handler = new PaginatedRowHandler<>(limOff, "total_updated",
                TOKEN_ROW_MAPPER);
        String sql = SqlQueryUtils.addOrderAndLimitOffset(SELECT_HEARING_UPDATES.getSql(schema()), orderBy, limOff);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getPublicHearingParams(PublicHearing publicHearing) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Matches the filename regardless of file type.
        params.addValue("filenameRegex", publicHearing.getFilename().split("[.]")[0] + ".%");
        params.addValue("filename", publicHearing.getFilename());
        params.addValue("date", toDate(publicHearing.getDate()));
        params.addValue("title", publicHearing.getTitle());
        params.addValue("address", publicHearing.getAddress());
        params.addValue("text", publicHearing.getText());
        params.addValue("startTime", toTime(publicHearing.getStartTime()));
        params.addValue("endTime", toTime(publicHearing.getEndTime()));
        params.addValue("modifiedDateTime", toDate(LocalDateTime.now()));
        return params;
    }

    /** --- Row Mapper Instances --- */

    private static final RowMapper<PublicHearing> HEARING_ROW_MAPPER = (rs, rowNum) -> {
        PublicHearing publicHearing = new PublicHearing(rs.getString("filename"), rs.getString("text"),
                rs.getString("title"), rs.getString("address"), getLocalDateFromRs(rs, "date"),
                getLocalTimeFromRs(rs, "start_time"), getLocalTimeFromRs(rs, "end_time"));
        publicHearing.setId(new PublicHearingId(rs.getInt("id")));
        publicHearing.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        publicHearing.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return publicHearing;
    };

    private static final RowMapper<String> FILENAME_ROW_MAPPER = (rs, rowNum) ->
        rs.getString("filename");

    private static final RowMapper<PublicHearingId> ID_ROW_MAPPER = (rs, rowNum) ->
            new PublicHearingId(rs.getInt("id"));

    private static final RowMapper<PublicHearingUpdateToken> TOKEN_ROW_MAPPER = (rs, rowNum) ->
        new PublicHearingUpdateToken(new PublicHearingId(rs.getInt("id")),
                getLocalDateTimeFromRs(rs, "modified_date_time"));

}
