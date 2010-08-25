package gov.nysenate.openleg.model.calendar;

import java.util.List;
import java.util.HashMap;

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
import org.apache.lucene.document.Field;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.util.HideFrom;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("calendar")
public class Calendar  extends SenateObject implements LuceneObject {

	@Persistent
	@Column(name="year")
	@XStreamAsAttribute
	protected int year;
	
	@Persistent
	@Column(name="type")
	@XStreamAsAttribute
	protected String type;
	
	@Persistent
	@Column(name="session_year")
	@XStreamAsAttribute
	protected int sessionYear;
	
	@Persistent
	@Column(name="no")
	@XStreamAsAttribute
	protected int no;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="calendar")
	@Join
	@Element(dependent = "true")  
	@Order(column="integer_idx")
	protected List<Supplemental> supplementals;
	
	@Persistent
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	@HideFrom({Calendar.class, Supplemental.class})
	protected String id;	
	
	public final static String TYPE_FLOOR = "floor";
	public final static String TYPE_ACTIVE = "active";

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
	 * @return the no
	 */
	 @XmlAttribute
	public int getNo() {
		return no;
	}

	/**
	 * @param no the no to set
	 */
	public void setNo(int no) {
		this.no = no;
	}

	/**
	 * @return the sessionYear
	 */
	 @XmlAttribute
	public int getSessionYear() {
		return sessionYear;
	}

	/**
	 * @param sessionYear the sessionYear to set
	 */
	public void setSessionYear(int sessionYear) {
		this.sessionYear = sessionYear;
	}

	/**
	 * @return the year
	 */
	 @XmlAttribute
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
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
	 * @return the supplementals
	 */
	@XmlElementWrapper(name = "supplementals")
	@XmlElement(name = "supplemental")
	public List<Supplemental> getSupplementals() {
		return supplementals;
	}

	/**
	 * @param supplementals the supplementals to set
	 */
	public void setSupplementals(List<Supplemental> supplementals) {
		this.supplementals = supplementals;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Calendar)
		{
			if ( ((Calendar)obj).getId().equals(this.getId()))
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
	
	@Override
	public HashMap<String,Field> luceneFields() {
		return null;
	}

	@Override
	public String luceneTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String luceneSummary() {
		// TODO Auto-generated method stub
		return null;
	}
}
