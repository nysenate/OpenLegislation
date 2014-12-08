package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.model.bill.BillUpdateDigest;
import gov.nysenate.openleg.model.bill.BillUpdateToken;

import java.util.List;
import java.util.stream.Collectors;

public class BillUpdateTokenDigestView extends BillUpdateTokenView
{
    protected List<BillUpdateDigestView> updates;

    public BillUpdateTokenDigestView(BillUpdateToken token, List<BillUpdateDigest> digests) {
        super(token);
        if (digests != null) {
            this.updates = digests.stream()
                .map(digest -> new BillUpdateDigestView(digest)).collect(Collectors.toList());
        }
    }

    @Override
    public String getViewType() {
        return "bill-update-token-digest";
    }

    public List<BillUpdateDigestView> getUpdates() {
        return updates;
    }
}
