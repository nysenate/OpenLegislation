package gov.nysenate.openleg.legislation.transcripts.hearing.dao;

import com.google.common.eventbus.EventBus;
import gov.nysenate.openleg.common.dao.LimitOffset;
import gov.nysenate.openleg.common.dao.SortOrder;
import gov.nysenate.openleg.legislation.transcripts.hearing.Hearing;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingId;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingNotFoundEx;
import gov.nysenate.openleg.updates.transcripts.hearing.HearingUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class SqlHearingDataService implements HearingDataService {
    private final EventBus eventBus;
    private final HearingDao hearingDao;

    @Autowired
    public SqlHearingDataService(EventBus eventBus, HearingDao hearingDao) {
        this.eventBus = eventBus;
        this.hearingDao = hearingDao;
    }

    @PostConstruct
    private void init() {
        eventBus.register(this);
    }

    /** {@inheritDoc} */
    @Override
    public Hearing getHearing(HearingId hearingId) throws HearingNotFoundEx {
        if (hearingId == null) {
            throw new IllegalArgumentException("HearingId cannot be null");
        }
        try {
            return hearingDao.getHearing(hearingId);
        } catch (EmptyResultDataAccessException ex) {
            throw new HearingNotFoundEx(hearingId, ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getFilename(HearingId id) throws HearingNotFoundEx {
        if (id == null)
            throw new IllegalArgumentException("HearingId cannot be null");
        try {
            return hearingDao.getFilename(id);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new HearingNotFoundEx(id, ex);
        }
    }

    @Override
    public Hearing getHearing(String filename) throws HearingNotFoundEx {
        if (filename == null)
            throw new IllegalArgumentException("HearingId cannot be null");
        try {
            return hearingDao.getHearing(filename);
        } catch (EmptyResultDataAccessException ex) {
            throw new HearingNotFoundEx(filename, ex);
        }
    }

    /** {@inheritDoc */
    @Override
    public List<HearingId> getHearingIds(SortOrder order, LimitOffset limitOffset) {
        return hearingDao.getHearingIds(order, limitOffset);
    }

    /** {@inheritDoc */
    @Override
    public void saveHearing(Hearing hearing, boolean postUpdateEvent) {
        if (hearing == null) {
            throw new IllegalArgumentException("Hearing cannot be null");
        }
        hearingDao.updateHearing(hearing);
        if (postUpdateEvent) {
            eventBus.post(new HearingUpdateEvent(hearing));
        }
    }

    @Override
    public List<Hearing> getHearings(Integer year) {
        return hearingDao.getHearings(year);
    }
}
