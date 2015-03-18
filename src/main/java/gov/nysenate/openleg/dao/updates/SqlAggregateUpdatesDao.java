package gov.nysenate.openleg.dao.updates;

import com.google.common.collect.Range;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.updates.*;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static gov.nysenate.openleg.dao.updates.SqlAggregateUpdatesQuery.*;

@Repository
public class SqlAggregateUpdatesDao extends SqlBaseDao implements AggregateUpdatesDao {

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateToken<Map<String, String>>> getUpdateTokens(Range<LocalDateTime> dateTimeRange,
                                                                           Set<UpdateContentType> types, UpdateType updateType,
                                                                           SortOrder order, LimitOffset limitOffset) {
        String query = buildQuery(schema(), limitOffset, order, types, UpdateReturnType.TOKEN, updateType);
        PaginatedRowHandler<UpdateToken<Map<String, String>>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", aggregateUpdateTokenRowMapper);
        jdbcNamed.query(query, getDateTimeRangeParams(dateTimeRange), rowHandler);
        return rowHandler.getList();
    }

    /** {@inheritDoc} */
    @Override
    public PaginatedList<UpdateDigest<Map<String, String>>> getUpdateDigests(Range<LocalDateTime> dateTimeRange,
                                                                             Set<UpdateContentType> types, UpdateType updateType,
                                                                             SortOrder order, LimitOffset limitOffset, boolean detail) {
        String query = buildQuery(schema(), limitOffset, order, types,
                detail ? UpdateReturnType.DETAIL_DIGEST : UpdateReturnType.DIGEST, updateType);
        PaginatedRowHandler<UpdateDigest<Map<String, String>>> rowHandler =
                new PaginatedRowHandler<>(limitOffset, "total_updated", aggregateUpdateDigestRowMapper);
        jdbcNamed.query(query, getDateTimeRangeParams(dateTimeRange), rowHandler);
        return rowHandler.getList();
    }

    protected static final RowMapper<UpdateToken<Map<String, String>>> aggregateUpdateTokenRowMapper = (rs, num) ->
            new UpdateToken<>(getHstoreMap(rs, "id"),
                    UpdateContentType.getValue(rs.getString("content_type")),
                    rs.getString("last_source_id"),
                    getLocalDateTimeFromRs(rs, "last_published_date_time"),
                    getLocalDateTimeFromRs(rs, "last_processed_date_time"));


    protected static final RowMapper<UpdateDigest<Map<String, String>>> aggregateUpdateDigestRowMapper = (rs, num) -> {
        UpdateDigest<Map<String, String>> digest = new UpdateDigest<>(aggregateUpdateTokenRowMapper.mapRow(rs, num));
        digest.setAction(rs.getString("action"));
        digest.setTable(rs.getString("table_name"));
        try {
            digest.setFields(getHstoreMap(rs, "data"));
        } catch (SQLException ignored) {}
        return digest;
    };
}
