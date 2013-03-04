package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;

import org.junit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DeleteTests
{
	private Environment env = new Environment("/data/openleg/test_new_environment");
	private File sobiDirectory = new File("/home/kevin/openleg/OpenLegislation/src/test/resources/sobi");  // -- /legislation/src/test/resources/sobi??
	private Storage storage = new Storage(env.getStorageDirectory());
	
	@Before
	public void setup()
	{
		try {
			env.reset();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Should json be deleted or just empty?
	@Test
	public void doesEntireBillGetDeleted()
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T201049.TXT");
		TestHelper.processFile(env, initialCommit);
    	//Bill initialBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
    	//assertThat(initialBill, notNullValue());
		File[] deleteCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202549.TXT");
		TestHelper.processFile(env, deleteCommit);
    	Bill deletedBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
		assertThat(deletedBill, nullValue());
	}
	
	@Test
	public void doesFullTextGetDeleted()
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T201549.TXT");
		TestHelper.processFile(env, initialCommit);
    	//Bill initialBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
    	//assertThat(initialBill.getFulltext(), notNullValue()); //covered in basic test, not needed
    	
		File[] deleteTextCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120311.T202049.TXT");
		TestHelper.processFile(env, deleteTextCommit);
    	Bill deletedTextBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
		assertThat(deletedTextBill, notNullValue());
		assertThat(deletedTextBill.getFulltext(), nullValue());
	
	}
	
	
	/*
	 * Will "100000 00000 0000" in the first line of SOBI will delete sponsor name?
	 * Test says it doesnt
	 */
	@Test
	public void doesNullSponsorDelete() // Needs better name
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120312.T000059.TXT");
		TestHelper.processFile(env, initialCommit);
    	Bill initialBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
    	//assertThat(initialBill.getSponsor().getFullname(), notNullValue());
    	
		File[] emptyCommit = TestHelper.getFilesByName(sobiDirectory, "SOBI.D120312.T092623.TXT");
		TestHelper.processFile(env, emptyCommit);
    	Bill nullSponsorBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
    	assertThat(nullSponsorBill.getSponsor().getFullname(), is(initialBill.getSponsor().getFullname()));
	}
	
}
