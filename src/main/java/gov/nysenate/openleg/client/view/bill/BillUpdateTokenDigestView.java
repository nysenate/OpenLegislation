package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateInfo;

import java.util.List;
import java.util.stream.Collectors;

public class BillUpdateTokenDigestView extends BillUpdateTokenView
{
    protected ListView<BillUpdateDigestView> updates;

    public BillUpdateTokenDigestView(BillUpdateInfo token, List<BillUpdateDigest> digests) {
        super(token);
        if (digests != null) {
            this.updates = ListView.of(digests.stream()
                .map(digest -> new BillUpdateDigestView(digest)).collect(Collectors.toList()));
        }
    }

    @Override
    public String getViewType() {
        return "bill-update-token-digest";
    }

    public ListView<BillUpdateDigestView> getUpdates() {
        return updates;
    }
}
