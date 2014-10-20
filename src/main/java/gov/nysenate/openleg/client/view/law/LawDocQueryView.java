package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;

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
