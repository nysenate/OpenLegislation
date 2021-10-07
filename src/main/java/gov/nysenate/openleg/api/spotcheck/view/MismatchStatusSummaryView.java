package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.MismatchStatus;
import gov.nysenate.openleg.spotchecks.model.MismatchStatusSummary;

public class MismatchStatusSummaryView implements ViewObject {

    protected MapView<MismatchStatus, Integer> summary;


    public MismatchStatusSummaryView(MismatchStatusSummary summary) {
        this.summary = MapView.ofIntMap(summary.getSummary());
    }

    public MapView<MismatchStatus, Integer> getSummary() {
        return summary;
    }


    @Override
    public String getViewType() {
        return "mismatch-status-summary";
    }
}
