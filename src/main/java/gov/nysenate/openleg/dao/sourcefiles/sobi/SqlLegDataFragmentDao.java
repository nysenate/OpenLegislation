package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.util.DateUtils.toDate;

@Repository
public class SqlLegDataFragmentDao extends SqlBaseDao implements LegDataFragmentDao {

    @Autowired private SourceFileRefDao sourceFileRefDao;

    @Override
    public List<LegDataFragment> getLegDataFragments(SobiFile sobiFile, LegDataFragmentType fragmentType,
                                                     SortOrder pubDateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFileName", sobiFile.getFileName());
        params.addValue("fragmentType", fragmentType.name());
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(
                SqlLegDataFragmentQuery.GET_LEG_DATA_FRAGMENTS_BY_LEG_DATA_FILE_AND_TYPE.getSql(schema(), orderBy,
                        LimitOffset.ALL),
                params, new LegDataFragmentRowMapper(sobiFile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getPendingLegDataFragments(SortOrder pubDateOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_PENDING_LEG_DATA_FRAGMENTS.getSql(schema(), orderBy, limOff),
                new LegDataFragmentRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getPendingLegDataFragments(ImmutableSet<LegDataFragmentType> restrict,
                                                            SortOrder pubDateOrder,
                                                            LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentTypes",
                restrict.stream().map(Enum::name).collect(Collectors.toSet()));
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_PENDING_LEG_DATA_FRAGMENTS_BY_TYPE.getSql(schema(), orderBy, limOff),
                params, new LegDataFragmentRowMapper());
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
        return jdbcNamed.queryForObject(SqlLegDataFragmentQuery.GET_LEG_DATA_FRAGMENT_BY_FILE_NAME.getSql(schema()), params,
                new LegDataFragmentRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<LegDataFragment> getLegDataFragments(SourceFile sobiFile, SortOrder pubDateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName",
                sobiFile.getFileName());
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(SqlLegDataFragmentQuery.GET_LEG_DATA_FRAGMENTS_BY_LEG_DATA_FILE.getSql(schema(), orderBy, LimitOffset.ALL),
                params, new LegDataFragmentRowMapper(sobiFile));
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to LegDataFragment values.
     */
    private MapSqlParameterSource getLegDataFragmentParams(LegDataFragment fragment) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragment.getFragmentId());
        params.addValue("sobiFileName", fragment.getParentSobiFile().getFileName());
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
    protected class LegDataFragmentRowMapper implements RowMapper<LegDataFragment> {
        private String pfx = "";
        private Map<String, SourceFile> sobiFileMap = new HashMap<>();

        public LegDataFragmentRowMapper() {
            this("", Collections.emptyList());
        }

        public LegDataFragmentRowMapper(SourceFile sobiFile) {
            this("", Arrays.asList(sobiFile));
        }

        public LegDataFragmentRowMapper(String pfx, List<SourceFile> sobiFiles) {
            this.pfx = pfx;
            for (SourceFile sobiFile : sobiFiles) {
                sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }

        @Override
        public LegDataFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sobiFileName = rs.getString(pfx + "sobi_file_name");
            // Passing the sobi file objects in the constructor is a means of caching the objects
            // so that they don't have to be re-mapped. If not supplied, an extra call will be
            // made to fetch the sobi file.
            SourceFile sourceFile = sobiFileMap.get(sobiFileName);
            if (sourceFile == null) {
                sourceFile = sourceFileRefDao.getSourceFile(sobiFileName);
            }
            LegDataFragmentType type = LegDataFragmentType.valueOf(rs.getString(pfx + "fragment_type").toUpperCase());
            int sequenceNo = rs.getInt(pfx + "sequence_no");
            String text = rs.getString(pfx + "text");

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