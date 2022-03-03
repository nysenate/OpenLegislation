package gov.nysenate.openleg.spotchecks.sensite.bill;

import com.google.common.collect.ImmutableList;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.pipeline.Pipeline;
import gov.nysenate.openleg.common.util.pipeline.PipelineFactory;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.legislation.PublishStatus;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.bill.exception.BillNotFoundEx;
import gov.nysenate.openleg.spotchecks.base.SpotCheckException;
import gov.nysenate.openleg.spotchecks.model.SpotCheckObservation;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReport;
import gov.nysenate.openleg.spotchecks.sensite.BaseSenateSiteReportService;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDump;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpId;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

@Service
public class SenateSiteBillReportService extends BaseSenateSiteReportService<BillId> {

    private final OpenLegEnvironment env;
    private final PipelineFactory pipelineFactory;
    private final SenateSiteBillJsonParser billJsonParser;
    private final BillDataService billDataService;
    private final SenateSiteBillCheckService billCheckService;

    @Autowired
    public SenateSiteBillReportService(OpenLegEnvironment env, PipelineFactory pipelineFactory,
                                       SenateSiteBillJsonParser billJsonParser, BillDataService billDataService,
                                       SenateSiteBillCheckService billCheckService) {
        this.env = env;
        this.pipelineFactory = pipelineFactory;
        this.billJsonParser = billJsonParser;
        this.billDataService = billDataService;
        this.billCheckService = billCheckService;
    }

    @Override
    public SpotCheckRefType getSpotcheckRefType() {
        return SpotCheckRefType.SENATE_SITE_BILLS;
    }

    /**
     * Populate report with observations given senate site dump
     */
    @Override
    protected void checkDump(SenateSiteDump billDump, SpotCheckReport<BillId> report) {

        // Set up a pipeline: dump parsing -> bill retrieval -> checking

        BillChecker billChecker = new BillChecker(getBillIdsForSession(billDump));

        int refQueueSize = env.getSensiteBillRefQueueSize();
        int dataQueueSize = env.getSensiteBillDataQueueSize();

        Pipeline<SpotCheckObservation<BillId>> pipeline =
                pipelineFactory.pipelineBuilder(billDump.getDumpFragments())
                        .addTask(new FragmentParser(), refQueueSize)
                        .addTask(new BillLoader(), dataQueueSize, 2)
                        .addTask(billChecker)
                        .build();

        // Wait for pipeline to finish and add observations to report
        CompletableFuture<ImmutableList<SpotCheckObservation<BillId>>> obsFuture = pipeline.run();
        List<SpotCheckObservation<BillId>> observations;
        try {
            // Allow maximum 1 hour for asynchronous report execution
            observations = obsFuture.get(1, TimeUnit.HOURS);
        } catch (TimeoutException | InterruptedException | ExecutionException ex) {
            throw new SpotCheckException("Error occurred while running NYSenate.gov bill spotcheck", ex);
        }
        report.addObservations(observations);

        // Record ref missing mismatches from unchecked openleg bills
        generateRefMissingObs(billChecker.getUncheckedBaseBillIds(), billChecker.getUncheckedBillIds(), report);
    }

    /* --- Functional classes for pipeline --- */

    /**
     * Parses {@link SenateSiteDumpFragment} into {@link SenateSiteBill}s
     */
    private class FragmentParser implements Function<SenateSiteDumpFragment, Collection<SenateSiteBill>> {
        @Override
        public Collection<SenateSiteBill> apply(SenateSiteDumpFragment fragment) {
            return billJsonParser.extractBillsFromFragment(fragment);
        }
    }

