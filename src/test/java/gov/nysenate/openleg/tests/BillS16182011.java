package gov.nysenate.openleg.tests;

import org.junit.Test;

public class BillS16182011 extends TestSetup
{
	private static final String billKey = "2011/bill/S1618-2011";
	private static final String initialSenateSobi = "SOBI.D110110.T154119.TXT";
	private static final String senateSponsor = "Sampson";
	private static final String meetingKey2011 = "2011/meeting/meeting-Codes-7-2011-2011";
	private static final String meetingKey2012 = "2012/meeting/meeting-Codes-2-2011-2012";
	private static final String billName = "S1618-2011";
	private static final String senateVoteSobi2011 = "SOBI.D110401.T110240.TXT";
	private static final String senateVoteSobi2012 = "SOBI.D120130.T171403.TXT";
	private static final String committeeVoteSobi2011 = "SOBI.D110308.T122351.TXT-agenda-1.xml";
	private static final String committeeVoteSobi2012 = "SOBI.D120118.T144938.TXT-agenda-1.xml";
	// TODO find a better way to compare dates.
	private static final long senateVoteDate2011 = 1301544000000L; // March 31, 2011.
	private static final long senateVoteDate2012 = 1327899600000L; // Jan 20, 2012.
	private static final long committeeVoteDate2011 = 1299598200000L; // March 8, 2011.
	private static final long committeeVoteDate2012 = 1326906000000L; // Jan 18, 2012.

