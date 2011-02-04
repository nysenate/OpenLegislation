package model.bill;

import gov.nysenate.openleg.lucene.LuceneField;
import ingest.SenateObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Field;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import util.BillCleaner;

@XStreamAlias("bill")
@JsonIgnoreProperties("votes")
public class Bill implements SenateObject  {
	
	@XStreamAsAttribute
	@LuceneField
	protected int year;
	
	@XStreamAlias("senateId")
	@XStreamAsAttribute
	protected String senateBillNo;
	
	@XStreamAsAttribute
	@LuceneField
	protected String title;
	
	@XStreamAsAttribute
	@LuceneField
	protected String lawSection;
	
	@XStreamAsAttribute
	@LuceneField
	protected String sameAs;
	
	@LuceneField
	protected Person sponsor;
	
	@XStreamAlias("cosponsors")
	@LuceneField
	protected List<Person> coSponsors;
	
//	@XStreamConverter(BillListConverter.class)
//	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected List<String> amendments;
	
	@LuceneField
	protected String summary;
	
	@XStreamAlias("committee")
	@LuceneField("committee")
	protected String currentCommittee;
	
	@XStreamAlias("actions")
//	@XStreamConverter(BillListConverter.class)
//	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField("actions")
	protected List<BillEvent> billEvents;
	
	@XStreamAlias("text")
//	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField("full")
	protected String fulltext;
	
//	@HideFrom({Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String memo;
	
//	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String law;
	
//	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected String actClause;
	
//	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	protected int sortIndex = -1;
	
//	@HideFrom({Bill.class})
	protected List<Vote> votes;
	
//	@HideFrom({Bill.class,Meeting.class, Calendar.class, Supplemental.class})
	@LuceneField
	protected Bill latestAmendment;

	public Bill () {
		
	}

	public int getYear() {
		return year;
	}



	public String getSenateBillNo() {
		return senateBillNo;
	}



	public String getTitle() {
		return title;
	}



	public String getLawSection() {
		return lawSection;
	}



	public String getSameAs() {
		return sameAs;
	}



	public Person getSponsor() {
		return sponsor;
	}



	public List<Person> getCoSponsors() {
		return coSponsors;
	}



	public List<String> getAmendments() {
		return amendments;
	}



	public String getSummary() {
		return summary;
	}



	public String getCurrentCommittee() {
		return currentCommittee;
	}



	public List<BillEvent> getBillEvents() {
		return billEvents;
	}



	public String getFulltext() {
		return fulltext;
	}



	public String getMemo() {
		return memo;
	}



	public String getLaw() {
		return law;
	}



	public String getActClause() {
		return actClause;
	}



	public int getSortIndex() {
		return sortIndex;
	}



	public List<Vote> getVotes() {
		return votes;
	}



	public Bill getLatestAmendment() {
		return latestAmendment;
	}



	public void setYear(int year) {
		this.year = year;
	}



	public void setSenateBillNo(String senateBillNo) {
		this.senateBillNo = senateBillNo;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public void setLawSection(String lawSection) {
		this.lawSection = lawSection;
	}



	public void setSameAs(String sameAs) {
		this.sameAs = sameAs;
	}



	public void setSponsor(Person sponsor) {
		this.sponsor = sponsor;
	}



	public void setCoSponsors(List<Person> coSponsors) {
		this.coSponsors = coSponsors;
	}



	public void setAmendments(List<String> amendments) {
		this.amendments = amendments;
	}



	public void setSummary(String summary) {
		this.summary = summary;
	}



	public void setCurrentCommittee(String currentCommittee) {
		this.currentCommittee = currentCommittee;
	}



	public void setBillEvents(List<BillEvent> billEvents) {
		this.billEvents = billEvents;
	}



	public void setFulltext(String fulltext) {
		this.fulltext = fulltext;
	}



	public void setMemo(String memo) {
		this.memo = memo;
	}



	public void setLaw(String law) {
		this.law = law;
	}



	public void setActClause(String actClause) {
		this.actClause = actClause;
	}



	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}



	public void setVotes(List<Vote> votes) {
		this.votes = votes;
	}



	public void setLatestAmendment(Bill latestAmendment) {
		this.latestAmendment = latestAmendment;
	}



	public void addVote (Vote vote) {
		if (votes == null)
			votes = new ArrayList<Vote>();
		votes.add(vote);
	}
	
	public void removeVote(Vote vote) {
		if(votes == null) {
			return;
		}
		votes.remove(vote);
	}


