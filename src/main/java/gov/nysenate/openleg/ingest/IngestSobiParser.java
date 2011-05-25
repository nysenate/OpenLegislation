package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.util.Timer;
import gov.nysenate.openleg.util.XmlHelper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.apache.log4j.Logger;

public class IngestSobiParser {
	private final long THE_TIME = new Date().getTime();
	private final String LOG_FILE = ".log";
	
	private Logger logger = Logger.getLogger(IngestSobiParser.class);
	
	BufferedWriter logWriter;
	
	BasicParser basicParser;
	CalendarParser calendarParser;
	CommitteeParser committeeParser;
	JsonDao ingestJson;
	Timer timer;
	
	ArrayList<ISenateObject> senateObjects;
	
	public IngestSobiParser(JsonDao ingestJson) {
		this.ingestJson = ingestJson;
		
		committeeParser = new CommitteeParser();
		basicParser = new BasicParser();
		calendarParser = new CalendarParser();
		timer = new gov.nysenate.openleg.util.Timer();
		
		senateObjects = new ArrayList<ISenateObject>();
	}
	
	private void openLog(String dir) {
		if(logWriter == null) {
			try {
				logWriter = new BufferedWriter(new FileWriter(new File((dir == null ? "" : dir + "/") + LOG_FILE)));
			} catch (IOException e) {
				logger.error(e);
				System.exit(0);
			}
		}
	}
	
	private void logUpdate(String filePath) {
		try {
			logWriter.write(filePath + "\n");
		} catch (IOException e) {
			logger.error(e);
			System.exit(0);
		}
	}
	
	private void closeLog() {
		try {
			logWriter.close();
		} catch (IOException e) {
			logger.error(e);
			System.exit(0);
		}
	}
	
	public void writeJsonFromSobi(String sobiDirectory, String jsonDirectory) {
		openLog(jsonDirectory);
		writeJsonFromSobi(new File(sobiDirectory));
		closeLog();
	}
	
	private void writeJsonFromSobi(File sobiDirectory) {
		if(sobiDirectory.exists()) {
			if(sobiDirectory.isDirectory()) {
				File[] files = sobiDirectory.listFiles();
				
				Arrays.sort(files);
				
				for(File file:files) {
					writeJsonFromSobi(file);
				}
			}
			else {
				writeJson(sobiDirectory);
			}
		}
	}
	
	public void writeJson(File sobiFile) {
		logger.warn("Reading file: " + sobiFile.getName());
		
		timer.start();
		if(sobiFile.getName().endsWith(".TXT")) {
			
			try {
				senateObjects = basicParser.handleBill(sobiFile.getAbsolutePath(), '-');
			} catch (IOException e) {
				logger.error(e);
			}
			
		} else if(sobiFile.getName().contains("-calendar-")) {
			XmlHelper.fixCalendar(sobiFile);
			
			try {
				senateObjects = calendarParser.doParsing(sobiFile.getAbsolutePath());
			} catch (Exception e) {
				logger.error(e);
			}
			
		} else if(sobiFile.getName().contains("-agenda-")){
			senateObjects = committeeParser.doParsing(sobiFile);
		} else {
			return;
		}
		logger.warn(timer.stop() + " - Processed Objects");
		
		if(senateObjects != null) {
			timer.start();
			
			ISenateObject sObj;
			for(int i = 0; i < senateObjects.size(); i++) {
				sObj = ingestJson.mergeSenateObject(
						senateObjects.get(i), senateObjects.get(i).getClass());
				
				sObj.addSobiReference(sobiFile.getName());
				sObj.setLuceneModified(getDateFromFileName(sobiFile.getName()));
				
				String filePath = ingestJson.writeSenateObject(sObj);
				if(filePath != null) {
					logUpdate(filePath);
				}
			}
			
			logger.warn(timer.stop() + " - Wrote " + senateObjects.size() + " Objects");
		}
		
		calendarParser.clearCalendars();
		committeeParser.clearUpdates();
		basicParser.clearBills();
		senateObjects.clear();
	}
	
	public long getDateFromFileName(String fileName) {
		try {
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
		catch (Exception e) {
			logger.error(e);
		}
		return THE_TIME;
	}
	
	public File[] sortFilesByName(File[] fList) {
		Arrays.sort(fList, new Comparator<File>() {
			@Override
			public int compare(File one, File two) {
				return one.getName().compareTo(two.getName());
			}
		});
		
		return fList;
	}
}
