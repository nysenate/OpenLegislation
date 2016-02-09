package gov.nysenate.openleg.service.bill.search;

import com.google.common.collect.Range;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.dao.bill.search.ElasticBillSearchDao;
import gov.nysenate.openleg.config.Environment;
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
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchParseException;
import org.elasticsearch.search.rescore.RescoreBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
            QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("session", session.getYear())),
            null, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String query, String sort, LimitOffset limOff) throws SearchException {
        query = smartSearch(query);
        QueryBuilder queryBuilder = QueryBuilders.queryString(query);
        return searchBills(queryBuilder, null, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(String query, SessionYear session, String sort, LimitOffset limOff) throws SearchException {
        query = smartSearch(query);
        TermFilterBuilder sessionFilter = FilterBuilders.termFilter("session", session.getYear());
        return searchBills(
            QueryBuilders.filteredQuery(QueryBuilders.queryString(query), sessionFilter), null, null, sort, limOff);
    }

    /**
     * Delegates to the underlying bill search dao.
     */
    private SearchResults<BaseBillId> searchBills(QueryBuilder query, FilterBuilder postFilter, RescoreBuilder.Rescorer rescorer,
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
            throw new UnexpectedSearchException(ex);
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
        if (env.isElasticIndexing()) {
            if (isBillIndexable(bill)) {
                logger.info("Indexing bill {} into elastic search.", bill.getBaseBillId());
                billSearchDao.updateBillIndex(bill);
            }
            else if (bill != null) {
                logger.info("Deleting {} from index.", bill.getBaseBillId());
                billSearchDao.deleteBillFromIndex(bill.getBaseBillId());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndex(Collection<Bill> bills) {
        if (env.isElasticIndexing() && !bills.isEmpty()) {
            List<Bill> indexableBills = bills.stream()
                .filter(b -> isBillIndexable(b))
                .collect(toList());
            logger.info("Indexing {} valid bills into elastic search.", indexableBills.size());
            billSearchDao.updateBillIndex(indexableBills);

            // Ensure any bills that currently don't meet the criteria are not in the index.
            if (indexableBills.size() != bills.size()) {
                bills.stream()
                    .filter(b -> !isBillIndexable(b) && b != null)
                    .forEach(b -> {
                        logger.info("Deleting {} from index.", b.getBaseBillId());
                        billSearchDao.deleteBillFromIndex(b.getBaseBillId());
                    });
            }
        }
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
        if (sessions.isPresent()) {
            SessionYear session = sessions.get().lowerEndpoint();
            while (session.getSessionStartYear() <= LocalDate.now().getYear()) {
                LimitOffset limOff = LimitOffset.THOUSAND;
                List<BaseBillId> billIds = billDataService.getBillIds(session, limOff);
                while (!billIds.isEmpty()) {
                    logger.info("Indexing {} bills starting from {}", billIds.size(), billIds.get(0));
                    updateIndex(billIds.stream().map(id -> billDataService.getBill(id)).collect(toList()));
                    limOff = limOff.next();
                    billIds = billDataService.getBillIds(session, limOff);
                }
                session = session.next();
            }
        }
        else {
            logger.info("Can't rebuild the bill search index because there are no bills. Clearing it instead!");
            clearIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleRebuildEvent(RebuildIndexEvent event) {
        if (event.affects(SearchIndex.BILL)) {
            logger.info("Handling bill re-index event!");
            try {
                rebuildIndex();
            }
            catch (Exception ex) {
                logger.error("Unexpected exception during handling of Bill RebuildIndexEvent!", ex);
            }
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