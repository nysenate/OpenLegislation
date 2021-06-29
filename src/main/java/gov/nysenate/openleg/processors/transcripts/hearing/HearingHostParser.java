package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HearingHostParser {
    private static final String CHAMBERS = Chamber.SENATE + "|" + Chamber.ASSEMBLY;
    private static final Pattern hostPattern = Pattern.compile("(?<chamber>" + CHAMBERS + ")(?!.*(" + CHAMBERS + "))" +
            "(?<hostTitles>.*)", Pattern.DOTALL);
    public static final TreeSet<HearingHost> hosts = new TreeSet<>((o1, o2) -> {
        int chamberCompare = o1.getChamber().compareTo(o2.getChamber());
        if (chamberCompare == 0)
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
        return chamberCompare;
    });

    private HearingHostParser() {}

    /**
     * Extracts HearingHost's from a string containing the committee info.
     */
    public static List<HearingHost> parse(String committeeBlock) {
        // Strips the line number and extra spaces
        committeeBlock = committeeBlock.replaceAll(" +\\d? +", "")
                // Removes text before the chamber is identified.
                .replaceFirst("^.*?(" + CHAMBERS + ")\\s+", "$1 ");
        var ret = new ArrayList<HearingHost>();

        // TODO: see other hearing hosts. May have multiple types.
        if (!committeeBlock.contains("COMMITTEE"))
            return ret;

        Matcher m = hostPattern.matcher(committeeBlock);
        while (m.find()) {
            String currChamber = m.group("chamber");
            String hostTitles = m.group("hostTitles");
            String[] hostSplit = m.group("hostTitles").trim().split(";|\nAND\n|(STANDING )?(SUB)?COMMITTEE(S)?\\s+ON ");
            for (var s : hostSplit) {
                var currHost = new HearingHost(currChamber, HearingHostType.COMMITTEE, s);
                if (currHost.getName().isEmpty())
                    continue;
                hosts.add(currHost);
                ret.add(currHost);
            }
        }
        return ret;
    }
}
