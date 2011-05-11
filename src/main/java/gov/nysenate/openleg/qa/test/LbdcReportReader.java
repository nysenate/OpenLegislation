package gov.nysenate.openleg.qa.test;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Person;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.SessionYear;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class LbdcReportReader {
	public static void main(String[] args) {
		LbdcReportReader reader = new LbdcReportReader("src/main/resources/senate3.html", ReportType.BILL_HTML);
		
		reader.processFile();
	}
	
	public enum ReportType { 
		BILL_HTML, MEMO
	}
	
	private Logger logger = Logger.getLogger(LbdcReportReader.class);
	private File file;
	private ReportType reportType;
	
	public LbdcReportReader(String filePath, ReportType reportType) {
		this(new File(filePath), reportType);
	}
	
	public LbdcReportReader(File file, ReportType reportType) {
		this.file = file;
		this.reportType = reportType;
	}
	
	public void processFile() {
		switch(reportType) {
			case BILL_HTML:
				processBillHtml();
				break;
			case MEMO:
				processBillMemo();
				break;
		}
	}
	
	private void processBillMemo() {
		
	}
	
	public void processBillHtml() {
		
		LbdcBillReader reader = new LbdcBillReader(file);
				
		Bill lbdcBill = null;
		
		while((lbdcBill = reader.nextBill()) != null) {
			Bill luceneBill = SearchEngine.getInstance().getBill(lbdcBill.getSenateBillNo() + "-" + SessionYear.getSessionYear());
			
			if(luceneBill == null) {
				//TODO we don't have it
				continue;
			}
			else {
				/*
				 * to compare
				 * 		this report: 	title, summary, sponsor, cosponsors, law section, billevents
				 * 		other reports:	memo existance, full text max page
				 */
				
				if(lbdcBill.getSponsor() != null && luceneBill.getSponsor() != null &&
						!lbdcBill.getSponsor().getFullname().trim().equalsIgnoreCase(luceneBill.getSponsor().getFullname())) {
					
					System.out.println("---SPONSOR---"
							+ luceneBill.getSenateBillNo()
							+ "\n" + lbdcBill.getSponsor().getFullname()
							+ "\n" + luceneBill.getSponsor().getFullname() + "\n");
				}
				
				if(luceneBill.getBillEvents() != null) {
					if(lbdcBill.getBillEvents().size() - luceneBill.getBillEvents().size()  > 0) {
						
						System.out.println(luceneBill.getSenateBillNo() 
								+ " : " + (lbdcBill.getBillEvents().size() - luceneBill.getBillEvents().size()) + "\n");
					}
				}
				
				if(lbdcBill.getSummary() != null && luceneBill.getSummary() != null
						&& !lbdcBill.getSummary().trim().replaceAll("  "," ").matches(".*?" + Pattern.quote(luceneBill.getSummary().trim().replaceAll("  "," ")))) {

					System.out.println("---SUMMARY--- " 
							+ luceneBill.getSenateBillNo() 
							+ "\n" + lbdcBill.getSummary().trim().replaceAll("  "," ") 
							+ "\n" + luceneBill.getSummary().trim().replaceAll("  "," ")  + "\n"
							/*+ l(lbdcBill.getSummary().trim().replaceAll("  "," "), luceneBill.getSummary().trim().replaceAll("  "," ")) + "\n"*/);
				}
				
				if(lbdcBill.getTitle() != null && luceneBill.getTitle() != null &&
						!lbdcBill.getTitle().trim().replaceAll("  "," ").matches(Pattern.quote(luceneBill.getTitle().trim().replaceAll("  "," ")))) {
					
					System.out.println("---TITLE--- " 
							+ luceneBill.getSenateBillNo() + "\n" 
							+ lbdcBill.getTitle().trim().replaceAll("  "," ") + "\n" 
							+ luceneBill.getTitle().trim().replaceAll("  "," ") + "\n"
							+ l(lbdcBill.getTitle().trim().replaceAll("  "," "), luceneBill.getTitle().trim().replaceAll("  "," ")) + "\n");
					
				}
				
				if(lbdcBill.getLawSection() != null && luceneBill.getLawSection() != null &&
						!lbdcBill.getLawSection().trim().matches(luceneBill.getLawSection().trim())) {
					
					System.out.println("---LAW SECTION--- " 
							+ luceneBill.getSenateBillNo() + "\n" 
							+ lbdcBill.getLawSection().trim() + "\n" 
							+ luceneBill.getLawSection().trim() + "\n"
							+ l(lbdcBill.getLawSection(), luceneBill.getLawSection()) + "\n");
				}
			}
		}
		
		reader.close();
	}
	
	 public static int l( String s, String t) {
		int n = s.length();
		int m = t.length();
	 
		if (n == 0) return m;
		if (m == 0) return n;
	 
		int[][] d = new int[n + 1][m + 1];
	 
		for ( int i = 0; i <= n; d[i][0] = i++ );
		for ( int j = 1; j <= m; d[0][j] = j++ );
	 
		for ( int i = 1; i <= n; i++ ) {
			char sc = s.charAt( i-1 );
			for (int j = 1; j <= m;j++) {
				int v = d[i-1][j-1];
				if ( t.charAt( j-1 ) !=  sc ) v++;
				d[i][j] = Math.min( Math.min( d[i-1][ j] + 1, d[i][j-1] + 1 ), v );
			}
		}
		return d[n][m];
	}
	
	public class LbdcBillReader {
		Pattern billP = Pattern.compile(
				"<a .+?>(.+?)</a>" + 										//bill number
				"(.+?)<br>" + 												//sponsors: (sponsor) (,(cosponsor))*
				"(.+?)<br> " + 												//title
				"<b>Primary Law: </b>(.+?)<br>" + 							//primary law
				"(?:<b>SUMM \\: </b>)?(BILL SUMMARY NOT FOUND|.+?)<br>" + 	//summary
				"(?:(Criminal Sanction Impact.)(?: <br>))?"); 				//criminal sanction impact
		Pattern actionP = Pattern.compile("(<b>(?:&nbsp;)+</b>)?(\\d{2}/\\d{2}/\\d{2}) (.+?)<br>");
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		
		private EasyReader er = null;
		private File file;
		
		public LbdcBillReader(String file) {
			this(new File(file));
		}
		
		public LbdcBillReader(File file) {
			this.file = file;
			if(file.exists()) {
				er = new EasyReader(file).open();
			}
		}
		
		public File getFile() {
			return file;
		}
		
		public void close() {
			er.close();
		}
		
		public Bill nextBill() {
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
	}
	
	public class EasyReader {
		private Logger logger = Logger.getLogger(EasyReader.class);
		public BufferedReader br = null;
		public File file;
		
		public EasyReader(File file) {
			this.file = file;
		}
		
		public EasyReader open() {
			try {
				this.close();
				
				br = new BufferedReader(new FileReader(file));
			} catch (IOException e) {
				logger.error(e);
			}
			return this;
		}
		
		public void close() {
			if(isOpen()) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		
		public String readLine() {
			if(isOpen()) {
				try {
					return br.readLine();
				} catch (IOException e) {
					logger.error(e);
				}
			}
			return null;
		}
		
		public void mark(int readAheadLimit) {
			if(isOpen()) {
				try {
					br.mark(readAheadLimit);
				} catch (IOException e) {
					logger.error(e);
				}
			}			
		}
		
		public void reset() {
			if(isOpen()) {
				try {
					br.reset();
				} catch (IOException e) {
					logger.error(e);
				}
			}
		}
		
		public boolean isOpen() {
			try {
				if(br != null && br.ready()) {
					return true;
				}
			} catch (IOException e) {
				logger.error(e);
			}
			return false;
		}
	}
}
