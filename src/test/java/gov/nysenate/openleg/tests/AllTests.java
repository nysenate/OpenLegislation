package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.Storage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.abdera.model.Collection;
import org.junit.Before;
import org.junit.Test;

public class AllTests {
	
	Environment env;
	@Before
	public void prepareEnv() throws IOException
	{
		
	 env= new Environment("/home/shweta/testing_environment");
	env.reset();
    
   
	}
	
	@Test
	public void testDeleteWhole()
	{   
		File sobiDirectory = new File("/home/shweta/OpenLegislation/src/test/resources/sobi");
	    Storage storage = new Storage(env.getStorageDirectory());
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202049.TXT");
	    TestHelper.processFile(env, initialCommit);
	    File[] finalCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202549.TXT");
	    TestHelper.processFile(env, finalCommit);
	    Bill theBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class); 
	    assertThat(theBill,nullValue());
	    
		
	}
	@Test
	public void verifyWholeBills() throws IOException
	{
		
		File sobiDirectory = new File("/home/shweta/test/processed/2013/bills/");
	    Storage storage = new Storage(env.getStorageDirectory());
	    ArrayList<File> file = (ArrayList<File>) TestHelper.getFilesByNameCollection(sobiDirectory, "SOBI.D130114.T213149.TXT", "SOBI.D130115.T124554.TXT", "SOBI.D130114.T212649.TXT", "SOBI.D130115.T095048.TXT", "SOBI.D130115.T124054.TXT", "SOBI.D130115.T093046.TXT", "SOBI.D130116.T122729.TXT", "SOBI.D130114.T225652.TXT", "SOBI.D130114.T233653.TXT", "SOBI.D130115.T093547.TXT", "SOBI.D130116.T120230.TXT", "SOBI.D130115.T162604.TXT", "SOBI.D130114.T232653.TXT", "SOBI.D130116.T115728.TXT", "SOBI.D130114.T223651.TXT", "SOBI.D130115.T162104.TXT", "SOBI.D130115.T132056.TXT", "SOBI.D130115.T094047.TXT", "SOBI.D130115.T101048.TXT");
	    for(File f:file)
	    {
	    TestHelper.processFileC(env ,f );
	    Bill theBill = (Bill)storage.get("2013/bill/S2230-2013", Bill.class); 
	    // is it that all SOBI files do not have title
	    assertNotNull("Should not be null", theBill);
	  
	    System.out.println( f.getName());
	    System.out.println(theBill.getTitle());
	    
	    //assertEquals(theBill.getSponsor().getFullname(),"Klien");
	    env.reset();
	    }
	    
	    
	    
	    
	}
}
