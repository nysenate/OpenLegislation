package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.PaginatedList;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateToken;

import java.time.LocalDateTime;
import java.util.List;

public interface TranscriptDao {
    /**
     * Retrieves all TranscriptId's for a year.
     *
     * @param sortOrder SortOrder
     * @param limOff LimitOffset
     * @return List<TranscriptId>
     */
    List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limOff);

    /**
     * Retrieves a Transcript via its TranscriptId.
     *
     * @param transcriptId The transcriptId of the Transcript to return
     * @return The Transcript belonging to the transcriptId
     * @see TranscriptId
     * @see Transcript
     */
    Transcript getTranscript(TranscriptId transcriptId);

    /**
     * Updates the backing store with the given instance or inserts it
     * if the record doesn't already exist.
     *
     * @param transcript The {@link Transcript} to update.
     */
    void updateTranscript(Transcript transcript);

    /**
     * Fetches new and updated transcripts from a specified date range.
     * @param dateRange Range{@literal <}LocalDateTime{@literal >} - The date range to search within.
     * @param dateOrder SortOrder - Order by the date/time.
     * @param limOff LimitOffset - Restrict the result set.
     * @return PaginatedList containing TranscriptUpdateToken's
     */
    PaginatedList<TranscriptUpdateToken> transcriptsUpdatedDuring(Range<LocalDateTime> dateRange,
                                                                SortOrder dateOrder, LimitOffset limOff);
}
