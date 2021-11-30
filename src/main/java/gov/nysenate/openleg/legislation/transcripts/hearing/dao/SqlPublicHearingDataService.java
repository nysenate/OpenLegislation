package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingNotFoundEx;
import gov.nysenate.openleg.updates.transcripts.hearing.PublicHearingUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SqlPublicHearingDataService implements PublicHearingDataService {
    @Autowired
    private EventBus eventBus;

    @Autowired
    private PublicHearingDao publicHearingDao;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws PublicHearingNotFoundEx {
        if (publicHearingId == null)
            throw new IllegalArgumentException("PublicHearingId cannot be null");
        try {
            return publicHearingDao.getPublicHearing(publicHearingId);
        } catch (EmptyResultDataAccessException ex) {
            throw new PublicHearingNotFoundEx(publicHearingId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getFilename(PublicHearingId id) throws PublicHearingNotFoundEx {
        if (id == null)
            throw new IllegalArgumentException("PublicHearingId cannot be null");
        try {
            return publicHearingDao.getFilename(id);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new PublicHearingNotFoundEx(id, ex);
        }
    }

    @Override
    public PublicHearing getPublicHearing(String filename) throws PublicHearingNotFoundEx {
        if (filename == null)
            throw new IllegalArgumentException("PublicHearingId cannot be null");
        try {
            return publicHearingDao.getPublicHearing(filename);
        } catch (EmptyResultDataAccessException ex) {
            throw new PublicHearingNotFoundEx(filename, ex);
        }
    }

    /** {@inheritDoc */
    @Override
    public List<PublicHearingId> getPublicHearingIds(SortOrder order, LimitOffset limitOffset) {
        return publicHearingDao.getPublicHearingIds(order, limitOffset);
    }

    /** {@inheritDoc */
    @Override
    public void savePublicHearing(PublicHearing publicHearing, boolean postUpdateEvent) {
        if (publicHearing == null)
            throw new IllegalArgumentException("publicHearing cannot be null");
        publicHearingDao.updatePublicHearing(publicHearing);
        if (postUpdateEvent)
            eventBus.post(new PublicHearingUpdateEvent(publicHearing, LocalDateTime.now()));
    }
}
