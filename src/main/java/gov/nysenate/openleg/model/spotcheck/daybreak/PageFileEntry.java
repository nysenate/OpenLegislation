package gov.nysenate.openleg.model.spotcheck.daybreak;

import com.google.common.base.MoreObjects;
import gov.nysenate.openleg.model.bill.BillId;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a line from a daybreak page file for a single bill version.
 * Contains identifiers for the senate and assembly versions of the bill if they exist.
 * Also contains the date the bill version was published and the full text page count.
 */
public class PageFileEntry {

    /** The bill id for the senate version of the bill */
    private BillId senateBillId;

    /** The bill id for the assembly version of the bill */
    private BillId assemblyBillId;

    /** The date that the associated daybreak report was received */
    private LocalDate reportDate;

    /** The file that this entry was retrieved from */
    private DaybreakFile daybreakFile;

    /** The date that this bill version was published */
    private LocalDate publishedDate;

    /** The number of pages in the bill text */
    private int pageCount;

    /** --- Constructors --- */

    public PageFileEntry(BillId senateBillId, BillId assemblyBillId, DaybreakFile daybreakFile, LocalDate publishedDate, int pageCount) {
        this.senateBillId = senateBillId;
        this.assemblyBillId = assemblyBillId;
        this.daybreakFile = daybreakFile;
        this.publishedDate = publishedDate;
        this.pageCount = pageCount;
        this.reportDate = daybreakFile.getReportDate();
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("senateBillId", senateBillId)
                .add("assemblyBillId", assemblyBillId)
                .add("reportDate", reportDate)
                .add("daybreakFile", daybreakFile)
                .add("publishedDate", publishedDate)
                .add("pageCount", pageCount)
                .toString();
    }

    /** --- Functional Getters/Setters --- */

    /**
     * Returns Senate and/or Assembly bill ids as a list.  Omits null ids
     * @return
     */
    public List<BillId> getBillIds(){
        List<BillId> billIds = new LinkedList<>();
        if(senateBillId!=null){
            billIds.add(senateBillId);
        }
        if(assemblyBillId!=null){
            billIds.add(assemblyBillId);
        }
        return billIds;
    }

    /** --- Getters/Setters --- */

    public BillId getSenateBillId() {
        return senateBillId;
    }

    public void setSenateBillId(BillId senateBillId) {
        this.senateBillId = senateBillId;
    }

    public BillId getAssemblyBillId() {
        return assemblyBillId;
    }

    public void setAssemblyBillId(BillId assemblyBillId) {
        this.assemblyBillId = assemblyBillId;
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

    public LocalDate getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(LocalDate publishedDate) {
        this.publishedDate = publishedDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }
}
