package gov.nysenate.openleg.model;

import gov.nysenate.openleg.lucene.DocumentBuilder;
import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.util.HideFrom;
import gov.nysenate.openleg.xstream.XStreamCollectionAlias;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.lucene.document.Field;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
@XmlRootElement
@Cacheable
@XStreamAlias("vote")
public class Vote  extends SenateObject implements LuceneObject {
	
	@Persistent
	@Column(name="VOTE_TYPE")
	@XStreamAsAttribute
	private int voteType;
	
	@Persistent
	@PrimaryKey
	@Column(name="VOTE_ID", jdbcType="VARCHAR", length=50)
	@XStreamAsAttribute
	private String id;
	
	@Persistent
	@Column(name="VOTE_DATE")
	@XStreamAsAttribute
	private Date voteDate;	
	
	/*
	@Persistent
	@Column(name="ID", jdbcType="VARCHAR", length=50)
	private String oldId = "";
	
	@Persistent
	@Column(name="VOTETYPE")
	private int voteTypeOld = 0;
	*/
	
	
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
		this.id = buildId(bill, voteDate, ayeCount, nayCount);
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

	@Override
	public HashMap<String, Field> luceneFields() {
		HashMap<String,Field> map = new HashMap<String,Field>();

		if (bill != null)
			map.put("billno", new Field("billno",bill.getSenateBillNo(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		
		switch(voteType) {
			case Vote.VOTE_TYPE_COMMITTEE:
				if(description !=null)
					map.put("committee", new Field("committee",description, DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
				else if (bill != null)
					map.put("committee", new Field("committee",bill.getCurrentCommittee(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		}
		
		Iterator<String> itVote = null;
		StringBuilder sbVotes = null;
		
		if (abstains != null) {
    		sbVotes = new StringBuilder();
    		itVote = abstains.iterator();
    		while (itVote.hasNext()) {
    			sbVotes.append(itVote.next()).append(" ");
    		}
    		
    		map.put("abstain", new Field("abstain",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		}
		
		if (ayes != null) {
    		sbVotes = new StringBuilder();
    		itVote = ayes.iterator();
    		while (itVote.hasNext()) {
    			sbVotes.append(itVote.next()).append(" ");
    		}
    		
    		map.put("aye", new Field("aye",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		}
		
		if (excused != null) {
    		
    		sbVotes = new StringBuilder();
    		itVote = excused.iterator();
    		while (itVote.hasNext()) {
    			sbVotes.append(itVote.next()).append(" ");
    		}
    		
    		map.put("excused", new Field("excused",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		}
		
		if (nays != null) {
    		
    		sbVotes = new StringBuilder();
    		itVote = nays.iterator();
    		while (itVote.hasNext()) {
    			sbVotes.append(itVote.next()).append(" ");
    		}
    		
    		map.put("nay", new Field("nay",sbVotes.toString(), DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));
		} 
		
		map.put("when", new Field("when",voteDate.getTime()+"", DocumentBuilder.DEFAULT_STORE, DocumentBuilder.DEFAULT_INDEX));

		
		return map;
	}

	@Override
	public String luceneOid() {
		return id;
	}

	@Override
	public String luceneOsearch() {
		
		if (bill == null)
			return "";
		
		StringBuilder oSearch = new StringBuilder("");
		oSearch.append(bill.getSenateBillNo() + " ");
		switch(voteType) {
			case Vote.VOTE_TYPE_COMMITTEE:
				oSearch.append(" Committee Vote ");
				oSearch.append(bill.getCurrentCommittee());
			case Vote.VOTE_TYPE_FLOOR:
				oSearch.append(" Floor Vote ");
		}
		return oSearch.toString();
	}

	@Override
	public String luceneOtype() {
		return "vote";
	}

	@Override
	public String luceneSummary() {	
		return java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(voteDate);
	}

	@Override
	public String luceneTitle() {
		
		String title = "";
		
		if (bill != null)
			title += bill.getSenateBillNo();
		
		title += " - " + java.text.DateFormat.getDateInstance(DateFormat.MEDIUM).format(voteDate);
		
		switch(voteType) {
			case Vote.VOTE_TYPE_COMMITTEE:
				return title + " - Committee Vote";
			case Vote.VOTE_TYPE_FLOOR:
				return title + " - Floor Vote";
		}
		return title;
	}

}
