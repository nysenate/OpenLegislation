package gov.nysenate.openleg.model.hearing;

import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public class PublicHearing extends BaseLegislativeContent
{

    /** The Public Hearing id. */
    private PublicHearingId id;

    /** The location of this Public Hearing. */
    private String address;

    /** The {@link gov.nysenate.openleg.model.hearing.PublicHearingCommittee}
     * holding this PublicHearing. */
    private List<PublicHearingCommittee> committees;

    /** List of legislators who attended the PublicHearing. */
    private List<Member> attendance;

    /** The raw text of the Public Hearing. */
    private String text;

    /** --- Constructors --- */

    public PublicHearing(PublicHearingId publicHearingId, String address, String text) {
        this.id = publicHearingId;
        this.year = publicHearingId.getYear();
        this.session = SessionYear.of(this.getYear());
        this.address = address;
        this.text = text;
    }

    /** --- Basic Getters/Setters --- */

    public PublicHearingId getId() {
        return id;
    }

    public String getTitle() {
        return id.getTitle();
    }

    public LocalDateTime getDateTime() {
        return id.getDateTime();
    }

    public String getAddress() {
        return address;
    }

    public String getText() {
        return text;
    }

    public List<PublicHearingCommittee> getCommittees() {
        return committees;
    }

    public void setCommittees(List<PublicHearingCommittee> committees) {
        this.committees = committees;
    }

    public List<Member> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<Member> attendance) {
        this.attendance = attendance;
    }

    public void addAttendant(Member member) {
        attendance.add(member);
    }
}
