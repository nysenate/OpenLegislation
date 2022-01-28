package gov.nysenate.openleg.spotchecks.daybreak;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMailProcessService;
import gov.nysenate.openleg.spotchecks.daybreak.bill.DaybreakDao;
import gov.nysenate.openleg.spotchecks.daybreak.process.DaybreakProcessService;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class DaybreakSpotcheckProcessService extends SpotcheckMailProcessService {

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
