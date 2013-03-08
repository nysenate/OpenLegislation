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
	private static final String initialBill = "SOBI.D120311.T201049.TXT";
	private static final String initialBillText = "SOBI.D120311.T201549.TXT";
	private static final String billStatusDelete = "SOBI.D120311.T202549.TXT";
	private static final String billTextDelete = "SOBI.D120311.T202049.TXT";
	private static final String secondBillCommit = "SOBI.D120312.T000059.TXT";
	private static final String nullSponsor = "SOBI.D120312.T092623.TXT";

	protected static Environment env;
	protected static File sobiDirectory;
	protected static Storage storage; 

	@BeforeClass
	public static void setup()
	{
		env = new Environment("/data/openleg/test_new_environment");
		sobiDirectory = new File("src/test/resources/sobi");
		storage = new Storage(env.getStorageDirectory());
	}	

	@Before
	public void reset()
	{
		try {
			env.reset();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void doesEntireBillGetDeleted()
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, initialBill);
		TestHelper.processFile(env, initialCommit);
		File[] deleteCommit = TestHelper.getFilesByName(sobiDirectory, billStatusDelete);
		TestHelper.processFile(env, deleteCommit);
		Bill deletedBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
		assertThat(deletedBill, nullValue());
	}

	@Test
	public void doesFullTextGetDeleted()
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, initialBillText);
		TestHelper.processFile(env, initialCommit);   	
		File[] deleteTextCommit = TestHelper.getFilesByName(sobiDirectory, billTextDelete);
		TestHelper.processFile(env, deleteTextCommit);
		Bill deletedTextBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
		String expectedText = "";
		assertThat(deletedTextBill.getFulltext(), is(expectedText));

	}


	/*
	 * Will "100000 00000 0000" in the first line of SOBI will delete sponsor name?
	 * Test says it does not.
	 */
	@Test
	public void doesNullSponsorDelete() // Needs better name
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, secondBillCommit);
		TestHelper.processFile(env, initialCommit);
		Bill initialBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);    	
		File[] emptyCommit = TestHelper.getFilesByName(sobiDirectory, nullSponsor);
		TestHelper.processFile(env, emptyCommit);
		Bill nullSponsorBill = (Bill)storage.get("2011/bill/S6696-2011", Bill.class);
		assertThat(nullSponsorBill.getSponsor().getFullname(), is(initialBill.getSponsor().getFullname()));
		// Test if anything else got changed.
		assertThat(initialBill.equals(nullSponsorBill), is(true));
	}

}
