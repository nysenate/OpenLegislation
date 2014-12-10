package gov.nysenate.openleg.service.hearing.search;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.search.SearchException;
import gov.nysenate.openleg.model.search.SearchResults;
import gov.nysenate.openleg.service.hearing.event.PublicHearingUpdateEvent;
import gov.nysenate.openleg.service.hearing.event.BulkPublicHearingUpdateEvent;

public interface PublicHearingSearchService
{

    /**
     * Performs a search of all public hearing id's
     */
    public SearchResults<PublicHearingId> searchPublicHearings(String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search of public hearing id's by year.
     */
    public SearchResults<PublicHearingId> searchPublicHearings(int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all public hearings.
     */
    public SearchResults<PublicHearingId> searchPublicHearings(String query, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Performs a search across all public hearings in a given year.
     * @param query Search query.
     * @param year Filter by year.
     * @param sort Sort by field(s).
     * @param limOff Restrict the result set.
     * @return
     * @throws SearchException
     */
    public SearchResults<PublicHearingId> searchPublicHearings(String query, int year, String sort, LimitOffset limOff) throws SearchException;

    /**
     * Hanldes a public hearing update event by indexing the supplied public hearing.
     * @param publicHearingUpdateEvent
     */
    public void handlePublicHearingUpdate(PublicHearingUpdateEvent publicHearingUpdateEvent);

    /**
     * Handles a batch public hearing update by indexing the supplied public hearings.
     * @param bulkPublicHearingUpdateEvent
     */
    public void handleBulkPublicHearingUpdate(BulkPublicHearingUpdateEvent bulkPublicHearingUpdateEvent);
}
