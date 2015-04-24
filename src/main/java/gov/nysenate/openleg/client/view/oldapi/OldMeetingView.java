package gov.nysenate.openleg.client.view.oldapi;

import java.io.Serializable;
import java.util.List;

public class OldMeetingView implements Serializable {

    private static final long serialVersionUID = 1037893292641150349L;

    String meetingDateTime;
    String meetday;
    String location;
    String committeeName;
    String committeeChair;
    String notes;

    List<OldBillInfoView> bills;
    List<String> addendums;

    public String getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(String meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public String getMeetday() {
        return meetday;
    }

    public void setMeetday(String meetday) {
        this.meetday = meetday;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCommitteeName() {
        return committeeName;
    }

    public void setCommitteeName(String committeeName) {
        this.committeeName = committeeName;
    }

    public String getCommitteeChair() {
        return committeeChair;
    }

    public void setCommitteeChair(String committeeChair) {
        this.committeeChair = committeeChair;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OldBillInfoView> getBills() {
        return bills;
    }

    public void setBills(List<OldBillInfoView> bills) {
        this.bills = bills;
    }

    public List<String> getAddendums() {
        return addendums;
    }

    public void setAddendums(List<String> addendums) {
        this.addendums = addendums;
    }
}
