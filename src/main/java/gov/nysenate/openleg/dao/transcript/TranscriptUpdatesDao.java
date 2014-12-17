package gov.nysenate.openleg.dao.transcript;

import com.google.common.collect.Range;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.transcript.TranscriptUpdateToken;

import java.time.LocalDateTime;
import java.util.List;

public interface TranscriptUpdatesDao
{

    /**
     * Fetches new and updated transcripts from a specified date range.
     * @param dateRange Range{@literal <}LocalDateTime{@literal >} - The date range to search within.
     * @param dateOrder SortOrder - Order by the date/time.
     * @param limOff LimitOffset - Restrict the result set.
     * @return List of TranscriptUpdateToken
     */
    public List<TranscriptUpdateToken> transcriptsUpdatedDuring(Range<LocalDateTime> dateRange,
                                                                         SortOrder dateOrder, LimitOffset limOff);
}
