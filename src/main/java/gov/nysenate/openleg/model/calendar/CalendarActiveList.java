package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.BaseLegContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActiveList extends BaseLegContent
{
    private Integer id;
    private String notes;
    private Date calDate;
    private Date releaseDateTime;
    private List<CalendarActiveListEntry> entries;

    public CalendarActiveList()
    {
        super();
        this.setEntries(new ArrayList<CalendarActiveListEntry>());
    }

    public CalendarActiveList(Integer id, String notes, Date calDate, Date releaseDateTime)
    {
        this();
        this.setId(id);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public List<CalendarActiveListEntry> getEntries()
    {
        return entries;
    }

    public void setEntries(List<CalendarActiveListEntry> entries)
    {
        this.entries = entries;
    }

    public void addEntry(CalendarActiveListEntry entry)
    {
        this.entries.add(entry);
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public Date getCalDate()
    {
        return calDate;
    }

    public void setCalDate(Date calDate)
    {
        this.calDate = calDate;
    }

    public Date getReleaseDateTime()
    {
        return releaseDateTime;
    }

    public void setReleaseDateTime(Date releaseDateTime)
    {
        this.releaseDateTime = releaseDateTime;
    }
}
