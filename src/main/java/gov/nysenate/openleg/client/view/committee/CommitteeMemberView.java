package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.client.view.entity.MemberView;
import gov.nysenate.openleg.model.entity.CommitteeMember;

public class CommitteeMemberView extends MemberView {

    protected int sequenceNo;
    protected String title;

    public CommitteeMemberView(CommitteeMember committeeMember) {
        super(committeeMember != null ? committeeMember.getMember() : null);
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
