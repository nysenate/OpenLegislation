package gov.nysenate.openleg.model.bill;

import com.google.common.collect.Maps;
import gov.nysenate.openleg.model.sobi.SobiLineType;

import java.util.Arrays;
import java.util.Map;

public enum BillTextType
{
    RESOLUTION    (SobiLineType.RESOLUTION_TEXT,    "RESO TEXT"),
    BILL          (SobiLineType.TEXT,               "BTXT"),
    SPONSOR_MEMO  (SobiLineType.SPONSOR_MEMO,       "MTXT"),
    VETO_APPROVAL (SobiLineType.VETO_APPROVE_MEMO,  "VETO|APPROVAL")
    ;

    private SobiLineType sobiLineType;
    private String typeString;

    private static Map<SobiLineType, BillTextType> lineTypeMap =
        Maps.uniqueIndex(Arrays.asList(values()), BillTextType::getSobiLineType);

    BillTextType (SobiLineType sobiLineType, String typeString) {
        this.sobiLineType = sobiLineType;
        this.typeString = typeString;
    }

    public SobiLineType getSobiLineType() {
        return sobiLineType;
    }

    public static BillTextType getTypeString(SobiLineType sobiLineType) {
        return lineTypeMap.get(sobiLineType);
    }

    public String getTypeString() {
        return typeString;
    }
}
