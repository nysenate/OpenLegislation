package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.lucene.ILuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.Result;
import gov.nysenate.openleg.search.SearchEngine2;
import gov.nysenate.openleg.search.SenateResponse;
import gov.nysenate.openleg.util.JsonSerializer;
import gov.nysenate.openleg.util.TranscriptFixer;
import gov.nysenate.openleg.util.XmlHelper;
import gov.nysenate.openleg.util.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoFilepatternException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.errors.UnmergedPathException;
import org.eclipse.jgit.lib.RepositoryBuilder;

public class IngestReader {
	
	@SuppressWarnings("serial")
	public static class IngestException extends Exception {}
	
	
	private static Logger logger = Logger.getLogger(IngestReader.class);
	
	private static String WRITE_DIRECTORY = "/usr/local/openleg/json/";	
	
	BasicParser basicParser = null;
	ObjectMapper mapper = null;
	CalendarParser calendarParser = null;
	CommitteeParser committeeParser = null;
	SearchEngine2 searchEngine = null;
	
	private final long THE_TIME = new Date().getTime();
	
	Git repo;
	
	ArrayList<Calendar> calendars;
	ArrayList<Bill> bills;
	ArrayList<ISenateObject> committeeUpdates;
	
	public static void main(String[] args) throws IOException {
		IngestReader ir = new IngestReader();
				
		try {
			if(args.length < 2) {
				throw new IngestException();
			}
			
			String command = args[0];
			if(args.length == 2) {
				if(command.equals("-gx")) {
					XmlHelper.generateXml(args[1]);
				}
				else if(command.equals("-b")) {
					ir.writeBills(new ArrayList<Bill>(Arrays.asList((Bill)ir.loadObject(args[1], Bill.class))), null, false);
				}
				else if(command.equals("-c")) {
					ir.indexSenateObject((Calendar)ir.loadObject(args[1], Calendar.class));
				}
				else if(command.equals("-a")) {
					ir.indexSenateObject((Agenda)ir.loadObject(args[1], Agenda.class));
				}
				else if(command.equals("-t")) {
					ir.indexSenateObject((Transcript)ir.loadObject(args[1], Transcript.class));
				}
				else if(command.equals("-it")) {
					ir.handleTranscript(args[1]);
				}
				else {
					throw new IngestException();
				}
			}
			else if(args.length == 3){
				if(command.equals("-i")) {
					WRITE_DIRECTORY = args[1];
					ir.handlePath(args[2]);
				}
				else if(command.equals("-fc")) {
					ir.fixCalendarBills(args[1], args[2]);
				}
				else if(command.equals("-fa")) {
					ir.fixAgendaBills(args[1], args[2]);
				}
				else {
					throw new IngestException();
				}
			}
			else if(args.length == 5) {				
				if(command.equals("-pull")) {
					ir.pullSobis(args[1], args[2], args[3], args[4]);
				}
			}
		} catch(IngestException e) {
			System.err.println("appropriate usage is:\n" +
					"\t-i <json directory> <sobi directory> (to create index)\n" +
					"\t-gx <sobi directory> (to generate agenda and calendar xml from sobi)\n" +
					"\t-fc <year> <calendar directory> (to fix calendar bills)\n" +
					"\t-fa <year> <agenda directory> (to fix agenda bills)\n" +
					"\t-b <bill json path> (to reindex single bill)\n" +
					"\t-c <calendar json path> (to reindex single calendar)\n" +
					"\t-a <agenda json path> (to reindex single agenda)\n" +
					"\t-t <transcript json path> (to reindex single transcript)" +
					"\t-it <transcript sobi path> (to reindex dir of transcripts)\n" +
					"\t-pull <sobi directory> <output directory> <id> <year> (get an objects referencing sobis)");
		}
	}
	
	public IngestReader() {
		searchEngine = SearchEngine2.getInstance();
		calendars = new ArrayList<Calendar>();
		bills = new ArrayList<Bill>();
		committeeUpdates = new ArrayList<ISenateObject>();
	}
	
	public Git getRepo(String workingDrive) {
		if(repo == null) {
			try {
				if(!new File(workingDrive+".git/").exists()) {
					Git.init().setDirectory(new File(workingDrive)).call();
				}
				repo = new Git(new RepositoryBuilder().setWorkTree(new File(workingDrive)).build());
			} catch(IOException e) {
				logger.error(e);
				System.exit(0);
			}			
		}
		return repo;
	}
	
