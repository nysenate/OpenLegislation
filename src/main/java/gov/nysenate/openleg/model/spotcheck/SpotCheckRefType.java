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
    LBDC_DAYBREAK("daybreak", "Daybreak", NotificationType.DAYBREAK_SPOTCHECK),

    LBDC_SCRAPED_BILL("scraped-bill", "Scraped Bill", NotificationType.BILL_TEXT_SPOTCHECK),

    LBDC_CALENDAR_ALERT("floor-alert", "Floor Calendar Alert", NotificationType.CALENDAR_SPOTCHECK),

    LBDC_AGENDA_ALERT("agenda-alert", "Agenda Alert", NotificationType.AGENDA_SPOTCHECK),

    SENATE_SITE_BILLS("senate-site-bills", "Nysenate.gov Bill", NotificationType.SENSITE_BILL_SPOTCHECK),

    ;

    private String refName;
    private String displayName;

    /** A notification type that is used to send notifications for this type of report */
    private NotificationType notificationType;

    private SpotCheckRefType(String refName, String displayName, NotificationType type) {
        this.refName = refName;
        this.displayName = displayName;
        this.notificationType = type;
    }

    public String getRefName() {
        return refName;
    }

    public String getDisplayName() {
        return displayName;
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
    public static String getRefJsonMap() {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckRefType.class).stream()
                .collect(Collectors.toMap(SpotCheckRefType::name, SpotCheckRefType::getRefName)));
    }


    /**
     * Get a json object of each refType mapped to its display name
     */
    public static String getDisplayJsonMap() {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckRefType.class).stream()
                .collect(Collectors.toMap(SpotCheckRefType::name, SpotCheckRefType::getDisplayName)));
    }

}