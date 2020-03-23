package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.TextDiff;
import gov.nysenate.openleg.model.bill.TextDiffType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextDiffSearch {

    private final Pattern pattern;
    private final String billText;
    private final TextDiffType diffType;
    private Matcher matcher;
    /**
     * The starting index of the matcher's current match. -1 if no match was found.
     *
     * When using multiple TextDiffSearch's you can determine the order of the matches
     * by comparing the startingIndex.
     */
    private int startingIndex;

    /**
     * The ending index of the matcher's current result. -1 if no match was found.
     */
    private int endingIndex;
    private String matchingText;

    public TextDiffSearch(Pattern pattern, TextDiffType diffType, String billText) {
        this.pattern = pattern;
        this.diffType = diffType;
        this.billText = billText;
        this.startingIndex = -1;
        this.endingIndex = -1;
    }

    /**
     * Searches for the next instance of pattern in billText
     * and sets startingIndex, endingIndex, and matchingText.
     *
     * This can then be used to get a TextDiff representing this text with the createDiff method.
     *
     * Once the diff for a match is used, call this method again to find the next match.
     */
    public void findNext() {
        if (matcher == null) {
            matcher = pattern.matcher(billText);
        }

        if (matcher.find()) {
            this.startingIndex = matcher.start();
            this.endingIndex = matcher.end();
            if (matcher.groupCount() > 0) {
                this.matchingText = matcher.group(1);
            }
            else {
                // If there is no group to match, its always an empty string.
                this.matchingText = "";
            }
        }
        else {
            this.startingIndex = -1;
            this.endingIndex = -1;
            this.matchingText = "";
        }
    }

    /**
     * Creates a TextDiff from the diffType and matchingText.
     * @return
     */
    public TextDiff createDiff() {
        return new TextDiff(this.getDiffType(), this.getMatchingText());
    }

    public TextDiffType getDiffType() {
        return diffType;
    }

    public int getStartingIndex() {
        return startingIndex;
    }

    public int getEndingIndex() {
        return endingIndex;
    }

    public String getMatchingText() {
        return matchingText;
    }
}
