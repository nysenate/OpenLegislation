package gov.nysenate.openleg.model.spotcheck.billscrape;

import com.google.common.collect.SortedSetMultimap;
import gov.nysenate.openleg.model.bill.BillVoteCode;
import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.util.Objects;

public class BillScrapeVote {

    private LocalDate voteDate;
    // Maps BillVoteCode to senator's last name.
    private SortedSetMultimap<BillVoteCode, String> votes;

    public BillScrapeVote(LocalDate voteDate, SortedSetMultimap<BillVoteCode, String> votes) {
        this.voteDate = voteDate;
        this.votes = votes;
    }

    public LocalDate getVoteDate() {
        return voteDate;
    }

    public SortedSetMultimap<BillVoteCode, String> getVotes() {
        return votes;
    }

    @Override
    public String toString() {
        String s = "BillScrapeVote{ \n" +
                "    voteDate=" + voteDate + ",\n" +
                "        votes=" + votes + "\n" +
                '}';
        return WordUtils.wrap(s, 70, "\n            ", false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillScrapeVote that = (BillScrapeVote) o;
        return Objects.equals(voteDate, that.voteDate) &&
                Objects.equals(votes, that.votes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(voteDate, votes);
    }
}
