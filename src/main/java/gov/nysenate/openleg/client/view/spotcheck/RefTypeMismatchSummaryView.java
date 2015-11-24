package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.model.spotcheck.RefTypeMismatchSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;

public class RefTypeMismatchSummaryView extends SpotCheckSummaryView {

    protected SpotCheckRefType refType;

    public RefTypeMismatchSummaryView(RefTypeMismatchSummary summary) {
        super(summary);
        this.refType = summary.getRefType();
    }

    public SpotCheckRefType getRefType() {
        return refType;
    }
}
