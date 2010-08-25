package gov.nysenate.openleg.model.calendar;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.HideFrom;

import java.util.Date;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("calendarEntries")
public class CalendarEntry {

	@Persistent
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	@XStreamAsAttribute
	private String id;
	
	@Persistent
	@Column(name="no")
	@XStreamAsAttribute
	private String no;
	
	@Persistent(serialized = "false",dependent = "false",defaultFetchGroup="true")
	private Bill bill;
	
	@Persistent
	@Column(name="bill_high")
	@HideFrom({Calendar.class, Supplemental.class})
	private String billHigh;
	
	/**
	 * @return the billHigh
	 */
	@XmlAttribute
	public String getBillHigh() {
		return billHigh;
	}

	/**
	 * @param billHigh the billHigh to set
	 */
	public void setBillHigh(String billHigh) {
		this.billHigh = billHigh;
	}

	@Persistent(serialized = "false",dependent = "false",defaultFetchGroup="true")
	@HideFrom({Calendar.class, Supplemental.class})
	private Bill subBill;

	@Persistent
	@HideFrom({Calendar.class, Supplemental.class})
	private Date motionDate;
	
	@Persistent(serialized = "false",dependent = "false",defaultFetchGroup="true")
	@XmlTransient
	@HideFrom({Calendar.class, Supplemental.class})
	private Section section;
	
	@Persistent(serialized = "false",dependent = "false",defaultFetchGroup="true")
	@XmlTransient
	@HideFrom({Calendar.class, Supplemental.class})
	private Sequence sequence;
	
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
	 * @return the sequence
	 */
	@XmlTransient
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
	 * @return the section
	 */
	@XmlTransient
	public Section getSection() {
		return section;
	}

	/**
	 * @param section the section to set
	 */
	public void setSection(Section section) {
		this.section = section;
	}

	/**
	 * @return the motionDate
	 */
	@XmlAttribute
	public Date getMotionDate() {
		return motionDate;
	}

	/**
	 * @param motionDate the motionDate to set
	 */
	public void setMotionDate(Date motionDate) {
		this.motionDate = motionDate;
	}

	

	/**
	 * @return the bill
	 */
	@XmlElement
	public Bill getBill() {
		return bill;
	}

	/**
	 * @param bill the bill to set
	 */
	public void setBill(Bill bill) {
		this.bill = bill;
	}

	/**
	 * @return the subBill
	 */
	@XmlElement
	public Bill getSubBill() {
		return subBill;
	}

	/**
	 * @param subBill the subBill to set
	 */
	public void setSubBill(Bill subBill) {
		this.subBill = subBill;
	}

	/**
	 * @return the no
	 */
	@XmlAttribute
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
		
		if (obj != null && obj instanceof CalendarEntry)
		{
			if ( ((CalendarEntry)obj).getId().equals(this.getId()))
				return true;
		}
		
		return false;
	}


}
/*
 * <calno no="0000000005">
<bill no="S00405" />
<sponsor><![CDATA[ROBACH]]></sponsor>
<subbill no="" />
<subsponsor></subsponsor>
</calno>
*/
