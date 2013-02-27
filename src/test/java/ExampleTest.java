import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.FileUtils;


public class ExampleTest
{
    public static void main(String[] args) throws IOException
    {
        Environment env = new Environment("/data/openleg/test_new_environment");
        File sobiDirectory = new File("/data/openleg/processed/2013/bills/");
        File[] testFiles = getFilesByName(sobiDirectory, "SOBI.D130221.T121245.TXT", "SOBI.D130221.T143250.TXT");
        env.reset();
        env.stageFiles(testFiles);
        env.collateFiles(FileUtils.listFiles(env.getStagingDirectory(), null, true));
        env.ingestFiles(FileUtils.listFiles(env.getWorkingDirectory(), null, true));
        Storage storage = new Storage(env.getStorageDirectory());
        Bill theBill = (Bill)storage.get("2013/bill/J554-2013", Bill.class);
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
