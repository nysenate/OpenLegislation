package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchIgnore;
import gov.nysenate.openleg.model.spotcheck.SpotCheckPriorMismatch;
import gov.nysenate.openleg.util.StringDiffer;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MismatchView implements ViewObject
{
    protected int mismatchId;
    protected String mismatchType;
    protected String status;
    protected String referenceData;
    protected String observedData;
    protected String notes;
    protected ListView<String> issueIds;
    protected ListView<PriorMismatchView> prior;
    protected SpotCheckMismatchIgnore ignoreStatus;

    public MismatchView(SpotCheckMismatch mismatch, List<SpotCheckPriorMismatch> priorMismatches) {
        if (mismatch != null) {
            this.mismatchId = mismatch.getMismatchId();
            this.mismatchType = mismatch.getMismatchType().name();
            this.status = mismatch.getStatus().name();
            this.referenceData = mismatch.getReferenceData();
            this.observedData = mismatch.getObservedData();
            this.notes = mismatch.getNotes();
            this.issueIds = ListView.ofStringList(mismatch.getIssueIds());
            this.ignoreStatus = mismatch.getIgnoreStatus();
            this.prior = ListView.of(
                    priorMismatches.stream().map(PriorMismatchView::new)
                            .sorted((a, b) -> b.getReportId().compareTo(a.getReportId()))
                            .collect(Collectors.toList()));
        }
    }

    public int getMismatchId() {
        return mismatchId;
    }

    public String getMismatchType() {
        return mismatchType;
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

    public String getNotes() {
        return notes;
    }

    public ListView<String> getIssueIds() {
        return issueIds;
    }

    public SpotCheckMismatchIgnore getIgnoreStatus() {
        return ignoreStatus;
    }

    public ListView<PriorMismatchView> getPrior() {
        return prior;
    }

    @Override
    public String getViewType() {
        return "mismatch";
    }
}