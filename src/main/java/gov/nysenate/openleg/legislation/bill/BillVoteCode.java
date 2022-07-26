package gov.nysenate.openleg.legislation.bill;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents the possible voting code prefixes.
 */
public enum BillVoteCode {
    AYE("YES"), NAY("NO"), EXC("EXCUSED"), ABS("ABSENT"),
    ABD("ABSTAINED"), AYEWR("AYE W/R"); // 'Aye, with reservations'

    private final Set<String> acceptableStrings;

    // Lookup string names quickly
    private static final Map<String, BillVoteCode> nameLookupMap = new HashMap<>();
    static {
        for (BillVoteCode voteCode : values()) {
            for (String s : voteCode.acceptableStrings) {
                nameLookupMap.put(s, voteCode);
            }
        }
    }

    BillVoteCode(String alternateString) {
        this.acceptableStrings = Set.of(name(), alternateString);
    }

    /**
     * Returns a BillVoteCode reference where the given 'code' matches one of the
     * acceptable strings for the vote code.
     */
    public static BillVoteCode getValue(String code) {
        if (code != null) {
            code = code.trim().toUpperCase();
            if (nameLookupMap.containsKey(code)) {
                return nameLookupMap.get(code);
            }
        }
        throw new IllegalArgumentException("Failed to map " + code + " to a BillVoteCode value.");
    }
}
