package gov.nysenate.openleg.processors;

public abstract class AbstractLegDataProcessor extends AbstractDataProcessor implements LegDataProcessor {
    @Override
    public void postProcess() {
        flushBillUpdates();
    }

    @Override
    public void checkIngestCache() {
        if (!env.isLegDataBatchEnabled() || billIngestCache.exceedsCapacity()) {
            flushBillUpdates();
        }
    }
}
