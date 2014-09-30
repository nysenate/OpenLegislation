package gov.nysenate.openleg.service.transcript.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;

import java.util.List;

public interface TranscriptDataService
{
    /**
     *
     * @param transcriptId
     * @return Transcript
     */
    public Transcript getTranscript(TranscriptId transcriptId);

    /**
     *
     * @param sessionYear SessionYear
     * @param limitOffset LimitOffset
     * @return List<TranscriptId>
     */
    public List<TranscriptId> getTranscriptIds(SessionYear sessionYear, LimitOffset limitOffset);

    /**
     *
     * @param transcript Transcript
     * @param transcriptFile TranscriptFile
     */
    public void saveTranscript(Transcript transcript, TranscriptFile transcriptFile);
}
