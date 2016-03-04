package gov.nysenate.openleg.client.view.agenda;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.model.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillInfo;
import gov.nysenate.openleg.service.bill.data.BillDataService;

public class AgendaItemView implements ViewObject
{
    private BillIdView billId;
    private BillInfoView billInfo;
    private String message;

    public AgendaItemView(AgendaInfoCommitteeItem item, BillDataService billDataService) {
        if (item != null) {
            billId = new BillIdView(item.getBillId());
            if (billDataService != null) {
                this.billInfo = new BillInfoView(billDataService.getBillInfoSafe(BaseBillId.of(item.getBillId())));
            }
            this.message = item.getMessage();
        }
    }

    public BillIdView getBillId() {
        return billId;
    }

    public BillInfoView getBillInfo() {
        return billInfo;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getViewType() {
        return "agenda-info-bill";
    }
}
