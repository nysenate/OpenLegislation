package gov.nysenate.openleg.legislation.law.dao;

import java.time.LocalDate;

public class LawDocumentNotFoundEx extends RuntimeException
{
    private static final long serialVersionUID = -2581576982938740304L;

    String docId;
    LocalDate endPublishedDate;
    boolean findNext;
    String details;

    public LawDocumentNotFoundEx(String docId, LocalDate endPublishedDate, String details) {
        super("Law Document with id: " + docId + " and end publish date " + endPublishedDate + " could not be found!\n" +
                details);
        this.docId = docId;
        this.endPublishedDate = endPublishedDate;
        this.findNext = false;
        this.details = details;
    }

    public LawDocumentNotFoundEx(String docId, LocalDate endPublishedDate, boolean findNext, String details) {
        super("Law Document with id: " + docId + " and " + (findNext ? "end" : "start") + " publish date " + endPublishedDate + " could not be found!\n" +
                details);
        this.docId = docId;
        this.endPublishedDate = endPublishedDate;
        this.findNext = findNext;
        this.details = details;
    }

    public String getDocId() {
        return docId;
    }

    public LocalDate getEndPublishedDate() {
        return endPublishedDate;
    }

    public boolean getFindNext() {
        return findNext;
    }

    public String getDetails() {
        return details;
    }
}
