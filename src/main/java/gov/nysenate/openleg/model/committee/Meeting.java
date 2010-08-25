package gov.nysenate.openleg.model.committee;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Committee;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.util.HideFrom;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("meeting")
public class Meeting  extends SenateObject implements LuceneObject {

	@Persistent
	@Column(name="meeting_date_time")
	@XStreamAsAttribute
	protected Date meetingDateTime;	
	
	@Persistent
	@Column(name="meetday")
	@XStreamAsAttribute
	protected String meetday;
	
	@Persistent
	@Column(name="location")
	@XStreamAsAttribute
	protected String location;	
	
	@Persistent 
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	@XStreamAsAttribute
	protected String id;	
	
	@Persistent
	@Column(name="committee_name")
	@XStreamAsAttribute
	protected String committeeName;
	
	@Persistent
	@Column(name="committee_chair")
	@XStreamAsAttribute
	protected String committeeChair;
	
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@Element(dependent = "false")
	protected List<Bill> bills;
	
	@Persistent(serialized = "false",defaultFetchGroup="true")
	@Element(dependent = "false")
	@Join
	@Order(column="integer_idx")
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected List<Vote> votes;	
	
	@Persistent
	@Column(name="notes", jdbcType="LONGVARCHAR", length=250000)
	protected String notes;

	@Persistent
	@Element(dependent = "false")
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected Committee committee;	

	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="meetings")
	@XmlTransient
	@Element(dependent = "false")
	@Join
	@Order(column="integer_idx")
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	protected List<Addendum> addendums;
	
	/*
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="meeting")
	@Join
	@Order(column="integer_idx")
	@Element(dependent = "false")
	protected List<Attendance> attendees;
	*/
	
	/**
	 * @return the votes
	 */
	public List<Vote> getVotes() {
		return votes;
	}

	/**
	 * @param votes the votes to set
	 */
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	/**
	 * @return the committeeChair
	 */
	@XmlAttribute
	public String getCommitteeChair() {
		return committeeChair;
	}

	/**
	 * @param committeeChair the committeeChair to set
	 */
	public void setCommitteeChair(String committeeChair) {
		this.committeeChair = committeeChair;
	}
	
	/**
	 * @return the id
	 */
	@XmlAttribute
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the committee
	 */
	public Committee getCommittee() {
		return committee;
	}

	/**
	 * @param committee the committee to set
	 */
	public void setCommittee(Committee committee) {
		this.committee = committee;
	}
	

	/**
	 * @return the addendums
	 */
	@XmlTransient
	public List<Addendum> getAddendums() {
		return addendums;
	}

	/**
	 * @param addendums the addendums to set
	 */
	public void setAddendums(List<Addendum> addendums) {
		this.addendums = addendums;
	}

	/**
	 * @return the location
	 */
	@XmlAttribute
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * @return the meetday
	 */
	@XmlAttribute
	public String getMeetday() {
		return meetday;
	}

	/**
	 * @param meetday the meetday to set
	 */
	public void setMeetday(String meetday) {
		this.meetday = meetday;
	}


	/**
	 * @return the meetingDateTime
	 */
	@XmlAttribute
	public Date getMeetingDateTime() {
		return meetingDateTime;
	}

	/**
	 * @param meetingDateTime the meetingDateTime to set
	 */
	public void setMeetingDateTime(Date meetingDateTime) {
		this.meetingDateTime = meetingDateTime;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @param notes the notes to set
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * @return the bills
	 */
	@XmlElementWrapper(name = "bills")
	@XmlElement(name = "bill")
	public List<Bill> getBills() {
		return bills;
	}

	/**
	 * @param bills the bills to set
	 */
	public void setBills(List<Bill> bills) {
		this.bills = bills;
	}

	/**
	 * @return the attendees
	 */
	/*
	@XmlElementWrapper(name = "attendees")
	@XmlElement(name = "attendee")
	public List<Attendance> getAttendees() {
		return attendees;
	}

	/**
	 * @param attendees the attendees to set
	 */
	/*
	public void setAttendees(List<Attendance> attendees) {
		this.attendees = attendees;
	}*/


	/**
	 * @return the committeeName
	 */
	@XmlAttribute
	public String getCommitteeName() {
		return committeeName;
	}

	/**
	 * @param committeeName the committeeName to set
	 */
	public void setCommitteeName(String committeeName) {
		this.committeeName = committeeName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Meeting)
		{
			if ( ((Meeting)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}
	
	
	@Override
	public String luceneOid() { return committeeName+"-"+new SimpleDateFormat("MM-DD-YYYY").format(meetingDateTime); }

	@Override
	public String luceneOsearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String luceneOtype() { return "meeting"; }	
}
/*
<committee>

<name><![CDATA[Finance]]></name>

<chair><![CDATA[Carl Kruger]]></chair>

<location><![CDATA[Room 124 CAP]]></location>

<meetday>Tuesday</meetday>

<meetdate>2009-11-03</meetdate>

<meettime>T11.00.00Z</meettime>

<notes><![CDATA[

]]></notes>

<bills>

<bill no="S00039">

<sponsor><![CDATA[Fuschillo]]></sponsor>

<message><![CDATA[]]></message>

<title><![CDATA[

An act to amend the general business law, in relation to itinerant vendors

]]></title>

</bill>

</bills>

</committee>
*/