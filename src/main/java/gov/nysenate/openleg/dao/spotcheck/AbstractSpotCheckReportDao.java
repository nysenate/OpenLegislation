package gov.nysenate.openleg.dao.spotcheck;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import gov.nysenate.openleg.dao.base.*;
import gov.nysenate.openleg.model.spotcheck.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<SpotCheckReportId> getReportIds(SpotCheckRefType refType, LocalDateTime start, LocalDateTime end,
                                                SortOrder dateOrder, LimitOffset limOff) {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource()
            .addValue("startDateTime", toDate(start))
            .addValue("endDateTime", toDate(end))
            .addValue("referenceType", refType.toString()));
        OrderBy orderBy = new OrderBy("report_date_time", dateOrder);
        return jdbcNamed.query(SELECT_REPORTS_BY_DATE_AND_TYPE.getSql(schema(), orderBy, limOff), params, (rs,row) ->
            new SpotCheckReportId(SpotCheckRefType.valueOf(rs.getString("reference_type")),
                                  getLocalDateTimeFromRs(rs, "reference_date_time"),
                                  getLocalDateTimeFromRs(rs, "report_date_time"))
        );
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
        // Add resolved mismatches to the report
        setResolvedMismatchesFromPrior(report);
        // Insert only the observations that have mismatches associated with them
        report.getObservations().forEach((k,v) -> {
            if (v.hasMismatches()) {
                ImmutableParams observationParams = ImmutableParams.from(getObservationParams(reportParams, v));
                int observationId = jdbcNamed.queryForObject(INSERT_OBSERVATION_AND_RETURN_ID.getSql(schema()), observationParams,
                        (rs, row) -> rs.getInt(1));
                // Insert the mismatches for the observation
                v.getMismatches().values().forEach(m -> {
                    // Figure out the status for this mismatch, as long as it's not marked as resolved
                    if (!m.getStatus().equals(SpotCheckMismatchStatus.RESOLVED)) {
                        m.setStatus(determineMismatchStatus(observationParams, m));
                    }
                    ImmutableParams mismatchParams = ImmutableParams.from(getMismatchParams(observationId, m));
                    jdbcNamed.update(INSERT_MISMATCH.getSql(schema()), mismatchParams);
                });
            }
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
     * Determine the status of the mismatch by searching for the most recent mismatch of the same key and type.
     * If a prior mismatch exists, base the current status off of the prior one.
     */
    private SpotCheckMismatchStatus determineMismatchStatus(ImmutableParams observationParams, SpotCheckMismatch m) {
        OrderBy orderBy = new OrderBy("report_date_time", SortOrder.DESC);
        ImmutableParams mismatchSearchParams =
            observationParams.add(new MapSqlParameterSource().addValue("type", m.getMismatchType().name()));
        SpotCheckMismatchStatus currStatus = SpotCheckMismatchStatus.NEW;
        List<SpotCheckMismatchStatus> lastStatuses = jdbcNamed.query(SELECT_OBS_MISMATCHES_BY_TYPE.getSql(schema(),
            orderBy, LimitOffset.ONE),
                mismatchSearchParams, (rs, row) -> SpotCheckMismatchStatus.valueOf(rs.getString("status")));
        if (!lastStatuses.isEmpty()) {
            SpotCheckMismatchStatus lastStatus = lastStatuses.get(0);
            switch (lastStatus) {
                case RESOLVED: currStatus = SpotCheckMismatchStatus.REGRESSION; break;
                case EXISTING:
                case NEW:
                case REGRESSION: currStatus = SpotCheckMismatchStatus.EXISTING; break;
            }
        }
        return currStatus;
    }

    /**
     * Find all the mismatches from the most prior report (if it exists) and if they do not appear
     * as mismatches in the current report, add them as resolved mismatches to the current report.
     */
    private void setResolvedMismatchesFromPrior(SpotCheckReport<ContentKey> report) {
        LocalDateTime minPriorTime = LocalDate.ofEpochDay(0).atStartOfDay(); // Way back
        LocalDateTime maxPriorTime = report.getReportDateTime().minusNanos(1000000); // 1 ms prior
        // Get the latest prior report id
        List<SpotCheckReportId> priorReportIds =
            getReportIds(report.getReferenceType(), minPriorTime, maxPriorTime, SortOrder.DESC, LimitOffset.ONE);
        if (!priorReportIds.isEmpty()) {
            // Fetch the full report
            SpotCheckReport<ContentKey> priorReport = getReport(priorReportIds.get(0));
            Map<ContentKey, SpotCheckObservation<ContentKey>> obsMap = report.getObservations();
            // For each observation on a content key, if the observation exists in the current report
            // then we want to find all the mismatches that exist in the prior report but do not exist
            // in the current report. We can assume those are resolved.
            priorReport.getObservations().forEach((contentKey,observation) -> {
                if (obsMap.containsKey(contentKey)) {
                    MapDifference<SpotCheckMismatchType, SpotCheckMismatch> diff =
                        Maps.difference(observation.getMismatches(), obsMap.get(contentKey).getMismatches());
                    diff.entriesOnlyOnLeft().forEach((mismatchType,mismatch) -> {
                        if (!mismatch.getStatus().equals(SpotCheckMismatchStatus.RESOLVED)) {
                            mismatch.setStatus(SpotCheckMismatchStatus.RESOLVED);
                            // Set the resolved mismatch to the current report so it can be saved
                            report.getObservations().get(contentKey).addMismatch(mismatch);
                        }
                    });
                }
            });
        }
    }

    /** --- Helper Classes --- */

    protected class ReportObservationsHandler implements RowCallbackHandler
    {
        private Map<ContentKey, SpotCheckObservation<ContentKey>> obsMap = new HashMap<>();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            ContentKey key = getKeyFromMap(getHstoreMap(rs, "key_arr"));
            if (!obsMap.containsKey(key)) {
                // Set the observation
                SpotCheckObservation<ContentKey> obs = new SpotCheckObservation<>();
                obs.setKey(key);
                obsMap.put(key, obs);
            }
            SpotCheckObservation<ContentKey> obs = obsMap.get(key);
            boolean current = rs.getBoolean("current");
            SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
            SpotCheckMismatchStatus status = SpotCheckMismatchStatus.valueOf(rs.getString("status"));
            String refData = rs.getString("reference_data");
            String obsData = rs.getString("observed_data");
            String notes = rs.getString("notes");
            // Add the current mismatch
            if (current) {
                // Set observation details if not already set
                if (obs.getReferenceId() == null) {
                    SpotCheckReferenceId refId = new SpotCheckReferenceId(
                            SpotCheckRefType.valueOf(rs.getString("reference_type")),
                            getLocalDateTimeFromRs(rs, "reference_active_date"));
                    obs.setReferenceId(refId);
                    obs.setObservedDateTime(getLocalDateTimeFromRs(rs, "observed_date_time"));
                }
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

        public Map<ContentKey, SpotCheckObservation<ContentKey>> getObsMap() {
            return obsMap;
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
}