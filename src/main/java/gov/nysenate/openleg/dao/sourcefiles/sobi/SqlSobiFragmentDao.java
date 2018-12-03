package gov.nysenate.openleg.dao.sourcefiles.sobi;

import com.google.common.collect.ImmutableSet;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.OrderBy;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.dao.sourcefiles.SourceFileRefDao;
import gov.nysenate.openleg.model.sourcefiles.SourceFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFile;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
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
public class SqlSobiFragmentDao extends SqlBaseDao implements SobiFragmentDao {

    @Autowired private SourceFileRefDao sourceFileRefDao;

    @Override
    public List<SobiFragment> getSobiFragments(SobiFile sobiFile, SobiFragmentType fragmentType,
                                               SortOrder pubDateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFileName", sobiFile.getFileName());
        params.addValue("fragmentType", fragmentType.name());
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(
                SqlSobiFragmentQuery.GET_SOBI_FRAGMENTS_BY_SOBI_FILE_AND_TYPE.getSql(schema(), orderBy,
                        LimitOffset.ALL),
                params, new SobiFragmentRowMapper(sobiFile));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SobiFragment> getPendingSobiFragments(SortOrder pubDateOrder, LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(SqlSobiFragmentQuery.GET_PENDING_SOBI_FRAGMENTS.getSql(schema(), orderBy, limOff),
                new SobiFragmentRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SobiFragment> getPendingSobiFragments(ImmutableSet<SobiFragmentType> restrict,
                                                      SortOrder pubDateOrder,
                                                      LimitOffset limOff) {
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentTypes",
                restrict.stream().map(Enum::name).collect(Collectors.toSet()));
        return jdbcNamed.query(SqlSobiFragmentQuery.GET_PENDING_SOBI_FRAGMENTS_BY_TYPE.getSql(schema(), orderBy, limOff),
                params, new SobiFragmentRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateSobiFragment(SobiFragment fragment) {
        MapSqlParameterSource params = getSobiFragmentParams(fragment);
        if (jdbcNamed.update(SqlSobiFragmentQuery.UPDATE_SOBI_FRAGMENT.getSql(schema()), params) == 0) {
            jdbcNamed.update(SqlSobiFragmentQuery.INSERT_SOBI_FRAGMENT.getSql(schema()), params);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setPendProcessingFalse(List<SobiFragment> fragments) {
        fragments.forEach(f -> {
            f.setPendingProcessing(false);
            updateSobiFragment(f);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SobiFragment getSobiFragment(String fragmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fragmentId", fragmentId);
        return jdbcNamed.queryForObject(SqlSobiFragmentQuery.GET_SOBI_FRAGMENT_BY_FILE_NAME.getSql(schema()), params,
                new SobiFragmentRowMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SobiFragment> getSobiFragments(SourceFile sobiFile, SortOrder pubDateOrder) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName",
                sobiFile.getFileName());
        OrderBy orderBy = new OrderBy("published_date_time", pubDateOrder, "sequence_no", pubDateOrder);
        return jdbcNamed.query(SqlSobiFragmentQuery.GET_SOBI_FRAGMENTS_BY_SOBI_FILE.getSql(schema(), orderBy, LimitOffset.ALL),
                params, new SobiFragmentRowMapper(sobiFile));
    }

    /**
     * Returns a MapSqlParameterSource with columns mapped to SobiFragment values.
     */
    private MapSqlParameterSource getSobiFragmentParams(SobiFragment fragment) {
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
     * Maps rows from the sobi fragment table to SobiFragment objects.
     */
    protected class SobiFragmentRowMapper implements RowMapper<SobiFragment> {
        private String pfx = "";
        private Map<String, SourceFile> sobiFileMap = new HashMap<>();

        public SobiFragmentRowMapper() {
            this("", Collections.emptyList());
        }

        public SobiFragmentRowMapper(SourceFile sobiFile) {
            this("", Arrays.asList(sobiFile));
        }

        public SobiFragmentRowMapper(String pfx, List<SourceFile> sobiFiles) {
            this.pfx = pfx;
            for (SourceFile sobiFile : sobiFiles) {
                sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }

        @Override
        public SobiFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sobiFileName = rs.getString(pfx + "sobi_file_name");
            // Passing the sobi file objects in the constructor is a means of caching the objects
            // so that they don't have to be re-mapped. If not supplied, an extra call will be
            // made to fetch the sobi file.
            SourceFile sourceFile = sobiFileMap.get(sobiFileName);
            if (sourceFile == null) {
                sourceFile = sourceFileRefDao.getSourceFile(sobiFileName);
            }
            SobiFragmentType type = SobiFragmentType.valueOf(rs.getString(pfx + "fragment_type").toUpperCase());
            int sequenceNo = rs.getInt(pfx + "sequence_no");
            String text = rs.getString(pfx + "text");

            SobiFragment fragment = new SobiFragment(sourceFile, type, text, sequenceNo);
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