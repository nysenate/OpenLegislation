package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.ListViewResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.client.view.base.ModelView;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.model.base.Environment;
import gov.nysenate.openleg.model.process.DataProcessRun;
import gov.nysenate.openleg.service.process.DataProcessLogService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/process")
public class DataProcessCtrl extends BaseCtrl
{
    @Autowired private Environment env;
    @Autowired private DataProcessLogService processLogService;

    /**
     * Enable/Disable Data Processing API
     *
     *
     *
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public BaseResponse setProcessEnabled(@RequestParam(required = true) boolean enabled,
                                          @RequestParam(required = true) boolean scheduled) {
        env.setProcessingEnabled(enabled);
        env.setProcessingScheduled(scheduled);
        return new SimpleResponse(true,
                "Set data processing to: " + enabled + " and scheduling to: " + scheduled, "process-enable");
    }

    /**
     *
     *
     */
    @RequestMapping("/logs/{from}")
    public Object getLogsFrom(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                              @RequestParam(defaultValue = "false") boolean activity, WebRequest webRequest) {
        return getLogsDuring(from, DateUtils.THE_FUTURE.atStartOfDay(), activity, webRequest);
    }

    /**
     *
     */
    @RequestMapping("/logs/{from}/{to}")
    public BaseResponse getLogsDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(defaultValue = "false") boolean activity, WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 100);
        PaginatedList<DataProcessRun> runs = processLogService.getRuns(Range.closedOpen(from, to), limOff, activity);
        return ListViewResponse.of(runs.getResults().stream().map(run -> new ModelView<>(run)).collect(toList()),
            runs.getTotal(), runs.getLimOff());
    }
}
