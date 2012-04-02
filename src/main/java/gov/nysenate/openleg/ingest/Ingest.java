package gov.nysenate.openleg.ingest;

import gov.nysenate.openleg.ingest.hook.CacheHook;
import gov.nysenate.openleg.ingest.hook.Hook;
import gov.nysenate.openleg.model.Agenda;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Calendar;
import gov.nysenate.openleg.model.SenateObject;
import gov.nysenate.openleg.model.Transcript;
import gov.nysenate.openleg.search.SearchEngine;
import gov.nysenate.openleg.util.Config;
import gov.nysenate.openleg.util.serialize.XmlHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;

public class Ingest {

    private static final String LOG_FILE = ".log";

    public static void main(String[] args) throws IngestException {
        Ingest ingest = null;
        Options options = buildOptions();
        try {
            CommandLine line = new PosixParser().parse(options, args);
            if(line.hasOption("-h")) {
                new HelpFormatter().printHelp("posix", options );
                System.exit(0);
            }

            String sobiDir = line.hasOption("sobi-directory") ? line.getOptionValue("sobi-directory") : Config.get("data.sobi");
            String jsonDir = line.hasOption("json-directory") ? line.getOptionValue("json-directory") : Config.get("data.json");

            if( sobiDir == null || jsonDir == null ) {
                throw new org.apache.commons.cli.ParseException("sobi-directory and json-directory are both required parameters.");
            }

            SearchEngine searchEngine = SearchEngine.getInstance();
            JsonDao jsonDao = new JsonDao(jsonDir, jsonDir + LOG_FILE);

            ArrayList<Hook<List<? extends SenateObject>>> hooks = new ArrayList<Hook<List<? extends SenateObject>>>();

            if(line.hasOption("purge-cache")) {
                hooks.add(new CacheHook());
            }

            ingest = new Ingest(sobiDir, jsonDir, SearchEngine.getInstance(),
                    jsonDao,
                    new IngestJsonWriter(jsonDao, searchEngine),
                    new IngestIndexWriter(
                            jsonDir,
                            jsonDir + LOG_FILE,
                            searchEngine,
                            jsonDao,
                            hooks));

            ingest.lock();

            if(line.hasOption("write")) {
                ingest.write();
            }
            if(line.hasOption("index")) {
                ingest.index(hooks);
            }

            if(line.hasOption("write-transcript")) {
                String transcriptDir = line.getOptionValue("write-transcript");
                ingest.writeTranscripts(transcriptDir);
            }

            if(line.hasOption("index-document") && line.hasOption("document-type")) {
                String path = line.getOptionValue("index-document");
                String type = line.getOptionValue("document-type");

                ingest.indexJsonDocument(type, path);
            }
            else {
                if(line.hasOption("index-document") || line.hasOption("document-type")) {
                    throw new org.apache.commons.cli.ParseException(
                            "To index a single document you must use both "
                                    + "index-document" + " and " + "document-type");
                }
            }

            if(line.hasOption("reindex-amended-bills")) {
                ingest.reindexAmendedBills();
            }

            if(line.hasOption("reset-inactive-bills")) {
                ingest.resetInactiveBills();
            }

            if(line.hasOption("fix-all-summaries")) {
                ingest.fixSummaries();
            }

            if(line.hasOption("generate-xml")) {
                XmlHelper.generateXml(sobiDir);
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

    public static Options buildOptions() {
        Options options = new Options();

        options.addOption("sd", "sobi-directory", true, "The path to your SOBI directory");
        options.addOption("jd", "json-directory", true, "The path to your JSON directory");

        options.addOption("w", "write", false, "Write SOBI's in sobi-directory to JSON in json-directory");
        options.addOption("i", "index", false, "Index logged changes");
        options.addOption("pc", "purge-cache", false, "Purge cache while indexing");
        options.addOption("wt", "write-transcript", true, "Write transcripts located in directory specified by argument");

        options.addOption("id", "index-document", true, "Index JSON document specified by argument (path to file)");
        options.addOption("dt", "document-type", true, "Type of document being indexed with -id (REQUIRED WITH -id).. (bill|calendar|agenda|transcript)");

        options.addOption("rab", "reindex-amended-bills", false, "Scans index and marks amended bills as inactive");
        options.addOption("rib", "reset-inactive-bills", false, "Resets all inactive bills");

        options.addOption("fs", "fix-all-summaries", false, "Scans index, attempts to assign summaries to bills where information was never received from LBDC");

        options.addOption("gx", "generate-xml", false, "Will pull XML data from SOBI documents");

        options.addOption("h", "help", false, "Print this message");
        return options;
    }

    /**************************************
     *  Start Class
     *************************************/

    private final String LOCK_FILE = ".lock";

    private final Logger logger = Logger.getLogger(Ingest.class);

    String sobiDirectory;
    String jsonDirectory;

    JsonDao jsonDao;
    SearchEngine searchEngine;
    IngestJsonWriter ingestJsonWriter;
    IngestIndexWriter ingestIndexWriter;

    public Ingest(String sobiDirectory, String jsonDirectory, SearchEngine searchEngine,
            JsonDao jsonDao, IngestJsonWriter ingestJsonWriter,
            IngestIndexWriter ingestIndexWriter) {
        this.sobiDirectory = sobiDirectory;
        this.jsonDirectory = jsonDirectory;

        this.searchEngine = searchEngine;
        this.jsonDao = jsonDao;
        this.ingestJsonWriter = ingestJsonWriter;
        this.ingestIndexWriter = ingestIndexWriter;
    }

    public void write() {
        ingestJsonWriter.writeJsonFromDirectory(sobiDirectory);
    }

    public void index(ArrayList<Hook<List<? extends SenateObject>>> hooks) {
        ingestIndexWriter.indexBulk(hooks);
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
                ingestIndexWriter.indexList(Arrays.asList(senateObject));
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
                File repository = new File("write"_DIRECTORY);
                Runtime runtime = Runtime.getRuntime();

                if(! new File("write"_DIRECTORY+"/.git").exists()) {
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
