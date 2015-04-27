package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.SqlBillTextReferenceDao;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillTextSpotcheckProcessService extends BaseSpotcheckProcessService<BaseBillId> {
    // get queue , return first billID from queue
    @Autowired
    SqlBillTextReferenceDao dao;
    @Autowired
    BillTextScraper scraper;
    @Autowired
    ScrapedBillMemoParser scrapedBillMemoParser;
    @Autowired
    ScrapedBillTextParser scrapedBillTextParser;
    @Autowired
    BillTextReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(BillTextSpotcheckProcessService.class);

    @Override
    public int doCollate() throws Exception {
        List<BaseBillId> l = dao.getScrapeQueue();
        if (!l.isEmpty()) {
            BaseBillId id = l.get(0);
            List<File> textFileList = scraper.scrape(id);

            //bill and memo parsed from file they're scraped into, retrieves amendment from memo parser
            String billText = scrapedBillTextParser.getBillText(textFileList.get(0), id.getBillType().toString());
            String memoText = scrapedBillMemoParser.getBillMemoText(textFileList.get(1));
            String amendment = scrapedBillMemoParser.getAmendment();
            BillTextSpotcheckReference b =
                    new BillTextSpotcheckReference(id.getPrintNo(), id.getSession(), LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                                                   billText, memoText, Version.of(amendment));
            addToDatabase(b);
            dao.deleteBillFromScrapeQueue(id);
            return 1;
        }
        return 0;
    }

    public void addToDatabase(BillTextSpotcheckReference ref) {
        dao.insertBillTextReference(ref);
    }

    @Override
    public int doIngest() throws Exception {
        return 0;
    }

    @Override
    protected SpotCheckRefType getRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    @Override
    public String getCollateType() {
        return "Scraped Bill Text";
    }
}
















