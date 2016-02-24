package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.BillInfoView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.Comparator;
import java.util.Optional;

public class CalendarEntryView extends BillInfoView
{
    protected int billCalNo;

    protected String selectedVersion;

    public CalendarEntryView(CalendarEntry calendarEntry, BillDataService billDataService) {
        super(calendarEntry != null ? billDataService.getBillInfoSafe(BillId.getBaseId(calendarEntry.getBillId())) : null);
        if (calendarEntry != null) {
            this.billCalNo = calendarEntry.getBillCalNo();
            this.selectedVersion = Optional.ofNullable(calendarEntry.getBillId())
                    .map(BillId::getVersion)
                    .map(Version::getValue)
                    .orElse(null);
        }
    }

    public int getBillCalNo() {
        return billCalNo;
    }

    public String getSelectedVersion() {
        return selectedVersion;
    }

    public static Comparator<CalendarEntryView> calEntryViewComparator =
            (ent1, ent2) -> Integer.compare(ent1.billCalNo, ent2.billCalNo);

    @Override
    public String getViewType() {
        return "calendar-activelist-entry";
    }
}
