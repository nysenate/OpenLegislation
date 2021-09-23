package gov.nysenate.openleg.api.legislation.committee.view;

import gov.nysenate.openleg.api.ViewObject;
import gov.nysenate.openleg.legislation.committee.CommitteeId;
import org.apache.commons.text.WordUtils;

public class CommitteeIdView implements ViewObject {

    protected String chamber;
    protected String name;

    public CommitteeIdView(CommitteeId committeeId) {
        if (committeeId != null) {
            this.chamber = committeeId.getChamber().name();
            this.name = WordUtils.capitalizeFully(committeeId.getName());
        }
    }

    public CommitteeIdView(){}

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
