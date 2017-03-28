package gov.nysenate.openleg.controller.api.admin;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.client.response.base.BaseResponse;
import gov.nysenate.openleg.client.response.base.SimpleResponse;
import gov.nysenate.openleg.controller.api.base.BaseCtrl;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckRunService;
import gov.nysenate.openleg.service.spotcheck.openleg.OpenlegBillReportService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

import static gov.nysenate.openleg.controller.api.base.BaseCtrl.BASE_ADMIN_API_PATH;

/**
 * Created by Chenguang He on 2017/3/27.
 * This api controller is used to give the access of openleg checking service.
 * It generates the report of difference between openleg dev and xml-processing bills.
 */

@RestController
@RequestMapping(value = BASE_ADMIN_API_PATH + "/openlegspotcheck")
public class OpenlegSpotCheckCtrl extends BaseCtrl {
    private static final Logger logger = LoggerFactory.getLogger(OpenlegSpotCheckCtrl.class);

    @Autowired
    private EventBus eventBus;

    @Autowired
    private SpotcheckRunService spotcheckRunService;

    @Autowired
    OpenlegBillReportService openlegBillReportService;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    @RequiresPermissions("admin:spotcheck")
    @RequestMapping(value = "/{sessionYear}", method = RequestMethod.GET)
    public BaseResponse performSpotCheck(@PathVariable String sessionYear) {
        try {
            logger.info("Running Openleg Spotcheck with session year: " + sessionYear);
            spotcheckRunService.runReports(openlegBillReportService.getSpotcheckRefType(), Range.closed(LocalDateTime.of(Integer.valueOf(sessionYear),1,1,1,1), LocalDateTime.of(Integer.valueOf(sessionYear),1,1,1,1)));
            return new SimpleResponse(true, "Successful Running Openleg Spotcheck", " Openleg Spotcheck");
        }
         catch (Exception e) {
            e.printStackTrace();
        }

        return new SimpleResponse(false, "Failed Running Openleg Spotcheck", " Openleg Spotcheck");
    }

}
