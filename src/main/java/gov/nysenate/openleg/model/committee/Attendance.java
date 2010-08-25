package gov.nysenate.openleg.model.committee;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import gov.nysenate.openleg.model.Person;

import com.thoughtworks.xstream.annotations.*;


@PersistenceCapable
@XmlRootElement
public class Attendance {

	@Persistent 
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	@XStreamAlias("attendance_id")
	private String id;
	
	@Persistent(dependent = "false")
	@XStreamAlias("attendance_member")
	private Person member;
	
	@Persistent
	@Column(name="rank")
	@XStreamAlias("attendance_rank")
	private int rank;
	
	@Persistent
	@Column(name="party")
	@XStreamAlias("attendance_party")
	private String party;
	
	@Persistent
	@Column(name="attendance")
	@XStreamAlias("attendance_attendance")
	private String attendance;
	
	@Persistent
	@Column(name="name")
	@XStreamAlias("attendance_name")
	private String name;
	
	@Persistent
	@Element(dependent = "false")
	@XStreamAlias("attendance_meeting")
	private Meeting meeting;

	/**
	 * @return the meeting
	 */
	@XmlTransient
	public Meeting getMeeting() {
		return meeting;
	}

	/**
	 * @param meeting the meeting to set
	 */
	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	/**
	 * @return the id
	 */
	@XmlTransient
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
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the member
	 */
	public Person getMember() {
		return member;
	}

	/**
	 * @param member the member to set
	 */
	public void setMember(Person member) {
		this.member = member;
	}

	/**
	 * @return the rank
	 */
	@XmlAttribute
	public int getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return the party
	 */
	@XmlAttribute
	public String getParty() {
		return party;
	}

	/**
	 * @param party the party to set
	 */
	public void setParty(String party) {
		this.party = party;
	}

	/**
	 * @return the attendance
	 */
	@XmlAttribute
	public String getAttendance() {
		return attendance;
	}

	/**
	 * @param attendance the attendance to set
	 */
	public void setAttendance(String attendance) {
		this.attendance = attendance;
	}

	
}
