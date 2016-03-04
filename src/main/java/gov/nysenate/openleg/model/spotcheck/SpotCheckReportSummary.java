package gov.nysenate.openleg.model.spotcheck;

/**
 * Holds summary information for a particular spotcheck report
 */
public class SpotCheckReportSummary extends SpotCheckSummary {

    /** The Id of the described report */
    protected SpotCheckReportId reportId;

    /** The report notes for the described report */
    protected String notes;

    /** --- Constructor --- */

    public SpotCheckReportSummary(SpotCheckReportId reportId, String notes) {
        this.reportId = reportId;
        this.notes = notes;
    }

    /** --- Getters / Setters --- */

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public String getNotes() {
        return notes;
    }
}
