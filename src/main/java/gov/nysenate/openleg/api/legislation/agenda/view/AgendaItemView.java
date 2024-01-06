package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillInfoView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;

public record AgendaItemView(BillIdView billId, BillInfoView billInfo, String message) implements ViewObject {
    public AgendaItemView(BillId billId, BillInfo billInfo, String message) {
        this(new BillIdView(billId), new BillInfoView(billInfo), message);
    }

    @Override
    public String getViewType() {
        return "agenda-info-bill";
    }
}
