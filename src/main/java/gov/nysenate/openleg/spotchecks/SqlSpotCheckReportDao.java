package gov.nysenate.openleg.spotchecks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.common.dao.*;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.spotchecks.base.MismatchNotFoundEx;
import gov.nysenate.openleg.spotchecks.base.MismatchUtils;
import gov.nysenate.openleg.spotchecks.keymapper.SpotCheckDaoKeyMapper;
import gov.nysenate.openleg.spotchecks.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.common.util.DateUtils.toDate;
import static gov.nysenate.openleg.spotchecks.SqlSpotCheckReportQuery.*;

/**
 * The AbstractSpotCheckReportDao implements all the functionality required by SpotCheckReportDao
 * regardless of the content key specified. This class must be subclasses with a concrete type for
 * the ContentKey. The subclass will need to handle just the conversions for the ContentKey class.
 */
@Repository
public class SqlSpotCheckReportDao extends SqlBaseDao
        implements SpotCheckReportDao {

    private static final Logger logger = LoggerFactory.getLogger(SqlSpotCheckReportDao.class);

    private final ImmutableMap<Class<?>, SpotCheckDaoKeyMapper<?>> keyMappers;

    public SqlSpotCheckReportDao(List<SpotCheckDaoKeyMapper<?>> keyMapperList) {
        keyMappers = ImmutableMap.copyOf(
                Maps.uniqueIndex(keyMapperList, SpotCheckDaoKeyMapper::getKeyClass)
        );
    }

    /* --- Implemented Methods --- */

    /**
     * {@inheritDoc}
     */
    @Override
    public DeNormSpotCheckMismatch<?> getMismatch(int mismatchId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId);
        String sql = GET_MISMATCH.getSql(schema());
        List<DeNormSpotCheckMismatch<?>> results = jdbcNamed.query(sql, params, mismatchRowMapper);
        if (results.size() == 0) {
            throw new MismatchNotFoundEx(mismatchId);
        }
        return results.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public PaginatedList<DeNormSpotCheckMismatch<?>> getMismatches(MismatchQuery mmQuery,
                                                                LimitOffset limitOffset) {
        LocalDateTime toDateTime = getReportEndDateTime(mmQuery.getReportDate());
        MapSqlParameterSource params = activeMismatchParams(toDateTime, mmQuery.getDataSource())
                .addValue("contentTypes", mmQuery.getContentTypes().stream()
                        .map(Enum::name).collect(Collectors.toSet()))
                .addValue("state", mmQuery.getState().name())
                .addValue("observedStartDateTime", mmQuery.getObservedStartDateTime())
                .addValue("firstSeenStartDateTime", mmQuery.getFirstSeenStartDateTime())
                .addValue("firstSeenEndDateTime", mmQuery.getFirstSeenEndDateTime())
                .addValue("observedEndDateTime", mmQuery.getObservedEndDateTime())
                .addValue("ignoreStatuses", mmQuery.getIgnoredStatuses().stream()
                        .map(Enum::name).collect(Collectors.toSet()))
                .addValue("mismatchTypes", extractEnumSetParams(mmQuery.getMismatchTypes()))
                ;
        final SqlSpotCheckReportQuery query;
        if (mmQuery.isFilteringKeys()) {
            query = GET_MISMATCHES_FOR_KEYS;
            params.addValue("keys",
                    mmQuery.getKeys().entries().stream()
                            .map(e -> getMapFromKey(e.getKey(), e.getValue()))
                            .map(SqlBaseDao::toHstoreString)
                            .collect(Collectors.toList()));
        } else {
            query = GET_MISMATCHES;
        }
        String sql = query.getSql(schema(), mmQuery.getOrderBy(), limitOffset);
        PaginatedRowHandler<DeNormSpotCheckMismatch<?>> handler =
                new PaginatedRowHandler<>(limitOffset, "total_rows", mismatchRowMapper);
        jdbcNamed.query(sql, params, handler);
        return handler.getList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchStatusSummary getMismatchStatusSummary(LocalDate reportDate, SpotCheckDataSource datasource,
                                                          SpotCheckContentType contentType, Set<SpotCheckMismatchIgnore> ignoreStatuses) {
        MapSqlParameterSource params = activeMismatchParams(getReportEndDateTime(reportDate), datasource)
                .addValue("contentType", contentType.name())
                .addValue("ignoreStatuses", extractEnumSetParams(ignoreStatuses))
                .addValue("reportStartDateTime", getReportStartDateTime(reportDate))
                .addValue("reportEndDateTime", getReportEndDateTime(reportDate));
        String sql = MISMATCH_STATUS_SUMMARY.getSql(schema());
        MismatchStatusSummaryHandler summaryHandler = new MismatchStatusSummaryHandler();
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchTypeSummary getMismatchTypeSummary(LocalDate reportDate, SpotCheckDataSource datasource,
                                                      SpotCheckContentType contentType, MismatchStatus mismatchStatus,
                                                      Set<SpotCheckMismatchIgnore> ignoreStatuses) {
        MapSqlParameterSource params = activeMismatchParams(getReportEndDateTime(reportDate), datasource)
                .addValue("ignoreStatuses", extractEnumSetParams(ignoreStatuses))
                .addValue("observedStartDateTime", mismatchStatus.getObservedStartDateTime(reportDate))
                .addValue("firstSeenStartDateTime", mismatchStatus.getFirstSeenStartDateTime(reportDate))
                .addValue("firstSeenEndDateTime", mismatchStatus.getFirstSeenEndDateTime(reportDate))
                .addValue("observedEndDateTime", mismatchStatus.getObservedEndDateTime(reportDate))
                .addValue("contentType", contentType.name())
                .addValue("state", mismatchStatus.getState().name());
        String sql = MISMATCH_TYPE_SUMMARY.getSql(schema());
        MismatchTypeSummaryHandler summaryHandler = new MismatchTypeSummaryHandler(contentType);
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MismatchContentTypeSummary getMismatchContentTypeSummary(LocalDate reportDate, SpotCheckDataSource datasource,
                                                                    Set<SpotCheckMismatchIgnore> ignoreStatuses) {
          MapSqlParameterSource params = activeMismatchParams(getReportEndDateTime(reportDate), datasource)
                  .addValue("ignoreStatuses", extractEnumSetParams(ignoreStatuses))
                  .addValue("reportStartDateTime", getReportStartDateTime(reportDate))
                  .addValue("reportEndDateTime", getReportEndDateTime(reportDate));
        String sql = MISMATCH_CONTENT_TYPE_SUMMARY.getSql(schema());
        MismatchContentTypeSummaryHandler summaryHandler = new MismatchContentTypeSummaryHandler();
        jdbcNamed.query(sql, params, summaryHandler);
        return summaryHandler.getSummary();
    }

    /**
     * Corresponds to the max number of parameters passable to postgres via jdbc.
     * (a little bit less than {@link Short#MAX_VALUE} to allow for additional query params
     * besides content keys)
     */
    private static final int mismatchSaveBatchSize = Short.MAX_VALUE - 500;

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveReport(SpotCheckReport<?> report) {
        int reportId = insertReport(report);
        report.setId(reportId);

        // Partition observations into saveable sizes and save each partition.

        List<List<SpotCheckObservation<?>>> obsPartitions =
                Lists.partition(new ArrayList<>(report.getObservations()), mismatchSaveBatchSize);

        obsPartitions.forEach(obsBatch -> saveObservations(report, obsBatch));
    }

    /**
     * Save the given subset of observations for the given report.
     * @param report {@link SpotCheckReport}
     * @param observations {@link List<SpotCheckObservation>}
     */
    private void saveObservations(SpotCheckReport<?> report, Collection<SpotCheckObservation<?>> observations) {

        Map<SpotCheckMismatchKey<?>, DeNormSpotCheckMismatch<?>> reportMismatches = observations.stream()
                .flatMap(obs -> toDeNormMismatches(obs, report).stream())
                .collect(Collectors.toMap(DeNormSpotCheckMismatch::getMismatchKey, Function.identity()));

        Map<SpotCheckMismatchKey<?>, DeNormSpotCheckMismatch<?>> savedMismatchMap = Maps.uniqueIndex(
                getRelevantSavedMismatches(report, observations),
                DeNormSpotCheckMismatch::getMismatchKey
        );

        // Determine which saved mismatches are closed out with this report and add them to the report mismatches
        List<DeNormSpotCheckMismatch<?>> closedMismatches = MismatchUtils.determineClosedMismatches(
                savedMismatchMap.values(), report);
        reportMismatches.putAll(Maps.uniqueIndex(closedMismatches, DeNormSpotCheckMismatch::getMismatchKey));

        // Perform any necessary tweaks to each report mismatch
        for (DeNormSpotCheckMismatch<?> reportMismatch : reportMismatches.values()) {
            Optional<DeNormSpotCheckMismatch<?>> savedMismatchOpt = Optional.ofNullable(
                    savedMismatchMap.get(reportMismatch.getMismatchKey())
            );

            savedMismatchOpt.ifPresent(reportMismatch::copyIgnoreStatus);

            MismatchUtils.updateIgnoreStatus(reportMismatch);
            MismatchUtils.updateFirstSeenDateTime(reportMismatch, savedMismatchOpt);
        }

        insertMismatches(reportMismatches.values());
    }

    /**
     * Gets any open mismatches for the content observed in the report
     */
    private List<DeNormSpotCheckMismatch<?>> getRelevantSavedMismatches(SpotCheckReport<?> report,
                                                                     Collection<SpotCheckObservation<?>> observations) {
        Set<Object> keys = observations.stream()
                .map(SpotCheckObservation::getKey)
                .collect(Collectors.toSet());
        MismatchQuery query = new MismatchQuery(report.getReportDateTime().toLocalDate(),
                                                report.getReferenceType().getDataSource(),
                                                MismatchStatus.OPEN,
                                                Sets.newHashSet(report.getReferenceType().getContentType()))
                .withIgnoredStatuses(EnumSet.allOf(SpotCheckMismatchIgnore.class))
                .withKeys(report.getReferenceType().getContentType(), keys);
        return getMismatches(query, LimitOffset.ALL).results();
    }

    private int insertReport(SpotCheckReport<?> report) {
        ImmutableParams reportParams = ImmutableParams.from(getReportIdParams(report));
        KeyHolder reportIdHolder = new GeneratedKeyHolder();
        jdbcNamed.update(INSERT_REPORT.getSql(schema()), reportParams, reportIdHolder, new String[]{"id"});
        return reportIdHolder.getKey().intValue();
    }

    private void insertMismatches(Collection<DeNormSpotCheckMismatch<?>> mismatches) {
        List<MapSqlParameterSource> params = mismatches.stream()
                .map(this::mismatchParams).toList();
        String sql = INSERT_MISMATCH.getSql(schema());
        jdbcNamed.batchUpdate(sql, params.toArray(MapSqlParameterSource[]::new));
    }

    /**
     * Parameters used in the {@link SqlSpotCheckReportQuery} ACTIVE_MISMATCHES query.
     */
    private MapSqlParameterSource activeMismatchParams(LocalDateTime toDateTime, SpotCheckDataSource dataSource) {
        return new MapSqlParameterSource()
                .addValue("sessionStartDateTime", SessionYear.of(toDateTime.getYear()).getStartDateTime())
                .addValue("reportEndDateTime", toDateTime)
                .addValue("datasource", dataSource.name());
    }

    private MapSqlParameterSource mismatchParams(DeNormSpotCheckMismatch<?> mismatch) {
        return new MapSqlParameterSource()
                .addValue("key", toHstoreString(getMapFromKey(mismatch.getContentType(), mismatch.getKey())))
                .addValue("mismatchType", mismatch.getType().name())
                .addValue("reportId", mismatch.getReportId())
                .addValue("datasource", mismatch.getDataSource().name())
                .addValue("contentType", mismatch.getContentType().name())
                .addValue("referenceType", mismatch.getReferenceId().getReferenceType().name())
                .addValue("mismatchStatus", mismatch.getState().name())
                .addValue("referenceData", mismatch.getReferenceData())
                .addValue("observedData", mismatch.getObservedData())
                .addValue("notes", mismatch.getNotes())
                .addValue("issueIds", toPostgresArray(mismatch.getIssueIds()))
                .addValue("ignoreLevel", mismatch.getIgnoreStatus().name())
                .addValue("reportDateTime", mismatch.getReportDateTime())
                .addValue("observedDateTime", mismatch.getObservedDateTime())
                .addValue("firstSeenDateTime", mismatch.getFirstSeenDateTime())
                .addValue("referenceActiveDateTime", mismatch.getReferenceId().getRefActiveDateTime());
    }

    /**
     * Converts SpotCheckMismatches in a SpotCheckReport into a map of key -> DeNormSpotCheckMismatch
     * Initializes firstSeenDateTime to the observedDateTime.
     */
    private List<DeNormSpotCheckMismatch<?>> toDeNormMismatches(SpotCheckObservation<?> obs,
                                                             SpotCheckReport<?> report) {
        return obs.getMismatches().values().stream()
                .map(mm -> toDeNormMismatch(mm, obs, report))
                .collect(Collectors.toList());
    }

    private DeNormSpotCheckMismatch<?> toDeNormMismatch(SpotCheckMismatch mismatch,
                                                     SpotCheckObservation<?> obs,
                                                     SpotCheckReport<?> report) {
        DeNormSpotCheckMismatch<?> dnm = new DeNormSpotCheckMismatch<>(obs.getKey(), mismatch.getMismatchType(),
                report.getReferenceType().getDataSource());
        dnm.setReportId(report.getId());
        dnm.setContentType(report.getReferenceType().getContentType());
        dnm.setReferenceId(obs.getReferenceId());
        dnm.setReferenceData(mismatch.getReferenceData());
        dnm.setObservedData(mismatch.getObservedData());
        dnm.setNotes(mismatch.getNotes());
        dnm.setObservedDateTime(obs.getObservedDateTime());
        dnm.setReportDateTime(report.getReportDateTime());
        if (mismatch.getIgnoreStatus() != null)
            dnm.setIgnoreStatus(mismatch.getIgnoreStatus());
        dnm.setIssueIds(new HashSet<>(mismatch.getIssueIds()));
        return dnm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMismatchIgnoreStatus(int mismatchId, SpotCheckMismatchIgnore ignoreStatus) {
        if (ignoreStatus == null) {
            throw new IllegalArgumentException("Cannot set mismatch ignore state to null.");
        }
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId)
                .addValue("ignoreStatus", ignoreStatus.name());
        String sql = UPDATE_MISMATCH_IGNORE.getSql(schema());
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
        String sql = UPDATE_ISSUE_ID.getSql(schema());
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
        String sql = ADD_ISSUE_ID.getSql(schema());
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
        String sql = DELETE_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllIssueId(int mismatchId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("mismatchId", mismatchId);
        String sql = DELETE_ALL_ISSUE_ID.getSql(schema());
        jdbcNamed.update(sql, params);
    }

    /** Convert a Set containing enums into a Set containing each enum's name. */
    private <E extends Enum<E>> Set<String> extractEnumSetParams(Set<E> enumSet) {
        return enumSet.stream().map(Enum::name).collect(Collectors.toSet());
    }

    /* --- Helper Classes --- */

    private SpotCheckDaoKeyMapper<?> getMapperForKeyClass(Class<?> keyClass) {
        SpotCheckDaoKeyMapper<?> keyMapper = keyMappers.get(keyClass);
        if (keyMapper == null) {
            throw new IllegalStateException("No key mapper found for mismatch of type " + keyClass.getSimpleName());
        }
        return keyMapper;
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> getMapFromKey(SpotCheckContentType contentType, Object key) {
        Class<?> keyClass = contentType.getContentKeyClass();
        SpotCheckDaoKeyMapper spotCheckDaoKeyMapper = getMapperForKeyClass(keyClass);
        return spotCheckDaoKeyMapper.getMapFromKey(key);
    }

    private Object getKeyFromMap(SpotCheckContentType contentType, Map<String, String> keyMap) {
        Class<?> contentKeyClass = contentType.getContentKeyClass();
        SpotCheckDaoKeyMapper<?> keyMapper = getMapperForKeyClass(contentKeyClass);
        return keyMapper.getKeyFromMap(keyMap);
    }

    private final RowMapper<DeNormSpotCheckMismatch<?>> mismatchRowMapper = (rs, rowNum) -> {
        SpotCheckContentType contentType = SpotCheckContentType.valueOf(rs.getString("content_type"));
        Object key = getKeyFromMap(contentType, getHstoreMap(rs, "key_arr"));
        SpotCheckMismatchType type = SpotCheckMismatchType.valueOf(rs.getString("type"));
        SpotCheckDataSource dataSource = SpotCheckDataSource.valueOf(rs.getString("datasource"));
        DeNormSpotCheckMismatch<?> mismatch = new DeNormSpotCheckMismatch<>(key, type, dataSource);
        mismatch.setMismatchId(rs.getInt("mismatch_id"));
        mismatch.setReportId(rs.getInt("report_id"));
        mismatch.setState(MismatchState.valueOf(rs.getString("state")));
        mismatch.setContentType(contentType);
        mismatch.setReferenceData(rs.getString("reference_data"));
        mismatch.setObservedData(rs.getString("observed_data"));
        mismatch.setReportDateTime(getLocalDateTimeFromRs(rs, "report_date_time"));
        mismatch.setObservedDateTime(getLocalDateTimeFromRs(rs, "observed_date_time"));
        mismatch.setFirstSeenDateTime(getLocalDateTimeFromRs(rs, "first_seen_date_time"));
        mismatch.setNotes(rs.getString("notes"));
        mismatch.setIgnoreStatus(SpotCheckMismatchIgnore.valueOf(rs.getString("ignore_status")));
        String[] issueIds = (String[]) rs.getArray("issue_ids").getArray();
        mismatch.setIssueIds(Sets.newHashSet(issueIds));

        SpotCheckRefType refType = SpotCheckRefType.valueOf(rs.getString("reference_type"));
        LocalDateTime refActiveDateTime = getLocalDateTimeFromRs(rs, "reference_active_date_time");
        mismatch.setReferenceId(new SpotCheckReferenceId(refType, refActiveDateTime));
        return mismatch;
    };

    private static class MismatchStatusSummaryHandler implements RowCallbackHandler {

        private final MismatchStatusSummary summary = new MismatchStatusSummary();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            MismatchStatus status = MismatchStatus.valueOf(rs.getString("status"));
            int count = rs.getInt("count");
            summary.putSummary(status, count);
        }

        protected MismatchStatusSummary getSummary() {
            summary.putSummary(MismatchStatus.OPEN, summary.getSummary().get(MismatchStatus.NEW)+summary.getSummary().get(MismatchStatus.EXISTING));
            return summary;
        }
    }

    private static class MismatchTypeSummaryHandler implements RowCallbackHandler {

        private final MismatchTypeSummary summary;

        MismatchTypeSummaryHandler(SpotCheckContentType contentType) {
            summary = new MismatchTypeSummary(contentType);
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckMismatchType spotCheckMismatchType = SpotCheckMismatchType.valueOf(rs.getString("type"));
            int count = rs.getInt("count");
            summary.addSpotCheckMismatchTypeCount(spotCheckMismatchType, count);
        }

        protected MismatchTypeSummary getSummary() {
            int all = 0;
            for (Map.Entry<SpotCheckMismatchType, Integer> entry:summary.getSummary().entrySet()){
                all += entry.getValue();
            }
            summary.getSummary().put(SpotCheckMismatchType.All,all);
            return summary;
        }
    }

    private static class MismatchContentTypeSummaryHandler implements RowCallbackHandler {

        private final MismatchContentTypeSummary summary = new MismatchContentTypeSummary();

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            SpotCheckContentType contentType = SpotCheckContentType.valueOf(rs.getString("content_type"));
            int count = rs.getInt("count");
            summary.addSpotCheckMismatchContentTypeCount(contentType, count);
        }

        protected MismatchContentTypeSummary getSummary() {
            return summary;
        }
    }

    private MapSqlParameterSource getReportIdParams(SpotCheckReport<?> report) {
        return new MapSqlParameterSource()
                .addValue("referenceType", report.getReferenceType().name())
                .addValue("reportDateTime", toDate(report.getReportDateTime()))
                .addValue("referenceDateTime", toDate(report.getReferenceDateTime()))
                .addValue("notes", report.getNotes());
    }

    private LocalDateTime getReportStartDateTime(LocalDate date) {
        return date.atStartOfDay();
    }

    private LocalDateTime getReportEndDateTime(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

}
