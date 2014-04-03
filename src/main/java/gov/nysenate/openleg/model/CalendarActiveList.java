package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarActiveList extends BaseObject
{
    private Integer id;
    private String notes;
    private Date calDate;
    private Date releaseDateTime;
    private List<CalendarActiveListEntry> entries;

    public CalendarActiveList()
    {
        this.setEntries(new ArrayList<CalendarActiveListEntry>());
    }

    public CalendarActiveList(Integer id, String notes, Date calDate, Date releaseDateTime)
    {
        super();
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

    @Override
    public String getOid()
    {
        return this.getOtype()+"-"+DateHelper.LRS_DATE_ONLY_FORMAT.format(this.getCalDate())+"-"+this.getId();
    }

    @Override
    public String getOtype()
    {
        return "calendar-active";
    }
}
