package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.Comparator;

public class CalendarEntryView extends SimpleBillInfoView {

    protected int billCalNo;

    public CalendarEntryView(CalendarEntry calendarEntry, BillDataService billDataService) {
        super(calendarEntry != null ? billDataService.getBillInfoSafe(BillId.getBaseId(calendarEntry.getBillId())) : null);
        if (calendarEntry != null) {
            this.billCalNo = calendarEntry.getBillCalNo();
        }
    }

    public int getBillCalNo() {
        return billCalNo;
    }

    public static Comparator<CalendarEntryView> calEntryViewComparator =
            (ent1, ent2) -> Integer.compare(ent1.billCalNo, ent2.billCalNo);

    @Override
    public String getViewType() {
        return "calendar-activelist-entry";
    }
}
