package gov.nysenate.openleg.service.spotcheck.senatesite.base;

import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
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
