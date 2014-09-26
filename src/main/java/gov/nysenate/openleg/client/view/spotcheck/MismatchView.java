package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.ViewList;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckPriorMismatch;
import gov.nysenate.openleg.util.StringDiffer;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class MismatchView implements ViewObject
{
    protected String mismatchType;
    protected String status;
    protected String referenceData;
    protected String observedData;
    protected String notes;
    protected LinkedList<StringDiffer.Diff> diff;
    protected ViewList<PriorMismatchView> prior;

    public MismatchView(SpotCheckMismatch mismatch, List<SpotCheckPriorMismatch> priorMismatches) {
        if (mismatch != null) {
            this.mismatchType = mismatch.getMismatchType().name();
            this.status = mismatch.getStatus().name();
            this.referenceData = mismatch.getReferenceData();
            this.observedData = mismatch.getObservedData();
            this.notes = mismatch.getNotes();
            this.diff = mismatch.getDiff(true);
            this.prior = new ViewList<>(
                priorMismatches.stream().map(pm -> new PriorMismatchView(pm))
                               .sorted((a,b) -> b.getReportId().compareTo(a.getReportId()))
                               .collect(Collectors.toList()));
        }
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

    public LinkedList<StringDiffer.Diff> getDiff() {
        return diff;
    }

    public String getNotes() {
        return notes;
    }

    public ViewList<PriorMismatchView> getPrior() {
        return prior;
    }


}