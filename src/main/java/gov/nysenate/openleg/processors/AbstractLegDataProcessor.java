package gov.nysenate.openleg.processors;

import java.time.format.DateTimeFormatter;

public abstract class AbstractLegDataProcessor extends AbstractDataProcessor implements LegDataProcessor {
    /** Date format found in SobiBlock[V] vote memo blocks. e.g. 02/05/2013 */
    protected static final DateTimeFormatter voteDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

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
