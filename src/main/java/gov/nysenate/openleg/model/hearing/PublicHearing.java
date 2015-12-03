package gov.nysenate.openleg.model.hearing;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PublicHearing extends BaseLegislativeContent
{

    /** The Public Hearing id. */
    private PublicHearingId id;

    /** The title of the public hearing. */
    private String title;

    /** The date of the public hearing. */
    private LocalDate date;

    /** The location of this Public Hearing. */
    private String address;

    /** The {@link gov.nysenate.openleg.model.hearing.PublicHearingCommittee}
     * holding this PublicHearing. */
    private List<PublicHearingCommittee> committees;

    /** The raw text of the Public Hearing. */
    private String text;

    /** The start time of the public hearing. */
    private LocalTime startTime;

    /** The end time of the public hearing. */
    private LocalTime endTime;

    /** --- Constructors --- */

    public PublicHearing(PublicHearingId publicHearingId, LocalDate date, String text) {
        this.id = publicHearingId;
        this.date = date;
        this.text = text;
        this.year = this.date.getYear();
        this.session = SessionYear.of(this.getYear());
    }

    /** --- Basic Getters/Setters --- */

    public PublicHearingId getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public void setCommittees(List<PublicHearingCommittee> committees) {
        this.committees = committees;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
}
