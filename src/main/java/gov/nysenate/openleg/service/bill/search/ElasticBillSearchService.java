package gov.nysenate.openleg.service.bill.search;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.search.*;
import gov.nysenate.openleg.service.base.search.ElasticSearchServiceUtils;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;
import gov.nysenate.openleg.service.bill.data.BillDataService;
import gov.nysenate.openleg.service.bill.event.BillUpdateEvent;
import gov.nysenate.openleg.service.bill.event.BulkBillUpdateEvent;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Matcher;

import static java.util.stream.Collectors.toList;

@Service
public class ElasticBillSearchService implements BillSearchService, IndexedSearchService<Bill>
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchService.class);

    @Autowired protected Environment env;
    @Autowired protected EventBus eventBus;
    @Autowired protected ElasticBillSearchDao billSearchDao;
    @Autowired protected BillDataService billDataService;

    @PostConstruct
    protected void init() {
        eventBus.register(this);
    }

    /** --- BillSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(SessionYear session, String sort, LimitOffset limOff) throws SearchException {
        return searchBills(
            QueryBuilders.boolQuery()
                    .must(QueryBuilders.matchAllQuery())
                    .filter(QueryBuilders.termQuery("session", session.getYear())),
            null, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff) throws SearchException {
        query = smartSearch(query);
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(query);
        return searchBills(queryBuilder, null, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String query, SessionYear session, String sort, LimitOffset limOff) throws SearchException {
        query = smartSearch(query);
        TermQueryBuilder sessionFilter = QueryBuilders.termQuery("session", session.getYear());
        return searchBills(
            QueryBuilders.boolQuery()
                    .must(QueryBuilders.queryStringQuery(query))
                    .filter(sessionFilter),
                null, null, sort, limOff);
    }

    /**
     * Delegates to the underlying bill search dao.
     */
    private SearchResults<BaseBillId> searchBills(QueryBuilder query, QueryBuilder postFilter, RescorerBuilder rescorer,
                                                  String sort, LimitOffset limOff)
        throws SearchException {
        if (limOff == null) limOff = LimitOffset.TEN;
        try {
            return billSearchDao.searchBills(query, postFilter, rescorer,
                    ElasticSearchServiceUtils.extractSortBuilders(sort), limOff);
        }
        catch (SearchParseException ex) {
            throw new SearchException("Invalid query string", ex);
        }
        catch (ElasticsearchException ex) {
            throw new UnexpectedSearchException(ex.getMessage(), ex);
        }
    }

    private String smartSearch(String query) {
        if (query != null && !query.contains(":")) {
            Matcher matcher = BillId.billIdPattern.matcher(query.replaceAll("\\s", ""));
            if (matcher.find()) {
                query = String.format("(printNo:%s OR basePrintNo:%s) AND session:%s",
                        matcher.group("printNo"), matcher.group("printNo"), matcher.group("year"));
            }
        }
        return query;
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBillUpdate(BillUpdateEvent billUpdateEvent) {
        if (billUpdateEvent.getBill() != null) {
            updateIndex(billUpdateEvent.getBill());
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleBulkBillUpdate(BulkBillUpdateEvent bulkBillUpdateEvent) {
        if (bulkBillUpdateEvent.getBills() != null) {
            updateIndex(bulkBillUpdateEvent.getBills());
        }
    }

    /** --- IndexedSearchService implementation --- */

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Bill bill) {
        if (bill != null) {
            updateIndex(Collections.singleton(bill));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Bill> bills) {
        if (!env.isElasticIndexing() || bills.isEmpty()) {
            return;
        }
        List<Bill> indexableBills = new ArrayList<>();
        List<Bill> nonIndexableBills = new ArrayList<>();
        // Categorize bills based on whether or not they should be index.
        for (Bill bill : bills) {
            if (isBillIndexable(bill)) {
                indexableBills.add(bill);
            } else {
                nonIndexableBills.add(bill);
            }
        }
        logger.info("Indexing {} valid bill(s) into elastic search.", indexableBills.size());
        billSearchDao.updateBillIndex(indexableBills);

        // Ensure any bills that currently don't meet the criteria are not in the index.
        nonIndexableBills.stream()
                .map(Bill::getBaseBillId)
                .forEach(billSearchDao::deleteBillFromIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void clearIndex() {
        billSearchDao.purgeIndices();
        billSearchDao.createIndices();
    }

    /** {@inheritDoc} */
    @Override
    public void rebuildIndex() {
        clearIndex();
        Optional<Range<SessionYear>> sessions = billDataService.activeSessionRange();
        if (!sessions.isPresent()) {
            logger.info("Can't rebuild the bill search index because there are no bills. Cleared it instead!");
            return;
        }
        try {
            // Prep elasticsearch for heavy indexing.
            billSearchDao.reindexSetup();
            for (SessionYear session = sessions.get().lowerEndpoint();
                 session.compareTo(SessionYear.current()) < 1;
                 session = session.next()) {

                LimitOffset limOff = new LimitOffset(50);
                while (true) {
                    List<Bill> bills = billDataService.getBillIds(session, limOff).stream()
                            .map(billDataService::getBill)
                            .collect(toList());
                    if (bills.isEmpty()) {
                        break;
                    }
                    // Ensure that the reindex settings don't time out
                    billSearchDao.reaffirmReindexing();
                    logger.info("Reindexing session {} bills {} - {}", session,
                            limOff.getOffsetStart(),
                            limOff.getOffsetStart() + bills.size() - 1);
                    updateIndex(bills);
                    limOff = limOff.next();
                }
            }
        } finally {
            // Restore normal index settings.
            billSearchDao.reindexCleanup();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.BILL)) {
            logger.info("Handling bill re-index event!");
            rebuildIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleClearEvent(ClearIndexEvent event) {
        if (event.affects(SearchIndex.BILL)) {
            clearIndex();
        }
    }

    /** --- Internal --- */

    /**
     * Returns true if the given bill meets the criteria for being indexed in the search layer.
     *
     * @param bill Bill
     * @return boolean
     */
    protected boolean isBillIndexable(Bill bill) {
        return bill != null && bill.isBaseVersionPublished();
    }
}