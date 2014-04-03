package gov.nysenate.openleg.model;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TreeMap;

public class Calendar extends BaseObject
{
    private Integer number;
    private Integer session;
    private Integer year;
    private TreeMap<String, CalendarSupplemental> supplementals;
    private LinkedHashMap<Integer, CalendarActiveList> activeLists;

    public Calendar()
    {
        this.setSupplementals(new TreeMap<String, CalendarSupplemental>());
        this.setActiveLists(new LinkedHashMap<Integer, CalendarActiveList>());
    }

    public Calendar(Integer number, Integer session, Integer year)
    {
        super();
        this.setNumber(number);
        this.setSession(session);
        this.setYear(year);
    }

    @Override
    public String getOid()
    {
        return "senagenda-"+this.session+"-"+this.year;
    }

    @Override
    public String getOtype()
    {
        // TODO Auto-generated method stub
        return "sencalendar";
    }

    public Integer getNumber()
    {
        return number;
    }

    public void setNumber(Integer number)
    {
        this.number = number;
    }

    public LinkedHashMap<Integer, CalendarActiveList> getActiveLists()
    {
        return activeLists;
    }

    public void setActiveLists(LinkedHashMap<Integer, CalendarActiveList> activeLists)
    {
        this.activeLists = activeLists;
    }

    public CalendarActiveList getActiveList(String id)
    {
        return this.activeLists.get(id);
    }

    public void putActiveList(CalendarActiveList activeList)
    {
        this.activeLists.put(activeList.getId(), activeList);
    }

    public void removeActiveList(Integer id)
    {
        this.activeLists.remove(id);
    }

    public TreeMap<String, CalendarSupplemental> getSupplementals()
    {
        return supplementals;
    }

    public void setSupplementals(TreeMap<String, CalendarSupplemental> supplementals)
    {
        this.supplementals = supplementals;
    }

    public CalendarSupplemental getSupplemental(String id)
    {
        return this.supplementals.get(id);
    }

    public void putSupplemental(CalendarSupplemental supplemental)
    {
        this.supplementals.put(supplemental.getId(), supplemental);
    }

    public void removeSupplemental(String id)
    {
        this.supplementals.remove(id);
    }

    public Date getCalDate()
    {
        if (this.supplementals.size() > 0) {
            return this.supplementals.values().iterator().next().getCalDate();
        }
        else if (this.activeLists.size() > 0) {
            return this.activeLists.values().iterator().next().getCalDate();
        }
        else {
            return null;
        }
    }
}
