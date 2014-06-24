package gov.nysenate.openleg.model.entity;

import org.joda.time.LocalTime;

import java.util.List;

public class Committee
{
    /** The name of the committee */
    protected String name;

    /** The normal meeting location. */
    protected String location;

    /** The day of the week when this committee normally meets. */
    protected String meetDay;

    /** The time of the day when the committee normally meets. */
    protected LocalTime meetTime;

    /** True if the committee normally meets on alternate weeks. */
    protected boolean meetAltWeek;

    /** A short memo that indicates that the committee will meet on alternate weeks. */
    protected String meetAltWeekText;

    /** List of all the committee members. */
    protected List<CommitteeMember> members;

    /** --- Constructors --- */

    public Committee() {}

    /** --- Basic Getters/Setters --- */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalTime getMeetTime() {
        return meetTime;
    }

    public void setMeetTime(LocalTime meetTime) {
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
