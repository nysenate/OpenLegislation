package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingNotFoundEx;

import java.util.List;

public interface PublicHearingDataService
{
    /**
     * Retrieves a {@link PublicHearing} instance from a {@link PublicHearingId}.
     * @param publicHearingId primary key for hearing.
     * @return
     */
    PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws PublicHearingNotFoundEx;

    /**
     * Used to maintain backward compatibility after hearing ID changes.
     * @param filename of a public hearing.
     * @return the relevant hearing.
     */
    PublicHearing getPublicHearing(String filename) throws PublicHearingNotFoundEx;

    /**
     * Retrieves the filename of a hearing via its {@link PublicHearingId}.
     */
    String getFilename(PublicHearingId id) throws PublicHearingNotFoundEx;

    /**
     * Retrieves a List of {@link PublicHearingId}.
     * @param limitOffset Restrict the number of results.
     * @return
     */
    List<PublicHearingId> getPublicHearingIds(SortOrder order, LimitOffset limitOffset);

    /**
     * Saves a {@link PublicHearing} to the backing store.
     * The PublicHearing is inserted if it is a new instance, Updated otherwise.
     */
    void savePublicHearing(PublicHearing publicHearing, boolean postUpdateEvent);

}
