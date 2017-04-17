package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.MismatchStatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.dao.spotcheck.SqlSpotCheckReportQuery.*;
import static gov.nysenate.openleg.util.DateUtils.toDate;

/**
 * The AbstractSpotCheckReportDao implements all the functionality required by SpotCheckReportDao
 * regardless of the content key specified. This class must be subclasses with a concrete type for
 * the ContentKey. The subclass will need to handle just the conversions for the ContentKey class.
 *
 * @param <ContentKey>
 */
public abstract class AbstractSpotCheckReportDao<ContentKey> extends SqlBaseDao
        implements SpotCheckReportDao<ContentKey> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSpotCheckReportDao.class);

    /** --- Abstract Methods --- */

    /**
     * Subclasses should implement this conversion from a Map containing certain key/val pairs to
     * an instance of ContentKey. This is needed since the keys are stored as an hstore in the
     * database.
     *
     * @param keyMap Map<String, String>
     * @return ContentKey
     */
    public abstract ContentKey getKeyFromMap(Map<String, String> keyMap);

    /**
     * Subclasses should implement a conversion from an instance of ContentKey to a Map of
     * key/val pairs that fully represent that ContentKey.
     *
     * @param key ContentKey
     * @return Map<String, String>
     */
    public abstract Map<String, String> getMapFromKey(ContentKey key);

    /** --- Implemented Methods --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public DeNormSpotCheckMismatch getMismatch(int mismatchId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId);
        String sql = SqlSpotCheckReportQuery.GET_MISMATCH.getSql(schema());
        List<DeNormSpotCheckMismatch> results = jdbcNamed.query(sql, params, new MismatchMapper());
        if (results.size() == 0) {
            throw new MismatchNotFoundEx(mismatchId);
        }
        return results.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginatedList<DeNormSpotCheckMismatch> getMismatches(MismatchQuery query, LimitOffset limitOffset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("datasource", query.getDataSource().name())
                .addValue("contentTypes", query.getContentTypes().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("statuses", query.getMismatchStatuses().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("ignoreStatuses", query.getIgnoredStatuses().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("toDate", query.getToDate())
                .addValue("fromDate", query.getFromDate())
                .addValue("mismatchtype", SpotCheckMismatchType.getName(query.getSpotCheckMismatchType()));
        String sql = SqlSpotCheckReportQuery.GET_MISMATCHES.getSql(schema(), query.getOrderBy(), limitOffset);
        PaginatedRowHandler<DeNormSpotCheckMismatch> handler = new PaginatedRowHandler<>(limitOffset, "total_rows", new MismatchMapper());
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchStatusSummary getMismatchStatusSummary(SpotCheckDataSource datasource, LocalDateTime summaryDateTime) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("datasource", datasource.name())
                .addValue("fromDate", SessionYear.of(summaryDateTime.getYear()).asDateTimeRange().lowerEndpoint())
                .addValue("toDate", summaryDateTime)
                .addValue("startOfToDate", summaryDateTime.truncatedTo(ChronoUnit.DAYS));
        String sql = SqlSpotCheckReportQuery.MISMATCH_STATUS_SUMMARY.getSql(schema());
        MismatchStatusSummaryHandler summaryHandler = new MismatchStatusSummaryHandler();
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchTypeSummary getMismatchTypeSummary(SpotCheckDataSource datasource, LocalDateTime summaryDateTime, SpotCheckMismatchStatus spotCheckMismatchStatus) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("datasource", datasource.name())
                .addValue("fromDate", SessionYear.of(summaryDateTime.getYear()).asDateTimeRange().lowerEndpoint())
                .addValue("toDate", summaryDateTime)
                .addValue("statuses", spotCheckMismatchStatus)
                .addValue("startOfToDate", summaryDateTime.truncatedTo(ChronoUnit.DAYS));
        String sql = SqlSpotCheckReportQuery.MISMATCH_TYPE_SUMMARY.getSql(schema());
        MismatchTypeSummaryHandler summaryHandler = new MismatchTypeSummaryHandler();
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchContentTypeSummary getMismatchContentTypeSummary(SpotCheckDataSource datasource, LocalDateTime summaryDate, SpotCheckMismatchStatus spotCheckMismatchStatus, SpotCheckMismatchType spotCheckMismatchType){
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("datasource", datasource.name())
                .addValue("fromDate", SessionYear.of(summaryDate.getYear()).asDateTimeRange().lowerEndpoint())
                .addValue("toDate", summaryDate)
                .addValue("statuses", spotCheckMismatchStatus)
                .addValue("mismatchtype",spotCheckMismatchType)
                .addValue("startOfToDate", summaryDate.truncatedTo(ChronoUnit.DAYS));
        String sql = SqlSpotCheckReportQuery.MISMATCH_CONTENTTYPE_SUMMARY.getSql(schema());
        MismatchContentTypeSummaryHandler summaryHandler = new MismatchContentTypeSummaryHandler();
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveReport(SpotCheckReport<ContentKey> report) {
        int reportId = insertReport(report);
        // Return early if the observations have not been set
        if (report.getObservations() == null) {
            logger.warn("The observations have not been set on this report.");
            return;
        }
        // Get the Keys and MismatchTypes checked in this report. (Used in calculating resolved mismatches)
        Set<Object> checkedKeys = report.getObservations().values().stream().map(SpotCheckObservation::getKey).collect(Collectors.toSet());
        Set<SpotCheckMismatchType> checkedTypes = report.getReferenceType().checkedMismatchTypes();

        List<DeNormSpotCheckMismatch> reportMismatches = reportToDeNormMismatches(report, reportId);
        MismatchQuery query = new MismatchQuery(report.getReferenceType().getDataSource(), Sets.newHashSet(report.getReferenceType().getContentType()))
                .withToDate(report.getReferenceDateTime())
                .withMismatchStatuses(EnumSet.allOf(SpotCheckMismatchStatus.class))
                .withIgnoredStatuses(EnumSet.allOf(SpotCheckMismatchIgnore.class));
        List<DeNormSpotCheckMismatch> currentMismatches = getMismatches(query, LimitOffset.ALL).getResults();

        List<DeNormSpotCheckMismatch> updatedMismatches = MismatchStatusService.deriveStatuses(reportMismatches, currentMismatches);
        updatedMismatches.addAll(MismatchStatusService.deriveResolved(reportMismatches, currentMismatches, checkedKeys,
                checkedTypes, report.getReportDateTime(), report.getReferenceDateTime()));

        insertMismatches(updatedMismatches);
    }

    private int insertReport(SpotCheckReport<ContentKey> report) {
        ImmutableParams reportParams = ImmutableParams.from(getReportIdParams(report));
        KeyHolder reportIdHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_REPORT.getSql(schema()), reportParams, reportIdHolder, new String[]{"id"});
        return reportIdHolder.getKey().intValue();
    }

    private void insertMismatches(List<DeNormSpotCheckMismatch> mismatches) {
        List<MapSqlParameterSource> params = mismatches.stream()
                .map(this::mismatchParams)
                .collect(Collectors.toList());
        String sql = INSERT_MISMATCH.getSql(schema());
        jdbcNamed.batchUpdate(sql, params.stream().toArray(MapSqlParameterSource[]::new));
    }

    private MapSqlParameterSource mismatchParams(DeNormSpotCheckMismatch mismatch) {
        return new MapSqlParameterSource()
                .addValue("key", toHstoreString(getMapFromKey((ContentKey) mismatch.getKey())))
                .addValue("mismatchType", mismatch.getType().name())
                .addValue("reportId", mismatch.getReportId())
                .addValue("datasource", mismatch.getDataSource().name())
                .addValue("contentType", mismatch.getContentType().name())
                .addValue("referenceType", mismatch.getReferenceId().getReferenceType().name())
                .addValue("mismatchStatus", mismatch.getStatus().name())
                .addValue("referenceData", mismatch.getReferenceData())
                .addValue("observedData", mismatch.getObservedData())
                .addValue("notes", mismatch.getNotes())
                .addValue("issueIds", toPostgresArray(mismatch.getIssueIds()))
                .addValue("ignoreLevel", mismatch.getIgnoreStatus().name())
                .addValue("reportDateTime", mismatch.getReportDateTime())
                .addValue("observedDateTime", mismatch.getObservedDateTime())
                .addValue("referenceActiveDateTime", mismatch.getReferenceId().getRefActiveDateTime());
    }

    private String toPostgresArray(Set<String> strings) {
        return "{" + StringUtils.join(strings, ',') + "}";
    }

    private List<DeNormSpotCheckMismatch> reportToDeNormMismatches(SpotCheckReport<ContentKey> report, int reportId) {
        List<DeNormSpotCheckMismatch> mismatches = new ArrayList<>();
        for (SpotCheckObservation<ContentKey> ob : report.getObservations().values()) {
            // Skip if no mismatches in the observation
            if (ob.getMismatches().size() == 0) {
                continue;
            }
            for (SpotCheckMismatch m : ob.getMismatches().values()) {
                DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch<>(ob.getKey(), m.getMismatchType(),
                        report.getReferenceType().getDataSource());
                mismatch.setReportId(reportId);
                mismatch.setContentType(report.getReferenceType().getContentType());
                mismatch.setReferenceId(ob.getReferenceId());
                mismatch.setReferenceData(m.getReferenceData());
                mismatch.setObservedData(m.getObservedData());
                mismatch.setNotes(m.getNotes());
                mismatch.setObservedDateTime(ob.getObservedDateTime());
                mismatch.setReportDateTime(report.getReportDateTime());
                if (m.getIgnoreStatus() != null)
                    mismatch.setIgnoreStatus(m.getIgnoreStatus());
                mismatch.setIssueIds(new HashSet<>(m.getIssueIds()));
                mismatches.add(mismatch);
            }
        }
        return mismatches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        if (ignoreStatus == null) {
            throw new IllegalArgumentException("Cannot set mismatch ignore status to null.");
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("ignoreStatus", ignoreStatus.name());
        String sql = SqlSpotCheckReportQuery.UPDATE_MISMATCH_IGNORE.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateIssueId(int mismatchId, String issueId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("issueId", issueId);
        String sql = SqlSpotCheckReportQuery.UPDATE_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIssueId(int mismatchId, String issueId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("issueId", issueId);
        String sql = SqlSpotCheckReportQuery.ADD_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteIssueId(int mismatchId, String issueId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("issueId", issueId);
        String sql = SqlSpotCheckReportQuery.DELETE_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllIssueId(int mismatchId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId);
        String sql = SqlSpotCheckReportQuery.DELETE_ALL_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /**
     * --- Helper Classes ---
     */

    private class MismatchMapper implements RowMapper<DeNormSpotCheckMismatch> {

        @Override
        public DeNormSpotCheckMismatch<ContentKey> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ContentKey key = getKeyFromMap(hstoreStringToMap(rs.getString("key")));
            SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
            SpotCheckDataSource dataSource = SpotCheckDataSource.valueOf(rs.getString("datasource"));
            DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch<>(key, type, dataSource);
            mismatch.setMismatchId(rs.getInt("mismatch_id"));
            mismatch.setReportId(rs.getInt("report_id"));
            mismatch.setStatus(SpotCheckMismatchStatus.valueOf(rs.getString("status")));
            mismatch.setContentType(SpotCheckContentType.valueOf(rs.getString("content_type")));
            mismatch.setReferenceData(rs.getString("reference_data"));
            mismatch.setObservedData(rs.getString("observed_data"));
            mismatch.setReportDateTime(getLocalDateTimeFromRs(rs, "report_date_time"));
            mismatch.setObservedDateTime(getLocalDateTimeFromRs(rs, "observed_date_time"));
            mismatch.setNotes(rs.getString("notes"));
            mismatch.setIgnoreStatus(SpotCheckMismatchIgnore.valueOf(rs.getString("ignore_status")));
            String[] issue_idss = getArrayFromPgRs(rs, "issue_ids");
            mismatch.setIssueIds(Sets.newHashSet(issue_idss));

            SpotCheckRefType refType = SpotCheckRefType.valueOf(rs.getString("reference_type"));
            LocalDateTime refActiveDateTime = getLocalDateTimeFromRs(rs, "reference_active_date_time");
            mismatch.setReferenceId(new SpotCheckReferenceId(refType, refActiveDateTime));
            return mismatch;
        }
    }

    private class MismatchStatusSummaryHandler implements RowCallbackHandler {

        private MismatchStatusSummary summary = new MismatchStatusSummary();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckMismatchStatus status = SpotCheckMismatchStatus.valueOf(rs.getString("status"));
            int count = rs.getInt("count");
            summary.addSpotCheckStatusSummary(status, count);
        }

        protected MismatchStatusSummary getSummary() {
            return summary;
        }
    }

    private class MismatchTypeSummaryHandler implements RowCallbackHandler {

        private MismatchTypeSummary summary = new MismatchTypeSummary();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckMismatchType spotCheckMismatchType = SpotCheckMismatchType.valueOf(rs.getString("type"));
            int count = rs.getInt("count");
            summary.addSpotCheckMismatchTypeCount(spotCheckMismatchType, count);
        }

        protected MismatchTypeSummary getSummary() {
            return summary;
        }
    }

    private class MismatchContentTypeSummaryHandler implements RowCallbackHandler {

        private MismatchContentTypeSummary summary = new MismatchContentTypeSummary();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckContentType spotCheckContentType = SpotCheckContentType.valueOf(rs.getString("type"));
            int count = rs.getInt("count");
            summary.addSpotCheckMismatchContentTypeCount(spotCheckContentType, count);
        }

        protected MismatchContentTypeSummary getSummary() {
            return summary;
        }
    }


    private MapSqlParameterSource getReportIdParams(SpotCheckReport<ContentKey> report) {
        return new MapSqlParameterSource()
                .addValue("referenceType", report.getReferenceType().name())
                .addValue("reportDateTime", toDate(report.getReportDateTime()))
                .addValue("referenceDateTime", toDate(report.getReferenceDateTime()))
                .addValue("notes", report.getNotes());
    }

}
