package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.MismatchSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatusSummary;

import java.util.HashMap;
import java.util.Map;

public class MismatchSummaryView implements ViewObject {

    protected MapView<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummaryView> summary;

    public MismatchSummaryView(MismatchSummary summary) {
        Map<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummaryView> summaryView = new HashMap<>();
        for (Map.Entry<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummary> entry : summary.getSummary().entrySet()) {
            summaryView.put(entry.getKey(), new SpotCheckMismatchStatusSummaryView(entry.getValue()));
        }
        this.summary = MapView.of(summaryView);
    }

    public MapView<SpotCheckMismatchStatus, SpotCheckMismatchStatusSummaryView> getSummary() {
        return summary;
    }

    @Override
    public String getViewType() {
        return "mismatch-summary";
    }
}
