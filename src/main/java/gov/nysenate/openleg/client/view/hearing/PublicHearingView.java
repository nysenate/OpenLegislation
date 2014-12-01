package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.model.hearing.PublicHearingId;

import java.util.List;

public class PublicHearingView extends PublicHearingIdView
{
    private String address;
    private List<PublicHearingCommittee> committees;
    private List<Member> attendance;
    private String text;

    public PublicHearingView(PublicHearing publicHearing) {
        super(new PublicHearingId(publicHearing.getTitle(), publicHearing.getDateTime()));
        this.address = publicHearing.getAddress();
        this.committees = publicHearing.getCommittees();
        this.attendance = publicHearing.getAttendance();
        this.text = publicHearing.getText();
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public String getAddress() {
        return address;
    }

    public List<Member> getAttendance() {
        return attendance;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "hearing";
    }
}
