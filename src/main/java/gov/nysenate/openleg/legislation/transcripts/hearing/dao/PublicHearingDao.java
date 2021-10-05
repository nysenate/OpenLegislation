package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateToken;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicHearingDao
{

    /**
     * Retrieves all PublicHearingId's.
     */
    List<PublicHearingId> getPublicHearingIds(SortOrder order, LimitOffset limOff);

    /**
     * Retrieves a {@link PublicHearing} via its {@link PublicHearingId}.
     */
    PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws EmptyResultDataAccessException;

    /**
     * Retrieves a {@link PublicHearing} via its filename.
     */
    PublicHearing getPublicHearing(String filename) throws EmptyResultDataAccessException;

    /**
     * Updates the hearing info in the database, or inserts it if necessary.
     * Also assigns the hearing an ID.
     * @param publicHearing The {@link PublicHearing} to update.
     *
     */
    void updatePublicHearing(PublicHearing publicHearing);

    /**
     * Fetches new and updated public hearings from a specified date range.
     * @param dateRange Range{@literal <}LocalDateTime{@literal >} - The date range to search within.
     * @param dateOrder SortOrder - Order by the date/time.
     * @param limOff LimitOffset - Restrict the result set.
     * @return List of PublicHearingUpdateToken
     */
    PaginatedList<PublicHearingUpdateToken> publicHearingsUpdatedDuring(Range<LocalDateTime> dateRange, SortOrder dateOrder, LimitOffset limOff);
}
