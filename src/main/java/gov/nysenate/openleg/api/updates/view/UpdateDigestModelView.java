package gov.nysenate.openleg.api.updates.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.updates.UpdateDigest;

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
