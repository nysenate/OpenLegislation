package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;

import java.time.LocalDate;

public class LawIdQueryView implements ViewObject
{
    protected String lawId;
    protected LocalDate endDate;

    public LawIdQueryView(String lawId, LocalDate endDate) {
        this.lawId = lawId;
        this.endDate = endDate;
    }

    @Override
    public String getViewType() {
        return "law-id-query";
    }

    public String getLawId() {
        return lawId;
    }

    public String getEndDate() {
        return (endDate != null) ? endDate.toString() : null;
    }
}
