package gov.nysenate.openleg.tests;

import org.junit.*;

public class BillS66962011 extends TestSetup
{
	private static final String billKey = "2011/bill/S6696-2011"; // Directory and name of expected json file within testing environment.
	private static final String initialSobi = "SOBI.D120311.T201049.TXT";
	private static final String initialBillText = "SOBI.D120311.T201549.TXT";
	private static final String billStatusDelete = "SOBI.D120311.T202549.TXT";
	private static final String billTextDelete = "SOBI.D120311.T202049.TXT";
	//private static final String secondBillCommit = "SOBI.D120312.T000059.TXT";
	private static final String nullSponsorSobi = "SOBI.D120312.T092623.TXT";


	@Test
	public void testIfBillInitiallyNull()
	{
		BillTests.isBillInitiallyNull(storage, billKey);
	}

	@Test
	public void testIfBillExistsAfterProcessing()
	{
		BillTests.doesBillExistsAfterProcessing(env, sobiDirectory, storage, billKey, initialSobi);
	}

	@Test
	public void testIfSponsorNameCorrect()
	{
		String sponsorName = "NOZZOLIO";
		BillTests.testPrimeSponsor(env, sobiDirectory, storage, billKey, initialSobi, sponsorName, false);

	}

	@Test
	public void testIfBillTextExist()
	{
		BillTests.doesBillTextExist(env, sobiDirectory, storage, billKey, initialBillText);
	}

	@Test
	public void testIfEntireBillDeleteWork()
	{
		BillTests.doesEntireBillDeleteWork(env, sobiDirectory, storage, billKey, billStatusDelete, initialSobi, initialBillText);
	}

	@Test
	public void testIfFullTextGetsDeleted()
	{
		BillTests.doesFullTextGetDeleted(env, sobiDirectory, storage, billKey, initialBillText, billTextDelete);
	}

	/*
	 * Tests if "00000 00000 0000" in bill status line will change anything.
	 * test assumes it shouldn't.
	 */
	@Test
	public void testIfNullSponsorChangeBill()
	{
		BillTests.testIrregularBillStatusLines(env, sobiDirectory, storage, billKey, nullSponsorSobi, initialSobi, initialBillText);
	}
}
