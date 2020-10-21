package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawVersionId;

import java.time.LocalDate;

public class LawVersionIdView implements ViewObject
{
    protected String lawId;
    protected LocalDate activeDate;

    public LawVersionIdView(LawVersionId versionId) {
        if (versionId != null) {
            this.lawId = versionId.getLawId();
            this.activeDate = versionId.getPublishedDate();
        }
    }

    @Override
    public String getViewType() {
        return "law-version";
    }

    public String getLawId() {
        return lawId;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }
}
