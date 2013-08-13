package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

public class BillA83542011 extends TestSetup
{
    public static final String billKey = "2011/bill/A8354-2011";
    public static final String initialSobi = "SOBI.D110614.T162232.TXT";
    public static final String billTextSobi = "SOBI.D110614.T162734.TXT";
    public static final String voteSobi = "SOBI.D110624.T230028.TXT";
    public static final String actionsSobi = "SOBI.D110625.T001531.TXT";

    @Test
    public void isSponsorCorrect()
    {
        String billSponsor = "O'Donnell";
        BillTests.testPrimeSponsor(env, sobiDirectory, storage, billKey, initialSobi, billSponsor, false);
    }

    @Test
    public void testCoSponsors()
    {
        String[] billCoSponsors = { "Gottfried", "Glick", "Titone", "Kellner", "Bronson", "Rivera J", "Silver", "Farrell",
                "Sayward", "Lentol", "Nolan", "Weisenberg", "Arroyo", "Brennan", "Dinowitz", "Hoyt", "Lifton", "Millman", "Cahill", "Paulin",
                "Reilly", "Bing", "Jeffries", "Jaffee", "Rosenthal", "Kavanagh", "DenDekker", "Schimel", "Hevesi", "Benedetto", "Schroeder",
                "Miller J", "Lavine", "Lancman", "Linares", "Moya", "Roberts", "Simotas", "Abinanti", "Braunstein" };
        BillTests.testCoSponsors(env, sobiDirectory, storage, billKey, initialSobi, billCoSponsors);
    }

    @Test
    public void testMultiSponsors()
    {
        String[] billMultiSponsors = { "Aubry", "Boyland", "Brook-Krasny", "Canestrari", "Cook", "Duprey", "Englebright",
                "Latimer", "Lopez V", "Lupardo", "Magnarelli", "McEneny", "Morelle", "Ortiz", "Pretlow", "Ramos", "Rivera N", "Rivera P",
                "Rodriguez", "Russell", "Sweeney", "Thiele", "Titus", "Weprin", "Wright", "Zebrowski" };
        BillTests.testMultiSponsors(env, sobiDirectory, storage, billKey, initialSobi, billMultiSponsors);
    }

    @Test
    public void testTitle()
    {
        String billTitle = "Enacts the Marriage Equality Act relating to ability of individuals to marry";
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
        String[] ayeVotes = { "Adams", "Addabbo", "Alesi", "Avella", "Breslin", "Carlucci", "Dilan", "Duane", "Espaillat", "Gianaris",
                "Grisanti", "Hassell-Thomps", "Huntley", "Kennedy", "Klein", "Krueger", "Kruger", "McDonald", "Montgomery", "Oppenheimer",
                "Parker","Peralta", "Perkins", "Rivera", "Saland", "Sampson", "Savino", "Serrano", "Smith", "Squadron", "Stavisky",
                "Stewart-Cousin", "Valesky" };
        String[] nayVotes = {"Ball", "Bonacic", "DeFrancisco", "Diaz", "Farley", "Flanagan", "Fuschillo", "Gallivan", "Golden",
                "Griffo", "Hannon", "Johnson", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz",
                "Nozzolio", "O'Mara", "Ranzenhofer", "Ritchie", "Robach", "Seward", "Skelos", "Young", "Zeldin" };
        Vote expectedVote = new Vote();
        // Add any non-null vote types to vote object.
        expectedVote.setAyes(Arrays.asList(ayeVotes));
        expectedVote.setNays(Arrays.asList(nayVotes));
        VoteTests.testSenateVotes(env, sobiDirectory, storage, billKey, voteSobi, expectedVote);
    }

    @Test
    public void testVoteDate()
    {
        String voteDate = "6/24/11";
        VoteTests.testVoteDate(env, sobiDirectory, storage, billKey, voteSobi, voteDate);
    }

    @Test
    public void testLawSection()
    {
        String expectedLawSection = "Domestic Relations Law";
        BillTests.testLawSection(env, sobiDirectory, storage, billKey, initialSobi, expectedLawSection);
    }

    @Test
    public void testActions()
    {
        String billNumber = "A8354-2011";
        ArrayList<String[]> actionStrings = new ArrayList<String[]>();
        actionStrings.add(new String[]{"6/14/11", "referred to judiciary"});
        actionStrings.add(new String[]{"06/15/11", "reported referred to rules"});
        actionStrings.add(new String[]{"06/15/11", "reported"});
        actionStrings.add(new String[]{"06/15/11", "rules report cal.320"});
        actionStrings.add(new String[]{"06/15/11", "ordered to third reading rules cal.320"});
        actionStrings.add(new String[]{"06/15/11", "message of necessity - 3 day message"});
        actionStrings.add(new String[]{"06/15/11", "passed assembly"});
        actionStrings.add(new String[]{"06/15/11", "delivered to senate"});
        actionStrings.add(new String[]{"06/24/11", "ORDERED TO THIRD READING CAL.1545"});
        actionStrings.add(new String[]{"06/24/11", "MESSAGE OF NECESSITY"});
        actionStrings.add(new String[]{"06/24/11", "PASSED SENATE"});
        actionStrings.add(new String[]{"06/24/11", "RETURNED TO ASSEMBLY"});
        actionStrings.add(new String[]{"06/24/11", "delivered to governor"});
        actionStrings.add(new String[]{"06/24/11", "signed chap.95"});

        BillTests.testBillStatusActions(env, sobiDirectory, storage, billKey, actionsSobi, actionStrings, new Bill(billNumber, 2011));
    }

    @Test
    public void testLaw()
    {
        String law = "Add SS10-a & 10-b, amd SS13 & 11, Dom Rel L";
        BillTests.testBillLaw(env, sobiDirectory, storage, billKey, billTextSobi, law);
    }

    @Test
    public void testSameAs()
    {
        // The string "No same as" in a sobi gets ignored when parsed into json.
        String sameAs = "No same as";
        BillTests.testSameAs(env, sobiDirectory, storage, billKey, initialSobi, sameAs);
    }

    @Test
    public void testActToClause()
    {
        String clause = "AN ACT to amend the domestic relations law, in relation to the ability to marry";
        BillTests.testActToClause(env, sobiDirectory, storage, billKey, initialSobi, clause);
    }

    @Test
    public void testBillSummary()
    {
        String summary = "Enacts the Marriage Equality Act relating to ability of individuals to marry.";
        BillTests.testBillSummary(env, sobiDirectory, storage, billKey, initialSobi, summary);
    }

    @Test
    public void testSponsorMemo()
    {
        String memo = "";
        BillTests.testSponsorMemo(env, sobiDirectory, storage, billKey, initialSobi, memo);
    }
}
