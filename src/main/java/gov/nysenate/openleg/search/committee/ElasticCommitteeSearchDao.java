package gov.nysenate.openleg.search.committee;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.mapping.Property;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.DeleteOperation;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.committee.*;
import gov.nysenate.openleg.legislation.committee.dao.CommitteeDataService;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResult;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticCommitteeSearchDao extends ElasticBaseDao<Committee> implements CommitteeSearchDao {
    private static final String committeeSearchIndexName = SearchIndex.COMMITTEE.getName();

    private final CommitteeDataService committeeDataService;

    @Autowired
    public ElasticCommitteeSearchDao(CommitteeDataService committeeDataService) {
        this.committeeDataService = committeeDataService;
    }

    @Override
    public SearchResults<CommitteeVersionId> searchCommittees(Query query, Query filter,
                                                              List<SortOptions> sort, LimitOffset limitOffset) {
        return search(committeeSearchIndexName, query, filter, null, null, sort, limitOffset,
                true, Committee::getVersionId);
    }

    @Override
    public void updateCommitteeIndex(CommitteeSessionId committeeSessionId) {
        updateCommitteeIndexBulk(Collections.singletonList(committeeSessionId));
    }

    @Override
    public void updateCommitteeIndexBulk(Collection<CommitteeSessionId> sessionIds) {
        var bulkBuilder = new BulkOperation.Builder();
        sessionIds.stream().peek(this::deleteCommitteeFromIndex)
                .flatMap(sessionId -> committeeDataService.getCommitteeHistory(sessionId).stream())
                .map(comm -> getIndexOperationRequest(committeeSearchIndexName, comm.getVersionId().toString(), comm))
                .forEach(bulkBuilder::index);
        safeBulkRequestExecute(BulkRequest.of(b -> b.operations(bulkBuilder.build())));
    }

    @Override
    public void deleteCommitteeFromIndex(CommitteeSessionId committeeSessionId) {
        BulkRequest bulkRequest = getCommitteeDeleteRequest(committeeSessionId);
        safeBulkRequestExecute(bulkRequest);
    }

    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.COMMITTEE;
    }

    @Override
    protected ImmutableMap<String, Property> getCustomMappingProperties() {
        return ImmutableMap.of("meetTime", basicTimeMapping);
    }

    /* --- Internal Methods --- */

    /**
     * Generates a delete request that will delete all indexed committee responses for a specific committee
     * for a specific session year.
     */
    private BulkRequest getCommitteeDeleteRequest(CommitteeSessionId committeeSessionId) {
        var bulkBuilder = new BulkOperation.Builder();
        searchCommittees(getCommitteeSessionQuery(committeeSessionId), null, List.of(), LimitOffset.ALL)
                .resultList().stream().map(SearchResult::result)
                .map(id -> DeleteOperation.of(b -> b.index(committeeSearchIndexName).id(id.toString())))
                .forEach(bulkBuilder::delete);
        return BulkRequest.of(b -> b.index(committeeSearchIndexName).operations(bulkBuilder.build()));
    }

    /**
     * Builds a query to match all committee versions for the given committee session.
     */
    private static Query getCommitteeSessionQuery(CommitteeSessionId committeeSessionId) {
        var chamberBuilder = new MatchQuery.Builder().field("chamber")
                .query(committeeSessionId.getChamber().toString().toLowerCase());
        var nameBuilder = new MatchQuery.Builder().field("name")
                        .query(committeeSessionId.getName().toLowerCase());
        return BoolQuery.of(b -> b.must(chamberBuilder.build()._toQuery(), nameBuilder.build()._toQuery()))._toQuery();
    }
}