    /**
     * Gets the {@link Bill} corresponding to the given {@link SenateSiteBill} and packages them in a Pair.
     * Substitutes an empty optional for the bill of it does not exist in openleg
     */
    private class BillLoader implements Function<SenateSiteBill, Collection<Pair<SenateSiteBill, Optional<Bill>>>> {
        @Override
        public Collection<Pair<SenateSiteBill, Optional<Bill>>> apply(SenateSiteBill refBill) {
            BillId billId = refBill.getBillId();
            Optional<Bill> olBill;
            try {
                olBill = Optional.of(
                        billDataService.getBill(BaseBillId.of(billId)));
            } catch (BillNotFoundEx ex) {
                olBill = Optional.empty();
            }
            return Collections.singletonList(Pair.of(refBill, olBill));
        }
    }

    /**
     * An object that performs checks on {@link SenateSiteBill} against {@link Bill}s,
     * while keeping track of {@link Bill}s that had no {@link SenateSiteBill} counterpart.
     */
    private class BillChecker
            implements Function<Pair<SenateSiteBill, Optional<Bill>>, Collection<SpotCheckObservation<BillId>>> {

        /*
         *  Keep track of which openleg amendments were checked.
         *
         *  If no amendments of a bill were checked, it will be in the unchecked base bill id set.
         *  When a check is performed on a bill for the first time, all of its amendments
         *  will be added to the unchecked bill id set and it will be removed from the base bill id set.
         *  Each time an amendment is checked, it will be removed from the bill id set.
         */
        private Set<BaseBillId> uncheckedBaseBillIds;
        private Set<BillId> uncheckedBillIds = new HashSet<>();

        public BillChecker( Set<BaseBillId> uncheckedBaseBillIds) {
            this.uncheckedBaseBillIds = new HashSet<>(uncheckedBaseBillIds);
        }

        @Override
        public Collection<SpotCheckObservation<BillId>> apply(Pair<SenateSiteBill, Optional<Bill>> checkData) {
            Optional<Bill> olBillOpt = checkData.getRight();
            SenateSiteBill refBill = checkData.getLeft();
            BillId billId = refBill.getBillId();
            BaseBillId baseBillId = BaseBillId.of(billId);

            SpotCheckObservation<BillId> observation;
            if (olBillOpt.isPresent()) {
                Bill olBill = olBillOpt.get();
                observation = billCheckService.check(olBill, refBill);
                if (uncheckedBaseBillIds.remove(baseBillId)) {
                    uncheckedBillIds.addAll(olBill.getAmendmentIds());
                }
            } else {
                observation = SpotCheckObservation.getObserveDataMissingObs(
                        refBill.getReferenceId(), billId);
            }
            uncheckedBillIds.remove(billId);
            return Collections.singletonList(observation);
        }


        public Set<BillId> getUncheckedBillIds() {
            return uncheckedBillIds;
        }

        public Set<BaseBillId> getUncheckedBaseBillIds() {
            return uncheckedBaseBillIds;
        }
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
     * Generate reference data missing observations for all openleg bills that were not present in the dump.
     *
     * @param uncheckedBaseBillIds {@link Set<BaseBillId>} - ids for bills with 0 checked amendments
     * @param uncheckedBillIds {@link Set<BillId>} - ids for bill amendments that were never checked
     * @param report SpotCheckReport - ref missing obs. will be added to this report
     */
    private void generateRefMissingObs(Set<BaseBillId> uncheckedBaseBillIds,
                                       Set<BillId> uncheckedBillIds,
                                       SpotCheckReport<BillId> report) {
        for (BaseBillId baseBillId : uncheckedBaseBillIds) {
            Bill bill = billDataService.getBill(baseBillId);
            uncheckedBillIds.addAll(bill.getAmendmentIds());
        }

        for (BillId billId : uncheckedBillIds) {
            Bill bill = billDataService.getBill(BaseBillId.of(billId));
            boolean published = bill.getPublishStatus(billId.getVersion())
                    .map(PublishStatus::isPublished)
                    .orElse(false);
            if (published) {
                report.addRefMissingObs(billId);
            } else {
                // Add empty observation for unpublished bills not in dump
                // Because unpublished bills shouldn't be on NYSenate.gov
                report.addEmptyObservation(billId);
            }
        }
    }

}