	public void gitCommit(String message) {
		Git git = getRepo(WRITE_DIRECTORY);
		try {
			git.add().addFilepattern(".").call();
			git.commit().setMessage(message).setAuthor("Tester", "notmyfault@nysenate.gov").call();
		} catch(WrongRepositoryStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoHeadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnmergedPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConcurrentRefUpdateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JGitInternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoFilepatternException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ObjectMapper getMapper() {
		if(mapper == null) {
			mapper = new ObjectMapper();
			SerializationConfig cnfg = mapper.getSerializationConfig();
			cnfg.set(Feature.INDENT_OUTPUT, true);
			mapper.setSerializationConfig(cnfg);
		}
		
		return mapper;
	}
	
	public CommitteeParser getCommitteeParser() {
		if(committeeParser == null) {
			committeeParser = new CommitteeParser(this);
		}
		return committeeParser;
	}
	
	public BasicParser getBasicParser() {
		if(basicParser == null) {
			basicParser = new BasicParser();
		}
		return basicParser;
	}
	
	public CalendarParser getCalendarParser() {
		if(calendarParser == null) {
			calendarParser = new CalendarParser(this);
		}
		return calendarParser;
	}
	
	/* TODO
	 * FILE READING 
	 */
	
	public void handlePath(String path) {	
		File file = new File(path);
		if (file.isDirectory())	{			
			
			File[] files = sortFilesByName(file.listFiles());
			
			for (int i = 0; i < files.length; i++)
			{
				if(files[i].isFile()) {
					handleFile(files[i]);
				}
				else if(files[i].isDirectory()) {
					handlePath(files[i].getAbsolutePath());
				}
			}
		}
		else {
			handleFile(file);
		}
	}
	
	public static File[] sortFilesByName(File[] fList) {
		Arrays.sort(fList, new Comparator<File>() {
			@Override
			public int compare(File one, File two) {
				return one.getName().compareTo(two.getName());
			}
		});
		
		return fList;
	}
	
	public void handleFile(File file) {
		logger.info("Reading file: " + file);
				
		if(file.getName().endsWith(".TXT")) {			
			bills = new ArrayList<Bill>();
			try {
				bills.addAll(getBasicParser().handleBill(file.getAbsolutePath(), '-'));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(!bills.isEmpty()) {
				writeBills(bills, file, true);
				basicParser.clearBills();
			}
			
			bills.clear();
		}
		
		else if(file.getName().contains("-calendar-")) {
			
			XmlHelper.fixCalendar(file);
			
			try {
				calendars = getCalendarParser().doParsing(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!calendars.isEmpty()) {
				writeCalendars(calendars, file);
				calendarParser.clearCalendars();
			}
			
			calendars.clear();
		}
		else if(file.getName().contains("-agenda-")) {
			try {
				committeeUpdates = getCommitteeParser().doParsing(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			writeCommitteeUpdates(committeeUpdates, file);
			committeeParser.clearUpdates();
		}
		
//		long start = System.currentTimeMillis();
//		String message = file.getName();
//		gitCommit(message);
//		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Committed Changes");
//		logger.warn("Finished with file: "+file.getName());
	}
	
	//TODO this is pretty bad
	public void handleTranscript(String path) {
		File file = new File(path);
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				handleTranscript(temp.getAbsolutePath());
			}
		}
		else {
			Transcript trans = null;
			
			//transcripts often come incorrectly formatted..
			//this attempts to reprocess and save the raw text
			//if there is a parsing error, and then attempts
			//parsing one more time
			try {				
				trans = getBasicParser().handleTranscript(path);
			}
			catch (Exception e) {
				TranscriptFixer fixer = new TranscriptFixer();
				List<String> in;
				
				try {
					if((in = fixer.readContents(file)) != null) {
						
						List<String> ret = fixer.fix(in);
						BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
						
						for(String s:ret) {
							bw.write(s);
							bw.newLine();
						}
						
						bw.close();
						trans = getBasicParser().handleTranscript(path);
					}
				}
				catch (Exception e2) {
					e2.printStackTrace();
					trans = null;
				}
				
			}
			if(trans != null) {
				writeSenateObject(trans, Transcript.class, true);
			}
		}
		
	}
	
	public ISenateObject loadObject(String id, String year, String type, Class<? extends ISenateObject> clazz) {
		return loadObject(WRITE_DIRECTORY + "/" + year + "/" + type + "/" + id + ".json", clazz);
	}
	
	/**
	 * @param path to json document
	 * @param clazz class of object to be loaded
	 * @return deserialized SenateObject of type clazz
	 */
	public ISenateObject loadObject(String path, Class<? extends ISenateObject> clazz) {
		logger.info("Loading object at: " + path);
		
		mapper = getMapper();
		File file = new File(path);
		if(!file.exists()) 
			return null;
		
		try {
			return this.getMapper().readValue(file, clazz);
		} catch (org.codehaus.jackson.JsonParseException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not map json", e);
		} catch (IOException e) {
			logger.warn("error with file", e);
		}
		
		return null;
	}
	
	
	
	
	
	
	/* TODO
	 * FILE WRITING
	 */
	
	private void writeCommitteeUpdates(ArrayList<ISenateObject> committeeUpdates, File file) {
		long start = System.currentTimeMillis();
		logger.warn("Writing "+committeeUpdates.size()+" Committee Updates");
		for(ISenateObject so:committeeUpdates) {
			if(so instanceof Bill) {
				//if a bill is being updated from the committee xml
				//it is either adding or removing a vote from an existing bill
				//which has been deserialized or creating a new bill
				//in which case merging isn't necessary
				writeBills(new ArrayList<Bill>(Arrays.asList(((Bill)so))), file, true);
			}
			else if(so instanceof Agenda) {
				writeSenateObject(so, Agenda.class, file, true);
			}
		}
		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Wrote "+committeeUpdates.size()+" committee updates");
	}
	
	private void writeCalendars(ArrayList<Calendar> calendars, File file) {
		long start = System.currentTimeMillis();
		for(Calendar calendar:calendars) {
			writeSenateObject(calendar, Calendar.class, file, true);
		}
		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Wrote "+calendars.size()+" Calendars");
	}

	public void writeBills(ArrayList<Bill> bills, File file, boolean merge) {
		long start = System.currentTimeMillis();
		for(Bill bill:bills) {
			if(bill == null)
				continue;
			
			//TODO
			//if this returns true bill is not active
			if(reindexAmendedVersions(bill)) {
				bill.setLuceneActive(false);
			}
			writeSenateObject(bill, Bill.class, file, merge);
			
		}
		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Wrote "+bills.size()+" bills");
	}
	
	public void writeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file, boolean merge) {
		if(file == null)
			writeSenateObject(obj, clazz, merge);
		else {
			obj.addSobiReference(file.getName());
			writeSenateObject(obj, clazz, getDateFromFileName(file.getName()), merge);
		}
	}
	
	public void writeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, boolean merge) {
		writeSenateObject(obj, clazz, THE_TIME, merge);
	}
	
	public void writeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, long modified, boolean merge) {
		logger.info("Writing object type: " + obj.luceneOtype() + " with id: " + obj.luceneOid());
		
		try {
			if(obj == null)
				return;
			
			File newFile = new File(WRITE_DIRECTORY + "/" + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
			
			if(merge) {
				obj = mergeSenateObject(obj, clazz, newFile);
			}
			
			obj.setLuceneModified(modified);
			
			if(this.writeJsonFromSenateObject(obj, clazz, newFile)) {
				//TODO
				indexSenateObject(obj);
			}
		}
		catch (Exception e) {
			logger.warn("Exception while writing object", e);
		}
	}
	
	public boolean writeJsonFromSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file) {
		logger.info("Writing json to path: " + file.getAbsolutePath());
		
		if(file == null) 
			file = new File(WRITE_DIRECTORY + "/" + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		File dir = new File(WRITE_DIRECTORY + "/" + obj.getYear());
		if(!dir.exists()) {
			logger.info("creating directory: " + dir.getAbsolutePath());
			dir.mkdir();
		}
		dir = new File(WRITE_DIRECTORY + "/" + obj.getYear() + "/" + obj.luceneOtype());
		if(!dir.exists()) {
			logger.info("creating directory: " + dir.getAbsolutePath());
			dir.mkdir();
		}
		
		try {			
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(file));
			
			JsonGenerator generator = this.getMapper().getJsonFactory().createJsonGenerator(osw,JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			this.getMapper().writeValue(generator, obj);
			osw.close();
			
			return true;
		} catch (JsonGenerationException e) {
			logger.warn("could not parse json", e);
		} catch (JsonMappingException e) {
			logger.warn("could not parse json", e);
		} catch (IOException e) {
			logger.warn("error reading file", e);
		}
		
		return false;
	}
	
	public ISenateObject mergeSenateObject(ISenateObject obj, Class<? extends ISenateObject> clazz, File file) {		
		if(file == null)
			file = new File(WRITE_DIRECTORY  + "/" + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		if(file.exists()) {
			logger.info("Merging object with id: " + obj.luceneOid());
			ISenateObject oldObject =  null;
			try {
				oldObject = (ISenateObject)this.getMapper().readValue(file, clazz);
			} catch (JsonGenerationException e) {
				logger.warn("could not parse json", e);
			} catch (JsonMappingException e) {
				logger.warn("could not parse json", e);
			} catch (IOException e) {
				logger.warn("error reading file", e);
			}
			if(oldObject != null) {
				oldObject.setLuceneActive(obj.getLuceneActive());
				oldObject.merge(obj);
				obj = oldObject;
			}
		}
		
		return obj;
	}
	
	public boolean deleteSenateObject(ISenateObject so) {
		try {
			searchEngine.deleteSenateObject(so);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deleteFile(so.luceneOid(), so.getYear() +"", so.luceneOtype());
	}
	
	public boolean deleteFile(String id, String year, String type) {
		return deleteFile(WRITE_DIRECTORY + "/" + year + "/" + type + "/" + id + ".json");
	}
	
	public boolean deleteFile(String path) {
		logger.info("Deleting file at: " + path);
		
		File file = new File(path);
		return file.delete();
	}
	
	
	
	
	
	/* TODO
	 * INDEXING
	 */
	
	public void indexSenateObject(ISenateObject obj) {
		try {
			searchEngine.indexSenateObjects(
					new ArrayList<ILuceneObject>(
						Arrays.asList(obj)), 
						new LuceneSerializer[]{
							new XmlSerializer(), 
							new JsonSerializer()});
		} catch (IOException e) {
			logger.warn("Exception while indexing object", e);
		}
	}
	
	/**
	 * desirable to hide old versions of an amended bill from the default search
	 * this appends "active:false" as a field to any old verions of bills
	 * 
	 * to avoid constantly rewriting amended versions of bills this does a query
	 * to lucene to check if they've already been hidden, if they haven't then 
	 * they are sent to reindexInactiveBill
	 * 
	 * @param bill
	 * 
	 * returns true if current bill isn't searchable, false otherwise
	 */
	public boolean reindexAmendedVersions(Bill bill) {
		int idx = bill.getSenateBillNo().indexOf("-");
		char c = bill.getSenateBillNo().charAt(idx-1);
		String strings[] = bill.getSenateBillNo().split("-");
		
		String query = null;
		
		if(c >= 65 && c < 90)
			query = strings[0].substring(0, strings[0].length()-1);
		else 
			query = strings[0];
			
		try {
			//oid:(S418-2009 OR [S418A-2009 TO S418Z-2009]) AND year:2009
			query = "otype:bill AND oid:((" 
				+ query + "-" + strings[1] 
                    + " OR [" + query + "A-" + strings[1] 
                       + " TO " + query + "Z-" + strings[1]
                    + "]) AND " + query + "*-" + strings[1] + ")";
			//caches recent searces, if s1, s1a and s1b are added in close succession
			//it's possible they s1a won't be picked up.. closing the searcher
			//fixes that for the time being
			searchEngine.closeSearcher();
			SenateResponse sr = searchEngine.search(query,
					"json", 0,100, null, false);
						
			//if there aren't any results this is a new bill
			if(sr.getResults().isEmpty())
				return false;
								
			//create a list and store bill numbers from oldest to newest
			ArrayList<String> billNumbers = new ArrayList<String>();				
			for(Result result:sr.getResults()) {
				billNumbers.add(result.getOid());
			}
			if(!billNumbers.contains(bill))
				billNumbers.add(bill.getSenateBillNo());
			Collections.sort(billNumbers);
			
			String newest = billNumbers.get(billNumbers.size()-1);
			
			//if bill being stored isn't the newest we can assume
			//that the newest bill has already reindexed older bills
			if(!bill.getSenateBillNo().equals(newest))
				return true;
			
			billNumbers.remove(newest);
			billNumbers.remove(bill.getSenateBillNo());				
							
			for(Result result:sr.getResults()) {
				if(billNumbers.contains(result.getOid())) {
					if(result.getActive().equals("true")) {
						reindexInactiveBill(result.getOid(), bill.getYear()+"");
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (org.apache.lucene.queryParser.ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private void reindexInactiveBill(String senateBillNo, String year) {
		Bill temp = (Bill)this.loadObject(senateBillNo,
				year,
				"bill",
				Bill.class);
		
		if(temp != null) {
			temp.setLuceneActive(false);
			
			this.indexSenateObject(temp);
		}
	}
	
	
	
	
	
	/* TODO
	 * UTILITIES
	 */
	
	/*
	 * fixCalendarBills(year,path) and fixAgendaBills(year,path) can be
	 * executed to update the two document types with the latest bill information.
	 * This solves an issue where occasionally calendars or agendas
	 * would be missing relevant information that SHOULD be available to them.
	 */
	
	public void fixCalendarBills(String year, String path) {
		File file = new File(path);
		
		if(!file.exists())
			return;
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				fixCalendarBills(year, temp.getAbsolutePath());
			}
		}
		else {
			Calendar cal = (Calendar) this.loadObject(file.getAbsolutePath(), Calendar.class);
			
			if(cal == null) 
				return;
			
			if(cal.getSupplementals() != null) {
				for(Supplemental sup:cal.getSupplementals()) {
					if(sup.getSections() != null) {
						for(Section section:sup.getSections()) {
							for(CalendarEntry ce:section.getCalendarEntries()) {
								ce.setBill(
									(Bill)this.loadObject(
										ce.getBill().getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
					
					if(sup.getSequence() != null) {
						for(CalendarEntry ce:sup.getSequence().getCalendarEntries()) {
							if(ce.getBill() != null) {							
								ce.setBill(
									(Bill)this.loadObject(
										ce.getBill().getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
				}
			}
			this.writeSenateObject(cal, Calendar.class, false);
		}
	}
	
	public void fixAgendaBills(String year, String path) {
		File file = new File(path);
		
		if(!file.exists()){
			return;
		}
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				fixAgendaBills(year, temp.getAbsolutePath());
			}
		}
		else {
			Agenda agenda = (Agenda) this.loadObject(file.getAbsolutePath(), Agenda.class);
			
			if(agenda == null) {
				return;
			}
			
			if(agenda.getAddendums() != null) {
				for(Addendum addendum:agenda.getAddendums()) {
					if(addendum.getMeetings() != null) {
						for(Meeting meeting:addendum.getMeetings()) {
							if(meeting.getBills() ==  null) {
								continue;
							}
							
							for(int i = 0; i < meeting.getBills().size(); i++) {
								meeting.getBills().set(i,
									(Bill)this.loadObject(
										meeting.getBills().get(i).getSenateBillNo(),
										year,
										"bill",
										Bill.class)
								);
							}
						}
					}
				}
			}
			this.writeSenateObject(agenda, Agenda.class, false);
		}
	}
	
	public long getDateFromFileName(String fileName) {
		java.util.Calendar cal = java.util.Calendar.getInstance();
		
		fileName = fileName.replaceAll("(SOBI\\.D|\\.TXT.*$)", "");
		
		if(fileName.length() == 14) {
			cal.set(Integer.parseInt(fileName.substring(0,2)) + 2000,
					Integer.parseInt(fileName.substring(2,4))-1,
					Integer.parseInt(fileName.substring(4,6)),
					Integer.parseInt(fileName.substring(8,10)),
					Integer.parseInt(fileName.substring(10,12)),
					Integer.parseInt(fileName.substring(12,14)));
		}
				
		return cal.getTimeInMillis();
	}
	
	
	public void pullSobis(String id, String year, String sobiDirectory, String writeDirectory) {
		Bill bill = (Bill) this.loadObject(id, year, "bill", Bill.class);
		for(String sobi:bill.getSobiReferenceList()) {
			File sobiFile = new File(sobiDirectory + System.getProperty("file.separator") + sobi);
			if(sobiFile.exists()) {
				File copySobi = new File(writeDirectory + System.getProperty("file.separator") + sobi);
				try {
					copySobi.createNewFile();
				} catch (IOException e) {
					logger.warn(e);
				}
				
				if(!copySobi.exists()) 
					continue;
				
				FileChannel source = null;
				FileChannel destination = null;
				
				try {
					source = new FileInputStream(sobiFile).getChannel();
					destination = new FileOutputStream(copySobi).getChannel();
					destination.transferFrom(source, 0, source.size());
				} catch (FileNotFoundException e) {
					logger.warn(e);
				} catch (IOException e) {
					logger.warn(e);
				}
				finally {
					if(source != null) {
						try {
							source.close();
						} catch (IOException e) {
							logger.warn(e);
						}
					}
					if(destination != null) {
						try {
							destination.close();
						} catch (IOException e) {
							logger.warn(e);
						}
					}
				}
			}
		}
	}
}
