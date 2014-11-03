package gov.nysenate.openleg.service.law.data;

import com.google.common.eventbus.Subscribe;
import gov.nysenate.openleg.dao.law.LawDataDao;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawInfo;
import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.cache.CacheEvictEvent;
import gov.nysenate.openleg.model.cache.ContentCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Service interface for retrieving and saving NYS Law data.
 */
@Service
public class CachedLawDataService implements LawDataService
{
    private static final Logger logger = LoggerFactory.getLogger(CachedLawDataService.class);

    @Autowired
    private LawDataDao lawDataDao;

//    @Override
    public void setupCaches() {
        /** TODO */
    }

//    @Override
    public void evictCaches() {
        /** TODO */
    }

//    @Override
    @Subscribe
    public void handleCacheEvictEvent(CacheEvictEvent evictEvent) {
        if (evictEvent.affects(ContentCache.LAW)) {
            logger.info("Clearing the law cache.");
            evictCaches();
        }
    }

    @Override
    public List<LawInfo> getLawInfos() {
        List<LawInfo> infos = lawDataDao.getLawInfos();
        return infos.stream().sorted().collect(toList());
    }

    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishedDate) throws LawTreeNotFoundEx {
        if (lawId == null) throw new IllegalArgumentException("Supplied lawId cannot be null");
        if (endPublishedDate == null) endPublishedDate = LocalDate.now();
        try {
            return lawDataDao.getLawTree(lawId, endPublishedDate);
        }
        catch (DataAccessException ex) {
            throw new LawTreeNotFoundEx(lawId, endPublishedDate, ex.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public LawDocument getLawDocument(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx {
        if (documentId == null) throw new IllegalArgumentException("Supplied documentId cannot be null");
        if (endPublishedDate == null) endPublishedDate = LocalDate.now();
        try {
            return lawDataDao.getLawDocument(documentId, endPublishedDate);
        }
        catch (EmptyResultDataAccessException ex) {
            throw new LawDocumentNotFoundEx(documentId, endPublishedDate, "");
        }
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawTree(LawFile lawFile, LawTree lawTree) {
        if (lawTree == null) throw new IllegalArgumentException("Supplied lawTree cannot be null");
        lawDataDao.updateLawTree(lawFile, lawTree);
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument) {
        if (lawDocument == null) throw new IllegalArgumentException("Supplied lawDocument cannot be null");
        if (lawFile == null) throw new IllegalArgumentException("Supplied lawFile cannot be null");
        lawDataDao.updateLawDocument(lawFile, lawDocument);
    }
}
