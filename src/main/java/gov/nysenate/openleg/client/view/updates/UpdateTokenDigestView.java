package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;

public class UpdateTokenDigestView<ContentId> extends UpdateTokenView implements ViewObject
{
    protected UpdateDigestView update;

    public UpdateTokenDigestView(UpdateToken updateToken, ViewObject idView, UpdateDigest<ContentId> digest) {
        super(updateToken, idView);
        this.update = new UpdateDigestView(digest);
    }

    @Override
    public String getViewType() {
        return "update-token-digest-view";
    }

    public UpdateDigestView getUpdate() {
        return update;
    }
}
