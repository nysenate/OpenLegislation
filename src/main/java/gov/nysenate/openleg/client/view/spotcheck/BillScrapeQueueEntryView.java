package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.bill.BaseBillIdView;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.billtext.BillScrapeQueueEntry;

import java.time.LocalDateTime;

public class BillScrapeQueueEntryView extends BaseBillIdView
{
    private int priority;
    private LocalDateTime addedTime;

    public BillScrapeQueueEntryView(BillScrapeQueueEntry entry) {
        super(entry != null ? entry.getBaseBillId() : null);
        if (entry != null) {
            this.priority = entry.getPriority();
            this.addedTime = entry.getAddedTime();
        }
    }

    @Override
    public String getViewType() {
        return "bill scrape queue entry";
    }

    public int getPriority() {
        return priority;
    }

    public LocalDateTime getAddedTime() {
        return addedTime;
    }
}
