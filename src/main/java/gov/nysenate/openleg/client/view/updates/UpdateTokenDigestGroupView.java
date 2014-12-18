package gov.nysenate.openleg.client.view.updates;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.updates.UpdateDigest;
import gov.nysenate.openleg.model.updates.UpdateToken;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateTokenDigestGroupView<ContentId> extends UpdateTokenView
{
    protected ListView<UpdateDigestView> updates;

    public UpdateTokenDigestGroupView(UpdateToken<ContentId> updateToken, ViewObject idView, List<UpdateDigest<ContentId>> digests)  {
        super(updateToken, idView);
        this.updates = ListView.of(digests.stream().map(UpdateDigestView::new).collect(Collectors.toList()));
    }

    @Override
    public String getViewType() {
        return id.getViewType() + "-update-token-digest";
    }

    public ListView<UpdateDigestView> getUpdates() {
        return updates;
    }
}
