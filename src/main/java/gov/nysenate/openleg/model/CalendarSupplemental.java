package gov.nysenate.openleg.model;

import gov.nysenate.openleg.util.DateHelper;

import java.util.Date;
import java.util.LinkedHashMap;

public class CalendarSupplemental extends BaseObject
{
    private String id;
    private Date calDate;
    private Date releaseDateTime;
    private LinkedHashMap<Integer, CalendarSupplementalSection> sections;

    public CalendarSupplemental()
    {
        super();
        this.setSections(new LinkedHashMap<Integer, CalendarSupplementalSection>());
    }

    public CalendarSupplemental(String id, Date calDate, Date releaseDateTime)
    {
        this();
        this.setId(id);
        this.setCalDate(calDate);
        this.setReleaseDateTime(releaseDateTime);
    }

    @Override
    public String getOid()
    {
        return this.getOtype()+"-"+DateHelper.LRS_DATE_ONLY_FORMAT.format(this.getCalDate())+"-"+this.getId();
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

    public LinkedHashMap<Integer, CalendarSupplementalSection> getSections()
    {
        return sections;
    }

    public void setSections(LinkedHashMap<Integer, CalendarSupplementalSection> sections)
    {
        this.sections = sections;
    }

    public CalendarSupplementalSection getSection(Integer cd)
    {
        return this.sections.get(cd);
    }

    public void putSection(CalendarSupplementalSection section)
    {
        this.sections.put(section.getCd(), section);
    }
}
