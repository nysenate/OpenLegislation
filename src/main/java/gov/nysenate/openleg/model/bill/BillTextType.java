package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.sobi.SobiLineType;

import java.util.HashMap;
import java.util.Map;

public enum BillTextType
{
    RESOLUTION    (SobiLineType.RESOLUTION_TEXT, "RESO TEXT"),
    BILL          (SobiLineType.TEXT, "BTXT"),
    SPONSOR_MEMO  (SobiLineType.SPONSOR_MEMO, "MTXT"),
    VETO          (SobiLineType.VETO_APPROVE_MEMO, "VETO");

    private SobiLineType sobiLineType;
    private String typeString;

    private static Map<SobiLineType, BillTextType> lineTypeMap = new HashMap<>();
    static {
        for (BillTextType fullTextType : BillTextType.values()) {
            lineTypeMap.put(fullTextType.sobiLineType, fullTextType);
        }
    }

    BillTextType (SobiLineType sobiLineType, String typeString) {
        this.sobiLineType = sobiLineType;
        this.typeString = typeString;
    }

    public static BillTextType getTypeString(SobiLineType sobiLineType) {
        return lineTypeMap.get(sobiLineType);
    }

    public String getTypeString() {
        return typeString;
    }
}
