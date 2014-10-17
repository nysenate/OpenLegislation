package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.CommitteeId;

public class CommitteeIdView implements ViewObject {

    protected String chamber;
    protected String name;

    public CommitteeIdView(CommitteeId committeeId) {
        if (committeeId != null) {
            this.chamber = committeeId.getChamber().name();
            this.name = committeeId.getName();
        }
    }

    public String getChamber() {
        return chamber;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getViewType() {
        return "committee-id";
    }
}
