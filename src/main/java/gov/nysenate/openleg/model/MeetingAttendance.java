package gov.nysenate.openleg.model;

public class MeetingAttendance
{
    String name;
    String rank;
    String party;
    String attendance;

    public MeetingAttendance()
    {

    }

    public MeetingAttendance(String name, String rank, String party, String attendance)
    {
        super();
        this.name = name;
        this.rank = rank;
        this.party = party;
        this.attendance = attendance;
    }
}
