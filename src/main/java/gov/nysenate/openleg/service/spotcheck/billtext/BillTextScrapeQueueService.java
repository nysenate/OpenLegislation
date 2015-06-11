package gov.nysenate.openleg.service.spotcheck.billtext;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.bill.text.BillTextReferenceDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillUpdateField;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.billtext.ScrapeQueuePriority;
import gov.nysenate.openleg.service.bill.event.BillFieldUpdateEvent;
import gov.nysenate.openleg.service.spotcheck.base.SpotcheckMismatchEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/** Adds bills to the bill text scrape queue based on certain events */
@Service
public class BillTextScrapeQueueService {
    private static final Logger logger = LoggerFactory.getLogger(BillTextScrapeQueueService.class);

    @Autowired
    BillTextReferenceDao btrDao;

    @Autowired
    EventBus eventBus;

    @Autowired
    Environment env;

    @PostConstruct
    public void init() {
        eventBus.register(this);
    }

    /**
     * Adds a bill to the scrape queue in response to a full text update event
     * @param updateEvent BillFieldUpdateEvent
     */
    @Subscribe
    public void handleBillFullTextUpdate(BillFieldUpdateEvent updateEvent) {
        if (BillUpdateField.FULLTEXT.equals(updateEvent.getUpdateField()) &&
                env.isBillScrapeQueueEnabled()) {
            logger.info("adding {} to bill scrape queue after full text update", updateEvent.getBillId());
            btrDao.addBillToScrapeQueue(updateEvent.getBillId(), ScrapeQueuePriority.UPDATE_TRIGGERED.getPriority());
        }
    }

    /**
     * Adds a bill the the scrape queue in response to a page count spotcheck mismatch
     * @param mismatchEvent SpotcheckMismatchEvent<BaseBillId>
     */
    @Subscribe
    public void handlePageCountSpotcheckMismatch(SpotcheckMismatchEvent<BaseBillId> mismatchEvent) {
        if (SpotCheckMismatchType.BILL_FULLTEXT_PAGE_COUNT.equals(mismatchEvent.getMismatch().getMismatchType()) &&
                env.isBillScrapeQueueEnabled()) {
            logger.info("adding {} to bill scrape queue after spotcheck", mismatchEvent.getContentId());
            btrDao.addBillToScrapeQueue(mismatchEvent.getContentId(),
                    ScrapeQueuePriority.SPOTCHECK_TRIGGERED.getPriority());
        }
    }
}
