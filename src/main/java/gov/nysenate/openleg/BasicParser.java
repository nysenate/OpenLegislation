package gov.nysenate.openleg;


import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.BillEvent;
import gov.nysenate.openleg.model.Person;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.util.BillCleaner;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;

public class BasicParser implements OpenLegConstants {

	private static Logger logger = Logger.getLogger(BasicParser.class);
	
	private final static DateFormat DATE_PARSER = new SimpleDateFormat ("MM/dd/yy");
	
	private final static char NULL_LINE_CHAR = '-';
	
	LinkedHashMap<String, Bill> billMap = new LinkedHashMap<String, Bill>();
	
	private Bill currentBill = null;
	private Vote currentVote = null;
	
	private StringBuffer summaryBuffer = null;
	private StringBuffer actBuffer = null;
	private StringBuffer titleBuffer = null;
	private StringBuffer textBuffer = null;
	private StringBuffer memoBuffer = null;
	private List<BillEvent> billEventsBuffer = null;
	private List<Person> coSponsorBuffer = null;
	
	private int currentVoteCount = -1;
	
	private Transaction currentTx = null;
	private PersistenceManager persistenceManager = null;
	
	private SearchEngine2 searchEngine = null;
	private ArrayList<LuceneObject> objectsToUpdate = new ArrayList<LuceneObject>();
	
	
	public static void main (String[] args) throws FileNotFoundException, IOException
	{
		String fileType = args[0];
		String filePath = args[1];
		char lineCodeToMatch = NULL_LINE_CHAR;
		
		BasicParser bp = new BasicParser();
		
		if (args.length > 2)
		{
			lineCodeToMatch = args[2].charAt(0);
		}
		
		File file = new File (filePath);
		
		if (file.isDirectory())
		{
			File[] files = file.listFiles();
			
			
			for (int i = 0; i < files.length; i++)
			{
				bp.handleFile (fileType,files[i].getAbsolutePath(),lineCodeToMatch);
			}
		}
		else
		{
			bp.handleFile (fileType,filePath,lineCodeToMatch);
		}
		

		
		//"/Users/nathan/Desktop/NYSS/LBDCSOBIS1THRUS4999.TXT"
	}
	
	
	
	public static File[] sortFilesByLastModDate(File[] fList, String order) {
		
		Arrays.sort(fList, new Comparator() {
			
			public int compare(Object file1, Object file2) {
				if ("desc".equals("order")) {
					return (int)(((File)file2).lastModified() - ((File)file1).lastModified());
				}
				else {
					return (int)(((File)file1).lastModified() - ((File)file2).lastModified());
				}
			}
			
		});
		
		return fList;
	}

	
	public BasicParser ()
	{
		persistenceManager = PMF.getPersistenceManager();
		searchEngine = new SearchEngine2();
	}
	
	public PersistenceManager getPersistenceManager ()
	{
		return persistenceManager;
	}
	
