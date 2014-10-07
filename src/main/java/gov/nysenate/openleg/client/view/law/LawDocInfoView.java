package gov.nysenate.openleg.client.view.law;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.law.LawDocInfo;

import java.time.LocalDate;

public class LawDocInfoView implements ViewObject
{
    protected String lawId;
    protected String locationId;
    protected String title;
    protected String docType;
    protected String docLevelId;
    protected LocalDate activeDate;

    public LawDocInfoView(LawDocInfo docInfo) {
        if (docInfo != null) {
            this.lawId = docInfo.getLawId();
            this.locationId = docInfo.getLocationId();
            this.title = docInfo.getTitle();
            this.docType = docInfo.getDocType().name();
            this.docLevelId = docInfo.getDocTypeId();
            this.activeDate = docInfo.getPublishedDate();
        }
    }

    @Override
    public String getViewType() {
        return "law-document-info";
    }

    public String getLawId() {
        return lawId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getTitle() {
        return title;
    }

    public String getDocType() {
        return docType;
    }

    public String getDocLevelId() {
        return docLevelId;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }
}
