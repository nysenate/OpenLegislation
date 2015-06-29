package gov.nysenate.openleg.dao.spotcheck;

import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
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
     * @return ContentillKey
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
        Map<ContentKey, SpotCheckObservation<ContentKey>> obsMap = new HashMap<>();
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

    /** {@inheritDoc}
     * @param query*/
    @Override
    public SpotCheckOpenMismatches<ContentKey> getOpenObservations(OpenMismatchQuery query) {
        ImmutableParams params = ImmutableParams.from(getOpenObsParams(query));
        ReportObservationsHandler handler = new ReportObservationsHandler();
        jdbcNamed.query(getLatestOpenObsMismatchesQuery(schema(), query),
                params, handler);
        return new SpotCheckOpenMismatches<>(query.getRefType(), handler.getObsMap(), handler.getMismatchTotal());
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
                jdbcNamed.update(INSERT_MISMATCH.getSql(schema()), mismatchParams);
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

    /** --- Internal Methods --- */

    /**
     * Looks at past observations for each checked content key and sets mismatch statuses in the current report
     * according to their presence in previous reports
     */
    private void setMismatchStatuses(SpotCheckReport<ContentKey> report) {
        // Get latest observation for each content key of this report's reference type
        ReportObservationsHandler obsHandler = new ReportObservationsHandler();
        MapSqlParameterSource params = new MapSqlParameterSource("referenceType", String.valueOf(report.getReferenceType()));
        jdbcNamed.query(SELECT_LATEST_OBS_MISMATCHES_PARTIAL.getSql(schema()), params, obsHandler);
        Map<ContentKey, SpotCheckObservation<ContentKey>> latestObs = obsHandler.getObsMap();

        report.getObservations().forEach((contentKey, reportObs) -> {
            if (latestObs.containsKey(contentKey)) { // Leave mismatch statuses as NEW if no prior occurrence
                latestObs.get(contentKey).getMismatches().forEach((type, lastMismatch) -> {
                    if (reportObs.getMismatches().containsKey(type)) { // if a past mismatch has occurred in this report
                        SpotCheckMismatch currentMismatch = reportObs.getMismatches().get(type);
                        switch (lastMismatch.getStatus()) {
                            case RESOLVED: currentMismatch.setStatus(SpotCheckMismatchStatus.REGRESSION); break;
                            case IGNORE: currentMismatch.setStatus(SpotCheckMismatchStatus.IGNORE); break;
                            default: currentMismatch.setStatus(SpotCheckMismatchStatus.EXISTING);
                        }
                    } else if (!SpotCheckMismatchStatus.RESOLVED.equals(lastMismatch.getStatus())){
                        // If a past mismatch is not represented for this content in this report, it is resolved
                        lastMismatch.setStatus(SpotCheckMismatchStatus.RESOLVED);
                        reportObs.addMismatch(lastMismatch);
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

    protected class ReportIdHandler extends PaginatedRowHandler<SpotCheckReportId>
    {
        public ReportIdHandler(LimitOffset limOff) {
            super(limOff, "total", reportIdRowMapper);
        }
    }

    protected class ReportSummaryHandler implements RowCallbackHandler
    {
        private Map<SpotCheckReportId, SpotCheckReportSummary> summaryMap;

        public ReportSummaryHandler(SortOrder order) {
            summaryMap = new TreeMap<>((a, b) -> a.compareTo(b) * (SortOrder.ASC.equals(order) ? 1 : -1));
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckReportId id = reportIdRowMapper.mapRow(rs, rs.getRow());
            if (!summaryMap.containsKey(id)) {
                summaryMap.put(id, new SpotCheckReportSummary(id, rs.getString("notes"), rs.getInt("observation_count")));
            }
            try {
                SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
                SpotCheckMismatchStatus status = SpotCheckMismatchStatus.valueOf(rs.getString("status"));
                int count = rs.getInt("mismatch_count");
                summaryMap.get(id).addMismatchTypeCount(type, status, count);
            } catch (NullPointerException ignored) {}
        }

        public List<SpotCheckReportSummary> getSummaries() {
            return new ArrayList<>(summaryMap.values());
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
                obsMap.put(key, obs);
            }
            SpotCheckObservation<ContentKey> obs = obsMap.get(key);
            rs.getInt("mismatch_id");
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
                // Add the current mismatch
                if (current) {
                    SpotCheckMismatch mismatch = new SpotCheckMismatch(type, refData, obsData, notes);
                    mismatch.setStatus(status);
                    obs.addMismatch(mismatch);
                }
                // Otherwise add the prior mismatch
                else {
                    SpotCheckReportId reportId = new SpotCheckReportId(
                            SpotCheckRefType.valueOf(rs.getString("report_reference_type")),
                            getLocalDateTimeFromRs(rs, "reference_active_date"),
                            getLocalDateTimeFromRs(rs, "report_date_time")
                    );
                    SpotCheckPriorMismatch priorMismatch = new SpotCheckPriorMismatch(type, refData, obsData, notes);
                    priorMismatch.setReportId(reportId);
                    priorMismatch.setStatus(status);
                    obs.addPriorMismatch(priorMismatch);
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
        return new MapSqlParameterSource()
            .addValue("observationId", observationId)
            .addValue("type", mismatch.getMismatchType().name())
            .addValue("status", mismatch.getStatus().name())
            .addValue("referenceData", mismatch.getReferenceData())
            .addValue("observedData", mismatch.getObservedData())
            .addValue("notes", mismatch.getNotes());
    }

    private MapSqlParameterSource getOpenObsParams(OpenMismatchQuery query) {
        return new MapSqlParameterSource()
                .addValue("referenceType", query.getRefType().name())
                .addValue("earliest", toDate(query.getObservedAfter()))
                .addValue("mismatchTypes", query.getMismatchTypes().stream()
                        .map(SpotCheckMismatchType::name)
                        .collect(Collectors.toList()));
    }
}