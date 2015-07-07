package gov.nysenate.openleg.processor.hearing;

import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.model.hearing.PublicHearingCommittee;
import gov.nysenate.openleg.util.PublicHearingTextUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingCommitteeParser
{
    /* matches lines only containing "-" characters which divide up content. */
    private static final Pattern SEPARATOR = Pattern.compile("^\\s*(\\d+)?\\s*-+$");

    private static final Pattern CHECK_FOR_COMMITTEES = Pattern.compile("BEFORE THE NEW YORK STATE (SENATE|ASSEMBLY)");

    private static final Pattern COMMITTEE_SPLIT = Pattern.compile("(, )?(?=AND(( THE)? SENATE|( THE)? ASSEMBLY))");

    private static final Pattern FIRST_COMMITTEE = Pattern.compile(
            "BEFORE THE NEW YORK STATE (?<chamber>SENATE|ASSEMBLY) ?(MAJORITY COALITION JOINT TASK FORCE ON |STANDING COMMITTEE ON )?(?<name>.*)");

    private static final Pattern ADDITIONAL_COMMITTEE = Pattern.compile(
            "AND (THE )?(SENATE|ASSEMBLY) (STANDING COMMITTEE|TASK FORCE) ON (.+)");

    /**
     * Extracts PublicHearingCommittee's from the first page of a PublicHearingFile.
     * @param firstPage
     * @return
     */
    public List<PublicHearingCommittee> parse(List<String> firstPage) {
        String committeeBlock = parseCommitteeBlock(firstPage);
        return parse(committeeBlock);
    }

    /**
     * Extracts PublicHearingCommittee's from a string containing the committee info.
     * @param committeeBlock
     * @return
     */
    public List<PublicHearingCommittee> parse(String committeeBlock) {
        if (!committeeExists(committeeBlock)) {
            return null;
        }
        if (multipleCommittees(committeeBlock)) {
            return parseMultipleCommittees(committeeBlock);
        } else {
            return Arrays.asList(parseSingleCommittee(committeeBlock));
        }
    }

    /**
     * Checks if any PublicHearingCommittee information is present in this committeeBlock.
     * @param committeeBlock text to search for PublicHearingCommittee info.
     * @return
     */
    private boolean committeeExists(String committeeBlock) {
        Matcher committeeMatcher = CHECK_FOR_COMMITTEES.matcher(committeeBlock);
        return committeeMatcher.find();
    }

    /** Determines if a String contains info for multiple committees. */
    private boolean multipleCommittees(String committeeBlock) {
        Matcher committeeSplitMatcher = COMMITTEE_SPLIT.matcher(committeeBlock);
        return committeeSplitMatcher.find();
    }

    /**
     * Extract the PublicHearingCommittee's from a String when the String contains
     * multiple PublicHearingCommittee's
     * @param committeeBlock
     * @return
     */
    private List<PublicHearingCommittee> parseMultipleCommittees(String committeeBlock) {
        List<PublicHearingCommittee> committees = new ArrayList<>();

        // Parse the first committee.
        String[] committeeStrings = committeeBlock.split(COMMITTEE_SPLIT.toString());
        committees.add(parseSingleCommittee(committeeStrings[0]));

        // Parse the additional committees.
        for (int i = 1; i < committeeStrings.length; i++) {
            if (!committeeStrings[i].isEmpty()) {
                committees.add(parseAdditionalCommittee(committeeStrings[i]));
            }
        }

        return committees;
    }

    /**
     * Extracts the nth PublicHearingCommittee from a String where n > 1.
     * @param committeeString The String containing PublicHearingCommittee information.
     * @return
     */
    private PublicHearingCommittee parseAdditionalCommittee(String committeeString) {
        PublicHearingCommittee committee = new PublicHearingCommittee();
        Matcher additionalCommitteeMatcher = ADDITIONAL_COMMITTEE.matcher(committeeString);
        additionalCommitteeMatcher.find();

        committee.setName(additionalCommitteeMatcher.group(4));
        committee.setChamber(Chamber.valueOf(additionalCommitteeMatcher.group(2).toUpperCase()));
        return committee;
    }

    /**
     * Extracts the first PublicHearingCommittee from a String.
     * @param committeeBlock
     * @return
     */
    private PublicHearingCommittee parseSingleCommittee(String committeeBlock) {
        PublicHearingCommittee committee = new PublicHearingCommittee();
        Matcher matchFirstCommittee = FIRST_COMMITTEE.matcher(committeeBlock);
        matchFirstCommittee.find();

        committee.setName(matchFirstCommittee.group("name").trim());
        committee.setChamber(Chamber.valueOf(matchFirstCommittee.group("chamber").toUpperCase()));
        return committee;
    }

    /** Parses out the block of text containing committee info from the first page of the PublicHearing. */
    private String parseCommitteeBlock(List<String> firstPage) {
        String committeeBlock = "";
        for (String line : firstPage) {
            // Committee is the first piece of information on page.
            Matcher endOfCommittee = SEPARATOR.matcher(line);
            if (endOfCommittee.matches()) {
                break;
            }
            if (PublicHearingTextUtils.hasContent(line)) {
                committeeBlock += " " + PublicHearingTextUtils.stripLineNumber(line);
            }
        }
        return committeeBlock;
    }
}
