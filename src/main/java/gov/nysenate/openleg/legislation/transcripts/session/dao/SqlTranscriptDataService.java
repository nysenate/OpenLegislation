package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlTranscriptDataService implements TranscriptDataService {
    private static final Logger logger = LoggerFactory.getLogger(SqlTranscriptDataService.class);
    private final EventBus eventBus;
    private final TranscriptDao transcriptDao;

    @Autowired
    public SqlTranscriptDataService(EventBus eventBus, TranscriptDao transcriptDao) {
        this.eventBus = eventBus;
        this.transcriptDao = transcriptDao;
        this.eventBus.register(this);
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
        catch (EmptyResultDataAccessException ex) {
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
        try {
            var currInfo = new TranscriptFilenameInfo(transcriptDao.getTranscript(transcript.getId()));
            var newInfo = new TranscriptFilenameInfo(transcript);
            if (newInfo.isLessAccurateThan(currInfo)) {
                final String summary = "Skipped transcript file " + transcript.getFilename();
                var notif = new Notification(NotificationType.PROCESS_WARNING, LocalDateTime.now(),
                        summary, summary + ". An older version of this file has more accurate data.");
                logger.warn(notif.getMessage());
                eventBus.post(notif);
                return;
            }
        } catch (EmptyResultDataAccessException ignored) {}
        transcriptDao.updateTranscript(transcript);
        if (postUpdateEvent) {
            eventBus.post(new TranscriptUpdateEvent(transcript));
        }
    }
}
