package ingest;

import gov.nysenate.openleg.lucene.LuceneObject;
import gov.nysenate.openleg.lucene.LuceneSerializer;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.calendar.CalendarEntry;
import gov.nysenate.openleg.model.calendar.Section;
import gov.nysenate.openleg.model.calendar.Supplemental;
import gov.nysenate.openleg.model.committee.Addendum;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.committee.Meeting;
import gov.nysenate.openleg.search.SearchEngine2;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;


import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import util.JsonSerializer;
import util.XmlSerializer;


import com.google.gson.JsonParseException;


public class IngestReader {
	
//	private static String WRITE_DIRECTORY = "/Users/jaredwilliams/Desktop/1/";
	private static String WRITE_DIRECTORY = "/usr/local/openleg/json/";
	
	BasicParser basicParser = null;
	ObjectMapper mapper = null;
	CalendarParser calendarParser = null;
	CommitteeParser committeeParser = null;
	SearchEngine2 searchEngine = null;
	
	ArrayList<Calendar> calendars;
	ArrayList<Bill> bills;
	ArrayList<SenateObject> committeeUpdates;
	
	public static void main(String[] args) throws IOException {
		IngestReader ir = new IngestReader(); 		
		
//		System.out.println("bills");
//		index(ir, "/Users/jaredwilliams/Desktop/test/2011/bill/", Bill.class);
//		System.out.println("calendars");
//		index(ir, "/Users/jaredwilliams/Desktop/test/2011/calendar/", Calendar.class);
//		System.out.println("agendas");
//		index(ir, "/Users/jaredwilliams/Desktop/test/2011/agenda/", Agenda.class);
		
//		ir.handlePath("/Users/jaredwilliams/Desktop/2011");
		
		if(args.length == 3){
			String command = args[0];
			String p1 = args[1];
			String p2 = args[2];
			if(command.equals("-c")) {
				WRITE_DIRECTORY = p1;
				ir.handlePath(p2);
			}
			else if(command.equals("-fc")) {
				ir.fixCalendarBills(p1, p2);
			}
			else if(command.equals("-fa")) {
				ir.fixAgendaBills(p1, p2);
			}
			else if(command.equals("-b")) {
				Bill bill = (Bill)ir.loadObject(p1, p2, "bill", Bill.class);
				ir.writeSenateObject(bill, Bill.class, false);
			}
			else if(command.equals("-c")) {
				Calendar cal = (Calendar)ir.loadObject(p1, p2, "calendar", Calendar.class);
				ir.writeSenateObject(cal, Calendar.class, false);
			}
			else if(command.equals("-a")) {
				Agenda agenda = (Agenda)ir.loadObject(p1, p2, "agenda", Agenda.class);
				ir.writeSenateObject(agenda, Agenda.class, false);
			}
		}
		else {
			System.err.println("appropriate usage is:\n" +
					"\t-i <json directory> <sobi directory> (to create index)\n" +
					"\t-fc <year> <calendar directory> (to fix calendar bills)\n" +
					"\t-fa <year> <agenda directory> (to fix agenda bills)\n" +
					"\t-b <billid> <year> (to reindex bill)\n" +
					"\t-c <calendarid> <year> (to reindex calendar)\n" +
					"\t-a <agenda id> < year> (to reindex agenda)");
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
	
	
	
	
	
	public IngestReader() {
		searchEngine = SearchEngine2.getInstance();
		
		calendars = new ArrayList<Calendar>();
		bills = new ArrayList<Bill>();
		committeeUpdates = new ArrayList<SenateObject>();
	}
	
	public void handlePath(String path) {
		File file = new File(path);
		if (file.isDirectory())	{
			File[] files = file.listFiles();
			
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
	
	//TODO since structure already exists to pass in files with a given type
	// like "sobi", "transcript" or etc it would be best to move away
	// from relying on file names to determine what sort of document is
	// being processed
	
	public void handleFile(File file) {
		System.out.println(file.getName());
		//TODO ending in .TXT doesn't necessaril signify a bill
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
				writeBills(bills, true);
				basicParser.clearBills();
			}
			
			bills.clear();
		}
		else if(file.getName().contains("-calendar-")) {
			
			XmlFixer.fixCalendar(file);
			
			try {
				calendars = getCalendarParser().doParsing(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!calendars.isEmpty()) {
				writeCalendars(calendars);
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
			
			writeCommitteeUpdates(committeeUpdates);
			committeeParser.clearUpdates();
		}
		else {
			//TODO trancsripts
			//ignore for now
		}
	}
	
	private void writeCommitteeUpdates(ArrayList<SenateObject> committeeUpdates) {
		for(SenateObject so:committeeUpdates) {
			if(so instanceof Bill) {
				//if a bill is being updated from the committee xml
				//it is either adding or removing a vote from an existing bill
				//which has been deserialized or creating a new bill
				//in which case merging isn't necessary
				writeBills(new ArrayList<Bill>(Arrays.asList(((Bill)so))), false);
			}
			else if(so instanceof Agenda) {
				writeSenateObject(so, Agenda.class, true);
			}
		}
	}
	
	private void writeCalendars(ArrayList<Calendar> calendars) {
		for(Calendar calendar:calendars) {
			writeSenateObject(calendar, Calendar.class, true);
		}
	}

	public void writeBills(ArrayList<Bill> bills, boolean merge) {
		for(Bill bill:bills) {
			if(bill == null)
				continue;
			
			writeSenateObject(bill, Bill.class, merge);
			
		}
	}
	
	public void writeSenateObject(SenateObject obj, Class<? extends SenateObject> clazz, boolean merge) {
		mapper = getMapper();
		
		if(obj == null)
			return;
		
		System.out.println(obj.luceneOtype() + " : " + obj.luceneOid());
		
		File newFile = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
				
		if(merge) {
			obj = mergeSenateObject(obj, clazz, newFile);
		}
		
		if(this.writeJsonFromSenateObject(obj, clazz, newFile)) {
			indexSenateObject(obj);
		}
		
	}
	
	public void indexSenateObject(SenateObject obj) {
		try {
			/*
			 * fullText for bills must be saved and reapplied after processing.. on long processes
			 * where many SOBIs are processed bills stay in memory, so if fulltext is reprocessed
			 * the next update will see the new text and not be able to parse 
			 * it properly (due to line numbers)
			 */
			if(obj instanceof Bill 
					&& ((Bill)obj).getFulltext() != null 
					&& !((Bill)obj).getFulltext().equals("")) {
				
				StringBuffer fullText = new StringBuffer(((Bill)obj).getFulltext());
				((Bill)obj).setFulltext(formatBillText(((Bill)obj).getFulltext()));
								
				searchEngine.indexSenateObjects(
						new ArrayList<LuceneObject>(
							Arrays.asList(obj)), 
							new LuceneSerializer[]{
								new XmlSerializer(), 
								new JsonSerializer()});
				
				((Bill)obj).setFulltext(fullText.toString());
				fullText = null;
			}
			else {
				searchEngine.indexSenateObjects(
						new ArrayList<LuceneObject>(
							Arrays.asList(obj)), 
							new LuceneSerializer[]{
								new XmlSerializer(), 
								new JsonSerializer()});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean writeJsonFromSenateObject(SenateObject obj, Class<? extends SenateObject> clazz, File file) {
		if(file == null) 
			file = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		File dir = new File(WRITE_DIRECTORY + obj.getYear());
		if(!dir.exists()) {
			dir.mkdir();
		}
		dir = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype());
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		try {			
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(file));
			
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw,JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(generator, obj);
			osw.close();
			
			return true;
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public SenateObject mergeSenateObject(SenateObject obj, Class<? extends SenateObject> clazz, File file) {
		if(file == null)
			file = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		if(file.exists()) {
			SenateObject oldObject =  null;
			try {
				oldObject = (SenateObject)mapper.readValue(file, clazz);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(oldObject != null) {
				oldObject.merge(obj);
				obj = oldObject;
			}
		}
		
		return obj;
	}
	
	public String formatBillText(String text) {
		StringBuffer ret = new StringBuffer("");
		StringTokenizer st = new StringTokenizer (text,"\n");
		
		String line = null;
		
		while(st.hasMoreTokens()) {
			line = st.nextToken();
			if(line.matches("^ ?T\\d{5}\\:(\\s{3,4}\\d{1,2})?.+?")) {
				ret.append(line.substring(13) + "\n");
			}
			else if(line.matches("^ ?T\\d{5}\\:")) {
				ret.append(line.substring(7) + "\n");
			}
			else if(line.matches("^ ?R\\d{5}\\:.*?")) {
				ret.append(line.substring(7) + "\n");
			}
			else {
				ret.append(line + "\n");
			}
		}
		return ret.toString();
	}
	
	public SenateObject loadObject(String id, String year, String type, Class<? extends SenateObject> clazz) {
		return loadObject(WRITE_DIRECTORY + year + "/" + type + "/" + id + ".json", clazz);
	}
	
	/**
	 * @param path to json document
	 * @param clazz class of object to be loaded
	 * @return deserialized SenateObject of type clazz
	 */
	public SenateObject loadObject(String path, Class<? extends SenateObject> clazz) {
		mapper = getMapper();
		File file = new File(path);
		if(!file.exists()) 
			return null;
		
		try {
			return mapper.readValue(file, clazz);
		} catch (org.codehaus.jackson.JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean deleteFile(String id, String year, String type) {
		return deleteFile(WRITE_DIRECTORY + year + "/" + type + "/" + id + ".json");
	}
	
	public boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}
	
	/*
	 * fixCalendarBills(year,path) and fixAgendaBills(year,path) can be
	 * executed to update the two document types with the latest bill information.
	 * This solves an issue where occasionaly calendars or agendas
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
		
		if(!file.exists())
			return;
		
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				fixAgendaBills(year, temp.getAbsolutePath());
			}
		}
		else {
			Agenda agenda = (Agenda) this.loadObject(file.getAbsolutePath(), Agenda.class);
			
			if(agenda == null)
				return;
			
			if(agenda.getAddendums() != null) {
				for(Addendum addendum:agenda.getAddendums()) {
					if(addendum.getMeetings() != null) {
						for(Meeting meeting:addendum.getMeetings()) {
							if(meeting.getBills() ==  null)
								continue;
							
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
	
	/**
	 * used to index already serialized json documents
	 * @param path directory to files (e.g. "2011/bills/")
	 * @param clazz class of object to be indexed
	 */
	public void index(String path, Class<? extends SenateObject> clazz) {		
		File dir = new File(path);
		
		for(File file:dir.listFiles()) {
			try {
				this.searchEngine.indexSenateObjects(
						new ArrayList<LuceneObject>(Arrays.asList(
								loadObject(file.getAbsolutePath(), clazz))),
						new LuceneSerializer[]{new XmlSerializer(), new JsonSerializer()});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
