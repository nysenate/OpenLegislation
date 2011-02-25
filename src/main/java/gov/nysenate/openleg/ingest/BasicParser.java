package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.OpenLegConstants;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.bill.BillEvent;
import gov.nysenate.openleg.model.bill.Person;
import gov.nysenate.openleg.model.bill.Vote;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.util.BillCleaner;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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


import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;


public class BasicParser implements OpenLegConstants {
	private static Logger logger = Logger.getLogger(BasicParser.class);
	
	private final static DateFormat DATE_PARSER = new SimpleDateFormat ("MM/dd/yy");
	private final static char NULL_LINE_CHAR = '-';
	private int currentVoteCount = -1;
	
	private Bill currentBill = null;
	private Vote currentVote = null;
	
	private StringBuffer summaryBuffer = null;
	private StringBuffer actBuffer = null;
	private StringBuffer titleBuffer = null;
	private StringBuffer textBuffer = null;
	private StringBuffer memoBuffer = null;
	private List<BillEvent> billEventsBuffer = null;
	private List<Person> coSponsorBuffer = null;
	
	private HashMap<String,Person> personCache;
	ObjectMapper mapper;
	
	private ArrayList<Bill> returnBills = null;
	
	public BasicParser () {
		mapper = new ObjectMapper();
		SerializationConfig cnfg = mapper.getSerializationConfig();
		cnfg.set(Feature.INDENT_OUTPUT, true);
		mapper.setSerializationConfig(cnfg);
		
		returnBills = new ArrayList<Bill>();
	}
	
	public Transcript handleTranscript(String dataPath) {
		try {
			return parseTranscriptFile(new BufferedReader (new FileReader (dataPath)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		};
		return null;
	}
	
	public ArrayList<Bill> handleBill(String dataPath, char lineCodeToMatch) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(dataPath));
		parseSOBIFile (br,lineCodeToMatch);
		br.close();
		
		return returnBills;
	}
	
