package gov.nysenate.openleg.service.spotcheck.scrape;

import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatch;
import gov.nysenate.openleg.model.spotcheck.SpotCheckMismatchType;
import gov.nysenate.openleg.model.spotcheck.billscrape.BillScrapeVote;
import org.springframework.stereotype.Component;

import java.text.Collator;
import java.util.*;

@Component
public class BillScrapeVoteMismatchService {

    private static Comparator<BillScrapeVote> scrapeVoteDateComparator = Comparator.comparing(BillScrapeVote::getVoteDate);

    /**
     * Compares two sets of BillScrapeVotes for any significant differences between them.
     * @param observed BillScrapeVotes representing current openleg data.
     * @param reference BillScrapeVotes generated from reference data.
     * @return An Optional containing a SpotCheckMismatch detailing the differences if differences exist, empty otherwise.
     */
    public Optional<SpotCheckMismatch> compareVotes(Set<BillScrapeVote> observed, Set<BillScrapeVote> reference) {
        TreeSet<BillScrapeVote> observedOrdered = new TreeSet<>(scrapeVoteDateComparator);
        observedOrdered.addAll(observed);
        TreeSet<BillScrapeVote> referenceOrdered = new TreeSet<>(scrapeVoteDateComparator);
        referenceOrdered.addAll(reference);

        String openlegVotesString = observedOrdered.toString();
        String referenceVotesString = referenceOrdered.toString();

        // Only consider primary differences significant when comparing.
        // This will consider ú, u and U equal.
        Collator usCollator = Collator.getInstance(Locale.US);
        usCollator.setStrength(Collator.PRIMARY);

        if (usCollator.compare(referenceVotesString, openlegVotesString) != 0) {
            return Optional.of(new SpotCheckMismatch(SpotCheckMismatchType.BILL_SCRAPE_VOTE,
                    observed.toString(), reference.toString()));
        }

        return Optional.empty();
    }

}
