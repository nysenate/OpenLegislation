package gov.nysenate.openleg.model.daybreak;

import gov.nysenate.openleg.model.bill.BaseBillId;
import gov.nysenate.openleg.model.bill.BillId;

import java.time.LocalDate;

/** Serves as an identifier for daybreak fragments
 */
public class DaybreakFragmentId {

    /** The Id of the fragment's bill */
    private BaseBillId baseBillId;

    /** The date of the Daybreak Report that generated the fragment*/
    private LocalDate reportDate;

    /** --- Constructors --- */

    public DaybreakFragmentId(BaseBillId baseBillId, LocalDate reportDate) {
        this.baseBillId = baseBillId;
        this.reportDate = reportDate;
    }

    /** --- Getters/Setters --- */

    public BaseBillId getBaseBillId() {
        return baseBillId;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }
}
