package gov.nysenate.openleg.dao.entity.committee.search;

import com.google.common.collect.Lists;
import gov.nysenate.openleg.client.view.committee.CommitteeView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.*;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.entity.committee.data.CommitteeDataService;
import gov.nysenate.openleg.model.entity.CommitteeNotFoundEx;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class ElasticCommitteeSearchDao extends ElasticBaseDao implements CommitteeSearchDao {

    private static final Logger logger = LoggerFactory.getLogger(ElasticCommitteeSearchDao.class);

    private static final String committeeSearchIndexName = "committees";

    private static final Pattern committeeSearchIdPattern =
            Pattern.compile("(SENATE|ASSEMBLY)-([A-z, ]*)-(.*)");

    @Autowired
    CommitteeDataService committeeDataService;

    @Override
    public SearchResults<CommitteeVersionId> searchCommittees(QueryBuilder query, FilterBuilder filter,
                                                              List<SortBuilder> sort, LimitOffset limitOffset) {
        SearchRequestBuilder searchRequest = getSearchRequest(committeeSearchIndexName, query, filter, sort, limitOffset);
        SearchResponse response = searchRequest.execute().actionGet();
        return getSearchResults(response, limitOffset, this::getCommitteeVersionId);
    }

    @Override
    public void updateCommitteeIndex(CommitteeSessionId committeeSessionId) {
        deleteCommitteeFromIndex(committeeSessionId);
        BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
        committeeHistoryIndexBulkAdd(committeeSessionId, bulkRequest);
        bulkRequest.execute().actionGet();
    }

    @Override
    public void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds) {
        if (sessionIds.isEmpty()) {
            return;
        }
        BulkRequestBuilder bulkRequest = searchClient.prepareBulk();
        sessionIds.stream()
                .peek(this::deleteCommitteeFromIndex)
                .forEach(sessionId -> committeeHistoryIndexBulkAdd(sessionId, bulkRequest));
        bulkRequest.execute().actionGet();
    }

    @Override
    public void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId) {
        getCommitteeDeleteRequest(committeeSessionId).execute().actionGet();
    }

    @Override
    protected List<String> getIndices() {
        return Lists.newArrayList(committeeSearchIndexName);
    }

    /**
     * --- Internal Methods ---
     */

    /**
     * Generates a delete request that will delete all indexed committee responses for a specific committee
     *  for a specific session year
     *
     * @param committeeSessionId
     * @return
     */
    protected DeleteByQueryRequestBuilder getCommitteeDeleteRequest(CommitteeSessionId committeeSessionId) {
        return searchClient.prepareDeleteByQuery(committeeSearchIndexName)
                .setTypes(Integer.toString(committeeSessionId.getSession().getYear()))
                .setQuery(QueryBuilders.filteredQuery( QueryBuilders.matchAllQuery(),
                        FilterBuilders.boolFilter()
                            .must(FilterBuilders.termFilter("chamber", committeeSessionId.getChamber().toString()))
                            .must(FilterBuilders.termFilter("name", committeeSessionId.getName())) ) );
    }

    /**
     * Adds index requests for all committee versions for a committee session history to the given bulk request
     *
     * @param committeeSessionId
     * @param bulkRequest
     */
    protected void committeeHistoryIndexBulkAdd(CommitteeSessionId committeeSessionId, BulkRequestBuilder bulkRequest) {
        try {
            committeeDataService.getCommitteeHistory(committeeSessionId).stream()
                    .map(this::getCommitteeVersionIndexRequest)
                    .forEach(bulkRequest::add);
        } catch (CommitteeNotFoundEx ex) {
            logger.warn(ExceptionUtils.getStackTrace(ex));
        }
    }

    /**
     * Generates an index request for a single committee version
     *
     * @param committee
     * @return
     */
    protected IndexRequestBuilder getCommitteeVersionIndexRequest(Committee committee) {
        return searchClient.prepareIndex(committeeSearchIndexName,
                Integer.toString(committee.getSession().getYear()),
                generateCommitteeVersionSearchId(committee.getVersionId()))
                .setSource(OutputUtils.toJson(new CommitteeView(committee)));
    }

    /**
     * --- Id Mappers ---
     */

    protected String generateCommitteeVersionSearchId(CommitteeVersionId committeeVersionId) {
        return generateCommitteeSearchId(committeeVersionId) + "-" +
                committeeVersionId.getReferenceDate();
    }

    protected String generateCommitteeSearchId(CommitteeId committeeId) {
        return committeeId.getChamber() + "-" +
                committeeId.getName();
    }

    protected CommitteeVersionId getCommitteeVersionId(SearchHit hit) {
        Matcher versionIdMatcher = committeeSearchIdPattern.matcher(hit.getId());
        versionIdMatcher.find();
        return new CommitteeVersionId(Chamber.getValue(versionIdMatcher.group(1)), versionIdMatcher.group(2),
                SessionYear.of(Integer.parseInt(hit.getType())),
                LocalDateTime.parse(versionIdMatcher.group(3), DateTimeFormatter.ISO_DATE_TIME));
    }
}
