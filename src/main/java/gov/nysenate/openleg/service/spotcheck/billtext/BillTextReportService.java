package gov.nysenate.openleg.service.spotcheck.billtext;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.text.SqlFsBillTextReferenceDao;
import gov.nysenate.openleg.dao.spotcheck.BaseBillIdSpotCheckReportDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billtext.BillTextReference;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.scraping.BillTextScraper;
import gov.nysenate.openleg.service.scraping.ScrapedBillMemoParser;
import gov.nysenate.openleg.service.scraping.ScrapedBillTextParser;
import gov.nysenate.openleg.service.spotcheck.base.SpotCheckReportService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Created by kyle on 3/12/15.
 */
@Service("LRSBillTextReport")
public class BillTextReportService implements SpotCheckReportService<BaseBillId>{
    @Autowired
    BillTextScraper scraper;
    @Autowired
    ScrapedBillTextParser scrapedBillTextParser;
    @Autowired
    ScrapedBillMemoParser scrapedBillMemoParser;
    @Autowired
    SqlFsBillTextReferenceDao dao;
    @Autowired
    BillDataService billDataService;
    @Autowired
    BaseBillIdSpotCheckReportDao reportDao;
    @Autowired
    BillTextCheckService billTextCheckService;

    @PostConstruct
    public void init() throws IOException{
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx {
        List<BillTextReference> references = dao.getUncheckedBillTextReferences();
        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx();
        }
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL,
                references.get(0).getReferenceDate(), LocalDateTime.now()));

        report.setNotes(references.stream()
                .map(btr -> btr.getBaseBillId().toString())
                .reduce("", (a, b) -> a + (StringUtils.isBlank(a) ? "" : ", ") + b));

        references.stream()
                .map(this::generateObservation)
                .forEach(report::addObservation);

        references.stream()
                .map(BillTextReference::getBaseBillId)
                .forEach(dao::setChecked);

        return report;
    }

    @Override
    public void saveReport(SpotCheckReport<BaseBillId> report) {
        reportDao.saveReport(report);
    }

    @Override
    public SpotCheckReport<BaseBillId> getReport(SpotCheckReportId reportId) throws SpotCheckReportNotFoundEx {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId cannot be null");
        }
        try {
            return reportDao.getReport(reportId);
        } catch (EmptyResultDataAccessException ex) {
            throw new SpotCheckReportNotFoundEx(reportId);
        }

    }

    @Override
    public List<SpotCheckReportId> getReportIds(LocalDateTime start, LocalDateTime end, SortOrder dateOrder, LimitOffset limOff) {
        return reportDao.getReportIds(SpotCheckRefType.LBDC_SCRAPED_BILL, start, end, dateOrder, limOff);
    }

    @Override
    public void deleteReport(SpotCheckReportId reportId) {
        if (reportId == null) {
            throw new IllegalArgumentException("Supplied reportId to delete cannot be null");
        }

        reportDao.deleteReport(reportId);
    }

    private SpotCheckObservation<BaseBillId> generateObservation(BillTextReference btr) {
        //Gets bill from openleg processed info
        try {
            Bill bill = billDataService.getBill(new BaseBillId(btr.getPrintNo(), btr.getSessionYear()));
            return billTextCheckService.check(bill, btr);
        }catch(BillNotFoundEx e){
            SpotCheckObservation<BaseBillId> ob = new SpotCheckObservation<>(btr.getReferenceId(),
                    new BaseBillId(btr.getPrintNo(), btr.getSessionYear()));
            ob.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, btr.getBaseBillId().toString(), ""));
            return ob;
        }
    }
}
