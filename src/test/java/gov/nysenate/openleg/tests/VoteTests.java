package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;
import gov.nysenate.openleg.util.Storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class VoteTests 
{
	// Testing bill 2011S01618
	private static final String senateVote11 = "SOBI.D110401.T110240.TXT";
	private static final String senateVote12 = "SOBI.D120130.T171403.TXT";
	// Json max of 14 characters for name -- SOBI gives 15 characters
	private static final String[] S1618_2011AyeSenateVotes = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic",
		"Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Farley", "Flanagan", "Fuschillo", "Gallivan", 
		"Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy", "Klein", "Krueger", 
		"Kruger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", 
		"Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", 
		"Rivera", "Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", 
		"Stewart-Cousin", "Valesky", "Young", "Zeldin"};
	private static final String[] S1618_2011AbsSenateVotes = { "Espaillat", "Huntley" };
	private static final String[] S1618_2012AyeSenateVotes = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic", 
		"Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Espaillat", "Farley", "Flanagan", "Fuschillo", 
		"Gallivan", "Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy", 
		"Klein", "Krueger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald", 
		"Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", "Rivera", 
		"Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin", 
		"Valesky", "Young", "Zeldin"};
	private static final String[] S1618_2012ExcusedSenateVotes = {"Huntley"};



	private static Environment env;
	private static File sobiDirectory;
	private static Storage storage; 

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
	/*
	@Test
	public void testss()
	{
		File[] sobis = TestHelper.getFilesByName(sobiDirectory, senateVote11);
		Bill bill = (Bill)storage.get("2011/bill/S1618-2011", Bill.class);
		testAyeVotes(env, storage, sobis, bill, S1618_2011AyeSenateVotes);
	}
	 */
	@Test
	public void does2011VotesWork()
	{
		File[] voteSobi = TestHelper.getFilesByName(sobiDirectory, senateVote11);
		TestHelper.processFile(env, voteSobi);
		Bill billVotes = (Bill)storage.get("2011/bill/S1618-2011", Bill.class);
		Vote vote = billVotes.getVotes().get(0);
		assertThat(vote.getAyes(), containsInAnyOrder(S1618_2011AyeSenateVotes));
		assertThat(vote.getAbsent(), containsInAnyOrder(S1618_2011AbsSenateVotes));
		assertThat(vote.getNays(), empty());
	}

	@Test
	public void does2012VotesWork()
	{
		File[] voteSobi = TestHelper.getFilesByName(sobiDirectory, senateVote12);
		TestHelper.processFile(env, voteSobi);
		Bill billVotes = (Bill)storage.get("2011/bill/S1618-2011", Bill.class);
		Vote vote = billVotes.getVotes().get(0);
		assertThat(vote.getAyes(), containsInAnyOrder(S1618_2012AyeSenateVotes));
		assertThat(vote.getExcused(), containsInAnyOrder(S1618_2012ExcusedSenateVotes));
		assertThat(vote.getAbsent(), empty());
		assertThat(vote.getNays(), empty());
	}

	/*
	public void testAyeVotes(Environment env, Storage stg, File[] sobis, Bill bill, String[] expectedAyeVotes)
	{
		TestHelper.processFile(env, sobis);
		// Is there only 1 vote per bill? should have assembly and senate votes.... no assembly votes until xml processed?
		//Vote vote = bill.getVotes().get(0);
		//assertThat(vote.getAyes(), containsInAnyOrder(expectedAyeVotes));
	}
	 */
}
