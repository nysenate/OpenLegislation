package gov.nysenate.openleg.spotchecks.daybreak;

import java.time.LocalDateTime;

/** Defines a Daybreak document that can be categorized by DaybreakDocType*/
public interface DaybreakDocument {

    /**
     * Returns the daybreak document type
     * @return
     */
    DaybreakDocType getDaybreakDocType();

    /**
     * Returns the date of the associated Daybreak report
     * @return
     */
    LocalDateTime getReportDateTime();
}
