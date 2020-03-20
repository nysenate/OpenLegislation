package gov.nysenate.openleg.processor.bill;

import gov.nysenate.openleg.model.bill.TextDiff;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextDiffSearch {

    private final Pattern pattern;
    private final String billText;
    private final int diffType;
    private final List<String> cssClasses;
    private Matcher matcher;
    private int startingIndex;
    private int endingIndex;
    private String matchingText;

    public TextDiffSearch(Pattern pattern, int diffType, List<String> cssClasses, String billText) {
        this.pattern = pattern;
        this.diffType = diffType;
        this.cssClasses = cssClasses;
        this.billText = billText;
    }

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

    public TextDiff createDiff() {
        return new TextDiff(this.getDiffType(), this.getMatchingText(), this.getCssClasses());
    }

    public int getDiffType() {
        return diffType;
    }

    public List<String> getCssClasses() {
        return cssClasses;
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
