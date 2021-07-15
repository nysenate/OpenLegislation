package gov.nysenate.openleg.processors.transcripts.hearing;

import gov.nysenate.openleg.common.util.RegexUtils;
import gov.nysenate.openleg.legislation.committee.Chamber;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHost;
import gov.nysenate.openleg.legislation.transcripts.hearing.HearingHostType;
import org.elasticsearch.common.collect.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class HearingHostParser {
    private static final String CHAMBERS = Chamber.SENATE + "|" + Chamber.ASSEMBLY,
            BUDGET_COMMITTEE_PATTERN = "(?s).*(FORECASTING CONFERENCE.*|FINANCE.*WAYS AND MEANS COMMITTEE(S?)$)";
    private static final List<HearingHost> BUDGET_COMMITTEES = List.of(
            new HearingHost(Chamber.SENATE, HearingHostType.COMMITTEE, "Finance"),
            new HearingHost(Chamber.ASSEMBLY, HearingHostType.COMMITTEE, "WAYS AND MEANS"));

    // TODO: remove this.
    public static final TreeSet<HearingHost> hosts = new TreeSet<>((o1, o2) -> {
        int chamberCompare = o1.getChamber().compareTo(o2.getChamber());
        if (chamberCompare != 0)
            return chamberCompare;
        int typeCompare = o1.getType().compareTo(o2.getType());
        if (typeCompare != 0)
            return typeCompare;
        return String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName());
    });

    private HearingHostParser() {}

    /**
     * Extracts HearingHost's from a string containing the committee info.
     */
    public static List<HearingHost> parse(String hostBlock) {
        if (hostBlock.matches(BUDGET_COMMITTEE_PATTERN))
            return BUDGET_COMMITTEES;
        hostBlock = HearingHostType.standardizeHostBlock(hostBlock);
        var ret = new ArrayList<HearingHost>();
        // Default chamber.
        Chamber chamber = Chamber.SENATE;

        for (var chamberTuple : RegexUtils.specialSplit(hostBlock, CHAMBERS)) {
            chamber = Chamber.getValue(chamberTuple.v1());
            var typeTuples = RegexUtils.specialSplit(chamberTuple.v2(), HearingHostType.TYPE_LABELS);
            for (Tuple<String, String> typeTuple : typeTuples) {
                var type = HearingHostType.toType(typeTuple.v1());
                var host = new HearingHost(chamber, type, typeTuple.v2());
                ret.add(host);
            }
        }
        if (ret.isEmpty())
            ret.add(new HearingHost(chamber, HearingHostType.WHOLE_CHAMBER, ""));
        return ret;
    }
}
