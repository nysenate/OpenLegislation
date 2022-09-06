package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.DeNormSpotCheckMismatch;

public class MismatchHtmlDiffView<ContentKey> extends MismatchSummaryView<ContentKey> implements ViewObject {

    private String combinedHtmlDiff;
    private String referenceHtmlDiff;
    private String observedHtmlDiff;

    private MismatchHtmlDiffView(DeNormSpotCheckMismatch<ContentKey> mismatch) {
        super(mismatch);
    }

    public MismatchHtmlDiffView(DeNormSpotCheckMismatch<ContentKey> mismatch,
                                String combinedHtmlDiff,
                                String referenceHtmlDiff,
                                String observedHtmlDiff) {
        super(mismatch);
        this.combinedHtmlDiff = combinedHtmlDiff;
        this.referenceHtmlDiff = referenceHtmlDiff;
        this.observedHtmlDiff = observedHtmlDiff;
    }

    public String getCombinedHtmlDiff() {
        return combinedHtmlDiff;
    }

    public String getReferenceHtmlDiff() {
        return referenceHtmlDiff;
    }

    public String getObservedHtmlDiff() {
        return observedHtmlDiff;
    }

    @Override
    public String getViewType() {
        return "mismatch-html-diff";
    }
}
