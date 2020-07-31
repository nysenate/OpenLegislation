package gov.nysenate.openleg.model.law;

import java.time.LocalDate;

public class RepealedLawDocId extends LawDocId {
    private LocalDate repealedDate;

    public RepealedLawDocId(LawDocId other, LocalDate repealedDate) {
        super(other);
        this.repealedDate = repealedDate;
    }

    public LocalDate getRepealedDate() {
        return repealedDate;
    }

    public void setRepealedDate(LocalDate repealedDate) {
        this.repealedDate = repealedDate;
    }
}
