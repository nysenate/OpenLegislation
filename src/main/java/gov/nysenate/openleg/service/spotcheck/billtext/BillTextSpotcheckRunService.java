package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.bill.text.SqlBillTextReferenceDao;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaAddendumId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotcheckRunService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

/**
 * Created by kyle on 4/21/15.
 */
@Service
public class BillTextSpotcheckRunService extends BaseSpotcheckRunService<BaseBillId> {
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
    BillTextCheckReportService reportService;

    private static final Logger logger = LoggerFactory.getLogger(BillTextSpotcheckRunService.class);

    @Override
    protected List<SpotCheckReport<BaseBillId>> doGenerateReports() throws Exception {
        logger.info("attempting to run scraped bill text report");
        try {
            // Find unchecked references from any time

            SpotCheckReport<BaseBillId> report =
                    reportService.generateReport(LocalDateTime.now().minusMinutes(10), LocalDateTime.now());
            if (report == null) {
                return Collections.emptyList();
            }
            logger.info("saving scraped bll text reports..");
            reportService.saveReport(report);
            return Collections.singletonList(report);
        } catch (ReferenceDataNotFoundEx ex) {
            logger.info("No reports generated: {}", ex.getMessage());
        }
        return Collections.emptyList();
    }
    public void addToDatabase(BillTextSpotcheckReference ref){
        dao.insertBillTextReference(ref);
    }

    @Override
    protected int doCollate() throws Exception {
        List<BaseBillId> l = dao.getScrapeQueue();
        if (!l.isEmpty()){
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
            return 1;
        }
        return 0;
    }

    @Override
    public String getCollateType() {
        return "Scraped Bill Text";
    }
}
















