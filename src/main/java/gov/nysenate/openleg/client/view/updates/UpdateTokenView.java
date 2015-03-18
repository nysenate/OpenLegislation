package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateToken;

import java.time.LocalDateTime;

public class UpdateTokenView implements ViewObject
{
    protected ViewObject id;
    protected String contentType;
    protected String sourceId;
    protected LocalDateTime sourceDateTime;
    protected LocalDateTime processedDateTime;

    public UpdateTokenView(UpdateToken updateToken, ViewObject idView) {
        id = idView;
        if (updateToken != null) {
            this.contentType = updateToken.getContentType() != null ? updateToken.getContentType().toString(): null;
            this.sourceId = updateToken.getSourceId();
            this.sourceDateTime = updateToken.getSourceDateTime();
            this.processedDateTime = updateToken.getProcessedDateTime();
        }
    }

    @Override
    public String getViewType() {
        return "update-token";
    }

    public ViewObject getId() {
        return id;
    }

    public String getContentType() {
        return contentType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public LocalDateTime getSourceDateTime() {
        return sourceDateTime;
    }

    public LocalDateTime getProcessedDateTime() {
        return processedDateTime;
    }
}
