package gov.nysenate.openleg.service.law.data;

import java.time.LocalDate;

public class LawDocumentNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -2581576982938740304L;

    String docId;
    LocalDate endPublishedDate;
    String details;

    public LawDocumentNotFoundEx(String docId, LocalDate endPublishedDate, String details) {
        super("Law Document with id: " + docId + " and end publish date " + endPublishedDate + " could not be found!\n" +
                details);
        this.docId = docId;
        this.endPublishedDate = endPublishedDate;
        this.details = details;
    }

    public String getDocId() {
        return docId;
    }

    public LocalDate getEndPublishedDate() {
        return endPublishedDate;
    }

    public String getDetails() {
        return details;
    }
}
