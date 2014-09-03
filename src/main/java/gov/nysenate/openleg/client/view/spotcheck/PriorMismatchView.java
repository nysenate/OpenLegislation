package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.SpotCheckPriorMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;

public class PriorMismatchView
{
    protected SpotCheckReportId reportId;
    protected String status;
    protected String referenceData;
    protected String observedData;

    public PriorMismatchView(SpotCheckPriorMismatch priorMismatch) {
        if (priorMismatch != null) {
            this.reportId = priorMismatch.getReportId();
            this.status = priorMismatch.getStatus().name();
            this.referenceData = priorMismatch.getReferenceData();
            this.observedData = priorMismatch.getObservedData();
        }
    }

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public String getStatus() {
        return status;
    }

    public String getReferenceData() {
        return referenceData;
    }

    public String getObservedData() {
        return observedData;
    }
}
