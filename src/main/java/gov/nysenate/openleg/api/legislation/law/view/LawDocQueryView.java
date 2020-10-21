package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;

import java.time.LocalDate;

public class LawDocQueryView implements ViewObject
{
    protected String lawDocId;
    protected LocalDate endDate;

    public LawDocQueryView(String lawDocId, LocalDate endDate) {
        this.lawDocId = lawDocId;
        this.endDate = endDate;
    }

    @Override
    public String getViewType() {
        return "law-doc-query";
    }

    public String getLawDocId() {
        return lawDocId;
    }

    public String getEndDate() {
        return endDate.toString();
    }
}
