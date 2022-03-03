package gov.nysenate.openleg.spotchecks.base;

import gov.nysenate.openleg.config.OpenLegEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A {@link BaseSpotcheckProcessService} for email based spotchecks
 * Allows email checking to be disabled.
 */
public abstract class SpotcheckMailProcessService extends BaseSpotcheckProcessService {

    @Autowired private OpenLegEnvironment environment;

    @Override
    public int collate() {
        if (environment.isCheckmailEnabled()) {
            return super.collate();
        }
        return 0;
    }

    @Override
    public int ingest() {
        if (environment.isCheckmailEnabled()) {
            return super.ingest();
        }
        return 0;
    }
}
