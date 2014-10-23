package gov.nysenate.openleg.dao.calendar;

import com.google.common.primitives.Ints;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.calendar.CalendarActiveListId;
import gov.nysenate.openleg.model.calendar.CalendarId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.service.base.SearchResult;
import gov.nysenate.openleg.service.base.SearchResults;
import org.elasticsearch.action.search.SearchRequestBuilder;
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
public class ElasticCalendarSearchDao extends ElasticBaseDao implements CalendarSearchDao {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCalendarSearchDao.class);

    @Value("${elastic.search.index.calendar.name:calendars")
    protected String calIndexName;

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarId> searchCalendars(String query, String sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchBuilder = searchClient.prepareSearch(calIndexName)
                .setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.queryString(query))
                .setFrom(limitOffset.getOffsetStart() - 1)
                .setSize((limitOffset.hasLimit()) ? limitOffset.getLimit() : -1)
                .setFetchSource(false);

        extractSortFilters(sort).forEach(searchBuilder::addSort);

        SearchResponse response = searchBuilder.execute().actionGet();
        List<SearchResult<CalendarId>> resultList = new ArrayList<>();
        for (SearchHit hit : response.getHits().hits()) {
            SearchResult<CalendarId> result = new SearchResult<>(
                    new CalendarId(Integer.parseInt(hit.id()), Integer.parseInt(hit.type())),
                    (!Float.isNaN(hit.getScore())) ? BigDecimal.valueOf(hit.getScore()) : BigDecimal.ONE);
            resultList.add(result);
        }
        return new SearchResults<>(Ints.checkedCast(response.getHits().getTotalHits()), resultList, limitOffset);
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarSupplementalId> searchFloorCalendars(String query, String sort, LimitOffset limitOffset) {
        return null;
    }

    /**{@inheritDoc}*/
    @Override
    public SearchResults<CalendarActiveListId> searchActiveLists(String query, String sort, LimitOffset limitOffset) {
        return null;
    }
}
