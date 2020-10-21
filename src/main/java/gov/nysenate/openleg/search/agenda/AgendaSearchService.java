package gov.nysenate.openleg.search.agenda;

import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;

public interface AgendaSearchService
{
    /**
     * Performs a search across all committee agendas.
     * @see #searchCommitteeAgendas(String, int, String, LimitOffset)
     */
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Retrieve all committee agendas for a given year, with sorting.
     * @see #searchCommitteeAgendas(String, int, String, LimitOffset)
     */
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(int year, String sort, LimitOffset limOff) throws SearchException;

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
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, int year, String sort, LimitOffset limOff)
        throws SearchException;

    /**
     * Handle an agenda update event by indexing the supplied agenda in the update.
     *
     * @param agendaUpdateEvent AgendaUpdateEvent
     */
    void handleAgendaUpdateEvent(AgendaUpdateEvent agendaUpdateEvent);

    /**
     * Handle a batch agenda update event by indexing the supplied agendas in the update.
     *
     * @param bulkAgendaUpdateEvent BulkAgendaUpdateEvent
     */
    void handleBulkAgendaUpdateEvent(BulkAgendaUpdateEvent bulkAgendaUpdateEvent);
}
