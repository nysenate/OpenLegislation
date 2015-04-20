package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.util.OutputUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Enumeration of the types of sources that can provide data for QA purposes.
 */
public enum SpotCheckRefType
{
    LBDC_DAYBREAK("daybreak"),             // DayBreaks consist of a set of files sent by LBDC weekly
                               // which consist of a dump of basic information for all bills
                               // that are active in the current session.
    LBDC_SCRAPED_BILL("scraped-bill"),

    LBDC_FLOOR_CALENDAR("alert-floor"),

    LBDC_ACTIVE_LIST("alert-activelist"),

    LBDC_AGENDA_ALERT("alert-agenda"),

    ;

    private String refName;

    private SpotCheckRefType(String refName) {
        this.refName = refName;
    }

    public String getRefName() {
        return refName;
    }

    private static final ImmutableMap<String, SpotCheckRefType> refNameMap = ImmutableMap.copyOf(
            Arrays.asList(SpotCheckRefType.values()).stream()
                    .collect(Collectors.toMap(SpotCheckRefType::getRefName, Function.identity()))
    );

    public static SpotCheckRefType getByRefName(String refName) {
        return refNameMap.get(refName);
    }

    /**
     * Get a json object of each refType mapped to its refName
     */
    public static String getJsonMap() {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckRefType.class).stream()
                .collect(Collectors.toMap(SpotCheckRefType::name, SpotCheckRefType::getRefName)));
    }

}