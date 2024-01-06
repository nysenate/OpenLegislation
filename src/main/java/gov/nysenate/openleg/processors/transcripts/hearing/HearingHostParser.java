package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.Pair;
import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType;

import java.util.HashSet;
import java.util.Set;

public final class HearingHostParser {
    private static final Set<HearingHost> BUDGET_COMMITTEES = Set.of(
            new HearingHost(Chamber.SENATE, HearingHostType.COMMITTEE, "FINANCE"),
            new HearingHost(Chamber.ASSEMBLY, HearingHostType.COMMITTEE, "WAYS AND MEANS"));
    private static final String JOINT = "JOINT LEGISLATURE", CHAMBERS = Chamber.SENATE + "|" + Chamber.ASSEMBLY +
            "|" + JOINT, BUDGET_COMMITTEE_PATTERN = "(?s).*(FORECASTING CONFERENCE.*|" +
            "FINANCE.*WAYS AND MEANS COMMITTEE(S?)$)";

    private HearingHostParser() {}

    /**
     * Extracts HearingHosts from a string containing the committee info.
     */
    public static Set<HearingHost> parse(String hostBlock) {
        if (hostBlock.matches(BUDGET_COMMITTEE_PATTERN))
            return BUDGET_COMMITTEES;
        hostBlock = HearingHostType.standardizeHostBlock(hostBlock)
                .replaceAll("(NYS|NEW YORK STATE) (" + CHAMBERS + ")", "$2");
        var ret = new HashSet<HearingHost>();
        // Default chamber.
        Chamber chamber = Chamber.SENATE;
        for (var chamberPair : RegexUtils.specialSplit(hostBlock, CHAMBERS)) {
            // Splits out the Chamber.
            boolean isJoint = chamberPair.v1().equals(JOINT);
            chamber = isJoint ? null : Chamber.getValue(chamberPair.v1());
            var typePairs = RegexUtils.specialSplit(chamberPair.v2(), HearingHostType.TYPE_LABELS);
            for (Pair<String> typePair : typePairs)
                ret.addAll(HearingHost.getHosts(typePair.v1(), typePair.v2(), chamber));
        }
        if (ret.isEmpty())
            ret.add(new HearingHost(chamber, HearingHostType.WHOLE_CHAMBER, ""));
        return ret;
    }
}
