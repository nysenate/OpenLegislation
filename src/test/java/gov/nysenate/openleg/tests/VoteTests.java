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
	}

	public static void areSenateVotesCorrect(Environment env, File sobiDirectory,
			Storage storage, String billKey, String voteSobi, Vote expected)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, voteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Bill bill = TestHelper.getBill(storage, billKey);
		Vote testVote = bill.getVotes().get(0);
		// Look into ListAssert.assertEquals if we don't care about the order of the lists.
		assertThat(testVote.getAyes(), is(expected.getAyes()));
		assertThat(testVote.getAyeswr(), is(expected.getAyeswr()));
		assertThat(testVote.getAbsent(), is(expected.getAbsent()));
		assertThat(testVote.getAbstains(), is(expected.getAbstains()));
		assertThat(testVote.getExcused(), is(expected.getExcused()));
		assertThat(testVote.getNays(), is(expected.getNays()));
	}

	public static void areCommitteeVotesCorrect(Environment env, File sobiDirectory, Storage storage,
			String meetingKey, String billName, String committeeVoteSobi, Vote expected)
	{
		File[] voteSobiFile = TestHelper.getFilesByName(sobiDirectory, committeeVoteSobi);
		TestHelper.processFile(env, voteSobiFile);
		Meeting meeting = TestHelper.getMeeting(storage, meetingKey);
		List<Bill> bills = meeting.getBills();
		Bill bill = TestHelper.getBillByName(bills, billName);
		Vote testVote = bill.getVotes().get(0);
		assertThat(testVote.getAyes(), is(expected.getAyes()));
		assertThat(testVote.getAyeswr(), is(expected.getAyeswr()));
		assertThat(testVote.getAbsent(), is(expected.getAbsent()));
		assertThat(testVote.getAbstains(), is(expected.getAbstains()));
		assertThat(testVote.getExcused(), is(expected.getExcused()));
		assertThat(testVote.getNays(), is(expected.getNays()));
	}

}
