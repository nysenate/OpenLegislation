package gov.nysenate.openleg.model.agenda;

import java.util.HashMap;
import java.util.Map;

public enum AgendaVoteAction
{
    FIRST_READING("F"),
    THIRD_READING("3"),
    REFERRED_TO_COMMITTEE("RC"),
    DEFEATED("D"),
    RESTORED_TO_THIRD("R3"),
    SPECIAL("S");

    private static Map<String, AgendaVoteAction> codeMap = new HashMap<>();
    static {
        for (AgendaVoteAction ava : AgendaVoteAction.values()) {
            codeMap.put(ava.code, ava);
        }
    }

    private String code;

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
        return null;
    }
}
