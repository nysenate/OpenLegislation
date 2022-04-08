package gov.nysenate.openleg.api.legislation.committee.view;

import gov.nysenate.openleg.legislation.committee.CommitteeSessionId;

public class CommitteeSessionIdView extends CommitteeIdView {

    protected int sessionYear;

    public CommitteeSessionIdView(CommitteeSessionId committeeSessionId) {
        super(committeeSessionId);
        if (committeeSessionId != null) {
            this.sessionYear = committeeSessionId.getSession() != null ? committeeSessionId.getSession().year() : 0;
        }
    }

    public CommitteeSessionIdView() {
    }

    @Override
    public String getViewType() {
        return "committee-session-id";
    }

    public int getSessionYear() {
        return sessionYear;
    }
}
