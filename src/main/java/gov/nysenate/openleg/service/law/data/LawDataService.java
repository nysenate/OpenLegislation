package gov.nysenate.openleg.service.law.data;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawTree;

import java.time.LocalDate;
import java.util.Map;

public interface LawDataService
{
    /**
     * Retrieves the LawTree from the backing store given the law id and an endPublishedDate. The most recent law tree
     * with a published date prior to or on 'endPublishedDate' will be returned. Otherwise a LawTreeNotFoundEx will
     * be thrown.
     *
     * @param lawId String - Three letter law id
     * @param endPublishedDate - LocalDate - The ending published date
     * @return LawTree
     * @throws LawTreeNotFoundEx - If there is no law tree that meets the given criteria.
     */
    public LawTree getLawTree(String lawId, LocalDate endPublishedDate);

    /**
     * Retrieves the LawDocument from the backing store given the document id and an endPublishedDate. The most recent
     * law document with a published date prior to or on 'endPublishedDate' will be returned. Otherwise a
     * LawDocumentNotFoundEx will be thrown.
     *
     * @param documentId String - The law document id
     * @param endPublishedDate LocalDate - The ending published date
     * @return LawDocument
     * @throws LawDocumentNotFoundEx - If there is no law document that meets the given criteria.
     */
    public LawDocument getLawDocument(String documentId, LocalDate endPublishedDate) throws LawDocumentNotFoundEx;

    /**
     * Persists the LawTree into the backing store with LawFile used as a reference to the source data.
     *
     * @param lawFile LawFile - The LawFile instance that represents the source data.
     * @param lawTree LawTree - The LawTree to persist.
     */
    public void saveLawTree(LawFile lawFile, LawTree lawTree);

    /**
     * Persists the LawDocument into the backing store with LawFile used as a reference to the source data.
     *
     * @param lawFile LawFile - The LawFile instance that represents the source data.
     * @param lawDocument LawDocument - The LawDocument to persist.
     */
    public void saveLawDocument(LawFile lawFile, LawDocument lawDocument);
}
