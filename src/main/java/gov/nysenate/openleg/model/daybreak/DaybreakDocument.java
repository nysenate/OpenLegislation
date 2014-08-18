package gov.nysenate.openleg.model.daybreak;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Defines a Daybreak document that can be categorized by DaybreakDocType*/
public interface DaybreakDocument {

    /**
     * Returns the daybreak document type
     * @return
     */
    public DaybreakDocType getDayBreakDocType();

    /**
     * Returns the date of the associated Daybreak report
     * @return
     */
    public LocalDateTime getReportDateTime();
}
