package gov.nysenate.openleg.model.entity;

import gov.nysenate.openleg.model.BaseLegislativeContent;

import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class Committee extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = 867918085158335059L;

    /** The name of the committee */
    protected String name;

    /** The legislative chamber containing the committee */
    protected Chamber chamber;

    /** The date that this version of the committee was reformed */
    protected Date reformed;

    /** The normal meeting location. */
    protected String location;

    /** The day of the week when this committee normally meets. */
    protected String meetDay;

    /** The time of the day when the committee normally meets. */
    protected Time meetTime;

    /** True if the committee normally meets on alternate weeks. */
    protected boolean meetAltWeek;

    /** A short memo that indicates that the committee will meet on alternate weeks. */
    protected String meetAltWeekText;

    /** List of all the committee members. */
    protected List<CommitteeMember> members;

    /** --- Operators --- */

    public boolean memberEquals(Committee that){
        if(!this.name.equals(that.name)) return false;
        if(this.session!=that.session) return false;
        if(this.members.size()!=that.members.size()) return false;
        if(!this.members.containsAll(that.members)) return false;
        return true;
    }

    public boolean meetingEquals(Committee that){
        if(!this.name.equals(that.name)) return false;
        if(this.session!=that.session) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (meetDay != null ? !meetDay.equals(that.meetDay) : that.meetDay != null) return false;
        if (meetTime != null ? !meetTime.toString().equals(that.meetTime.toString()) : that.meetTime != null) return false;
        if(this.meetAltWeek!=that.meetAltWeek) return false;
        if (meetAltWeekText != null ? !meetAltWeekText.equals(that.meetAltWeekText) : that.meetAltWeekText != null)
            return false;
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Committee that = (Committee) o;

        if(this.publishDate!=null ? this.publishDate.getTime()!=that.publishDate.getTime() : that.publishDate!=null) return false;
        if(this.session!=that.session) return false;
        if (meetAltWeek != that.meetAltWeek) return false;
        if (chamber != that.chamber) return false;
        if (location != null ? !location.equals(that.location) : that.location != null) return false;
        if (meetAltWeekText != null ? !meetAltWeekText.equals(that.meetAltWeekText) : that.meetAltWeekText != null)
            return false;
        if (meetDay != null ? !meetDay.equals(that.meetDay) : that.meetDay != null) return false;
        if (meetTime != null ? !meetTime.toString().equals(that.meetTime.toString()) : that.meetTime != null) return false;
        if(this.members.size()!=that.members.size()) return false;
        if(!this.members.containsAll(that.members)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + chamber.hashCode();
        result = 31 * result + (reformed != null ? reformed.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (meetDay != null ? meetDay.hashCode() : 0);
        result = 31 * result + (meetTime != null ? meetTime.hashCode() : 0);
        result = 31 * result + (meetAltWeek ? 1 : 0);
        result = 31 * result + (meetAltWeekText != null ? meetAltWeekText.hashCode() : 0);
        result = 31 * result + members.hashCode();
        return result;
    }

    public static final Comparator<Committee> BY_DATE =
        new Comparator<Committee>() {
            @Override
            public int compare(Committee left, Committee right) {
                if(left.session<right.session) return -1;
                else if(left.session>right.session) return 1;
                else {
                    if (left.publishDate.getTime()==right.publishDate.getTime()) return 0;
                    else if (left.publishDate.getTime()>=right.publishDate.getTime()) return 1;
                    else return -1;
                }
            }
        };

    /** --- Constructors --- */

    public Committee() {
        super();
    }

    public Committee(Committee other) {
        super(other);
        this.name = other.name;
        this.chamber = other.chamber;
        this.reformed = other.reformed;
        this.location = other.location;
        this.meetDay = other.meetDay;
        this.meetTime = other.meetTime;
        this.meetAltWeek = other.meetAltWeek;
        this.meetAltWeekText = other.meetAltWeekText;
        this.members = new ArrayList<CommitteeMember>();
        for(CommitteeMember cm : other.members){
            members.add(new CommitteeMember(cm));
        }
    }

    public Committee(String name, Chamber chamber){
        this.name = name;
        this.chamber = chamber;
    }

    /** --- Helper functions --- */

    public boolean isCurrent(){
        // The current Committee should have a reformed date of 'infinity'
        return reformed.after(new Date());
    }

    public void updateMeetingInfo(Committee updatedCommittee){
        this.location = updatedCommittee.location;
        this.meetDay = updatedCommittee.meetDay;
        this.meetTime = updatedCommittee.meetTime;
        this.meetAltWeek = updatedCommittee.meetAltWeek;
        this.meetAltWeekText = updatedCommittee.meetAltWeekText;
    }

    /** --- Basic Getters/Setters --- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public Date getReformed() {
        return reformed;
    }

    public void setReformed(Date reformed) {
        this.reformed = reformed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetDay() {
        return meetDay;
    }

    public void setMeetDay(String meetDay) {
        this.meetDay = meetDay;
    }

    public Time getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(Time meetTime) {
        this.meetTime = meetTime;
    }

    public boolean isMeetAltWeek() {
        return meetAltWeek;
    }

    public void setMeetAltWeek(boolean meetAltWeek) {
        this.meetAltWeek = meetAltWeek;
    }

    public String getMeetAltWeekText() {
        return meetAltWeekText;
    }

    public void setMeetAltWeekText(String meetAltWeekText) {
        this.meetAltWeekText = meetAltWeekText;
    }

    public List<CommitteeMember> getMembers() {
        return members;
    }

    public void setMembers(List<CommitteeMember> members) {
        this.members = members;
    }
}
