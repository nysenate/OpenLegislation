package gov.nysenate.openleg.api.legislation.committee.view;

import gov.nysenate.openleg.api.legislation.member.view.MemberView;
import gov.nysenate.openleg.legislation.committee.CommitteeMember;

public class CommitteeMemberView extends MemberView {

    protected int sequenceNo;
    protected String title;

    public CommitteeMemberView(CommitteeMember committeeMember) {
        super(committeeMember != null ? committeeMember.getSessionMember() : null);
        if (committeeMember != null) {
            this.sequenceNo = committeeMember.getSequenceNo();
            this.title = committeeMember.getTitle() != null ? committeeMember.getTitle().name() : null;
        }
    }

    @Override
    public String getViewType() {
        return "committee-member";
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getTitle() {
        return title;
    }
}
