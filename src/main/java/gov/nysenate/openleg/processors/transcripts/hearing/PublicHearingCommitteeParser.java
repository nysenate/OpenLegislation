package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.PublicHearingCommittee;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PublicHearingCommitteeParser {
    private static final String CHAMBER = "(?<chamber>" + Chamber.SENATE.name() + "|" + Chamber.ASSEMBLY.name() + ")",
            COMMITTEE_OR_TASK_FORCE = "(MAJORITY COALITION JOINT|STANDING COMMITTEE ON|COMMITTEES?|TASK FORCE ON) ?",
            COMMITTEE_SPLIT = "([,;] )?(?=AND( THE)? " + CHAMBER + ")";
    // Matches lines only containing "-" characters which divide up content.
    private static final Pattern COMMITTEE_PATTERN = Pattern.compile(CHAMBER + "(?<name>.+)");

    private PublicHearingCommitteeParser() {}

    /**
     * Extracts PublicHearingCommittee's from a string containing the committee info.
     */
    public static List<PublicHearingCommittee> parse(String committeeBlock) {
        List<PublicHearingCommittee> committees = new ArrayList<>();
        String[] committeeStrings = committeeBlock.split(COMMITTEE_SPLIT);
        // Parse committees.
        for (String committeeString : committeeStrings) {
            String comStr = committeeString.replaceAll(COMMITTEE_OR_TASK_FORCE, "");
            Matcher matcher = COMMITTEE_PATTERN.matcher(comStr.trim());
            if (!matcher.find())
                continue;
            var committee = new PublicHearingCommittee(matcher.group("name"), matcher.group("chamber"));
            committees.add(committee);
        }
        return committees;
    }
}
