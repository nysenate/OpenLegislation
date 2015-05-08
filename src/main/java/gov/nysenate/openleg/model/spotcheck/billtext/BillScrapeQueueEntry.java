package gov.nysenate.openleg.model.spotcheck.billtext;

import gov.nysenate.openleg.model.bill.BaseBillId;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BillScrapeQueueEntry implements Serializable {

    private static final long serialVersionUID = 9117591103282634248L;

    private BaseBillId baseBillId;
    private int priority;
    private LocalDateTime addedTime;

    public BillScrapeQueueEntry(BaseBillId baseBillId, int priority, LocalDateTime addedTime) {
        this.baseBillId = baseBillId;
        this.priority = priority;
        this.addedTime = addedTime;
    }

    @Override
    public String toString() {
        return "BillScrapeQueueEntry{" +
                "baseBillId=" + baseBillId +
                ", priority=" + priority +
                ", addedTime=" + addedTime +
                '}';
    }

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public int getPriority() {
        return priority;
    }

    public LocalDateTime getAddedTime() {
        return addedTime;
    }
}
