package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.SpotCheckContentType;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatus;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchStatusSummary;

public class SpotCheckMismatchStatusSummaryView implements ViewObject {

    protected SpotCheckMismatchStatus status;
    protected int total;
    protected MapView<SpotCheckContentType, Integer> contentTypeCounts;

    public SpotCheckMismatchStatusSummaryView(SpotCheckMismatchStatusSummary summary) {
        this.status = summary.getStatus();
        this.total = summary.getTotal();
        this.contentTypeCounts = MapView.ofIntMap(summary.getContentTypeCounts());
    }

    public SpotCheckMismatchStatus getStatus() {
        return status;
    }

    public int getTotal() {
        return total;
    }

    public MapView<SpotCheckContentType, Integer> getContentTypeCounts() {
        return contentTypeCounts;
    }

    @Override
    public String getViewType() {
        return "spotcheck-mismatch-status-summary";
    }
}
