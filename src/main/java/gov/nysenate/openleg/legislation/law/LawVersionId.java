package gov.nysenate.openleg.legislation.law;

import java.time.LocalDate;

public record LawVersionId(String lawId, LocalDate publishedDate) {
    @Override
    public String toString() {
        return lawId + "v" + publishedDate;
    }
}
