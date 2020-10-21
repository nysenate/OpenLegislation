package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptFile;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlTranscriptDataService implements TranscriptDataService
{
    @Autowired
    private EventBus eventBus;

    @Autowired
    private TranscriptDao transcriptDao;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public Transcript getTranscript(TranscriptId transcriptId) throws TranscriptNotFoundEx {
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
    public List<TranscriptId> getTranscriptIds(SortOrder sortOrder, LimitOffset limitOffset) {
        return transcriptDao.getTranscriptIds(sortOrder, limitOffset);
    }

    /** {@inheritDoc} */
    @Override
    public void saveTranscript(Transcript transcript, boolean postUpdateEvent) {
        if (transcript == null) {
            throw new IllegalArgumentException("transcript cannot be null");
        }
        transcriptDao.updateTranscript(transcript);
        if (postUpdateEvent) {
            eventBus.post(new TranscriptUpdateEvent(transcript, LocalDateTime.now()));
        }
    }
}