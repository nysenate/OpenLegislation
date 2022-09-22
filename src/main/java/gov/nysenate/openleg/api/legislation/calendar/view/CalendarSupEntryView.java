package gov.nysenate.openleg.api.legislation.calendar.view;

import gov.nysenate.openleg.api.legislation.bill.view.SimpleBillInfoView;
import gov.nysenate.openleg.legislation.bill.BillId;
import gov.nysenate.openleg.legislation.bill.BillInfo;
import gov.nysenate.openleg.legislation.bill.dao.service.BillDataService;
import gov.nysenate.openleg.legislation.calendar.CalendarSupplementalEntry;

import java.util.Comparator;

public class CalendarSupEntryView extends CalendarEntryView {
    protected String sectionType;
    protected SimpleBillInfoView subBillInfo;
    protected boolean billHigh;

    public CalendarSupEntryView(CalendarSupplementalEntry supEntry, BillInfo info) {
        super(supEntry, info);

        if (supEntry != null) {
            this.sectionType = supEntry.getSectionType().toString();
            this.subBillInfo = supEntry.getSubBillId() != null
                    ? new SimpleBillInfoView(info) : null;
            this.billHigh = supEntry.getBillHigh();
        }
    }

    //Added for Json deserialization
    protected CalendarSupEntryView() {}

    public boolean getBillHigh() {
        return billHigh;
    }

    public String getSectionType() {
        return sectionType;
    }

    public SimpleBillInfoView getSubBillInfo() {
        return subBillInfo;
    }

    public boolean isBillHigh() {
        return billHigh;
    }

    @Override
    public String getViewType() {
        return "calendar-floor-entry";
    }

    public String toString() {
        return " Bill Cal No: " + this.getBillCalNo()  +"\n"+ " Selected Version: " + this.getSelectedVersion() +"\n"+
                " Section Type: " + this.getSectionType() +"\n"+ " BillHigh: " + this.getBillHigh() +"\n"+ " View Type: " + this.getViewType() + "\n\n";
    }

}
