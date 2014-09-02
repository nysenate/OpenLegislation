package gov.nysenate.openleg.controller.api.spotcheck;

import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.service.spotcheck.DaybreakCheckReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_API_PATH;
import static java.util.stream.Collectors.toList;
import static org.springframework.format.annotation.DateTimeFormat.*;

@RestController
@RequestMapping(value = BASE_API_PATH + "/spotcheck", method = RequestMethod.GET)
public class SpotCheckGetCtrl extends BaseCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(SpotCheckGetCtrl.class);

    @Autowired
    DaybreakCheckReportService daybreakService;

    @RequestMapping(value = "/daybreaks/{from}/{to}")
    public List<SpotCheckReport<BaseBillId>> getDaybreakReport(
           @PathVariable @DateTimeFormat(iso = ISO.DATE) LocalDate from,
           @PathVariable @DateTimeFormat(iso = ISO.DATE) LocalDate to) {
        logger.info("Retrieving daybreak reports from {} to {}", from , to);
        return
            daybreakService.getReportIds(from.atStartOfDay(), to.atTime(23,59,59), SortOrder.DESC, LimitOffset.FIFTY)
                .parallelStream()
                .map(daybreakService::getReport).collect(toList());
    }


    @RequestMapping(value = "/daybreak/{reportDateTime}")
    public SpotCheckReport<BaseBillId> getDaybreakReport(
           @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) LocalDateTime reportDateTime) {
        SpotCheckReport<BaseBillId> daybreakReport =
            daybreakService.getReport(new SpotCheckReportId(SpotCheckRefType.LBDC_DAYBREAK, reportDateTime));
        return daybreakReport;
    }



}
