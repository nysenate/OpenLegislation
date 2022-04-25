package gov.nysenate.openleg.spotchecks.sensite;

import gov.nysenate.openleg.spotchecks.base.BaseSpotcheckProcessService;
import gov.nysenate.openleg.spotchecks.sensite.bill.SenateSiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public abstract class SenateSiteSpotcheckProcessService extends BaseSpotcheckProcessService {

    @Autowired private SenateSiteDao senateSiteDao;

    @Override
    public String getCollateType() {
        return getRefType() + " spotcheck dump";
    }

    @Override
    protected int doCollate() {
        return 0;
    }

    @Override
    protected int doIngest() {
        return getUncheckedRefCount();
    }

    @Override
    protected int getUncheckedRefCount() {
        try {
            return (int) senateSiteDao.getPendingDumps(getRefType()).stream()
                    .filter(SenateSiteDump::isComplete)
                    .count();
        } catch (IOException e) {
            throw new IllegalStateException("Error while looking for " + getRefType() + " dumps."
                    + "\n\nSource error is:", e);
        }
    }

}
