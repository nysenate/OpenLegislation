package gov.nysenate.openleg.legislation.transcripts.session.dao;

import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.session.DuplicateTranscriptEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data service layer for retrieval and persistence of transcripts.
 */
public interface TranscriptDataService {
    /**
     * Attempts to fetch a transcript based only on its dateTime.
     * @throws DuplicateTranscriptEx if multiple transcripts have the same dateTime.
     */
    default Transcript getTranscriptByDateTime(LocalDateTime localDateTime) throws TranscriptNotFoundEx, DuplicateTranscriptEx {
        if (localDateTime == null) {
            throw new IllegalArgumentException("TranscriptId cannot be null");
        }
        try {
            return getTranscript(new TranscriptId(localDateTime, null));
        }
        catch (IncorrectResultSizeDataAccessException ex) {
            throw new DuplicateTranscriptEx(localDateTime);
        }
    }

    /**
     * Fetch a transcript given an id.
     *
     * @param transcriptId TranscriptId
     * @return Transcript
     */
    Transcript getTranscript(TranscriptId transcriptId) throws TranscriptNotFoundEx;

    /**
     * Get a list of transcript ids for a given session year.
     *
     * @param sortOrder SortOrder
     * @param limitOffset LimitOffset
     * @return List<TranscriptId>
     */
    List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Saves the transcript.
     *
     * @param transcript Transcript
     */
    void saveTranscript(Transcript transcript, boolean postUpdateEvent);
}
