package gov.nysenate.openleg.service.transcript;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.transcript.TranscriptDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.model.transcript.TranscriptFile;
import gov.nysenate.openleg.model.transcript.TranscriptId;
import gov.nysenate.openleg.model.transcript.TranscriptNotFoundEx;
import gov.nysenate.openleg.service.base.CachingService;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CachedTranscriptDataService implements TranscriptDataService, CachingService
{
    private static final String transcriptCache = "transcriptCache";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TranscriptDao transcriptDao;

    @Override
    @PostConstruct
    public void setupCaches() {
        cacheManager.addCache(transcriptCache);
    }

    @Override
    @CacheEvict(value = transcriptCache, allEntries = true)
    public void evictCaches() {}

    /** {@inheritDoc} */
    @Override
    @Cacheable(value = transcriptCache, key = "#transcriptId")
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
    public List<TranscriptId> getTranscriptIds(SessionYear sessionYear, LimitOffset limitOffset) {
        throw new NotImplementedException();
    }

    /** {@inheritDoc} */
    @Override
    @CacheEvict(value = transcriptCache, key = "#transcriptId")
    public void saveTranscript(Transcript transcript, TranscriptFile transcriptFile) {
        if (transcript == null) {
            throw new IllegalArgumentException("transcript cannot be null");
        }
        transcriptDao.updateTranscript(transcript, transcriptFile);
    }
}
