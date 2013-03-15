package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Meeting;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class VoteTests {

	public static void isVoteDateCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, long expectedDate)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getVoteDate(), is(new Date(expectedDate)));
		System.out.println(vote.getVoteDate().toString());
	}

	// TODO All Vote Tests can only test single votes, cannot handle multiple votes in a bill
	// TODO Allow more than 1 sobi to be sent for vote information.
	// 	 -what if a sobi contains more than 1 vote? ( can a sobi have more than 1 vote on the same bill?? )
	
	/*
	 * ------- Senate Vote Tests -------
	 */
	public static void areSenateAyeVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAyes)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyes(), containsInAnyOrder(expectedAyes));
	}
	
	public static void areSenateAyeWRVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAyeWR)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyeswr(), containsInAnyOrder(expectedAyeWR));
	}

	public static void areSenateAbsVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAbs)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbsent(), containsInAnyOrder(expectedAbs));
	}

	public static void areSenateExcusedVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedExcused)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getExcused(), containsInAnyOrder(expectedExcused));
	}

	public static void areSenateNayVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedNay)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getNays(), containsInAnyOrder(expectedNay));
	}

	public static void areSenateAbstainVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, String[] expectedAbstain)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbstains(), containsInAnyOrder(expectedAbstain));
	}
	
	public static void areSenateAyeVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyes(), is(empty()));
	}
	
	public static void areSenateAyeWRVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyeswr(), is(empty()));
	}
	
	public static void areSenateAbsVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbsent(), is(empty()));
	}
	
	public static void areSenateExcusedVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getExcused(), is(empty()));
	}
	
	public static void areSenateNayVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getNays(), is(empty()));
	}
	
	public static void areSenateAbstainVotesNull(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbstains(), is(empty()));
	}

	
	/*
	 * ------- Committee Vote Tests -------
	 */
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

	public static void areCommitteeNayVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi, String[] expectedNayVotes)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getNays(), containsInAnyOrder(expectedNayVotes));
	}
	
	// TODO finish the rest of committee vote tests
	
	public static void areCommitteeAyeVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyes(), is(empty()));
	}
	
	public static void areCommitteeAyeWRVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAyeswr(), is(empty()));
	}
	
	public static void areCommitteeAbsVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbsent(), is(empty()));
	}
	
	public static void areCommitteeExcusedVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getExcused(), is(empty()));
	}
	
	public static void areCommitteeNayVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getNays(), is(empty()));
	}
	
	public static void areCommitteeAbstainVotesNull(Environment env, File sobiDirectory,
			Storage storage, String meetingKey, String billName, String committeeVoteSobi)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote vote = bill.getVotes().get(0);
		assertThat(vote.getAbstains(), is(empty()));
	}
}
