package gov.nysenate.openleg.service.spotcheck.senatesite.bill;

import com.google.common.collect.Sets;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.spotcheck.BillIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.AsyncUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class BillReportService extends BaseSpotCheckReportService<BillId> {

    private static final Logger logger = LoggerFactory.getLogger(BillReportService.class);

    @Autowired private AsyncUtils asyncUtils;

    @Autowired private BillIdSpotCheckReportDao billReportDao;
    @Autowired private SenateSiteDao senateSiteDao;
    @Autowired private BillJsonParser billJsonParser;

    @Autowired private BillDataService billDataService;

    @Autowired private BillCheckService billCheckService;

    @Override
    protected SpotCheckReportDao<BillId> getReportDao() {
        return billReportDao;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_BILLS;
    }

    @Override
    public synchronized SpotCheckReport<BillId> generateReport(LocalDateTime start, LocalDateTime end) throws Exception {
        SenateSiteDump billDump = getMostRecentDump();
        SpotCheckReportId reportId = new SpotCheckReportId(SpotCheckRefType.SENATE_SITE_BILLS,
                billDump.getDumpId().getDumpTime(), LocalDateTime.now());
        SpotCheckReport<BillId> report = new SpotCheckReport<>(reportId);
        report.setNotes(billDump.getDumpId().getNotes());
        try {

            generateReport(billDump, report);

        } finally {
            logger.info("archiving bill dump...");
            senateSiteDao.setProcessed(billDump);
        }
        return report;
    }

    /* --- Internal Methods --- */

    /**
     * Populate report with observations given senate site dump
     */
    private void generateReport(SenateSiteDump billDump, SpotCheckReport<BillId> report) {

        // Get openleg bills and parse the bill dump in parallel

        CompletableFuture<Map<BaseBillId, Bill>> olBillFuture =
                asyncUtils.get(() -> getBills(billDump, report));

        CompletableFuture<List<SenateSiteBill>> refBillFuture =
                asyncUtils.get(() -> parseBillDump(billDump));

        Map<BaseBillId, Bill> openlegBills = olBillFuture.join();
        List<SenateSiteBill> dumpedBills = refBillFuture.join();

        generateRefMissingObs(dumpedBills, openlegBills.values(), report);

        logger.info("checking bills");
        // Check each dumped senate site bill
        dumpedBills.stream()
                .map(senSiteBill -> billCheckService.check(openlegBills.get(senSiteBill.getBaseBillId()), senSiteBill))
                .forEach(report::addObservation);
        logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
    }

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_BILLS).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site bill dumps"));
    }

    /**
     *  Parses bills from dump and logs when done
     */
    private List<SenateSiteBill> parseBillDump(SenateSiteDump billDump) {
        logger.info("Extracting bill references from dump...");
        List<SenateSiteBill> senateSiteBills = billJsonParser.parseBills(billDump);
        logger.info("parsed {} dumped bills", senateSiteBills.size());
        return senateSiteBills;
    }

    private Map<BaseBillId, Bill> getBills(SenateSiteDump billDump, SpotCheckReport<BillId> report) {
        // Get reference billids
        Set<BaseBillId> sessionBillIds = getBillIdsForSession(billDump);
        logger.info("got {} bill ids for {}", sessionBillIds.size(), billDump.getDumpId().getSession());
        Map<BaseBillId, Bill> sessionBills = new LinkedHashMap<>();
        logger.info("retrieving bills");
        for (BaseBillId billId : sessionBillIds) {
            try {
                sessionBills.put(billId, billDataService.getBill(billId));
            } catch (BillNotFoundEx ex) {
                report.addObservedDataMissingObs(billId);
            }
        }
        logger.info("got {} bills", sessionBills.size());
        return sessionBills;
    }

    /**
     * Gets a set of all openleg bill ids for the session of the given dump
     *
     * @param billDump SenateSiteBillDump
     * @return Set<Bill>
     */
    private Set<BaseBillId> getBillIdsForSession(SenateSiteDump billDump) {
        SenateSiteDumpId dumpId = billDump.getDumpId();
        return new TreeSet<>(
                billDataService.getBillIds(dumpId.getSession(), LimitOffset.ALL)
        );
    }

    /**
     * Generate reference data missing observations for all openleg bills that were not present in the dump
     * @param senSiteBills Collection<SenateSiteBill> - Bills extracted from the dump
     * @param openlegBills Collection<Bill> - Bills in openleg
     * @param report SpotCheckReport - ref missing obs. will be added to this report
     */
    private void generateRefMissingObs(Collection<SenateSiteBill> senSiteBills,
                                       Collection<Bill> openlegBills,
                                       SpotCheckReport<BillId> report) {
        logger.info("Looking for bills missing from dump..");
        Set<BillId> senSiteBillIds = senSiteBills.stream()
                .map(SenateSiteBill::getBillId)
                .collect(Collectors.toSet());
        Set<BillId> openlegBillIds = openlegBills.stream()
                .flatMap(bill -> bill.getAmendmentIds().stream()
                        .filter(billId -> bill.getPublishStatus(billId.getVersion())
                                .map(PublishStatus::isPublished)
                                .orElse(false)))
                .collect(Collectors.toSet());

        Sets.difference(openlegBillIds, senSiteBillIds).forEach(report::addRefMissingObs);
    }
}
