package gov.nysenate.openleg.client.view.hearing;

import gov.nysenate.openleg.model.hearing.PublicHearing;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PublicHearingInfoView extends PublicHearingIdView
{
    private String title;
    protected LocalDate date;
    protected List<PublicHearingCommittee> committees;
    private String address;
    private LocalTime startTime;
    private LocalTime endTime;

    public PublicHearingInfoView(PublicHearing publicHearing) {
        super(publicHearing.getId());
        this.title = publicHearing.getTitle();
        this.date = publicHearing.getDate();
        this.committees = publicHearing.getCommittees();
        this.address = publicHearing.getAddress();
        this.startTime = publicHearing.getStartTime();
        this.endTime = publicHearing.getEndTime();
    }

    @Override
    public String getViewType() {
        return "hearing-info";
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public String getAddress() {
        return address;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
