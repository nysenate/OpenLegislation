package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateToken;

import java.time.LocalDateTime;

public class UpdateTokenView implements ViewObject
{
    protected ViewObject id;
    protected LocalDateTime updatedDateTime;

    public UpdateTokenView(UpdateToken updateToken, ViewObject idView) {
        id = idView;
        updatedDateTime = updateToken.getUpdatedDateTime();
    }

    @Override
    public String getViewType() {
        return id.getViewType() + "-update-token";
    }

    public ViewObject getId() {
        return id;
    }

    public LocalDateTime getUpdatedDateTime() {
        return updatedDateTime;
    }
}
