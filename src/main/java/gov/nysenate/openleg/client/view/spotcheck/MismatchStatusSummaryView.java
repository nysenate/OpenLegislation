package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.*;

import java.util.HashMap;
import java.util.Map;

public class MismatchStatusSummaryView implements ViewObject {

    protected MapView<SpotCheckMismatchStatus, Integer> summary;


    public MismatchStatusSummaryView(MismatchStatusSummary summary) {
        this.summary = MapView.ofIntMap(summary.getSummary());
    }

    public MapView<SpotCheckMismatchStatus, Integer> getSummary() {
        return summary;
    }


    @Override
    public String getViewType() {
        return "mismatch-status-summary";
    }
}
