package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckPriorMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckReportId;
import gov.nysenate.openleg.util.StringDiffer;

import java.util.LinkedList;

public class PriorMismatchView implements ViewObject
{
    protected SpotCheckReportId reportId;
    protected int mismatchId;
    protected String status;
    protected LinkedList<StringDiffer.Diff> diff;

    public PriorMismatchView(SpotCheckPriorMismatch priorMismatch) {
        if (priorMismatch != null) {
            this.mismatchId = priorMismatch.getMismatchId();
            this.reportId = priorMismatch.getReportId();
            this.status = priorMismatch.getStatus().name();
            this.diff = priorMismatch.getDiff(true);
        }
    }

    public int getMismatchId() {
        return mismatchId;
    }

    public SpotCheckReportId getReportId() {
        return reportId;
    }

    public String getStatus() {
        return status;
    }

    public LinkedList<StringDiffer.Diff> getDiff() {
        return diff;
    }

    @Override
    public String getViewType() {
        return "prior-mismatch";
    }
}
