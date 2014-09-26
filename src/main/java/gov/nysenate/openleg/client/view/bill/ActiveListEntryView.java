package gov.nysenate.openleg.client.view.bill;

import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;

public class ActiveListEntryView extends BillIdView {

    protected int billCalNo;

    public ActiveListEntryView(CalendarActiveListEntry activeListEntry) {
        super(activeListEntry.getBillId());
        this.billCalNo = activeListEntry.getBillCalNo();
    }

    public int getBillCalNo() {
        return billCalNo;
    }
}
