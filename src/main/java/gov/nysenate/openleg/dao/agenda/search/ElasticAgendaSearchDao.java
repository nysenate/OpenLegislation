package gov.nysenate.openleg.dao.agenda.search;

import gov.nysenate.openleg.client.view.agenda.AgendaCommFlatView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.util.OutputUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ElasticAgendaSearchDao extends ElasticBaseDao implements AgendaSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticAgendaSearchDao.class);

    protected static final String agendaIndexName = SearchIndex.AGENDA.getIndexName();

    protected static final String agendaTypeName = agendaIndexName;

    /** {@inheritDoc} */
    @Override
    public SearchResults<AgendaId> searchAgendas(QueryBuilder query, QueryBuilder postFilter,
                                                 List<SortBuilder> sort, LimitOffset limOff) {
        SearchResponse response = searchAgendasResponse(query, postFilter, sort, limOff);
        logger.debug("Agenda Search result with query {} took {} ms", query, response.getTook().getMillis());
        return getSearchResults(response, limOff, this::getAgendaIdFromHit);
    }

    /**
     * Helper method to generate a SearchResponse.
     */
    private SearchResponse searchAgendasResponse(QueryBuilder query, QueryBuilder postFilter,
                                                           List<SortBuilder> sort, LimitOffset limOff){
        SearchRequest searchRequest = getSearchRequest(agendaIndexName, query, postFilter, sort, limOff);
        try {
            return searchClient.search(searchRequest);
        }
        catch (IOException ex){
            logger.warn("Search Agendas request failed.", ex);
        }
        return new SearchResponse();
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(QueryBuilder query, QueryBuilder postFilter,
                                                                   List<SortBuilder> sort, LimitOffset limOff) {
        SearchResponse response = searchAgendasResponse(query, postFilter, sort, limOff);
        logger.debug("Committee Agenda search result with query {} took {} ms", query, response.getTook().getMillis());
        return getSearchResults(response, limOff, (hit) ->
            new CommitteeAgendaId(
                getAgendaIdFromHit(hit), new CommitteeId(Chamber.SENATE, hit.getId()))
        );
    }

    private AgendaId getAgendaIdFromHit(SearchHit hit) {
        String[] type = hit.getType().split("-");
        return new AgendaId(Integer.parseInt(type[1]), Integer.parseInt(type[0]));
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Agenda agenda) {
        updateAgendaIndex(Collections.singletonList(agenda));
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Collection<Agenda> agendas) {
        if (!agendas.isEmpty()) {
            BulkRequest request = new BulkRequest();
            agendas.forEach(agenda ->
                agenda.getCommittees().stream()
                    .map(cid -> new AgendaCommFlatView(agenda, cid, null))
                    .forEach(cfv ->
                        request.add(
                            new IndexRequest(agendaIndexName,
                                agenda.getId().getYear() + "-" + cfv.getAgenda().getId().getNumber(),
                                cfv.getCommittee().getCommitteeId().getName())
                            .source(OutputUtils.toJson(cfv), XContentType.JSON))));
            safeBulkRequestExecute(request);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgendaFromIndex(AgendaId agendaId) {
        if (agendaId != null) {
            deleteEntry(agendaIndexName, Long.toString(agendaId.getNumber()));
        }
    }

    @Override
    protected List<String> getIndices() {
        return Collections.singletonList(agendaIndexName);
    }
}
