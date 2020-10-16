package gov.nysenate.openleg.dao.agenda.search;

import gov.nysenate.openleg.client.view.agenda.AgendaCommFlatView;
import gov.nysenate.openleg.dao.base.ElasticBaseDao;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SearchIndex;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.entity.CommitteeId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
public class ElasticAgendaSearchDao extends ElasticBaseDao implements AgendaSearchDao
{
    private static final Logger logger = LoggerFactory.getLogger(ElasticAgendaSearchDao.class);

    protected static final String agendaIndexName = SearchIndex.AGENDA.getIndexName();

    /** {@inheritDoc} */
    @Override
    public SearchResults<AgendaId> searchAgendas(QueryBuilder query, QueryBuilder postFilter,
                                                 List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(agendaIndexName, query, postFilter, sort, limOff, this::getAgendaIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(QueryBuilder query, QueryBuilder postFilter,
                                                                   List<SortBuilder<?>> sort, LimitOffset limOff) {
        return search(agendaIndexName, query, postFilter, sort, limOff, this::getCommAgendaIdFromHit);
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Agenda agenda) {
        updateAgendaIndex(Collections.singletonList(agenda));
    }

    /** {@inheritDoc} */
    @Override
    public void updateAgendaIndex(Collection<Agenda> agendas) {
        BulkRequest request = new BulkRequest();
        agendas.forEach(agenda -> addAgendaToBulkIndex(agenda, request));
        safeBulkRequestExecute(request);
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

    /**
     * Allocate additional shards for agenda index.
     *
     * @return Settings.Builder
     */
    @Override
    protected Settings.Builder getIndexSettings() {
        Settings.Builder indexSettings = super.getIndexSettings();
        indexSettings.put("index.number_of_shards", 2);
        return indexSettings;
    }

    /* --- Internal Methods --- */

    private AgendaId getAgendaIdFromHit(SearchHit hit) {
        String[] id = hit.getId().split("-");
        return new AgendaId(Integer.parseInt(id[1]), Integer.parseInt(id[0]));
    }

    private CommitteeAgendaId getCommAgendaIdFromHit(SearchHit hit) {
        return new CommitteeAgendaId(
                getAgendaIdFromHit(hit), new CommitteeId(Chamber.SENATE, hit.getId().split("-")[2]));
    }

    private String toElasticId(CommitteeAgendaId commAgendaId) {
        AgendaId agendaId = commAgendaId.getAgendaId();
        CommitteeId commId = commAgendaId.getCommitteeId();
        return agendaId.getYear() + "-" +
                agendaId.getNumber() + "-" +
                commId.getName();
    }

    private void addAgendaToBulkIndex(Agenda agenda, BulkRequest request) {
        agenda.getCommittees().stream()
                .map(cid -> getJsonIndexRequest(agendaIndexName,
                        toElasticId(new CommitteeAgendaId(agenda.getId(), cid)),
                        new AgendaCommFlatView(agenda, cid, null)))
                .forEach(request::add);
    }
}
