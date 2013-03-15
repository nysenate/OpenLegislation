package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BillTests
{
	public static void isBillInitiallyNull(Storage storage, String billKey)
	{
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill, nullValue());
	}

	public static void doesBillExistsAfterProcessing(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill, notNullValue());
	}

	// TODO should this ignore 
	public static void isSponserNameCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String expectedSponsorName)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		String billSponsorName = bill.getSponsor().getFullname();
		assertThat(billSponsorName, equalToIgnoringCase(expectedSponsorName));
	}

	public static void doesBillTextExist(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi)
	{
		File[] billTextSobi = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, billTextSobi);
		Bill bill = TestHelper.getBill(storage, billKey);
		assertThat(bill.getFulltext(), notNullValue());
	}

	public static void doesEntireBillDeleteWork(Environment env, File sobiDirectory,
			Storage storage, String billKey, String deleteStatusSobi, String...initialSobis)
	{
		File[] commitFile;
		List<String> sobiCommits = Arrays.asList(initialSobis);
		for(String commit: sobiCommits){
			commitFile = TestHelper.getFilesByName(sobiDirectory, commit);
			TestHelper.processFile(env, commitFile);
		}
		File[] deleteCommit = TestHelper.getFilesByName(sobiDirectory, deleteStatusSobi);
		TestHelper.processFile(env, deleteCommit);
		Bill deletedBill = TestHelper.getBill(storage, billKey);
		assertThat(deletedBill, nullValue());
	}

	public static void doesFullTextGetDeleted(Environment env, File sobiDirectory,
			Storage storage, String billKey, String billTextSobi, String deleteTextSobi)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, billTextSobi);
		TestHelper.processFile(env, initialCommit);
		File[] deleteTextCommit = TestHelper.getFilesByName(sobiDirectory, deleteTextSobi);
		TestHelper.processFile(env, deleteTextCommit);
		Bill deletedTextBill = TestHelper.getBill(storage, billKey);
		String expectedText = "";
		assertThat(deletedTextBill.getFulltext(), is(expectedText));
	}

	/*
	 * Will "00000 00000 0000" in the first line(Status Line) of SOBI will delete anything from the bill?
	 * Test says it does not.
	 * TODO make name more informative.
	 */
	public static void doesNullSponsorDelete(Environment env, File sobiDirectory,
			Storage storage, String billKey, String nullStatusSobi, String...billSobis)
	{
		File[] commitFile;
		List<String> sobiCommits = Arrays.asList(billSobis);
		for(String commit: sobiCommits){
			commitFile = TestHelper.getFilesByName(sobiDirectory, commit);
			TestHelper.processFile(env, commitFile);
		}
		Bill initialBill = TestHelper.getBill(storage, billKey);    	
		File[] emptyCommit = TestHelper.getFilesByName(sobiDirectory, nullStatusSobi);
		TestHelper.processFile(env, emptyCommit);
		Bill nullSponsorBill = TestHelper.getBill(storage, billKey);
		assertThat(nullSponsorBill.getSponsor().getFullname(), is(initialBill.getSponsor().getFullname()));
		// Test if anything else got changed.
		assertThat(initialBill.equals(nullSponsorBill), is(true)); // TODO is this working correctly?
	}

}