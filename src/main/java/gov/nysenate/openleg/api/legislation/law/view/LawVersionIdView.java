package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawVersionId;

import java.time.LocalDate;

public record LawVersionIdView(String lawId, LocalDate activeDate) implements ViewObject {
    public LawVersionIdView(LawVersionId lawVersionId) {
        this(lawVersionId.lawId(), lawVersionId.publishedDate());
    }

    @Override
    public String getViewType() {
        return "law-version";
    }
}
