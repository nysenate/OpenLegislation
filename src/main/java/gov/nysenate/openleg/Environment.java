package gov.nysenate.openleg;

import gov.nysenate.util.Config;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class Environment
{
    private final File directory;
    private final File stagingDirectory;
    private final File workingDirectory;
    private final File storageDirectory;
    private final File archiveDirectory;

    public Environment(String directoryPath)
    {
        this(new File(directoryPath));
    }

    public Environment(Config config, String prefix)
    {
        this.directory = new File(config.getValue(prefix+".directory"));
        this.stagingDirectory = new File(config.getValue(prefix+".data"));
        this.workingDirectory = new File(config.getValue(prefix+".work"));
        this.storageDirectory = new File(config.getValue(prefix+".storage"));
        this.archiveDirectory = new File(config.getValue(prefix+".archive"));
    }

    public Environment(File directory)
    {
        this.directory = directory;
        this.stagingDirectory = new File(directory,"data");
        this.workingDirectory = new File(directory,"work");
        this.storageDirectory = new File(directory,"json");
        this.archiveDirectory = new File(directory,"archive");
    }

    public File getDirectory()
    {
        return directory;
    }

    public File getStagingDirectory()
    {
        return stagingDirectory;
    }

    public File getWorkingDirectory()
    {
        return workingDirectory;
    }

    public File getStorageDirectory()
    {
        return storageDirectory;
    }

    public File getArchiveDirectory()
    {
        return archiveDirectory;
    }

    public void create() throws IOException
    {
        FileUtils.forceMkdir(directory);
        FileUtils.forceMkdir(stagingDirectory);
        FileUtils.forceMkdir(workingDirectory);
        FileUtils.forceMkdir(storageDirectory);
        FileUtils.forceMkdir(archiveDirectory);
    }

    public void delete() throws IOException
    {
        FileUtils.deleteQuietly(directory);
    }

    public void reset() throws IOException
    {
        delete();
        create();
    }
}
