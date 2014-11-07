package gov.nysenate.openleg.model.entity;

import com.google.common.collect.Collections2;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.TreeMultiset;
import gov.nysenate.openleg.model.base.BaseLegislativeContent;
import javafx.collections.transformation.SortedList;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Committee extends BaseLegislativeContent implements Serializable
{
    private static final long serialVersionUID = 867918085158335059L;

    /** The name of the committee */
    protected String name;

    /** The legislative chamber containing the committee */
    protected Chamber chamber;

    /** The date that this version of the committee was reformed */
    protected LocalDateTime reformed;

    /** The normal meeting location. */
    protected String location;

    /** The day of the week when this committee normally meets. */
    protected DayOfWeek meetDay;

    /** The time of the day when the committee normally meets. */
    protected LocalTime meetTime;

    /** True if the committee normally meets on alternate weeks. */
    protected boolean meetAltWeek;

    /** A short memo that indicates that the committee will meet on alternate weeks. */
    protected String meetAltWeekText;

    /** List of all the committee members. */
    protected TreeMultiset<CommitteeMember> members = TreeMultiset.create();

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
        this.members = TreeMultiset.create();
        other.members.forEach((member) -> this.members.add(new CommitteeMember(member)));
    }

    public Committee(String name, Chamber chamber){
        this.name = name;
        this.chamber = chamber;
    }

    /** --- Operators --- */

    public boolean membersEquals(Committee other) {
        return
            Objects.equals(this.name, other.name) &&
            Objects.equals(this.session, other.session) &&
            Objects.equals(this.members.size(), other.members.size()) &&
            this.members.containsAll(other.members);
    }

    public boolean meetingEquals(Committee other) {
        return
            Objects.equals(this.name, other.name) &&
            Objects.equals(this.session, other.session) &&
            Objects.equals(this.location, other.location) &&
            Objects.equals(this.meetDay, other.meetDay) &&
            Objects.equals(this.meetTime, other.meetTime) &&
            Objects.equals(this.meetAltWeek, other.meetAltWeek) &&
            Objects.equals(this.meetAltWeekText, other.meetAltWeekText);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final Committee other = (Committee) obj;
        return
            Objects.equals(this.name, other.name) &&
            Objects.equals(this.chamber, other.chamber) &&
            Objects.equals(this.reformed, other.reformed) &&
            meetingEquals(other) &&
            membersEquals(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, chamber, reformed, location, meetDay, meetTime, meetAltWeek, meetAltWeekText, members);
    }

    public static final Comparator<Committee> BY_DATE = (left, right) ->
        ComparisonChain.start()
            .compare(left.session, right.session)
            .compare(left.publishedDateTime, right.publishedDateTime)
            .result();

    /** --- Helper functions --- */

    public boolean isCurrent() {
        // The current Committee should have a reformed date of 'infinity'
        return reformed == null || reformed.isAfter(LocalDateTime.now());
    }

    public void updateMeetingInfo(Committee updatedCommittee) {
        this.location = updatedCommittee.location;
        this.meetDay = updatedCommittee.meetDay;
        this.meetTime = updatedCommittee.meetTime;
        this.meetAltWeek = updatedCommittee.meetAltWeek;
        this.meetAltWeekText = updatedCommittee.meetAltWeekText;
    }

    /** --- Functional Getters/Setters --- */

    public CommitteeId getId(){
        return new CommitteeId(this.chamber, this.name);
    }

    public CommitteeSessionId getSessionId() {
        return new CommitteeSessionId(this.chamber, this.name, this.session);
    }

    public CommitteeVersionId getVersionId() {
        return new CommitteeVersionId(this.chamber, this.name, this.session, this.publishedDateTime);
    }

    /**
     * --- Functional Getters/Setters ---
     */
    public void addMember(CommitteeMember member) {
        this.members.add(member);
    }

    public LocalDateTime getCreated() {
        return this.publishedDateTime;
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

    public LocalDateTime getReformed() {
        return reformed;
    }

    public void setReformed(LocalDateTime reformed) {
        this.reformed = reformed;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DayOfWeek getMeetDay() {
        return meetDay;
    }

    public void setMeetDay(DayOfWeek meetDay) {
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
        return members.stream().collect(Collectors.toList());
    }

    public void setMembers(Collection<CommitteeMember> members) {
        this.members = TreeMultiset.create(members);
    }
}