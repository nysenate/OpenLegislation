package gov.nysenate.openleg.model.bill;

public enum BillVoteType
{
    FLOOR(1),
    COMMITTEE(2);

    int code;

    BillVoteType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static BillVoteType valueOfCode(int code) {
        for (BillVoteType type : BillVoteType.values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No BillVoteType mapping with code: " + code);
    }
}
