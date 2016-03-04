package gov.nysenate.openleg.service.law.data;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.law.data.LawDataDao;
import gov.nysenate.openleg.model.cache.CacheEvictIdEvent;
import gov.nysenate.openleg.model.cache.CacheWarmEvent;
import gov.nysenate.openleg.model.law.*;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import gov.nysenate.openleg.service.base.data.CachingService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.MemoryUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Service interface for retrieving and saving NYS Law data.
 */
@Service
public class CachedLawDataService implements LawDataService, CachingService<LawVersionId>
{
    private static final Logger logger = LoggerFactory.getLogger(CachedLawDataService.class);

    @Autowired private LawDataDao lawDataDao;
    @Autowired private CacheManager cacheManager;
    @Autowired private EventBus eventBus;

    @Value("${law.cache.size}") private long lawTreeCacheHeapSize;

    private EhCacheCache lawTreeCache;

    private Map<String, LocalDate> maxPubDates = new HashMap<>();

    @PostConstruct
    private void init() {
        eventBus.register(this);
        setupCaches();
        maxPubDates = lawDataDao.getLastPublishedMap();
    }

    @PreDestroy
    private void cleanUp() {
        evictCaches();
        cacheManager.removeCache(ContentCache.LAW.name());
        maxPubDates.clear();
    }

    /** --- CachingService implementation --- */

    /** {@inheritDoc} */
    @Override
    public List<Ehcache> getCaches() {
        return Arrays.asList(lawTreeCache.getNativeCache());
    }

    /** {@inheritDoc} */
    @Override
    public void setupCaches() {
        Cache cache = new Cache(new CacheConfiguration().name(ContentCache.LAW.name())
                .eternal(true)
                .maxBytesLocalHeap(lawTreeCacheHeapSize, MemoryUnit.MEGABYTES)
                .sizeOfPolicy(defaultSizeOfPolicy()));
        cacheManager.addCache(cache);
        this.lawTreeCache = new EhCacheCache(cache);
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.LAW)) {
            evictCaches();
            maxPubDates.clear();
        }
    }

    /** {@inheritDoc} */
    @Subscribe
    @Override
    public void handleCacheEvictIdEvent(CacheEvictIdEvent<LawVersionId> evictIdEvent) {
        if (evictIdEvent.affects(ContentCache.LAW)) {
            evictContent(evictIdEvent.getContentId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(LawVersionId lawVersionId) {
        lawTreeCache.evict(lawVersionId);
        maxPubDates.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void warmCaches() {
        try {
            logger.info("Warming up law cache..");
            getLawInfos().forEach(lawInfo -> getLawTree(lawInfo.getLawId(), LocalDate.now()));
            logger.info("Finished warming up law cache..");
        }
        catch (LawTreeNotFoundEx ex) {
            logger.warn("Failed to warm up law cache!.", ex);
        }
    }

    /** {@inheritDoc} */
    @Override
    @Subscribe
    public void handleCacheWarmEvent(CacheWarmEvent warmEvent) {
        if (warmEvent.affects(ContentCache.LAW)) {
            warmCaches();
        }
    }

    /** --- LawDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public List<LawInfo> getLawInfos() {
        return lawDataDao.getLawInfos().stream().sorted().collect(toList());
    }

    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishedDate) throws LawTreeNotFoundEx {
        if (lawId == null) throw new IllegalArgumentException("Supplied lawId cannot be null");
        try {
            if (endPublishedDate == null) {
                if (maxPubDates.isEmpty()) {
                    maxPubDates = lawDataDao.getLastPublishedMap();
                }
                endPublishedDate = maxPubDates.get(lawId);
            }
            LawVersionId lawVersionId = new LawVersionId(lawId.toUpperCase(), endPublishedDate);
            LawTree lawTree;
            if (lawTreeCache.get(lawVersionId) != null) {
                lawTree = (LawTree) lawTreeCache.get(lawVersionId).get();
            }
            else {
                lawTree = lawDataDao.getLawTree(lawId, endPublishedDate);
                lawTreeCache.put(lawTree.getLawVersionId(), lawTree);
            }
            return lawTree;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new LawTreeNotFoundEx(lawId, endPublishedDate, ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public LawDocInfo getLawDocInfo(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx {
        if (documentId == null || documentId.length() < 4) {
            throw new IllegalArgumentException("Document id cannot be less than 4 characters");
        }
        Optional<LawTreeNode> node =
            getLawTree(documentId.substring(0, 3), endPublishedDate).find(documentId);
        if (node.isPresent()) {
            return node.get().getLawDocInfo();
        }
        else {
            throw new LawDocumentNotFoundEx(documentId, endPublishedDate, "Law tree was found but document was not matched");
        }
    }

    /** {@inheritDoc} */
    @Override
    public LawDocument getLawDocument(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx {
        if (documentId == null) throw new IllegalArgumentException("Supplied documentId cannot be null");
        if (endPublishedDate == null) endPublishedDate = LocalDate.now();
        try {
            return lawDataDao.getLawDocument(documentId.toUpperCase(), endPublishedDate);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new LawDocumentNotFoundEx(documentId, endPublishedDate, "");
        }
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, LawDocument> getLawDocuments(String lawId, LocalDate endPublishedDate) {
        if (lawId == null) throw new IllegalArgumentException("Supplied lawId cannot be null");
        if (endPublishedDate == null) endPublishedDate = LocalDate.now();
        return lawDataDao.getLawDocuments(lawId.toUpperCase(), endPublishedDate);
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawTree(LawFile lawFile, LawTree lawTree) {
        if (lawTree == null) throw new IllegalArgumentException("Supplied lawTree cannot be null");
        lawDataDao.updateLawTree(lawFile, lawTree);
        lawTreeCache.put(lawTree.getLawVersionId(), lawTree);
        maxPubDates.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument) {
        if (lawDocument == null) throw new IllegalArgumentException("Supplied lawDocument cannot be null");
        if (lawFile == null) throw new IllegalArgumentException("Supplied lawFile cannot be null");
        lawDataDao.updateLawDocument(lawFile, lawDocument);
    }
}
