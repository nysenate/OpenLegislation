package gov.nysenate.openleg.service.hearing.data;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.hearing.PublicHearingDao;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.service.hearing.event.PublicHearingUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CachedPublicHearingDataService implements PublicHearingDataService
{
    private static final String publicHearingCache = "publicHearingCache";

    @Autowired
    private EventBus eventBus;

    @Autowired
    private PublicHearingDao publicHearingDao;

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc */
    @Override
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId) throws PublicHearingNotFoundEx {
        if (publicHearingId == null) {
            throw new IllegalArgumentException("PublicHearingId cannot be null");
        }

        try {
            return publicHearingDao.getPublicHearing(publicHearingId);
        } catch (EmptyResultDataAccessException ex) {
            throw new PublicHearingNotFoundEx(publicHearingId, ex);
        }
    }

    /** {@inheritDoc */
    @Override
    public List<PublicHearingId> getPublicHearingIds(SortOrder order, LimitOffset limitOffset) {
        return publicHearingDao.getPublicHearingIds(order, limitOffset);
    }

    /** {@inheritDoc */
    @Override
    public void savePublicHearing(PublicHearing publicHearing, PublicHearingFile publicHearingFile, boolean postUpdateEvent) {
        if (publicHearing == null) {
            throw new IllegalArgumentException("publicHearing cannot be null");
        }
        publicHearingDao.updatePublicHearing(publicHearing, publicHearingFile);
        if (postUpdateEvent) {
            eventBus.post(new PublicHearingUpdateEvent(publicHearing, LocalDateTime.now()));
        }
    }
}
