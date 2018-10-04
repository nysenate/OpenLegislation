package gov.nysenate.openleg.service.spotcheck.scrape;

import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import gov.nysenate.openleg.annotation.UnitTest;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeVote;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class BillScrapeVoteMismatchServiceTest {

    private static BillScrapeVoteMismatchService mmService;
    private static LocalDate DATE_ONE = LocalDate.of(2018, 1, 1);
    private static LocalDate DATE_TWO = LocalDate.of(2018, 1, 2);
    private static SortedSetMultimap<BillVoteCode, String> EMPTY_VOTE;
    private static SortedSetMultimap<BillVoteCode, String> VOTE_A;
    private static SortedSetMultimap<BillVoteCode, String> VOTE_B;
    private static SortedSetMultimap<BillVoteCode, String> VOTE_C;

    @BeforeClass
    public static void beforeClass() {
        mmService = new BillScrapeVoteMismatchService();
        EMPTY_VOTE = TreeMultimap.create();
        VOTE_A = TreeMultimap.create();
        VOTE_A.put(BillVoteCode.AYE, "SenatorA");
        VOTE_A.put(BillVoteCode.AYE, "SenatorB");
        VOTE_B = TreeMultimap.create();
        VOTE_B.put(BillVoteCode.NAY, "Senator-ú");
        VOTE_C = TreeMultimap.create();
        VOTE_C.put(BillVoteCode.NAY, "Senator-u");
    }

    @Test
    public void sameVotesAreEqual() {
        BillScrapeVote olVotes = new BillScrapeVote(DATE_ONE, VOTE_A);
        BillScrapeVote refVotes = new BillScrapeVote(DATE_ONE, VOTE_A);
        assertNoMismatch(Sets.newHashSet(olVotes), Sets.newHashSet(refVotes));
    }

    @Test
    public void differentDatesCreatesMismatch() {
        BillScrapeVote olVotes = new BillScrapeVote(DATE_ONE, EMPTY_VOTE);
        BillScrapeVote refVotes = new BillScrapeVote(DATE_TWO, EMPTY_VOTE);
        assertMismatch(Sets.newHashSet(olVotes), Sets.newHashSet(refVotes));
    }

    @Test
    public void diffVotesCreatesMismatch() {
        BillScrapeVote olVotes = new BillScrapeVote(DATE_ONE, VOTE_A);
        BillScrapeVote refVotes = new BillScrapeVote(DATE_ONE, VOTE_B);
        assertMismatch(Sets.newHashSet(olVotes), Sets.newHashSet(refVotes));
    }

    @Test
    public void similarCharactersNotAMismatch() {
        BillScrapeVote olVotes = new BillScrapeVote(DATE_ONE, VOTE_B);
        BillScrapeVote refVotes = new BillScrapeVote(DATE_ONE, VOTE_C);
        assertNoMismatch(Sets.newHashSet(olVotes), Sets.newHashSet(refVotes));
    }

    @Test
    public void senatorVoteOrderDoesNotMatter() {
        BillScrapeVote  olVotes = new BillScrapeVote(DATE_ONE, VOTE_A);
        SortedSetMultimap<BillVoteCode, String> reversedVote = TreeMultimap.create();
        reversedVote.put(BillVoteCode.AYE, "SenatorB");
        reversedVote.put(BillVoteCode.AYE, "SenatorA");
        BillScrapeVote refVotes = new BillScrapeVote(DATE_ONE, reversedVote);

        assertNoMismatch(Sets.newHashSet(olVotes), Sets.newHashSet(refVotes));
    }

    @Test
    public void voteDateOrderDoesNotMatter() {
        Set<BillScrapeVote> olVotes = new LinkedHashSet<>();
        Set<BillScrapeVote> refVotes = new LinkedHashSet<>();

        olVotes.add(new BillScrapeVote(DATE_ONE, VOTE_A));
        olVotes.add(new BillScrapeVote(DATE_TWO, VOTE_B));

        refVotes.add(new BillScrapeVote(DATE_TWO, VOTE_B));
        refVotes.add(new BillScrapeVote(DATE_ONE, VOTE_A));

        assertNoMismatch(olVotes, refVotes);
    }

    private void assertNoMismatch(Set<BillScrapeVote> olVotes, Set<BillScrapeVote> refVotes) {
        assertFalse("Unexpectedly found a mismatch", mmService.compareVotes(olVotes, refVotes).isPresent());
    }

    private void assertMismatch(Set<BillScrapeVote> olVotes, Set<BillScrapeVote> refVotes) {
        assertTrue("Expected mismatch but none found.", mmService.compareVotes(olVotes, refVotes).isPresent());
    }
}
