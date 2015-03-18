package gov.nysenate.openleg.dao.law.data;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.law.LawDocId;
import gov.nysenate.openleg.model.law.LawVersionId;
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
import java.util.HashMap;
import java.util.Map;

import static gov.nysenate.openleg.dao.law.data.SqlLawUpdatesQuery.*;

@Repository
public class SqlLawUpdatesDao extends SqlBaseDao implements LawUpdatesDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlLawUpdatesDao.class);

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateToken<LawVersionId>> getUpdates(
        Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        String sql = getQuery(SELECT_LAW_UPDATE_TOKENS, type, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateToken<LawVersionId>> handler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", lawIdUpdateTokenMapper);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdates(
        Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        addDateTimeRangeParams(params, dateTimeRange);
        String sql = getQuery(SELECT_LAW_UPDATE_DIGESTS, type, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateDigest<LawDocId>> handler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", lawDocIdUpdateDigestMapper);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdatesForLaw(
        String lawId, Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("lawId", lawId);
        addDateTimeRangeParams(params, dateTimeRange);
        String sql = getQuery(SELECT_LAW_UPDATE_DIGESTS_FOR_LAW, type, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateDigest<LawDocId>> handler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", lawDocIdUpdateDigestMapper);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<LawDocId>> getDetailedUpdatesForDocument(
        String documentId, Range<LocalDateTime> dateTimeRange, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("documentId", documentId);
        addDateTimeRangeParams(params, dateTimeRange);
        String sql = getQuery(SELECT_LAW_UPDATE_DIGESTS_FOR_DOCUMENT, type, dateOrder, limitOffset);
        PaginatedRowHandler<UpdateDigest<LawDocId>> handler =
            new PaginatedRowHandler<>(limitOffset, "total_updated", lawDocIdUpdateDigestMapper);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /** --- Internal --- */

    private String getQuery(SqlLawUpdatesQuery query, UpdateType type, SortOrder dateOrder, LimitOffset limitOffset) {
        String dateColumn = getDateColumnForUpdateType(type);
        OrderBy orderBy = getOrderByForUpdateType(type, dateOrder);
        String sql = query.getSql(schema(), orderBy, limitOffset);
        return queryReplace(sql, "dateColumn", dateColumn);
    }

    private static final RowMapper<UpdateToken<LawVersionId>> lawIdUpdateTokenMapper = (rs, rowNum) -> {
        LocalDateTime lastPubDateTime = getLocalDateTimeFromRs(rs, "last_published_date_time");
        return new UpdateToken<>(new LawVersionId(rs.getString("law_id"), lastPubDateTime.toLocalDate()), UpdateContentType.LAW,
            rs.getString("last_source_file"), lastPubDateTime, getLocalDateTimeFromRs(rs, "last_processed_date_time"));
    };

    private static final RowMapper<UpdateToken<LawDocId>> lawDocIdUpdateTokenMapper = (rs, rowNum) -> {
        LocalDateTime lastPubDateTime = getLocalDateTimeFromRs(rs, "last_published_date_time");
        LawDocId lawDocId = new LawDocId(rs.getString("document_id"), lastPubDateTime.toLocalDate());
        return new UpdateToken<>(lawDocId, UpdateContentType.LAW, rs.getString("last_source_file"), lastPubDateTime,
            getLocalDateTimeFromRs(rs, "last_processed_date_time"));
    };

    private static final RowMapper<UpdateDigest<LawDocId>> lawDocIdUpdateDigestMapper = (rs, rowNum) -> {
        UpdateToken<LawDocId> updateToken = lawDocIdUpdateTokenMapper.mapRow(rs, rowNum);
        UpdateDigest<LawDocId> digest = new UpdateDigest<>(updateToken);
        digest.setTable(rs.getString("table_name"));
        digest.setAction(rs.getString("action"));
        return digest;
    };
}