package gov.nysenate.openleg.search.committee;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.api.legislation.committee.view.CommitteeView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.SessionYear;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class ElasticCommitteeSearchDao extends ElasticBaseDao implements CommitteeSearchDao {
    private static final String committeeSearchIndexName = SearchIndex.COMMITTEE.getIndexName();
    private static final Pattern committeeSearchIdPattern =
            Pattern.compile("(SENATE|ASSEMBLY)-([A-z, ]*)-(\\d{4})-(.*)");

    private final CommitteeDataService committeeDataService;

    @Autowired
    public ElasticCommitteeSearchDao(CommitteeDataService committeeDataService) {
        this.committeeDataService = committeeDataService;
    }

    @Override
    public SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, QueryBuilder filter,
                                                              List<SortBuilder<?>> sort, LimitOffset limitOffset) {
        return search(committeeSearchIndexName, query, filter, sort, limitOffset, this::getCommitteeVersionId);
    }

    @Override
    public void updateCommitteeIndex(CommitteeSessionId committeeSessionId) {
        updateCommitteeIndexBulk(Collections.singletonList(committeeSessionId));
    }

    @Override
    public void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds) {
        BulkRequest bulkRequest = new BulkRequest();
        sessionIds.stream()
                .peek(this::deleteCommitteeFromIndex)
                .forEach(sessionId -> committeeHistoryIndexBulkAdd(sessionId, bulkRequest));
        safeBulkRequestExecute(bulkRequest);
    }

    @Override
    public void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId) {
        BulkRequest bulkRequest = getCommitteeDeleteRequest(committeeSessionId);
        safeBulkRequestExecute(bulkRequest);
    }

    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(committeeSearchIndexName);
    }

    @Override
    protected HashMap<String, Object> getCustomMappingProperties() throws IOException {
        HashMap<String, Object> props = super.getCustomMappingProperties();
        props.put("meetTime", basicTimeMapping);
        return props;
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
        BulkRequest request = new BulkRequest();
        SearchResults<CommitteeVersionId> searchResults = searchCommittees(
                getCommitteeSessionQuery(committeeSessionId), null, Collections.emptyList(), LimitOffset.ALL);

        searchResults.resultList().stream()
                .map(SearchResult::result)
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
        committeeDataService.getCommitteeHistory(committeeSessionId).stream()
                .map(this::getCommitteeVersionIndexRequest)
                .forEach(bulkRequest::add);
    }

    private DeleteRequest getCommitteeVersionDeleteRequest(CommitteeVersionId committeeVersionId) {
        return new DeleteRequest(
                committeeSearchIndexName,
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
        return getJsonIndexRequest(committeeSearchIndexName,
                generateCommitteeVersionSearchId(committee.getVersionId()),
                new CommitteeView(committee));
    }

    /* --- Id Mappers --- */

    private String generateCommitteeVersionSearchId(CommitteeVersionId committeeVersionId) {
        return generateCommitteeSessionSearchId(committeeVersionId) + "-" +
                committeeVersionId.getReferenceDate();
    }

    private String generateCommitteeSessionSearchId(CommitteeSessionId committeeSessionId){
        return generateCommitteeSearchId(committeeSessionId) + "-" +
                committeeSessionId.getSession().toString();
    }

    private String generateCommitteeSearchId(CommitteeId committeeId) {
        return committeeId.getChamber() + "-" +
                committeeId.getName();
    }

    private CommitteeVersionId getCommitteeVersionId(SearchHit hit) {
        Matcher versionIdMatcher = committeeSearchIdPattern.matcher(hit.getId());
        if (!versionIdMatcher.find()){
            return null;
        }
        return new CommitteeVersionId(Chamber.getValue(versionIdMatcher.group(1)), versionIdMatcher.group(2),
                new SessionYear(Integer.parseInt(versionIdMatcher.group(3))),
                LocalDateTime.parse(versionIdMatcher.group(4), DateTimeFormatter.ISO_DATE_TIME));
    }
}
