package gov.nysenate.openleg.service.transcript.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;

import java.util.List;

/**
 * Data service layer for retrieval and persistence of transcripts.
 */
public interface TranscriptDataService
{
    /**
     * Fetch a transcript given an id.
     *
     * @param transcriptId TranscriptId
     * @return Transcript
     */
    public Transcript getTranscript(TranscriptId transcriptId) throws TranscriptNotFoundEx;

    /**
     * Get a list of transcript ids for a given session year.
     *
     * @param sortOrder SortOrder
     * @param limitOffset LimitOffset
     * @return List<TranscriptId>
     */
    public List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limitOffset);

    /**
     * Saves the transcript.
     *
     * @param transcript Transcript
     * @param transcriptFile TranscriptFile
     */
    public void saveTranscript(Transcript transcript, TranscriptFile transcriptFile, boolean postUpdateEvent);
}