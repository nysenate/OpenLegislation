package gov.nysenate.openleg.tests;

import org.junit.*;

/*
 * This class tests the Sobi files for bill S6696.
 * All tests needed for this bill are done here
 * All information related to this bill is stored here.
 */
public class BillS66962011 extends TestSetup
{
	private static final String billKey = "2011/bill/S6696-2011"; // Directory and name of expected json file within testing environment.
	private static final String initialSobi = "SOBI.D120311.T201049.TXT";
	private static final String initialBillText = "SOBI.D120311.T201549.TXT";
	private static final String billStatusDelete = "SOBI.D120311.T202549.TXT";
	private static final String billTextDelete = "SOBI.D120311.T202049.TXT";
	private static final String secondBillCommit = "SOBI.D120312.T000059.TXT";
	private static final String nullSponsorSobi = "SOBI.D120312.T092623.TXT";
	private static final String sponsorName = "NOZZOLIO";

	/* 
	 * All tests needed for this bill go here.
	 * Limit tests to 1 per method to provide better error message feedback, keep the testing environment clean, and to prevent any
	 * tests from being skipped.
	 */
	@Test
	public void isBillInitiallyNull()
	{
		BillTests.isBillInitiallyNull(storage, billKey);
	}

	@Test
	public void doesExistsAfterProcessing()
	{
		BillTests.doesBillExistsAfterProcessing(env, sobiDirectory, storage, billKey, initialSobi);
	}

	@Test
	public void isSponsorNameCorrect()
	{
		BillTests.isSponserNameCorrect(env, sobiDirectory, storage, billKey, initialSobi, sponsorName);

	}

	@Test
	public void doesBillTextExist()
	{
		BillTests.doesBillTextExist(env, sobiDirectory, storage, billKey, initialBillText);
	}

	@Test
	public void doesEntireBillDeleteWork()
	{
		BillTests.doesEntireBillDeleteWork(env, sobiDirectory, storage, billKey, billStatusDelete, initialSobi, initialBillText);
	}

	@Test
	public void doesFullTextGetDeleted()
	{
		BillTests.doesFullTextGetDeleted(env, sobiDirectory, storage, billKey, initialBillText, billTextDelete);
	}

	@Test
	public void doesNullSponsorChangeBill()
	{
		BillTests.doesNullSponsorDelete(env, sobiDirectory, storage, billKey, nullSponsorSobi, initialSobi, initialBillText);
	}
}
