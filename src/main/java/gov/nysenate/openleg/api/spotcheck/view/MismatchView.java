package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.DeNormSpotCheckMismatch;

public class MismatchView<ContentKey> extends MismatchSummaryView<ContentKey> implements ViewObject {

    private final String referenceData;
    private final String observedData;

    public MismatchView(DeNormSpotCheckMismatch<ContentKey> mismatch) {
        super(mismatch);
        this.referenceData = mismatch.getReferenceData();
        this.observedData = mismatch.getObservedData();
    }

    public String getReferenceData() {
        return referenceData;
    }

    public String getObservedData() {
        return observedData;
    }

    @Override
    public String getViewType() {
        return "mismatch";
    }
}
