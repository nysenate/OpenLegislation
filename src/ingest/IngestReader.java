package ingest;

import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Agenda;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import model.bill.Bill;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.util.DefaultPrettyPrinter;

import com.google.gson.JsonParseException;


public class IngestReader {
	
	private static String WRITE_DIRECTORY = "/Users/jaredwilliams/Desktop/test/";
	
	BasicParser basicParser = null;
	ObjectMapper mapper = null;
	CalendarParser calendarParser = null;
	CommitteeParser committeeParser = null;
	
	ArrayList<Calendar> calendars;
	ArrayList<Bill> bills;
	ArrayList<SenateObject> committeeUpdates;
	
	public static void main(String[] args) throws IOException {
		IngestReader ir = new IngestReader();
		
		ir.handlePath("/Users/jaredwilliams/Desktop/2011");
		
//		ir.handlePath(args[0]);
	}
	
	public IngestReader() {
		calendarParser = new CalendarParser(this);
		basicParser = new BasicParser();
		committeeParser = new CommitteeParser(this);
		
		calendars = new ArrayList<Calendar>();
		bills = new ArrayList<Bill>();
		committeeUpdates = new ArrayList<SenateObject>();
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
	
	public void handleFile(File file) {
		if(file.getName().endsWith(".TXT")) { //TODO always a bill? nope
			
			bills = new ArrayList<Bill>();
			try {
				bills.addAll(basicParser.handleBill(file.getAbsolutePath(), '-'));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(!bills.isEmpty()) {
				writeBills(bills);
				basicParser.clearBills();
			}
			
			bills.clear();
		}
		else if(file.getName().contains("-calendar-")) {
			
			XmlFixer.fixCalendar(file);
			
			try {
				calendars = calendarParser.doParsing(file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!calendars.isEmpty()) {
				writeCalendars(calendars);
				calendarParser.clearCalendars();
			}
			
			calendars.clear();
		}
		if(file.getName().contains("-agenda-")) {
			
			try {
				committeeUpdates = committeeParser.doParsing(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			
			writeCommitteeUpdates(committeeUpdates);
			committeeParser.clearUpdates();
		}
		else {
			//ignore for now
		}
	}
	
	private void writeCommitteeUpdates(ArrayList<SenateObject> committeeUpdates) {
		for(SenateObject so:committeeUpdates) {
			if(so instanceof Bill) {
				//if a bill is being updated from the committee xml
				//it is either adding or removing a vote from an existing bill
				//in which case merging isn't necessary
				writeSenateObject(so, Bill.class, false);
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

	public void writeBills(ArrayList<Bill> bills) {
		for(Bill bill:bills) {
			if(bill == null)
				continue;
			
			writeSenateObject(bill, Bill.class, true);
		}
	}
	
	public void writeSenateObject(SenateObject obj, Class<?> clazz, boolean merge) {
		mapper = getMapper();
		
		if(obj == null)
			return;
		
		System.out.println(obj.luceneOtype() + " : " + obj.luceneOid());
		
		File dir = new File(WRITE_DIRECTORY + obj.getYear());
		if(!dir.exists()) {
			dir.mkdir();
		}
		dir = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype());
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		File newFile = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
		
		if(merge) {
			if(newFile.exists()) {
				File oldFile = new File(WRITE_DIRECTORY + obj.getYear() + "/" + obj.luceneOtype() + "/" + obj.luceneOid() + ".json");
				SenateObject oldObject =  null;
				try {
					oldObject = (SenateObject)mapper.readValue(oldFile, clazz);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (JsonMappingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(oldObject != null) {
					obj.merge(oldObject);
				}
			}
		}
		
		try {
			BufferedOutputStream osw = new BufferedOutputStream(new FileOutputStream(newFile));
			
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(osw,JsonEncoding.UTF8);
			generator.setPrettyPrinter(new DefaultPrettyPrinter());
			mapper.writeValue(generator, obj);
			osw.close();
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public SenateObject loadObject(String id, String year, String type, Class<? extends SenateObject> clazz) {
		return loadObject(WRITE_DIRECTORY + year + "/" + type + "/" + id + ".json", clazz);
	}
	
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
}
