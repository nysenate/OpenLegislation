package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
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
        OrderBy orderBy = new OrderBy("id", order);
        String sql = SqlQueryUtils.addOrderAndLimitOffset(SELECT_PUBLIC_HEARING_IDS, orderBy, limOff);
        return jdbcNamed.query(sql, ID_ROW_MAPPER);
    }

    /** {@inheritDoc} */
    @Override
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws EmptyResultDataAccessException {
        var params = new MapSqlParameterSource("id", publicHearingId.getId());
        PublicHearing publicHearing = jdbcNamed.queryForObject(
                SELECT_PUBLIC_HEARING_BY_ID, params, HEARING_ROW_MAPPER);
        if (publicHearing != null)
            publicHearing.setHosts(hearingHostDao.getHearingHosts(publicHearingId));
        return publicHearing;
    }

    /** {@inheritDoc} */
    @Override
    public void updatePublicHearing(PublicHearing publicHearing, boolean isManualFix) {
        // TODO: just have SQL for this.
        boolean noHearings = getPublicHearingIds(SortOrder.NONE, LimitOffset.ALL).isEmpty();
        if (noHearings)
            jdbc.execute(RESET_ID);
        MapSqlParameterSource params = getPublicHearingParams(publicHearing);
        if (jdbcNamed.update(UPDATE_PUBLIC_HEARING, params) == 0)
            jdbcNamed.update(INSERT_PUBLIC_HEARING, params);
        var hearingId = new PublicHearingId(jdbcNamed.queryForObject(SELECT_HEARING_ID_BY_FILENAME,
                new MapSqlParameterSource("filename", publicHearing.getFilename()), Integer.class));
        if (isManualFix)
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
        String sql = SqlQueryUtils.addOrderAndLimitOffset(SELECT_PUBLIC_HEARING_UPDATES, orderBy, limOff);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** --- Param Source Methods --- */

    private MapSqlParameterSource getPublicHearingParams(PublicHearing publicHearing) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        // Matches the filename regardless of file type.
        params.addValue("filenameStart", publicHearing.getFilename().split("[.]")[0]);
        params.addValue("filename", publicHearing.getFilename());
        params.addValue("date", toDate(publicHearing.getDate()));
        params.addValue("title", publicHearing.getTitle());
        params.addValue("address", publicHearing.getAddress());
        params.addValue("text", publicHearing.getText());
        params.addValue("startTime", toTime(publicHearing.getStartTime()));
        params.addValue("endTime", toTime(publicHearing.getEndTime()));
        addModPubDateParams(publicHearing.getModifiedDateTime(), publicHearing.getPublishedDateTime(), params);
        return params;
    }

    /** --- Row Mapper Instances --- */

    private static final RowMapper<PublicHearing> HEARING_ROW_MAPPER = (rs, rowNum) -> {
        PublicHearing publicHearing = new PublicHearing(rs.getString("filename"), rs.getString("text"), rs.getString("title"), rs.getString("address"), getLocalDateFromRs(rs, "date"),
                getLocalTimeFromRs(rs, "start_time"), getLocalTimeFromRs(rs, "end_time"));
        publicHearing.setId(new PublicHearingId(rs.getInt("id")));
        publicHearing.setModifiedDateTime(getLocalDateTimeFromRs(rs, "modified_date_time"));
        publicHearing.setPublishedDateTime(getLocalDateTimeFromRs(rs, "published_date_time"));
        return publicHearing;
    };

    private static final RowMapper<PublicHearingId> ID_ROW_MAPPER = (rs, rowNum) ->
            new PublicHearingId(rs.getInt("id"));

    private static final RowMapper<PublicHearingUpdateToken> TOKEN_ROW_MAPPER = (rs, rowNum) ->
        new PublicHearingUpdateToken(new PublicHearingId(rs.getInt("id")),
                getLocalDateTimeFromRs(rs, "modified_date_time"));

}
