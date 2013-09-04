package gov.nysenate.openleg.model;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Graylin Kim
 */
public class Calendar extends BaseObject
{
    /**
     * The calendar year for the calendar.
     */
    protected int year;

    /**
     * The type of calendar, active list or floor.
     */
    protected String type;

    /**
     * The session base year for the calendar.
     */
    protected int session;

    /**
     * The number for the calendar in this calendar year.
     */
    protected int no;

    /**
     * The list of supplementals for this calendar.
     */
    protected List<Supplemental> supplementals;

    /**
     * The unique object id.
     */
    protected String oid;

    /**
     * JavaBean Constructor
     */
    public Calendar()
    {
        super();
        supplementals = new ArrayList<Supplemental>();
    }

    /**
     * Fully constructs a calendar object.
     * @param no
     * @param session
     * @param year
     * @param type
     */
    public Calendar(int no, int session, int year, String type)
    {
        this();
        this.setNo(no);
        this.setSession(session);
        this.setYear(year);
        this.setType(type);
        this.setOid("cal-"+type+"-"+no+"-"+year);
    }

    /**
     * The object type of the calendar.
     */
    public String getOtype()
    {
        return "calendar";
    }

    /**
     * @return - This calendar's unique object id.
     */
    public String getOid()
    {
        return oid;
    }

    /**
     *
     * @return
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    /**
     * @return - The calendar number of this object.
     */
    public int getNo()
    {
        return no;
    }

    /**
     * @param no - The new calendar number for this object.
     */
    public void setNo(int no)
    {
        this.no = no;
    }

    /**
     * @return - The calendar year for this object.
     */
    public int getYear()
    {
        return year;
    }

    /**
     * @param year - The new calendar year for this object.
     */
    public void setYear(int year)
    {
        this.year = year;
    }

    /**
     * @return - The current calendar type. One of "active" or "floor".
     */
    public String getType()
    {
        return type;
    }

    /**
     * @param type - The new calendar type. One of "active" or "floor".
     */
    public void setType(String type)
    {
        this.type = type;
    }

    /**
     * @return - The current list of supplementals.
     */
    public List<Supplemental> getSupplementals()
    {
        return supplementals;
    }

    /**
     * @param supplementals - The new list of supplementals.
     */
    public void setSupplementals(List<Supplemental> supplementals)
    {
        this.supplementals = supplementals;
    }

    /**
     * @param supplemental - The supplemental to add to our list of supplementals. Replaces previous versions of this supplemental.
     */
    public void addSupplemental(Supplemental supplemental)
    {
        if(supplementals ==  null) {
            supplementals = new ArrayList<Supplemental>();
        }

        int index = -1;
        if((index = supplementals.indexOf(supplemental)) != -1) {
            supplementals.remove(index);
        }
        supplementals.add(supplemental);
    }

    /**
     * @return - The calendar date as described by the supplementals.
     */
    public Date getDate()
    {
        if (this.getType().equals("active")) {
            if (this.getSupplementals() != null && this.getSupplementals().size() != 0 && this.getSupplementals().get(0).getSequences() != null && this.getSupplementals().get(0).getSequences().size() != 0 && this.getSupplementals().get(0).getSequences().get(0).getActCalDate() != null) {
                return this.getSupplementals().get(0).getSequences().get(0).getActCalDate();
            }
            else {
                return null;
            }
        }
        else {
            if (this.getSupplementals() != null && this.getSupplementals().size() != 0 && this.getSupplementals().get(0).getCalendarDate() != null) {
                return this.getSupplementals().get(0).getCalendarDate();
            }
            else {
                return null;
            }
        }
    }

    /**
     * @return - The calendar title
     */
    public String getTitle()
    {
        return this.getNo()+" - "+this.getType()+" - "+DateFormat.getDateInstance(DateFormat.MEDIUM).format(this.getDate());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Calendar) {
            Calendar other = (Calendar)obj;
            return other.getOid().equals(this.getOid());
        }
        else {
            return false;
        }
    }
}
