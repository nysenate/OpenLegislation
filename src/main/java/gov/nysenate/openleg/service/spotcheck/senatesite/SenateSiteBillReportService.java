package gov.nysenate.openleg.service.spotcheck.senatesite;

import com.google.common.collect.*;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.PaginatedList;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.bill.data.BillUpdatesDao;
import gov.nysenate.openleg.dao.bill.reference.senatesite.SenateSiteDao;
import gov.nysenate.openleg.dao.spotcheck.BillIdSpotCheckReportDao;
import gov.nysenate.openleg.dao.spotcheck.SpotCheckReportDao;
import gov.nysenate.openleg.model.base.PublishStatus;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.spotcheck.*;
import gov.nysenate.openleg.model.spotcheck.senatesite.bill.SenateSiteBill;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.updates.UpdateToken;
import gov.nysenate.openleg.model.updates.UpdateType;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.data.BillNotFoundEx;
import gov.nysenate.openleg.service.spotcheck.base.BaseSpotCheckReportService;
import gov.nysenate.openleg.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SenateSiteBillReportService extends BaseSpotCheckReportService<BillId> {

    private static final Logger logger = LoggerFactory.getLogger(SenateSiteBillReportService.class);

    @Autowired private BillIdSpotCheckReportDao billReportDao;
    @Autowired private SenateSiteDao senateSiteDao;
    @Autowired private SenateSiteBillJsonParser billJsonParser;

    @Autowired private BillDataService billDataService;
    @Autowired private BillUpdatesDao billUpdatesDao;

    @Autowired private SenateSiteBillCheckService billCheckService;

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
                DateUtils.endOfDateTimeRange(billDump.getDumpId().getRange()), LocalDateTime.now());
        SpotCheckReport<BillId> report = new SpotCheckReport<>(reportId);
        try {

            logger.info("getting bill updates");

            // Get reference bills using the bill dump update interval
            Set<BaseBillId> updatedBillIds = getBillUpdatesDuring(billDump);
            logger.info("got {} updated bill ids", updatedBillIds.size());
            Map<BaseBillId, Bill> updatedBills = new LinkedHashMap<>();
            logger.info("retrieving bills");
            for (BaseBillId billId : updatedBillIds) {
                try {
                    updatedBills.put(billId, billDataService.getBill(billId));
                } catch (BillNotFoundEx ex) {
                    SpotCheckObservation<BillId> observation = new SpotCheckObservation<>(reportId.getReferenceId(), billId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.OBSERVE_DATA_MISSING, "", billId));
                    report.addObservation(observation);
                }
            }
            logger.info("got {} bills", updatedBills.size());
            logger.info("retrieving bill dump");
            // Extract senate site bills from the dump
            Multimap<BaseBillId, SenateSiteBill> dumpedBills = ArrayListMultimap.create();
            billJsonParser.parseBills(billDump).forEach(b -> dumpedBills.put(b.getBaseBillId(), b));
            logger.info("parsed {} dumped bills", dumpedBills.size());

            prunePostDumpBills(billDump, report, dumpedBills, updatedBills);

            logger.info("comparing bills present");
            // Add observations for any missing bills that should have been in the dump
            report.addObservations(getRefDataMissingObs(dumpedBills.values(), updatedBills.values(),
                    reportId.getReferenceId()));

            logger.info("checking bills");
            // Check each dumped senate site bill
            dumpedBills.values().stream()
                    .map(senSiteBill -> billCheckService.check(updatedBills.get(senSiteBill.getBaseBillId()), senSiteBill))
                    .forEach(report::addObservation);

            logger.info("done: {} mismatches", report.getOpenMismatchCount(false));
        } finally {
            logger.info("archiving bill dump...");
            senateSiteDao.setProcessed(billDump);
        }
        return report;
    }

    /** --- Internal Methods --- */

    private SenateSiteDump getMostRecentDump() throws IOException, ReferenceDataNotFoundEx {
        return senateSiteDao.getPendingDumps(SpotCheckRefType.SENATE_SITE_BILLS).stream()
                .filter(SenateSiteDump::isComplete)
                .max(SenateSiteDump::compareTo)
                .orElseThrow(() -> new ReferenceDataNotFoundEx("Found no full senate site bill dumps"));
    }

    /**
     * Gets a set of bill ids that were updated during the update interval specified by the bill dump
     * Bills that were updated after the end of this interval are excluded, even if they may have been included in the dump
     *
     * @param billDump SenateSiteBillDump
     * @return Set<Bill>
     */
    private Set<BaseBillId> getBillUpdatesDuring(SenateSiteDump billDump) {
        Range<LocalDateTime> dumpUpdateInterval = billDump.getDumpId().getRange();
        return billUpdatesDao.getUpdates(Range.greaterThan(DateUtils.startOfDateTimeRange(billDump.getDumpId().getRange())),
                UpdateType.PROCESSED_DATE, null, SortOrder.ASC, LimitOffset.ALL)
                .getResults().stream()
                .filter(token -> dumpUpdateInterval.contains(token.getProcessedDateTime()))
                .map(UpdateToken::getId)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Get the base bill ids of all bills updated after the update interval specified by the dump
     * Remove these bills from the openleg and senate site bill references
     * Store a string list of these bill ids in the report notes
     *
     * @param billDump SenateSiteBillDump
     * @param senSiteBills Multimap<BaseBillId, SenateSiteBill>
     * @param openlegBills Map<BaseBillId, Bill>
     */
    private void prunePostDumpBills(SenateSiteDump billDump, SpotCheckReport report,
                                    Multimap<BaseBillId, SenateSiteBill> senSiteBills, Map<BaseBillId, Bill> openlegBills) {
        Range<LocalDateTime> billDumpRange = billDump.getDumpId().getRange();
        Range<LocalDateTime> postDumpRange =  Range.downTo(DateUtils.endOfDateTimeRange(billDumpRange),
                billDumpRange.upperBoundType() == BoundType.OPEN ? BoundType.CLOSED : BoundType.OPEN);
        PaginatedList<UpdateToken<BaseBillId>> postDumpUpdates =
                billUpdatesDao.getUpdates(postDumpRange, UpdateType.PROCESSED_DATE, null, SortOrder.NONE, LimitOffset.ALL);
        Set<BaseBillId> postDumpUpdatedBills = postDumpUpdates.stream()
                .map(UpdateToken::getId)
                .collect(Collectors.toSet());

        if (!postDumpUpdatedBills.isEmpty()) {
            // Iterate over bills updated after the update interval, removing them from the references and
            //  collecting them in a list to add to the report notes
            String notes = postDumpUpdatedBills.stream()
                    .peek(senSiteBills::removeAll)
                    .peek(openlegBills::remove)
                    .reduce("Ignored Bills:", (str, billId) -> str + " " + billId, (a, b) -> a + " " + b);
            report.setNotes(notes);
        }
    }

    /**
     * Generate data missing observations for all bills that were updated in the bill dump update interval,
     *  but not included in the bill dump
     * @param senSiteBills Collection<SenateSiteBill> - Bills extracted from the dump
     * @param openlegBills Collection<Bill> - Bills updated during the dump interval
     * @param refId SpotCheckReferenceId - reference Id used to create the observations
     * @return List<SpotCheckObservation<BillId>>
     */
    private List<SpotCheckObservation<BillId>> getRefDataMissingObs(Collection<SenateSiteBill> senSiteBills,
                                                                    Collection<Bill> openlegBills,
                                                                    SpotCheckReferenceId refId) {
        Set<BillId> senSiteBillIds = senSiteBills.stream()
                .map(SenateSiteBill::getBillId)
                .collect(Collectors.toSet());
        Set<BillId> openlegBillIds = openlegBills.stream()
                .flatMap(bill -> bill.getAmendmentIds().stream()
                        .filter(billId -> bill.getPublishStatus(billId.getVersion())
                                .map(PublishStatus::isPublished)
                                .orElse(false)))
                .collect(Collectors.toSet());
        return Sets.difference(openlegBillIds, senSiteBillIds).stream()
                .map(billId -> {
                    SpotCheckObservation<BillId> observation = new SpotCheckObservation<>(refId, billId);
                    observation.addMismatch(new SpotCheckMismatch(SpotCheckMismatchType.REFERENCE_DATA_MISSING, "", ""));
                    return observation;
                })
                .collect(Collectors.toList());
    }
}