	public Bill getBill (String line) {
		if (line.trim().startsWith("<"))
			return null;
		
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
	
	

	public Transcript parseTranscriptFile (BufferedReader reader) throws IOException {
		Transcript transcript = new Transcript();
		StringBuffer fullText = new StringBuffer();
		StringBuffer fullTextProcessed = new StringBuffer();
		
		String pLine = null;
		int locationLineIdx = 9;
		boolean checkedLineFour = false;
		
		String line = null;
		while ((line = reader.readLine()) != null) {
			pLine = line.trim();
			
			if (pLine.startsWith("4") && (!checkedLineFour)) {
				if (pLine.indexOf("STENOGRAPHIC RECORD")==-1)
					locationLineIdx = 10;
				
				checkedLineFour = true;
			}
			else if (transcript.getLocation() == null && pLine.startsWith(locationLineIdx+" "))	{
				pLine = pLine.trim();
				
				if (pLine.length() < 3)
					locationLineIdx++; //location must be on the next line
				else {
					//9                   ALBANY, NEW YORK
					pLine = pLine.substring(2).trim();
					
					transcript.setLocation(pLine);
					logger.info("got location: " + transcript.getLocation());
				}
			}
			else if (transcript.getTimeStamp() == null && pLine.startsWith((locationLineIdx+1)+" ")) {
				// 11                    August 7, 2009
			      //  12                      10:00 a.m.
				pLine = pLine.substring(2).trim();
				
				logger.info("got day: " + pLine);
				
				String nextLine = reader.readLine();
				nextLine = reader.readLine().trim();
				nextLine = nextLine.substring(2).trim();
				
				logger.info("got time: " + nextLine);
				
				pLine += ' ' + nextLine;
				pLine = pLine.replace(".", "");
				
				try {
					Date tTime = TRANSCRIPT_DATE_PARSER.parse(pLine);
					transcript.setTimeStamp(tTime);
				} catch (ParseException e) {
					logger.warn("unable to parse transcript datetime" + pLine,e);
				}
			}
			else if (transcript.getType() == null && pLine.startsWith((locationLineIdx+5)+" "))	{
				// 15                    REGULAR SESSION
				pLine = pLine.substring(2);
				pLine = pLine.trim();
				
				transcript.setType(pLine);
			}
			
			fullText.append(line);
			fullText.append('\n');
			
			line = line.trim();
			
			if (line.length() > 2) {
				line = line.substring(2);
				fullTextProcessed.append(line);
				fullTextProcessed.append('\n');
			}
		}
		transcript.setTranscriptText(fullText.toString());
		transcript.setTranscriptTextProcessed(fullTextProcessed.toString());
				
		return transcript;
	}
	
	public void parseSOBIFile (BufferedReader reader, char lineCodeToMatch) throws IOException {
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
					break;
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
					
					if (lineCodeToMatch != NULL_LINE_CHAR //okay the value is being checked
							&& lineCodeToMatch != lineCode) {//this line isn't what we are looking for)
						continue; //skip this line
					}
					
					bill = getBill(line);
					
					if (lineCode == 'M')//memo text
						bill = parseMemoData (line);
					
					else if (lineCode == 'T')//bill text
						bill = parseTextData (line);
					
					else if (lineCode == 'R')//resolution text
						bill = parseTextData (line);
					
					else if (lineCode == 'C')
						bill = parseSummaryData(line);
						
					else if (lineCode == 'A')
						bill = parseActClause(line);
						
					else if (lineCode == 'V')
						bill = parseVoteData(line);

					else if (lineCode == 'B') {
						if(line.contains("DELETE")) {
							currentBill = null;
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
						
					else if (lineCode == '1'){ //new bill
						if (lineData.charAt(0)!='0') {
							//SOBI.D100106.T214552.TXT:2009S06415 1JOHNSON C           000000000014825020
							//SOBI.D100113.T200727.TXT:2009S06457 1SCHNEIDERMAN        00000
							int zeroIdx = lineData.indexOf("0000");
							
							if (zeroIdx!=-1) {
								if(bill.getSponsor() == null || bill.getSponsor().equals("")) {
									String sponsor = lineData.substring(0,zeroIdx).trim();
									if(!sponsor.equals("DELETE"))
										bill.setSponsor(getPerson(sponsor));
									else 
										currentBill = null;
								}
							}
						}
					}
					else if (lineCode == '6') {
						if (lineData.charAt(0)!='0') {							
							String sponsor = lineData.trim();
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
						if (beText.startsWith("REFERRED TO ")) {
							String newCommittee = beText.substring(12);
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
						else if (beTextTemp.startsWith("SUBSTITUTED FOR "))	{
							String substituted = beText.substring(16).trim();
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else {				
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
						}
						else if (beTextTemp.startsWith("SUBSTITUTED BY ")) {
							String substituted = beText.substring("SUBSTITUTED BY ".length()).trim();
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else {
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
						}
						else if(beText.contains("REPORT CAL")) {
							if(bill.getCurrentCommittee() != null 
									&& !bill.getCurrentCommittee().equals("")) {
								bill.addPastCommittee(bill.getCurrentCommittee());
							}
							bill.setCurrentCommittee(null);
						}
						
						//currently we don't want to keep track of assembly committees
						if(bill.getSenateBillNo().startsWith("A")) {
							bill.setCurrentCommittee(null);
							bill.setPastCommittees(null);
						}
					}
					else if (lineCode == '2')
						bill.setLawSection(lineData);

					else if (lineCode == 'B')
						bill.setLaw(lineData);

				}
			}
			catch (Exception e) {
				logger.warn("warning line:" + lineCount + " line=" + line + " ;",e);
			}
			
			lineCount++;
		}
		
		commitCurrentBill();
	}
	
	private void commitCurrentBill () {
		if(returnBills == null)
			returnBills = new ArrayList<Bill>();
		
		persistBuffers();
		
		int index = -1;
		if((index = returnBills.indexOf(currentBill)) != -1) {
			if(currentBill != null) 
				returnBills.get(index).merge(currentBill);
		}
		else {
			returnBills.add(currentBill);
		}
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
			
			memoBuffer.append(line);
			memoBuffer.append('\n');
		}
		return bill;
	}
	
	public Bill parseSummaryData (String line) throws IOException {
		Bill bill = getBill(line);
	
		if (line.length() > 12)	{
			line = line.substring(12);
			line = line.replace((char)0xC, ' ');

			if (summaryBuffer == null)
				summaryBuffer = new StringBuffer();
			
			summaryBuffer.append(line);
			summaryBuffer.append(' ');		
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
			line = line.replace((char)0xC, ' ');

			if (titleBuffer == null) {
				titleBuffer = new StringBuffer();
				titleBuffer.append(line);
				titleBuffer.append(' ');
			}
			else {
				if (!titleBuffer.toString().contains(line)) {
					titleBuffer.append(line);
					titleBuffer.append(' ');
				}
			}		
		}
		return bill;
	}
	
	public Bill parseSameAsData (String line) throws IOException {
		Bill bill = null;
		bill = getBill(line);
	
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
		//2009S00022 T00000.SO DOC S 22                                     BTXT                 2009
//2009S00022 T00000.SO DOC S 22            *END*                    BTXT                 2009
		Bill bill = getBill(line);
		
		String lineCode = line.substring(11,17);
		line = line.substring(17);
		
		if (line.indexOf("*END*")!=-1)	{

		}
		else if (lineCode.equals("T00000"))	{
			
		}
		else if (lineCode.equals("R00000")) {
			
		}
		else {
			line = line.replace((char)0xC, ' ');

			if (textBuffer == null)
				textBuffer = new StringBuffer();
			
			textBuffer.append(line);
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
			currentBill.setSummary(summaryBuffer.toString());
			summaryBuffer = null;
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
				e.printStackTrace();
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
	}


	public void clearBills() {
		returnBills.clear();
	}
}
