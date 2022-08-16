package gov.nysenate.openleg.api.spotcheck.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.spotchecks.model.SpotCheckMismatchType;

public class SpotCheckMismatchTypeView implements ViewObject {

    private String name;
    private String displayName;

    public SpotCheckMismatchTypeView() {
    }

    public SpotCheckMismatchTypeView(SpotCheckMismatchType mismatchType) {
        this.name = mismatchType.name();
        this.displayName = mismatchType.getDisplayName();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getViewType() {
        return "spotcheck-mismatch-type";
    }
}
