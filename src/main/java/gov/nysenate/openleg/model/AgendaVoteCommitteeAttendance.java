package gov.nysenate.openleg.model;

public class AgendaVoteCommitteeAttendance
{
    String name;
    String rank;
    String party;
    String attendance;

    public AgendaVoteCommitteeAttendance()
    {

    }

    public AgendaVoteCommitteeAttendance(String name, String rank, String party, String attendance)
    {
        super();
        this.name = name;
        this.rank = rank;
        this.party = party;
        this.attendance = attendance;
    }
}
