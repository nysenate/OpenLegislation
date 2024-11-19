package gov.nysenate.openleg.search.bill;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.util.AsyncUtils;
import gov.nysenate.openleg.common.util.DateUtils;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.search.*;
import gov.nysenate.openleg.updates.bill.BillUpdateEvent;
import gov.nysenate.openleg.updates.bill.BulkBillUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@Service
public class ElasticBillSearchService extends IndexedSearchService<Bill> implements BillSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchService.class);
    private static final int billReindexThreadCount = 4;
    private static final int billReindexBatchSize = 400;

    private final ElasticBillSearchDao billSearchDao;
    private final BillDataService billDataService;
    private final AsyncUtils asyncUtils;

    @Autowired
    public ElasticBillSearchService(ElasticBillSearchDao billSearchDao, BillDataService billDataService,
                                    AsyncUtils asyncUtils, EventBus eventBus) {
        super(billSearchDao);
        this.billSearchDao = billSearchDao;
        this.billDataService = billDataService;
        this.asyncUtils = asyncUtils;
        eventBus.register(this);
    }

    /* --- BillSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String queryStr, SessionYear sessionYear, String sort, LimitOffset limOff)
            throws SearchException {
        Integer year = sessionYear == null ? null : sessionYear.year();
        return billSearchDao.searchForIds(smartSearch(queryStr), sort, limOff, getYearQuery("session", year));
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBillUpdate(BillUpdateEvent billUpdateEvent) {
        if (billUpdateEvent.bill() != null) {
            updateIndex(billUpdateEvent.bill());
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBulkBillUpdate(BulkBillUpdateEvent bulkBillUpdateEvent) {
        if (bulkBillUpdateEvent.bills() != null) {
            updateIndex(bulkBillUpdateEvent.bills());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Bill> bills) {
        List<Bill> indexableBills = new ArrayList<>();
        // Categorize bills based on whether they should be indexed.
        for (Bill bill : bills) {
            if (isBillIndexable(bill)) {
                indexableBills.add(bill);
            } else {
                billSearchDao.deleteFromIndex(billSearchDao.getId(bill));
            }
        }
        super.updateIndex(indexableBills);
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        try {
            // Prep elasticsearch for heavy indexing.
            billSearchDao.reindexSetup();
            // Load all bill ids into a queue
            final var billIdQueue = new LinkedBlockingQueue<BaseBillId>();
            for (int year = DateUtils.LEG_DATA_START_YEAR; year <= LocalDate.now().getYear(); year += 2) {
                billIdQueue.addAll(billDataService.getBillIds(new SessionYear(year), LimitOffset.ALL));
            }
            if (billIdQueue.isEmpty()) {
                logger.warn("Can't rebuild the bill search index because there are no bills.");
                return;
            }

            // Initialize and run several BillReindexWorkers to index bills from the queue
            var futures = new CompletableFuture[billReindexThreadCount];
            var interrupted = new AtomicBoolean(false);
            for (int workerNo = 0; workerNo < billReindexThreadCount; workerNo++) {
                futures[workerNo] = asyncUtils.run(new BillReindexWorker(billIdQueue, interrupted));
            }
            CompletableFuture.allOf(futures).join();
        } finally {
            // Restore normal index settings.
            billSearchDao.reindexCleanup();
        }
    }

    /* --- Internal --- */

    private static String smartSearch(String query) {
        if (query != null && !query.contains(":")) {
            Matcher matcher = BillId.BILL_ID_PATTERN.matcher(query.replaceAll("\\s", ""));
            if (matcher.find()) {
                query = String.format("(printNo:%s OR basePrintNo:%s) AND session:%s",
                        matcher.group("printNo"), matcher.group("printNo"), matcher.group("year"));
            }
        }
        return query;
    }

    /**
     * Returns true if the given bill meets the criteria for being indexed in the search layer.
     *
     * @param bill Bill
     * @return boolean
     */
    private static boolean isBillIndexable(Bill bill) {
        return bill != null && bill.isBaseVersionPublished();
    }

    /**
     * Runnable that loads and indexes bills from a bill id queue.
     */
    private class BillReindexWorker implements Runnable {

        /** Job queue of bill ids of bills to be indexed */
        private final BlockingQueue<BaseBillId> billIdQueue;
        /** Flag shared between workers that is set to true if one worker experiences an error */
        private final AtomicBoolean interrupted;

        BillReindexWorker(BlockingQueue<BaseBillId> billIdQueue, AtomicBoolean interrupted) {
            this.billIdQueue = billIdQueue;
            this.interrupted = interrupted;
        }

        @Override
        public void run() {
            List<BaseBillId> billIdBatch;
            try {
                do {
                    if (interrupted.get()) {
                        logger.info("Terminating reindex job due to exception in other worker");
                        return;
                    }
                    billSearchDao.reaffirmReindexing();

                    billIdBatch = new ArrayList<>(billReindexBatchSize);
                    billIdQueue.drainTo(billIdBatch, billReindexBatchSize);

                    List<Bill> bills = billIdBatch.stream()
                            .map(billDataService::getBill)
                            .collect(Collectors.toCollection(() -> new ArrayList<>(billReindexBatchSize)));

                    updateIndex(bills);
                } while (!billIdBatch.isEmpty());
            } catch (Throwable ex) {
                // Set the interrupted flag if an exception occurs
                interrupted.set(true);
                throw ex;
            }
        }
    }
}