package gov.nysenate.openleg.legislation.agenda.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaCommFlatView;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaIdView;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import gov.nysenate.openleg.search.SearchResults;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ElasticAgendaSearchDao extends ElasticBaseDao<AgendaCommFlatView> implements AgendaSearchDao {
    protected static final String agendaIndexName = SearchIndex.AGENDA.getName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<AgendaId> searchAgendas(Query query, Query postFilter,
                                                 List<SortOptions> sort, LimitOffset limOff) {
        return search(agendaIndexName, query, postFilter, sort, limOff, ElasticAgendaSearchDao::getAgendaIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(Query query, Query postFilter,
                                                                   List<SortOptions> sort, LimitOffset limOff) {
        return search(agendaIndexName, query, postFilter, sort, limOff, ElasticAgendaSearchDao::getCommAgendaId);
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Agenda agenda) {
        updateAgendaIndex(Collections.singletonList(agenda));
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Collection<Agenda> agendas) {
        var bulkBuilder = new BulkOperation.Builder();
        for (Agenda agenda : agendas) {
            for (CommitteeId commId : agenda.getCommittees()) {
                var commAgendaId = new CommitteeAgendaId(agenda.getId(), commId);
                var view = new AgendaCommFlatView(agenda, commId, null);
                bulkBuilder.index(getIndexOperationRequest(agendaIndexName, commAgendaId.toString(), view));
            }
        }
        safeBulkRequestExecute(BulkRequest.of(b -> b.index(agendaIndexName).operations(bulkBuilder.build())));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAgendaFromIndex(AgendaId agendaId) {
        if (agendaId != null) {
            deleteEntry(agendaIndexName, Long.toString(agendaId.getNumber()));
        }
    }

    @Override
    protected SearchIndex getIndex() {
        return SearchIndex.AGENDA;
    }

    /**
     * Allocate additional shards for agenda index.
     *
     * @return Settings.Builder
     */
    @Override
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("2");
    }

    /* --- Internal Methods --- */

    private static AgendaId getAgendaIdFromHit(AgendaCommFlatView hit) {
        AgendaIdView idView = hit.agenda().getId();
        return new AgendaId(idView.number(), idView.year());
    }

    private static CommitteeAgendaId getCommAgendaId(AgendaCommFlatView hit) {
        return new CommitteeAgendaId(getAgendaIdFromHit(hit), hit.committee().committeeId());
    }
}
