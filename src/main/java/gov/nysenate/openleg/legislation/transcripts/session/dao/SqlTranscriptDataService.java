package gov.nysenate.openleg.legislation.transcripts.session.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.session.DuplicateTranscriptEx;
import gov.nysenate.openleg.legislation.transcripts.session.Transcript;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptId;
import gov.nysenate.openleg.legislation.transcripts.session.TranscriptNotFoundEx;
import gov.nysenate.openleg.notifications.model.Notification;
import gov.nysenate.openleg.notifications.model.NotificationType;
import gov.nysenate.openleg.updates.transcripts.session.TranscriptUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlTranscriptDataService implements TranscriptDataService {
    private final EventBus eventBus;
    private final TranscriptDao transcriptDao;

    @Autowired
    public SqlTranscriptDataService(EventBus eventBus, TranscriptDao transcriptDao) {
        this.eventBus = eventBus;
        this.transcriptDao = transcriptDao;
        this.eventBus.register(this);
    }

    public Transcript getTranscriptByDateTime(LocalDateTime localDateTime)
            throws TranscriptNotFoundEx, DuplicateTranscriptEx {
        if (localDateTime == null) {
            throw new IllegalArgumentException("TranscriptId cannot be null");
        }
        var id = new TranscriptId(localDateTime, null);
        try {
            return transcriptDao.getTranscript(id);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new TranscriptNotFoundEx(id, ex);
        }
        catch (DataAccessException ex) {
            throw new DuplicateTranscriptEx(id.dateTime());
        }
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
        try {
            var currInfo = new TranscriptFilenameInfo(transcriptDao.getTranscript(transcript.getId()));
            var newInfo = new TranscriptFilenameInfo(transcript);
            if (newInfo.isLessAccurateThan(currInfo)) {
                final String summary = "Skipped transcript file " + transcript.getFilename();
                eventBus.post(new Notification(NotificationType.PROCESS_WARNING, LocalDateTime.now(),
                        summary, summary + "\nAn older version of this file has more accurate data."));
                return;
            }
        } catch (EmptyResultDataAccessException ignored) {}
        final String summary = "The transcript from %s lacks a dayType.".formatted(transcript.getFilename());
        if (transcript.getDayType() == null) {
            eventBus.post(new Notification(NotificationType.PROCESS_WARNING, LocalDateTime.now(),
                    summary, summary + "\nAll floor transcripts should be legislative or session transcripts."));
        }
        transcriptDao.updateTranscript(transcript);
        if (postUpdateEvent) {
            eventBus.post(new TranscriptUpdateEvent(transcript));
        }
    }
}
