package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Range;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.service.process.DataProcessLogService;
import gov.nysenate.openleg.util.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/process")
public class DataProcessCtrl extends BaseCtrl
{
    @Autowired private DataProcessLogService processLogService;

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
    public Object getLogsDuring(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
                                @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
                                @RequestParam(defaultValue = "false") boolean activity, WebRequest webRequest) {
        LimitOffset limOff = getLimitOffset(webRequest, 100);
        return processLogService.getRuns(Range.closedOpen(from, to), limOff, activity);
    }
}
