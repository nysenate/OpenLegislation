package gov.nysenate.openleg.qa;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Person;
import gov.nysenate.openleg.qa.model.FieldName;
import gov.nysenate.openleg.qa.model.LbdcFile;
import gov.nysenate.openleg.qa.model.NonMatchingField;
import gov.nysenate.openleg.qa.model.ProblemBill;
import gov.nysenate.openleg.qa.model.LbdcFile.AssociatedFields;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.SessionYear;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AssociatedFields({	FieldName.SPONSOR, 
					FieldName.COSPONSORS, 
					FieldName.ACTIONS, 
					FieldName.TITLE, 
					FieldName.SUMMARY, 
					FieldName.LAW_SECTION })
public class LbdcFileHtml extends LbdcFile {
	Pattern billP = Pattern.compile(
			"<a .+?>(.+?)</a>" + 										//bill number
			"(.+?)<br>" + 												//sponsors: (sponsor) (,(cosponsor))*
			"(.+?)<br> " + 												//title
			"<b>Primary Law: </b>(.+?)<br>" + 							//primary law
			"(?:<b>SUMM \\: </b>)?(BILL SUMMARY NOT FOUND|.+?)<br>" + 	//summary
			"(?:(Criminal Sanction Impact.)(?: <br>))?"); 				//criminal sanction impact
	Pattern actionP = Pattern.compile("(<b>(?:&nbsp;)+</b>)?(\\d{2}/\\d{2}/\\d{2}) (.+?)<br>");
	
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
	
	public LbdcFileHtml(File file) {
		super(file);
	}
	
	public ArrayList<ProblemBill> getProblemBills() {
		ArrayList<ProblemBill> ret = new ArrayList<ProblemBill>();
					
		Bill lbdcBill = null;
		
		open();
		
		while((lbdcBill = nextBill()) != null) {
			Bill luceneBill = SearchEngine.getInstance().getBill(lbdcBill.getSenateBillNo() + "-" + SessionYear.getSessionYear());
			
			if(luceneBill == null) {
				//TODO we don't have it
				continue;
			}
			else {					
				ProblemBill problemBill = new ProblemBill(luceneBill.getSenateBillNo(), luceneBill.getLuceneModified());
				
				if(valid(lbdcBill.getSponsor(), luceneBill.getSponsor())) {
					if(!cln(lbdcBill.getSponsor().getFullname()).equalsIgnoreCase(cln(luceneBill.getSponsor().getFullname()))) {
						problemBill.addNonMatchingField(
								new NonMatchingField(
										FieldName.SPONSOR, 
										luceneBill.getSponsor().getFullname(),
										lbdcBill.getSponsor().getFullname()));
					}
				}
				
				doCollectionField(problemBill, FieldName.ACTIONS, luceneBill.getBillEvents(), lbdcBill.getBillEvents());
				doCollectionField(problemBill, FieldName.COSPONSORS, luceneBill.getCoSponsors(), lbdcBill.getCoSponsors());
				doStringField(problemBill, FieldName.SUMMARY, luceneBill.getSummary(), lbdcBill.getSummary(), ".*?");
				doStringField(problemBill, FieldName.TITLE, luceneBill.getTitle(), lbdcBill.getTitle(), null);
				doStringField(problemBill, FieldName.LAW_SECTION, luceneBill.getLawSection(), lbdcBill.getLawSection(), null);
			
				if(problemBill.getNonMatchingFields() != null && problemBill.getNonMatchingFields().size() != 0)
					ret.add(problemBill);
			}
		}
					
		close();
				
		return ret;
	}
	
	/**
	 * parsing data from the LBDC html dump
	 * @return next Bill if available from the parsed HTML
	 */
	private Bill nextBill() {
		String in = null;
		StringBuffer buffer = null;
		boolean readToggle = false;
		
		while((in = er.readLine()) != null) {
			//if in matches the beginning of a new bill element
			if(in.matches("(</td></tr>)?(<tr align=\"left\"|\\Q <script>document.getElementById(\"SRCHCNT\")\\E).*$")) {
				if(buffer != null) {
					er.reset();
					return this.getBillFromHtml(buffer.toString());
				}
				
				if(in.contains("<script>document.getElementById(\"SRCHCNT\")")) {
					//this signifies the end of last bill in the table			
					break;
				}
				else {
					readToggle = true;
					buffer = new StringBuffer().append(in);
				}				
			}
			else {
				if(readToggle) {
					buffer.append(in);
				}
			}
			
			er.mark(65535);
		}
		
		return null;
	}
	
	private Bill getBillFromHtml(String text) {
		Bill bill = null;
		
		text = text.replaceAll("(</?(tr|td).+?>|<table.+?/table>)", "");
		Matcher m = billP.matcher((text));
		
		if(m.find()) {
			bill = new Bill();
			
			bill.setSenateBillNo(m.group(1).trim());
			bill.setTitle(m.group(3).trim());
			bill.setLawSection(m.group(4).trim());
			bill.setSummary(m.group(5).equals("BILL SUMMARY NOT FOUND") ? null : m.group(5).trim());
			
			String sponsorString = m.group(2).trim();
			String[] sponsorsString = sponsorString.split(", ");
			bill.setSponsor(new Person(sponsorsString[0]));
			
			if(sponsorsString.length > 1) {
				ArrayList<Person> cosponsors = new ArrayList<Person>();
				for(int i = 1; i < sponsorsString.length; i++) {
					cosponsors.add(new Person(sponsorsString[i]));
				}
				bill.setCoSponsors(cosponsors);
			}
			
			text = text.substring(m.end());
			m.usePattern(actionP).reset(text);
			
			ArrayList<BillEvent> billEvents = new ArrayList<BillEvent>();
			
			while(m.find()) {
				
				if(m.group(1) != null) {
					continue;
				}
				
				try {
					billEvents.add(new BillEvent(bill.getSenateBillNo(), sdf.parse(m.group(2)), m.group(3)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			bill.setBillEvents(billEvents);
		}
		return bill;
	}
	
	private String cln(String str) {
		return str.trim().replaceAll("  "," ");
	}
	
	private boolean valid(Object o1, Object o2) {
		if(o1 != null && o2 != null)
			return true;
		return false;
	}
	
	private void doCollectionField(ProblemBill problemBill, FieldName fieldName, Collection<?> openCol, Collection<?> lbdcCol) {
		if(valid(openCol, lbdcCol)) {
			if(lbdcCol.size() - openCol.size() > 0) {
				problemBill.addNonMatchingField(
					new NonMatchingField(
						fieldName,
						openCol.size()+"",
						lbdcCol.size()+""));
			}
		}
	}
	
	private void doStringField(ProblemBill problemBill, FieldName fieldName, String openField, String lbdcField, String pattern) {
		if(valid(openField,lbdcField)) {
			if(!cln(lbdcField).matches((pattern == null ? "" : pattern) + Pattern.quote(cln(openField)))) {
				problemBill.addNonMatchingField(
					new NonMatchingField(
						fieldName,
						openField,
						lbdcField));
			}
		}
	}
}