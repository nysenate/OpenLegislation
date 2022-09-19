package gov.nysenate.openleg.legislation.agenda;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.Map;

public enum AgendaVoteAction
{
    REPORTED("R"),
    FIRST_READING("F"),
    THIRD_READING("3"),
    REFERRED_TO_COMMITTEE("RC"),
    DEFEATED("D"),
    RESTORED_TO_THIRD("R3"),
    SPECIAL("S");

    private static final Map<String, AgendaVoteAction> codeMap =
        Maps.uniqueIndex(Arrays.asList(values()), AgendaVoteAction::getCode);

    /** Code referenced by the sobi files. */
    private final String code;

    AgendaVoteAction(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AgendaVoteAction valueOfCode(String code) {
        if (code != null) {
            return codeMap.get(code.trim().toUpperCase());
        }
        throw new IllegalArgumentException("Supplied code cannot be null when mapping to AgendaVoteAction.");
    }
}
