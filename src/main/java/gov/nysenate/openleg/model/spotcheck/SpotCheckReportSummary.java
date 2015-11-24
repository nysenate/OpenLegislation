package gov.nysenate.openleg.model.spotcheck;

/**
 * Holds summary information for a particular spotcheck report
 */
public class SpotCheckReportSummary extends SpotCheckSummary {

    /** The Id of the described report */
    protected SpotCheckReportId reportId;

    /** The report notes for the described report */
    protected String notes;

    /** The number of observations that were conducted for this report */
    protected int observedCount;

    /** --- Constructor --- */

    public SpotCheckReportSummary(SpotCheckReportId reportId, String notes, int observedCount) {
        this.reportId = reportId;
        this.notes = notes;
        this.observedCount = observedCount;
    }

    /** --- Getters / Setters --- */

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public String getNotes() {
        return notes;
    }

    public int getObservedCount() {
        return observedCount;
    }
}
