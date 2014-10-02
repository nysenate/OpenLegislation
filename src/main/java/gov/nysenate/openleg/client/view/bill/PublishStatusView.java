package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.base.PublishStatus;

public class PublishStatusView implements ViewObject {

    protected boolean published;
    protected String effectDateTime;
    protected boolean override;
    protected String notes;

    public PublishStatusView(PublishStatus publishStatus) {
        if (publishStatus != null) {
            this.published = publishStatus.isPublished();
            this.effectDateTime = publishStatus.getEffectDateTime().toString();
            this.override = publishStatus.isOverride();
            this.notes = publishStatus.getNotes();
        }
    }

    @Override
    public String getViewType() {
        return "publish-status";
    }

    public boolean isPublished() {
        return published;
    }

    public String getEffectDateTime() {
        return effectDateTime;
    }

    public boolean isOverride() {
        return override;
    }

    public String getNotes() {
        return notes;
    }
}
