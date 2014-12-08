package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarActiveListEntry;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.Comparator;

public class ActiveListEntryView extends SimpleBillInfoView {

    protected int billCalNo;

    public ActiveListEntryView(CalendarActiveListEntry activeListEntry, BillDataService billDataService) {
        super(activeListEntry != null ? billDataService.getBillInfo(BillId.getBaseId(activeListEntry.getBillId())) : null);
        if (activeListEntry != null) {
            this.billCalNo = activeListEntry.getBillCalNo();
        }
    }

    public int getBillCalNo() {
        return billCalNo;
    }

    public static Comparator<ActiveListEntryView> activeListEntryViewComparator =
            (ent1, ent2) -> Integer.compare(ent1.billCalNo, ent2.billCalNo);

    @Override
    public String getViewType() {
        return "calendar-activelist-entry";
    }
}
