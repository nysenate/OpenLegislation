package gov.nysenate.openleg.legislation.law;

import java.time.LocalDate;

public class RepealedLawDocId extends LawDocId {
    private final LocalDate repealedDate;

    public RepealedLawDocId(String docId, LocalDate publishedDate, LocalDate repealedDate) {
        super(docId, publishedDate);
        this.repealedDate = repealedDate;
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }
}
