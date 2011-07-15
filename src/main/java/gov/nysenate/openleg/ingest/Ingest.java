package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.bill.Bill;
import gov.nysenate.openleg.model.calendar.Calendar;
import gov.nysenate.openleg.model.committee.Agenda;
import gov.nysenate.openleg.model.transcript.Transcript;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.serialize.XmlHelper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class Ingest {
	
	private static final String JSON_DIRECTORY = "json-directory";
	private static final String SOBI_DIRECTORY = "sobi-directory";
	private static final String WRITE = "write";
	private static final String INDEX = "index";
	private static final String WRITE_TRANSCRIPT = "write-transcript";
	private static final String INDEX_DOCUMENT = "index-document";
	private static final String DOCUMENT_TYPE = "document-type";
	private static final String REINDEX_AMENDED_BILLS = "reindex-amended-bills";
	private static final String RESET_INACTIVE_BILLS = "reset-inactive-bills";
	private static final String FIX_SUMMARIES = "fix-all-summaries";
	private static final String GENERATE_XML = "generate-xml";
	private static final String HELP = "help";
	
	public static void main(String[] args) throws IngestException {
		Ingest ingest = null;
		
		CommandLineParser parser = new PosixParser();
		
		Options options = new Options();
				
		options.addOption("sd", SOBI_DIRECTORY, true, "The path to your SOBI directory");
		options.addOption("jd", JSON_DIRECTORY, true, "The path to your JSON directory");
		
		options.addOption("w", WRITE, false, "Write SOBI's in sobi-directory to JSON in json-directory");
		options.addOption("i", INDEX, false, "Index logged changes");
		options.addOption("wt", WRITE_TRANSCRIPT, true, "Write transcripts located in directory specified by argument");
		
		options.addOption("id", INDEX_DOCUMENT, true, "Index JSON document specified by argument (path to file)");
		options.addOption("dt", DOCUMENT_TYPE, true, "Type of document being indexed with -id (REQUIRED WITH -id).. (bill|calendar|agenda|transcript)");
		
		options.addOption("rab", REINDEX_AMENDED_BILLS, false, "Scans index and marks amended bills as inactive");
		options.addOption("rib", RESET_INACTIVE_BILLS, false, "Resets all inactive bills");

		options.addOption("fs", FIX_SUMMARIES, false, "Scans index, attempts to assign summaries to bills " +
				"where information was never received from LBDC");
		
		options.addOption("gx", GENERATE_XML, false, "Will pull XML data from SOBI documents");
		
		options.addOption("h", HELP, false, "Print this message");
		
		try {
		    CommandLine line = parser.parse(options, args);
		    
		    if(line.hasOption("-h")) {
		    	HelpFormatter formatter = new HelpFormatter();
		    	formatter.printHelp("posix", options );
		    }
		    else {
		    	if(line.hasOption(SOBI_DIRECTORY) && line.hasOption(JSON_DIRECTORY)) {
		    		String sobiDir = line.getOptionValue(SOBI_DIRECTORY);
		    		String jsonDir = line.getOptionValue(JSON_DIRECTORY);
		    		
		    		ingest = new Ingest(sobiDir, jsonDir);
		    		
		    		ingest.lock();
		    		
		    		if(line.hasOption(WRITE)) {
		    			ingest.write();
		    		}
		    		if(line.hasOption(INDEX)) {
		    			ingest.index();
		    		}
		    		
		    		if(line.hasOption(WRITE_TRANSCRIPT)) {
		    			String transcriptDir = line.getOptionValue(WRITE_TRANSCRIPT);
		    			ingest.writeTranscripts(transcriptDir);
		    		}
		    		
		    		if(line.hasOption(INDEX_DOCUMENT) && line.hasOption(DOCUMENT_TYPE)) {
		    			String path = line.getOptionValue(INDEX_DOCUMENT);
		    			String type = line.getOptionValue(DOCUMENT_TYPE);
		    			
		    			ingest.indexJsonDocument(type, path);
		    		}
		    		else {
		    			if(line.hasOption(INDEX_DOCUMENT) || line.hasOption(DOCUMENT_TYPE)) {
		    				throw new org.apache.commons.cli.ParseException(
		    						"To index a single document you must use both " 
		    						+ INDEX_DOCUMENT + " and " + DOCUMENT_TYPE);
		    			}
		    		}
		    		
		    		if(line.hasOption(REINDEX_AMENDED_BILLS)) {
		    			ingest.reindexAmendedBills();
		    		}
		    		
		    		if(line.hasOption(RESET_INACTIVE_BILLS)) {
		    			ingest.resetInactiveBills();
		    		}
		    		
		    		if(line.hasOption(FIX_SUMMARIES)) {
		    			ingest.fixSummaries();
		    		}
		    		
		    		if(line.hasOption(GENERATE_XML)) {
		    			XmlHelper.generateXml(sobiDir);
		    		}
		    	}
		    	else {
		    		throw new org.apache.commons.cli.ParseException(
		    				SOBI_DIRECTORY + " and " + JSON_DIRECTORY + " are both required parameters.");
		    	}
		    }
		}
	    catch( org.apache.commons.cli.ParseException exp ) {
		    System.out.println( "Unexpected exception:" + exp.getMessage() );
		}
	    finally {
	    	if(ingest != null){
	    		ingest.unlock();
	    	}
	    }
	}
	
	private static String LOCK_FILE = ".lock";
	private final String LOG_FILE = ".log";
		
	private Logger logger = Logger.getLogger(Ingest.class);
	
	String sobiDirectory;
	String jsonDirectory;
	
	SearchEngine searchEngine;
	JsonDao jsonDao;
	IngestJsonWriter ingestJsonWriter;
	IngestIndexWriter ingestIndexWriter;
	
	public Ingest(String sobiDirectory, String jsonDirectory) {
		this.sobiDirectory = sobiDirectory;
		this.jsonDirectory = jsonDirectory;
		
		searchEngine = SearchEngine.getInstance();
		jsonDao = new JsonDao(jsonDirectory, jsonDirectory + LOG_FILE);
		ingestJsonWriter = new IngestJsonWriter(jsonDao, searchEngine);
		ingestIndexWriter = new IngestIndexWriter(jsonDirectory, 
												  jsonDirectory + LOG_FILE, 
												  searchEngine, 
												  jsonDao);
	}
	
	public void write() {
		ingestJsonWriter.writeJsonFromDirectory(sobiDirectory);
	}
	
	public void index() {
		ingestIndexWriter.indexBulk();
	}
	
	public void reindexAmendedBills() {
		ingestIndexWriter.markInactiveBills();
	}
	
	public void resetInactiveBills() {
		ingestIndexWriter.resetInactiveBills();
	}
	
	public void fixSummaries() {
		ingestIndexWriter.fixSummaries();
	}
	
	public void writeTranscripts(String transcriptDirectory) {
		ingestJsonWriter.writeTranscriptsFromDirectory(transcriptDirectory);
	}
	
	public void indexJsonDocument(String type, String path) {
		IngestType ingestType = getIngestType(type);
		
		if(ingestType != null) {
			SenateObject senateObject = jsonDao.load(path, ingestType.clazz());
			
			if(senateObject != null) {
				try {
					searchEngine.indexSenateObject(senateObject);
				} catch (IOException e) {
					logger.error(e);
				}
			}
			else {
				logger.warn("couldn't load file " + path + "with type: " + type);
			}
		}
		else {
			logger.warn("no associated IngestType: " + type);
		}
	}
	
	public boolean lock() {
		File lock = new File(jsonDirectory + "/" + LOCK_FILE);
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
		File lock = new File(jsonDirectory + "/" + LOCK_FILE);
		if(lock.exists()) {
			return lock.delete();
		}
		return false;
	}	
	
	public static ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SerializationConfig cnfg = mapper.getSerializationConfig();
		cnfg.set(Feature.INDENT_OUTPUT, true);
		mapper.setSerializationConfig(cnfg);
		
		return mapper;
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
	
	public static IngestType getIngestType(String type) {
		for(IngestType ingestType:IngestType.values()) {
			if(ingestType.type().equalsIgnoreCase(type))
				return ingestType;
		}
		return null;
	}
	
	public enum IngestType {
		BILL("bill", Bill.class),
		CALENDAR("calendar", Calendar.class),
		AGENDA("agenda", Agenda.class),
		TRANSCRIPT("transcript", Transcript.class);
		
		private String type;
		private Class<? extends SenateObject> clazz;
		
		private IngestType(String type, Class<? extends SenateObject> clazz) {
			this.type = type;
			this.clazz = clazz;
		}
		
		public String type() {
			return type;
		}
		
		public Class<? extends SenateObject> clazz() {
			return clazz;
		}
	}
	
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
		
				Commit the changes made to the file system
				start = System.currentTimeMillis();
				commit(file.getName());
				logger.warn(((System.currentTimeMillis()-start))/1000.0+" - Committed Changes");
		
	*/
}
