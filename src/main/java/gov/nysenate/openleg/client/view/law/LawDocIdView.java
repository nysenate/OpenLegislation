package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocId;

import java.time.LocalDate;

public class LawDocIdView implements ViewObject
{
    protected String lawId;
    protected String locationId;
    protected LocalDate publishedDate;

    public LawDocIdView(LawDocId lawDocId) {
        if (lawDocId != null) {
            this.lawId = lawDocId.getLawId();
            this.locationId = lawDocId.getLocationId();
            this.publishedDate = lawDocId.getPublishedDate();
        }
    }

    @Override
    public String getViewType() {
        return "law-doc-id";
    }

    public String getLawId() {
        return lawId;
    }

    public String getLocationId() {
        return locationId;
    }

    public LocalDate getPublishedDate() {
        return publishedDate;
    }
}
