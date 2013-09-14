package gov.nysenate.openleg.tests;

import gov.nysenate.openleg.model.Bill;
import gov.nysenate.openleg.model.Vote;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;


public class BillS16182011 extends TestSetup
{
    private static final String billKey = "2011/bill/S1618-2011"; // Directory and name of expected json file within testing environment.
    private static final String initialSenateSobi = "SOBI.D110110.T154119.TXT";
    private static final String billName = "S1618-2011";
    private static final String senateVoteSobi2011 = "SOBI.D110401.T110240.TXT";
    private static final String senateVoteSobi2012 = "SOBI.D120130.T171403.TXT";
    private static final String committeeVoteSobi2011 = "SOBI.D110308.T122351.TXT-agenda-1.xml";
    private static final String committeeVoteSobi2012 = "SOBI.D120118.T144938.TXT-agenda-1.xml";
    private static final String coSponsorsSobi = "SOBI.D120113.T165033.TXT";
    private static final String billLawSobi = "SOBI.D110110.T161630.TXT";

    /*
     * ------- Senate Vote Tests -------
     */
    @Test
    public void testSenateVoteDate2011()
    {
        String senateVoteDate2011 = "3/31/11";
        VoteTests.testVoteDate(env, sobiDirectory, storage, billKey, senateVoteSobi2011, senateVoteDate2011);
    }

    @Test
    public void testSenateVoteDate2012()
    {
        String senateVoteDate2012 = "1/30/12";
        VoteTests.testVoteDate(env, sobiDirectory, storage, billKey, senateVoteSobi2012, senateVoteDate2012);
    }

