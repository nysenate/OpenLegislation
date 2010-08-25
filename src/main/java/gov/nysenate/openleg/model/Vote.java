package gov.nysenate.openleg.model;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;
import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
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

@PersistenceCapable
@XmlRootElement
@Cacheable
@XStreamAlias("vote")
public class Vote  extends SenateObject {
	
	@Persistent
	@Column(name="vote_type")
	@XStreamAsAttribute
	private int voteType;
	
	@Persistent
	@PrimaryKey
	@Column(name="VOTE_ID")
	@XStreamAsAttribute
	private String id;
	
	@Persistent
	@Column(name="vote_date")
	@XStreamAsAttribute
	private Date voteDate;	
	
	
	
	
	@Persistent(defaultFetchGroup="true", serialized = "false")
	@XStreamCollectionAlias(node="ayes",value="member")
	private List<String> ayes;
	
	@Persistent(defaultFetchGroup="true", serialized = "false")
	@XStreamCollectionAlias(node="nays",value="member")
	private List<String> nays;
	
	@Persistent(defaultFetchGroup="true", serialized = "false")
	@XStreamCollectionAlias(node="abstains",value="member")
	private List<String> abstains;	
	
	
	@Persistent(defaultFetchGroup="true", serialized = "false")
	@XStreamCollectionAlias(node="excused",value="member")
	private List<String> excused;
	
	@Persistent(defaultFetchGroup="true", dependent = "false")
	@XmlTransient
	@HideFrom({Bill.class, Meeting.class, Calendar.class, Supplemental.class})
	private Bill bill;
	

	
	@Persistent(defaultFetchGroup="true", serialized = "false")
	@XStreamCollectionAlias(node="ayeswr",value="member")
	private List<String> ayeswr;
	
	@Persistent
	@Column(name="description")
	private String description;	

	
	

	public final static int VOTE_TYPE_FLOOR = 1;
	public final static int VOTE_TYPE_COMMITTEE = 2;
	
	/**
	 * @return the excused
	 */

	@XmlElementWrapper(name = "excuseds")
	@XmlElement(name = "member")
	public List<String> getExcused() {
		return excused;
	}

	/**
	 * @param excused the excused to set
	 */
	public void setExcused(List<String> excused) {
		this.excused = excused;
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
	 * @return the voteDate
	 */
	@XmlAttribute
	public Date getVoteDate() {
		return voteDate;
	}

	/**
	 * @param voteDate the voteDate to set
	 */
	public void setVoteDate(Date voteDate) {
		this.voteDate = voteDate;
	}



	
	/**
	 * @return the ayes
	 */
	@XmlElementWrapper(name = "ayes")
	@XmlElement(name = "member")
	public List<String> getAyes() {
		return ayes;
	}

	/**
	 * @param ayes the ayes to set
	 */
	public void setAyes(List<String> ayes) {
		this.ayes = ayes;
	}

	/**
	 * @return the nays
	 */

	@XmlElementWrapper(name = "nays")
	@XmlElement(name = "member")
	public List<String> getNays() {
		return nays;
	}

	/**
	 * @return the ayeswr
	 */
	public List<String> getAyeswr() {
		return ayeswr;
	}

	/**
	 * @param ayeswr the ayeswr to set
	 */
	public void setAyeswr(List<String> ayeswr) {
		this.ayeswr = ayeswr;
	}

	/**
	 * @param nays the nays to set
	 */
	public void setNays(List<String> nays) {
		this.nays = nays;
	}

	/**
	 * @return the bill
	 */
	@XmlTransient
	public Bill getBill() {
		return bill;
	}

	/**
	 * @param bill the bill to set
	 */
	public void setBill(Bill bill) {
		this.bill = bill;
	}

	public Vote ()
	{
		
	}
	
	public Vote (Bill bill, Date voteDate, int ayeCount, int nayCount)
	{
		id = buildId(bill, voteDate, ayeCount, nayCount);
		this.bill = bill;
		this.voteDate = voteDate;
		
	}
	
	public static String buildId (Bill bill, Date voteDate, int ayeCount, int nayCount)
	{
		return voteDate.getTime() + bill.getSenateBillNo() + '-' + ayeCount + '-' + nayCount;
		
	}
	
	public void addAye(Person person)
	{
		if (ayes == null)
		{
			ayes = new ArrayList<String>();
		}
		
		ayes.add(person.getFullname());
	}
	
	
	public void addAyeWR(Person person)
	{
		if (ayeswr == null)
		{
			ayeswr = new ArrayList<String>();
		}
		
		ayeswr.add(person.getFullname());
	}
	
	public void addNay(Person person)
	{
		if (nays == null)
		{
			nays = new ArrayList<String>();
		}
		
		nays.add(person.getFullname());
	}
	
	public void addAbstain(Person person)
	{
		if (abstains == null)
		{
			abstains = new ArrayList<String>();
		}
		
		abstains.add(person.getFullname());
	}
	
	public void addExcused(Person person)
	{
		if (excused == null)
		{
			excused = new ArrayList<String>();
		}
		
		excused.add(person.getFullname());
	}

	/**
	 * @return the abstains
	 */

	@XmlElementWrapper(name = "abstains")
	@XmlElement(name = "member")
	public List<String> getAbstains() {
		return abstains;
	}

	/**
	 * @param abstains the abstains to set
	 */
	public void setAbstains(List<String> abstains) {
		this.abstains = abstains;
	}
	
	

	/**
	 * @return the voteType
	 */
	@XmlAttribute
	public int getVoteType() {
		return voteType;
	}

	/**
	 * @param voteType the voteType to set
	 */
	public void setVoteType(int voteType) {
		this.voteType = voteType;
	}
	


	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

}
