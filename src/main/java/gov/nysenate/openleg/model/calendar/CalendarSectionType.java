package gov.nysenate.openleg.model.calendar;

import java.util.HashMap;
import java.util.Map;

/**
 * CalendarSectionType enumerates the different sections of a calendar supplemental.
 * These sections designate a bill's stage within it's lifecycle after being reported
 * out of a committee.
 */
public enum CalendarSectionType
{
    ORDER_OF_THE_FIRST_REPORT(150, "BILLS ON ORDER OF THE FIRST REPORT"),
    ORDER_OF_THE_SECOND_REPORT(200, "BILLS ON ORDER OF THE SECOND REPORT"),
    ORDER_OF_THE_SPECIAL_REPORT(250, "BILLS ON ORDER OF THE SPECIAL REPORT"),
    THIRD_READING_FROM_SPECIAL_REPORT(350, "BILLS ON THIRD READING FROM SPECIAL REPORT"),
    THIRD_READING(400, "BILLS ON THIRD READING"),
    STARRED_ON_THIRD_READING(450, "BILLS STARRED ON THIRD READING");



    /** This code is used in the source sobi data to identify the section. */
    int code;
    String lrsRepresentation;

    static Map<Integer, CalendarSectionType> codeMap = new HashMap<>();
    static Map<String, CalendarSectionType>lrsMap = new HashMap<>();
    static {
        for (CalendarSectionType cst : CalendarSectionType.values()) {
            codeMap.put(cst.code, cst);
            lrsMap.put(cst.lrsRepresentation, cst);

        }


    }



    CalendarSectionType(int code) {
        this.code = code;
    }
    CalendarSectionType(int code, String lrsRepresentation) {
        this.lrsRepresentation = lrsRepresentation;
    }

    public int getCode() {
        return code;
    }

    public static CalendarSectionType valueOfCode(int code) {
        if (!codeMap.containsKey(code)) {
            throw new IllegalArgumentException("No CalendarSectionType matches code " + code);
        }
        return codeMap.get(code);
    }

    //new, might not work yet
    public static CalendarSectionType valueOflrsRepresentation(String lrsRepresentation) {
        if (!lrsMap.containsKey(lrsRepresentation)) {
            throw new IllegalArgumentException("No CalendarSectionType matches lrsRepresentation " + lrsRepresentation);
        }
        return lrsMap.get(lrsRepresentation);
    }
}