    /*
     * For Vote tests, add any non-null vote types to vote objects.
     */
    @Test
    public void testSenateVote2011()
    {
        String[] senateAyeVotes2011 = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic",
                "Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Farley", "Flanagan", "Fuschillo", "Gallivan",
                "Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy", "Klein", "Krueger",
                "Kruger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald",
                "Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie",
                "Rivera", "Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky",
                "Stewart-Cousin", "Valesky", "Young", "Zeldin"};
        String[] senateAbsVotes2011 = { "Espaillat", "Huntley" };
        Vote expected = new Vote();
        expected.setAyes(Arrays.asList(senateAyeVotes2011));
        expected.setAbsent(Arrays.asList(senateAbsVotes2011));
        VoteTests.testSenateVotes(env, sobiDirectory, storage, billKey, senateVoteSobi2011, expected);
    }

    @Test
    public void testSenateVote2012()
    {
        String[] senateAyeVotes2012 = { "Adams", "Addabbo", "Alesi", "Avella", "Ball", "Bonacic",
                "Breslin", "Carlucci", "DeFrancisco", "Diaz", "Dilan", "Duane", "Espaillat", "Farley", "Flanagan", "Fuschillo",
                "Gallivan", "Gianaris", "Golden", "Griffo", "Grisanti", "Hannon", "Hassell-Thomps", "Johnson", "Kennedy",
                "Klein", "Krueger", "Lanza", "Larkin", "LaValle", "Libous", "Little", "Marcellino", "Martins", "Maziarz", "McDonald",
                "Montgomery", "Nozzolio", "O'Mara", "Oppenheimer", "Parker", "Peralta", "Perkins", "Ranzenhofer", "Ritchie", "Rivera",
                "Robach", "Saland", "Sampson", "Savino", "Serrano", "Seward", "Skelos", "Smith", "Squadron", "Stavisky", "Stewart-Cousin",
                "Valesky", "Young", "Zeldin"};
        String[] senateExcusedVotes2012 = {"Huntley"};
        Vote expected = new Vote();
        expected.setAyes(Arrays.asList(senateAyeVotes2012));
        expected.setExcused(Arrays.asList(senateExcusedVotes2012));
        VoteTests.testSenateVotes(env, sobiDirectory, storage, billKey, senateVoteSobi2012, expected);
    }

    /*
     * ------- Committee Vote Tests -------
     */
    @Test
    public void testCommitteeVoteDate2011()
    {
        String committeeVoteDate2011 = "3/8/11";
        VoteTests.testVoteDate(env, sobiDirectory, storage, billKey, committeeVoteSobi2011, committeeVoteDate2011);
    }

    @Test
    public void testCommitteeVoteDate2012()
    {
        String committeeVoteDate2012 = "1/18/12";
        VoteTests.testVoteDate(env, sobiDirectory, storage, billKey, committeeVoteSobi2012, committeeVoteDate2012);
    }

    @Test
    public void testCommitteeVote2011()
    {
        String meetingKey2011 = "2011/meeting/meeting-Codes-7-2011-2011";
        String[] committeeAyeVotes2011 = {"Saland", "DeFrancisco", "Flanagan", "Fuschillo", "Gallivan", "Golden", "Lanza",
                "Nozzolio", "O'Mara", "Gianaris", "Duane", "Huntley", "Parker", "Perkins", "Squadron", "Espaillat"};
        Vote expected = new Vote();
        expected.setAyes(Arrays.asList(committeeAyeVotes2011));
        VoteTests.testCommitteeVotes(env, sobiDirectory, storage, meetingKey2011, billName, committeeVoteSobi2011, expected);
    }

    @Test
    public void testCommitteeVote2012()
    {
        String meetingKey2012 = "2012/meeting/meeting-Codes-2-2011-2012";
        String[] committeeAyeVotes2012 = {"Saland", "DeFrancisco", "Flanagan", "Fuschillo", "Gallivan", "Golden", "Lanza",
                "Nozzolio", "O'Mara", "Gianaris", "Huntley", "Parker", "Perkins", "Squadron", "Espaillat"};
        String[] committeeAyeWRVotes2012 = {"Duane"};
        Vote expected = new Vote();
        expected.setAyes(Arrays.asList(committeeAyeVotes2012));
        expected.setAyeswr(Arrays.asList(committeeAyeWRVotes2012));
        VoteTests.testCommitteeVotes(env, sobiDirectory, storage, meetingKey2012, billName, committeeVoteSobi2012, expected);
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
        String senateSponsor = "Sampson";
        BillTests.testPrimeSponsor(env, sobiDirectory, storage, billKey, initialSenateSobi, senateSponsor, false);
    }

    /*
     * Test the incorrectly formed bill status line:
     * "100000Pen L. sex. abuse 2nd degree     00000"
     * Where the data and title are positioned in the wrong character locations.
     * Should be like:
     * "1SAMPSON             00000Pen L. sex. abuse 2nd degree     000000"
     */
    @Test
    public void testIrregularBillStatus()
    {
        String correctBillStatusSobi = "SOBI.D110309.T151716.TXT";
        String incorrectBillStatusSobi = "SOBI.D110318.T090635.TXT";
        BillTests.testIrregularBillStatusLines(env, sobiDirectory, storage, billKey, incorrectBillStatusSobi, correctBillStatusSobi);
    }

    @Test
    public void testLawSection()
    {
        String lawSection = "Penal Law";
        BillTests.testLawSection(env, sobiDirectory, storage, billKey, initialSenateSobi, lawSection);
    }

    @Test
    public void testShortTitle()
    {
        String title = "Provides that sexual abuse in the 2nd degree is a class E felony";
        BillTests.testBillTitle(env, sobiDirectory, storage, billKey, initialSenateSobi, title);
    }

    @Test
    public void testBillStatusActions()
    {
        String billNumber = "S1618-2011";
        ArrayList<String[]> actionStrings = new ArrayList<String[]>();
        actionStrings.add(new String[]{"01/10/11", "REFERRED TO CODES"});
        actionStrings.add(new String[]{"03/08/11", "1ST REPORT CAL.174"});
        actionStrings.add(new String[]{"03/09/11", "2ND REPORT CAL."});
        actionStrings.add(new String[]{"03/10/11", "ADVANCED TO THIRD READING"});
        actionStrings.add(new String[]{"03/31/11", "PASSED SENATE"});
        actionStrings.add(new String[]{"03/31/11", "DELIVERED TO ASSEMBLY"});
        actionStrings.add(new String[]{"03/31/11", "referred to codes"});
        actionStrings.add(new String[]{"01/04/12", "died in assembly"});
        actionStrings.add(new String[]{"01/04/12", "returned to senate"});
        actionStrings.add(new String[]{"01/04/12", "REFERRED TO CODES"});
        actionStrings.add(new String[]{"01/18/12", "1ST REPORT CAL.59"});
        actionStrings.add(new String[]{"01/19/12", "2ND REPORT CAL."});
        actionStrings.add(new String[]{"01/23/12", "ADVANCED TO THIRD READING"});
        actionStrings.add(new String[]{"01/30/12", "PASSED SENATE"});
        actionStrings.add(new String[]{"01/30/12", "DELIVERED TO ASSEMBLY"});
        actionStrings.add(new String[]{"01/30/12", "referred to codes"});
        // TODO: Passed assembly??
        BillTests.testBillStatusActions(env, sobiDirectory, storage, billKey, initialSenateSobi, actionStrings, new Bill("S1618-2011", 2011));
    }

    @Test
    public void testSameAs()
    {
        // TODO: Processing this sobi takes a long time.
        String sameAsSobi = "SOBI.D120104.T223233.TXT";
        String sameAs = "No same as";
        BillTests.testSameAs(env, sobiDirectory, storage, billKey, sameAsSobi, sameAs);
    }

    @Test
    public void testCoSponsors()
    {
        String[] coSponsors = {"AVELLA", "SAVINO", "STAVISKY"};
        BillTests.testCoSponsors(env, sobiDirectory, storage, billKey, coSponsorsSobi, coSponsors);
    }

    @Test
    public void testMultiSponsors()
    {
        String[] multiSponsors = {};
        BillTests.testMultiSponsors(env, sobiDirectory, storage, billKey, coSponsorsSobi, multiSponsors);
    }

    @Test
    public void testActToClause()
    {
        String actToClauseSobi = "SOBI.D110110.T170157.TXT";
        String clause = "AN ACT to amend the penal law, in relation to changing sexual abuse in the second degree from a " +
                "class A misdemeanor to a class E felony";
        BillTests.testActToClause(env, sobiDirectory, storage, billKey, actToClauseSobi, clause);
    }

    @Test
    public void testBillLaw()
    {
        String billLaw = "Amd S130.60, Pen L";
        BillTests.testBillLaw(env, sobiDirectory, storage, billKey, billLawSobi, billLaw);
    }

    @Test
    public void testBillSummary()
    {
        String summary = "Raises the penalty for sexual abuse in the 2nd degree from a class A misdemeanor to a class E felony.";
        BillTests.testBillSummary(env, sobiDirectory, storage, billKey, billLawSobi, summary);
    }

    @Test
    public void testIfBillTextExists()
    {
        String billTextSobi = "SOBI.D110111.T153721.TXT";
        BillTests.doesBillTextExist(env, sobiDirectory, storage, billKey, billTextSobi);
    }

}