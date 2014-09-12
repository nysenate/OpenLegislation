package gov.nysenate.openleg.service.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;

import java.util.List;

public interface TranscriptDataService
{
    public Transcript getTranscript(TranscriptId transcriptId);

    public List<TranscriptId> getTranscriptIds(SessionYear sessionYear, LimitOffset limitOffset);

    public void saveTranscript(Transcript transcript, TranscriptFile transcriptFile);
}
