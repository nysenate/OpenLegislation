package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateDigest;

public class UpdateDigestModelView extends UpdateDigestView
{
    private ViewObject item;

    public UpdateDigestModelView(UpdateDigest<?> updateDigest, ViewObject idView, ViewObject item) {
        super(updateDigest, idView);
        this.item = item;
    }

    public ViewObject getItem() {
        return item;
    }

    @Override
    public String getViewType() {
        return super.getViewType() + "with-item";
    }
}
