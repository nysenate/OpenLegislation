package gov.nysenate.openleg.spotchecks;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.SimpleResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.spotcheck.view.MismatchContentTypeSummaryView;
import gov.nysenate.openleg.api.spotcheck.view.MismatchStatusSummaryView;
import gov.nysenate.openleg.api.spotcheck.view.MismatchTypeSummaryView;
import gov.nysenate.openleg.api.spotcheck.view.MismatchView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.OrderBy;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.spotchecks.base.SpotcheckRunService;
import gov.nysenate.openleg.spotchecks.model.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck", produces = APPLICATION_JSON_VALUE)
public class SpotCheckCtrl extends BaseCtrl {
    private final SpotcheckRunService spotcheckRunService;
    private final SpotCheckReportDao spotCheckReportDao;

    public SpotCheckCtrl(SpotcheckRunService spotcheckRunService, SpotCheckReportDao spotCheckReportDao) {
        this.spotcheckRunService = spotcheckRunService;
        this.spotCheckReportDao = spotCheckReportDao;
    }

    /**
     * Spotcheck Mismatch API
     *
     * <p>Queries for spotcheck mismatches matching the supplied parameters.
     *
     * <p>Usage: (GET) /api/3/admin/spotcheck/mismatches
     *
     * <p>Request Parameters: <ul>
     *                     <li>datasource - string - retrieves mismatches for the specified datasource.
     *                     <li>contentType - string - retrieves mismatches for the specified content type.
     *                     <li>mismatchStatus - string[] - retrieves mismatches of the specified status.
     *                     <li>reportDate - string (ISO date) - optional - returns summary information for this date.
     *                                       Defaults to current date.
     *                     <li>mismatchType - string - optional, default all mismatch types. - retrieves mismatches of the specified type.
     *                     <li>ignoredStatuses - string[] - optional, default [NOT_IGNORED] - retrieves mismatches with the given ignore status.
     *                     <li>orderBy - string - optional, order results by the specified field, must be a valid {@link MismatchOrderBy} value.
     *                              - Defaults to REFERENCE_DATE.
     *                     <li>sort - string - optional, a SortOrder value representing the sort order. Defaults to DESC
     *                     <li>limit - int - limit the number of results.
     *                     <li>offset - int - start results from an offset.
     *                     </ul>
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/mismatches", method = RequestMethod.GET)
    public BaseResponse getMismatches(@RequestParam String datasource,
                                      @RequestParam String contentType,
                                      @RequestParam String mismatchStatus,
                                      @RequestParam(required = false) String reportDate,
                                      @RequestParam(required = false) String mismatchType,
                                      @RequestParam(required = false) String[] ignoredStatuses,
                                      @RequestParam(required = false) String orderBy,
                                      @RequestParam(required = false) String sort,
                                      WebRequest request) {
        SpotCheckDataSource ds = getDatasource(datasource);
        SpotCheckContentType ct = getContentType(contentType);
        MismatchStatus status = getMismatchStatus(mismatchStatus);
        LocalDate rDate = getReportDate(reportDate);
        EnumSet<SpotCheckMismatchType> type = getMismatchTypes(mismatchType);
        Set<SpotCheckMismatchIgnore> igs = getIgnoredStatuses(ignoredStatuses);
        OrderBy order = getOrderBy(orderBy, sort);
        LimitOffset limitOffset = getLimitOffset(request, 10);

        MismatchQuery query = new MismatchQuery(rDate, ds, status, Collections.singleton(ct))
                .withIgnoredStatuses(igs)
                .withOrderBy(order)
                .withMismatchTypes(type);

        PaginatedList<DeNormSpotCheckMismatch<?>> mismatches = spotCheckReportDao.getMismatches(query, limitOffset);
        List<MismatchView<?>> mismatchViews = new ArrayList<>();
        for (DeNormSpotCheckMismatch<?> mm : mismatches.results()) {
            mismatchViews.add(new MismatchView(mm));
        }
        return ListViewResponse.of(mismatchViews, mismatches.total(), mismatches.limOff());
    }

    /**
     * SpotCheck Mismatch Status Summary API
     *
     * Get a summary of mismatch status counts for a report.
     *
     * Usage: (GET) /api/3/admin/spotcheck/mismatches/summary/status
     *
     * Request Parameters: datasource - string - The datasource to return summary information on.
     *                     reportDate - string (ISO date) - optional - returns summary information for this date.
     *                                       Defaults to current date.
     *                     ignoredStatuses - string[] - optional, default [NOT_IGNORED] - retrieves mismatches with the given ignore status.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/mismatches/summary/status", method = RequestMethod.GET)
    public BaseResponse getMismatchStatusSummary(@RequestParam String datasource,
                                                 @RequestParam String contentType,
                                                 @RequestParam(required = false) String reportDate,
                                                 @RequestParam(required = false) String[] ignoredStatuses) {
        SpotCheckDataSource ds = getDatasource(datasource);
        SpotCheckContentType ct = getContentType(contentType);
        LocalDate rDate = getReportDate(reportDate);
        Set<SpotCheckMismatchIgnore> igs = getIgnoredStatuses(ignoredStatuses);
        MismatchStatusSummary summary = spotCheckReportDao.getMismatchStatusSummary(rDate, ds, ct, igs);
        return new ViewObjectResponse<>(new MismatchStatusSummaryView(summary));
    }

    /**
     * SpotCheck Mismatch Type Summary API
     *
     * Get a summary of mismatch type counts for a given datasource and mismatch status.
     *
     * Usage: (GET) /api/3/admin/spotcheck/mismatches/summary/mismatchtype
     *
     * Request Parameters: datasource - string - The datasource to return summary information on.
     *                     reportDate - string (ISO date) - optional - returns summary information for this date.
     *                                       Defaults to current date.
     *                     mismatchStatus - string - optional - only includes counts for mismatches with this {@link MismatchStatus}.
     *                                       Defaults to OPEN
     *                     ignoredStatuses - string[] - optional, default [NOT_IGNORED] - retrieves mismatches with the given ignore status.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/mismatches/summary/mismatchtype", method = RequestMethod.GET)
    public BaseResponse getMismatchTypeSummary(@RequestParam String datasource,
                                               @RequestParam String contentType,
                                               @RequestParam(required = false) String reportDate,
                                               @RequestParam(required = false) String mismatchStatus,
                                               @RequestParam(required = false) String[] ignoredStatuses) {
        SpotCheckDataSource ds = getDatasource(datasource);
        SpotCheckContentType ct = getContentType(contentType);
        LocalDate rDate = getReportDate(reportDate);
        MismatchStatus status = mismatchStatus == null ? MismatchStatus.OPEN : getMismatchStatus(mismatchStatus);
        Set<SpotCheckMismatchIgnore> igs = getIgnoredStatuses(ignoredStatuses);
        MismatchTypeSummary summary = spotCheckReportDao.getMismatchTypeSummary(rDate, ds, ct, status, igs);
        return new ViewObjectResponse<>(new MismatchTypeSummaryView(summary));
    }

    /**
     * Spotcheck Mismatch Content Type Summary API
     *
     * Get a summary of mismatch Content type counts for all content types for a specific datasource.
     *
     * Usage: (GET) /api/3/admin/spotcheck/mismatches/summary/contenttype
     *
     * Request Parameters: datasource - string - The datasource to return summary information on.
     *                     reportDate - string (ISO date) - optional - returns summary information for this date.
     *                                       Defaults to current date.
     *                     mismatchStatus - string - optional - only include counts for mismatches with this {@link MismatchStatus}.
     *                                       Defaults to OPEN
     *                     mismatchType - string - optional - only include counts for mismatches of this {@link SpotCheckMismatchType}.
     *                                       Defaults to ALL mismatch types. Set this value to filter for a single mismatch type.
     *                     ignoredStatuses - string[] - optional, default [NOT_IGNORED] - retrieves mismatches with the given ignore status.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/mismatches/summary/contenttype", method = RequestMethod.GET)
    public BaseResponse getMismatchContentTypeSummary(@RequestParam String datasource,
                                                      @RequestParam(required = false) String reportDate,
                                                      @RequestParam(required = false) String[] ignoredStatuses) {
        SpotCheckDataSource ds = getDatasource(datasource);
        LocalDate rDate = getReportDate(reportDate);
        Set<SpotCheckMismatchIgnore> igs = getIgnoredStatuses(ignoredStatuses);
        MismatchContentTypeSummary summary = spotCheckReportDao.getMismatchContentTypeSummary(rDate, ds, igs);
        return new ViewObjectResponse<>(new MismatchContentTypeSummaryView(summary));
    }

    /**
     * Spotcheck Mismatch Ignore API
     *
     * Set the ignore status of a particular mismatch
     *
     * Usage: (POST) /api/3/admin/spotcheck/mismatches/{mismatchId}/ignore
     *
     * Request Parameters: ignoreLevel - string - specifies desired ignore level or unsets ignore if null or not present
     *                                  @see SpotCheckMismatchIgnore
     */
    @RequestMapping(value = "/mismatches/{mismatchId:\\d+}/ignore", method = RequestMethod.POST)
    public BaseResponse setIgnoreStatus(@PathVariable int mismatchId, @RequestParam(required = false) String ignoreLevel) {
        SpotCheckMismatchIgnore ignoreStatus = ignoreLevel == null
                ? SpotCheckMismatchIgnore.NOT_IGNORED
                : getEnumParameter("ignoreLevel", ignoreLevel, SpotCheckMismatchIgnore.class);
        spotCheckReportDao.setMismatchIgnoreStatus(mismatchId, ignoreStatus);
        return new SimpleResponse(true, "ignore level set", "ignore-level-set");
    }


