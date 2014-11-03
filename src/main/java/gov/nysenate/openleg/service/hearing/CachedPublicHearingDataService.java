package gov.nysenate.openleg.service.hearing;

import gov.nysenate.openleg.dao.base.LimitOffset;
import gov.nysenate.openleg.dao.hearing.PublicHearingDao;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingFile;
import gov.nysenate.openleg.model.hearing.PublicHearingId;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import net.sf.ehcache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class CachedPublicHearingDataService implements PublicHearingDataService
{
    private static final String publicHearingCache = "publicHearingCache";

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private PublicHearingDao publicHearingDao;

//    @Override
    @PostConstruct
    public void setupCaches() {
        cacheManager.addCache(publicHearingCache);
    }

//    @Override
    @CacheEvict(value = publicHearingCache, allEntries = true)
    public void evictCaches() {}

//    @Override
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {

    }

    /** {@inheritDoc */
    @Override
    @Cacheable(value = publicHearingCache, key = "#publicHearingId")
    public PublicHearing getPublicHearing(PublicHearingId publicHearingId) {
        if (publicHearingId == null) {
            throw new IllegalArgumentException("PublicHearingId cannot be null");
        }

        return publicHearingDao.getPublicHearing(publicHearingId);
    }

    /** {@inheritDoc */
    @Override
    public List<PublicHearingId> getPublicHearingIds(SessionYear sessionYear, LimitOffset limitOffset) {
        throw new UnsupportedOperationException();
    }

    /** {@inheritDoc */
    @Override
    public void savePublicHearing(PublicHearing publicHearing, PublicHearingFile publicHearingFile) {
        if (publicHearing == null) {
            throw new IllegalArgumentException("publicHearing cannot be null");
        }
        publicHearingDao.updatePublicHearing(publicHearing, publicHearingFile);
    }
}