	// Json max of 14 characters for name -- SOBI gives 15 characters
	private static final String[] senateAyeVotes2011 = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic",
		"Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Farley", "Flanagan", "Fuschillo", "Gallivan", 
		"Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy", "Klein", "Krueger", 
		"Kruger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", 
		"Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", 
		"Rivera", "Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", 
		"Stewart-Cousin", "Valesky", "Young", "Zeldin"};
	private static final String[] senateAbsVotes2011 = { "Espaillat", "Huntley" };
	private static final String[] committeeAyeVotes2011 = {"Saland", "DeFrancisco", "Flanagan", "Fuschillo", "Gallivan", 
		"Golden", "Lanza", "Nozzolio", "O'Mara", "Gianaris", "Duane", "Huntley", "Parker", "Perkins", "Squadron", "Espaillat"};
	private static final String[] senateAyeVotes2012 = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic", 
		"Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Espaillat", "Farley", "Flanagan", "Fuschillo", 
		"Gallivan", "Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy", 
		"Klein", "Krueger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", 
		"Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", "Rivera", 
		"Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin", 
		"Valesky", "Young", "Zeldin"};
	private static final String[] senateExcusedVotes2012 = {"Huntley"};
	private static final String[] committeeAyeVotes2012 = {"Saland", "DeFrancisco", "Flanagan", "Fuschillo", "Gallivan", "Golden",
		"Lanza", "Nozzolio", "O'Mara", "Gianaris", "Huntley", "Parker", "Perkins", "Squadron", "Espaillat"};	
	private static final String[] committeeAyeWRVotes2012 = {"Duane"};

	
	// TODO test SOBI.D110331.T112352.TXT:2011S01618 --> 100000Pen L. sex. abuse 2nd degree     00000

	
	/*
	 * ------- Senate vote tests -------
	 */
	@Test
	public void testSenateVoteDate2012()
	{
		VoteTests.isVoteDateCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateVoteDate2012);
	}
	
	@Test
	public void testSenateVoteDate2011()
	{
		VoteTests.isVoteDateCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateVoteDate2011);
	}
	
	@Test
	public void testSenateAyeVotes2011()
	{
		VoteTests.areSenateAyeVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateAyeVotes2011);
	}

	@Test
	public void testSenateAbsVotes2011()
	{
		VoteTests.areSenateAbsVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateAbsVotes2011);
	}
	
	@Test
	public void testSenateAyeWRVotes2011()
	{
		VoteTests.areSenateAyeWRVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2011);
	}
	
	@Test
	public void testSenateAbstainVotes2011()
	{
		VoteTests.areSenateAbstainVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2011);
	}
	
	@Test
	public void testSenateExcusedVotes2011()
	{
		VoteTests.areSenateExcusedVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2011);
	}
	
	@Test
	public void testSenateNayVotes2011()
	{
		VoteTests.areSenateNayVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2011);
	}
	
	@Test
	public void testSenateAyeVotes2012()
	{
		VoteTests.areSenateAyeVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateAyeVotes2012);
	}

	@Test
	public void testSenateExcusedVotes2012()
	{
		VoteTests.areSenateExcusedVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateExcusedVotes2012);
	}
	
	@Test
	public void testSenateAyeWRVotes2012()
	{
		VoteTests.areSenateAyeWRVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2012);
	}
	
	@Test
	public void testSenateAbsVotes2012()
	{
		VoteTests.areSenateAbsVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2012);
	}
	
	@Test
	public void testSenateAbstainVotes2012()
	{
		VoteTests.areSenateAbstainVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2012);
	}
	
	@Test
	public void testSenateNayVotes2012()
	{
		VoteTests.areSenateNayVotesNull(env, sobiDirectory, storage, billKey, senateVoteSobi2012);
	}

	/*
	 * ------- Committee vote tests -------
	 */
	@Test
	public void testCommitteeVoteDate2011()
	{
		VoteTests.isVoteDateCorrect(env, sobiDirectory, storage, billKey, committeeVoteSobi2011, committeeVoteDate2011);
	}
	
	@Test
	public void testCommitteeAyeVotes2011()
	{
		VoteTests.areCommitteeAyeVotesCorrect(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011, committeeAyeVotes2011);
	}
	
	@Test
	public void testCommitteeAyeWRVotes2011()
	{
		VoteTests.areCommitteeAyeWRVotesNull(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011);
	}
	
	@Test
	public void testCommitteeAbsVotes2011()
	{
		VoteTests.areCommitteeAbsVotesNull(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011);
	}
	
	@Test
	public void testCommitteeAbstainVotes2011()
	{
		VoteTests.areCommitteeAbstainVotesNull(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011);
	}
	
	@Test
	public void testCommitteeAExcusedVotes2011()
	{
		VoteTests.areCommitteeExcusedVotesNull(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011);
	}
	
	@Test
	public void testCommitteeNayVotes2011()
	{
		VoteTests.areCommitteeNayVotesNull(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011);
	}
	
	@Test
	public void testCommitteeVoteDate2012()
	{
		VoteTests.isVoteDateCorrect(env, sobiDirectory, storage, billKey, committeeVoteSobi2012, committeeVoteDate2012);
	}

	@Test
	public void testCommitteeAyeVotes2012()
	{
		VoteTests.areCommitteeAyeVotesCorrect(env, sobiDirectory, storage, meetingKey2012, billName, committeeVoteSobi2012, committeeAyeVotes2012);
	}

	@Test
	public void testCommitteeAyeWRVotes2012()
	{
		VoteTests.areCommitteeAyeWRVotesCorrect(env, sobiDirectory, storage, meetingKey2012, billName, committeeVoteSobi2012, committeeAyeWRVotes2012);
	}
	
	/*
	 * ------- Other Tests -------
	 */
	@Test
	public void testInitiallyNull()
	{
		BillTests.isBillInitiallyNull(storage, billKey);
	}
	
	@Test
	public void testProcessingSenate()
	{
		BillTests.doesBillExistsAfterProcessing(env, sobiDirectory, storage, billKey, initialSenateSobi);
	}
	
	@Test
	public void testProcessingCommittee()
	{
		BillTests.doesBillExistsAfterProcessing(env, sobiDirectory, storage, billKey, committeeVoteSobi2012);
	}
	
	@Test
	public void testSenateSponsor()
	{
		BillTests.isSponserNameCorrect(env, sobiDirectory, storage, billKey, initialSenateSobi, senateSponsor);
	}
}
