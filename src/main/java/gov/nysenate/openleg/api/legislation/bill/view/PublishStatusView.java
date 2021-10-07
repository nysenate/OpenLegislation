package gov.nysenate.openleg.api.legislation.bill.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.PublishStatus;

public class PublishStatusView implements ViewObject
{
    protected String version;
    protected boolean published;
    protected String effectDateTime;

    public PublishStatusView(String version, PublishStatus publishStatus) {
        if (publishStatus != null) {
            this.version = version;
            this.published = publishStatus.isPublished();
            this.effectDateTime = publishStatus.getEffectDateTime().toString();
        }
    }

    protected PublishStatusView() {}

    @Override
    public String getViewType() {
        return "publish-status";
    }

    public String getVersion() {
        return version;
    }

    public boolean isPublished() {
        return published;
    }

    public String getEffectDateTime() {
        return effectDateTime;
    }
}
