package gov.nysenate.openleg.legislation.bill;

import java.util.Objects;

import static gov.nysenate.openleg.legislation.bill.BillActionCode.ExpectedActionData.*;

public enum BillActionCode {

    REFERRED_TO(COMMITTEE_CODE),
    AMEND_AND_RECOMMIT_TO(COMMITTEE_CODE),
    REPORTED_REFERRED_TO(COMMITTEE_CODE),
    REPORTED_AND_COMMITTED_TO(COMMITTEE_CODE),
    COMMITTED_TO(COMMITTEE_CODE),
    RECOMMITTED_TO(COMMITTEE_CODE),
    REFERENCE_CHANGE_TO(COMMITTEE_CODE),
    OPINION_REFERRED_TO(COMMITTEE_CODE),
    COMMITTEE_DISCHARGED_AND_COMMITTED_TO(COMMITTEE_CODE),
    REPORTED_WITH_AMENDMENTS_AND_COMMITTED_TO(COMMITTEE_CODE),
    HELD_FOR_CONSIDERATION_IN(COMMITTEE_CODE),
    DEFEATED_IN(COMMITTEE_CODE),
    REPORTED(CALENDAR_NUMBER),
    REPORTED_WITH_AMENDMENTS(CALENDAR_NUMBER),
    RULES_REPORT(CALENDAR_NUMBER),
    RULES_REPORT_WITH_AMENDMENT(CALENDAR_NUMBER),
    ORDERED_TO_THIRD_READING(CALENDAR_NUMBER),
    FIRST_REPORT(CALENDAR_NUMBER),
    SECOND_REPORT(CALENDAR_NUMBER),
    SPECIAL_REPORT(CALENDAR_NUMBER),
    COMPANION_BILL(BILL_NUMBER),
    SUBSTITUTED_FOR(BILL_NUMBER),
    SUBSTITUTED_BY(BILL_NUMBER),
    MISSING(""), // 24
    CHAPTER(CHAPTER_NUMBER),
    SIGNED(CHAPTER_NUMBER),
    VETOED(MEMO_NUMBER),
    AMENDED_BY_RESTORING_TO_PREVIOUS_PRINT(BILL_NUMBER_AND_AMENDMENT_CODE),
    LINE_VETO(MEMO_NUMBER),
    HOME_RULE_REQUEST,
    MESSAGE_OF_NECESSITY,
    ADVANCED_TO_THIRD_READING(CALENDAR_NUMBER), // Calendar number is optional.
    RESTORED_TO_THIRD_READING,
    VOTE_RECONSIDERED_RESTORED_TO_PREVIOUS_PRINT("vote reconsidered-restored to third reading"),
    COMMITTEE_DISCHARGED_AND_ADVANCED_TO_THIRD_READING,
    REPORTED_RESTORED_TO_THIRD_READING,
    AMENDED(BILL_NUMBER_AND_AMENDMENT_CODE),
    ASSEMBLY_CONCURS_IN_SENATE_AMENDMENTS,
    SENATE_CONCURS_IN_ASSEMBLY_AMENDMENTS,

    PASSED,
    REPASSED,
    ADOPTED,
    LOST,
    MOTION_TO_AMEND_LOST,
    MOTION_TO_DISCHARGE_LOST,
    MOTION_TAKEN_FROM_TABLE,
    MOTION_TO_RECONSIDER_TABLED("motion to reconsider-tabled"),
    RECOMMIT_ENACTING_CLAUSE_STRICKEN("recommit, enacting clause stricken"),
    SUBSTITUTION_RECONSIDERED,
    TO_ATTORNEY_GENERAL_FOR_OPINION("to attorney-general for opinion"),

    DELIVERED_TO_GOVERNOR,
    DELIVERED_TO_SECRETARY_OF_STATE,
    DELIVERED_TO_ASSEMBLY,
    DELIVERED_TO_SENATE,
    RECALLED_FROM_GOVERNOR,
    RECALLED_FROM_ASSEMBLY,
    RECALLED_FROM_SENATE,
    RETURNED_TO_GOVERNOR,
    RETURNED_FROM_GOVERNOR,
    RETURNED_TO_ASSEMBLY,
    RETURNED_TO_SENATE;

    enum ExpectedActionData {
        COMMITTEE_CODE, CALENDAR_NUMBER, BILL_NUMBER, CHAPTER_NUMBER, MEMO_NUMBER, BILL_NUMBER_AND_AMENDMENT_CODE
    }

    private final String billActionDesc, expectedActionData;

    BillActionCode(String billActionDesc, ExpectedActionData expectedActionData) {
        this.billActionDesc = Objects.requireNonNullElseGet(billActionDesc, () ->
                name().replaceAll("_", " ").toLowerCase());
        if (expectedActionData == null)
            this.expectedActionData = "";
        else
            this.expectedActionData = expectedActionData.name().replaceAll("_", " ").toLowerCase();
    }

    BillActionCode(String billActionDesc) {
        this(billActionDesc, null);
    }

    BillActionCode(ExpectedActionData data) {
        this(null, data);
    }

    BillActionCode() {
        this(null, null);
    }

    public String getBillActionDesc() {
        return billActionDesc;
    }

    public String getExpectedActionData() {
        return expectedActionData;
    }

    public static BillActionCode findBillActionCode(int num) {
        if (num >= 1 && num <= values().length && num != 24)
            return values()[num - 1];
        return null;
    }
}
