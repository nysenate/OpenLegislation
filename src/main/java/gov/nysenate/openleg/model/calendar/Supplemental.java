package gov.nysenate.openleg.model.calendar;

import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import gov.nysenate.openleg.lucenemodel.LuceneSupplemental;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("supplemental")
public class Supplemental  extends LuceneSupplemental {
	
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