    /**
     * Spotcheck Mismatch Add Issue Id API
     *
     * Adds an issue id to a spotcheck mismatch
     *
     * Usage: (POST) /api/3/admin/spotcheck/mismatches/{mismatchId}/issue/{issueId}
     */
    @RequestMapping(value = "/mismatches/{mismatchId:\\d+}/issue/{issueId}", method = RequestMethod.GET)
    public BaseResponse addMismatchIssueId(@PathVariable int mismatchId, @PathVariable String issueId) {
        spotCheckReportDao.addIssueId(mismatchId, issueId);
        return new SimpleResponse(true, "issue id added", "issue-id-added");
    }

    /**
     * Spotcheck Mismatch update Issue Id API
     * @param mismatchId  mismatch id
     * @param issueId mismatch issues id separate by comma ,e.g 12,3,61
     * @return true
     *
     * Usage: (POST) /api/3/admin/spotcheck/mismatches/{mismatchId}/issue/{issueId}
     */
    @RequestMapping(value = "/mismatches/{mismatchId:\\d+}/issue/{issueId}", method = RequestMethod.POST)
    public BaseResponse updateMismatchIssueId(@PathVariable int mismatchId, @PathVariable String issueId) {
        spotCheckReportDao.updateIssueId(mismatchId, issueId);
        return new SimpleResponse(true, "issue id updated", "issue-id-updated");
    }

