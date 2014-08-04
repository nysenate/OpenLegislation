package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ImmutableSet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the possible voting code prefixes.
 */
public enum BillVoteCode
{
    AYE   (ImmutableSet.of("AYE", "YES")),
    NAY   (ImmutableSet.of("NAY", "NO")),
    EXC   (ImmutableSet.of("EXC", "EXCUSED")),
    ABS   (ImmutableSet.of("ABS", "ABSENT")),
    ABD   (ImmutableSet.of("ABD", "ABSTAINED")),
    AYEWR (ImmutableSet.of("AYEWR", "AYE W/R")); // 'Aye, with reservations'

    private Set<String> acceptableStrings = new HashSet<>();

    // Lookup string names quickly
    private static Map<String, BillVoteCode> nameLookupMap = new HashMap<>();
    static {
        for (BillVoteCode voteCode : values()) {
            for (String s : voteCode.acceptableStrings) {
                nameLookupMap.put(s, voteCode);
            }
        }
    }

    BillVoteCode(Set<String> acceptableStrings) {
        this.acceptableStrings = acceptableStrings;
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
