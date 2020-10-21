package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.SpotCheckPriorMismatch;
import gov.nysenate.openleg.spotchecks.model.SpotCheckReportId;
import gov.nysenate.openleg.common.util.StringDiffer;

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
            this.status = priorMismatch.getState().name();
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
