package gov.nysenate.openleg.model.spotcheck.daybreak;

import com.google.common.base.MoreObjects;
import gov.nysenate.openleg.model.bill.BillId;

import java.time.LocalDate;
import java.util.Map;

/**
 * A fragment of a daybreak report file.  Contains information pertaining to a single bill and its amendments
 * DaybreakFragments are parsed into DaybreakBills
 * @see DaybreakBill
 */
public class DaybreakFragment {

    /** The id of the associated bill's current amendment */
    private BillId billId;

    /** The date that this fragment's report was received */
    private LocalDate reportDate;

    /** The file that this fragment was parsed from */
    private DaybreakFile daybreakFile;

    /** The text for this daybreak entry, to be parsed */
    private String daybreakText;

    /** A list containing all page file entries associated with this bill keyed by an amendment id*/
    private Map<BillId, PageFileEntry> pageFileEntries;

    /** --- Constructors --- */

    public DaybreakFragment(BillId billId, DaybreakFile daybreakFile, String daybreakText ) {
        this.daybreakFile = daybreakFile;
        this.daybreakText = daybreakText;
        this.billId = billId;
        this.reportDate = daybreakFile.getReportDate();
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("BillId", billId)
                .add("DayBreakFile", daybreakFile)
                .toString();
    }

    /** --- Functional Getters/Setters --- */

    public DaybreakBillId getDaybreakBillId(){
        return new DaybreakBillId(BillId.getBaseId(billId), reportDate);
    }

    /** --- Basic Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public DaybreakFile getDaybreakFile() {
        return daybreakFile;
    }

    public void setDaybreakFile(DaybreakFile daybreakFile) {
        this.daybreakFile = daybreakFile;
    }

    public String getDaybreakText() {
        return daybreakText;
    }

    public void setDaybreakText(String daybreakText) {
        this.daybreakText = daybreakText;
    }

    public Map<BillId, PageFileEntry> getPageFileEntries() {
        return pageFileEntries;
    }

    public void setPageFileEntries(Map<BillId, PageFileEntry> pageFileEntries) {
        this.pageFileEntries = pageFileEntries;
    }
}
