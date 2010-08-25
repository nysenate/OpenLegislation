package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.HideFrom;

import java.util.Date;
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
import javax.xml.bind.annotation.XmlRootElement;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
public class Sequence  extends SenateObject {

	@Persistent
	@Column(name="no")
	@XStreamAsAttribute
	private String no;
	
	@Persistent
	@PrimaryKey
	@Column(name="id")	
	@XStreamAsAttribute
	private String id;
	
	@Persistent
	@Column(name="act_cal_date")
	private Date actCalDate;
	
	@Persistent
	@Column(name="release_date_time")
	private Date releaseDateTime;	
	
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="sequence")
	@Join
	@Element(dependent = "true")  
	@Order(column="integer_idx")
	private List<CalendarEntry> calendarEntries;	
	
	@Persistent
	@Column(name="FULLTEXT", jdbcType="LONGVARCHAR", length=250000)
	@HideFrom({Calendar.class, Supplemental.class})
	private String notes;
	
	@Persistent(serialized = "false",dependent = "false")
	@HideFrom({Calendar.class, Supplemental.class})
	private Supplemental supplemental;

	/**
	 * @return the supplemental
	 */
	public Supplemental getSupplemental() {
		return supplemental;
	}

	/**
	 * @param supplemental the supplemental to set
	 */
	public void setSupplemental(Supplemental supplemental) {
		this.supplemental = supplemental;
	}

	/**
	 * @return the actCalDate
	 */
	public Date getActCalDate() {
		return actCalDate;
	}

	/**
	 * @param actCalDate the actCalDate to set
	 */
	public void setActCalDate(Date actCalDate) {
		this.actCalDate = actCalDate;
	}

	/**
	 * @return the releaseDateTime
	 */
	public Date getReleaseDateTime() {
		return releaseDateTime;
	}

	/**
	 * @param releaseDateTime the releaseDateTime to set
	 */
	public void setReleaseDateTime(Date releaseDateTime) {
		this.releaseDateTime = releaseDateTime;
	}

	
	/**
	 * @return the calendarEntries
	 */
	public List<CalendarEntry> getCalendarEntries() {
		return calendarEntries;
	}

	/**
	 * @param calendarEntries the calendarEntries to set
	 */
	public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
		this.calendarEntries = calendarEntries;
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
	 * @return the no
	 */
	public String getNo() {
		return no;
	}

	/**
	 * @param no the no to set
	 */
	public void setNo(String no) {
		this.no = no;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Sequence)
		{
			if ( ((Sequence)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}


}


/*
<sequence no="">
<actcaldate>2009-11-07</actcaldate>
<releasedate>2009-11-10</releasedate>
<releasetime>T11.27.19Z</releasetime>
<notes></notes>
<calnos>
<calno no="12">
<bill no="S01028" />
</calno>
</calnos>
</sequence>
*/