package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.BillIdView;
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

    @Override
    public String getViewType() {
        return "calendar-activelist-entry";
    }
}
