package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateToken;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public interface HearingDao {
    /**
     * Retrieves all HearingId's.
     */
    List<HearingId> getHearingIds(SortOrder order, LimitOffset limOff);

    /**
     * Retrieves a {@link Hearing} via its {@link HearingId}.
     */
    Hearing getHearing(HearingId hearingId) throws EmptyResultDataAccessException;

    /**
     * Retrieves the filename of a hearing via its {@link HearingId}.
     */
    String getFilename(HearingId hearingId)throws EmptyResultDataAccessException;

    /**
     * Retrieves a {@link Hearing} via its filename.
     */
    Hearing getHearing(String filename) throws EmptyResultDataAccessException;

    /**
     * Updates the hearing info in the database, or inserts it if necessary.
     * Also assigns the hearing an ID.
     * @param hearing The {@link Hearing} to update.
     *
     */
    void updateHearing(Hearing hearing);

    /**
     *
     * @param year
     * @return
     */
    List<Hearing> getHearings(Integer year);

    /**
     * Fetches new and updated hearings from a specified date range.
     * @param dateRange Range{@literal <}LocalDateTime{@literal >} - The date range to search within.
     * @param dateOrder SortOrder - Order by the date/time.
     * @param limOff LimitOffset - Restrict the result set.
     * @return List of HearingUpdateToken
     */
    PaginatedList<HearingUpdateToken> hearingsUpdatedDuring(Range<LocalDateTime> dateRange, SortOrder dateOrder, LimitOffset limOff);
}
