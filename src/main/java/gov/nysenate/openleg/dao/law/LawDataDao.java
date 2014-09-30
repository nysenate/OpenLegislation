package gov.nysenate.openleg.dao.law;

import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import gov.nysenate.openleg.model.law.LawTree;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;

public interface LawDataDao
{
    /**
     * Retrieves and constructs a LawTree given the lawId and an ending publish date. This LawTree
     * can be used to determine the structure of a given law including the placement of its articles
     * and sections. The tree does not contain the text body of the laws however in an effort to
     * avoid the memory overhead of loading all the text in a given law.
     *
     * @param lawId String - The three letter law id. (Case insensitive)
     * @param endPublishDate LocalDate - Returns the law tree that has the most recent publish date that
     *                                   is prior or on this date.
     * @return LawTree
     * @throws DataAccessException - If there was an error while trying to retrieve the given law.
     */
    public LawTree getLawTree(String lawId, LocalDate endPublishDate) throws DataAccessException;

    /**
     * Retrieve a LawDocument using the given document id and end published date. The law document that
     * has a published date closest to but before the 'endPublishDate' will be returned, otherwise a
     * DataAccessException will be thrown.
     *
     * @param documentId String - The LBDC document id
     * @param endPublishDate LocalDate - Returns the law document that has the most recent publish date that
     *                                   is prior to or on this date.
     * @return LawDocument
     * @throws DataAccessException - If there was an error while trying to retrieve the law document.
     */
    public LawDocument getLawDocument(String documentId, LocalDate endPublishDate);

    /**
     * Updates or inserts a LawDocument into the database, using the document id and published date as the
     * unique identifiers.
     *
     * @param lawFile LawFile - The law file source that triggered the update.
     * @param lawDocument LawDocument - The law document to persist.
     */
    public void updateLawDocument(LawFile lawFile, LawDocument lawDocument);

}