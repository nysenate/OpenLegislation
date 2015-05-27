package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.model.notification.NotificationType;
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
    LBDC_DAYBREAK("daybreak", NotificationType.DAYBREAK_SPOTCHECK),

    LBDC_SCRAPED_BILL("scraped-bill", NotificationType.BILL_TEXT_SPOTCHECK),

    LBDC_CALENDAR_ALERT("floor-alert", NotificationType.CALENDAR_SPOTCHECK),

    LBDC_AGENDA_ALERT("agenda-alert", NotificationType.AGENDA_SPOTCHECK),

    ;

    private String refName;

    /** A notification type that is used to send notifications for this type of report */
    private NotificationType notificationType;

    private SpotCheckRefType(String refName, NotificationType type) {
        this.refName = refName;
        this.notificationType = type;
    }

    public String getRefName() {
        return refName;
    }

    public NotificationType getNotificationType() {
        return notificationType;
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