    /**
     * Spotcheck Mismatch Remove Issue Id API
     *
     * Removes an issue id to a spotcheck mismatch
     *
     * Usage: (DELETE) /api/3/admin/spotcheck/mismatch/{mismatchId}/issue/{issueId}
     */
    @RequestMapping(value = "/mismatch/{mismatchId:\\d+}/issue/{issueId}", method = RequestMethod.DELETE)
    public BaseResponse deleteMismatchIssueId(@PathVariable int mismatchId, @PathVariable String issueId) {
        spotCheckReportDao.deleteIssueId(mismatchId, issueId);
        return new SimpleResponse(true, "issue id deleted", "issue-id-deleted");
    }

    /**
     * Spotcheck Mismatch remove All Issue Id API
     *
     * Removes an issue id to a spotcheck mismatch
     *
     * Usage: (DELETE) /api/3/admin/spotcheck/mismatch/{mismatchId}/delete
     */
    @RequestMapping(value = "/mismatch/{mismatchId:\\d+}/delete", method = RequestMethod.DELETE)
    public BaseResponse deleteMismatchIssueId(@PathVariable int mismatchId) {
        spotCheckReportDao.deleteAllIssueId(mismatchId);
        return new SimpleResponse(true, "issue id deleted", "issue-id-deleted");
    }
    /**
     * Spotcheck Report Run API
     *
     * Attempts to run spotcheck reports for the given report types
     *
     * Usage: (GET) /api/3/admin/spotcheck/run
     *
     * Request Parameters: reportType - string[] or string (in path variable) - specifies which kinds of report summaries
     *                                  are retrieved - defaults to all
     *                                  @see SpotCheckRefType
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run")
    public BaseResponse runReports(@RequestParam String[] reportType) {
        Set<SpotCheckRefType> refTypes = getSpotcheckRefTypes(reportType, "reportType");
        refTypes.forEach(spotcheckRunService::runReports);
        return new ViewObjectResponse<>(ListView.ofStringList(
                refTypes.stream().map(SpotCheckRefType::toString).toList()),
                "spotcheck reports run");
    }

    /**
     * Spotcheck Report Run API
     *
     * Attempts to run spotcheck reports for the given report types
     * Will run the spot check reports on the session specified by start year
     *
     * Usage: (GET) /api/3/admin/spotcheck/run/{startYear}
     *
     * Request Parameters: reportType - string[] or string (in path variable) - specifies which kinds of report summaries
     *                                  are retrieved - defaults to all
     *                                  @see SpotCheckRefType
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run/{startYear}")
    public BaseResponse runReports(@RequestParam String[] reportType, @PathVariable String startYear ) {
        Set<SpotCheckRefType> refTypes = getSpotcheckRefTypes(reportType, "reportType");
        String endYear = String.valueOf( Integer.parseInt(startYear) + 2 );
        Range<LocalDateTime> reportRange = Range.closedOpen( LocalDateTime.parse(startYear +"-01-01T00:00:00"),LocalDateTime.parse(endYear+"-01-01T00:00:00") );
        for (SpotCheckRefType refType: refTypes) {
            spotcheckRunService.runReports(refType, reportRange);
        }
        refTypes.forEach(spotcheckRunService::runReports);
        return new ViewObjectResponse<>(ListView.ofStringList(
                refTypes.stream().map(SpotCheckRefType::toString).toList()),
                "spotcheck reports run");
    }

    /**
     * Spotcheck Interval Report Run API
     *
     * Attempts to run all spotcheck reports designated as interval reports
     *
     * Usage: (GET) /api/3/admin/spotcheck/run/interval
     *
     * Request Parameters:
     *          year - int - optional - The year to run interval reports for, defaults to current year.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run/interval")
    public BaseResponse runWeeklyReports(@RequestParam(required = false) Integer year) {
        int yr = year == null ? LocalDate.now().getYear() : year;
        spotcheckRunService.runIntervalReports(yr);
        return new SimpleResponse(true, "Interval Reports for " + yr + " have been run.", "report report");
    }

    /**
     * Spotcheck Calendar Interval Report Run API
     *
     * Attempts to run calendar spotcheck reports designated as interval reports
     *
     * Usage: (GET) /api/3/admin/spotcheck/run/interval/calendar
     *
     * Request Parameters:
     *          year - int - optional - The year to run interval reports for, defaults to current year.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run/interval/calendar")
    public BaseResponse runCalendarWeeklyReports(@RequestParam(required = false) Integer year) {
        int yr = year == null ? LocalDate.now().getYear() : year;
        spotcheckRunService.runIntervalReports(yr, SpotCheckRefType.LBDC_CALENDAR_ALERT);
        return new SimpleResponse(true, "Calendar Interval Reports for " + yr + " have been run.", "report report");
    }

    /**
     * Spotcheck Agenda Interval Report Run API
     *
     * Attempts to run Agenda spotcheck reports designated as interval reports
     *
     * Usage: (GET) /api/3/admin/spotcheck/run/interval/agenda
     *
     * Request Parameters:
     *          year - int - optional - The year to run interval reports for, defaults to current year.
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run/interval/agenda")
    public BaseResponse runAgendaWeeklyReports(@RequestParam(required = false) Integer year) {
        int yr = year == null ? LocalDate.now().getYear() : year;
        spotcheckRunService.runIntervalReports(yr, SpotCheckRefType.LBDC_AGENDA_ALERT);
        return new SimpleResponse(true, "Agenda Interval Reports for " + yr + " have been run.", "report report");
    }

    /** --- Internal Methods --- */

