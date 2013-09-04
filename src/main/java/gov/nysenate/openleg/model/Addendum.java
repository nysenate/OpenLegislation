package gov.nysenate.openleg.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 *
 * @author GraylinKim
 */
public class Addendum extends BaseObject
{
    /**
     * The object's unique id.
     */
    private String oid;

    /**
     * The addendum id. A letter from "" to Z
     */
    private String addendumId;

    /**
     * The Monday of the week of committee meetings that this agenda covers.
     */
    private String weekOf;

    /**
     * A list of meetings detailed by this addendum
     */
    private List<Meeting> meetings;

    /**
     * The agenda that this addendum is on.
     */
    private Agenda agenda = null;

    /**
     * JavaBean constructor.
     */
    public Addendum()
    {
        super();
        meetings = new ArrayList<Meeting>();
    }

    public Addendum(String addendumId, String weekOf, Date publishDate, int agendaNo, int year)
    {
        this.setAddendumId(addendumId);
        this.setWeekOf(weekOf);
        this.setYear(year);
        this.setSession(year % 2 == 0 ? year-1 : year);
        this.setPublishDate(publishDate);
        this.setModifiedDate(publishDate);
        this.setAgenda(agenda);
        this.setOid(agendaNo+this.addendumId+"-"+this.getYear());
        meetings = new ArrayList<Meeting>();
    }

    /**
     * The object type of the addendum.
     */
    public String getOtype()
    {
        return "addendum";
    }

    /**
     * @return - The object's unique id.
     */
    public String getOid()
    {
        return this.oid;
    }

    /**
     * @param oid - The object's new object id.
     */
    public void setOid(String oid)
    {
        this.oid = oid;
    }

    /**
     * @return - The agenda this addendum is for.
     */
    @JsonIgnore
    public Agenda getAgenda()
    {
        return agenda;
    }

    /**
     * @param agenda - The new target agenda for this addendum.
     */
    public void setAgenda(Agenda agenda)
    {
        this.agenda = agenda;
    }

    /**
     * @return - The addendum id. Letters from "" to Z
     */
    public String getAddendumId()
    {
        return addendumId;
    }

    /**
     * @param addendumId - The new addendum id.
     */
    public void setAddendumId(String addendumId)
    {
        this.addendumId = addendumId;
    }

    /**
     * @return - The Monday of the week this agenda covers.
     */
    public String getWeekOf()
    {
        return weekOf;
    }

    /**
     * @param weekOf - The new Monday of the week this agenda covers.
     */
    public void setWeekOf(String weekOf)
    {
        this.weekOf = weekOf;
    }

    /**
     * @return - The list of meetings detailed by this addendum.
     */
    public List<Meeting> getMeetings()
    {
        return meetings;
    }

    /**
     * @param meetings - The new list of meetings for this addendum
     */
    public void setMeetings(List<Meeting> meetings)
    {
        this.meetings = meetings;
    }

    /**
     * @param meeting - The new meeting to add to the addendum.
     */
    public void addMeeting(Meeting meeting)
    {
        this.meetings.add(meeting);
    }

    /**
     * @param meeting - The meeting to remove from the addendum.
     */
    public void removeMeeting(Meeting meeting)
    {
        this.meetings.remove(meeting);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Addendum) {
            Addendum other = (Addendum)obj;
            return this.getOid().equals(other.getOid());
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return this.getOid() + "-" + this.getPublishDate() + "-" + this.getMeetings();
    }
}
