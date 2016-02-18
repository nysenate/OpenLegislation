package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
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
                                                             implements SpotCheckReportDao<ContentKey>
{
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

    /** {@inheritDoc} */
    @Override
    public SpotCheckReport<ContentKey> getReport(SpotCheckReportId id) throws DataAccessException {
        ImmutableParams reportIdParams = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("referenceType", id.getReferenceType().name())
            .addValue("reportDateTime", toDate(id.getReportDateTime())));
        // Check for the report record or throw a DataAccessException if not present
        SpotCheckReport<ContentKey> report =
                jdbcNamed.queryForObject(SELECT_REPORT.getSql(schema()), reportIdParams, (rs, row) ->
                    new SpotCheckReport<>(
                        new SpotCheckReportId(SpotCheckRefType.valueOf(rs.getString("reference_type")),
                            getLocalDateTimeFromRs(rs, "reference_date_time"),
                            getLocalDateTimeFromRs(rs, "report_date_time")),
                        rs.getString("notes")
                    )
                );
        // Obtain all the current and prior observations/mismatches
        ReportObservationsHandler handler = new ReportObservationsHandler();
        jdbcNamed.query(SELECT_OBS_MISMATCHES_BY_REPORT.getSql(schema()), reportIdParams, handler);
        report.setObservations(handler.getObsMap());
        return report;
    }

    /** {@inheritDoc} */
    @Override
    public List<SpotCheckReportSummary> getReportSummaries(SpotCheckRefType refType, LocalDateTime start, LocalDateTime end,
                                                           SortOrder dateOrder) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("startDateTime", toDate(start))
                .addValue("endDateTime", toDate(end))
                .addValue("getAllRefTypes", refType == null)
                .addValue("referenceType", refType != null ? refType.toString() : ""));
        ReportSummaryHandler handler = new ReportSummaryHandler(dateOrder);
        jdbcNamed.query(SELECT_REPORT_SUMMARIES.getSql(schema()), params, handler);
        return handler.getSummaries();
    }

    /** {@inheritDoc} */
    @Override
    public SpotCheckOpenMismatches<ContentKey> getOpenMismatches(OpenMismatchQuery query) {
        ImmutableParams params = ImmutableParams.from(getOpenObsParams(query));
        ReportObservationsHandler handler = new ReportObservationsHandler();
        final String sqlQuery = getLatestOpenObsMismatchesQuery(schema(), query);
        jdbcNamed.query(sqlQuery, params, handler);
        return new SpotCheckOpenMismatches<>(query.getRefTypes(), handler.getObsMap(), handler.getMismatchTotal());
    }

    /** {@inheritDoc} */
    @Override
    public OpenMismatchSummary getOpenMismatchSummary(Set<SpotCheckRefType> refTypes, LocalDateTime observedAfter) {
        OpenMismatchQuery query = new OpenMismatchQuery(refTypes, null, observedAfter, null, null, null, false, true, false, true, true);
        ImmutableParams params = ImmutableParams.from(getOpenObsParams(query));
        OpenMismatchSummaryHandler handler = new OpenMismatchSummaryHandler(refTypes, observedAfter);
        final String sqlQuery = getOpenObsMismatchesSummaryQuery(schema(), query);
        jdbcNamed.query(sqlQuery, params, handler);
        return handler.getSummary();
    }

    /** {@inheritDoc} */
    @Override
    public void saveReport(SpotCheckReport<ContentKey> report) {
        if (report == null) {
            throw new IllegalArgumentException("Supplied report cannot be null.");
        }
        ImmutableParams reportParams = ImmutableParams.from(getReportIdParams(report));
        // Delete the report first if it already exists.
        jdbcNamed.update(DELETE_REPORT.getSql(schema()), reportParams);
        // Insert the report
        jdbcNamed.update(INSERT_REPORT.getSql(schema()), reportParams);
        // Return early if the observations have not been set
        if (report.getObservations() == null) {
            logger.warn("The observations have not been set on this report.");
            return;
        }
        // Set mismatch statuses for the report based on previous reports
        setMismatchStatuses(report);
        // Insert all observations
        report.getObservations().forEach((k,v) -> {
            ImmutableParams observationParams = ImmutableParams.from(getObservationParams(reportParams, v));
            int observationId = jdbcNamed.queryForObject(INSERT_OBSERVATION_AND_RETURN_ID.getSql(schema()), observationParams,
                    (rs, row) -> rs.getInt(1));
            // Insert the mismatches for the observation
            v.getMismatches().values().forEach(m -> {
                ImmutableParams mismatchParams = ImmutableParams.from(getMismatchParams(observationId, m));
                KeyHolder mismatchIdHolder = new GeneratedKeyHolder();
                jdbcNamed.update(INSERT_MISMATCH.getSql(schema()), mismatchParams, mismatchIdHolder, new String[]{"id"});
                m.setMismatchId(mismatchIdHolder.getKey().intValue());
                m.getIssueIds().forEach(issueId -> addIssueId(m.getMismatchId(), issueId));
            });
        });
    }

    /** {@inheritDoc} */
    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        ImmutableParams reportIdParams = ImmutableParams.from(new MapSqlParameterSource()
                .addValue("referenceType", reportId.getReferenceType().name())
                .addValue("reportDateTime", toDate(reportId.getReportDateTime())));
        jdbcNamed.update(DELETE_REPORT.getSql(schema()), reportIdParams);
    }

    /** {@inheritDoc} */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("ignoreLevel", Optional.ofNullable(ignoreStatus).map(SpotCheckMismatchIgnore::getCode).orElse(null));
        if (ignoreStatus == null || ignoreStatus == SpotCheckMismatchIgnore.NOT_IGNORED) {
            jdbcNamed.update(DELETE_MISMATCH_IGNORE.getSql(schema()), params);
        } else {
            if (jdbcNamed.update(UPDATE_MISMATCH_IGNORE.getSql(schema()), params) == 0) {
                jdbcNamed.update(INSERT_MISMATCH_IGNORE.getSql(schema()), params);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addIssueId(int mismatchId, String issueId) {
        SqlParameterSource params = getIssueIdParams(mismatchId, issueId);
        jdbcNamed.update(ADD_ISSUE_ID.getSql(schema()), params);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteIssueId(int mismatchId, String issueId) {
        SqlParameterSource params = getIssueIdParams(mismatchId, issueId);
        jdbcNamed.update(DELETE_ISSUE_ID.getSql(schema()), params);
    }

    /** --- Internal Methods --- */

    /**
     * Looks at past observations for each checked content key and sets mismatch statuses in the current report
     * according to their presence in previous reports
     */
    private void setMismatchStatuses(SpotCheckReport<ContentKey> report) {
        // Get latest observation for each content key of this report's reference type
        ReportObservationsHandler obsHandler = new ReportObservationsHandler();
        MapSqlParameterSource params = new MapSqlParameterSource("referenceTypes", String.valueOf(report.getReferenceType()));
        jdbcNamed.query(SELECT_LATEST_OBS_MISMATCHES_PARTIAL.getSql(schema()), params, obsHandler);
        Map<ContentKey, SpotCheckObservation<ContentKey>> latestObs = obsHandler.getObsMap();

        report.getObservations().forEach((contentKey, reportObs) -> {
            if (latestObs.containsKey(contentKey)) { // Leave mismatch statuses as NEW if no prior occurrence
                latestObs.get(contentKey).getMismatches().forEach((type, lastMismatch) -> {
                    if (reportObs.getMismatches().containsKey(type)) { // if a past mismatch has occurred in this report
                        SpotCheckMismatch currentMismatch = reportObs.getMismatches().get(type);
                        switch (lastMismatch.getStatus()) {
                            case RESOLVED: currentMismatch.setStatus(SpotCheckMismatchStatus.REGRESSION); break;
                            default: currentMismatch.setStatus(SpotCheckMismatchStatus.EXISTING);
                        }
                        currentMismatch.setIssueIds(lastMismatch.getIssueIds());
                    } else if (!SpotCheckMismatchStatus.RESOLVED.equals(lastMismatch.getStatus())){
                        // If a past mismatch is not represented for this content in this report, it is resolved
                        lastMismatch.setStatus(SpotCheckMismatchStatus.RESOLVED);
                        reportObs.addMismatch(lastMismatch);
                        // Remove ignore status if the mismatch had a status of 'ignore until resolved'
                        if (lastMismatch.getIgnoreStatus() == SpotCheckMismatchIgnore.IGNORE_UNTIL_RESOLVED) {
                            setMismatchIgnoreStatus(lastMismatch.getMismatchId(), SpotCheckMismatchIgnore.NOT_IGNORED);
                        }
                    }
                    if (lastMismatch.getIgnoreStatus() == SpotCheckMismatchIgnore.IGNORE_ONCE) {
                        setMismatchIgnoreStatus(lastMismatch.getMismatchId(), SpotCheckMismatchIgnore.NOT_IGNORED);
                    }
                });
            }
        });
    }

    /** --- Helper Classes --- */

    protected static final RowMapper<SpotCheckReportId> reportIdRowMapper = (rs,row) ->
            new SpotCheckReportId(SpotCheckRefType.valueOf(rs.getString("reference_type")),
                                  getLocalDateTimeFromRs(rs, "reference_date_time"),
                                  getLocalDateTimeFromRs(rs, "report_date_time"));

    protected class ReportSummaryHandler extends SummaryHandler
    {
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

    protected class OpenMismatchSummaryHandler extends SummaryHandler
    {
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

    protected abstract class SummaryHandler implements RowCallbackHandler
    {
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

    protected class ReportObservationsHandler implements RowCallbackHandler
    {
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

    /** --- Param Source Methods --- */

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

    private MapSqlParameterSource getOpenObsParams(OpenMismatchQuery query) {
        return new MapSqlParameterSource()
                .addValue("referenceTypes", query.getRefTypes().stream().map(Enum::name).collect(Collectors.toList()))
                .addValue("earliest", toDate(query.getObservedAfter()))
                .addValue("mismatchTypes", query.getMismatchTypes() != null
                        ? query.getMismatchTypes().stream()
                                .map(SpotCheckMismatchType::name)
                                .collect(Collectors.toList())
                        : null);
    }

    private MapSqlParameterSource getIssueIdParams(int mismatchId, String issueId) {
        return new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("issueId", issueId);
    }
}