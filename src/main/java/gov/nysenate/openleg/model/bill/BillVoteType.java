package gov.nysenate.openleg.model.bill;

/**
 * Represents the possible types of votes that can take place.
 */
public enum BillVoteType
{
    COMMITTEE(1),
    FLOOR(2);

    int code;

    BillVoteType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * Delegate to valueOf but performs some string normalization to prevent errors.
     */
    public static BillVoteType getValue(String type) {
        if (type != null) {
            type = type.trim().toUpperCase();
            return valueOf(type);
        }
        throw new IllegalArgumentException("Supplied null 'type' when mapping BillVoteType.");
    }

    public static BillVoteType getValueFromCode(int code) {
        for (BillVoteType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No BillVoteType mapping with code: " + code);
    }
}
