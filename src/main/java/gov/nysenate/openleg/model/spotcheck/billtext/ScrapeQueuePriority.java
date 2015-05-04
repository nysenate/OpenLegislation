package gov.nysenate.openleg.model.spotcheck.billtext;

/** Assigns a priority number to actions that add bills to the scrape queue.
 * Higher numbers indicate a higher priority */
public enum ScrapeQueuePriority {

    UPDATE_TRIGGERED(20),
    SPOTCHECK_TRIGGERED(50),
    MANUAL_ENTRY(100),
    ;

    private int priority;

    ScrapeQueuePriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static ScrapeQueuePriority of(int priority) {
        for (ScrapeQueuePriority scp : ScrapeQueuePriority.values()) {
            if (scp.priority == priority) {
                return scp;
            }
        }
        return null;
    }
}
