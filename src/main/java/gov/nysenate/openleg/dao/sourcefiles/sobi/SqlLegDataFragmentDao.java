package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileFsDao;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRowMapper;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.model.sourcefiles.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlLegDataFragmentDao extends SqlBaseDao implements LegDataFragmentDao {

    @Autowired private List<SourceFileFsDao> sourceFileFsDaos;
    private ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap;

    @PostConstruct
    protected void init() {
        sourceFileDaoMap = Maps.uniqueIndex(sourceFileFsDaos, SourceFileFsDao::getSourceType);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getPendingLegDataFragments(SortOrder pubDateOrder, LimitOffset limOff) {
        OrderBy orderBy = fragmentOrderBy(pubDateOrder);
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_PENDING_LEG_DATA_FRAGMENTS.getSql(schema(), orderBy, limOff),
                new LegDataFragmentRowMapper(sourceFileDaoMap));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getPendingLegDataFragments(ImmutableSet<LegDataFragmentType> restrict,
                                                            SortOrder pubDateOrder,
                                                            LimitOffset limOff) {
        OrderBy orderBy = fragmentOrderBy(pubDateOrder);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentTypes",
                restrict.stream().map(Enum::name).collect(Collectors.toSet()));
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_PENDING_LEG_DATA_FRAGMENTS_BY_TYPE.getSql(schema(), orderBy, limOff),
                params, new LegDataFragmentRowMapper(sourceFileDaoMap));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateLegDataFragment(LegDataFragment fragment) {
        MapSqlParameterSource params = getLegDataFragmentParams(fragment);
        if (jdbcNamed.update(SqlLegDataFragmentQuery.UPDATE_LEG_DATA_FRAGMENT.getSql(schema()), params) == 0) {
            jdbcNamed.update(SqlLegDataFragmentQuery.INSERT_LEG_DATA_FRAGMENT.getSql(schema()), params);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendProcessingFalse(List<LegDataFragment> fragments) {
        fragments.forEach(f -> {
            f.setPendingProcessing(false);
            updateLegDataFragment(f);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LegDataFragment getLegDataFragment(String fragmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragmentId);
        return jdbcNamed.queryForObject(SqlLegDataFragmentQuery.GET_LEG_DATA_FRAGMENT_BY_FRAGMENT_ID.getSql(schema()), params,
                new LegDataFragmentRowMapper(sourceFileDaoMap));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getLegDataFragments(String sourceFileName, SortOrder pubDateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource("legDataFileName", sourceFileName);
        OrderBy orderBy = fragmentOrderBy(pubDateOrder);
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_LEG_DATA_FRAGMENTS_BY_SOURCE_FILE_NAME.getSql(schema(), orderBy, LimitOffset.ALL),
                params, new LegDataFragmentRowMapper(sourceFileDaoMap));
    }

    private OrderBy fragmentOrderBy(SortOrder sortOrder) {
        return new OrderBy(SqlTable.LEG_DATA_FRAGMENT + ".published_date_time", sortOrder,
                SqlTable.LEG_DATA_FRAGMENT + ".sequence_no", sortOrder);
    }
    /**
     * Returns a MapSqlParameterSource with columns mapped to LegDataFragment values.
     */
    private MapSqlParameterSource getLegDataFragmentParams(LegDataFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragment.getFragmentId());
        params.addValue("legDataFileName", fragment.getParentLegDataFile().getFileName());
        params.addValue("publishedDateTime", toDate(fragment.getPublishedDateTime()));
        params.addValue("fragmentType", fragment.getType().name());
        params.addValue("sequenceNo", fragment.getSequenceNo());
        // Replace all null characters with empty string.
        params.addValue("text", fragment.getText().replace('\0', ' '));
        params.addValue("processedCount", fragment.getProcessedCount());
        params.addValue("processedDateTime", toDate(fragment.getProcessedDateTime()));
        params.addValue("pendingProcessing", fragment.isPendingProcessing());
        params.addValue("manualFix", fragment.isManualFix());
        params.addValue("manualFixNotes", fragment.getManualFixNotes());
        params.addValue("processStartDateTime", fragment.getProcessStartDateTime());
        return params;
    }

    /**
     * Maps rows from the sobi fragment table to LegDataFragment objects.
     */
    protected static class LegDataFragmentRowMapper implements RowMapper<LegDataFragment> {

        SourceFileRowMapper sourceFileRowMapper;

        public LegDataFragmentRowMapper(ImmutableMap<SourceType, SourceFileFsDao> sourceFileDaoMap) {
            sourceFileRowMapper = new SourceFileRowMapper(sourceFileDaoMap);
        }

        @Override
        public LegDataFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            SourceFile sourceFile = sourceFileRowMapper.mapRow(rs, rowNum);

            LegDataFragmentType type = LegDataFragmentType.valueOf(rs.getString("fragment_type").toUpperCase());
            int sequenceNo = rs.getInt("sequence_no");
            String text = rs.getString("text");

            LegDataFragment fragment = new LegDataFragment(sourceFile, type, text, sequenceNo);
            fragment.setStagedDateTime(getLocalDateTimeFromRs(rs, "staged_date_time"));
            fragment.setPendingProcessing(rs.getBoolean("pending_processing"));
            fragment.setProcessedCount(rs.getInt("processed_count"));
            fragment.setProcessedDateTime(getLocalDateTimeFromRs(rs, "processed_date_time"));
            fragment.setManualFix(rs.getBoolean("manual_fix"));
            fragment.setManualFixNotes(rs.getString("manual_fix_notes"));
            fragment.setProcessStartDateTime(getLocalDateTimeFromRs(rs, "process_start_date_time"));
            return fragment;
        }
    }
}