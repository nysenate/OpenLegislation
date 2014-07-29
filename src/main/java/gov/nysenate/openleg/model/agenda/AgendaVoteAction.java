package gov.nysenate.openleg.model.agenda;

public enum AgendaVoteAction
{
    FIRST_READING("F"),
    THIRD_READING("3"),
    REFERRED_TO_COMMITTEE("RC"),
    DEFEATED("D"),
    RESTORED_TO_THIRD("R3"),
    SPECIAL("S");

    private String code;

    AgendaVoteAction(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
