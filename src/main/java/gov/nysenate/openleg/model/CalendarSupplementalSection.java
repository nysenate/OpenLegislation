package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CalendarSupplementalSection
{
    public static enum SectionType { ORDER_OF_THE_FIRST_REPORT, ORDER_OF_THE_SECOND_REPORT, ORDER_OF_THE_SPECIAL_REPORT, THIRD_READING_FROM_SPECIAL_REPORT, THIRD_READING, STARRED_ON_THIRD_READING }
    public static Map<Integer, SectionType> SECTION_MAP = new TreeMap<Integer, SectionType>();
    static {
        SECTION_MAP.put(150, SectionType.ORDER_OF_THE_FIRST_REPORT);
        SECTION_MAP.put(200, SectionType.ORDER_OF_THE_SECOND_REPORT);
        SECTION_MAP.put(250, SectionType.ORDER_OF_THE_SPECIAL_REPORT);
        SECTION_MAP.put(350, SectionType.THIRD_READING_FROM_SPECIAL_REPORT);
        SECTION_MAP.put(400, SectionType.THIRD_READING);
        SECTION_MAP.put(450, SectionType.STARRED_ON_THIRD_READING);
    };

    private String name;
    private String type;
    private Integer cd;
    private List<CalendarSupplementalSectionEntry> entries;

    public CalendarSupplementalSection()
    {
        this.setEntries(new ArrayList<CalendarSupplementalSectionEntry>());
    }

    public CalendarSupplementalSection(String name, String type, Integer cd)
    {
        this();
        this.setName(name);
        this.setType(type);
        this.setCd(cd);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Integer getCd()
    {
        return cd;
    }

    public void setCd(Integer cd)
    {
        this.cd = cd;
    }

    public List<CalendarSupplementalSectionEntry> getEntries()
    {
        return entries;
    }

    public void setEntries(List<CalendarSupplementalSectionEntry> entries)
    {
        this.entries = entries;
    }

    public void addEntry(CalendarSupplementalSectionEntry entry)
    {
        this.entries.add(entry);
    }
}
