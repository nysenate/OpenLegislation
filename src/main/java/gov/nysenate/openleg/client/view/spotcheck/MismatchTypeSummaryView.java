package gov.nysenate.openleg.client.view.spotcheck;

import gov.nysenate.openleg.client.view.base.MapView;
import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.spotcheck.MismatchTypeSummary;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;

import java.util.HashMap;
import java.util.Map;

public class MismatchTypeSummaryView  implements ViewObject {

    protected MapView<SpotCheckMismatchType, Integer> typeCount;

    public MismatchTypeSummaryView(MismatchTypeSummary summary) {
        this.typeCount = MapView.ofIntMap(summary.getSummary());
    }

    public MapView<SpotCheckMismatchType, Integer> getTypeCount(){
        return typeCount;
    }

    @Override
    public String getViewType() {
        return "mismatch-type-summary";
    }
}
