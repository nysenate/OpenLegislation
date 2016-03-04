package gov.nysenate.openleg.model.spotcheck;

/**
 * A SpotCheckPriorMismatch is a mismatch that has previously been recorded by some spot check
 * persistence layer. It has a report id associated with it to provide some context.
 */
public class SpotCheckPriorMismatch extends SpotCheckMismatch
{
    /** The id of the report where this mismatch was recorded. */
    protected SpotCheckReportId reportId;

    /** --- Constructor --- */

    public SpotCheckPriorMismatch(SpotCheckMismatchType mismatchType, String referenceData, String observedData) {
        super(mismatchType, observedData, referenceData);
    }

    public SpotCheckPriorMismatch(SpotCheckMismatchType mismatchType, String referenceData, String observedData,
                                  String notes) {
        super(mismatchType, observedData, referenceData, notes);
    }

    /** --- Basic Getters/Setters --- */

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public void setReportId(SpotCheckReportId reportId) {
        this.reportId = reportId;
    }
}