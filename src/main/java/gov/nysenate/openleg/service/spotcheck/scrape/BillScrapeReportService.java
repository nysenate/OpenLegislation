package gov.nysenate.openleg.service.spotcheck.scrape;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.bill.scrape.SqlFsBillScrapeReferenceDao;
import gov.nysenate.openleg.dao.spotcheck.BaseBillIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeReference;
import gov.nysenate.openleg.processor.base.ParseError;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.scraping.bill.BillScrapeFile;
import gov.nysenate.openleg.service.scraping.bill.BillScrapeReferenceFactory;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType.*;

/**
 * Created by kyle on 3/12/15.
 */
@Service
public class BillScrapeReportService extends BaseSpotCheckReportService {

    private static final Logger logger = LoggerFactory.getLogger(BillScrapeReportService.class);

    private static final int maxBillsPerReport = 5000;

    @Autowired private SqlFsBillScrapeReferenceDao dao;
    @Autowired private BillDataService billDataService;
    @Autowired private BaseBillIdSpotCheckReportDao reportDao;
    @Autowired private BillScrapeCheckService billScrapeCheckService;
    @Autowired private BillScrapeReferenceFactory billScrapeReferenceFactory;

    @Override
    protected SpotCheckReportDao getReportDao() {
        return reportDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.LBDC_SCRAPED_BILL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SpotCheckReport<BaseBillId> generateReport(LocalDateTime start, LocalDateTime end) throws ReferenceDataNotFoundEx, IOException {
        PaginatedList<BillScrapeFile> pendingScrapeFiles = dao.getPendingScrapeBills(new LimitOffset(maxBillsPerReport));
        if (pendingScrapeFiles.getTotal() > maxBillsPerReport) {
            logger.info("Checking {} of {} pending scraped bills", maxBillsPerReport, pendingScrapeFiles.getTotal());
        }
        List<BillScrapeReference> references = parseBillScrapeReferences(pendingScrapeFiles.getResults());

        if (references.isEmpty()) {
            throw new ReferenceDataNotFoundEx();
        }

        SpotCheckReport<BaseBillId> report = createReport(references);
        report.setNotes(createNotes(references));

        // Get observations for each reference
        references.stream()
                .map(this::generateObservation)
                .forEach(report::addObservation);

        // Set each reference as checked
        for (BillScrapeFile file : pendingScrapeFiles.getResults()) {
            file.setPendingProcessing(false);
            dao.updateScrapedBill(file);
        }

        return report;
    }

    private List<BillScrapeReference> parseBillScrapeReferences(List<BillScrapeFile> scrapeFiles) throws IOException {
        List<BillScrapeReference> references = new ArrayList<>();
        for (BillScrapeFile file: scrapeFiles) {
            try {
                BillScrapeReference btr = billScrapeReferenceFactory.createFromFile(file);
                references.add(btr);
            } catch (ParseError ex) {
                logger.warn("Could not successfully parse bill text reference file {}", file.getFileName());
            }
        }
        return references;
    }

    private SpotCheckReport<BaseBillId> createReport(List<BillScrapeReference> references) {
        SpotCheckReport<BaseBillId> report = new SpotCheckReport<>();
        report.setReportId(new SpotCheckReportId(SpotCheckRefType.LBDC_SCRAPED_BILL,
                references.get(0).getReferenceDate(), LocalDateTime.now()));
        return report;
    }

    // Set checked billids as notes
    private String createNotes(List<BillScrapeReference> references) {
        return references.stream()
                .map(btr -> btr.getBaseBillId().toString())
                .reduce("", (a, b) -> a + (StringUtils.isBlank(a) ? "" : ", ") + b);
    }

    private SpotCheckObservation<BaseBillId> generateObservation(BillScrapeReference btr) {
        //Gets bill from openleg processed info
        try {
            Bill bill = billDataService.getBill(new BaseBillId(btr.getPrintNo(), btr.getSessionYear()), EnumSet.allOf(BillTextFormat.class));
            return billScrapeCheckService.check(bill, btr);
        } catch (BillNotFoundEx e) {
            SpotCheckObservation<BaseBillId> ob = new SpotCheckObservation<>(btr.getReferenceId(), btr.getBaseBillId());
            if (btr.isNotFound()) { // Bill text references are still generated if LRS data is not found
                ob.addMismatch(new SpotCheckMismatch(REFERENCE_DATA_MISSING,
                        "also missing", btr.getBaseBillId() + "\n" + btr.getText()));
            }
            ob.addMismatch(new SpotCheckMismatch(OBSERVE_DATA_MISSING, "", btr.getBaseBillId().toString()));
            return ob;
        }
    }
}
