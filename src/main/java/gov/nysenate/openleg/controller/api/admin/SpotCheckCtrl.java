package gov.nysenate.openleg.controller.api.admin;

import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
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
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.DaybreakCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;
import static gov.nysenate.openleg.util.DateUtils.atEndOfDay;
import static java.util.stream.Collectors.toList;
import static org.springframework.format.annotation.DateTimeFormat.ISO;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/spotcheck", produces = APPLICATION_JSON_VALUE)
public class SpotCheckCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckCtrl.class);

    @Autowired private DaybreakCheckReportService daybreakService;
    @Autowired private Environment env;

    /**
     * Toggle Scheduled SpotChecks API
     *
     * TODO
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse toggleScheduling(@RequestParam(required = true) boolean scheduledReports,
                                         @RequestParam(required = true) boolean scheduledCheckMail) {
        env.setSpotcheckScheduled(scheduledReports);
        env.setCheckMailScheduled(scheduledCheckMail);
        return new SimpleResponse(true,
            "Scheduled reports: " + env.isSpotcheckScheduled() + ", Scheduled CheckMail: " + env.isCheckMailScheduled(),
            "spotcheck-enable-response");
    }

    /**
     * Daybreak Report Summary Retrieval API
     *
     * Get a list of daybreak reports that have been run in the past six months.
     * Usage: (GET) /api/3/admin/spotcheck/daybreaks
     *
     * Expected Output: ReportSummaryResponse
     */
    @RequestMapping(value = "/daybreaks", method = RequestMethod.GET)
    public BaseResponse getDaybreakReport() {
        LocalDate today = LocalDate.now();
        LocalDate sixMonthsAgo = today.minusMonths(6);
        return getDaybreakReportLimOff(sixMonthsAgo, today);
    }

    /**
     * Daybreak Report Summary Retrieval API
     *
     * Get a list of daybreak reports that have been run during the given dates.
     * Usage: (GET) /api/3/admin/spotcheck/daybreaks/{from}/{to}
     *
     * where 'from' and 'to' are ISO dates, e.g. 2014-12-01
     *
     * Expected Output: ReportSummaryResponse
     */
    @RequestMapping(value = "/daybreaks/{from}/{to}",method = RequestMethod.GET)
    public BaseResponse getDaybreakReportLimOff(
            @PathVariable @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @PathVariable @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        logger.info("Retrieving daybreak reports from {} to {}", from , to);
        // Have to retrieve the reports in full, no 'summary' view available
        List<SpotCheckReport<BaseBillId>> reports =
                daybreakService.getReportIds(from.atStartOfDay(), atEndOfDay(to), SortOrder.DESC, LimitOffset.ALL)
                        .parallelStream()
                        .map(daybreakService::getReport).collect(toList());
        // Construct the client response
        return new ReportSummaryResponse<>(
                ListView.of(reports.stream()
                        .map(r -> new ReportInfoView<>(r))
                        .collect(toList())), from, to);
    }

    /**
     * Daybreak Report Retrieval API
     *
     * Get a single daybreak report which is identified by the report's run date/time.
     * Usage: (GET) /api/3/admin/spotcheck/daybreaks/{reportDateTime}
     *
     * where 'reportDateTime' is an ISO Date/time.
     *
     * Expected Output: ReportDetailResponse
     */
    @RequestMapping(value = "/daybreaks/{reportDateTime}", method = RequestMethod.GET)
    public BaseResponse getDaybreakReport(
            @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime reportDateTime) {
        logger.info("Retrieving daybreak report {}", reportDateTime);
        return new ReportDetailResponse<>(
                daybreakService.getReport(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, reportDateTime)));
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
}