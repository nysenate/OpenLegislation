package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.model.entity.CommitteeSessionId;

public class CommitteeSessionIdView extends CommitteeIdView {

    protected int sessionYear;

    public CommitteeSessionIdView(CommitteeSessionId committeeSessionId) {
        super(committeeSessionId);
        if (committeeSessionId != null) {
            this.sessionYear = committeeSessionId.getSession() != null ? committeeSessionId.getSession().getYear() : 0;
        }
    }

    @Override
    public String getViewType() {
        return "committee-session-id";
    }

    public int getSessionYear() {
        return sessionYear;
    }
}
