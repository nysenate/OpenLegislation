package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a weekly senate meetings agenda.
 *
 * @author GraylinKim
 */
public class Agenda extends BaseObject
{
    /**
     * The agenda's unique object id.
     */
    private String oid;

    /**
     * The agenda's calendar number. Starts at 1 at the beginning of each calendar year.
     */
    private int number;

    /**
     * The list of addendum to the agenda.
     */
    private List<Addendum> addendums;

    /**
     * JavaBean constructor
     */
    public Agenda()
    {
        super();
        addendums = new ArrayList<Addendum>();
    }

    /**
     * Fully constructs a new agenda.
     *
     * @param session - The session year for the agenda
     * @param year - The calendar year for the agenda
     * @param number - The agenda number for the calendar year
     */
    public Agenda(int session, int year, int number)
    {
        this();
        this.setSession(session);
        this.setYear(year);
        this.setNumber(number);
        this.setOid("commagenda-"+number+"-"+year);
    }

    /**
     * The object type of the agenda.
     */
    public String getOtype()
    {
        return "agenda";
    }

    /**
     * @return - This object's unique object id.
     */
    public String getOid()
    {
        return this.oid;
    }

    /**
     * @param oid - The new object id.
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    /**
     * @return - The agenda number.
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @param number - The new agenda number.
     */
    public void setNumber(int number)
    {
        this.number = number;
    }

    /**
     * @return - The list of addendum.
     */
    public List<Addendum> getAddendums()
    {
        return addendums;
    }

    /**
     * @param addendums - The new list of addendum.
     */
    public void setAddendums(List<Addendum> addendums)
    {
        this.addendums = addendums;
    }

    /**
     * @param meeting - The meeting to remove from the meetings list.
     */
    public void removeCommitteeMeeting(Meeting meeting)
    {
        for(Addendum addendum:this.getAddendums()) {
            addendum.removeMeeting(meeting);
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof Agenda) {
            Agenda other = (Agenda)obj;
            return this.getOid().equals(other.getOid());
        }
        else {
            return false;
        }
    }
}
