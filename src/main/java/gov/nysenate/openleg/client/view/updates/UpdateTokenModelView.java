package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateToken;

/**
 * Adds a field to display a ViewModel as part of the token response.
 */
public class UpdateTokenModelView extends UpdateTokenView
{
    protected ViewObject item;

    public UpdateTokenModelView(UpdateToken updateToken, ViewObject idView, ViewObject item) {
        super(updateToken, idView);
        this.item = item;
    }

    public ViewObject getItem() {
        return item;
    }

    @Override
    public String getViewType() {
        return super.getViewType() + "-with-item";
    }
}
