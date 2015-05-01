package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.util.OutputUtils;

import java.util.EnumSet;
import java.util.stream.Collectors;

/**
 * Enumeration of the different bill fields that we can check for data quality issues.
 */
public enum SpotCheckMismatchType
{
    /** --- General --- */

    REFERENCE_DATA_MISSING("Ref. Missing"),
    OBSERVE_DATA_MISSING("Data Missing"),

    /** --- Bill data mismatches --- */

    BILL_ACTION("Action"),
    BILL_ACTIVE_AMENDMENT("Amendment"),
    BILL_AMENDMENT_PUBLISH("Publish"),
    BILL_COSPONSOR("Co Sponsor"),
    BILL_FULLTEXT_PAGE_COUNT("Page Count"),
    BILL_FULL_TEXT("Full Text"),
    BILL_FULL_TEXT_NORMALIZED("Normalized Full Text"),
    BILL_FULL_TEXT_SUPER_NORMALIZED("Super Normalized Full Text"),
    BILL_FULL_TEXT_ULTRA_NORMALIZED("Ultra Normalized Full Text"),
    BILL_LAW_CODE("Law Code"),
    BILL_LAW_CODE_SUMMARY("Law/Summary"),
    BILL_LAW_SECTION("Law Section"),
    BILL_MEMO("Memo"),
    BILL_MULTISPONSOR("Multi Sponsor"),
    BILL_SESSION_YEAR("Session Year"),
    BILL_SPONSOR("Sponsor"),
    BILL_SPONSOR_MEMO("Sponsor Memo"),
    BILL_SAMEAS("Same As"),
    BILL_SUMMARY("Summary"),
    BILL_TITLE("Title"),


    /** --- Active List data mismatches --- */

    LIST_CAL_DATE("Cal Date"),
    LIST_RELEASE_DATE_TIME("Release Time"),
    LIST_CALENDAR_MISMATCH("Calendar?"),
    LIST_ENTRY_MISMATCH("Cal Entry"),

    /** --- Agenda Committee Meeting info mismatches --- */

    AGENDA_BILL_LISTING("Bill List"),
    AGENDA_CHAIR("Chair"),
    AGENDA_MEETING_TIME("Meeting Time"),
    AGENDA_LOCATION("Location"),
    AGENDA_NOTES("Notes"),

    /** --- Supplemental mismatches --- */
    SUPPLEMENTAL_VERSION("Supplemental Version"),
    SUPPLEMENTAL_CAL_DATE("Supplemental Calendar Date"),
    SUPPLEMENTAL_SECTION_TYPE("Supplemental Section"),
    SUPPLEMENTAL_ENTRY("Supplemental Entry"),

    /** --- Active list mismatches --- */
    ACTIVE_LIST_SEQUENCE_NO("Active List Sequence No"),
    ACTIVE_LIST_CAL_DATE("Active List Cal Date"),
    ACTIVE_LIST_NOTES("Active List Notes"),
    ACTIVE_LIST_ENTRY("Active List Entry"),

    ;

    private String displayName;

    private SpotCheckMismatchType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static String getJsonMap() {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckMismatchType.class).stream()
                .collect(Collectors.toMap(SpotCheckMismatchType::name, SpotCheckMismatchType::getDisplayName)));
    }
}