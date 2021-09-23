package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawDocId;

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
