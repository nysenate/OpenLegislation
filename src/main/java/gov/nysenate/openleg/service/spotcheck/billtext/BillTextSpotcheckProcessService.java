package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.SqlFsBillTextReferenceDao;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillTextSpotcheckProcessService extends BaseSpotcheckProcessService {

    @Autowired
    SqlFsBillTextReferenceDao dao;
    @Autowired
    BillTextScraper scraper;
    @Autowired
    ScrapedBillMemoParser scrapedBillMemoParser;
    @Autowired
    ScrapedBillTextParser scrapedBillTextParser;
    @Autowired
    BillTextReportService reportService;
    @Autowired
    SpotCheckNotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(BillTextSpotcheckProcessService.class);

    @Override
    public int doCollate() throws Exception {
        return scraper.scrape();
    }

    public void addToDatabase(BillTextReference ref) {
        dao.insertBillTextReference(ref);
    }

    @Override
    public int doIngest() throws Exception {
        Collection<File> incomingScrapedBills = dao.getIncomingScrapedBills();
        List<BillTextReference> billTextReferences = new ArrayList<>();
        for (File file : incomingScrapedBills) {
            try {
                BillTextReference btr = scrapedBillTextParser.parseReference(file);
                dao.insertBillTextReference(btr);
                billTextReferences.add(btr);
            } catch (Exception ex) {
                notificationService.handleSpotcheckException(ex, false);
            } finally {
                dao.archiveScrapedBill(file);
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
        return dao.getUncheckedBillTextReferences().size();
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
















