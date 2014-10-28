package gov.nysenate.openleg.client.view.committee;

import gov.nysenate.openleg.client.view.base.ListView;
import gov.nysenate.openleg.model.entity.Committee;

import java.util.stream.Collectors;

public class CommitteeView extends CommitteeVersionIdView{

    protected String reformed;
    protected String location;
    protected String meetDay;
    protected String meetTime;
    protected boolean meetAltWeek;
    protected String meetAltWeekText;
    protected ListView<CommitteeMemberView> committeeMembers;

    public CommitteeView(Committee committee) {
        super(committee != null ? committee.getVersionId() : null);
        if (committee != null) {
            this.reformed = committee.getReformed() != null ? committee.getReformed().toString() : null;
            this.location = committee.getLocation();
            this.meetDay = committee.getMeetDay() != null ? committee.getMeetDay().toString() : null;
            this.meetTime = committee.getMeetTime() != null ? committee.getMeetTime().toString() : null;
            this.meetAltWeek = committee.isMeetAltWeek();
            this.meetAltWeekText = committee.getMeetAltWeekText();
            this.committeeMembers = ListView.of(committee.getMembers().stream()
                    .map(CommitteeMemberView::new)
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public String getViewType() {
        return "committee";
    }

    public String getReformed() {
        return reformed;
    }

    public String getLocation() {
        return location;
    }

    public String getMeetDay() {
        return meetDay;
    }

    public String getMeetTime() {
        return meetTime;
    }

    public boolean isMeetAltWeek() {
        return meetAltWeek;
    }

    public String getMeetAltWeekText() {
        return meetAltWeekText;
    }

    public ListView<CommitteeMemberView> getCommitteeMembers() {
        return committeeMembers;
    }
}
