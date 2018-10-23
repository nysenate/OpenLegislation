package gov.nysenate.openleg.service.spotcheck.scrape;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.scrape.BillScrapeReferenceDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.service.scraping.bill.BillScraper;
import gov.nysenate.openleg.service.scraping.bill.BillScrapeFile;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillScrapeSpotcheckProcessService extends BaseSpotcheckProcessService {

    private static final Logger logger = LoggerFactory.getLogger(BillScrapeSpotcheckProcessService.class);

    private BillScrapeReferenceDao btrDao;
    private BillScraper scraper;
    private SpotCheckNotificationService notificationService;

    @Autowired
    public BillScrapeSpotcheckProcessService(BillScrapeReferenceDao btrDao, BillScraper scraper,
                                             SpotCheckNotificationService notificationService) {
        this.btrDao = btrDao;
        this.scraper = scraper;
        this.notificationService = notificationService;
    }

    @Override
    public int doCollate() throws IOException {
        // Scrape to generate a new file
        scraper.scrape();
        // Register any new files in the incoming dir.
        List<BillScrapeFile> newBillScrapeFiles = btrDao.registerIncomingScrapedBills();
        return newBillScrapeFiles.size();
    }

    @Override
    public int doIngest() throws IOException {
        List<BillScrapeFile> billScrapeFiles = btrDao.getIncomingScrapedBills();
        for (BillScrapeFile billScrape : billScrapeFiles) {
            btrDao.archiveScrapedBill(billScrape);
        }
        return billScrapeFiles.size();
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    @Override
    protected int getUncheckedRefCount() {
        return btrDao.getPendingScrapeBills(LimitOffset.ONE).getTotal();
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
















