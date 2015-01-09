package gov.nysenate.openleg.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import java.io.File;

/**
 * The Environment class contains various configuration options to be used throughout the application.
 * This class is mutable during runtime so that hot config changes can be made to the fields here whereas
 * the property file is only checked during initialization.
 *
 * The fields in this class are primarily wired directly to values in the property file. Many fields
 * have setters to allow for changes while the application is running.
 */
@Component
public class Environment
{
    /** The database schema where the legislative data is stored. */
    @Value("${env.schema:master}") private String schema;

    /** --- File system configuration --- */

    /** The root directory path where all data files are contained within. */
    @Value("${env.base}") private String envDirPath;

    /** The directory path where all incoming data files are contained. */
    @Value("${env.staging}") private String stagingDirPath;

    /** The directory path where all archived data files are contained. */
    @Value("${env.archive}") private String archiveDirPath;

    /** The directory path for scraped LBDC files. */
    @Value("${env.scraped.calendar}") private String calendarDirPath;
    @Value("${env.scraped.assemblyagenda}") private String assemblyAgendaDirPath;
    @Value("${env.scraped.senateagenda}") private String senateAgendaDirPath;
    
    private File baseDir;
    private File stagingDir;
    private File archiveDir;
    
    private File calendarDirectory;
    private File assemblyAgendaDirectory;
    private File senateAgendaDirectory;

    /** --- Api Auth --- */

    /** A secret key used to allow access to the API through the front-end. */
    @Value("${api.secret}") private String apiSecret;

    /** --- Search Index settings --- */

    /** Allow elastic search to index documents. */
    @Value("${elastic.search.enabled}") private boolean elasticIndexing;

    /** --- Processing settings --- */

    /** Enable processing of data. */
    @Value("${data.process.enabled}") private boolean processingEnabled;

    /** Allows for the option to enable/disable logging. */
    @Value("${data.process.log.enabled}") private boolean processLoggingEnabled;

    /** Enable batch processing of SOBI files. */
    @Value("${sobi.batch.process.enabled}") private boolean sobiBatchEnabled;

    /** If SOBI batch is enabled, this specifies the maximum batch size. */
    @Value("${sobi.batch.process.size}") private int sobiBatchSize;

    /** --- Scheduling Settings --- */

    /** Enable processing of data at scheduled intervals. */
    @Value("${scheduler.process.enabled}") private boolean processingScheduled;

    /** Enable spot-check report runs at scheduled intervals. */
    @Value("${scheduler.spotcheck.enabled}") private boolean spotcheckScheduled;

    /** --- Domain Url --- */

    @Value ("${domain.url}") private String domain;
    private String contextPath;
    private String fullPath;

    /** --- Constructors --- */

    public Environment() {}

    @PostConstruct
    private void init() {
        this.baseDir = new File(envDirPath);
        this.stagingDir = new File(stagingDirPath);
        this.archiveDir = new File(archiveDirPath);
        this.calendarDirectory = new File(calendarDirPath);
        this.assemblyAgendaDirectory = new File(assemblyAgendaDirPath);
        this.senateAgendaDirectory = new File(senateAgendaDirPath);
    }

    /** --- Basic Getters/Setters --- */

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public File getStagingDir() {
        return stagingDir;
    }

    public File getArchiveDir() {
        return archiveDir;
    }

    public boolean isElasticIndexing() {
        return elasticIndexing;
    }

    public void setElasticIndexing(boolean elasticIndexing) {
        this.elasticIndexing = elasticIndexing;
    }

    public boolean isProcessingEnabled() {
        return processingEnabled;
    }

    public void setProcessingEnabled(boolean processingEnabled) {
        this.processingEnabled = processingEnabled;
    }

    public boolean isProcessLoggingEnabled() {
        return processLoggingEnabled;
    }

    public void setProcessLoggingEnabled(boolean processLoggingEnabled) {
        this.processLoggingEnabled = processLoggingEnabled;
    }

    public boolean isSobiBatchEnabled() {
        return sobiBatchEnabled;
    }

    public void setSobiBatchEnabled(boolean sobiBatchEnabled) {
        this.sobiBatchEnabled = sobiBatchEnabled;
    }

    public int getSobiBatchSize() {
        return sobiBatchSize;
    }

    public void setSobiBatchSize(int sobiBatchSize) {
        this.sobiBatchSize = sobiBatchSize;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public boolean isProcessingScheduled() {
        return processingScheduled;
    }

    public void setProcessingScheduled(boolean processingScheduled) {
        this.processingScheduled = processingScheduled;
    }

    public boolean isSpotcheckScheduled() {
        return spotcheckScheduled;
    }

    public void setSpotcheckScheduled(boolean spotcheckScheduled) {
        this.spotcheckScheduled = spotcheckScheduled;
    }

    public File getCalendarDirectory() {
        return calendarDirectory;
    }

    public void setCalendarDirectory(File calendarDirectory) {
        this.calendarDirectory = calendarDirectory;
    }

    public File getSenateAgendaDirectory() {
        return senateAgendaDirectory;
    }

    public void setSenateAgendaDirectory(File senateAgendaDirectory) {
        this.senateAgendaDirectory = senateAgendaDirectory;
    }

    public File getAssemblyAgendaDirectory() {
        return assemblyAgendaDirectory;
    }

    public void setAssemblyAgendaDirectory(File assemblyAgendaDirectory) {
        this.assemblyAgendaDirectory = assemblyAgendaDirectory;
    }

    public String getDomain() {
        return domain;
    }

    public String getFullPath() {
        return fullPath;
    }
}