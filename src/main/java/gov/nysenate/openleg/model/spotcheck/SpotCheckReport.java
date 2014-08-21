package gov.nysenate.openleg.model.spotcheck;

import java.time.LocalDateTime;
import java.util.Map;

public class SpotCheckReport<ContentKey>
{
    /** Identifier for this report. */
    protected SpotCheckReportId reportId;

    /** All observations associated with this report. */
    protected Map<ContentKey, SpotCheckObservation<ContentKey>> observations;

    /** --- Constructors --- */

    public SpotCheckReport() {}

    /** --- Methods --- */


    /** --- Delegates --- */

    public LocalDateTime getReportDateTime() {
        return reportId.getReportDateTime();
    }

    public SpotCheckRefType getReferenceType() {
        return reportId.getReferenceType();
    }

    /** --- Basic Getters/Setters --- */

    public Map<ContentKey, SpotCheckObservation<ContentKey>> getObservations() {
        return observations;
    }

    public void setObservations(Map<ContentKey, SpotCheckObservation<ContentKey>> observations) {
        this.observations = observations;
    }
}