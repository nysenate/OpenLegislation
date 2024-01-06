package gov.nysenate.openleg.api.processor;

import com.google.common.collect.Range;
import gov.nysenate.openleg.api.BaseCtrl;
import gov.nysenate.openleg.api.InvalidRequestParamEx;
import gov.nysenate.openleg.api.processor.view.DataProcessRunDetailView;
import gov.nysenate.openleg.api.processor.view.DataProcessRunInfoView;
import gov.nysenate.openleg.api.processor.view.DataProcessRunView;
import gov.nysenate.openleg.api.response.BaseResponse;
import gov.nysenate.openleg.api.response.ListViewResponse;
import gov.nysenate.openleg.api.response.ViewObjectResponse;
import gov.nysenate.openleg.api.response.error.ErrorCode;
import gov.nysenate.openleg.api.response.error.ErrorResponse;
import gov.nysenate.openleg.api.response.error.ViewObjectErrorResponse;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.processors.DataProcessor;
import gov.nysenate.openleg.processors.log.DataProcessLogService;
import gov.nysenate.openleg.processors.log.DataProcessRun;
import gov.nysenate.openleg.processors.log.DataProcessRunInfo;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static gov.nysenate.openleg.api.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/process", method = RequestMethod.GET)
public class DataProcessCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(DataProcessCtrl.class);
    private final DataProcessLogService processLogs;
    private final DataProcessor dataProcessor;

    @Autowired
    public DataProcessCtrl(DataProcessLogService processLogs, DataProcessor dataProcessor) {
        this.processLogs = processLogs;
        this.dataProcessor = dataProcessor;
    }

    /**
     * Data Process API
     * ----------------
     *
     * Triggers a data processing run
     * Usage: (POST) /api/3/admin/process/run
     *
     * Expected Output: DataProcessRunView if the run was successful, ErrorResponse otherwise
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping(value = "/run", method = RequestMethod.POST)
    public BaseResponse triggerDataProcess() {
        try {
            DataProcessRun run = dataProcessor.run("api", true);
            if (run != null) {
                return new ViewObjectResponse<>(new DataProcessRunView(run), "run started");
            }
            return new ErrorResponse(ErrorCode.DATA_PROCESS_RUN_FAILED);
        } catch (Exception ex) {
            logger.error("DataProcess exception: \n{}", ex.getMessage());
            return new ViewObjectErrorResponse(ErrorCode.DATA_PROCESS_RUN_FAILED, ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Data Process Runs API
     * ---------------------
     *
     * Get the process runs that occurred within a given data/time range.
     * Usage;
     * (GET) /api/3/admin/process/runs
     * (GET) /api/3/admin/process/runs/{from}
     * (GET) /api/3/admin/process/runs/{from}/{to}
     *
     * Where 'from' and 'to' are date times.
     *
     * Optional Params: full (boolean) - If true, returns process runs with no activity as well
     *                  detail (boolean) - If true, returns the first hundred or so units for each run.
     *                  limit, offset (int) - Paginate through the runs.
     *
     * Expected Output: DataProcessRunDetailView if 'detail' = true, DataProcessRunView otherwise.
     */

    /**
     * Gets the process runs from the past week.
     * @see #getRunsDuring(String, String, WebRequest)
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping("/runs")
    public BaseResponse getRecentRuns(WebRequest request) throws InvalidRequestParamEx {
        return getRunsDuring(LocalDateTime.now().minusDays(7), LocalDateTime.now(), request);
    }

    /**
     * Gets the process runs from a given date time.
     * @see #getRunsDuring(String, String, WebRequest)
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping("/runs/{from}")
    public BaseResponse getRunsFrom(@PathVariable String from, WebRequest request) throws InvalidRequestParamEx {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        return getRunsDuring(fromDateTime, LocalDateTime.now(), request);
    }

    @RequiresPermissions("admin:dataProcess")
    @RequestMapping("/runs/{from}/{to}")
    public BaseResponse getRunsDuring(@PathVariable String from, @PathVariable String to, WebRequest request)
                                      throws InvalidRequestParamEx {
        LocalDateTime fromDateTime = parseISODateTime(from, "from");
        LocalDateTime toDateTime = parseISODateTime(to, "to");
        return getRunsDuring(fromDateTime, toDateTime, request);
    }

    private BaseResponse getRunsDuring(LocalDateTime fromDateTime, LocalDateTime toDateTime, WebRequest request) {
        LimitOffset limOff = getLimitOffset(request, 100);
        boolean full = getBooleanParam(request, "full", false);
        boolean detail = getBooleanParam(request, "detail", false);

        PaginatedList<DataProcessRunInfo> runs = processLogs.getRunInfos(Range.closedOpen(fromDateTime, toDateTime), limOff, !full);
        return ListViewResponse.of(runs.results().stream()
            .map(runInfo -> (detail)
                    ? new DataProcessRunDetailView(runInfo, processLogs.getUnits(runInfo.getRun().getProcessId(), LimitOffset.FIFTY))
                    : new DataProcessRunInfoView(runInfo))
            .toList(),
            runs.total(), runs.limOff());
    }

    /**
     * Single Data Process Run API
     * ---------------------------
     *
     * Get a single data process run via the process id (int).
     * Usage: (GET) /api/3/admin/process/runs/{id}
     *
     * Optional Params: limit, offset (int) - Paginate through the units associated with this run.
     *
     * Expected Output: DataProcessRunDetailView
     */
    @RequiresPermissions("admin:dataProcess")
    @RequestMapping("/runs/id/{id:\\d+}")
    public BaseResponse getRuns(@PathVariable int id, WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 100);
        Optional<DataProcessRunInfo> runInfo = processLogs.getRunInfo(id);
        if (runInfo.isPresent()) {
            return new ViewObjectResponse<>(
                new DataProcessRunDetailView(runInfo.get(), processLogs.getUnits(runInfo.get().getRun().getProcessId(), limOff)));
        }
        else {
            return new ErrorResponse(ErrorCode.PROCESS_RUN_NOT_FOUND);
        }
    }
}
