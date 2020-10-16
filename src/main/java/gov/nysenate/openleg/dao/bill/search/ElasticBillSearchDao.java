package gov.nysenate.openleg.dao.bill.search;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillTextFormat;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.request.WebRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static gov.nysenate.openleg.model.bill.BillTextFormat.PLAIN;

@Repository
public class ElasticBillSearchDao extends ElasticBaseDao implements BillSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchDao.class);

    private static final String billIndexName = SearchIndex.BILL.getIndexName();

    private static final int billMaxResultWindow = 500000;

    /** Period for running the reindex janitor in ms */
    private static final long reindexJanitorInterval = 300000;

    /** Lock that controls access to lastReindexRequest variable */
    private static final Object indexRefreshLock = new Object();

    /** Date that is set while reindexing is occurring, used to ensure that index refreshing isn't disabled indefinitely */
    private static volatile LocalDateTime lastReindexRequest = LocalDateTime.MIN;

    /** The amount of time allowed after the last reindex request before index refreshing is reenabled */
    private static final Duration lastReindexTimeoutDuration = Duration.ofMinutes(15);

    protected static final List<HighlightBuilder.Field> highlightedFields =
        Arrays.asList(new HighlightBuilder.Field("basePrintNo").numOfFragments(0),
                      new HighlightBuilder.Field("printNo").numOfFragments(0),
                      new HighlightBuilder.Field("title").numOfFragments(0));

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(QueryBuilder query, QueryBuilder postFilter, RescorerBuilder<?> rescorer,
                                                 List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(billIndexName, query, postFilter,
                highlightedFields, rescorer, sort, limOff,
                false, this::getBaseBillIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Bill bill) {
            updateBillIndex(Collections.singletonList(bill));
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Collection<Bill> bills) {
        BulkRequest bulkRequest = new BulkRequest();
        bills.stream()
                .map(b -> new BillView(b, Sets.newHashSet(BillTextFormat.PLAIN)))
                .map(bv -> getJsonIndexRequest(billIndexName, toElasticId(bv.toBaseBillId()), bv))
                .forEach(bulkRequest::add);
        safeBulkRequestExecute(bulkRequest);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBillFromIndex(BaseBillId baseBillId) {
        logger.info("Deleting {} from index.", baseBillId);
        if (baseBillId != null) {
            deleteEntry(billIndexName, toElasticId(baseBillId));
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
     * Cleans up after reindexing by reenabling index refresh.
     */
    public void reindexCleanup() {
        synchronized (indexRefreshLock) {
            lastReindexRequest = LocalDateTime.MIN;
            setIndexRefresh(billIndexName, true);
        }
    }

    /**
     * Method that ensures that index refresh does not remain disabled if it is not reenabled properly in case of errors.
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
    protected List<String> getIndices() {
        return Lists.newArrayList(billIndexName);
    }

    /**
     * Increase max result window for bills in order to perform paginated queries on lots of bills.
     *
     * @see gov.nysenate.openleg.controller.api.bill.BillGetCtrl#getBills(int, String, boolean, boolean, WebRequest)
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
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = super.getIndexSettings();
        indexSettings.put("index.number_of_shards", 6);
        return indexSettings;
    }

    private BaseBillId getBaseBillIdFromHit(SearchHit hit) {

        String[] IDparts = hit.getId().split("-");

        return new BaseBillId(IDparts[1], Integer.parseInt(IDparts[0]));
    }

    private String toElasticId(BaseBillId baseBillId) {
        return baseBillId.getSession() + "-" +
                baseBillId.getBasePrintNo();
    }
}