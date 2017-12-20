package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.BillTextReferenceDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.model.spotcheck.billtext.ScrapeQueuePriority;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.scraping.*;
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
import java.util.stream.Collectors;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillTextSpotcheckProcessService extends BaseSpotcheckProcessService {

    private static final Logger logger = LoggerFactory.getLogger(BillTextSpotcheckProcessService.class);

    private BillTextReferenceDao btrDao;
    private BillTextScraper scraper;
    private BillTextReferenceFactory btrFactory;
    private SpotCheckNotificationService notificationService;

    @Autowired
    public BillTextSpotcheckProcessService(BillTextReferenceDao btrDao, BillTextScraper scraper,
                                           BillTextReferenceFactory btrFactory, SpotCheckNotificationService notificationService) {
        this.btrDao = btrDao;
        this.scraper = scraper;
        this.btrFactory = btrFactory;
        this.notificationService = notificationService;
    }

    @Override
    public int doCollate() throws IOException {
        return scraper.scrape();
    }

    @Override
    public int doIngest() throws IOException {
        Collection<File> incomingScrapedBills = btrDao.getIncomingScrapedBills();
        List<BillTextReferenceFile> btrFiles = incomingScrapedBills.stream()
                .map(BillTextReferenceFile::new)
                .collect(Collectors.toList());
        List<BillTextReference> billTextReferences = new ArrayList<>();
        for (BillTextReferenceFile btrFile: btrFiles) {
            try {
                BillTextReference btr = btrFactory.fromFile(btrFile);
                btrDao.insertBillTextReference(btr);
                billTextReferences.add(btr);
            } catch (LrsOutageScrapingEx ex) {
                logger.warn("LRS outage detected from scraped file: {}", btrFile.getFile().getPath());
                notificationService.handleLrsOutageScrapingEx(ex);
                // Add the bill back to the queue
                btrDao.addBillToScrapeQueue(btrFile.getBaseBillId(), ScrapeQueuePriority.SPOTCHECK_TRIGGERED);
            } catch(ParseError ex) {
                logger.warn("Could not successfully parse bill text reference file {}", btrFile.getFile().getName());
                // TODO notify of parse error
            } finally {
                btrDao.archiveScrapedBill(btrFile.getFile());
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
















