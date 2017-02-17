package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.MismatchStatusService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
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
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId id) throws DataAccessException {
        return null; // TODO: WIP
//        ImmutableParams reportIdParams = ImmutableParams.from(new MapSqlParameterSource()
//                .addValue("referenceType", id.getReferenceType().name())
//                .addValue("reportDateTime", toDate(id.getReportDateTime())));
//        // Check for the report record or throw a DataAccessException if not present
//        SpotCheckReport<ContentKey> report =
//                jdbcNamed.queryForObject(SELECT_REPORT.getSql(schema()), reportIdParams, (rs, row) ->
//                        new SpotCheckReport<>(
//                                new SpotCheckReportId(SpotCheckRefType.valueOf(rs.getString("reference_type")),
//                                        getLocalDateTimeFromRs(rs, "reference_date_time"),
//                                        getLocalDateTimeFromRs(rs, "report_date_time")),
//                                rs.getString("notes")
//                        )
//                );
//        // Obtain all the current and prior observations/mismatches
//        ReportObservationsHandler handler = new ReportObservationsHandler();
//        jdbcNamed.query(SELECT_OBS_MISMATCHES_BY_REPORT.getSql(schema()), reportIdParams, handler);
//        report.setObservations(handler.getObsMap());
//        return report;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType refType, LocalDateTime start, LocalDateTime end,
                                                           SortOrder dateOrder) {
        return null; // TODO: WIP
//        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
//                .addValue("startDateTime", toDate(start))
//                .addValue("endDateTime", toDate(end))
//                .addValue("getAllRefTypes", refType == null)
//                .addValue("referenceType", refType != null ? refType.toString() : ""));
//        ReportSummaryHandler handler = new ReportSummaryHandler(dateOrder);
//        jdbcNamed.query(SELECT_REPORT_SUMMARIES.getSql(schema()), params, handler);
//        return handler.getSummaries();
    }

    /**
     * {@inheritDoc}
     */
    // TODO this is wrong
    @Override
    public PaginatedList<DeNormSpotCheckMismatch> getOpenMismatches(SpotCheckDataSource dataSource,
                                                                    LocalDateTime dateTime,
                                                                    LimitOffset limitOffset) {
        // TODO Remove
        return null;
    }

    /**
     * Gets the most recent version of all mismatches at the given date time.
     */
    public List<DeNormSpotCheckMismatch> getUpdatableMismatches(MismatchQuery query) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("datasource", query.getDataSource().name())
                .addValue("contentTypes", query.getContentTypes().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("statuses", query.getMismatchStatuses().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("ignoreStatuses", query.getIgnoredStatuses().stream().map(Enum::name).collect(Collectors.toSet()))
                .addValue("toDate", query.getToDate())
                .addValue("fromDate", query.getFromDate());
        String sql = SqlSpotCheckReportQuery.GET_MISMATCHES.getSql(schema());
        return jdbcNamed.query(sql, params, new OpenMismatchMapper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OpenMismatchSummary getOpenMismatchSummary(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter) {
        // TODO WIP
        return null;
//        OpenMismatchQuery query = new OpenMismatchQuery(refTypes, null, observedAfter, null, null, null, false, true, false, true, true);
//        ImmutableParams params = ImmutableParams.from(getOpenObsParams(query));
//        OpenMismatchSummaryHandler handler = new OpenMismatchSummaryHandler(refTypes, observedAfter);
//        final String sqlQuery = getOpenObsMismatchesSummaryQuery(schema(), query);
//        jdbcNamed.query(sqlQuery, params, handler);
//        return handler.getSummary();
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
        MismatchQuery query = new MismatchQuery(report.getReferenceType().getDataSource(), Sets.newHashSet(report.getReferenceType().getContentType()));
        query.withToDate(report.getReferenceDateTime());
        List<DeNormSpotCheckMismatch> currentMismatches = getUpdatableMismatches(query);

        List<DeNormSpotCheckMismatch> updatedMismatches = MismatchStatusService.deriveStatuses(reportMismatches, currentMismatches);
        updatedMismatches.addAll(MismatchStatusService.deriveResolved(reportMismatches, currentMismatches, checkedKeys, checkedTypes));

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
                .addValue("mismatchType", mismatch.getMismatchType().name())
                .addValue("reportId", mismatch.getReportId())
                .addValue("datasource", mismatch.getDataSource().name())
                .addValue("contentType", mismatch.getContentType().name())
                .addValue("referenceType", mismatch.getReferenceId().getReferenceType().name())
                .addValue("mismatchStatus", mismatch.getStatus().name())
                .addValue("referenceData", mismatch.getReferenceData())
                .addValue("observedData", mismatch.getObservedData())
                .addValue("notes", mismatch.getNotes())
                .addValue("issueIds", "{" + StringUtils.join(mismatch.getIssueIds(), ',') + "}")
                .addValue("ignoreLevel", mismatch.getIgnoreStatus().name())
                .addValue("reportDateTime", mismatch.getReportDateTime())
                .addValue("observedDateTime", mismatch.getObservedDateTime())
                .addValue("referenceActiveDateTime", mismatch.getReferenceId().getRefActiveDateTime());
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
    public void deleteReport(SpotCheckReportId reportId) {
        // TODO WIP
//        ImmutableParams reportIdParams = ImmutableParams.from(new MapSqlParameterSource()
//                .addValue("referenceType", reportId.getReferenceType().name())
//                .addValue("reportDateTime", toDate(reportId.getReportDateTime())));
//        jdbcNamed.update(DELETE_REPORT.getSql(schema()), reportIdParams);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        // TODO: WIP
//        MapSqlParameterSource params = new MapSqlParameterSource()
//                .addValue("mismatchId", mismatchId)
//                .addValue("ignoreLevel", Optional.ofNullable(ignoreStatus).map(SpotCheckMismatchIgnore::getCode).orElse(null));
//        if (ignoreStatus == null || ignoreStatus == SpotCheckMismatchIgnore.NOT_IGNORED) {
//            jdbcNamed.update(DELETE_MISMATCH_IGNORE.getSql(schema()), params);
//        } else {
//            if (jdbcNamed.update(UPDATE_MISMATCH_IGNORE.getSql(schema()), params) == 0) {
//                jdbcNamed.update(INSERT_MISMATCH_IGNORE.getSql(schema()), params);
//            }
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIssueId(int mismatchId, String issueId) {
        // TODO WIP
//        SqlParameterSource params = getIssueIdParams(mismatchId, issueId);
//        jdbcNamed.update(ADD_ISSUE_ID.getSql(schema()), params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteIssueId(int mismatchId, String issueId) {
        // TODO WIP
//        SqlParameterSource params = getIssueIdParams(mismatchId, issueId);
//        jdbcNamed.update(DELETE_ISSUE_ID.getSql(schema()), params);
    }

    /**
     * --- Helper Classes ---
     */

    private class OpenMismatchMapper implements RowMapper<DeNormSpotCheckMismatch> {

        @Override
        public DeNormSpotCheckMismatch<ContentKey> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ContentKey key = getKeyFromMap(getHstoreMap(rs, "key"));
            SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("mismatch_type"));
            SpotCheckDataSource dataSource = SpotCheckDataSource.valueOf(rs.getString("datasource"));
            DeNormSpotCheckMismatch mismatch = new DeNormSpotCheckMismatch<>(key, type, dataSource);
            mismatch.setMismatchId(rs.getInt("mismatch_id"));
            mismatch.setReportId(rs.getInt("report_id"));
            mismatch.setStatus(SpotCheckMismatchStatus.valueOf(rs.getString("mismatch_status")));
            mismatch.setContentType(SpotCheckContentType.valueOf(rs.getString("content_type")));
            mismatch.setReferenceData(rs.getString("reference_data"));
            mismatch.setObservedData(rs.getString("observed_data"));
            mismatch.setReportDateTime(getLocalDateTimeFromRs(rs, "report_date_time"));
            mismatch.setObservedDateTime(getLocalDateTimeFromRs(rs, "observed_date_time"));
            mismatch.setNotes(rs.getString("notes"));
            mismatch.setIgnoreStatus(SpotCheckMismatchIgnore.valueOf(rs.getString("ignore_level")));
            mismatch.setIssueIds(Sets.newHashSet(rs.getArray("issue_ids").getArray()));

            SpotCheckRefType refType = SpotCheckRefType.valueOf(rs.getString("reference_type"));
            LocalDateTime refActiveDateTime = getLocalDateTimeFromRs(rs, "reference_active_date_time");
            mismatch.setReferenceId(new SpotCheckReferenceId(refType, refActiveDateTime));
            return mismatch;
        }
    }

    protected static final RowMapper<SpotCheckReportId> reportIdRowMapper = (rs, row) ->
            new SpotCheckReportId(SpotCheckRefType.valueOf(rs.getString("reference_type")),
                    getLocalDateTimeFromRs(rs, "reference_date_time"),
                    getLocalDateTimeFromRs(rs, "report_date_time"));

    protected class ReportSummaryHandler extends SummaryHandler {
        private Map<SpotCheckReportId, SpotCheckReportSummary> summaryMap;

        public ReportSummaryHandler(SortOrder order) {
            summaryMap = new TreeMap<>((a, b) -> a.compareTo(b) * (SortOrder.ASC.equals(order) ? 1 : -1));
        }

        @Override
        protected SpotCheckSummary getRelevantSummary(ResultSet rs) throws SQLException {
            SpotCheckReportId id = reportIdRowMapper.mapRow(rs, rs.getRow());
            if (!summaryMap.containsKey(id)) {
                summaryMap.put(id, new SpotCheckReportSummary(id, rs.getString("notes")));
            }
            return summaryMap.get(id);
        }

        public List<SpotCheckReportSummary> getSummaries() {
            return new ArrayList<>(summaryMap.values());
        }
    }

    protected class OpenMismatchSummaryHandler extends SummaryHandler {
        protected OpenMismatchSummary summary;

        public OpenMismatchSummaryHandler(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter) {
            this.summary = new OpenMismatchSummary(refTypes, observedAfter);
        }

        @Override
        protected SpotCheckSummary getRelevantSummary(ResultSet rs) throws SQLException {
            SpotCheckRefType refType = SpotCheckRefType.valueOf(rs.getString("reference_type"));
            return summary.getRefTypeSummary(refType);
        }

        public OpenMismatchSummary getSummary() {
            return summary;
        }

    }

    protected abstract class SummaryHandler implements RowCallbackHandler {
        protected abstract SpotCheckSummary getRelevantSummary(ResultSet rs) throws SQLException;

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckSummary relevantSummary = getRelevantSummary(rs);

            // Check to see if this row was null in case the query used a left join
            String typeString = rs.getString("type");
            if (typeString == null) {
                return;
            }

            SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
            SpotCheckMismatchStatus status = SpotCheckMismatchStatus.valueOf(rs.getString("status"));
            boolean tracked = rs.getBoolean("tracked");
            int count = rs.getInt("mismatch_count");
            int ignoreLevel = rs.getInt("ignore_level");
            SpotCheckMismatchIgnore ignoreStatus = rs.wasNull()
                    ? SpotCheckMismatchIgnore.NOT_IGNORED
                    : SpotCheckMismatchIgnore.getIgnoreByCode(ignoreLevel);

            relevantSummary.addMismatchTypeCount(type, status, ignoreStatus, tracked, count);
        }
    }

    protected class ReportObservationsHandler implements RowCallbackHandler {
        private Map<ContentKey, SpotCheckObservation<ContentKey>> obsMap = new HashMap<>();

        // The total number of current mismatch rows before pagination
        int mismatchTotal = 0;

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            ContentKey key = getKeyFromMap(getHstoreMap(rs, "key_arr"));
            if (!obsMap.containsKey(key) && rs.getBoolean("current")) {
                // Set the observation only if the row is 'current' i.e. not a prior mismatch
                SpotCheckObservation<ContentKey> obs = new SpotCheckObservation<>();
                obs.setKey(key);
                SpotCheckReferenceId refId = new SpotCheckReferenceId(
                        SpotCheckRefType.valueOf(rs.getString("reference_type")),
                        getLocalDateTimeFromRs(rs, "reference_active_date"));
                obs.setReferenceId(refId);
                obs.setObservedDateTime(getLocalDateTimeFromRs(rs, "observed_date_time"));
                obs.setReportDateTime(getLocalDateTimeFromRs(rs, "report_date_time"));
                obsMap.put(key, obs);
            }
            SpotCheckObservation<ContentKey> obs = obsMap.get(key);
            int mismatchId = rs.getInt("mismatch_id");
            if (!rs.wasNull() && obs != null) {
                int mismatchCount = rs.getInt("mismatch_count");
                if (mismatchCount > mismatchTotal) {
                    mismatchTotal = mismatchCount;
                }
                boolean current = rs.getBoolean("current");
                SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
                SpotCheckMismatchStatus status = SpotCheckMismatchStatus.valueOf(rs.getString("status"));
                String refData = rs.getString("reference_data");
                String obsData = rs.getString("observed_data");
                String notes = rs.getString("notes");
                SpotCheckMismatchIgnore ignoreStatus = null;
                int ignoreStatusCode = rs.getInt("ignore_level");
                if (!rs.wasNull()) {
                    ignoreStatus = SpotCheckMismatchIgnore.getIgnoreByCode(ignoreStatusCode);
                } else {
                    ignoreStatus = SpotCheckMismatchIgnore.NOT_IGNORED;
                }
                String issueId = rs.getString("issue_id");

                SpotCheckMismatch mismatch;
                // Add the current mismatch
                if (current) {
                    if (obs.getMismatches().containsKey(type)) {
                        mismatch = obs.getMismatches().get(type);
                    } else {
                        mismatch = new SpotCheckMismatch(type, obsData, refData, notes);
                        obs.addMismatch(mismatch);
                    }
                }
                // Otherwise add as a prior mismatch
                else {
                    SpotCheckReportId reportId = new SpotCheckReportId(
                            SpotCheckRefType.valueOf(rs.getString("report_reference_type")),
                            getLocalDateTimeFromRs(rs, "reference_active_date"),
                            getLocalDateTimeFromRs(rs, "report_date_time")
                    );
                    List<SpotCheckPriorMismatch> existingPriorMMs = obs.getPriorMismatches().get(type);
                    Optional<SpotCheckPriorMismatch> priorMMOpt = Optional.ofNullable(existingPriorMMs)
                            .flatMap(priorMMs -> priorMMs.stream()
                                    .filter(priorMM -> reportId.equals(priorMM.getReportId()))
                                    .findFirst()
                            );
                    SpotCheckPriorMismatch priorMismatch;
                    if (priorMMOpt.isPresent()) {
                        priorMismatch = priorMMOpt.get();
                    } else {
                        priorMismatch = new SpotCheckPriorMismatch(type, refData, obsData, notes);
                        priorMismatch.setReportId(reportId);
                        obs.addPriorMismatch(priorMismatch);
                    }
                    mismatch = priorMismatch;
                }
                // set data common to standard and prior mismatches
                mismatch.setMismatchId(mismatchId);
                mismatch.setStatus(status);
                mismatch.setIgnoreStatus(ignoreStatus);
                if (issueId != null) {
                    mismatch.addIssueId(issueId);
                }
            }
        }

        public Map<ContentKey, SpotCheckObservation<ContentKey>> getObsMap() {
            return obsMap;
        }

        public int getMismatchTotal() {
            return mismatchTotal;
        }
    }

    /**
     * --- Param Source Methods ---
     */

    private MapSqlParameterSource getReportIdParams(SpotCheckReport<ContentKey> report) {
        return new MapSqlParameterSource()
                .addValue("referenceType", report.getReferenceType().name())
                .addValue("reportDateTime", toDate(report.getReportDateTime()))
                .addValue("referenceDateTime", toDate(report.getReferenceDateTime()))
                .addValue("notes", report.getNotes());
    }

    private MapSqlParameterSource getObservationParams(ImmutableParams reportParams,
                                                       SpotCheckObservation<ContentKey> observation) {
        return new MapSqlParameterSource(reportParams.getValues())
                .addValue("obsReferenceType", observation.getReferenceId().getReferenceType().name())
                .addValue("referenceActiveDate", toDate(observation.getReferenceId().getRefActiveDateTime()))
                .addValue("observedDateTime", toDate(observation.getObservedDateTime()))
                .addValue("key", toHstoreString(getMapFromKey(observation.getKey())));
    }

    private MapSqlParameterSource getMismatchParams(int observationId, SpotCheckMismatch mismatch) {
        List<String> issueIds = Optional.ofNullable(mismatch.getIssueIds()).orElse(Collections.emptyList());
        return new MapSqlParameterSource()
                .addValue("observationId", observationId)
                .addValue("type", mismatch.getMismatchType().name())
                .addValue("status", mismatch.getStatus().name())
                .addValue("referenceData", mismatch.getReferenceData())
                .addValue("observedData", mismatch.getObservedData())
                .addValue("notes", mismatch.getNotes())
                .addValue("issueIds", issueIds, Types.ARRAY);
    }

    private MapSqlParameterSource getIssueIdParams(int mismatchId, String issueId) {
        return new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("issueId", issueId);
    }
}