    private SpotCheckDataSource getDatasource(String datasource) {
        return getEnumParameter("datasource", datasource, SpotCheckDataSource.class);
    }

    private SpotCheckContentType getContentType(String contentType) {
        return getEnumParameter("contentType", contentType, SpotCheckContentType.class);
    }

    private MismatchStatus getMismatchStatus(String mismatchStatus) {
        return getEnumParameter("mismatchStatus", mismatchStatus, MismatchStatus.class);
    }

    private LocalDate getReportDate(String reportDate) {
        return reportDate == null ? LocalDate.now() : parseISODate(reportDate, "reportDate");
    }

    private EnumSet<SpotCheckMismatchType> getMismatchTypes(@RequestParam(required = false) String mismatchType) {
        return (mismatchType == null || mismatchType.equals("All")) ? EnumSet.allOf(SpotCheckMismatchType.class) : EnumSet.of(getEnumParameter("mismatchType", mismatchType, SpotCheckMismatchType.class));
    }

    private Set<SpotCheckMismatchIgnore> getIgnoredStatuses(@RequestParam(required = false) String[] ignoredStatuses) {
        return ignoredStatuses == null
                ? EnumSet.of(SpotCheckMismatchIgnore.NOT_IGNORED)
                : Lists.newArrayList(ignoredStatuses).stream()
                       .map(i -> getEnumParameter("ignoredStatuses", i, SpotCheckMismatchIgnore.class))
                       .collect(Collectors.toSet());
    }

