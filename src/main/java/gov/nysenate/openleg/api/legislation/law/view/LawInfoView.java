package gov.nysenate.openleg.api.legislation.law.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.law.LawInfo;

public class LawInfoView implements ViewObject
{
    protected String lawId;
    protected String name;
    protected String lawType;
    protected String chapter;

    public LawInfoView(LawInfo lawInfo) {
        if (lawInfo != null) {
            this.lawId = lawInfo.getLawId();
            this.name = lawInfo.getName();
            this.lawType = lawInfo.getType().name();
            this.chapter = lawInfo.getChapterId();
        }
    }

    @Override
    public String getViewType() {
        return "law-info";
    }

    public String getLawId() {
        return lawId;
    }

    public String getName() {
        return name;
    }

    public String getLawType() {
        return lawType;
    }

    public String getChapter() {
        return chapter;
    }
}
