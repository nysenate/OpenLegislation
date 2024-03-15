package gov.nysenate.openleg.spotchecks.scrape.bill;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.config.annotation.UnitTest;
import gov.nysenate.openleg.legislation.bill.BillVoteCode;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeReferenceHtmlParser;
import gov.nysenate.openleg.spotchecks.scraping.lrs.bill.BillScrapeVote;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import testing_utils.TestUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Category(UnitTest.class)
public class BillScrapeReferenceHtmlParserTest {

    private BillScrapeReferenceHtmlParser parser = new BillScrapeReferenceHtmlParser();

    @Test
    public void parsesNoVotes() {
        Document doc = loadDocument("billScrape/2015-S2422-20180510T155603.html");
        Set<BillScrapeVote> actualVotes = parser.parseVotes(doc);
        assertEquals(new HashSet(), actualVotes);
    }

    @Test
    public void parsesSingleVote() {
        Document doc = loadDocument("billScrape/2017-S4821-20180510T151502.html");
        Set<BillScrapeVote> expectedVotes = new HashSet<>();
        SortedSetMultimap<BillVoteCode, String> vote = TreeMultimap.create();
        vote.put(BillVoteCode.NAY, "Addabbo");
        vote.put(BillVoteCode.AYE, "Akshar");
        vote.put(BillVoteCode.NAY, "Alcantara");
        vote.put(BillVoteCode.AYE, "Amedore");
        vote.put(BillVoteCode.NAY, "Avella");
        vote.put(BillVoteCode.NAY, "Bailey");
        vote.put(BillVoteCode.NAY, "Benjamin");
        vote.put(BillVoteCode.AYE, "Bonacic");
        LocalDate expectedDate = LocalDate.of(2018, 5, 8);
        expectedVotes.add(new BillScrapeVote(expectedDate, vote));

        Set<BillScrapeVote> actualVotes = parser.parseVotes(doc);
        assertEquals(expectedVotes, actualVotes);
    }

    @Test
    public void parsesMultipleSenateVotes() {
        Document doc = loadDocument("billScrape/2015-S434-20180510T154604.html");
        Set<BillScrapeVote> expectedVotes = new HashSet<>();
        SortedSetMultimap<BillVoteCode, String> vote1 = TreeMultimap.create();
        vote1.put(BillVoteCode.AYE, "Addabbo");
        vote1.put(BillVoteCode.AYE, "Akshar");
        vote1.put(BillVoteCode.AYE, "Amedore");
        vote1.put(BillVoteCode.AYE, "Avella");
        vote1.put(BillVoteCode.AYE, "Bonacic");
        vote1.put(BillVoteCode.AYE, "Boyle");
        vote1.put(BillVoteCode.AYE, "Breslin");
        vote1.put(BillVoteCode.AYE, "Carlucci");
        LocalDate vote1Date = LocalDate.of(2016, 6, 14);
        expectedVotes.add(new BillScrapeVote(vote1Date, vote1));

        SortedSetMultimap<BillVoteCode, String> vote2 = TreeMultimap.create();
        vote2.put(BillVoteCode.AYE, "Addabbo");
        vote2.put(BillVoteCode.AYE, "Amedore");
        vote2.put(BillVoteCode.AYE, "Avella");
        vote2.put(BillVoteCode.AYE, "Bonacic");
        vote2.put(BillVoteCode.AYE, "Skelos");
        vote2.put(BillVoteCode.AYE, "Squadron");
        vote2.put(BillVoteCode.AYE, "Stavisky");
        vote2.put(BillVoteCode.AYE, "Stewart-Cousins");
        LocalDate vote2Date = LocalDate.of(2015, 6, 10);
        expectedVotes.add(new BillScrapeVote(vote2Date, vote2));

        Set<BillScrapeVote> actualVotes = parser.parseVotes(doc);

        assertEquals(expectedVotes, actualVotes);
    }

    @Test
    public void ignoresAssemblyVotes() {
        // This bill has an assembly and senate vote, should only return the senate vote.
        Document doc = loadDocument("billScrape/2017-A6361-20180510T153404.html");
        Set<BillScrapeVote> expectedVotes = new HashSet<>();
        SortedSetMultimap<BillVoteCode, String> vote = TreeMultimap.create();
        vote.put(BillVoteCode.AYE, "Addabbo");
        vote.put(BillVoteCode.AYE, "Akshar");
        vote.put(BillVoteCode.NAY, "Alcantara");
        vote.put(BillVoteCode.AYE, "Amedore");
        vote.put(BillVoteCode.NAY, "Avella");
        vote.put(BillVoteCode.NAY, "Bailey");
        vote.put(BillVoteCode.AYE, "Benjamin");
        vote.put(BillVoteCode.AYE, "Bonacic");
        LocalDate voteDate = LocalDate.of(2017, 6, 21);
        expectedVotes.add(new BillScrapeVote(voteDate, vote));

        Set<BillScrapeVote> actualVotes = parser.parseVotes(doc);

        assertEquals(expectedVotes, actualVotes);

        // This bill only has a single assembly vote
        doc = loadDocument("billScrape/2017-A1646-20180518T231109.html");
        expectedVotes = new HashSet<>();
        actualVotes = parser.parseVotes(doc);
        assertEquals(expectedVotes, actualVotes);
    }

    /**
     * Our vote parser was failing due to a new Videoconferencing icon.
     * This test verifies that our parser is able to parse the vote as expected while ignoring that icon.
     * The icon is represented as "&#8225;" in the test html file and is associated with Cooney's vote.
     */
    @Test
    public void ignoresVideoconferenceIndicator() {
        Document doc = loadDocument("billScrape/2023-S6169-20240109T175502.html");

        Set<BillScrapeVote> expectedVotes = new HashSet<>();
        SortedSetMultimap<BillVoteCode, String> vote = TreeMultimap.create();
        vote.put(BillVoteCode.ABS, "Addabbo");
        vote.put(BillVoteCode.EXC, "Ashby");
        vote.put(BillVoteCode.ABD, "Bailey");
        vote.put(BillVoteCode.NAY, "Borrello");
        vote.put(BillVoteCode.AYE, "Chu");
        vote.put(BillVoteCode.AYE, "Cleare");
        vote.put(BillVoteCode.AYE, "Comrie");
        vote.put(BillVoteCode.NAY, "Cooney");
        LocalDate voteDate = LocalDate.of(2023, 6, 9);
        expectedVotes.add(new BillScrapeVote(voteDate, vote));

        Set<BillScrapeVote> actualVotes = parser.parseVotes(doc);

        assertEquals(expectedVotes, actualVotes);
    }

    private Document loadDocument(String file) {
        Document doc = null;
        try {
            File html = TestUtils.openTestResource(file);
            doc = Jsoup.parse(html, "UTF-8");
        } catch (URISyntaxException|IOException e) {
            e.printStackTrace();
            fail("Failed to load test resources");
        }
        return doc;
    }
}
