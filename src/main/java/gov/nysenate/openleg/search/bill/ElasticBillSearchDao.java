package gov.nysenate.openleg.search.bill;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import co.elastic.clients.elasticsearch.core.search.Rescore;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.api.legislation.bill.BillGetCtrl;
import gov.nysenate.openleg.api.legislation.bill.view.BaseBillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.Bill;
import gov.nysenate.openleg.legislation.bill.BillTextFormat;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.WebRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class ElasticBillSearchDao extends ElasticBaseDao<BillView> implements BillSearchDao {
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchDao.class);

    private static final String billIndexName = SearchIndex.BILL.getName();
    private static final int billMaxResultWindow = 500000;
    /** Period for running the reindex janitor in ms */
    private static final long reindexJanitorInterval = 300000;
    /** Lock that controls access to lastReindexRequest variable */
    private static final Object indexRefreshLock = new Object();
    /** Date that is set while reindexing is occurring, used to ensure that index refreshing isn't disabled indefinitely */
    private static volatile LocalDateTime lastReindexRequest = LocalDateTime.MIN;
    /** The amount of time allowed after the last reindex request before index refreshing is re-enabled */
    private static final Duration lastReindexTimeoutDuration = Duration.ofMinutes(15);

    private static final Map<String, HighlightField> highlightedFields;
    static {
        var highlightField = HighlightField.of(b -> b.numberOfFragments(0));
        highlightedFields = Map.of("basePrintNo", highlightField, "printNo", highlightField,
                "title", highlightField);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(Query query, Query postFilter, Rescore rescorer,
                                                 List<SortOptions> sort, LimitOffset limOff) {
        return search(billIndexName, query, postFilter,
                highlightedFields, rescorer, sort, limOff,
                false, BaseBillIdView::toBaseBillId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Bill bill) {
        updateBillIndex(Collections.singletonList(bill));
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Collection<Bill> bills) {
        var bulkBuilder = new BulkOperation.Builder();
        bills.stream()
                .map(b -> new BillView(b, Sets.newHashSet(BillTextFormat.PLAIN)))
                .map(bv -> getIndexOperationRequest(billIndexName, bv.toBaseBillId().toString(), bv))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(billIndexName).operations(bulkBuilder.build())));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBillFromIndex(BaseBillId baseBillId) {
        logger.info("Deleting {} from index.", baseBillId);
        if (baseBillId != null) {
            deleteEntry(billIndexName, baseBillId.toString());
        }
    }

    /**
     * Sets up for reindexing by disabling index refresh.
     */
    public void reindexSetup() {
        synchronized (indexRefreshLock) {
            lastReindexRequest = LocalDateTime.now();
            setIndexRefresh(billIndexName, false);
        }
    }

    /**
     * Refreshes last reindex request timestamp to indicate that a reindex is still in progress.
     */
    public void reaffirmReindexing() {
        synchronized (indexRefreshLock) {
            lastReindexRequest = LocalDateTime.now();
        }
    }

    /**
     * Cleans up after reindexing by re-enabling index refresh.
     */
    public void reindexCleanup() {
        synchronized (indexRefreshLock) {
            lastReindexRequest = LocalDateTime.MIN;
            setIndexRefresh(billIndexName, true);
        }
    }

    /**
     * Method that ensures that index refresh does not remain disabled if it is not re-enabled properly in case of errors.
     */
    @Scheduled(fixedDelay = reindexJanitorInterval)
    public void reindexJanitor() {
        if (isIndexRefreshDefault(billIndexName)) {
            return;
        }
        synchronized (indexRefreshLock) {
            Duration timeSinceLastReindexRequest = Duration.between(lastReindexRequest, LocalDateTime.now());
            if (timeSinceLastReindexRequest.compareTo(lastReindexTimeoutDuration) > 0) {
                logger.warn(
                        "Index refresh has been disabled for past the timeout duration for index {}.  Reenabling...",
                        billIndexName);
                reindexCleanup();
            } else {
                logger.info("Index refresh is disabled, but will remain so due to recent reindex requests.");
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.BILL;
    }

    /**
     * Increase max result window for bills in order to perform paginated queries on lots of bills.
     *
     * @see BillGetCtrl#getBills(int, String, boolean, boolean, WebRequest)
     *
     * @return int
     */
    @Override
    protected int getMaxResultWindow() {
        return billMaxResultWindow;
    }

    /**
     * Allocate additional shards for bill index.
     *
     * @return Settings.Builder
     */
    @Override
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("6");
    }
}