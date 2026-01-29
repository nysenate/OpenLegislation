package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;

import java.time.LocalDate;
import java.util.List;

public class LawDocInfoView implements ViewObject
{
    protected String lawId;
    protected String lawName;
    protected String locationId;
    protected String title;
    protected String docType;
    protected List<LocalDate> publishedDates;
    // TODO: should really be docTypeId.
    protected String docLevelId;
    protected LocalDate activeDate;

    public LawDocInfoView(LawDocInfo docInfo) {
        if (docInfo != null) {
            this.lawId = docInfo.getLawId();
            this.lawName = LawChapterCode.valueOf(this.lawId).getChapterName();
            this.locationId = docInfo.getLocationId();
            this.title = (docInfo.getTitle() != null) ? docInfo.getTitle().replaceAll("\\\\n", " ") : null;
            this.docType = docInfo.getDocType().name();
            this.publishedDates = docInfo.getPublishedDates();
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

    public String getLawName() {
        return lawName;
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

    public List<LocalDate> getPublishedDates() {
        return publishedDates;
    }

    public String getDocLevelId() {
        return docLevelId;
    }

    public LocalDate getActiveDate() {
        return activeDate;
    }
}
