package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SenagendaVoteCommittee
{
    public String name;
    public String chair;
    public Date meetDate;
    public Date modifiedDate;
    public Map<String, MeetingItem> items;
    public List<MeetingAttendance> attendance = new ArrayList<MeetingAttendance>();

    public SenagendaVoteCommittee()
    {
        this.items = new HashMap<String, MeetingItem>();
    }

    public SenagendaVoteCommittee(String name, String chair, Date meetDate)
    {
        this.name = name;
        this.chair = chair;
        this.meetDate = meetDate;
    }
}
