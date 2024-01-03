package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingNotFoundEx;

import java.util.List;

public interface HearingDataService
{
    /**
     * Retrieves a {@link Hearing} instance from a {@link HearingId}.
     * @param hearingId primary key for hearing.
     * @return
     */
    Hearing getHearing(HearingId hearingId) throws HearingNotFoundEx;

    /**
     * Used to maintain backward compatibility after hearing ID changes.
     * @param filename of a hearing.
     * @return the relevant hearing.
     */
    Hearing getHearing(String filename) throws HearingNotFoundEx;

    /**
     * Retrieves the filename of a hearing via its {@link HearingId}.
     */
    String getFilename(HearingId id) throws HearingNotFoundEx;

    /**
     * Retrieves a List of {@link HearingId}.
     * @param limitOffset Restrict the number of resultList.
     * @return
     */
    List<HearingId> getHearingIds(SortOrder order, LimitOffset limitOffset);

    /**
     * Saves a {@link Hearing} to the backing store.
     * The Hearing is inserted if it is a new instance, Updated otherwise.
     */
    void saveHearing(Hearing hearing, boolean postUpdateEvent);

    List<Hearing> getHearings(Integer year);
}
