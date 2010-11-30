package gov.nysenate.openleg.model.committee;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;

import java.util.HashMap;
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

import org.apache.lucene.document.Field;

/*
 * <SENATEDATA TIME="2009-11-10-10.26.43">

<senagenda no="79" sessyr="2009" year="2009" action="replace" >

<addendum id="">

<weekof>2009-11-02</weekof>

<pubdate>2009-11-10</pubdate>

<pubtime>T09.29.54Z</pubtime>
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
public class Agenda  extends SenateObject implements LuceneObject {


	@Persistent 
	@PrimaryKey
	@Column(name="id", jdbcType="VARCHAR", length=100)
	private String id;
	
	@Persistent
	@Column(name="number")
	private int number;
	
	@Persistent
	@Column(name="session_year")
	private int sessionYear;
	
	@Persistent
	@Column(name="year")
	private int year;
	
	@Persistent(serialized = "false",defaultFetchGroup="true",mappedBy="agenda")
	@Join
	@Element(dependent = "true")
	@Order(column="integer_idx")
	private List<Addendum> addendums;


	/**
	 * @return the number
	 */
	@XmlAttribute
	public int getNumber() {
		return number;
	}

	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}

	/**
	 * @return the addendums
	 */
	@XmlElementWrapper(name = "addendums")
	@XmlElement(name = "addendum")
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

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Agenda)
		{
			if ( ((Agenda)obj).getId().equals(this.getId()))
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
	public String luceneSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String luceneTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashMap<String, Field> luceneFields() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
