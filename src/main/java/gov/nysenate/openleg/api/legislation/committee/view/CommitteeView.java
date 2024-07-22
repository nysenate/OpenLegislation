package gov.nysenate.openleg.api.legislation.committee.view;

import gov.nysenate.openleg.api.ListView;
import gov.nysenate.openleg.legislation.committee.Committee;

import java.time.format.DateTimeFormatter;

public class CommitteeView extends CommitteeVersionIdView {
    /** Time format to match our Elasticsearch mappings.*/
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
            this.meetTime = committee.getMeetTime() != null ? committee.getMeetTime().format(TIME_FORMAT) : null;
            this.meetAltWeek = committee.isMeetAltWeek();
            this.meetAltWeekText = committee.getMeetAltWeekText();
            this.committeeMembers = ListView.of(committee.getMembers().stream()
                    .map(CommitteeMemberView::new)
                    .toList());
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
