package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.api.legislation.bill.view.BillInfoView;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.Version;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.calendar.CalendarEntry;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;

import java.util.Comparator;
import java.util.Optional;

public class CalendarEntryView extends BillInfoView {
    protected int billCalNo;

    protected String selectedVersion;

    public CalendarEntryView(CalendarEntry calendarEntry, BillInfo info) {
        super(info);
        if (calendarEntry != null) {
            this.billCalNo = calendarEntry.getBillCalNo();
            this.selectedVersion = Optional.ofNullable(calendarEntry.getBillId())
                    .map(BillId::getVersion).map(Version::toString).orElse(null);
        }
    }

    // Added for Json deserialization
    protected CalendarEntryView() {}

    public int getBillCalNo() {
        return billCalNo;
    }

    public String getSelectedVersion() {
        return selectedVersion;
    }

    @Override
    public String getViewType() {
        return "calendar-activelist-entry";
    }

    public String toString() {
        return " Bill Cal No: " + this.getBillCalNo() + "\n" + " Selected Version: " + this.getSelectedVersion() +"\n\n";
    }
}
