package gov.nysenate.openleg.legislation.agenda.dao;

import co.elastic.clients.elasticsearch.indices.IndexSettings;
import gov.nysenate.openleg.api.legislation.agenda.view.AgendaCommFlatView;
import gov.nysenate.openleg.common.util.Tuple;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import gov.nysenate.openleg.search.ElasticBaseDao;
import gov.nysenate.openleg.search.SearchIndex;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;

@Repository
public class ElasticAgendaSearchDao extends ElasticBaseDao<CommitteeAgendaId, AgendaCommFlatView, Tuple<Agenda, CommitteeId>> {
    public void indexAgendas(Collection<Agenda> agendas) {
        var tuples = new ArrayList<Tuple<Agenda, CommitteeId>>();
        for (Agenda agenda : agendas) {
            agenda.getCommittees().stream().map(commId -> new Tuple<>(agenda, commId)).forEach(tuples::add);
        }
        updateIndex(tuples);
    }

    @Override
    public SearchIndex getIndex() {
        return SearchIndex.AGENDA;
    }

    @Override
    protected CommitteeAgendaId getId(Tuple<Agenda, CommitteeId> data) {
        return new CommitteeAgendaId(data.v1().getId(), data.v2());
    }

    @Override
    protected AgendaCommFlatView getDoc(Tuple<Agenda, CommitteeId> data) {
        return new AgendaCommFlatView(data.v1(), data.v2(), null);
    }

    @Override
    protected CommitteeAgendaId toId(String idStr) {
        String[] parts = idStr.split("-");
        var agendaId = new AgendaId(Integer.parseInt(parts[1]), Integer.parseInt(parts[0]));
        var comId = new CommitteeId(Chamber.valueOf(parts[2]), parts[3]);
        return new CommitteeAgendaId(agendaId, comId);
    }

    /**
     * Allocate additional shards for agenda index.
     * @return Settings.Builder
     */
    @Override
    protected IndexSettings.Builder getIndexSettings() {
        return super.getIndexSettings().numberOfShards("2");
    }
}
