package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class TestHelper 
{
    public static void processFile(Environment env, File[] testFiles)
    {
        try {
        	env.stageFiles(testFiles);
			env.collateFiles(FileUtils.listFiles(env.getStagingDirectory(), null, true));
		}
        catch (IOException e) {
			e.printStackTrace();
		}
        env.ingestFiles(FileUtils.listFiles(env.getWorkingDirectory(), null, true));
    }

    public static File[] getFilesByName(File directory, String...names)
    {
        return getFilesByName(directory, Arrays.asList(names)).toArray(new File[]{});
    }

    public static Collection<File> getFilesByName(File directory, Collection<String> names)
    {
        Collection<File> files = new ArrayList<File>();
        for (String name : names) {
            File file = new File(directory, name);
            files.add(file);
        }
        return files;
    }
}
