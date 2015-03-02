package gov.nysenate.openleg.service.agenda.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.agenda.event.AgendaUpdateEvent;
import gov.nysenate.openleg.service.agenda.event.BulkAgendaUpdateEvent;

public interface AgendaSearchService
{
    /**
     * Performs a search across all committee agendas.
     * @see #searchCommitteeAgendas(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Retrieve all committee agendas for a given year, with sorting.
     * @see #searchCommitteeAgendas(String, int, String, gov.nysenate.openleg.dao.base.LimitOffset)
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Search for committee agendas during a given year.
     *
     * @param query String - Lucene Query string
     * @param year int - Filter retrieved committee agendas by year
     * @param sort String - Lucene sort string
     * @param limOff LimitOffset - Limit the result set.
     * @return SearchResults<CommitteeAgendaId>
     * @throws SearchException
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, int year, String sort, LimitOffset limOff)
        throws SearchException;

    /**
     * Handle an agenda update event by indexing the supplied agenda in the update.
     *
     * @param agendaUpdateEvent AgendaUpdateEvent
     */
    public void handleAgendaUpdateEvent(AgendaUpdateEvent agendaUpdateEvent);

    /**
     * Handle a batch agenda update event by indexing the supplied agendas in the update.
     *
     * @param bulkAgendaUpdateEvent BulkAgendaUpdateEvent
     */
    public void handleBulkAgendaUpdateEvent(BulkAgendaUpdateEvent bulkAgendaUpdateEvent);
}
