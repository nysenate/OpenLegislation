package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillInfoView;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

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

    protected AgendaItemView(){
        super();
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
