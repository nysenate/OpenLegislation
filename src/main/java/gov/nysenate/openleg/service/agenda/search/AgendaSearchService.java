package gov.nysenate.openleg.service.agenda.search;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.agenda.Agenda;
import gov.nysenate.openleg.model.agenda.AgendaId;
import gov.nysenate.openleg.model.agenda.CommitteeAgendaId;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.agenda.event.AgendaUpdateEvent;
import gov.nysenate.openleg.service.agenda.event.BulkAgendaUpdateEvent;
import gov.nysenate.openleg.service.base.search.IndexedSearchService;

public interface AgendaSearchService
{
    /**
     *
     * @param query String
     * @param sort String
     * @param limOff LimitOffset
     * @return SearchResults<CommitteeAgendaId>
     * @throws SearchException
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     *
     * @param year int
     * @param sort String
     * @param limOff LimitOffset
     * @return SearchResults<CommitteeAgendaId>
     * @throws SearchException
     */
    public SearchResults<CommitteeAgendaId> searchCommitteeAgendas(int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Search for committee agendas using a free-form search.
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
