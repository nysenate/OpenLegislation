package gov.nysenate.openleg.tests;

import org.junit.Test;

public class BillS16182011 extends TestSetup
{
	private static final String billKey = "2011/bill/S1618-2011";
	private static final String meetingKey2011 = "2011/meeting/meeting-Codes-7-2011-2011";
	private static final String meetingKey2012 = "2012/meeting/meeting-Codes-2-2011-2012";
	private static final String billName = "S1618-2011";
	private static final String senateVoteSobi2011 = "SOBI.D110401.T110240.TXT";
	private static final String senateVoteSobi2012 = "SOBI.D120130.T171403.TXT";
	private static final String committeeVoteSobi2011 = "SOBI.D110308.T122351.TXT-agenda-1.xml";
	private static final String committeeVoteSobi2012 = "SOBI.D120118.T144938.TXT-agenda-1.xml";

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

	@Test
	public void testSenateAyeVotes2011()
	{
		BillTests.areSobiAyeVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateAyeVotes2011);
	}

	@Test
	public void testSenateAbsVotes2011()
	{
		BillTests.areSobiAbsVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateAbsVotes2011);
	}

	@Test
	public void testCommitteeAyeVotes2011()
	{
		BillTests.areCommitteeAyeVotesCorrect(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011, committeeAyeVotes2011);
	}

	@Test
	public void testSenateAyeVotes2012()
	{
		BillTests.areSobiAyeVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateAyeVotes2012);
	}

	@Test
	public void testSenateExcusedVotes2012()
	{
		BillTests.areSobiExcusedVotesCorrect(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateExcusedVotes2012);
	}

	@Test
	public void testCommitteeAyeVotes2012()
	{
		BillTests.areCommitteeAyeVotesCorrect(env, sobiDirectory, storage, meetingKey2012, billName, committeeVoteSobi2012, committeeAyeVotes2012);
	}

	@Test
	public void testCommitteeAyeWRVotes2012()
	{
		BillTests.areCommitteeAyeWRVotesCorrect(env, sobiDirectory, storage, meetingKey2012, billName, committeeVoteSobi2012, committeeAyeWRVotes2012);
	}
}
