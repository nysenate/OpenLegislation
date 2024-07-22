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
     * Performs a search on the top level agenda.
     * @see #searchAgendas(Query, Query, List, LimitOffset)
     * @returns SearchResults<AgendaId>
     */
    SearchResults<AgendaId> searchAgendas(Query query, Query postFilter,
                                          List<SortOptions> sort, LimitOffset limOff);

    /**
     * Performs a search across all agendas broken down by committee.
     *
     * @param query String - Query Builder
     * @param postFilter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<AgendaCommitteeId>
     */
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(Query query, Query postFilter,
                                                            List<SortOptions> sort, LimitOffset limOff);

    /**
     * Update the agenda index with the content of the supplied agenda.
     *
     * @param agenda Agenda
     */
    public void updateAgendaIndex(Agenda agenda);

    /**
     * Update the agenda index with the contents of the supplied agendas.
     *
     * @param agendas Collection<Agenda>
     */
    public void updateAgendaIndex(Collection<Agenda> agendas);

    /**
     * Removes the agenda from the index with the given AgendaId.
     *
     * @param agendaId AgendaId
     */
    public void deleteAgendaFromIndex(AgendaId agendaId);
}
