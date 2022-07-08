package gov.nysenate.openleg.legislation.law.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.CacheType;
import gov.nysenate.openleg.legislation.CachingService;
import gov.nysenate.openleg.legislation.law.*;
import gov.nysenate.openleg.processors.law.LawFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service interface for retrieving and saving NYS Law data.
 */
@Service
public class CachedLawDataService extends CachingService<LawVersionId, LawTree> implements LawDataService {
    @Autowired
    private LawDataDao lawDataDao;

    @Override
    protected CacheType cacheType() {
        return CacheType.LAW;
    }

    /** --- CachingService implementation --- */

    @Override
    public Map<LawVersionId, LawTree> initialEntries() {
        return lawDataDao.getLawInfos().stream()
                .map(lawInfo -> lawDataDao.getLawTree(lawInfo.getLawId(), LocalDate.now()))
                .collect(Collectors.toMap(LawTree::getLawVersionId, lawTree -> lawTree));
    }

    /** --- LawDataService implementation --- */

    /** {@inheritDoc} */
    @Override
    public List<LawInfo> getLawInfos() {
        return lawDataDao.getLawInfos().stream().sorted().toList();
    }

    /** {@inheritDoc} */
    @Override
    public LawTree getLawTree(String lawId, LocalDate endPublishedDate) throws LawTreeNotFoundEx {
        if (lawId == null) {
            throw new IllegalArgumentException("Supplied lawId cannot be null");
        }
        // Defaults to most recent LawTree.
        endPublishedDate = (endPublishedDate == null ? LocalDate.now() : endPublishedDate);
        try {
            LawVersionId lawVersionId = new LawVersionId(lawId.toUpperCase(), endPublishedDate);
            LawTree tree = cache.get(lawVersionId);
            if (tree == null) {
                tree = lawDataDao.getLawTree(lawId, endPublishedDate);
                cache.put(tree.getLawVersionId(), tree);
            }
            return tree;
        }
        catch (DataRetrievalFailureException ex) {
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
        if (lawId == null) {
            throw new IllegalArgumentException("Supplied lawId cannot be null");
        }
        if (endPublishedDate == null) {
            endPublishedDate = LocalDate.now();
        }
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
        if (lawTree == null) {
            throw new IllegalArgumentException("Supplied lawTree cannot be null");
        }
        lawDataDao.updateLawTree(lawFile, lawTree);
        cache.put(lawTree.getLawVersionId(), lawTree);
    }

    /** {@inheritDoc} */
    @Override
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument) {
        if (lawDocument == null) {
            throw new IllegalArgumentException("Supplied lawDocument cannot be null");
        }
        if (lawFile == null) {
            throw new IllegalArgumentException("Supplied lawFile cannot be null");
        }
        lawDataDao.updateLawDocument(lawFile, lawDocument);
    }
}
