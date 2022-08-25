package gov.nysenate.openleg.spotchecks.scraping.lrs.bill;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillUpdateField;
import gov.nysenate.openleg.spotchecks.base.SpotcheckMismatchEvent;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;
import gov.nysenate.openleg.updates.bill.BillFieldUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Adds bills to the bill text scrape queue based on certain events */
@Service
public class BillScrapeQueueService {
    private static final Logger logger = LoggerFactory.getLogger(BillScrapeQueueService.class);
    private final BillScrapeReferenceDao btrDao;
    private final OpenLegEnvironment env;

    @Autowired
    public BillScrapeQueueService(BillScrapeReferenceDao btrDao, OpenLegEnvironment env, EventBus eventBus) {
        this.btrDao = btrDao;
        this.env = env;
        eventBus.register(this);
    }

    /**
     * Adds a bill to the scrape queue in response to a full text update event
     * @param updateEvent BillFieldUpdateEvent
     */
    @Subscribe
    public void handleBillFullTextUpdate(BillFieldUpdateEvent updateEvent) {
        if (!env.isBillScrapeQueueEnabled()) {
            return;
        }
        if (BillUpdateField.FULLTEXT.equals(updateEvent.updateField())) {
            logger.info("adding {} to bill scrape queue after full text update", updateEvent.billId());
            btrDao.addBillToScrapeQueue(updateEvent.billId(), ScrapeQueuePriority.UPDATE_TRIGGERED.getPriority());
        }
        if (BillUpdateField.VOTE.equals(updateEvent.updateField())) {
            logger.info("adding {} to bill scrape queue after vote update", updateEvent.billId());
            btrDao.addBillToScrapeQueue(updateEvent.billId(), ScrapeQueuePriority.UPDATE_TRIGGERED.getPriority());
        }
    }

    /**
     * Adds a bill the the scrape queue in response to a page count spotcheck mismatch
     * @param mismatchEvent SpotcheckMismatchEvent<BaseBillId>
     */
    @Subscribe
    public void handlePageCountSpotcheckMismatch(SpotcheckMismatchEvent<BillId> mismatchEvent) {
        if (SpotCheckMismatchType.BILL_FULLTEXT_PAGE_COUNT.equals(mismatchEvent.mismatch().getMismatchType()) &&
                env.isBillScrapeQueueEnabled()) {
            logger.info("adding {} to bill scrape queue after spotcheck", mismatchEvent.contentId());
            btrDao.addBillToScrapeQueue(BaseBillId.of(mismatchEvent.contentId()),
                    ScrapeQueuePriority.SPOTCHECK_TRIGGERED.getPriority());
        }
    }
}
