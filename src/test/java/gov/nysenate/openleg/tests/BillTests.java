package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Vote;
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

	public static void isSponserNameCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String sobi, String expectedSponsorName)
	{
		File[] initialCommit = TestHelper.getFilesByName(sobiDirectory, sobi);
		TestHelper.processFile(env, initialCommit);
		Bill bill = TestHelper.getBill(storage, billKey);
		String billSponsorName = bill.getSponsor().getFullname();
		assertThat(billSponsorName, is(expectedSponsorName));
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
		//Bill initialBill = TestHelper.getBill(storage, billKey);
		//assertThat(initialBill, notNullValue());
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
	 * Will "100000 00000 0000" in the first line(Status Line) of SOBI will delete anything from the bill?
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
		assertThat(initialBill.equals(nullSponsorBill), is(true)); // TODO I think bills are just pointers so this is doing nothing.
	}

	// All Vote Tests can only test single votes, cannot handle multiple votes in a bill TODO
	public static void areSobiAyeVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAyes)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyes(), containsInAnyOrder(expectedAyes));
	}

	public static void areSobiAbsVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAbs)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbsent(), containsInAnyOrder(expectedAbs));
	}

	public static void areSobiExcusedVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedExcused)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getExcused(), containsInAnyOrder(expectedExcused));
	}

	public static void areSobiAyeWRVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAyeWR)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyeswr(), containsInAnyOrder(expectedAyeWR));
	}

	public static void areSobiNayVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedNay)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getNays(), containsInAnyOrder(expectedNay));
	}

	public static void areSobiAbstainVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAbstain)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbstains(), containsInAnyOrder(expectedAbstain));
	}

	public static void areCommitteeAyeVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi, String[] expectedAyeVotes)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyes(), containsInAnyOrder(expectedAyeVotes));
	}

	public static void areCommitteeAyeWRVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi, String[] expectedAyeWRVotes)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyeswr(), containsInAnyOrder(expectedAyeWRVotes));
	}

}