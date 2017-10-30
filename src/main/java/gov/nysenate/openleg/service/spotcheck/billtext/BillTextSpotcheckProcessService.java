package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.BillTextReferenceDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.model.spotcheck.billtext.ScrapeQueuePriority;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.LrsOutageScrapingEx;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillTextSpotcheckProcessService extends BaseSpotcheckProcessService {

    @Autowired private BillTextReferenceDao btrDao;
    @Autowired private BillTextScraper scraper;
    @Autowired private ScrapedBillTextParser scrapedBillTextParser;
    @Autowired private SpotCheckNotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(BillTextSpotcheckProcessService.class);

    @Override
    public int doCollate() throws IOException {
        return scraper.scrape();
    }

    @Override
    public int doIngest() throws IOException {
        Collection<File> incomingScrapedBills = btrDao.getIncomingScrapedBills();
        List<BillTextReference> billTextReferences = new ArrayList<>();
        for (File file : incomingScrapedBills) {
            BaseBillId baseBillId = scrapedBillTextParser.getBaseBillIdFromFileName(file);
            try {
                BillTextReference btr = scrapedBillTextParser.parseReference(file);
                btrDao.insertBillTextReference(btr);
                billTextReferences.add(btr);
            } catch (LrsOutageScrapingEx ex) {
                logger.warn("LRS outage detected from scraped file: {}", file.getPath());
                notificationService.handleLrsOutageScrapingEx(ex);
                // Add the bill back to the queue
                btrDao.addBillToScrapeQueue(baseBillId, ScrapeQueuePriority.SPOTCHECK_TRIGGERED);
            } finally {
                btrDao.archiveScrapedBill(file);
            }
        }
        return billTextReferences.size();
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    @Override
    protected int getUncheckedRefCount() {
        return btrDao.getUncheckedBillTextReferences().size();
    }

    @Override
    public String getCollateType() {
        return "Scraped Bill";
    }

    @Override
    public String getIngestType() {
        return "Bill Text spotcheck reference";
    }
}
















