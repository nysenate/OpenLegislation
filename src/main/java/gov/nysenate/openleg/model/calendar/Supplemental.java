package gov.nysenate.openleg.model.calendar;

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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.HideFrom;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("supplemental")
public class Supplemental  extends SenateObject implements LuceneObject {
	
	@Persistent
	@PrimaryKey
	@XStreamAsAttribute
	protected String id;
	
	@Persistent
	@Column(name="calendar_date")
	protected Date calendarDate;
	
	@Persistent
	@Column(name="release_date_time")
	protected Date releaseDateTime;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="supplemental")
	@Join
	@Element(dependent = "true") 
	@Order(column="integer_idx")
	protected List<Section> sections;
	
	@Persistent(serialized = "false",dependent = "true",defaultFetchGroup="true",mappedBy="supplemental")
	protected Sequence sequence;
	
	@Persistent
	@Column(name="supplemental_id")	
	@HideFrom({Calendar.class, Supplemental.class})
	protected String supplementalId;
	
	@Persistent
	@XmlTransient
	@Element(dependent = "false")  
	@HideFrom({Calendar.class, Supplemental.class})
	protected Calendar calendar;
	
	/**
	 * @return the sequence
	 */
	public Sequence getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(Sequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * @return the calendarDate
	 */
	public Date getCalendarDate() {
		return calendarDate;
	}

	/**
	 * @param calendarDate the calendarDate to set
	 */
	public void setCalendarDate(Date calendarDate) {
		this.calendarDate = calendarDate;
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
	 * @return the supplementalId
	 */
	 @XmlAttribute
	public String getSupplementalId() {
		return supplementalId;
	}

	/**
	 * @param supplementalId the supplementalId to set
	 */
	public void setSupplementalId(String supplementalId) {
		this.supplementalId = supplementalId;
	}

	/**
	 * @return the sections
	 */
	@XmlElementWrapper(name = "sections")
	@XmlElement(name = "section")
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * @param sections the sections to set
	 */
	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	/**
	 * @return the calendar
	 */
	 @XmlTransient
	public Calendar getCalendar() {
		return calendar;
	}

	/**
	 * @param calendar the calendar to set
	 */
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Supplemental)
		{
			if ( ((Supplemental)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}

	
	@Override
	public String luceneOid() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String luceneOsearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String luceneOtype() {
		// TODO Auto-generated method stub
		return null;
	}
}
/*
<supplemental id="">
<caldate>2009-11-05</caldate>
<releasedate>2009-11-10</releasedate>
<releasetime>T11.23.00Z</releasetime>
<sections>
<section name="BILLS ON ORDER OF FIRST REPORT" type="C" cd="0150">
<calnos>
<calno no="0000000002">
<bill no="S00430A" />
<sponsor><![CDATA[ROBACH]]></sponsor>
<subbill no="" />
<subsponsor></subsponsor>
</calno>
*/