package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.SimpleBillInfoView;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalId;
import gov.nysenate.openleg.model.calendar.spotcheck.CalendarEntryListId;
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
    public CalendarSupEntryView() {}

    //Added for Json deserialization
    public void setSectionType(String sectionType) {
        this.sectionType = sectionType;
    }

    //Added for Json deserialization
    public void setSubBillInfo(SimpleBillInfoView subBillInfo) {
        this.subBillInfo = subBillInfo;
    }

    //Added for spotcheck
    public boolean getBillHigh() {return billHigh;}

    public void setBillHigh(boolean billHigh) {
        this.billHigh = billHigh;
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
            (ent1, ent2) -> Integer.compare(ent1.billCalNo, ent2.billCalNo);

    @Override
    public String getViewType() {
        return "calendar-floor-entry";
    }

    public String toString() {
        return " Bill Cal No: " + this.getBillCalNo() +" Selected Version: " + this.getSelectedVersion() +
                " Section Type: " + this.getSectionType() + " BillHigh: " + this.getBillHigh() + " View Type: " + this.getViewType() + "\n\n";
    }

}
