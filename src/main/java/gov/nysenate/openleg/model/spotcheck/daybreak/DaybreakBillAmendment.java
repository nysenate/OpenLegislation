package gov.nysenate.openleg.model.spotcheck.daybreak;

import gov.nysenate.openleg.model.bill.BillId;

import java.time.LocalDate;

public class DaybreakBillAmendment {

    /** The amendment's bill id */
    private BillId billId;

    /** The id of this bills same as bill */
    private BillId sameAs;

    /** The number of pages in the amendment's full text */
    private int pageCount;

    /** The date that this amendment was published */
    private LocalDate publishDate;

    /** --- Constructors --- */

    public DaybreakBillAmendment() {
    }

    public DaybreakBillAmendment(BillId billId, BillId sameAs, int pageCount, LocalDate publishDate) {
        this.billId = billId;
        this.sameAs = sameAs;
        this.pageCount = pageCount;
        this.publishDate = publishDate;
    }

    /** --- Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public BillId getSameAs() {
        return sameAs;
    }

    public void setSameAs(BillId sameAs) {
        this.sameAs = sameAs;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public LocalDate getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }
}
