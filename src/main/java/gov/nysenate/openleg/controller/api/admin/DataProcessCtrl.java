package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.ViewObjectResponse;
import gov.nysenate.openleg.client.response.error.ErrorCode;
import gov.nysenate.openleg.client.response.error.ErrorResponse;
import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.process.DataProcessRunDetailView;
import gov.nysenate.openleg.client.view.process.DataProcessRunView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.controller.api.base.InvalidRequestParamEx;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.service.process.DataProcessLogService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/process", method = RequestMethod.GET)
public class DataProcessCtrl extends BaseCtrl
{
    @Autowired private Environment env;
    @Autowired private DataProcessLogService processLogs;

    /**
     * Gets the process runs from a given date time.
     * @see #getRunsDuring(String, String, WebRequest)
     */
    @RequestMapping("/runs/{from}")
    public BaseResponse getRunsFrom(@PathVariable String from, WebRequest webRequest) throws InvalidRequestParamEx {
        return getRunsDuring(from, DateUtils.THE_FUTURE.atStartOfDay().toString(), webRequest);
    }

    /**
     * Data Process Runs API
     * -------------------------
     *
     * Get the process runs that occurred within a given data/time range.
     * Usage; (GET) /api/3/admin/process/runs/{from}/{to}
     *
     * Where 'from' and 'to' are date times.
     *
     * Optional Params: full (boolean) - If true, returns process runs with no activity as well
     *                  detail (boolean) - If true, returns the first hundred or so units for each run.
     *                  limit, offset (int) - Paginate through the runs.
     *
     * Expected Output: DataProcessRunDetailView if 'detail' = true, DataProcessRunView otherwise.
     */
    @RequestMapping("/runs/{from}/{to}")
    public BaseResponse getRunsDuring(@PathVariable String from, @PathVariable String to, WebRequest webRequest)
                                      throws InvalidRequestParamEx {
        LimitOffset limOff = getLimitOffset(webRequest, 100);
        LocalDateTime fromDateTime = parseISODateTimeParam(from, "from");
        LocalDateTime toDateTime = parseISODateTimeParam(to, "to");
        boolean full = getBooleanParam(webRequest, "full", false);
        boolean detail = getBooleanParam(webRequest, "detail", false);

        PaginatedList<DataProcessRun> runs = processLogs.getRuns(Range.closedOpen(fromDateTime, toDateTime), limOff, !full);
        return ListViewResponse.of(runs.getResults().stream()
            .map(run -> (detail)
                ? new DataProcessRunDetailView(run, processLogs.getUnits(run.getProcessId(), LimitOffset.HUNDRED))
                : new DataProcessRunView(run))
            .collect(toList()),
                runs.getTotal(), runs.getLimOff());
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
    @RequestMapping("/runs/id/{id}")
    public BaseResponse getRuns(@PathVariable int id, WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 100);
        Optional<DataProcessRun> run = processLogs.getRun(id);
        if (run.isPresent()) {
            return new ViewObjectResponse<>(
                new DataProcessRunDetailView(run.get(), processLogs.getUnits(run.get().getProcessId(), limOff)));
        }
        else {
            return new ErrorResponse(ErrorCode.PROCESS_RUN_NOT_FOUND);
        }
    }
}
