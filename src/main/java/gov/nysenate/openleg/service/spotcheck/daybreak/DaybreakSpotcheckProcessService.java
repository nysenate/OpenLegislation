package gov.nysenate.openleg.service.spotcheck.daybreak;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.bill.reference.daybreak.DaybreakDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.processor.daybreak.DaybreakProcessService;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DaybreakSpotcheckProcessService extends BaseSpotcheckProcessService {

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
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_DAYBREAK;
    }

    @Override
    protected int getUncheckedRefCount() {
        try {
            return daybreakDao.isChecked(daybreakDao.getCurrentReportDate()) ? 0 : 1;
        } catch (DataAccessException ex) {
            return 0;
        }
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
