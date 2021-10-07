package gov.nysenate.openleg.spotchecks.sensite;

import gov.nysenate.openleg.spotchecks.sensite.bill.SenateSiteDao;
import gov.nysenate.openleg.spotchecks.base.BaseSpotcheckProcessService;
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
    protected int doCollate() throws Exception {
        return 0;
    }

    @Override
    protected int doIngest() throws Exception {
        return getUncheckedRefCount();
    }

    @Override
    protected int getUncheckedRefCount() {
        try {
            return (int) senateSiteDao.getPendingDumps(getRefType()).stream()
                    .filter(SenateSiteDump::isComplete)
                    .count();
        } catch (IOException e) {
            throw new IllegalStateException("Error while looking for " + getRefType() + " dumps", e);
        }
    }

}