	@Override
	public boolean equals(Object obj) {
		
		if (obj != null && obj instanceof Bill)
		{
			String d = getSenateBillNo() + '-' + getYear();
			String thatId =  ((Bill)obj).getSenateBillNo() + '-' +  ((Bill)obj).getYear();
			
			return (d.equals(thatId));
		}
		
		return false;
	}
	
	public void merge(SenateObject object) {
		if(!(object instanceof Bill)) {
			return;
		}
		Bill bill = (Bill)object;
		
		if(!getSenateBillNo().equals(bill.getSenateBillNo())) {
			return;
		}
		
		if(year == 0) {
			year = bill.getYear();
		}
		
		if(title == null) {
			title = bill.getTitle();
		}
		else {
			if(bill.getTitle() != null && !bill.getTitle().equals("")) {
				title = bill.getTitle();
			}
		}
		
		if(lawSection == null) {
			lawSection = bill.getLawSection();
		}
		else {
			if(bill.getLawSection() != null && !bill.getLawSection().equals("")) {
				lawSection = bill.getLawSection();
			}
		}
		
		if(sameAs == null) {
			sameAs = bill.getSameAs();
		}
		else {
			if(bill.getSameAs() != null && !bill.getSameAs().equals("")) {
				StringTokenizer st = new StringTokenizer(bill.getSameAs(), ", ");
				while(st.hasMoreElements()) {
					sameAs = BillCleaner.formatSameAs(sameAs, st.nextToken());
				}
			}
		}
		
		if(sponsor == null) {
			sponsor = bill.getSponsor();
		}
		else {
			if(bill.getSponsor() != null && bill.getSponsor().getFullname() != null && !bill.getSponsor().getFullname().equals("")) {
				sponsor = bill.getSponsor();
			}
		}	
		
		if(summary == null) {
			summary = bill.getSummary();
		}
		else {
			if(bill.getSummary() != null && !bill.getSummary().equals("")) {
				summary = bill.getSummary();
			}
		}		
		
		if(currentCommittee == null) {
			currentCommittee = bill.getCurrentCommittee();
		}
		else {
			if(bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) {
				currentCommittee = bill.getCurrentCommittee();
			}
		}
		
		if(fulltext == null) {
			fulltext = bill.getFulltext();
		}
		else {
			if(bill.getFulltext() != null && !bill.getFulltext().equals("")) {
				int newLineCodeStart = new Integer(bill.getFulltext().substring(1,6));
				int newLineCodeEnd = -1;
				
				int oldLineCodeStart = new Integer(this.fulltext.substring(1,6));
				int oldLineCodeEnd = -1;
				
				Pattern p = Pattern.compile("(\\d{5})\\:.*?\n$");
				Matcher m = p.matcher(this.fulltext);
				
				if(m.find())
					oldLineCodeEnd = new Integer(m.group(1));
				
				m = p.matcher(bill.getFulltext());
				if(m.find())
					newLineCodeEnd = new Integer(m.group(1));
				
				if(newLineCodeEnd == -1 || oldLineCodeEnd == -1) {
					//THIS SHOULD NOT HAPPEN
					System.err.println(fulltext + "" + bill.getFulltext());
				}
				
				if(newLineCodeStart == oldLineCodeStart
						&& newLineCodeEnd == oldLineCodeEnd) {
					this.fulltext = bill.getFulltext();
				}
				else {
					if(oldLineCodeStart == newLineCodeStart) {
						this.fulltext = bill.getFulltext();
					}
					else if(oldLineCodeEnd == newLineCodeEnd) {
						this.fulltext = bill.getFulltext();
					}
					else if (newLineCodeStart == (oldLineCodeEnd + 1)){
						System.out.println(senateBillNo + ": merge 1");
						String temp = new String(this.fulltext + bill.getFulltext());
						this.fulltext = temp;
					}
					else if(newLineCodeEnd == (oldLineCodeStart - 1)) {
						System.out.println(senateBillNo + ": merge 2");
						String temp = new String(bill.getFulltext() + this.fulltext);
						this.fulltext = temp;
					}
					else {
						if(oldLineCodeStart < newLineCodeEnd) {
							System.out.println(3 + ": " + senateBillNo + ": " + newLineCodeStart + ", " + newLineCodeEnd + " : "
									+ oldLineCodeStart + ", " + oldLineCodeEnd);
						}
						else if(newLineCodeStart < oldLineCodeStart) {
							System.out.println(4 + ": " + senateBillNo + ": " + newLineCodeStart + ", " + newLineCodeEnd + " : "
									+ oldLineCodeStart + ", " + oldLineCodeEnd);
						}
						else {
							/*THIS SHOULD NOT HAPPEN*/
							System.err.println(senateBillNo + ": " + newLineCodeStart + ", " + newLineCodeEnd + " : "
									+ oldLineCodeStart + ", " + oldLineCodeEnd);
						}
					}
				}
			}
		}
		
		if(memo == null) {
			memo = bill.getMemo();
		}
		else {
			if(bill.getMemo() != null && !bill.getMemo().equals("")) {
				memo = bill.getMemo();
			}
		}
		
		if(law == null) {
			law = bill.getLaw();
		}
		else {
			if(bill.getLaw() != null && !bill.getLaw().equals("")) {
				law = bill.getLaw();
			}
		}
		
		if(actClause == null) {
			actClause = bill.getActClause();
		}
		else {
			if(bill.getActClause() != null && !bill.getActClause().equals("")) {
				actClause = bill.getActClause();
			}
		}
		
		if(sortIndex == -1) {
			sortIndex = bill.getSortIndex();
		}
		
		if(latestAmendment == null) {
			latestAmendment = bill.getLatestAmendment();
		}
		else {
			if(bill.getLatestAmendment() != null) {
				latestAmendment = bill.getLatestAmendment();
			}
		}
		
		
		
		
		
		if(billEvents == null) {
			billEvents = bill.getBillEvents();
		}
		else {
			if(bill.getBillEvents() != null) {
				for(BillEvent billEvent:bill.getBillEvents()) {
					if(!this.billEvents.contains(billEvent)) {
						this.billEvents.add(billEvent);
					}
				}
			}
		}
		if(votes == null) {
			votes = bill.getVotes();
		}
		else {
			if(bill.getVotes() != null) {
				for(Vote vote:bill.getVotes()) {
					if(!this.votes.contains(vote)) {
						this.votes.add(vote);
					}
				}
			}
		}
		if(coSponsors == null) {
			coSponsors = bill.getCoSponsors();
		}
		else {
			if(bill.getCoSponsors() != null) {
				for(Person person:bill.getCoSponsors()) {
					if(!this.coSponsors.contains(person)) {
						this.coSponsors.add(person);
					}
				}
			}
		}
		if(amendments == null) {
			amendments = bill.getAmendments();
		}
		else {
			if(bill.getAmendments() != null) {
				for(String amendment:bill.getAmendments()) {
					if(!this.getAmendments().contains(amendment)) {
						this.amendments.add(amendment);
					}
				}
			}
		}		
	}
	
