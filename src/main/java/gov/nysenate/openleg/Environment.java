package gov.nysenate.openleg;

import gov.nysenate.util.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * The Environment class is essentially a configuration for an OpenLeg workspace.
 * Each workspace contains its own set of directories to store the source data files
 * as well as an associated database schema to store the data.
 *
 * Only one environment can be active at a given time but having multiple available
 * can be beneficial for testing new features on a subset of bills for example.
 */
public class Environment
{
    public final static String DEFAULT_SCHEMA = "master";

    private int id;
    private String schema;
    private final File directory;
    private final File stagingDirectory;
    private final File workingDirectory;
    private final File storageDirectory;
    private final File archiveDirectory;
    private boolean active;
    private Date createdDateTime;
    private Date modifiedDateTime;

    public Environment(String directoryPath) {
        this(new File(directoryPath));
    }

    public Environment(Config config, String prefix, String schema) {
        this.schema = schema;
        this.active = true;
        this.directory = new File(config.getValue(prefix+".directory"));
        this.stagingDirectory = new File(config.getValue(prefix+".data"));
        this.workingDirectory = new File(config.getValue(prefix+".work"));
        this.storageDirectory = new File(config.getValue(prefix+".storage"));
        this.archiveDirectory = new File(config.getValue(prefix+".archive"));
    }

    public Environment(File directory) {
        this.directory = directory;
        this.stagingDirectory = new File(directory,"data");
        this.workingDirectory = new File(directory,"work");
        this.storageDirectory = new File(directory,"json");
        this.archiveDirectory = new File(directory,"archive");
    }

    public File getDirectory() {
        return directory;
    }

    public File getStagingDirectory() {
        return stagingDirectory;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public File getStorageDirectory() {
        return storageDirectory;
    }

    public File getArchiveDirectory() {
        return archiveDirectory;
    }

    public int getId() {
        return id;
    }

    public String getSchema() {
        return schema;
    }

    public boolean isActive() {
        return active;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public Date getModifiedDateTime() {
        return modifiedDateTime;
    }

    /** TODO: Move this to the DAO */
    public void create() throws IOException {
        FileUtils.forceMkdir(directory);
        FileUtils.forceMkdir(stagingDirectory);
        FileUtils.forceMkdir(workingDirectory);
        FileUtils.forceMkdir(storageDirectory);
        FileUtils.forceMkdir(archiveDirectory);
    }

    /** TODO: Move this to the DAO */
    public void delete() throws IOException {
        FileUtils.deleteQuietly(directory);
    }

    /** TODO: Move this to the DAO */
    public void reset() throws IOException {
        delete();
        create();
    }
}
