package gov.nysenate.openleg.dao.bill.search;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ElasticBillSearchDao extends ElasticBaseDao implements BillSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticBillSearchDao.class);

    @Value("${elastic.search.index.bill.name:bills}")
    protected String billIndexName;

    @Override
    public SearchResults<BaseBillId> searchBills(String query, LimitOffset limOff) {
        SearchResponse response = searchClient.prepareSearch(billIndexName)
            .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
            .setQuery(QueryBuilders.queryString(query))
            .setFrom(limOff.getOffsetStart() - 1)
            .setSize((limOff.hasLimit()) ? limOff.getLimit() : -1)
            .setFetchSource(false)
            .execute()
            .actionGet();

        logger.debug("Bill search result with query {} took {} ms", query, response.getTookInMillis());
        List<SearchResult<BaseBillId>> resultList = new ArrayList<>();
        for (SearchHit hit : response.getHits().hits()) {
            SearchResult<BaseBillId> result = new SearchResult<>(
                new BaseBillId(hit.getId(), Integer.parseInt(hit.getType())),
                BigDecimal.valueOf(hit.getScore()));
            resultList.add(result);
        }
        return new SearchResults<>(Ints.checkedCast(response.getHits().getTotalHits()), resultList, limOff);
    }
}
