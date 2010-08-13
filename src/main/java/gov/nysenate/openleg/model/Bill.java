package gov.nysenate.openleg.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.*;

import gov.nysenate.openleg.xstream.BillListConverter;
import gov.nysenate.openleg.xstream.BillPersonConverter;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("bill")
public class Bill extends SenateObject implements Serializable
{

	@XStreamOmitField
	private static final long serialVersionUID = 5557623293161105544L;

	@Persistent
	@XStreamAsAttribute
	private int year;
	
	@Persistent
	@PrimaryKey
	@Column(name="senate_bill_no", jdbcType="VARCHAR", length=20)
	@XStreamAlias("senateId")
	@XStreamAsAttribute
	private String senateBillNo;	
	
	@Persistent
	@Column(name="title", jdbcType="VARCHAR", length=1000)
	@XStreamAsAttribute
	private String title;
	
	@Persistent
	@Column(name="law_section")
	@XStreamAsAttribute
	private String lawSection;
	
	
	
	@Persistent
	@Column(name="same_as", jdbcType="VARCHAR", length=256)
	@XStreamAsAttribute
	private String sameAs;
	
	/*
	@Persistent
	private String substitutedBy;
	
	@Persistent
	private String substitutedFor;
	*/

	
	@Persistent(defaultFetchGroup="true")
	private Person sponsor;
	
	@Persistent(defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@XStreamAlias("cosponsors")
	private List<Person> coSponsors;
	
	@Persistent(defaultFetchGroup="true")
	@XStreamConverter(BillListConverter.class)
	private List<Bill> amendments;
	
	@Persistent
	@Column(name="summary", jdbcType="VARCHAR", length=10000)
	private String summary;
	
	@Persistent
	@Column(name="current_committee")
	@XStreamAlias("committee")
	private String currentCommittee;
	
	@Persistent(defaultFetchGroup="true")
	@Order(column="bill_events_integer_idx")
	@Element(column="bill_events_senate_bill_no_own")
	@XStreamAlias("actions")
	@XStreamConverter(BillListConverter.class)
	private List<BillEvent> billEvents;
	
	@Persistent
	@Column(name="FULLTEXT", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("text")
	private String fulltext;
	
	@Persistent
	@Column(name="MEMO", jdbcType="LONGVARCHAR", length=250000)
	private String memo;
	
	@Persistent
	private String law;
	
	@Persistent
	@Column(name="act_clause", jdbcType="VARCHAR", length=10000)
	private String actClause;
	
	@Persistent
	@Column(name="sort_index")
	private int sortIndex = -1;
	
	@Persistent(defaultFetchGroup="true",mappedBy="bill")
	@Join
	@Order(column="integer_idx")
//	@XStreamConverter(BillListConverter.class)
	private List<Vote> votes;

	@Persistent(defaultFetchGroup="true")
	private Bill latestAmendment;
	
	
	
	
	
	/**
	 * @return the amendments
	 */
	@XmlTransient
	public List<Bill> getAmendments() {
		return amendments;
	}

	/**
	 * @param amendments the amendments to set
	 */
	public void setAmendments(List<Bill> amendments) {
		this.amendments = amendments;
	}

	public Bill ()
	{
	}

	/**
	 * @return the coSponsors
	 */
	public List<Person> getCoSponsors() {
		return coSponsors;
	}

	
	/**
	 * @return the sponsor
	 */
	@XmlElement
	public Person getSponsor() {
		return sponsor;
	}

	/**
	 * @param sponsor the sponsor to set
	 */
	public void setSponsor(Person sponsor) {
		this.sponsor = sponsor;
	}

	
	/**
	 * @return the currentCommittee
	 */
	public String getCurrentCommittee() {
		return currentCommittee;
	}

	/**
	 * @param currentCommittee the currentCommittee to set
	 */
	public void setCurrentCommittee(String currentCommittee) {
		this.currentCommittee = currentCommittee;
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
	 * @return the senateBillNo
	 */
	@XmlAttribute(name="id")
	public String getSenateBillNo() {
		return senateBillNo;
	}

	/**
	 * @param senateBillNo the senateBillNo to set
	 */
	public void setSenateBillNo(String senateBillNo) {
		this.senateBillNo = senateBillNo;
	}

	/**
	 * @return the assemblyBillNo aka SAME AS
	 */
	@XmlElement
	public String getSameAs() {
		return sameAs;
	}

	/**
	 * @param assemblyBillNo the assemblyBillNo to set
	 */
	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}

	
	/**
	 * @return the memo
	 */
	@XmlTransient
	public String getMemo() {
	
		return memo;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
		
	}

	/**
	 * @return the text
	 */
	@XmlTransient
	public String getFulltext() {
		return fulltext;
	}

	/**
	 * @param text the text to set
	 */
	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
	}

	/**
	 * @return the summary
	 */
	@XmlElement
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}


