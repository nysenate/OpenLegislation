package gov.nysenate.openleg.service.transcript.data;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.transcript.TranscriptDao;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SqlTranscriptDataService implements TranscriptDataService
{
    @Autowired
    private TranscriptDao transcriptDao;

    /** {@inheritDoc} */
    @Override
    public Transcript getTranscript(TranscriptId transcriptId) {
        if (transcriptId == null) {
            throw new IllegalArgumentException("TranscriptId cannot be null");
        }
        try {
            return transcriptDao.getTranscript(transcriptId);
        }
        catch (DataAccessException ex) {
            throw new TranscriptNotFoundEx(transcriptId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public List<TranscriptId> getTranscriptIds(int year, SortOrder dateOrder, LimitOffset limitOffset) {
        return transcriptDao.getTranscriptIds(year, dateOrder, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public void saveTranscript(Transcript transcript, TranscriptFile transcriptFile) {
        if (transcript == null) {
            throw new IllegalArgumentException("transcript cannot be null");
        }
        transcriptDao.updateTranscript(transcript, transcriptFile);
    }
}