package gov.nysenate.openleg.legislation.law.dao;

import com.google.common.collect.Range;
import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.legislation.CacheEvictEvent;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.ContentCache;
import gov.nysenate.openleg.legislation.law.*;
import gov.nysenate.openleg.processors.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class CachedLawDataService extends CachingService<LawVersionId, LawTree> implements LawDataService {
    private static final Logger logger = LoggerFactory.getLogger(CachedLawDataService.class);

    @Autowired
    private LawDataDao lawDataDao;
    @Value("${law.cache.element.size}")
    private int lawTreeCacheElementSize;
    private Map<String, LocalDate> maxPubDates = new HashMap<>();

    public CachedLawDataService() {
        super(cache);
    }

    @Override
    @PostConstruct
    protected void init() {
        super.init();
        maxPubDates = lawDataDao.getLastPublishedMap();
    }

    @PreDestroy
    protected void cleanUp() {
        super.cleanUp();
        maxPubDates.clear();
    }

    @Override
    protected List<ContentCache> getCacheEnums() {
        return List.of(ContentCache.LAW);
    }

    @Override
    protected boolean isByteSizeOf() {
        return false;
    }

    @Override
    protected int getNumUnits() {
        return lawTreeCacheElementSize;
    }


    /** --- CachingService implementation --- */

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
    @Override
    public void evictContent(LawVersionId lawVersionId) {
        super.evictContent(lawVersionId);
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
                if (maxPubDates.isEmpty())
                    maxPubDates = lawDataDao.getLastPublishedMap();
                endPublishedDate = maxPubDates.get(lawId);
            }
            LawVersionId lawVersionId = new LawVersionId(lawId.toUpperCase(), endPublishedDate);
            LawTree tree = cache.get(lawVersionId);
            if (tree == null) {
                tree = lawDataDao.getLawTree(lawId, endPublishedDate);
                cache.put(tree.getLawVersionId(), tree);
            }
            return tree;
        }
        catch (EmptyResultDataAccessException ex) {
            throw new LawTreeNotFoundEx(lawId, endPublishedDate, ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public LawDocInfo getLawDocInfo(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx {
        if (documentId == null || documentId.length() < 4)
            throw new IllegalArgumentException("Document id cannot be less than 4 characters");
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
    public Set<RepealedLawDocId> getRepealedLawDocs(Range<LocalDate> dateRange) {
        return new HashSet<>(lawDataDao.getRepealedLaws(dateRange));
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawTree(LawFile lawFile, LawTree lawTree) {
        if (lawTree == null) throw new IllegalArgumentException("Supplied lawTree cannot be null");
        lawDataDao.updateLawTree(lawFile, lawTree);
        cache.put(lawTree.getLawVersionId(), lawTree);
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
