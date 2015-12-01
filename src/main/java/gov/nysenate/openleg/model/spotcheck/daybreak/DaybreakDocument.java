package gov.nysenate.openleg.model.spotcheck.daybreak;

import java.time.LocalDateTime;

/** Defines a Daybreak document that can be categorized by DaybreakDocType*/
public interface DaybreakDocument {

    /**
     * Returns the daybreak document type
     * @return
     */
    public DaybreakDocType getDaybreakDocType();

    /**
     * Returns the date of the associated Daybreak report
     * @return
     */
    public LocalDateTime getReportDateTime();
}
