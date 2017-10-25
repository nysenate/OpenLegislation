package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.*;

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
