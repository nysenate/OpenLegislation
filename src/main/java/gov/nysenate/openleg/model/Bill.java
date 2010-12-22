package gov.nysenate.openleg.model;

import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.thoughtworks.xstream.annotations.*;
import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import gov.nysenate.openleg.lucene.LuceneField;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;
import gov.nysenate.openleg.xstream.BillListConverter;


@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("bill")
@JsonIgnoreProperties("votes")
public class Bill extends SenateObject implements LuceneObject  {
	
	
	@Persistent
	@XStreamAsAttribute
	@LuceneField
	protected int year;
	
	@Persistent
	@PrimaryKey
	@Column(name="senate_bill_no", jdbcType="VARCHAR", length=20)
	@XStreamAlias("senateId")
	@XStreamAsAttribute
	protected String senateBillNo;	
	
	@Persistent
	@Column(name="title", jdbcType="VARCHAR", length=1000)
	@XStreamAsAttribute
	@LuceneField
	protected String title;
	
	@Persistent
	@Column(name="law_section")
	@XStreamAsAttribute
	@LuceneField
	protected String lawSection;
	
	@Persistent
	@Column(name="same_as", jdbcType="VARCHAR", length=256)
	@XStreamAsAttribute
	@LuceneField
	protected String sameAs;
	
	@Persistent(defaultFetchGroup="true")
	@LuceneField
	protected Person sponsor;
	
	@Persistent(defaultFetchGroup="true")
	@Join
	@Order(column="integer_idx")
	@XStreamAlias("cosponsors")
	@LuceneField
	protected List<Person> coSponsors;
	
	@Persistent(defaultFetchGroup="true")
	@XStreamConverter(BillListConverter.class)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected List<String> amendments;
	
	@Persistent
	@Column(name="summary", jdbcType="VARCHAR", length=10000)
	@LuceneField
	protected String summary;

	@Persistent
	@Column(name="current_committee")
	@XStreamAlias("committee")
	@LuceneField("committee")
	protected String currentCommittee;
	
	@Persistent(defaultFetchGroup="true")
	@Order(column="bill_events_integer_idx")
	@Element(column="bill_events_senate_bill_no_own")
	@XStreamAlias("actions")
	@XStreamConverter(BillListConverter.class)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField("actions")
	protected List<BillEvent> billEvents;
	
	@Persistent
	@Column(name="FULLTEXT", jdbcType="LONGVARCHAR", length=250000)
	@XStreamAlias("text")
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField("full")
	protected String fulltext;
	
	@Persistent
	@Column(name="MEMO", jdbcType="LONGVARCHAR", length=250000)
	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String memo;
	
	@Persistent
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String law;
	
	@Persistent
	@Column(name="act_clause", jdbcType="VARCHAR", length=10000)
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String actClause;
	
	@Persistent
	@Column(name="sort_index")
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected int sortIndex = -1;
	
	@Persistent(defaultFetchGroup="true",mappedBy="bill")
	@Join
	@Order(column="integer_idx")
	@HideFrom({Bill.class})
	protected List<Vote> votes;

	@Persistent(defaultFetchGroup="true")
	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected Bill latestAmendment;
	
	/**
	 * @return the amendments
	 */
	@XmlTransient
	public List<String> getAmendments() {
		return amendments;
	}

	/**
	 * @param amendments the amendments to set
	 */
	public void setAmendments(List<String> amendments) {
		this.amendments = amendments;
	}

	public Bill () { }

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

	public void addVote (Vote vote) {
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
	 * @param law the laiw to set
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
	
	@Override
	public String luceneOtype() {
		return "bill";
	}
	
	@Override
	public String luceneOid() {
		
		if (senateBillNo.indexOf("-" + year)==-1)
			return senateBillNo + "-" + year;
		else
			return senateBillNo;
	}
	
	@Override
	public HashMap<String,Field> luceneFields() {
		return null;
	}
	
	@Override
	public String luceneSummary() {
		return summary;
	}
	
	@Override
	public String luceneTitle() {
		return (title == null) ? summary : title;
	}

	@Override public String luceneOsearch() {
		return senateBillNo.split("-")[0] + " "
		    + year + " "
		    + senateBillNo + "-" + year
			+ (sameAs != null ? " " + sameAs:"")
			+ (sponsor != null ? " " + sponsor.getFullname():"")
			+ (title != null ? " " + title:"")
			+ (summary != null ? " " + summary:"");
		
	}
	
	public String getLuceneCoSponsors() {
		StringBuilder response = new StringBuilder();
		for( Person sponsor : coSponsors) {
			response.append(sponsor.getFullname() + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
	
	public String getLuceneAmendments() {
		if(amendments == null) {
			return "";
		}
		StringBuilder response = new StringBuilder();
		for(String amendment : amendments) {
			response.append(amendment + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
	
	public String getLuceneBillEvents() {
		StringBuilder response = new StringBuilder();
		for(BillEvent be : billEvents) {
			response.append(be.getEventText() + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
	
	public String getLuceneSponsor() {
		if(sponsor != null) {
			return sponsor.getFullname();
		}
		return "";
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
}
