package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.client.view.base.ViewObject;
import gov.nysenate.openleg.model.entity.Committee;

public class CommitteeView extends CommitteeVersionIdView implements ViewObject {

    protected String reformed;
    protected String location;
    protected String meetDay;


    public CommitteeView(Committee committee) {
        super(committee != null ? committee.getVersionId() : null);
        if (committee != null) {

        }
    }

    @Override
    public String getViewType() {
        return "committee";
    }
}
