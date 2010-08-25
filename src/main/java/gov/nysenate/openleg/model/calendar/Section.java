package gov.nysenate.openleg.model.calendar;

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

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
public class Section {

	@Persistent
	@PrimaryKey
	@Column(name="id")
	private String id;
	
	@Persistent
	@Column(name="name")
	private String name;
	
	@Persistent
	@Column(name="type")
	private String type;
	
	@Persistent
	@Column(name="cd")
	private String cd;

	@Persistent(serialized = "false",dependent = "false",defaultFetchGroup="true")
	@XmlTransient
	private Supplemental supplemental;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="section")
	@Join
	@Element(dependent = "true")  
	@Order(column="integer_idx")
	private List<CalendarEntry> calendarEntries;
	

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
	 * @return the supplemental
	 */
	@XmlTransient
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
	 * @return the type
	 */
	 @XmlAttribute
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the cd
	 */
	 @XmlAttribute
	public String getCd() {
		return cd;
	}

	/**
	 * @param cd the cd to set
	 */
	public void setCd(String cd) {
		this.cd = cd;
	}

	/**
	 * @return the calendarEntries
	 */
	@XmlElementWrapper(name = "calNos")
	@XmlElement(name = "calNo")
	public List<CalendarEntry> getCalendarEntries() {
		return calendarEntries;
	}

	/**
	 * @param calendarEntries the calendarEntries to set
	 */
	public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
		this.calendarEntries = calendarEntries;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Section)
		{
			if ( ((Section)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}


	
}
/*
/*
 * <sections>
<section name="BILLS ON ORDER OF FIRST REPORT" type="C" cd="0150">
<calnos>
<calno no="0000000014">
<bill no="A02301" />
<sponsor><![CDATA[MCENENY]]></sponsor>
<subbill no="S01676" />
<subsponsor><![CDATA[AUBERTINE]]></subsponsor>
</calno>
<calno no="0000000022">
<bill no="S00138" />
<sponsor><![CDATA[SAMPSON]]></sponsor>
<subbill no="" />
<subsponsor></subsponsor>
</calno>
<calno no="0000000023">
<bill no="S01647A" />
<sponsor><![CDATA[SCHNEIDERMAN]]></sponsor>
<subbill no="" />
<subsponsor></subsponsor>
</calno>
</calnos>
</section>
 */