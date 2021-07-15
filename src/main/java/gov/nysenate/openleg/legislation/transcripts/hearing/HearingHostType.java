package gov.nysenate.openleg.legislation.transcripts.hearing;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The type of group hosting a PublicHearing.
 */
public enum HearingHostType {
    COMMITTEE, LEGISLATIVE_COMMISSION, TASK_FORCE, MAJORITY_COALITION, WHOLE_CHAMBER, UNKNOWN;

    public static final String TYPE_LABELS = Arrays.stream(values())
            .map(type -> type.match).collect(Collectors.joining("|"));
    private static final String COMMITTEE_LABEL = "(STANDING\\s*)?(SUB)?" + COMMITTEE.name() + "(S)?",
            COALITION_AND_TASK_FORCE = "MAJORITY COALITION\\s+JOINT TASK FORCE";

    private final String match;

    HearingHostType() {
        this.match = name().replaceAll("_", " ");
    }

    public static HearingHostType toType(String typeStr) {
        try {
            return valueOf(typeStr.trim().replaceAll(" ", "_").toUpperCase());
        }
        catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    public static String standardizeHostBlock(String block) {
        return block.replaceAll(COMMITTEE_LABEL, COMMITTEE.match)
                // The parser can't handle the multiple types that some hearings have.
                .replaceAll(COALITION_AND_TASK_FORCE, TASK_FORCE.match);
    }
}
