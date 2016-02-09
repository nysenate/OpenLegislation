package gov.nysenate.openleg.dao.agenda.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.search.SearchResults;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.util.Collection;
import java.util.List;

/**
 * DAO interface for searching agenda data.
 */
public interface AgendaSearchDao
{
    /**
     * Performs a search on the top level agenda.
     * @see #searchAgendas(QueryBuilder, FilterBuilder, List, LimitOffset)
     * @returns SearchResults<AgendaId>
     */
    public SearchResults<AgendaId> searchAgendas(QueryBuilder query, FilterBuilder postFilter,
                                                 List<SortBuilder> sort, LimitOffset limOff);

    /**
     * Performs a search across all agendas broken down by committee.
     *
     * @param query String - Query Builder
     * @param postFilter FilterBuilder - Filter result set
     * @param sort String - Sort String
     * @param limOff LimitOffset - Limit the result set
     * @return SearchResults<AgendaCommitteeId>
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(QueryBuilder query, FilterBuilder postFilter,
                                                                   List<SortBuilder> sort, LimitOffset limOff);

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
