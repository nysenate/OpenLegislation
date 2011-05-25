package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.model.ISenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.EasyReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class Ingest {
	
	public static void main(String[] args) throws IngestException {
		Ingest ir2 = new Ingest(SOBI_DIRECTORY, JSON_DIRECTORY);
		
		ir2.lock();
		
		ir2.index();
		
		ir2.unlock();
	}
	
	private static String LOCK_FILE = ".lock";
	
	private static String SOBI_DIRECTORY = "/Users/jaredwilliams/Desktop/2011/sobi/";
	private static String JSON_DIRECTORY = "/Users/jaredwilliams/Desktop/json/";
		
	private Logger logger = Logger.getLogger(Ingest.class);
	
	String sobiDirectory;
	String jsonDirectory;
	
	SearchEngine searchEngine;
	JsonDao jsonDao;
	IngestSobiParser ingestSobiParser;
	IngestIndexWriter ingestIndexWriter;
	
	public Ingest(String sobiDirectory, String jsonDirectory) {
		this.sobiDirectory = sobiDirectory;
		this.jsonDirectory = jsonDirectory;
		
		searchEngine = SearchEngine.getInstance();
		jsonDao = new JsonDao(JSON_DIRECTORY);
		ingestSobiParser = new IngestSobiParser(jsonDao);
		ingestIndexWriter = new IngestIndexWriter(searchEngine, jsonDao);
	}
	
	public void write() {
		ingestSobiParser.writeJsonFromSobi(sobiDirectory, jsonDirectory);
	}
	
	public void index() {
		File file = new File(jsonDirectory + "/.log");

		EasyReader er = new EasyReader(file).open();
		
		TreeSet<String> set = new TreeSet<String>();
		
		ArrayList<ISenateObject> lst;
		Object[] files;
		
		Pattern p = Pattern.compile("\\d{4}/(\\w+)/.*$");
		Matcher m = null;
		
		String in = null;
		while((in = er.readLine()) != null) {
			set.add(in);
		}
		er.close();
		
		files = (Object[]) set.toArray();
		
		int its = files.length/1000;
		for(int i = 0; i <= its; i++) {
			
			lst = new ArrayList<ISenateObject>();
			
			for(int j = (i * 1000); j < (((i+1) * 1000)) && j < files.length; j++) {
				m = p.matcher((String)files[j]);
				if(m.find()) {
					ISenateObject senObj = jsonDao.loadSenateObject((String)files[j], getIngestType(m.group(1)).clazz());
					if(senObj != null)
						lst.add(senObj);
				}
			}
			
			
			lst.clear();
		}
	}
	
	public boolean lock() {
		File lock = new File(JSON_DIRECTORY + "/" + LOCK_FILE);
		if(lock.exists())
			return false;
		
		lock.deleteOnExit();
		
		try {
			return lock.createNewFile();
		} catch (IOException e) {
			logger.error(e);
		}
		
		return false;
	}
	
	public boolean unlock() {
		File lock = new File(JSON_DIRECTORY + "/" + LOCK_FILE);
		if(lock.exists()) {
			return lock.delete();
		}
		return false;
	}	
	
	@SuppressWarnings("serial")
	public class IngestException extends Exception {
		public IngestException() {
			super();
		}
		public IngestException(String msg) {
			super(msg);
		}
	}
	
	public static ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SerializationConfig cnfg = mapper.getSerializationConfig();
		cnfg.set(Feature.INDENT_OUTPUT, true);
		mapper.setSerializationConfig(cnfg);
		
		return mapper;
	}
	
	public enum IngestType {
		BILL("bill", Bill.class),
		CALENDAR("calendar", Calendar.class),
		AGENDA("agenda", Agenda.class),
		TRANSCRIPT("transcript", Transcript.class);
		
		private String type;
		private Class<? extends ISenateObject> clazz;
		
		private IngestType(String type, Class<? extends ISenateObject> clazz) {
			this.type = type;
			this.clazz = clazz;
		}
		
		public String type() {
			return type;
		}
		
		public Class<? extends ISenateObject> clazz() {
			return clazz;
		}
	}
	
	public IngestType getIngestType(String type) {
		for(IngestType ingestType:IngestType.values()) {
			if(ingestType.type().equalsIgnoreCase(type))
				return ingestType;
		}
		return null;
	}
	
	
	
	
	/*
	
	
	public void handleTranscript(File file) {
		if(file.isDirectory()) {
			for(File temp:file.listFiles()) {
				handleTranscript(temp);
			}
		}
		else if (file.getName().contains(".TXT")){
			logger.warn("Reading transcript file " + file.getAbsolutePath());
			
			Transcript trans = null;
			
			//transcripts often come incorrectly formatted..
			//this attempts to reprocess and save the raw text
			//if there is a parsing error, and then attempts
			//parsing one more time
			try {				
				trans = basicParser.handleTranscript(file.getAbsolutePath());

			} catch (Exception e) {
				try {
					List<String> in;
					TranscriptFixer fixer = new TranscriptFixer();
					
					if((in = fixer.readContents(file)) != null) {
						
						List<String> ret = fixer.fix(in);
						BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
						
						for(String s:ret) {
							bw.write(s);
							bw.newLine();
						}
						
						bw.close();
						trans = basicParser.handleTranscript(file.getAbsolutePath());
					}
					
				} catch (Exception e2) {
					e2.printStackTrace();
					return; //We couldn't get a good read on the transcript
				}
				return;
			}
			
			//Conduct general processing, writing, and indexing
			processSenateObject(trans,Transcript.class,false);
			if(writeSenateObject(trans)) {
				logger.warn("writing and indexing transcript");
				indexSenateObject(trans);
			}
		}
		
	}
	
	
	
	*/
	
	
	
	
	/*
	public void bulkIndexJsonDirectory(Class<? extends ISenateObject> clazz, String directory) throws IOException {
		luceneObjects = new ArrayList<ILuceneObject>();
		
		long start = System.currentTimeMillis();
		doBulk(clazz, directory);
		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Read " + luceneObjects.size() + " Objects");
		
		start = System.currentTimeMillis();
		this.searchEngine.indexSenateObjects(
				luceneObjects,
				new LuceneSerializer[]{	new XmlSerializer(), new JsonSerializer()});
		luceneObjects.clear();
		logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Indexed Objects");
	}
	
	private void doBulk(Class<? extends ISenateObject> clazz, String directory) {
		File file = new File(directory);
		if(file.isDirectory()) {
			for(File f:file.listFiles()) {
				doBulk(clazz, f.getAbsolutePath());
			}
		}
		else {
			ISenateObject obj = loadObject(directory, clazz);
			if(obj instanceof Bill) {
				if(reindexAmendedVersions((Bill)obj))
					obj.setLuceneActive(false);
			}
			luceneObjects.add(obj);
		}
	}
	*/
	
	
	
	
	
	
	
	/* 
	 * git functions (currently unused)
	 * 
		public void commit(String message) {
			//Condensed for speed, don't pull the pieces out into a "run" func
			try {
				String line;
				Process process;
				BufferedReader error;
				File repository = new File(WRITE_DIRECTORY);
				Runtime runtime = Runtime.getRuntime();
				
				if(! new File(WRITE_DIRECTORY+"/.git").exists()) {
					process = runtime.exec("git init",null,repository);
					error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
					while((line = error.readLine()) != null)
						logger.error(line);
				}

				process = runtime.exec("git add .",null,repository);
				error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while((line = error.readLine()) != null)
					logger.error(line);
				
				process = runtime.exec("git commit -m "+message+"\"",null,repository);
				error = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				while((line = error.readLine()) != null)
					logger.error(line);
				
			} catch (IOException e) {
				logger.error(e);
			}
			
		}
		
		//not in use
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
		
		//not in use
		public void gitCommit(String message) {
			Git git = getRepo(WRITE_DIRECTORY);
			try {
				git.add().addFilepattern(".").call();
				git.commit().setMessage(message).setAuthor("Tester", "notmyfault@nysenate.gov").call();
			} catch(WrongRepositoryStateException e) {
				e.printStackTrace();
			} catch (NoHeadException e) {
				e.printStackTrace();
			} catch (NoMessageException e) {
				e.printStackTrace();
			} catch (UnmergedPathException e) {
				e.printStackTrace();
			} catch (ConcurrentRefUpdateException e) {
				e.printStackTrace();
			} catch (JGitInternalException e) {
				e.printStackTrace();
			} catch (NoFilepatternException e) {
				e.printStackTrace();
			}
		}
		
		commented out until a reasonable repo solution is found
				
				Commit the changes made to the file system
				start = System.currentTimeMillis();
				commit(file.getName());
				logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Committed Changes");
		
	*/
}
