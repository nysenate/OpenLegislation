package gov.nysenate.openleg.model;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("cosponsor")
public class Person extends SenateObject
{
	
	@Persistent 
	@Column(name="position")
	@XStreamAsAttribute
	private String position;
	
	@Persistent
	@Column(name="fullname")
	@XStreamAsAttribute
	private String fullname;
	
	@Persistent
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR")
	private String id;
	
	@Persistent
	@Column(name="branch")
	@HideFrom({Bill.class, Meeting.class, Calendar.class, Supplemental.class})
	private String branch;
	
	@Persistent
	@Column(name="contact_info")
	@HideFrom({Bill.class, Meeting.class, Calendar.class, Supplemental.class})
	private String contactInfo;
	
	@Persistent
	@Column(name="guid")
	@HideFrom({Bill.class, Meeting.class, Calendar.class, Supplemental.class})
	private String guid;
	
	/**
	 * @return the id
	 */
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
	 * @return the branch
	 */
	@XmlAttribute
	public String getBranch() {
		return branch;
	}

	/**
	 * @param branch the branch to set
	 */
	public void setBranch(String branch) {
		this.branch = branch;
	}

	/**
	 * @return the contactInfo
	 */
	@XmlAttribute
	public String getContactInfo() {
		return contactInfo;
	}

	/**
	 * @param contactInfo the contactInfo to set
	 */
	public void setContactInfo(String contactInfo) {
		this.contactInfo = contactInfo;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		this.guid = guid;
	}

	public Person ()
	{
		
	}
	
	public Person (String fullname)
	{
		this.fullname = fullname;
		
		this.id = fullname;
	}
	
	public Person (String fullname, String position)
	{
		this.fullname = fullname;
		this.position = position;
		
		this.id = fullname + '-' + position;
	}

	/**
	 * @return the position
	 */
	@XmlAttribute
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	

	/**
	 * @return the fullname
	 */
	@XmlAttribute
	public String getFullname() {
		return fullname;
	}

	/**
	 * @param fullname the fullname to set
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}


	
}
