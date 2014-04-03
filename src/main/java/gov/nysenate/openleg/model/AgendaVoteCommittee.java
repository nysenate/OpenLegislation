package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendaVoteCommittee
{
    private String name;
    private String chair;
    private Date meetDate;
    private Date modifiedDate;
    private Map<String, AgendaVoteCommitteeItem> items;
    private List<AgendaVoteCommitteeAttendance> attendance = new ArrayList<AgendaVoteCommitteeAttendance>();

    public AgendaVoteCommittee()
    {
        this.setItems(new HashMap<String, AgendaVoteCommitteeItem>());
    }

    public AgendaVoteCommittee(String name, String chair, Date meetDate)
    {
        this.setName(name);
        this.setChair(chair);
        this.setMeetDate(meetDate);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getChair()
    {
        return chair;
    }

    public void setChair(String chair)
    {
        this.chair = chair;
    }

    public Date getMeetDate()
    {
        return meetDate;
    }

    public void setMeetDate(Date meetDate)
    {
        this.meetDate = meetDate;
    }

    public Date getModifiedDate()
    {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate)
    {
        this.modifiedDate = modifiedDate;
    }

    public Map<String, AgendaVoteCommitteeItem> getItems()
    {
        return items;
    }

    public void setItems(Map<String, AgendaVoteCommitteeItem> items)
    {
        this.items = items;
    }

    public void putItem(AgendaVoteCommitteeItem item)
    {
        this.items.put(item.getBill().getBillId()+item.getBillAmendment(), item);
    }

    public AgendaVoteCommitteeItem getItem(String printNumber)
    {
        return this.items.get(printNumber);
    }

    public void removeItem(String printNumber)
    {
        this.items.remove(printNumber);
    }

    public List<AgendaVoteCommitteeAttendance> getAttendance()
    {
        return attendance;
    }

    public void setAttendance(List<AgendaVoteCommitteeAttendance> attendance)
    {
        this.attendance = attendance;
    }

    public void addAttendance(AgendaVoteCommitteeAttendance attendance)
    {
        this.attendance.add(attendance);
    }
}
