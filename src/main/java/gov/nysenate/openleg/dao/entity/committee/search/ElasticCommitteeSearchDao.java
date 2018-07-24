package gov.nysenate.openleg.dao.entity.committee.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.committee.CommitteeView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.SearchResult;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import gov.nysenate.openleg.model.entity.CommitteeNotFoundEx;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class ElasticCommitteeSearchDao extends ElasticBaseDao implements CommitteeSearchDao {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCommitteeSearchDao.class);

    private static final String committeeSearchIndexName = SearchIndex.COMMITTEE.getIndexName();

    private static final Pattern committeeSearchIdPattern =
            Pattern.compile("(SENATE|ASSEMBLY)-([A-z, ]*)-(.*)");

    private static final String[] filteredFields = {"sessionYear"};

    @Autowired
    CommitteeDataService committeeDataService;

    @Override
    public SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, QueryBuilder filter,
                                                              List<SortBuilder> sort, LimitOffset limitOffset) {
        SearchRequest searchRequest = getSearchRequest(committeeSearchIndexName, query, filter, sort, limitOffset, filteredFields);
        SearchResponse searchResponse = new SearchResponse();
        try {
            searchResponse = searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.warn("Search Committees request failed.", ex);
        }

        return getSearchResults(searchResponse, limitOffset, this::getCommitteeVersionId);
    }

    @Override
    public void updateCommitteeIndex(CommitteeSessionId committeeSessionId) {
        deleteCommitteeFromIndex(committeeSessionId);
        BulkRequest bulkRequest = new BulkRequest();
        committeeHistoryIndexBulkAdd(committeeSessionId, bulkRequest);
        try {
            searchClient.bulk(bulkRequest);
        }
        catch (IOException ex){
            logger.warn("Update Committees request failed.", ex);
        }
    }

    @Override
    public void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds) {
        if (sessionIds.isEmpty()) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        sessionIds.stream()
                .peek(this::deleteCommitteeFromIndex)
                .forEach(sessionId -> committeeHistoryIndexBulkAdd(sessionId, bulkRequest));
        try {
            searchClient.bulk(bulkRequest);
        }
        catch (IOException ex){
            logger.warn("Bulk Update Committee request failed.", ex);
        }
    }

    @Override
    public void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId) {
        BulkRequest bulkRequest = getCommitteeDeleteRequest(committeeSessionId);
        if (bulkRequest.numberOfActions() > 0) {
            try {
                searchClient.bulk(bulkRequest);
            }
            catch (IOException ex){
                logger.warn("Bulk Delete Committee request failed.", ex);
            }
        }
    }

    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(committeeSearchIndexName);
    }

    /* --- Internal Methods --- */

    /**
     * Generates a delete request that will delete all indexed committee responses for a specific committee
     *  for a specific session year
     *
     * @param committeeSessionId {@link CommitteeSessionId}
     * @return BulkRequestBuilder
     */
    private BulkRequest getCommitteeDeleteRequest(CommitteeSessionId committeeSessionId) {
        SearchResults<CommitteeVersionId> searchResults = searchCommittees(
                getCommitteeSessionQuery(committeeSessionId), null, Collections.emptyList(), LimitOffset.ALL);
        BulkRequest request = new BulkRequest();

        searchResults.getResults().stream()
                .map(SearchResult::getResult)
                .map(this::getCommitteeVersionDeleteRequest)
                .forEach(request::add);

        return request;
    }

    /**
     * Builds a query to match all committee versions for the given committee session.
     *
     * @param committeeSessionId {@link CommitteeSessionId}
     * @return BoolQueryBuilder
     */
    private BoolQueryBuilder getCommitteeSessionQuery(CommitteeSessionId committeeSessionId) {
        return QueryBuilders.boolQuery()
                .must(QueryBuilders.matchAllQuery())
                .filter(QueryBuilders.termQuery("chamber", committeeSessionId.getChamber().toString().toLowerCase()))
                .filter(QueryBuilders.termQuery("name", committeeSessionId.getName().toLowerCase()));
    }

    /**
     * Adds index requests for all committee versions for a committee session history to the given bulk request
     *
     * @param committeeSessionId {@link CommitteeSessionId}
     * @param bulkRequest BulkRequestBuilder
     */
    private void committeeHistoryIndexBulkAdd(CommitteeSessionId committeeSessionId, BulkRequest bulkRequest) {
        try {
            committeeDataService.getCommitteeHistory(committeeSessionId).stream()
                    .map(this::getCommitteeVersionIndexRequest)
                    .forEach(bulkRequest::add);
        } catch (CommitteeNotFoundEx ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    protected DeleteRequest getCommitteeVersionDeleteRequest(CommitteeVersionId committeeVersionId) {
        return new DeleteRequest(
                committeeSearchIndexName,
                defaultType,
                generateCommitteeVersionSearchId(committeeVersionId)
        );
    }

    /**
     * Generates an index request for a single committee version
     *
     * @param committee
     * @return
     */
    private IndexRequest getCommitteeVersionIndexRequest(Committee committee) {
        return new IndexRequest(committeeSearchIndexName,
                defaultType,
                generateCommitteeVersionSearchId(committee.getVersionId()))
                .source(OutputUtils.toJson(new CommitteeView(committee)), XContentType.JSON);
    }

    /* --- Id Mappers --- */

    private String generateCommitteeVersionSearchId(CommitteeVersionId committeeVersionId) {
        return generateCommitteeSearchId(committeeVersionId) + "-" +
                committeeVersionId.getReferenceDate();
    }

    private String generateCommitteeSearchId(CommitteeId committeeId) {
        return committeeId.getChamber() + "-" +
                committeeId.getName();
    }

    private CommitteeVersionId getCommitteeVersionId(SearchHit hit) {
        Matcher versionIdMatcher = committeeSearchIdPattern.matcher(hit.getId());
        versionIdMatcher.find();
        return new CommitteeVersionId(Chamber.getValue(versionIdMatcher.group(1)), versionIdMatcher.group(2),
                SessionYear.of((Integer)hit.getSourceAsMap().get(filteredFields[0])),
                LocalDateTime.parse(versionIdMatcher.group(3), DateTimeFormatter.ISO_DATE_TIME));
    }
}
