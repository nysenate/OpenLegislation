package gov.nysenate.openleg.model;


import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgendaInfoCommittee
{
    private String name;
    private String chair;
    private String location;
    private String meetDay;
    private Date meetDate;
    private String notes;
    private Map<String, AgendaInfoCommitteeItem> items;

    public AgendaInfoCommittee()
    {
        this.setItems(new HashMap<String, AgendaInfoCommitteeItem>());
    }

    public AgendaInfoCommittee(String name, String chair, String location, String notes, String meetDay, Date meetDate)
    {
        this();
        this.setName(name);
        this.setChair(chair);
        this.setLocation(location);
        this.setNotes(notes);
        this.setMeetDay(meetDay);
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

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public Date getMeetDate()
    {
        return meetDate;
    }

    public void setMeetDate(Date meetDate)
    {
        this.meetDate = meetDate;
    }

    public String getMeetDay()
    {
        return meetDay;
    }

    public void setMeetDay(String meetDay)
    {
        this.meetDay = meetDay;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Map<String, AgendaInfoCommitteeItem> getItems()
    {
        return items;
    }

    public void setItems(Map<String, AgendaInfoCommitteeItem> items)
    {
        this.items = items;
    }

    public void putItem(AgendaInfoCommitteeItem item)
    {
        this.items.put(item.getBill().getBillId()+item.getBillAmendment(), item);
    }

    public AgendaInfoCommitteeItem getItem(String printNumber)
    {
        return this.items.get(printNumber);
    }

    public void removeItem(String printNumber)
    {
        this.items.remove(printNumber);
    }
}