	/*
	public List<BillEvent> getBillEvents() {
		return billEvents;
	}

	public void addEvent (Date eventDate, String eventText)
	{

		if (billEvents == null)
			billEvents = new ArrayList<BillEvent>();
		
		BillEvent bEvent = PMF.getBillEvent(this, eventDate, eventText);
		
		//only add it if the item is null
		 if (bEvent == null)
	        {
	        	bEvent = new BillEvent(this,eventDate,eventText);
	        	bEvent = PMF.getPersistenceManager().makePersistent(bEvent);
	        	
	        }
		 
		 
		if (!billEvents.contains(bEvent))
			 billEvents.add(bEvent);
		
	}*/
	
	/*
	public void addCoSponsor (Person person)
	{
		if (coSponsors == null)
			coSponsors = new ArrayList<Person>();
		
		if (!hasCoSponsor(person))
			coSponsors.add(person);	
	}
	
	public boolean hasCoSponsor (Person person)
	{
		Iterator<Person> itPerson = coSponsors.iterator();
		while(itPerson.hasNext())
		{
			if (itPerson.next().getFullname().equalsIgnoreCase(person.getFullname()))
			{
				return true;
			}
		}
		
		return false;
	}*/
	

	/**
	 * @return the votes
	 */
	public List<Vote> getVotes() {
		return votes;
	}
	
	/**
	 * @param coSponsors the coSponsors to set
	 */
	public void setCoSponsors(List<Person> coSponsors) {
		this.coSponsors = coSponsors;
	}

	

	public void addVote (Vote vote)
	{

		if (votes == null)
			votes = new ArrayList<Vote>();
		
		votes.add(vote);
	}

	


	/**
	 * @return the sortIndex
	 */
	@XmlTransient
	public int getSortIndex() {
		return sortIndex;
	}

	/**
	 * @param sortIndex the sortIndex to set
	 */
	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}


	/**
	 * @param votes the votes to set
	 */
	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}

	/**
	 * @return the lawSection
	 */
	public String getLawSection() {
		return lawSection;
	}

	/**
	 * @param lawSection the lawSection to set
	 */
	public void setLawSection(String lawSection) {
		this.lawSection = lawSection;
	}

	/**
	 * @return the law
	 */
	public String getLaw() {
		return law;
	}

	/**
	 * @param law the law to set
	 */
	public void setLaw(String law) {
		this.law = law;
	}

	/**
	 * @return the actClause
	 */
	public String getActClause() {
		return actClause;
	}

	/**
	 * @param actClause the actClause to set
	 */
	public void setActClause(String actClause) {
		this.actClause = actClause;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	/**
	 * @return the latestAmendment
	 */
	@XmlTransient
	public Bill getLatestAmendment() {
		return latestAmendment;
	}

	/**
	 * @param latestAmendment the latestAmendment to set
	 */
	public void setLatestAmendment(Bill latestAmendment) {
		this.latestAmendment = latestAmendment;
	}
	
/*
	public String getSubstitutedBy() {
		return substitutedBy;
	}

	public void setSubstitutedBy(String substitutedBy) {
		this.substitutedBy = substitutedBy;
	}

	public void setSubstitutedFor(String substitutedFor) {
		this.substitutedFor = substitutedFor;
	}

	public String getSubstitutedFor() {
		return substitutedFor;
	}*/

	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Bill)
		{
			String thisId = getSenateBillNo() + '-' + getYear();
			String thatId =  ((Bill)obj).getSenateBillNo() + '-' +  ((Bill)obj).getYear();
			
			return (thisId.equals(thatId));
		}
		
		return false;
	}

	public void setBillEvents(List<BillEvent> billEvents) {
		this.billEvents = billEvents;
	}

	public List<BillEvent> getBillEvents() {
		return billEvents;
	}
}
