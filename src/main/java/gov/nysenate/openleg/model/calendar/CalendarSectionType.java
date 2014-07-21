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
    ORDER_OF_THE_FIRST_REPORT(150),
    ORDER_OF_THE_SECOND_REPORT(200),
    ORDER_OF_THE_SPECIAL_REPORT(250),
    THIRD_READING_FROM_SPECIAL_REPORT(350),
    THIRD_READING(400),
    STARRED_ON_THIRD_READING(450);

    /** This code is used in the source sobi data to identify the section. */
    int code;

    static Map<Integer, CalendarSectionType> codeMap = new HashMap<>();
    static {
        for (CalendarSectionType cst : CalendarSectionType.values()) {
            codeMap.put(cst.code, cst);
        }
    }

    CalendarSectionType(int code) {
        this.code = code;
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
}
