package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.LinkedHashMap;

public class CalendarSupplemental extends BaseObject
{
    private String id;
    private Date calDate;
    private Date releaseDateTime;
    private LinkedHashMap<Integer, CalendarSection> sections;

    public CalendarSupplemental(String id, Date calDate, Date releaseDateTime)
    {
        this.setId(id);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
        this.setSections(new LinkedHashMap<Integer, CalendarSection>());
    }

    @Override
    public String getOid()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getOtype()
    {
        return "calendar-supplemental";
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
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

    public LinkedHashMap<Integer, CalendarSection> getSections()
    {
        return sections;
    }

    public void setSections(LinkedHashMap<Integer, CalendarSection> sections)
    {
        this.sections = sections;
    }

    public CalendarSection getSection(Integer cd)
    {
        return this.sections.get(cd);
    }

    public void putSection(CalendarSection section)
    {
        this.sections.put(section.getCd(), section);
    }
}
