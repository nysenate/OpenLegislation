package gov.nysenate.openleg.tests;

import java.util.Arrays;
import gov.nysenate.openleg.model.Vote;
import org.junit.Test;

public class BillA83542011 extends TestSetup
{
	public static final String billKey = "2011/bill/A8354-2011";
	public static final String billSponsor = "O'Donnell";
	public static final String billTitle = "Enacts the Marriage Equality Act relating to ability of individuals to marry";
	public static final String voteDate = "6/24/11";

	public static final String initialSobi = "SOBI.D110614.T162232.TXT";
	public static final String billTextSobi = "SOBI.D110614.T162734.TXT";
	public static final String voteSobi = "SOBI.D110624.T230028.TXT";

	public static final String[] billCoSponsors = { "Gottfried", "Glick", "Titone", "Kellner", "Bronson", "Rivera J", "Silver", "Farrell",
		"Sayward", "Lentol", "Nolan", "Weisenberg", "Arroyo", "Brennan", "Dinowitz", "Hoyt", "Lifton", "Millman", "Cahill", "Paulin",
		"Reilly", "Bing", "Jeffries", "Jaffee", "Rosenthal", "Kavanagh", "DenDekker", "Schimel", "Hevesi", "Benedetto", "Schroeder",
		"Miller J", "Lavine", "Lancman", "Linares", "Moya", "Roberts", "Simotas", "Abinanti", "Braunstein" };
	public static final String[] billMultiSponsors = { "Aubry", "Boyland", "Brook-Krasny", "Canestrari", "Cook", "Duprey", "Englebright",
		"Latimer", "Lopez V", "Lupardo", "Magnarelli", "McEneny", "Morelle", "Ortiz", "Pretlow", "Ramos", "Rivera N", "Rivera P",
		"Rodriguez", "Russell", "Sweeney", "Thiele", "Titus", "Weprin", "Wright", "Zebrowski" };
	public static final String[] ayeVotes = { "Adams", "Addabbo", "Alesi", "Avella", "Breslin", "Carlucci", "Dilan", "Duane", "Espaillat",
		"Gianaris",	"Grisanti", "Hassell-Thomps", "Huntley", "Kennedy", "Klein", "Krueger", "Kruger", "McDonald", "Montgomery", "Oppenheimer",
		"Parker","Peralta", "Perkins", "Rivera", "Saland", "Sampson", "Savino", "Serrano", "Smith", "Squadron", "Stavisky", "Stewart-Cousin",
	"Valesky" };
	public static final String[] nayVotes = {"Ball", "Bonacic", "DeFrancisco", "Diaz", "Farley", "Flanagan", "Fuschillo", "Gallivan",
		"Golden", "Griffo", "Hannon", "Johnson", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz",
		"Nozzolio", "O'Mara", "Ranzenhofer", "Ritchie", "Robach", "Seward", "Skelos", "Young", "Zeldin" };

	@Test
	public void isSponsorCorrect()
	{
		BillTests.isSponserNameCorrect(env, sobiDirectory, storage, billKey, initialSobi, billSponsor);
	}

	@Test
	public void testCoSponsors()
	{
		BillTests.areCoSponsorsCorrect(env, sobiDirectory, storage, billKey, initialSobi, billCoSponsors);
	}

	@Test
	public void testMultiSponsors()
	{
		BillTests.areMultiSponsorsCorrect(env, sobiDirectory, storage, billKey, initialSobi, billMultiSponsors);
	}

	@Test
	public void testTitle()
	{
		BillTests.testBillTitle(env, sobiDirectory, storage, billKey, initialSobi, billTitle);
	}

	@Test
	public void testBillText()
	{
		BillTests.doesBillTextExist(env, sobiDirectory, storage, billKey, billTextSobi);
	}

	@Test
	public void testVotes()
	{
		Vote expectedVote = new Vote();
		// Add any non-null vote types to vote object.
		expectedVote.setAyes(Arrays.asList(ayeVotes));
		expectedVote.setNays(Arrays.asList(nayVotes));
		VoteTests.areSenateVotesCorrect(env, sobiDirectory, storage, billKey, voteSobi, expectedVote);
	}

	@Test
	public void testVoteDate()
	{
		VoteTests.isVoteDateCorrect(env, sobiDirectory, storage, billKey, voteSobi, voteDate);
	}	

}
