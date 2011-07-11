package gov.nysenate.openleg.ingest.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Person;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.util.BillCleaner;

public class BillParser extends SenateParser<Bill> {
	
	private final static DateFormat DATE_PARSER = new SimpleDateFormat ("MM/dd/yy");
	private int currentVoteCount = -1;
	
	private Bill currentBill = null;
	private Vote currentVote = null;
	
	private StringBuffer summaryBuffer = null;
	private StringBuffer tempSummaryBuffer = null;
	
	private StringBuffer lawBuffer = null;
	private StringBuffer tempLawBuffer = null;
	
	private StringBuffer actBuffer = null;
	private StringBuffer titleBuffer = null;
	private StringBuffer textBuffer = null;
	private StringBuffer memoBuffer = null;
	private List<BillEvent> billEventsBuffer = null;
	private List<Person> coSponsorBuffer = null;
	private List<Person> multiSponsorBuffer = null;
	
	private HashMap<String,Person> personCache;
	
	private String uniBillNumber;
	
	public BillParser() {
		super(BillParser.class);
	}

	public void parse(File file) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO-8859-1"));
			parseSOBIFile (br);
			br.close();
		}
		catch(IOException e) {
			logger.error(e);
		}
	}
	
	public Bill getBill (String line) {
		if (line.trim().startsWith("<"))
			return null;
		
		line = line.toUpperCase();
		
		int year = Integer.parseInt(line.substring(0,4));
		
		String billId = line.substring(4,11);
		
		String billType = billId.substring(0,1);
		String billRev = billId.substring(billId.length()-1).trim();
		int billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));
		
		billId = billType + billNumber + billRev;
		
		billId += "-" + year;
			
		if (currentBill != null) {
			if (currentBill.getYear()==year && currentBill.getSenateBillNo().equals(billId)) {
				//return existing instance
				return currentBill;
			}
			else {
				commitCurrentBill();
			}
		}
		
		//get new bill instance
		getBillMore(billId, year);
		
		return currentBill;
	}
	
	public void getBillMore (String billId, int year) {
		billId = billId.trim();
		
		currentBill = new Bill();
		currentBill.setSenateBillNo(billId);
		currentBill.setYear(year);
						
		if (currentBill.getYear()==0)
			currentBill.setYear(year);
	
		if (currentVote != null) {
			//if new bill doesn't match the current vote being tracked, then null it out
			if(!currentVote.getBill().getSenateBillNo().equals(currentBill.getSenateBillNo()))
				currentVote = null;
		}
	}
	
	public void parseSOBIFile(BufferedReader reader) throws IOException {
		Bill bill = null;
		int lineCount = 0;
		String lineData;
		char lineCode;
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			try	{
				if (line.startsWith("<DATAPROCESS")) {
					//SOF
				}
				else if (line.startsWith("</DATAPROCESS>"))	{
					//EOF
					commitCurrentBill();
					continue;
				}
				else if (line.startsWith("No data to process"))	{
					//do nothing
				}
				else if (line.startsWith("<?xml version= '1.0' encoding='UTF-8'?>")) {
					//do nothing
				}
				else if (line.startsWith("<")) {
					
					//do nothing
				}
				else if (line.startsWith("<SENATEDATA")) {
					//need to find the end tag and skip all of this
				}
				else if (line.length() > 11) {
					lineCode = line.charAt(11); //get the single line code letter for data type from LRS schema
					lineData = line.substring(12);
					
					try {
						bill = getBill(line);
					}
					catch (NumberFormatException nfe) {
						continue;
					}
					
					if (lineCode == '1'){ //new bill
						if(currentBill != null) {
							commitCurrentBill();
						}
						
						if (((int)lineData.charAt(0)) != 0) {
							//SOBI.D100106.T214552.TXT:2009S06415 1JOHNSON C           000000000014825020
							//SOBI.D100113.T200727.TXT:2009S06457 1SCHNEIDERMAN        00000
							int zeroIdx = lineData.indexOf("0000");
							
							if (zeroIdx!=-1) {
								if(bill.getSponsor() == null || bill.getSponsor().equals("")) {
									String sponsor = lineData.substring(0,zeroIdx).trim();
									if(!sponsor.equals("DELETE"))
										bill.setSponsor(getPerson(sponsor));
									else 
										currentBill.setSponsor(null);
								}								
							}
						}
						else {
							lineData = lineData.replaceAll("\\p{Cntrl}","");
							if(lineData.indexOf("00000", 5) == -1) {
								String billId = lineData.substring(5,12);
								int year = Integer.parseInt(lineData.substring(12,16));
								year = (year % 2 == 0 ? (year-1):year);
								
								String billType = billId.substring(0,1);
								String billRev = billId.substring(billId.length()-1).trim();
								int billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));
								
								billId = billType + billRev + billNumber + "-" + year;
								bill.addPreviousVersion(billId);
							}
							
						}
					}
					else if (lineCode == 'M')//memo text
						bill = parseMemoData (line);
					
					else if (lineCode == 'T') { //bill text
						if(line.contains("*DELETE*")) {
							currentBill.setFulltext("*DELETE*");
						}
						else {
							bill = parseTextData (line);
						}
					}
					
					else if (lineCode == 'R')//resolution text
						bill = parseTextData (line);
					
					else if (lineCode == 'C')
						bill = parseSummaryData(line);
						
					else if (lineCode == 'A')
						bill = parseActClause(line);
						
					else if (lineCode == 'V')
						bill = parseVoteData(line);
					/*
					 * TODO
					 * 
					 * B means delete summary/law
					 */
					else if (lineCode == 'B') {
						if(line.contains("DELETE")) {
							//delete code
							persistBuffers();
							newSenateObjects.remove(currentBill);
							currentBill = null;
						}
						else {
							parseLawData(line);
						}
					}
					else if (lineCode == 'N') {
						
					}
					else if (lineCode == 'D') {
						
					}
					else if (lineCode == '5')
						bill = parseSameAsData(line);
						
					else if (lineCode == '3')
						bill = parseTitle(line);
						
					else if (lineCode == '6') {
						if (lineData.charAt(0)!='0') {							
							String sponsor = lineData.trim();
							if(!sponsor.equals("DELETE"))
								bill.setSponsor(getPerson(sponsor));
						}
					}
					else if (lineCode == '7') {
						if (lineData.length() > 0 && lineData.charAt(0)!='0') {
							if (coSponsorBuffer == null)
								coSponsorBuffer = new ArrayList<Person>();
							
							String cosponsor = lineData.trim();
							
							StringTokenizer st = new StringTokenizer(cosponsor,",");
							while(st.hasMoreTokens()) {
								Person coSponsor = getPerson(st.nextToken().trim());
								if(!coSponsorBuffer.contains(coSponsor))
									coSponsorBuffer.add(coSponsor);
							}
						}
					}
					else if (lineCode == '8') {
						if (lineData.length() > 0 && lineData.charAt(0)!='0') {
							if (multiSponsorBuffer == null)
								multiSponsorBuffer = new ArrayList<Person>();
							
							String multisponsor = lineData.trim();
							
							StringTokenizer st = new StringTokenizer(multisponsor,",");
							while(st.hasMoreTokens()) {
								Person multiSponsor = getPerson(st.nextToken().trim());
								if(!multiSponsorBuffer.contains(multiSponsor))
									multiSponsorBuffer.add(multiSponsor);
							}
						}
					}
					else if (lineCode == '4') {
						Date beDate = DATE_PARSER.parse(lineData.substring(0,8));
						
						String beText = lineData.substring(9);
						
						if (billEventsBuffer == null)
							billEventsBuffer = new ArrayList<BillEvent>();
						
						BillEvent bEvent = new BillEvent(bill, beDate, beText);
						
						Calendar c = Calendar.getInstance();
						c.setTime(beDate);
						
						/*
						 * this fixes instances where two identical events occur
						 * on the same day, in the past the second instance
						 * was left out
						 */
						while(billEventsBuffer.contains(bEvent)) {							
							c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);
							
							bEvent = new BillEvent(bill, c.getTime(), beText);
						}
						
						/*
						 * preserves ordering of billevents that occur on 
						 * the same day, otherwise order is at the mercy of
						 * the jvm higher ups
						 */
						for(BillEvent be:billEventsBuffer) {
							if(be.getEventDate().equals(bEvent.getEventDate())) {
								c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 1);
								bEvent = new BillEvent(bill, c.getTime(), beText);
							}
						}
						
						billEventsBuffer.add(bEvent);						
						
						String beTextTemp = beText.toUpperCase();
						if (beText.indexOf("REFERRED TO ")!=-1) {
							int subIdx = beText.indexOf("REFERRED TO ") + 12;
							String newCommittee = beText.substring(subIdx).trim();
							if(bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) {
								bill.addPastCommittee(bill.getCurrentCommittee());
							}
							bill.setCurrentCommittee(newCommittee);
						}
						else if (beText.indexOf("COMMITTED TO ")!=-1) {
							int subIdx = beText.indexOf("COMMITTED TO ") + 13; 
							String newCommittee = beText.substring(subIdx).trim();
							if(bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) {
								bill.addPastCommittee(bill.getCurrentCommittee());
							}
							bill.setCurrentCommittee(newCommittee);
						}
						else if (beText.indexOf("RECOMMIT TO ")!=-1) {
							int subIdx = beText.indexOf("RECOMMIT TO ") + 12; 
							String newCommittee = beText.substring(subIdx).trim();
							if(bill.getCurrentCommittee() != null && !bill.getCurrentCommittee().equals("")) {
								bill.addPastCommittee(bill.getCurrentCommittee());
							}
							bill.setCurrentCommittee(newCommittee);
						}
						else if(beText.contains("REPORT CAL") 
								|| beText.contains("THIRD READING") 
								|| beText.contains("RULES REPORT")) {
							
							if(bill.getCurrentCommittee() != null 
									&& !bill.getCurrentCommittee().equals("")) {
								bill.addPastCommittee(bill.getCurrentCommittee());
							}
							bill.setCurrentCommittee(null);
						}
						else if (beTextTemp.startsWith("SUBSTITUTED FOR "))	{
							String substituted = beText.substring(16).trim().toUpperCase();
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else {				
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
						}
						else if (beTextTemp.startsWith("SUBSTITUTED BY ")) {
							String substituted = beText.substring("SUBSTITUTED BY ".length()).trim().toUpperCase();
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else {
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
						}
						
						if (beTextTemp.contains("ENACTING CLAUSE STRICKEN")) {
							bill.setStricken(true);
						}
						else {
							bill.setStricken(false);
						}
						
						
						//currently we don't want to keep track of assembly committees
						if(bill.getSenateBillNo().startsWith("A")) {
							bill.setCurrentCommittee(null);
							bill.setPastCommittees(null);
						}
					}
					else if (lineCode == '2')
						bill.setLawSection(lineData);
				}
			}
			catch (Exception e) {
				logger.error("warning line:" + lineCount + " line=" + line + " ;",e);
			}
			
			lineCount++;
		}
		commitCurrentBill();
	}
	
	private void commitCurrentBill () {
		if(currentBill == null)
			return;
		
		persistBuffers();
				
		int index = -1;
		
		if((index = newSenateObjects.indexOf(currentBill)) != -1)
			newSenateObjects.get(index).merge(currentBill);
		else
			newSenateObjects.add(currentBill);
	}
	
	public Bill parseMemoData (String line) throws IOException {
		Bill bill = getBill(line);
	
		String lineCode = line.substring(11,17);
		line = line.substring(17);
		
		if (line.indexOf("*END*")!=-1) {
			//do nothing
		}
		else if (lineCode.equals("M00000"))	{
			
		}
		else
		{
			line = line.replace((char)0xC, ' ');

			if (memoBuffer == null)
				memoBuffer = new StringBuffer();
			
			memoBuffer.append(line.replaceAll("\\xa7", "&sect;").replaceAll("\\xDF", "&sect;"));
			memoBuffer.append('\n');
		}
		
		return bill;
	}
	
	public Bill parseSummaryData (String line) throws IOException {
		Bill bill = getBill(line);
	
		if (line.length() > 12)	{
			line = line.substring(12);
			line = line.replace((char)0xC, ' ').replaceAll("\\x27(\\W|\\s)", "&apos;$1")
											   .replaceAll("›","S");

			if (summaryBuffer == null)
				summaryBuffer = new StringBuffer();
			
			/*
			 * there is a recurring problem with summaries being
			 * duplicated in the same activity stream, this attempts to avoid
			 * appending a duplicated summary
			 * 
			 */
			if(summaryBuffer.toString().trim().contains(line)) {
				if(tempSummaryBuffer == null) {
					tempSummaryBuffer = new StringBuffer();
				}
				
				tempSummaryBuffer.append(line);
				tempSummaryBuffer.append(' ');
				
				if(summaryBuffer.equals(tempSummaryBuffer)) {
					tempSummaryBuffer = null;
				}
			}
			else {
				/*
				 * the logic for bill summaries tries to not add duplicate lines,
				 * but occasionally there ARE duplicate lines in real summaries
				 */
				if(tempSummaryBuffer != null) {
					summaryBuffer.append(tempSummaryBuffer);
					tempSummaryBuffer = null;
				}
				
				summaryBuffer.append(line);
				summaryBuffer.append(' ');
			}
		}
		return bill;
	}
	
	public Bill parseLawData (String line) throws IOException {
		Bill bill = getBill(line);
	
		if (line.length() > 12)	{
			line = line.substring(12);
			line = line.replace((char)0xC, ' ');
			
			line = line.replaceAll("(›|•À)", "S").replaceAll("\\xBD","");

			if (lawBuffer == null)
				lawBuffer = new StringBuffer();
			
			if(lawBuffer.toString().trim().contains(line)) {
				if(tempLawBuffer == null) {
					tempLawBuffer = new StringBuffer();
				}
				
				tempLawBuffer.append(line);
				tempLawBuffer.append(' ');
				
				if(lawBuffer.equals(tempLawBuffer)) {
					tempLawBuffer = null;
				}
			}
			else {
				if(tempLawBuffer != null) {
					lawBuffer.append(tempLawBuffer);
					tempLawBuffer = null;
				}
				
				lawBuffer.append(line);
				lawBuffer.append(' ');
			}
		}
		return bill;
	}
	
	public Bill parseActClause (String line) throws IOException {
		Bill bill = getBill(line);
	
		if (line.length() > 12)	{
			line = line.substring(12);
			line = line.replace((char)0xC, ' ');

			if (actBuffer == null)
				actBuffer = new StringBuffer();
			
			actBuffer.append(line);
			actBuffer.append(' ');		
		}
		return bill;
	}
	
	public Bill parseTitle (String line) throws IOException	{
		Bill bill = getBill(line);
	
		if (line.length() > 12)	{
			line = line.substring(12);
			line = line.replace((char)0xC, ' ')
					.replaceAll("\\x27(\\W|\\s)", "&apos;$1")
					.replaceAll("›","S");

			if (titleBuffer == null) {
				titleBuffer = new StringBuffer();
				titleBuffer.append(line);
				titleBuffer.append(' ');
			}
			else {
				//if (!titleBuffer.toString().contains(line)) {
					titleBuffer.append(line);
					titleBuffer.append(' ');
				//}
			}		
		}
		return bill;
	}
	
	public Bill parseSameAsData (String line) throws IOException {
		Bill bill = null;
		bill = getBill(line);
		
		if(line.contains("DELETE")) {
			bill.setSameAs(null);
			return bill;
		}
	
		int lineCode = Integer.parseInt(line.substring(11,12));
		
		line = line.substring(12).trim();

		if (lineCode == 5) {
			
			String sameAsBillNo = null;
			
			if (line.startsWith("Same as Uni. ")) {
				line = line.substring("Same as Uni. ".length());
				sameAsBillNo = line.trim();
			}
			else if (line.startsWith("Same as ")) {
				line = line.substring("Same as ".length());
				sameAsBillNo = line.trim();
			}
			
			if (sameAsBillNo != null) {
				sameAsBillNo = sameAsBillNo.replace(" ","");
				sameAsBillNo = sameAsBillNo.replace("-","");
				sameAsBillNo = sameAsBillNo.replace(".","");
				sameAsBillNo = sameAsBillNo.replace(";","");
				sameAsBillNo = sameAsBillNo.replace("/",",");
				
				bill.setSameAs(sameAsBillNo);
			}
		}
		return bill;
	}
	
	public Bill parseTextData (String line) throws IOException {
				
		Bill bill = getBill(line);
		
		String lineCode = line.substring(11,17);
		line = line.substring(17);
		
		if (line.indexOf("*END*")!=-1)	{

		}
		//2011S05388 T00000.SO DOC C 5388/7728                              BTXT                 2011
		else if (lineCode.equals("T00000"))	{
			Pattern p = Pattern.compile("^\\.SO DOC C \\d+[a-zA-Z]?/(\\d+[a-zA-Z]?)\\s.*$");
			Matcher m = p.matcher(line);
			if(m.find()) {
				uniBillNumber = "A" + m.group(1).toUpperCase() + "-" + currentBill.getYear();
			}
		}
		else if (lineCode.equals("R00000")) {
			
		}
		else {
			line = line.replace((char)0xC, ' ');

			if (textBuffer == null)
				textBuffer = new StringBuffer();
			
			textBuffer.append(line.replaceAll("\\xa7", "&sect;").replaceAll("\\xDF", "&sect;"));
			textBuffer.append('\n');
			
		}
		return bill;
	}
	
	public Bill parseVoteData (String line) throws IOException {
		StringTokenizer st = new StringTokenizer(line.substring(12)," ");
		String token = st.nextToken();
		
		if (token.equals("Senate"))	{
			currentVoteCount = 0;
			
			Date voteDate = null;
			int ayeCount = -1;
			int nayCount = -1;
			
			//create new vote
			st.nextToken();//Vote
			st.nextToken();//Bill:
			st.nextToken();//Sxxx
			st.nextToken();//Date:
			
			try	{
				voteDate = DATE_PARSER.parse(st.nextToken());//Date value
			}
			catch (ParseException pe) {
				logger.warn("error parsing vote date",pe);
			}
			
			st.nextToken();//Aye
			st.nextToken();//-
			
			ayeCount = Integer.parseInt(st.nextToken());//Aye Count #
			
			st.nextToken();//Nay
			st.nextToken();//-
			
			nayCount = Integer.parseInt(st.nextToken());//Nay Count #
			
			if(currentVote == null) {
				currentVote = new Vote(currentBill, voteDate, ayeCount, nayCount);
				logger.info("CREATED NEW VOTE INSTANCE: " + currentVote.getId());
			}
			
			currentVote.setAyes(new ArrayList<String>());
			currentVote.setNays(new ArrayList<String>());
			currentVote.setAbstains(new ArrayList<String>());
			currentVote.setExcused(new ArrayList<String>());
			currentVote.setVoteType(Vote.VOTE_TYPE_FLOOR);
			
			logger.info("tracking vote: " + currentVote.getId());
			
		}
		else {
			//add to existing vote
			String vote = token;
			String voter = null;
			Person person = null;
			String nextToken = null;
			
			while (st.hasMoreTokens())
			{
				voter = st.nextToken();
				
				//TODO
				//need to generalize these rules and make them configurable
				if (voter.equals("Hassell-Thompso"))
				{
					voter = "Hassell-Thompson";
				}
				else if (voter.equals("Johnson")) //something Johnson, Johnson C, or Johnson O
				{	
					nextToken = st.nextToken();
					
					if (nextToken.length() == 1) {
						voter = voter + ' ' + nextToken;
						nextToken = null;
					}
				
				}
				
				//TODO get person?
				//2011/person should be written via services used with current json
				/*person = PMF.getPerson(persistenceManager,voter);
				if (person == null)	{
					logger.info("couldn't find voter: " + voter);
						
					continue;
					
				}*/
				person = new Person();
				person.setFullname(voter);
				
				currentVoteCount++;
				if (vote.equalsIgnoreCase("Aye") || vote.equalsIgnoreCase("Yea"))
					currentVote.addAye(person);
				else if (vote.equalsIgnoreCase("Nay"))
					currentVote.addNay(person);
				else if (vote.equalsIgnoreCase("Abs"))
					currentVote.addAbstain(person);
				else if (vote.equalsIgnoreCase("Exc"))
					currentVote.addExcused(person);
				
				if (nextToken != null) {
					vote = nextToken;
					nextToken = null;
				}
				else if (st.hasMoreTokens())
					vote = st.nextToken();
			}
		}
		return currentBill;
	}
	
	public Person getPerson(String name) {
		if(name.equalsIgnoreCase("delete")) {
			return null;
		}
		
		if(personCache == null) {
			personCache = new HashMap<String,Person>();
		}
		
		Person person = null;
		if((person = personCache.get(name)) != null) {
			return person;
		}
		
		person = new Person();
		person.setFullname(name);
		
		personCache.put(name, person);
		
		return person;
	}
	
	private void persistBuffers () {
		if(currentBill == null)
			return;
		
		if (summaryBuffer != null) {
			if(tempSummaryBuffer != null) {
				String temp = tempSummaryBuffer.toString();
				if(!temp.contains(summaryBuffer)
						&& !tempSummaryBuffer.equals(summaryBuffer)) {
					summaryBuffer.append(temp);
				}
			}
			
			currentBill.setSummary(summaryBuffer.toString().trim());			
			summaryBuffer = null;
			tempSummaryBuffer = null;
		}
		
		if (lawBuffer != null) {
			currentBill.setLaw(lawBuffer.toString());			
			lawBuffer = null;
			tempLawBuffer = null;
		}
		
		if (titleBuffer != null) {
			String newTitle = titleBuffer.toString().trim();
			currentBill.setTitle(newTitle);
			titleBuffer = null;
		}
		
		if (actBuffer != null) {
			currentBill.setActClause(actBuffer.toString());
			actBuffer = null;
		}
		
		if (currentVote != null) {
			currentBill.setVotes(new ArrayList<Vote>());
			currentBill.addVote(currentVote);
		}
		
		if (textBuffer != null) {
			try {
				//there was an encoding issue with jackson, forcing conversion to utf-8
				currentBill.setFulltext(new String(textBuffer.toString().getBytes("UTF-8"),"UTF-8"));
			} catch (UnsupportedEncodingException e) {
				logger.error(e);
			}
			
			if(uniBillNumber != null) {
				Bill temp = new Bill();
				temp.setSenateBillNo(new String(uniBillNumber));
				temp.setYear(currentBill.getYear());
				temp.setFulltext(currentBill.getFulltext());
				
				this.addNewSenateObject(temp);
				
				System.out.println(temp.getSenateBillNo());
				
				uniBillNumber = null;
			}
			
			textBuffer = null;
		}

		if (memoBuffer != null) {

			currentBill.setMemo(memoBuffer.toString());
			memoBuffer = null;
		}
		
		if (billEventsBuffer != null) {
			currentBill.setBillEvents(billEventsBuffer);			
			billEventsBuffer = null;
		}
		
		if (coSponsorBuffer != null) {
			currentBill.setCoSponsors(coSponsorBuffer);
			coSponsorBuffer = null;
		}
		
		if(multiSponsorBuffer != null) {
			currentBill.setMultiSponsors(multiSponsorBuffer);
			multiSponsorBuffer = null;
		}
	}


}
