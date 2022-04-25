package gov.nysenate.openleg.spotchecks.model;

import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.util.OutputUtils;
import gov.nysenate.openleg.notifications.model.NotificationType;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.notifications.model.NotificationType.*;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckContentType.*;
import static gov.nysenate.openleg.spotchecks.model.SpotCheckDataSource.*;

/**
 * Enumeration of the types of sources that can provide data for QA purposes.
 */
public enum SpotCheckRefType
{
    LBDC_DAYBREAK("daybreak", "Daybreak", LBDC, BILL, DAYBREAK_SPOTCHECK),

    LBDC_SCRAPED_BILL("scraped-bill", "Scraped Bill", LBDC, BILL, BILL_TEXT_SPOTCHECK),

    LBDC_CALENDAR_ALERT("floor-alert", "LBDC Calendar Alert", LBDC, CALENDAR, CALENDAR_SPOTCHECK),

    LBDC_AGENDA_ALERT("agenda-alert", "Agenda Alert", LBDC, AGENDA_WEEK, AGENDA_SPOTCHECK),

    SENATE_SITE_BILLS("senate-site-bills", "Nysenate.gov Bill", NYSENATE, BILL_AMENDMENT, SENSITE_BILL_SPOTCHECK),

    SENATE_SITE_CALENDAR("senate-site-calendar", "Nysenate.gov Calendar", NYSENATE, CALENDAR, SENSITE_CALENDAR_SPOTCHECK),

    SENATE_SITE_AGENDA("senate-site-agenda", "Nysenate.gov Agenda", NYSENATE, AGENDA, SENSITE_AGENDA_SPOTCHECK),

    SENATE_SITE_LAW("senate-site-law", "NYSenate.gov Law", NYSENATE, LAW, SENSITE_LAW_SPOTCHECK),

    OPENLEG_BILL ("openleg-bill", "Openleg Bill", OPENLEG, BILL, OPENLEG_SPOTCHECK),

    OPENLEG_CAL ("openleg-cal", "Openleg Cal", OPENLEG, CALENDAR, OPENLEG_SPOTCHECK),

    OPENLEG_AGENDA ("openleg-agenda", "Openleg Agenda", OPENLEG, AGENDA, OPENLEG_SPOTCHECK),
    ;

    private final String refName;
    private final String displayName;
    private final SpotCheckDataSource dataSource;
    private final SpotCheckContentType contentType;
    /** A notification type that is used to send notifications for this type of report */
    private final NotificationType notificationType;

    SpotCheckRefType(String refName, String displayName, SpotCheckDataSource dataSource,
                     SpotCheckContentType contentType, NotificationType type) {
        this.refName = refName;
        this.displayName = displayName;
        this.dataSource = dataSource;
        this.contentType = contentType;
        this.notificationType = type;
    }

    /** Get a set of SpotCheckMismatchTypes that are checked in this Reference Type. */
    public Set<SpotCheckMismatchType> checkedMismatchTypes() {
        return EnumSet.allOf(SpotCheckMismatchType.class)
                .stream()
                .filter(t -> t.getRefTypes().contains(this))
                .collect(Collectors.toSet());
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

    public SpotCheckDataSource getDataSource() {
        return dataSource;
    }

    public SpotCheckContentType getContentType() {
        return contentType;
    }

    private static final ImmutableMap<String, SpotCheckRefType> refNameMap = ImmutableMap.copyOf(
            Arrays.stream(SpotCheckRefType.values())
                    .collect(Collectors.toMap(SpotCheckRefType::getRefName, Function.identity()))
    );

    public static SpotCheckRefType getByRefName(String refName) {
        return refNameMap.get(refName);
    }

    public static List<SpotCheckRefType> get(SpotCheckDataSource dataSource,
                                             SpotCheckContentType contentType) {
        return Arrays.stream(SpotCheckRefType.values())
                .filter(refType -> refType.getDataSource().equals(dataSource) &&
                        refType.getContentType().equals(contentType)).toList();
    }

    public static String getRefJsonMap() {
        return getJsonMapHelper(SpotCheckRefType::getRefName);
    }

    public static String getDisplayJsonMap() {
        return getJsonMapHelper(SpotCheckRefType::getDisplayName);
    }

    public static String getRefContentTypeJsonMap() {
        return getJsonMapHelper(SpotCheckRefType::getContentType);
    }

    /**
     * Converts the set of enums into a Map from their name to some other field.
     * @param function to specify the field to get.
     * @return The Map as JSON.
     */
    private static String getJsonMapHelper(Function<SpotCheckRefType, ?> function) {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckRefType.class).stream()
                .collect(Collectors.toMap(SpotCheckRefType::name, function)));
    }
}