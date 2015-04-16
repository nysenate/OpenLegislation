package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.text.SqlBillTextReferenceDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
<<<<<<< Updated upstream:src/main/java/gov/nysenate/openleg/service/spotcheck/billtext/BillTextCheckReportService.java
=======
import gov.nysenate.openleg.model.bill.Bill;
>>>>>>> Stashed changes:src/main/java/gov/nysenate/openleg/service/bill/text/BillTextCheckReportService.java
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyle on 3/12/15.
 */
@Service("LRSBillTextReport")
public class BillTextCheckReportService implements SpotCheckReportService<BaseBillId>{
    @Autowired
    BillTextScraper scraper;
    @Autowired
    ScrapedBillTextParser scrapedBillTextParser;
    @Autowired
    ScrapedBillMemoParser scrapedBillMemoParser;
    @Autowired
    SqlBillTextReferenceDao dao;
    @Autowired
    BillDataService billDataService;


    @PostConstruct
    public void init() throws IOException{
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBCD_SCRAPED_BILL;
    }

    public void scrapeStuff(String billType, String billNo, int session) throws IOException{
        SessionYear sessionYear = new SessionYear(session);

        List<File> textFileList = scraper.scrape(billType, billNo, sessionYear);
        String printNo = billType + billNo;

        //bill and memo parsed from file they're scraped into, retrieves amendment from memo parser
        String billText = scrapedBillTextParser.getBillText(textFileList.get(0), billType);
        String memoText = scrapedBillMemoParser.getBillMemoText(textFileList.get(1));
        String amendment = scrapedBillMemoParser.getAmendment();
        BillTextSpotcheckReference b =
                new BillTextSpotcheckReference(printNo, sessionYear, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                billText, memoText, Version.of(amendment));

        //adds reference to db
        addToDatabase(b);

        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();

        //Gets bill from openleg processed info
        Bill bill = billDataService.getBill(new BaseBillId(b.getPrintNo(), b.getSessionYear()));
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_BILL,
                b.getReferenceDate().truncatedTo(ChronoUnit.SECONDS),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)));

        report.addObservation(billTextObservation(b, bill));


    }
    public SpotCheckObservation<BaseBillId> billTextObservation(BillTextSpotcheckReference b, Bill bill){


        for (int i = 0; i< 10; i++){

        }
        ////////////////
        SpotCheckReferenceId refId = new SpotCheckReferenceId(SpotCheckRefType.LBDC_BILL, b.getReferenceDate());
        SpotCheckObservation<BaseBillId> sourceMissingObs = new SpotCheckObservation<>(refId, bill.getBaseBillId());
        ///////////
        SpotCheckMismatch mismatch = new SpotCheckMismatch(SpotCheckMismatchType.BILL_FULL_TEXT, b.getText(), bill.getFullText());
        sourceMissingObs.addMismatch(mismatch);

        return sourceMissingObs;
    }

    public void addToDatabase(BillTextSpotcheckReference ref){
        dao.insertBillTextReference(ref);
    }

    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        return null;
    }

    @Override
    public void saveReport(SpotCheckReport<BaseBillId> report) {

    }

    @Override
    public SpotCheckReport<BaseBillId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        return null;
    }

    @Override
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return null;
    }

    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId to delete cannot be null");
        }
        ////////////////////////////////////////////////////dao.deleteBillTextReference(reportId);

    }
}
