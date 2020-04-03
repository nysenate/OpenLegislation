package gov.nysenate.openleg.model.spotcheck;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import gov.nysenate.openleg.util.OutputUtils;

import java.util.*;
import java.util.stream.Collectors;

import static gov.nysenate.openleg.model.spotcheck.SpotCheckRefType.*;

/**
 * Enumeration of the different bill fields that we can check for data quality issues.
 */
public enum SpotCheckMismatchType {
    /* --- General --- */

    All("All", SpotCheckRefType.values()),
    REFERENCE_DATA_MISSING("Ref. Missing", SpotCheckRefType.values()),
    OBSERVE_DATA_MISSING("Source Missing", SpotCheckRefType.values()),

    /* --- Bill data mismatches --- */

    BILL_ACTION("Action", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_ACTIVE_AMENDMENT("Active Amendment", LBDC_DAYBREAK, LBDC_SCRAPED_BILL, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_AMENDMENT_PUBLISH("Published Status", LBDC_DAYBREAK, SENATE_SITE_BILLS),
    BILL_COSPONSOR("Co Sponsor", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_FULLTEXT_PAGE_COUNT("Page Count", LBDC_DAYBREAK),
    BILL_TEXT_LINE_OFFSET("Text Line Offset", LBDC_SCRAPED_BILL, SENATE_SITE_BILLS),
    BILL_TEXT_CONTENT("Text Content", LBDC_SCRAPED_BILL, SENATE_SITE_BILLS),
    BILL_TEXT_RESO_HEADER("Reso. Header", LBDC_SCRAPED_BILL),
    BILL_LAW_CODE_SUMMARY("Law/Summary", LBDC_DAYBREAK),
    BILL_LAW_SECTION("Law Section", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_MEMO("Memo", LBDC_SCRAPED_BILL, SENATE_SITE_BILLS),
    BILL_MULTISPONSOR("Multi Sponsor", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_SESSION_YEAR("Session Year", LBDC_DAYBREAK, OPENLEG_BILL),
    BILL_SPONSOR("Sponsor", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_ADDITIONAL_SPONSOR("Bill Additional Sponsor", OPENLEG_BILL),
    BILL_TITLE("Title", LBDC_DAYBREAK, SENATE_SITE_BILLS, OPENLEG_BILL),

    BILL_BASE_PRINT_NO("Base Print No", SENATE_SITE_BILLS),
    BILL_CHAMBER("Chamber", SENATE_SITE_BILLS),
    BILL_SAME_AS("Same As", SENATE_SITE_BILLS),
    BILL_PREVIOUS_VERSIONS("Prev. Versions", SENATE_SITE_BILLS),
    BILL_IS_AMENDED("Is Amended", SENATE_SITE_BILLS),
    BILL_HAS_SAME_AS("Has Same As", SENATE_SITE_BILLS),
    BILL_PUBLISH_DATE("Publish Date", SENATE_SITE_BILLS),
    BILL_MILESTONES("Milestones", SENATE_SITE_BILLS),
    BILL_LAST_STATUS("Last Status", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_LAST_STATUS_COMM("Last Status Committee", SENATE_SITE_BILLS),
    BILL_LAST_STATUS_DATE("Last Status Date", SENATE_SITE_BILLS),
    BILL_SUMMARY("Summary", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_LAW_CODE("Law Code", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_TEXT("Full Text", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_VOTE_INFO("Bill Vote Info", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_VOTE_ROLL("Bill Vote Roll", SENATE_SITE_BILLS, OPENLEG_BILL),
    BILL_SCRAPE_VOTE("Bill Scrape Vote", LBDC_SCRAPED_BILL),
    BILL_HTML_TEXT("HTML Full Text", LBDC_SCRAPED_BILL),

    BILL_APPROVAL_MESSAGE("Bill Approve Message", OPENLEG_BILL),
    BILL_COMMITTEE_AGENDAS("Bill Committee Agendas", OPENLEG_BILL),
    BILL_PAST_COMMITTEES("Bill Past Committees", OPENLEG_BILL),
    BILL_CALENDARS("Bill Calendars", OPENLEG_BILL),

    /* --- Agenda mismatches --- */
    AGENDA_ID("Agenda Id", SENATE_SITE_AGENDA),

    /* --- Agenda Committee Meeting info mismatches --- */

    AGENDA_BILL_LISTING("Bill List", LBDC_AGENDA_ALERT, SENATE_SITE_AGENDA, OPENLEG_AGENDA),
    AGENDA_CHAIR("Chair", LBDC_AGENDA_ALERT, OPENLEG_AGENDA),
    AGENDA_MEETING_TIME("Meeting Time", LBDC_AGENDA_ALERT, SENATE_SITE_AGENDA, OPENLEG_AGENDA),
    AGENDA_LOCATION("Location", LBDC_AGENDA_ALERT, SENATE_SITE_AGENDA, OPENLEG_AGENDA),
    AGENDA_NOTES("Notes", LBDC_AGENDA_ALERT, SENATE_SITE_AGENDA, OPENLEG_AGENDA),
    AGENDA_BILLS("Bills", LBDC_AGENDA_ALERT, SENATE_SITE_AGENDA, OPENLEG_AGENDA),
    AGENDA_MODIFIED_DATE_TIME("Modified Date Time", OPENLEG_AGENDA),
    AGENDA_HAS_VOTES("Has Votes", OPENLEG_AGENDA),
    AGENDA_ATTENDANCE_LIST("Agenda Attendance List", OPENLEG_AGENDA),
    AGENDA_VOTES_LIST("Agenda Votes List", OPENLEG_AGENDA),

    /* --- Calendar mismatches --- */
    CALENDAR_ID("Calendar Id", SENATE_SITE_CALENDAR),

    /* --- Floor / Supplemental mismatches --- */
    SUPPLEMENTAL_ENTRY("Supplemental Entry", LBDC_CALENDAR_ALERT, SENATE_SITE_CALENDAR, OPENLEG_CAL),
    FLOOR_ENTRY("Floor Entry", SENATE_SITE_CALENDAR, OPENLEG_CAL),

    FLOOR_CAL_DATE("Floor Calendar Date", LBDC_CALENDAR_ALERT, OPENLEG_CAL),
    FLOOR_CAL_YEAR("Floor Calendar Year", OPENLEG_CAL),
    FLOOR_RELEASE_DATE_TIME("Floor Release Date Time", OPENLEG_CAL),
    FLOOR_SECTION_TYPE("Floor Section", LBDC_CALENDAR_ALERT, OPENLEG_CAL),

    /* --- Active list mismatches --- */
    ACTIVE_LIST_CAL_DATE("Active List Calendar Date", LBDC_CALENDAR_ALERT, OPENLEG_CAL),
    ACTIVE_LIST_ENTRY("Active List Entry", LBDC_CALENDAR_ALERT, SENATE_SITE_CALENDAR, OPENLEG_CAL),

    /* --- Active List data mismatches --- */
    ACTIVE_LIST_RELEASE_DATE_TIME("Active List Release Time", LBDC_CALENDAR_ALERT, OPENLEG_CAL),
    ACTIVE_LIST_NOTES("Active List Notes", OPENLEG_CAL),

    /* --- Law Mismatches --- */

    LAW_TREE("Law Tree", SENATE_SITE_LAW),
    LAW_IDS("Law Ids", SENATE_SITE_LAW),
    LAW_TREE_NODE_NOT_FOUND("Tree Node Not Found", SENATE_SITE_LAW),

    // Tree Node Mismatches
    LAW_DOC_NEXT_SIBLING_URL("Next Sibling Url", SENATE_SITE_LAW),
    LAW_DOC_PREV_SIBLING_URL("Prev Sibling Url", SENATE_SITE_LAW),
    LAW_DOC_PARENT_LOC_IDS("Parent Location Ids", SENATE_SITE_LAW),
    LAW_DOC_PARENT_ID("Parent Id", SENATE_SITE_LAW),
    LAW_DOC_REPEALED("Is Repealed", SENATE_SITE_LAW),
    LAW_DOC_REPEALED_DATE("Repealed Date", SENATE_SITE_LAW),
    LAW_DOC_SEQUENCE_NO("Sequence No.", SENATE_SITE_LAW),
    LAW_DOC_FROM_SECTION("From Section", SENATE_SITE_LAW),
    LAW_DOC_TO_SECTION("To Section", SENATE_SITE_LAW),

    // Document Mismatches
    LAW_DOC_TITLE("Title", SENATE_SITE_LAW),
    LAW_DOC_ACTIVE_DATE("Active Date", SENATE_SITE_LAW),
    LAW_DOC_DOC_LEVEL_ID("Doc Level Id", SENATE_SITE_LAW),
    LAW_DOC_DOC_TYPE("Doc Type", SENATE_SITE_LAW),
    LAW_DOC_LAW_ID("Law Id", SENATE_SITE_LAW),
    LAW_DOC_LAW_NAME("Law Name", SENATE_SITE_LAW),
    LAW_DOC_LAW_TYPE("Law Type", SENATE_SITE_LAW),
    LAW_DOC_LOCATION_ID("Location Id", SENATE_SITE_LAW),
    LAW_DOC_TEXT("Text", SENATE_SITE_LAW),
    ;

    private String displayName;
    Set<SpotCheckRefType> refTypes;

    SpotCheckMismatchType(String displayName, SpotCheckRefType... refTypes) {
        this.displayName = displayName;
        this.refTypes = new HashSet<>(Arrays.asList(refTypes));
    }

    public boolean possibleForContentType(SpotCheckContentType contentType) {
        Objects.requireNonNull(contentType);
        return this.getRefTypes().stream()
                .map(SpotCheckRefType::getContentType)
                .anyMatch(contentType::equals);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Set<SpotCheckRefType> getRefTypes() {
        return refTypes;
    }

    private static final SetMultimap<SpotCheckRefType, SpotCheckMismatchType> refTypeMismatchMap = HashMultimap.create();

    static {
        for (SpotCheckMismatchType type : SpotCheckMismatchType.values()) {
            type.refTypes.forEach(refType -> refTypeMismatchMap.put(refType, type));
        }
    }

    public static Set<SpotCheckMismatchType> getMismatchTypes(SpotCheckRefType refType) {
        return refTypeMismatchMap.get(refType);
    }

    public static SpotCheckMismatchType getSpotCheckMismatchByDisplayName(String displayName) {
        for (SpotCheckMismatchType spotCheckMismatchType : SpotCheckMismatchType.values()) {
            if (spotCheckMismatchType.displayName.equals(displayName))
                return spotCheckMismatchType;
        }
        return null;
    }

    public static String getName(SpotCheckMismatchType spotCheckMismatchType) {
        if (spotCheckMismatchType == null) // if the filter set to ALL then disable the filter by passing ''%' to where statement
            return "%";
        return spotCheckMismatchType.name();
    }

    public static String getJsonMap() {
        return OutputUtils.toJson(EnumSet.allOf(SpotCheckMismatchType.class).stream()
                .collect(Collectors.toMap(SpotCheckMismatchType::name, SpotCheckMismatchType::getDisplayName)));
    }

    public static String getJsonReftypeMismatchMap() {
        return OutputUtils.toJson(refTypeMismatchMap.asMap());
    }
}