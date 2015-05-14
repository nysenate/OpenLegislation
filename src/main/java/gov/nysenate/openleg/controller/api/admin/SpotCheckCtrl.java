package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.client.response.spotcheck.ReportDetailResponse;
import gov.nysenate.openleg.client.response.spotcheck.ReportSummaryResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.spotcheck.ReportIdView;
import gov.nysenate.openleg.client.view.spotcheck.ReportInfoView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
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
import static gov.nysenate.openleg.util.DateUtils.toDate;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck", produces = APPLICATION_JSON_VALUE)
public class SpotCheckCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCtrl.class);

    @Autowired private Environment env;
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
     * Toggle Scheduled SpotChecks API
     *
     * TODO move to environment api
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse toggleScheduling(@RequestParam(required = true) boolean scheduledReports) {
        env.setSpotcheckScheduled(scheduledReports);
        return new SimpleResponse(true,
            "Scheduled reports: " + env.isSpotcheckScheduled(), "spotcheck-enable-response");
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
    @RequestMapping(value = "/summaries/{from}/{to}")
    public BaseResponse getReportSummaries(@RequestParam(required = false) String[] reportType,
                                           @PathVariable String from,
                                           @PathVariable String to,
                                           WebRequest webRequest) {
        logger.debug("Retrieving daybreak reports from {} to {}", from, to);
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        SortOrder order = getSortOrder(webRequest, SortOrder.DESC);
        Set<SpotCheckRefType> refTypes = getSpotcheckRefTypes(reportType, "reportType");

        List<SpotCheckReport<?>> reports = new ArrayList<>();
        refTypes.forEach(refType -> {
            SpotCheckReportService<?> reportService = reportServiceMap.get(refType);
            if (reportService != null) {
                reportService.getReportIds(fromDateTime, toDateTime, order, LimitOffset.ALL)
                        .parallelStream()
                        .map(reportService::getReport)
                        .forEach(reports::add);
            }
        });
        reports.sort((a, b) -> SortOrder.ASC.equals(order)
                ? a.getReportDateTime().compareTo(b.getReportDateTime())
                : b.getReportDateTime().compareTo(a.getReportDateTime()));

        // Construct the client response
        return new ReportSummaryResponse<>(
                ListView.of(reports.stream()
                        .map(ReportInfoView::new)
                        .collect(Collectors.toList())), fromDateTime, toDateTime);
    }
    @RequestMapping(value = "/summaries")
    public BaseResponse getReportSummaries(@RequestParam(required = false) String[] reportType, WebRequest request) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime sixMonthsAgo = today.minusMonths(6);
        return getReportSummaries(reportType, sixMonthsAgo.toString(), today.toString(), request);
    }
    @RequestMapping(value = "/{reportType}/{from}/{to}",method = RequestMethod.GET)
    public BaseResponse getReportSummaries(
            @PathVariable String reportType,
            @PathVariable String from,
            @PathVariable String to, WebRequest request) {
        return getReportSummaries(new String[]{reportType}, from, to, request);
    }
    @RequestMapping(value = "/{reportType}", method = RequestMethod.GET)
    public BaseResponse getReportSummaries(@PathVariable String reportType, WebRequest request) {
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
}