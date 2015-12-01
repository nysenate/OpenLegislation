package gov.nysenate.openleg.service.spotcheck.base;

import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

public class SenateSiteBillSpotcheckProcessService extends BaseSpotcheckProcessService {
    @Override
    public String getCollateType() {
        return "senate-site-bill";
    }

    @Override
    protected int doCollate() throws Exception {
        return 0;
    }

    @Override
    protected int doIngest() throws Exception {
        // There is no ingest phase since the references are not stored
        return 0;
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.SENATE_SITE_BILLS;
    }

    @Override
    protected int getUncheckedRefCount() {
        return 0;
    }
}
