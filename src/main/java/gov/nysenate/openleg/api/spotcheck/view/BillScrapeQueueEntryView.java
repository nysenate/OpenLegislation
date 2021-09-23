package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.legislation.bill.view.BaseBillIdView;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeQueueEntry;

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
