import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;


public class ExampleTest
{
    public static void main(String[] args) throws IOException
    {
        Environment env = new Environment("/home/shweta/testing_environment");
        env.reset();
        /*File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
        File[] testFiles = getFilesByName(sobiDirectory, "SOBI.D130221.T121245.TXT", "SOBI.D130221.T143250.TXT");
       
        env.stageFiles(testFiles);
        env.collateFiles(FileUtils.listFiles(env.getStagingDirectory(), null, true));
        env.ingestFiles(FileUtils.listFiles(env.getWorkingDirectory(), null, true));
        Storage storage = new Storage(env.getStorageDirectory());
        Bill theBill = (Bill)storage.get("2013/bill/J554-2013", Bill.class); */
        testS066962011(env);
    }
    public static void testS066962011(Environment env)
    {
    	//  /legislation/src/test/resources/ -- doesnt work?
    	File sobiDirectory = new File("/home/shweta/OpenLegislation/src/test/resources/sobi");
        Storage storage = new Storage(env.getStorageDirectory());
    	Assert.assertNull((Bill)storage.get("2011/bill/S6696-2011", Bill.class));
    	
    	// Process and test first SOBI.
    	File[] initialCommit = getFilesByName(sobiDirectory, "SOBI.D120311.T201049.TXT");
    	processFile(env, initialCommit);
    	Bill bill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
    	Assert.assertNotNull(bill);
    	testSponsorName("NOZZOLIO", bill);
    	
    	// Process and test second SOBI.
    	initialCommit = getFilesByName(sobiDirectory, "SOBI.D120311.T201549.TXT");
    	processFile(env, initialCommit);
    	Assert.assertNotNull(bill); 
    	System.out.println(bill.getSponsor().getFullname());

    	testSponsorName("NOZZOLIO", bill);
    	
    }
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


    public static void testSponsorName(String expectedName, Bill bill)
    {
    	Assert.assertEquals(expectedName, bill.getSponsor().getFullname());

    }

    
    public static File[] getFilesByName(File directory, String...names)
    {
        return getFilesByName(directory, Arrays.asList(names)).toArray(new File[]{});
    }
    public static Collection<File> getFilesByNameCollection(File directory, String...names)
    {
        return getFilesByName(directory, Arrays.asList(names));
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
