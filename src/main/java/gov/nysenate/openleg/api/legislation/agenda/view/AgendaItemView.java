package gov.nysenate.openleg.api.legislation.agenda.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.api.legislation.bill.view.BillIdView;
import gov.nysenate.openleg.api.legislation.bill.view.BillInfoView;
import gov.nysenate.openleg.legislation.agenda.AgendaInfoCommitteeItem;
import gov.nysenate.openleg.legislation.bill.BaseBillId;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

public record AgendaItemView(BillIdView billId, BillInfoView billInfo, String message) implements ViewObject {
    public AgendaItemView(AgendaInfoCommitteeItem item, BillDataService billDataService) {
        this(new BillIdView(item.getBillId()), billDataService == null ?
                        null : new BillInfoView(billDataService.getBillInfoSafe(BaseBillId.of(item.getBillId()))),
                item.getMessage());
    }

    @Override
    public String getViewType() {
        return "agenda-info-bill";
    }
}
