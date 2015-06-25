package gov.nysenate.openleg.service.spotcheck.base;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReferenceEvent;
import gov.nysenate.openleg.processor.base.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseSpotcheckProcessService implements ProcessService
{
    private static final Logger logger = LoggerFactory.getLogger(BaseSpotcheckProcessService.class);

    @Autowired SpotCheckNotificationService spotCheckNotificationService;

    @Autowired EventBus eventBus;

    @Autowired Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public int collate() {
        try {
            if (environment.isSpotcheckScheduled()) {
                return doCollate();
            }
            return 0;
        } catch (Exception ex) {
            spotCheckNotificationService.handleSpotcheckException(ex, false);
            return 0;
        }
    }

    /**
     * @see #collate()
     */
    protected abstract int doCollate() throws Exception;

    /**
     * {@inheritDoc}
     */
    @Override
    public int ingest() {
        try {
            if (environment.isSpotcheckScheduled()) {
                int ingestCount = doIngest();
                registerReferenceEvent();
                return ingestCount;
            }
            return 0;
        } catch (Exception ex) {
            spotCheckNotificationService.handleSpotcheckException(ex, false);
            return 0;
        }
    }

    /**
     * @see #ingest()
     */
    protected abstract int doIngest() throws Exception;

    /**
     * @return SpotCheckRefType - the type of reference that is generated on the ingest step
     */
    protected abstract SpotCheckRefType getRefType();

    protected abstract int getUncheckedRefCount();

    private void registerReferenceEvent() {
        if (getUncheckedRefCount() > 0) {
            eventBus.post(new SpotCheckReferenceEvent(getRefType()));
        }
    }

}
