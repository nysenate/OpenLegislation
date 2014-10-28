package gov.nysenate.openleg.dao.bill.search;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.client.view.bill.BillView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ElasticBillSearchDao extends ElasticBaseDao implements BillSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchDao.class);

    protected static final String billIndexName = SearchIndex.BILL.getIndexName();

    @PostConstruct
    public void init() {
        if (!billIndexExists()) {
            logger.warn("ElasticSearch Bill index doesn't exist. Creating it now.");
            createBillIndex();
        }
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(QueryBuilder query, String sort, LimitOffset limOff) {
        return searchBills(query, null, sort, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<BaseBillId> searchBills(QueryBuilder query, FilterBuilder postFilter, String sort, LimitOffset limOff) {
        SearchRequestBuilder searchBuilder = searchClient.prepareSearch(billIndexName)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(query)
                .setFrom(limOff.getOffsetStart() - 1)
                .setSize((limOff.hasLimit()) ? limOff.getLimit() : -1)
                .setFetchSource(false);
        if (postFilter != null) {
            searchBuilder.setPostFilter(postFilter);
        }
        // Add the sort by fields
        extractSortFilters(sort).forEach(searchBuilder::addSort);
        // Perform the search request
        SearchResponse response = searchBuilder.execute().actionGet();
        logger.debug("Bill search result with query {} took {} ms", query, response.getTookInMillis());
        List<SearchResult<BaseBillId>> resultList = new ArrayList<>();
        for (SearchHit hit : response.getHits().hits()) {
            SearchResult<BaseBillId> result = new SearchResult<>(
                    new BaseBillId(hit.getId(), Integer.parseInt(hit.getType())),
                    (!Float.isNaN(hit.getScore())) ? BigDecimal.valueOf(hit.getScore()) : BigDecimal.ONE);
            resultList.add(result);
        }
        return new SearchResults<>(Ints.checkedCast(response.getHits().getTotalHits()), resultList, limOff);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Bill bill) {
        searchClient.prepareIndex(billIndexName, bill.getSession().toString(), bill.getBasePrintNo())
                    .setSource(OutputUtils.toJson(new BillView(bill)))
                    .execute().actionGet();
    }

    /** {@inheritDoc} */
    @Override
    public void updateBillIndex(Collection<Bill> bills) {
        BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
        List<BillView> billViewList = bills.stream().map(b -> new BillView(b)).collect(Collectors.toList());
        billViewList.forEach(b ->
            bulkRequest.add(searchClient.prepareIndex(billIndexName, Integer.toString(b.getSession()), b.getBasePrintNo())
                       .setSource(OutputUtils.toJson(b)))
        );
        bulkRequest.execute().actionGet();
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBillFromIndex(BaseBillId baseBillId) {
        if (baseBillId != null) {
            DeleteRequestBuilder request = new DeleteRequestBuilder(searchClient, billIndexName);
            request.setType(baseBillId.getSession().toString());
            request.setId(baseBillId.getPrintNo());
            request.execute().actionGet();
        }
    }

    protected boolean billIndexExists() {
        return indicesExist(billIndexName);
    }

    protected void createBillIndex() {
        createIndex(billIndexName);
    }

    protected void deleteBillIndex() {
        deleteIndex(billIndexName);
    }
}