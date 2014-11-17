package gov.nysenate.openleg.model.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

/**
 * The Environment class contains various configuration options to be used throughout the application.
 * This class is mutable during runtime so that hot config changes can be made to the fields here whereas
 * the property file is only checked during initialization.
 */
@Component
public class Environment
{
    @Value("${env.schema:master}") private String schema;

    /** --- File system configuration --- */

    @Value("${env.directory}") private String envDirPath;
    @Value("${env.staging}") private String stagingDirPath;
    @Value("${env.archive}") private String archiveDirPath;

    private File baseDirectory;
    private File stagingDirectory;
    private File archiveDirectory;

    /** --- Auth Stuff --- */

    @Value("${default.api.secret}")
    private String defaultApiSecret;

    /** --- Search Index settings --- */

    @Value("${elastic.indexing.enabled:true}")
    private boolean elasticIndexing = true;

    /** --- Processing settings --- */

    @Value("${incremental.update:true}") private boolean incrementalUpdates;
    @Value("${sobi.batch.size:100}") private int sobiBatchSize;

    /** --- Constructors --- */

    public Environment() {}

    @PostConstruct
    private void init() {
        this.baseDirectory = new File(envDirPath);
        this.stagingDirectory = new File(stagingDirPath);
        this.archiveDirectory = new File(archiveDirPath);
    }

    /** --- Basic Getters/Setters --- */

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public File getBaseDirectory() {
        return baseDirectory;
    }

    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public File getStagingDirectory() {
        return stagingDirectory;
    }

    public void setStagingDirectory(File stagingDirectory) {
        this.stagingDirectory = stagingDirectory;
    }

    public File getArchiveDirectory() {
        return archiveDirectory;
    }

    public void setArchiveDirectory(File archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    public boolean isElasticIndexing() {
        return elasticIndexing;
    }

    public void setElasticIndexing(boolean elasticIndexing) {
        this.elasticIndexing = elasticIndexing;
    }

    public boolean isIncrementalUpdates() {
        return incrementalUpdates;
    }

    public void setIncrementalUpdates(boolean incrementalUpdates) {
        this.incrementalUpdates = incrementalUpdates;
    }

    public int getSobiBatchSize() {
        return sobiBatchSize;
    }

    public void setSobiBatchSize(int sobiBatchSize) {
        this.sobiBatchSize = sobiBatchSize;
    }

    public String getDefaultApiSecret() {
        return defaultApiSecret;
    }
}