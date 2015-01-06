package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.model.entity.Member;
import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.model.hearing.PublicHearingId;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PublicHearingView extends PublicHearingIdView
{
    private String title;
    private LocalDate date;
    private String address;
    private List<PublicHearingCommittee> committees;
    private LocalTime startTime;
    private LocalTime endTime;
    private String text;

    public PublicHearingView(PublicHearing publicHearing) {
        super(new PublicHearingId(publicHearing.getId().getFileName()));
        this.title = publicHearing.getTitle();
        this.date = publicHearing.getDate();
        this.address = publicHearing.getAddress();
        this.committees = publicHearing.getCommittees();
        this.startTime = publicHearing.getStartTime();
        this.endTime = publicHearing.getEndTime();
        this.text = publicHearing.getText();
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public String getAddress() {
        return address;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getViewType() {
        return "hearing";
    }
}
