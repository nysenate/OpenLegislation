package gov.nysenate.openleg.service.bill.text;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.text.SqlBillTextReferenceDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.spotcheck.ReferenceDataNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReport;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportNotFoundEx;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextSpotcheckReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.SpotCheckReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kyle on 3/12/15.
 */
@Service
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
        //scrapeStuff();
    }


    public void scrapeStuff(String billType, String billNo, int session) throws IOException{
        SessionYear sessionYear = new SessionYear(session);

        List<File> textFileList = scraper.scrape(billType, billNo, sessionYear);
        String printNo = billType + billNo;

        String billText = scrapedBillTextParser.getBillText(textFileList.get(0), billType);
        String memoText = scrapedBillMemoParser.getBillMemoText(textFileList.get(1));
        String amendment = scrapedBillMemoParser.getAmendment();
        BillTextSpotcheckReference b =
                new BillTextSpotcheckReference(printNo, sessionYear, LocalDateTime.now(),
                billText, memoText, Version.of(amendment));

        addToDatabase(b);

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

    }
}
