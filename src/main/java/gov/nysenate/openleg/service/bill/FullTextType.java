package gov.nysenate.openleg.service.bill;

import gov.nysenate.openleg.model.sobi.SobiLineType;

import java.util.HashMap;
import java.util.Map;

public enum FullTextType {
    RESOLUTION(SobiLineType.RESOLUTION_TEXT, "RESO TEXT"),
    BILL(SobiLineType.TEXT, "BTXT"),
    SPONSOR_MEMO(SobiLineType.SPONSOR_MEMO, "MTXT"),
    VETO(SobiLineType.VETO_APPROVE_MEMO, "VETO")
    ;

    private SobiLineType sobiLineType;
    private String typeString;
    private static Map<SobiLineType, FullTextType> lineTypeMap = new HashMap<>();
    static{
        for(FullTextType fullTextType : FullTextType.values()){
            lineTypeMap.put(fullTextType.sobiLineType, fullTextType);
        }
    }
    public static FullTextType getTypeString(SobiLineType sobiLineType){ return lineTypeMap.get(sobiLineType); }
    
    FullTextType(SobiLineType sobiLineType, String typeString) {
        this.sobiLineType = sobiLineType;
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