    /**
     * Used to convert orderBy and sort request parameters into an OrderBy object.
     * Defaults to ordering by REFERENCE_DATE descending if orderByString and sortString are null.
     * When ordering by a field other than REFERENCE_DATE, a secondary order by on
     * REFERENCE_DATE desc is added so the most recent results are always displayed first.
     *
     * @param orderByString String representing a MismatchOrderBy value.
     * @param sortString String representing a SortOrder value.
     * @return An OrderBy representing the supplied orderByString and sortString, potentially
     * with a secondary order by of REFERENCE_DATE desc.
     * @throws InvalidRequestParamEx if orderByString or sortString
     * are not valid values.
     */
    private static OrderBy getOrderBy(String orderByString, String sortString) {
        MismatchOrderBy orderBy = orderByString == null
                ? MismatchOrderBy.REFERENCE_DATE
                : getEnumParameter("orderBy", orderByString, MismatchOrderBy.class);

        SortOrder sortOrder = sortString == null
                ? SortOrder.DESC
                : getEnumParameter("sort", sortString, SortOrder.class);

        if (orderBy != MismatchOrderBy.REFERENCE_DATE) {
            // Add secondary order by reference date
            return new OrderBy(orderBy.getColumnName(), sortOrder,
                               MismatchOrderBy.REFERENCE_DATE.getColumnName(), SortOrder.DESC);
        }
        return new OrderBy(orderBy.getColumnName(), sortOrder);
    }

    private static SpotCheckRefType getSpotcheckRefType(String parameter, String paramName) {
        SpotCheckRefType result = getEnumParameter(parameter, SpotCheckRefType.class, null);
        if (result == null) {
            result = getEnumParameterByValue(SpotCheckRefType.class, SpotCheckRefType::getByRefName,
                    SpotCheckRefType::getRefName, paramName, parameter);
        }
        return result;
    }

    private static Set<SpotCheckRefType> getSpotcheckRefTypes(String[] parameters, String paramName) {
        return parameters == null
                ? EnumSet.allOf(SpotCheckRefType.class)
                : Arrays.stream(parameters)
                        .map(param -> getSpotcheckRefType(param, paramName))
                        .collect(Collectors.toSet());
    }

}
