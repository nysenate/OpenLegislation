package gov.nysenate.openleg.model.sobi;

import java.util.HashMap;
import java.util.Map;

/**
 * SOBI files that are in the line item format contain character codes that indicate the type
 * of information that is to be applied. The SobiLineType enum maps the character codes for
 * easy use within the code base.
 */
public enum SobiLineType
{
    BILL_INFO('1'),
    LAW_SECTION('2'),
    TITLE('3'),
    BILL_EVENT('4'),
    SAME_AS('5'),
    SPONSOR('6'),
    CO_SPONSOR('7'),
    MULTI_SPONSOR('8'),
    PROGRAM_INFO('9'),
    ACT_CLAUSE('A'),
    LAW('B'),
    SUMMARY('C'),
    SPONSOR_MEMO('M'),
    VETO_APPROVE_MEMO('O'),
    RESOLUTION_TEXT('R'),
    TEXT('T'),
    VOTE_MEMO('V');

    private Character typeCode;
    private static Map<Character, SobiLineType> typeCodeMap = new HashMap<>();
    static {
        for (SobiLineType lineItem : SobiLineType.values()) {
            typeCodeMap.put(lineItem.getTypeCode(), lineItem);
        }
    }

    SobiLineType(Character typeCode) {
        this.typeCode = typeCode;
    }

    public static SobiLineType valueOfCode(Character typeCode) {
        return typeCodeMap.get(typeCode);
    }

    public char getTypeCode() {
        return typeCode;
    }
}
