package gov.nysenate.openleg.legislation.agenda.dao;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.agenda.Agenda;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.legislation.agenda.AgendaId;
import gov.nysenate.openleg.search.SearchResults;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching agenda data.
 */
public interface AgendaSearchDao {
    /**
     * Performs a search across all agendas broken down by committee.
     *
     * @param query  String - Query Builder
     * @param sort   String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<AgendaCommitteeId>
     */
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(Query query,
                                                            List<SortOptions> sort, LimitOffset limOff);

    /**
     * Update the agenda index with the content of the supplied agenda.
     *
     * @param agenda Agenda
     */
    void updateAgendaIndex(Agenda agenda);

    /**
     * Update the agenda index with the contents of the supplied agendas.
     *
     * @param agendas Collection<Agenda>
     */
    void updateAgendaIndex(Collection<Agenda> agendas);

    /**
     * Removes the agenda from the index with the given AgendaId.
     *
     * @param agendaId AgendaId
     */
    void deleteAgendaFromIndex(AgendaId agendaId);
}
