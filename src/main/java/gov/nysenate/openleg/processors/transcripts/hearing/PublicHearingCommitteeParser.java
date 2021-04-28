package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.PublicHearingTextUtils;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingCommittee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PublicHearingCommitteeParser {
    private static final String CHAMBER = "(?<chamber>" + Chamber.SENATE.name() + "|" + Chamber.ASSEMBLY.name() + ")",
            COMMITTEE_CHECK = "BEFORE THE NEW YORK STATE " + CHAMBER,
            COMMITTEE_OR_TASK_FORCE = "(MAJORITY COALITION JOINT|STANDING COMMITTEE ON|COMMITTEES?|TASK FORCE ON) ?";
    // Matches lines only containing "-" characters which divide up content.
    private static final Pattern SEPARATOR = Pattern.compile("^\\s*(\\d+)?\\s*-+$"),
            COMMITTEE_SPLIT = Pattern.compile("([,;] )?(?=AND( THE)? " + CHAMBER + ")"),
            COMMITTEE_PATTERN = Pattern.compile(CHAMBER + "(?<name>.+)");

    /**
     * Extracts PublicHearingCommittee's from a string containing the committee info.
     */
    public static List<PublicHearingCommittee> parse(List<String> firstPage) {
        String committeeBlock = parseCommitteeBlock(firstPage);
        List<PublicHearingCommittee> committees = new ArrayList<>();
        if (!Pattern.compile(COMMITTEE_CHECK).matcher(committeeBlock).find())
            return committees;
        String[] committeeStrings = committeeBlock.split(COMMITTEE_SPLIT.toString());
        // Parse committees.
        for (String committeeString : committeeStrings) {
            String comStr = committeeString.replaceAll(COMMITTEE_OR_TASK_FORCE, "");
            Matcher matcher = COMMITTEE_PATTERN.matcher(comStr);
            if (!matcher.find())
                continue;
            var committee = new PublicHearingCommittee(matcher.group("name"), matcher.group("chamber"));
            committees.add(committee);
        }
        return committees;
    }

    /**
     * Parses out the block of text containing committee info from the first page of the PublicHearing.
     */
    private static String parseCommitteeBlock(List<String> firstPage) {
        StringBuilder committeeBlock = new StringBuilder();
        for (String line : firstPage) {
            // Committee is the first piece of information on page.
            Matcher endOfCommittee = SEPARATOR.matcher(line);
            if (endOfCommittee.matches())
                break;
            if (PublicHearingTextUtils.hasContent(line))
                committeeBlock.append(" ").append(PublicHearingTextUtils.stripLineNumber(line));
        }
        return committeeBlock.toString();
    }
}