	public void handleFile (String fileType, String dataPath, char lineCodeToMatch) throws FileNotFoundException, IOException
	{
		
		if (fileType.equals("sobi"))
		{
			parseSOBIFile (new BufferedReader (new FileReader (dataPath)),lineCodeToMatch);
		}
		else if (fileType.equals("transcript"))
		{
			logger.info("parsing transcript: " + dataPath);
			
			try
			{
				currentTx = persistenceManager.currentTransaction();
				 
				if(!currentTx.isActive()) {
			        currentTx.begin();

				}
				
				
				parseTranscriptFile(new BufferedReader (new FileReader (dataPath)));
				
				searchEngine.indexSenateObjects(objectsToUpdate, new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
				objectsToUpdate.clear();
				
				currentTx.commit();
			}
			catch (Exception e)
			{
				currentTx.rollback();
			}
			
		}
		else if (fileType.equals("sort"))
		{
			PMF.resetBillSortIdx();
		}
		else if (fileType.equals("optimize"))
		{
			searchEngine.optimize();
		}
		
	
	}
	
	
	
	public Bill getBill (String line)
	{
		if (line.trim().startsWith("<"))
			return null;
		
		int year = Integer.parseInt(line.substring(0,4));
		
		String billId = line.substring(4,11);
		
		String billType = billId.substring(0,1);
		String billRev = billId.substring(billId.length()-1).trim();
		int billNumber = Integer.parseInt(billId.substring(1,billId.length()-1));
		
		billId = billType + billNumber + billRev;
		
		if (year != 2009)
			billId += "-" + year;
			
		if (currentBill != null)
		{
			if (currentBill.getYear()==year && 
					currentBill.getSenateBillNo().equals(billId)
					)
			{
				//return existing instance
				return currentBill;
			}
			else
			{

				//if requesting a new bill, persist the existing bill first
				commitCurrentBill();
				
				
				
				
			
				
				
			}
		}
		
		//get new bill instance
		getBillMore(billId, year);
		
		return currentBill;
	}
	
	public void getBillMore (String billId, int year)
	{
		
		currentTx = persistenceManager.currentTransaction();
		
		if(!currentTx.isActive()) {
	        currentTx.begin();
		}
        
		billId = billId.trim();
		
		currentBill = PMF.getBill(persistenceManager,billId,year);
		
		if (currentBill == null)
		{
			currentBill = new Bill();
			
			currentBill.setSenateBillNo(billId);
			
			currentBill.setYear(year);
			setSortIndex();
			
			currentBill = (Bill)PMF.makePersistent(persistenceManager,currentBill);
			
		}
		else if (currentBill.getYear()==0)
			currentBill.setYear(year);
	
		if (currentVote != null)
		{
			//if new bill doesn't match the current vote being tracked, then null it out
			if(!currentVote.getBill().getSenateBillNo().equals(currentBill.getSenateBillNo()))
			{
				currentVote = null;
			}
	
		}
	}
	
	
	/*
	 * 2009S00100 M00000.SO DOC S 100                                    MTXT                 2009
2009S00100 M00001 BILL NUMBER:  S100
2009S00100 M00002
2009S00100 M00003 TITLE OF BILL :
2009S00100 M00004An act to amend the alcoholic beverage control law, in relation to a
2009S00100 M00005license to sell liquor at retail for consumption on certain premises
2009S00100 M00006
2009S00100 M00007
2009S00100 M00008 PURPOSE OR GENERAL IDEA OF BILL :
2009S00100 M00009This bill would allow the Tropical Paradise restraint to obtain a
2009S00100 M00010liquor license.
2009S00100 M00011
2009S00100 M00012 SUMMARY OF SPECIFIC PROVISIONS :
2009S00100 M00013This bill amends the alcoholic beverage control law, allowing issuance
2009S00100 M00014of a retail license for on-premises consumption of alcohol for
2009S00100 M00015premises within 200 feet of a place of worship, provided that the
2009S00100 M00016purpose of such premises is for the sale and consumption of food or
2009S00100 M00017beverage, and such premises is within the boundaries of the borough of
2009S00100 M00018Brooklyn as directed therein.
2009S00100 M00019
2009S00100 M00020 JUSTIFICATION :
2009S00100 M00021While the alcoholic beverage control law prohibits the sale for
2009S00100 M00022on-premises consumption of consumption of alcohol at any location
2009S00100 M00023within 200 feet of a place of worship, exceptions have been made for
2009S00100 M00024certain establishments that do not pose problems for the place of
2009S00100 M00025worship or the surrounding neighborhood. This bill would exempt the
2009S00100 M00026Tropical Paradise restaurant in New York City from this provision
2009S00100 M00027allowing sale and consumption of alcohol in the restaurant.
2009S00100 M00028
2009S00100 M00029 FISCAL IMPLICATIONS FOR STATE AND LOCAL GOVERNMENTS :
2009S00100 M00030None.
2009S00100 M00031
2009S00100 M00032 EFFECTIVE DATE :
2009S00100 M00033This act shall take effect immediately.
2009S00100 M00000.SO DOC S 100           *END*                    MTXT                 2009

	 */
	
	
	/*
	 *         10                   ALBANY, NEW YORK

        11                    August 7, 2009

        12                      10:00 a.m.


        13

        14


        15                    REGULAR SESSION

	 */
	public Transcript parseTranscriptFile (BufferedReader reader) throws IOException
	{
		
		
        
		String line = null;
		Transcript transcript = new Transcript();
		
		StringBuffer fullText = new StringBuffer();
		StringBuffer fullTextProcessed = new StringBuffer();
		
		String pLine = null;
		
		int locationLineIdx = 9;
		boolean checkedLineFour = false;
		
		while ((line = reader.readLine()) != null)
		{
			pLine = line.trim();
			
			if (pLine.startsWith("4") && (!checkedLineFour))
			{
				if (pLine.indexOf("STENOGRAPHIC RECORD")==-1)
				{
					locationLineIdx = 10;
				}
				
				checkedLineFour = true;
			}
			else if (transcript.getLocation() == null && pLine.startsWith(locationLineIdx+" "))
			{
				pLine = pLine.trim();
				
				if (pLine.length() < 3)
				{
					locationLineIdx++; //location must be on the next line
				}
				else
				{
					//9                   ALBANY, NEW YORK
					pLine = pLine.substring(2).trim();
					
					transcript.setLocation(pLine);
					logger.info("got location: " + transcript.getLocation());
				}
			}
			else if (transcript.getTimeStamp() == null && pLine.startsWith((locationLineIdx+1)+" "))
			{
				
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
			else if (transcript.getType() == null && pLine.startsWith((locationLineIdx+5)+" "))
			{
				// 15                    REGULAR SESSION
				pLine = pLine.substring(2);
				pLine = pLine.trim();
				
				transcript.setType(pLine);
			}
			
			fullText.append(line);
			fullText.append('\n');
			
			line = line.trim();
			
			if (line.length() > 2)
			{
				line = line.substring(2);
				fullTextProcessed.append(line);
				fullTextProcessed.append('\n');
			}
		}
		
		transcript.setTranscriptText(fullText.toString());
		transcript.setTranscriptTextProcessed(fullTextProcessed.toString());
		
		objectsToUpdate.add(transcript);
		
		PMF.makePersistent(persistenceManager,transcript);
		
		return transcript;
	}
	
	public void parseSOBIFile (BufferedReader reader, char lineCodeToMatch) throws IOException
	{
		
		String line = null;
		Bill bill = null;
		char lineCode;

		String lineData;
		
		int lineCount = 0;
		
		
		while ((line = reader.readLine()) != null)
		{
			try
			{
				
				if (line.startsWith("<DATAPROCESS"))
				{
					//SOF
				}
				else if (line.startsWith("</DATAPROCESS>"))
				{
					//EOF
					commitCurrentBill ();
					break;
				}
				else if (line.startsWith("No data to process"))
				{
					//do nothing
				}
				else if (line.startsWith("<?xml version= '1.0' encoding='UTF-8'?>"))
				{
					
					//do nothing
				}
				else if (line.startsWith("<"))
				{
					
					//do nothing
				}
				else if (line.startsWith("<SENATEDATA"))
				{
					//need to find the end tag and skip all of this
				}
				else if (line.length() > 11)
				{
					lineCode = line.charAt(11); //get the single line code letter for data type from LRS schema
					lineData = line.substring(12);
					
					if (lineCodeToMatch != NULL_LINE_CHAR //okay the value is being checked
							&& lineCodeToMatch != lineCode) //this line isn't what we are looking for)
					{
						continue; //skip this line
					}
					
					bill = getBill(line);
					
					if (lineCode == 'M')//memo text
					{
						bill = parseMemoData (line);
					}
					else if (lineCode == 'T')//bill text
					{
						
						bill = parseTextData (line);
						
					}
					else if (lineCode == 'R')//resolution text
					{
						
						bill = parseTextData (line);
						
					}
					else if (lineCode == 'C')
					{
						bill = parseSummaryData(line);
						
					}
					else if (lineCode == 'A')
					{
						bill = parseActClause(line);
						
					}
					else if (lineCode == 'V')
					{
						bill = parseVoteData(line);
					}
					else if (lineCode == 'B')
					{
						
					}
					else if (lineCode == 'N')
					{
						
					}
					else if (lineCode == 'D')
					{
						
					}
					else if (lineCode == '5')
					{
						bill = parseSameAsData(line);
						
					}
					else if (lineCode == '3')
					{
						bill = parseTitle(line);
						
					}
					else if (lineCode == '1') //new bill
					{
						if (lineData.charAt(0)!='0')
						{
							//SOBI.D100106.T214552.TXT:2009S06415 1JOHNSON C           000000000014825020
							//SOBI.D100113.T200727.TXT:2009S06457 1SCHNEIDERMAN        00000

							int zeroIdx = lineData.indexOf("0000");
							
							if (zeroIdx!=-1)
							{
								String sponsor = lineData.substring(0,zeroIdx).trim();
								Person pSponsor = PMF.getPerson(persistenceManager,sponsor);
								
								bill.setSponsor(pSponsor);
							}
						}
					}
					else if (lineCode == '6')
					{
						if (lineData.charAt(0)!='0')
						{
							String sponsor = lineData.trim();
							Person pSponsor = PMF.getPerson(persistenceManager,sponsor);
							bill.setSponsor(pSponsor);
						}
					}
					else if (lineCode == '7')
					{
						
						if (lineData.length() > 0 && lineData.charAt(0)!='0')
						{
							if (coSponsorBuffer == null)
								coSponsorBuffer = new ArrayList<Person>();
							
							String cosponsor = lineData.trim();
							
							StringTokenizer st = new StringTokenizer(cosponsor,",");
							
							while(st.hasMoreTokens())
							{
								Person person = PMF.getPerson(persistenceManager, st.nextToken().trim());
								coSponsorBuffer.add(person);
								
							}
							
						}
					}
					else if (lineCode == '4')
					{
						Date beDate = DATE_PARSER.parse(lineData.substring(0,8));
						
						String beText = lineData.substring(9);
						
						if (billEventsBuffer == null)
							billEventsBuffer = new ArrayList<BillEvent>();
						
						BillEvent bEvent = new BillEvent(bill, beDate, beText);
						billEventsBuffer.add(bEvent);						
						
						if (beText.startsWith("REFERRED TO "))
						{
							String newCommittee = beText.substring(12);
							bill.setCurrentCommittee(newCommittee);
						}
						else if (beText.indexOf("COMMITTED TO ")!=-1)
						{
							int subIdx = beText.indexOf("COMMITTED TO ") + 13; 
							String newCommittee = beText.substring(subIdx).trim();
							bill.setCurrentCommittee(newCommittee);
						}
						else if (beText.indexOf("RECOMMIT TO ")!=-1)
						{
							int subIdx = beText.indexOf("RECOMMIT TO ") + 12; 
							String newCommittee = beText.substring(subIdx).trim();
							bill.setCurrentCommittee(newCommittee);
						}
						else if (beText.startsWith("SUBSTITUTED FOR "))
						{
							String substituted = beText.substring(16).trim();
						//	bill.setSubstitutedFor(substituted);
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else
							{								
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
							
						}
						else if (beText.startsWith("SUBSTITUTED BY "))
						{
							String substituted = beText.substring("SUBSTITUTED BY ".length()).trim();
						//	bill.setSubstitutedBy(substituted);
							
							if (bill.getSameAs()==null)
								bill.setSameAs(substituted);
							else
							{
								String sameAs = BillCleaner.formatSameAs(bill.getSameAs(),substituted);
								
								bill.setSameAs(sameAs);
							}
						}
					}
					else if (lineCode == '2')
					{
						bill.setLawSection(lineData);
					}
					else if (lineCode == 'B')
					{
						bill.setLaw(lineData);
					}
				}
				
			
			}
			catch (Exception e)
			{
				logger.warn("warning line:" + lineCount + " line=" + line + " ;",e);
			
			}
			
			lineCount++;
		}
		
		commitCurrentBill();
		
	}
	
	private void commitCurrentBill ()
	{
		
		if (currentBill != null && currentTx != null)
		{
			setSortIndex ();
			persistBuffers();
			
			
			try
			{
				logger.info("committing current bill: " + currentBill.getSenateBillNo());
				objectsToUpdate.add(persistenceManager.detachCopy(currentBill));
				
			}
			catch (Exception ioe)
			{
				logger.warn("error with bill detach: " + currentBill.getSenateBillNo(),ioe);
			}

			try
			{
				logger.info("indexing current bill: " + currentBill.getSenateBillNo());
				searchEngine.indexSenateObjects(objectsToUpdate, new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
				
			}
			catch (Exception ioe)
			{
				logger.warn("error with indexing: " + currentBill.getSenateBillNo(),ioe);
			}
			
			objectsToUpdate.clear();
			
			try
			{
				currentTx.commit();
				logger.info("updated bill: " + currentBill.getSenateBillNo());
			}
			catch (Exception e)
			{
				logger.warn("unable to commit tx for bill: " + currentBill.getSenateBillNo(),e);
			}
			
			
			currentBill = null;
			currentVote = null;
			currentVoteCount = -1;
			currentTx = null;
		}
	}
	/*
	 * 2009S00100 1SAMPSON                                                                                           
2009S00100 2Alcoholic Beverage Control Law
2009S00100 3Authorizes the state liquor authority to issue a retail license for on-premises consumption of
2009S00100 3alcoholic beverages for certain premises
2009S00100 401/07/09 REFERRED TO COMMERCE, ECONOMIC DEVELOPMENT AND SMALL BUSINESS
2009S00100 404/13/09 AMEND AND RECOMMIT TO COMMERCE, ECONOMIC DEVELOPMENT AND SMALL BUSINESS

	 */

	
	
	public Bill parseMemoData (String line) throws IOException
	{
		//2009S00021 M00000.SO DOC S 21                                     MTXT                 2009
//2009S00021 M00000.SO DOC S 21            *END*                    MTXT                 2009

			
		Bill bill = getBill(line);
	
		String lineCode = line.substring(11,17);
		line = line.substring(17);
		
		if (line.indexOf("*END*")!=-1)
		{
			//bill.setMemo(memoBuffer.toString());
			//memoBuffer = new StringBuffer ();
			//do nothing
		}
		else if (lineCode.equals("M00000"))
		{
			
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
	
	public Bill parseSummaryData (String line) throws IOException
	{
		
		Bill bill = getBill(line);
	
		if (line.length() > 12)
		{
			
			line = line.substring(12);
			
			line = line.replace((char)0xC, ' ');

			if (summaryBuffer == null)
				summaryBuffer = new StringBuffer();
			
			summaryBuffer.append(line);
			summaryBuffer.append(' ');
			
			//bill.setSummary(summary);
		
		}
		
		return bill;
	}
	
	public Bill parseActClause (String line) throws IOException
	{
		
		Bill bill = getBill(line);
	
		if (line.length() > 12)
		{
			
			line = line.substring(12);
			
			line = line.replace((char)0xC, ' ');

			if (actBuffer == null)
				actBuffer = new StringBuffer();
			
			actBuffer.append(line);
			actBuffer.append(' ');
			
			//bill.setSummary(summary);
		
		}
		
		return bill;
	}
	
	public Bill parseTitle (String line) throws IOException
	{
		
		Bill bill = getBill(line);
	
		if (line.length() > 12)
		{
			
			line = line.substring(12);
			
			line = line.replace((char)0xC, ' ');

			if (titleBuffer == null)
			{
				titleBuffer = new StringBuffer();
				titleBuffer.append(line);
				titleBuffer.append(' ');
			}
			else
			{
				if (!titleBuffer.toString().contains(line))
				{
					titleBuffer.append(line);
					titleBuffer.append(' ');
				}
			}
			
			//bill.setSummary(summary);
		
		}
		
		return bill;
	}
	
	/*
	 * 2009S00100 5Same as A 3555
2009S00100 9

WARN -> [openleg.BasicParser] line:40 line=2009S52205 5Same as A 9052, S 6068, S66002 / A40002 ;ERROR: java.lang.NullPointerException

	 */
	public Bill parseSameAsData (String line) throws IOException
	{
		Bill bill = null;
		
		bill = getBill(line);
	
		int lineCode = Integer.parseInt(line.substring(11,12));
		
		line = line.substring(12).trim();

		if (lineCode == 5)
		{
			/*
			 * 2009A08548B5Same as S 4039, S50437, S64437
2009S50437 5Same as S64437, S 4039, A 8548-B
2009S64437 5Same as S50437, S 4039, A 8548-B
2009S03428 5Same as A 7009, S50438, S64438

			 */
			
			String sameAsBillNo = null;
			
			if (line.startsWith("Same as Uni. "))
			{
				line = line.substring("Same as Uni. ".length());
				sameAsBillNo	= line.trim();
			}
			else if (line.startsWith("Same as "))
			{
				line = line.substring("Same as ".length());
				sameAsBillNo	= line.trim();
			}
			
			//2009S05440B5Same as A 8172-B
		
			
			//set the Same As for this bill
			if (sameAsBillNo != null)
			{
				sameAsBillNo = sameAsBillNo.replace(" ","");
				sameAsBillNo = sameAsBillNo.replace("-","");
				sameAsBillNo = sameAsBillNo.replace(".","");
				sameAsBillNo = sameAsBillNo.replace(";","");
				sameAsBillNo = sameAsBillNo.replace("/",",");
				
				
				bill.setSameAs(sameAsBillNo);
				
				/*
				StringTokenizer st = new StringTokenizer(sameAsBillNo,",");
				
				while (st.hasMoreTokens())
				{
				
					//String singleSameAs = st.nextToken().trim().replace(" ", "");
					
					
					//now check if the same as bill is set properly
					Bill billSameAs = PMF.getBill(persistenceManager,singleSameAs);
					
					if (billSameAs != null)
					{
						if (billSameAs.getSameAs()==null)
						{
							billSameAs.setSameAs(bill.getSenateBillNo());
							logger.info("set NEW same as for: " +  billSameAs.getSenateBillNo() + " - set to: " + billSameAs.getSameAs());
						}
						else if (billSameAs.getSameAs().indexOf(bill.getSenateBillNo())==-1)
						{
							String newSameAs = billSameAs.getSameAs();
							newSameAs += ',' + bill.getSenateBillNo();
							billSameAs.setSameAs(newSameAs);
							logger.info("updating same as for: " +  billSameAs.getSenateBillNo() + " - added " + bill.getSenateBillNo());
	
						}
					}
					
				}
				*/
					
			}
			
		}
		
		return bill;
		
	}
	
	/*gine
	 * 2009S00100 1                        ABC. exemption for premises                                               
2009S00100 6SAMPSON
2009S00100 7
2009S00100 8
	 */
	public void parseSponsorData (BufferedReader reader)
	{
		
	}
	
	/*
	 * 
2009S00100 T00000.SO DOC S 100                                    BTXT                 2009
2009S00100 T00001
2009S00100 T00002                           S T A T E   O F   N E W   Y O R K
2009S00100 T00083   29    S 2. This act shall take effect immediately.
2009S00100 T00000.SO DOC S 100           *END*                    BTXT                 2009

	 */
	public Bill parseTextData (String line) throws IOException
	{
		//2009S00022 T00000.SO DOC S 22                                     BTXT                 2009
//2009S00022 T00000.SO DOC S 22            *END*                    BTXT                 2009

		
		Bill bill = getBill(line);
		
		String lineCode = line.substring(11,17);
		line = line.substring(17);
		
		if (line.indexOf("*END*")!=-1)
		{
		//	bill.setFulltext(textBuffer.toString());
			
//			textBuffer = new StringBuffer ();
			
		//	logger.info("bill"+bill.getSenateBillNo() + " updating bill text");
		}
		else if (lineCode.equals("T00000"))//(line.indexOf("BTXT")!=-1)
		{
			
		}
		else if (lineCode.equals("R00000")) {
			
		}
		else
		{
			line = line.replace((char)0xC, ' ');

			if (textBuffer == null)
				textBuffer = new StringBuffer();
			
			textBuffer.append(line);
			textBuffer.append('\n');
			
		}
		
		return bill;
		
	}
	



	public Bill parseVoteData (String line) throws IOException
	{
		/*
		currentTx = persistenceManager.currentTransaction();
		
		if(!currentTx.isActive()) {
	        currentTx.begin();
		}
        
		//Bill bill = getBill(line);
		*/
		
		StringTokenizer st = new StringTokenizer(line.substring(12)," ");
		
		String token = st.nextToken();
		
		//2009S00738AVSenate Vote    Bill: S738-A             Date: 05/11/2009  Aye - 62  Nay - 0
		if (token.equals("Senate"))
		{

			currentVoteCount = 0;
			
			Date voteDate = null;
			int ayeCount = -1;
			int nayCount = -1;
			
			//create new vote
			st.nextToken();//Vote
			st.nextToken();//Bill:
			st.nextToken();//Sxxx
			st.nextToken();//Date:
			
			try
			{
				voteDate = DATE_PARSER.parse(st.nextToken());//Date value
			}
			catch (ParseException pe)
			{
				logger.warn("error parsing vote date",pe);
			}
			
			st.nextToken();//Aye
			st.nextToken();//-
			
			ayeCount = Integer.parseInt(st.nextToken());//Aye Count #
			
			st.nextToken();//Nay
			st.nextToken();//-
			
			nayCount = Integer.parseInt(st.nextToken());//Nay Count #
			
			currentVote = PMF.getVote(persistenceManager,currentBill, voteDate, ayeCount, nayCount);//new Vote(bill, voteDate, ayeCount, nayCount);
			
			if(currentVote == null) {
				currentVote = new Vote(currentBill, voteDate, ayeCount, nayCount);
				//persistenceManager.makePersistent(currentVote);
				logger.info("CREATED NEW VOTE INSTANCE: " + currentVote.getId());
			}	
			
			currentVote.setAyes(new ArrayList<String>());
			currentVote.setNays(new ArrayList<String>());
			currentVote.setAbstains(new ArrayList<String>());
			currentVote.setExcused(new ArrayList<String>());
			
			currentVote.setVoteType(Vote.VOTE_TYPE_FLOOR);
			
			logger.info("tracking vote: " + currentVote.getId());
			
			
		}
		else
		{
			
	        
			//add to existing vote
//			2009S00738AVAye  Adams            Aye  Addabbo          Aye  Alesi            Aye  Aubertine
			String vote = token;
			String voter = null;
			Person person = null;
			String nextToken = null;
			
			while (st.hasMoreTokens())
			{
				
				voter = st.nextToken();
				
				//need to generalize these rules and make them configurable
				if (voter.equals("Hassell-Thompso"))
				{
					voter = "Hassell-Thompson";
				}
				else if (voter.equals("Johnson")) //something Johnson, Johnson C, or Johnson O
				{	
					nextToken = st.nextToken();
					
					if (nextToken.length() == 1)
					{
						voter = voter + ' ' + nextToken;
						nextToken = null;
					}
				
				}
				
				
				person = PMF.getPerson(persistenceManager,voter);
				
				if (person == null)
				{
					logger.info("couldn't find voter: " + voter);
						
					continue;
					
				}
				
				currentVoteCount++;
				//logger.info("adding vote: #" + currentVoteCount + ":" + person.getFullname() + "=" + vote);
				
				if (vote.equalsIgnoreCase("Aye") || vote.equalsIgnoreCase("Yea"))
				{
					currentVote.addAye(person);
					
				}
				else if (vote.equalsIgnoreCase("Nay"))
				{
					currentVote.addNay(person);
				}
				else if (vote.equalsIgnoreCase("Abs"))
				{
					currentVote.addAbstain(person);
				}
				else if (vote.equalsIgnoreCase("Exc"))
				{
					currentVote.addExcused(person);
				}
				
				if (nextToken != null)
				{
					vote = nextToken;
					nextToken = null;
				}
				else if (st.hasMoreTokens())
					vote = st.nextToken();
				
			}
			
			
			
			
		}
		
		
		return currentBill;
	}
	
	private void persistBuffers ()
	{
		
		
		if (summaryBuffer != null)
		{
			currentBill.setSummary(summaryBuffer.toString());
			summaryBuffer = null;
		}
		
		if (titleBuffer != null)
		{
			String newTitle = titleBuffer.toString().trim();
			currentBill.setTitle(newTitle);
			titleBuffer = null;
		}
		
		if (actBuffer != null)
		{
			currentBill.setActClause(actBuffer.toString());
			actBuffer = null;
		}
		
		if (currentVote != null)
		{
			currentBill.setVotes(new ArrayList<Vote>());
			currentBill.addVote(currentVote);
			objectsToUpdate.add(persistenceManager.detachCopy(currentVote));

		}
		
		if (textBuffer != null)
		{
			currentBill.setFulltext(textBuffer.toString());
			
			textBuffer = null;
		}
		
		if (memoBuffer != null)
		{
			currentBill.setMemo(memoBuffer.toString());
			memoBuffer = null;
		}
		
		if (billEventsBuffer != null)
		{
			
			try {
				logger.info("deleting existing bill events for: " + currentBill.getSenateBillNo());

				for (BillEvent bEvent : currentBill.getBillEvents())
					searchEngine.deleteSenateObjectById("action", bEvent.getBillEventId()); //currentBill.getSenateBillNo() + "-*");
				
				PMF.deleteBillEvents(persistenceManager, currentBill);
				
			} catch (Exception e) {
				
				logger.warn("error clearing old bill events",e);
			}
			
			Iterator<BillEvent> itBe = billEventsBuffer.iterator();
			BillEvent bEvent = null;
			
			while (itBe.hasNext())
			{
				bEvent = itBe.next();
				
				objectsToUpdate.add(bEvent);
				
				
			}
			
			
			billEventsBuffer = null;
		}
		
		if (coSponsorBuffer != null)
		{
			currentBill.setCoSponsors(coSponsorBuffer);
			coSponsorBuffer = null;
		}
	}
	
	private void setSortIndex ()
	{
		if (currentBill.getSortIndex() == -1 || currentBill.getSortIndex() == 0)
		{
			String senateId = currentBill.getSenateBillNo().substring(1); //remove the letter!
			
			//remove leg type code
			senateId = senateId.replaceAll("[\\-. ;,A-Z,a-z]|%20","").trim();

			//remove amendment
			int amendIdx = senateId.length()-1;			
			if (!Character.isDigit(senateId.charAt(amendIdx)))
			{
				senateId = senateId.substring(0,amendIdx);
				
			}
			
			currentBill.setSortIndex(Integer.parseInt(senateId));
			
			logger.info("bill:" + currentBill.getSenateBillNo() + " set sort index=" + currentBill.getSortIndex());
		}
	}
}
