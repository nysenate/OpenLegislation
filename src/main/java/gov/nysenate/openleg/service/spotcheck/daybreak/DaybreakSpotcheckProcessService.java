package gov.nysenate.openleg.service.spotcheck.daybreak;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DaybreakSpotcheckProcessService extends BaseSpotcheckProcessService<BaseBillId> {

    private static final Logger logger = LoggerFactory.getLogger(DaybreakSpotcheckProcessService.class);

    @Autowired
    DaybreakCheckMailService checkMailService;

    @Autowired
    DaybreakDao daybreakDao;

    @Autowired
    DaybreakProcessService daybreakProcessService;

    @Autowired
    Environment env;

    @Autowired
    EventBus eventBus;

    /** {@inheritDoc} */
    @Override
    protected int doCollate() {
        int reports = checkMailService.checkMail();
        daybreakProcessService.collateDaybreakReports();
        return reports;
    }

    @Override
    protected int doIngest() throws Exception {
        return daybreakProcessService.processPendingFragments();
    }

    @Override
    public String getIngestType() {
        return "daybreak-bill";
    }

    @Override
    public String getCollateType() {
        return "daybreak-report";
    }
}
