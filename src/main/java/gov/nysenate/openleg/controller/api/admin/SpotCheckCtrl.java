package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.response.spotcheck.OpenMismatchesResponse;
import gov.nysenate.openleg.client.response.spotcheck.ReportDetailResponse;
import gov.nysenate.openleg.client.response.spotcheck.ReportSummaryResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.spotcheck.OpenMismatchSummaryView;
import gov.nysenate.openleg.client.view.spotcheck.ReportIdView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.spotcheck.MismatchOrderBy;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck", produces = APPLICATION_JSON_VALUE)
public class SpotCheckCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCtrl.class);

    @Autowired private List<SpotCheckReportService<?>> reportServices;
    @Autowired private SpotcheckRunService spotcheckRunService;

    private ImmutableMap<SpotCheckRefType, SpotCheckReportService<?>> reportServiceMap;

    @PostConstruct
    public void init() {
        reportServiceMap = ImmutableMap.copyOf(
                reportServices.stream()
                        .collect(Collectors.toMap(SpotCheckReportService::getSpotcheckRefType, Function.identity(),(a, b) -> b)));
    }

    /**
     * SpotCheck Report Summary Retrieval API
     *
     * Get a list of spotcheck reports that have been run in the past six months.
     * Usage: (GET) /api/3/admin/spotcheck/summaries/{from}/{to}
     *        (GET) /api/3/admin/spotcheck/summaries
     *        (GET) /api/3/admin/{reportType}/summaries/{from}/{to}
     *        (GET) /api/3/admin/{reportType}/summaries
     *
     * Request Parameters: reportType - string[] or string (in path variable) - specifies which kinds of report summaries
     *                                  are retrieved - defaults to all
     *                                  @see SpotCheckRefType
     *                     order - ASC|DESC - the report date sort order of the return summaries - default DESC
     *
     * Expected Output: ReportSummaryResponse
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/summaries/{from}/{to}")
    public BaseResponse getReportSummaries(@RequestParam(required = false) String reportType,
                                           @PathVariable String from,
                                           @PathVariable String to,
                                           WebRequest webRequest) {
        logger.debug("Retrieving daybreak reports from {} to {}", from, to);
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        SortOrder order = getSortOrder(webRequest, SortOrder.DESC);
        SpotCheckRefType refType = reportType != null ? getSpotcheckRefType(reportType, "reportType") : null;

        SpotCheckReportService<?> reportService = reportServiceMap.values().asList().get(0);

        List<SpotCheckReportSummary> summaries =
                reportService.getReportSummaries(refType, fromDateTime, toDateTime, order);

        // Construct the client response
        return new ReportSummaryResponse<>(
                ListView.of(summaries.stream()
                        .map(ReportInfoView::new)
                        .collect(Collectors.toList())), fromDateTime, toDateTime, summaries.size(), LimitOffset.ALL);
    }

    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/summaries")
    public BaseResponse getReportSummaries(@RequestParam(required = false) String reportType, WebRequest request) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = today.minusMonths(6);
        return getReportSummaries(reportType, sixMonthsAgo.toString(), today.toString(), request);
    }

    /**
     * Spotcheck Report Retrieval API
     *
     * Get a single spotcheck report which is identified by the report's run date/time and report type.
     * Usage: (GET) /api/3/admin/spotcheck/{reportType}/{reportDateTime}
     *
     * where 'reportDateTime' is an ISO Date/time.
     * @see SpotCheckRefType for possible values of 'reportType'
     *
     * Expected Output: ReportDetailResponse
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/{reportType}/{reportDateTime:.+}", method = RequestMethod.GET)
    public BaseResponse getReport(@PathVariable String reportType,
                                  @PathVariable String reportDateTime) {
        logger.debug("Retrieving {} report {}", reportType, reportDateTime);
        SpotCheckRefType refType = getSpotcheckRefType(reportType, "reportType");
        return new ReportDetailResponse<>(
                reportServiceMap.get(refType)
                        .getReport(new SpotCheckReportId(refType, parseISODateTime(reportDateTime, "reportDateTime"))));
    }

    /**
     * Spotcheck Open Observations API
     *
     * Queries spotcheck observations with open mismatches for a specific report type
     *
     * Usage: (GET) /api/3/admin/spotcheck/open-mismatches
     *
     * Request Parameters: reportType - string - the reference type of the mismatches to be retrieved
     *                     mismatchTypes - string[] - optional - only retrieves mismatches for the specified types if present
     *                     orderBy - string (ASC|DESC) - optional, default DESC - determines order of returned observations
     *                     observedAfter - string (ISO date) - optional - only returns observations with mismatches after
     *                          the given date if present
     *                     resolvedShown - boolean - optional, default false - will return resolved mismatches if true
     *                     ignoredShown - boolean - optional, default false - will return ignored mismatches if true
     *                     ignoredOnly - boolean - optional, default false - returns only ignored mismatches if true
     *                          will override ignoredShown=false if set to true
     *                     trackedShown - boolean - optional, default true - will return tracked issues if set to true
     *                     untrackedShown - boolean - optional, default true - will return untracked issues if set to true
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/open-mismatches", method = RequestMethod.GET)
    public BaseResponse getOpenMismatches(@RequestParam String reportType,
                                          @RequestParam(required = false) String[] mismatchType,
                                          @RequestParam(required = false) String orderBy,
                                          @RequestParam(required = false) String observedAfter,
                                          @RequestParam(defaultValue = "false") boolean resolvedShown,
                                          @RequestParam(defaultValue = "false") boolean ignoredShown,
                                          @RequestParam(defaultValue = "false") boolean ignoredOnly,
                                          @RequestParam(defaultValue = "true") boolean trackedShown,
                                          @RequestParam(defaultValue = "true") boolean untrackedShown,
                                          WebRequest request) {
        SpotCheckRefType refType = getSpotcheckRefType(reportType, "reportType");
        Set<SpotCheckRefType> refTypes = Collections.singleton(refType);
        LimitOffset limOff = getLimitOffset(request, 0);
        MismatchOrderBy mismatchOrderBy = getEnumParameter(orderBy, MismatchOrderBy.class, MismatchOrderBy.OBSERVED_DATE);
        SortOrder order = getSortOrder(request, SortOrder.DESC);
        Set<SpotCheckMismatchType> mismatchTypes = getSpotcheckMismatchTypes(mismatchType, "mismatchType", refTypes);
        LocalDateTime earliestDateTime = parseISODateTime(observedAfter, DateUtils.LONG_AGO.atStartOfDay());
        OpenMismatchQuery query = new OpenMismatchQuery(refTypes, mismatchTypes, earliestDateTime,
                mismatchOrderBy, order, limOff, resolvedShown, ignoredShown, ignoredOnly, trackedShown, untrackedShown);
        SpotCheckOpenMismatches<?> observations = reportServiceMap.get(refType).getOpenObservations(query);
        OpenMismatchSummary summary = getAnyReportService().getOpenMismatchSummary(refTypes, earliestDateTime);
        return new OpenMismatchesResponse<>(observations, summary, query);
    }

    /**
     * Spotcheck Open Observations Summary API
     *
     * Get a summary of spotcheck observations with open mismatches for a specific report type
     *
     * Usage: (GET) /api/3/admin/spotcheck/open-mismatches/summary
     *
     * Request Parameters: reportType - string - the reference type of the mismatches to be retrieved
     *                     observedAfter - string (ISO date) - optional - only returns observations with mismatches after
     *                          the given date if present
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/open-mismatches/summary", method = RequestMethod.GET)
    public BaseResponse getOpenMismatchSummary(@RequestParam(required = false) String[] reportType,
                                          @RequestParam(required = false) String observedAfter) {
        Set<SpotCheckRefType> refTypes = getSpotcheckRefTypes(reportType, "reportType");
        LocalDateTime earliestDateTime = parseISODateTime(observedAfter, DateUtils.LONG_AGO.atStartOfDay());
        OpenMismatchSummary summary = getAnyReportService().getOpenMismatchSummary(refTypes, earliestDateTime);
        return new ViewObjectResponse<>(new OpenMismatchSummaryView(summary));
    }

    /**
     * Spotcheck Mismatch Ignore API
     *
     * Set the ignore status of a particular mismatch
     *
     * Usage: (POST) /api/3/admin/spotcheck/mismatch/{mismatchId}/ignore
     *
     * Request Parameters: ignoreLevel - string - specifies desired ignore level or unsets ignore if null or not present
     *                                  @see SpotCheckMismatchIgnore
     */
    @RequestMapping(value = "/mismatch/{mismatchId:\\d+}/ignore", method = RequestMethod.POST)
    public BaseResponse setIgnoreStatus(@PathVariable int mismatchId, @RequestParam(required = false) String ignoreLevel) {
        SpotCheckMismatchIgnore ignoreStatus = ignoreLevel != null
                ? getEnumParameter("ignoreLevel", ignoreLevel, SpotCheckMismatchIgnore.class)
                : null;
        getAnyReportService().setMismatchIgnoreStatus(mismatchId, ignoreStatus);
        return new SimpleResponse(true, "ignore level set", "ignore-level-set");
    }

    /**
     * Spotcheck Mismatch Add Issue Id API
     *
     * Adds an issue id to a spotcheck mismatch
     *
     * Usage: (POST) /api/3/admin/spotcheck/mismatch/{mismatchId}/issue/{issueId}
     */
    @RequestMapping(value = "/mismatch/{mismatchId:\\d+}/issue/{issueId}", method = RequestMethod.POST)
    public BaseResponse addMismatchIssueId(@PathVariable int mismatchId, @PathVariable String issueId) {
        getAnyReportService().addIssueId(mismatchId, issueId);
        return new SimpleResponse(true, "issue id added", "issue-id-added");
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
        getAnyReportService().deleteIssueId(mismatchId, issueId);
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
                refTypes.stream().map(SpotCheckRefType::toString).collect(Collectors.toList())),
                "spotcheck reports run");
    }

    /**
     * Spotcheck Weekly Report Run API
     *
     * Attempts to run all spotcheck reports designated as weekly reports
     *
     * Usage: (GET) /api/3/admin/spotcheck/run/weekly
     */
    @RequiresPermissions("admin:view")
    @RequestMapping(value = "/run/weekly")
    public BaseResponse runWeeklyReports() {
        spotcheckRunService.runWeeklyReports();
        return new SimpleResponse(true, "weekly reports run", "report report");
    }

    /** --- Exception Handlers --- */

    /**
     * Handles cases where a query for a daybreak report that doesn't exist was made.
     */
    @ExceptionHandler(SpotCheckReportNotFoundEx.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorResponse handleSpotCheckReportNotFoundEx(SpotCheckReportNotFoundEx ex) {
        return new ViewObjectErrorResponse(ErrorCode.SPOTCHECK_REPORT_NOT_FOUND, new ReportIdView(ex.getReportId()));
    }

    /** --- Internal Methods --- */

    private SpotCheckReportService<?> getAnyReportService() {
        return reportServices.stream().findAny()
                .orElseThrow(() -> new IllegalStateException("No spotcheck report services found"));
    }

    private SpotCheckRefType getSpotcheckRefType(String parameter, String paramName) {
        SpotCheckRefType result = getEnumParameter(parameter, SpotCheckRefType.class, null);
        if (result == null) {
            result = getEnumParameterByValue(SpotCheckRefType.class, SpotCheckRefType::getByRefName,
                    SpotCheckRefType::getRefName, paramName, parameter);
        }
        return result;
    }

    private Set<SpotCheckRefType> getSpotcheckRefTypes(String[] parameters, String paramName) {
        return parameters == null
                ? EnumSet.allOf(SpotCheckRefType.class)
                : Arrays.asList(parameters).stream()
                        .map(param -> getSpotcheckRefType(param, paramName))
                        .collect(Collectors.toSet());
    }

    private Set<SpotCheckMismatchType> getSpotcheckMismatchTypes(String[] parameters, String paramName,
                                                                 Set<SpotCheckRefType> refTypes) {
        return parameters == null
                ? refTypes.stream()
                        .flatMap(refType -> SpotCheckMismatchType.getMismatchTypes(refType).stream())
                        .collect(Collectors.toSet())
                : Arrays.asList(parameters).stream()
                        .map(paramValue -> getEnumParameter(paramName, paramValue, SpotCheckMismatchType.class))
                        .collect(Collectors.toSet());
    }

}