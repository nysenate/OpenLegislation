package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommitteeItem;

public class AgendaItemView implements ViewObject
{
    private BillIdView billId;
    private String message;

    public AgendaItemView(AgendaInfoCommitteeItem item) {
        this.billId = new BillIdView(item.getBillId());
        this.message = item.getMessage();
    }

    public BillIdView getBillId() {
        return billId;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getViewType() {
        return "agenda-info-bill";
    }
}
