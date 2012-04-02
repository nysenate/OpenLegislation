package gov.nysenate.openleg.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Attendance {

    @XStreamAlias("attendance_id")
    private String id;

    @XStreamAlias("attendance_member")
    private Person member;

    @XStreamAlias("attendance_rank")
    private int rank;

    @XStreamAlias("attendance_party")
    private String party;

    @XStreamAlias("attendance_attendance")
    private String attendance;

    @XStreamAlias("attendance_name")
    private String name;

    @XStreamAlias("attendance_meeting")
    private Meeting meeting;

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Person getMember() {
        return member;
    }

    public void setMember(Person member) {
        this.member = member;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }
}
