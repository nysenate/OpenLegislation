package gov.nysenate.openleg.search.agenda;

import gov.nysenate.openleg.updates.agenda.AgendaUpdateEvent;
import gov.nysenate.openleg.updates.agenda.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.legislation.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.search.SearchException;
import gov.nysenate.openleg.search.SearchResults;

public interface AgendaSearchService {
    /**
     * Performs a search across all committee agendas.
     */
    default SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, String sort, LimitOffset limOff)
            throws SearchException {
        return searchCommitteeAgendas(query, null, sort, limOff);
    }

    /**
     * Search for committee agendas during a given year.
     *
     * @param query String - Lucene Query string
     * @param year int - Filter retrieved committee agendas by year
     * @param sort String - Lucene sort string
     * @param limOff LimitOffset - Limit the result set.
     * @return SearchResults<CommitteeAgendaId>
     */
    SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, Integer year, String sort, LimitOffset limOff)
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
