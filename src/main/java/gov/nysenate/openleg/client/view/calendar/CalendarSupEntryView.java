package gov.nysenate.openleg.client.view.calendar;

import gov.nysenate.openleg.client.view.bill.BillIdView;
import gov.nysenate.openleg.model.bill.BillId;
import gov.nysenate.openleg.model.calendar.CalendarSupplementalEntry;

public class CalendarSupEntryView extends BillIdView {

    protected int billCalNo;

    protected String sectionType;

    protected BillIdView subBillId;

    protected boolean billHigh;

    public CalendarSupEntryView(CalendarSupplementalEntry supEntry) {
        super(supEntry.getBillId());

        this.billCalNo = supEntry.getBillCalNo();
        this.sectionType = supEntry.getSectionType().toString();
        this.subBillId = supEntry.getSubBillId() != null ? new BillIdView(supEntry.getSubBillId()) : null;
        this.billHigh = supEntry.getBillHigh();
    }

    public int getBillCalNo() {
        return billCalNo;
    }

    public String getSectionType() {
        return sectionType;
    }

    public BillIdView getSubBillId() {
        return subBillId;
    }

    public boolean isBillHigh() {
        return billHigh;
    }

    @Override
    public String getViewType() {
        return "calendar-floor-entry";
    }
}