	@JsonIgnore
	@Override
	public String luceneOtype() {
		return "bill";
	}
	
	@JsonIgnore
	@Override
	public String luceneOid() {
		
		if (senateBillNo.indexOf("-" + year)==-1)
			return senateBillNo + "-" + year;
		else
			return senateBillNo;
	}
	
	@JsonIgnore
	@Override
	public HashMap<String,Field> luceneFields() {
		return null;
	}
	
	@JsonIgnore
	@Override
	public String luceneSummary() {
		return summary;
	}
	
	@JsonIgnore
	@Override
	public String luceneTitle() {
		return (title == null) ? summary : title;
	}

	@JsonIgnore
	@Override public String luceneOsearch() {
		return senateBillNo.split("-")[0] + " "
		    + year + " "
		    + senateBillNo + "-" + year
			+ (sameAs != null ? " " + sameAs:"")
			+ (sponsor != null ? " " + sponsor.getFullname():"")
			+ (title != null ? " " + title:"")
			+ (summary != null ? " " + summary:"");
		
	}
	
	@JsonIgnore
	public String getLuceneCoSponsors() {
		StringBuilder response = new StringBuilder();
		for( Person sponsor : coSponsors) {
			response.append(sponsor.getFullname() + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
	
	@JsonIgnore
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
	
	@JsonIgnore
	public String getLuceneBillEvents() {
		StringBuilder response = new StringBuilder();
		for(BillEvent be : billEvents) {
			response.append(be.getEventText() + ", ");
		}
		return response.toString().replaceAll(", $", "");
	}
	
	@JsonIgnore
	public String getLuceneSponsor() {
		if(sponsor != null) {
			return sponsor.getFullname();
		}
		return "";
	}
}










