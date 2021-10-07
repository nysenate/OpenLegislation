package gov.nysenate.openleg.api.updates.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.updates.UpdateToken;

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
