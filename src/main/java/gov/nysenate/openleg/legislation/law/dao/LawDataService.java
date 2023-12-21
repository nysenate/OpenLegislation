package gov.nysenate.openleg.legislation.law.dao;

import com.google.common.collect.Range;
import gov.nysenate.openleg.legislation.law.*;
import gov.nysenate.openleg.processors.law.LawFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LawDataService
{
    /**
     * Retrieves a listing of all basic law info ordered alphabetically by law id.
     *
     * @return List<LawTree>
     */
    List<LawInfo> getLawInfos();

    /**
     * Retrieves the LawTree from the backing store given the law id and an endPublishedDate. The most recent law tree
     * with a published date prior to or on 'endPublishedDate' will be returned. Otherwise a LawTreeNotFoundEx will
     * be thrown.
     *
     * @param lawId String - Three letter law id
     * @param endPublishedDate - LocalDate - The upper bound for published date - null for most recent
     * @return LawTree
     * @throws LawTreeNotFoundEx - If there is no law tree that meets the given criteria.
     */
    LawTree getLawTree(String lawId, LocalDate endPublishedDate) throws LawTreeNotFoundEx;

    /**
     * Gets the most recent law tree for the given law chapter id.
     *
     * @see #getLawTree(String, LocalDate)
     * @param lawId String - law chapter id
     * @return {@link LawTree}
     * @throws LawTreeNotFoundEx - The there is no law tree for the given law chapter id.
     */
    default LawTree getLawTree(String lawId) throws LawTreeNotFoundEx {
        return getLawTree(lawId, null);
    }

    /**
     * Retrieves the LawDocument from the backing store given the document id and an endPublishedDate. The most recent
     * law document with a published date prior to or on 'endPublishedDate' will be returned. Otherwise a
     * LawDocumentNotFoundEx will be thrown.
     *
     * @param documentId String - The law document id
     * @param endPublishedDate LocalDate - The upper bound for published date
     * @return LawDocument
     * @throws LawDocumentNotFoundEx - If there is no law document that meets the given criteria.
     */
    LawDocument getLawDocument(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx;

    /**
     * Similar to getLawDocument except the text is omitted which may result in a faster result.
     *
     * @param documentId String
     * @param endPublishedDate LocalDate
     * @return LawDocInfo
     * @throws LawDocumentNotFoundEx
     */
    LawDocInfo getLawDocInfo(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx;

    /**
     * Retrieves all the LawDocuments from the backing store that are associated with the given lawId
     * and were published prior to or on 'endPublishedDate'.
     *
     * @param lawId String - Three letter law id
     * @param endPublishedDate LocalDate - The upper bound for published date
     * @return Map<String, LawDocument> Map of documentId -> LawDocument
     */
    Map<String, LawDocument> getLawDocuments(String lawId, LocalDate endPublishedDate);

    /**
     * Get a set of law documents that have been repealed during the given datetime range.
     *
     * @param dateRange Range<LocalDateTime>
     * @return {@link Set<LawDocId>}
     */
    List<RepealedLawDocId> getRepealedLawDocs(Range<LocalDate> dateRange);

    /**
     * Persists the LawTree into the backing store with LawFile used as a reference to the source data.
     *
     * @param lawFile LawFile - The LawFile instance that represents the source data.
     * @param lawTree LawTree - The LawTree to persist.
     */
    void saveLawTree(LawFile lawFile, LawTree lawTree);

    /**
     * Persists the LawDocument into the backing store with LawFile used as a reference to the source data.
     *
     * @param lawFile LawFile - The LawFile instance that represents the source data.
     * @param lawDocument LawDocument - The LawDocument to persist.
     */
    void saveLawDocument(LawFile lawFile, LawDocument lawDocument);
}
