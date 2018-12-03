package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import gov.nysenate.openleg.service.bill.data.BillDataService;

import java.util.Comparator;

public class CalendarSupEntryView extends CalendarEntryView {

    protected String sectionType;

    protected SimpleBillInfoView subBillInfo;

    protected boolean billHigh;

    public CalendarSupEntryView(CalendarSupplementalEntry supEntry, BillDataService billDataService) {
        super(supEntry, billDataService);

        if (supEntry != null) {
            this.sectionType = supEntry.getSectionType().toString();
            this.subBillInfo = supEntry.getSubBillId() != null
                    ? new SimpleBillInfoView(billDataService.getBillInfo(BillId.getBaseId(supEntry.getSubBillId())))
                    : null;
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

    public static Comparator<CalendarSupEntryView> supEntryViewComparator =
            Comparator.comparingInt(CalendarSupEntryView::getBillCalNo);

    @Override
    public String getViewType() {
        return "calendar-floor-entry";
    }

    public String toString() {
        return " Bill Cal No: " + this.getBillCalNo()  +"\n"+ " Selected Version: " + this.getSelectedVersion() +"\n"+
                " Section Type: " + this.getSectionType() +"\n"+ " BillHigh: " + this.getBillHigh() +"\n"+ " View Type: " + this.getViewType() + "\n\n";
    }

}
