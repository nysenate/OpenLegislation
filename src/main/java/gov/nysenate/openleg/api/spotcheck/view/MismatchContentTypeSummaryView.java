package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.MapView;
import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.MismatchContentTypeSummary;
import gov.nysenate.openleg.spotchecks.model.SpotCheckContentType;

public class MismatchContentTypeSummaryView implements ViewObject{

    protected MapView<SpotCheckContentType, Integer> summary;

    public MismatchContentTypeSummaryView(MismatchContentTypeSummary summary){
        this.summary = MapView.ofIntMap(summary.getSummary());
    }

    public MapView<SpotCheckContentType, Integer> getSummary(){
        return summary;
    }
    @Override
    public String getViewType() {
        return "mismatch-content-type-summary";
    }

}
