package gov.nysenate.openleg.abstractmodel;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Committee;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public abstract class AbstractMeeting {
	
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

}
