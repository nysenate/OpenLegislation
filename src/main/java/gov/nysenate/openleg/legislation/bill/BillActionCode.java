package gov.nysenate.openleg.legislation.bill;

public enum BillActionCode {

    REFERRED_TO("referred to", "committee code"), //1
    AMEND_AND_RECOMMIT_TO("amend and recommit to", "committee code"), //2
    REPORTED_REFERRED_TO("reported referred to","comittee code"), //3
    REPORTED_AND_COMMITTED_TO("reported and committed to","committee code"), //4
    COMMITTED_TO("committed to","committee code"), //5
    RECOMMITTED_TO("recommitted to","committee code"), //6
    REFERENCE_CHANGE_TO("reference change to","committee code"), //7
    OPINION_REFERRED_TO("opinion referred to","committee code"), //8
    COMMITTEE_DISCHARGED_AND_COMMITTED_TO("committee discharged and committed to","committee code"), //9
    REPORTED_WITH_AMENDMENTS_AND_COMMITTED_TO("reported with amdnedments and committed to","committee code"), //10
    HELD_FOR_CONSIDERATION_IN("held for consideration in","committee code"), //11
    DEFEATED_IN("defeated in","committee code"), //12
    REPORTED("reported","calendar number"), //13
    REPORTED_WITH_AMENDMENT("reported with amendments","calendar number"), //14
    RULES_REPORT("rules report","calendar number"), //15
    RULES_REPORT_WITH_AMENDMENT("rules report with amendment","calendar number"), //16
    ORDERED_TO_THIRD_READING("ordered to third reading","calendar number"), //17
    FIRST_REPORT("1st report","calendar number"), //18
    SECOND_REPORT("2nd report","calendar number"), //19
    SPECIAL_REPORT("special report","calendar number"), //20
    COMPANION_BILL("companion bill","bill number"), //21
    SUBSITUTED_FOR("substitued for","bill number"), //22
    SUBSTITUTED_BY("substituted","bill number"), //23
    CHAPTER("chapter","chapter number"), //25
    SIGNED("signed","chapter number"), //26
    VETOED("vetoed","memo number"), //27
    AMENDED_BY_RESTORING_TO_PREVIOUS_PRINT("amended by restoring to previous print","bill number & amendment code"), //28
    LINE_VETO("line veto","memo number"), //29
    HOME_RULE_REQUEST("home rule request",""), //30
    MESSAGE_OF_NECESSITY("message of necessity",""), //31
    ADVANCED_TO_THIRD_READING("advanced to third reading","calendar number (optional)"), //32
    RESTORED_TO_THIRD_READING("restored to third reading",""), //33
    VOTE_RECONSIDERED_RESTORED_TO_PREVIOUS_PRINT("vote reconsidered-restored to third reading",""), //34
    COMMITTEE_DISCHARGED_AND_ADVANCED_TO_THIRD_READING("committee discharged and advanced to third reading",""), //35
    REPORTED_RESTORED_TO_THIRD_READING("reported restored to third reading",""), //36
    AMENDED("amended","bill number & amendment code"), //37
    ASSEMBLY_CONCURS_IN_SENATE_AMENDMENTS("assembly concurs in senate amendments",""), //38
    SENATE_CONCURS_IN_ASSEMBLY_AMENDMENTS("senate concurs in assembly amendments",""), //39

    PASSED("passed",""), //40
    REPASSED("repassed",""), //41
    ADOPTED("adopted",""), //42
    LOST("lost",""), //43
    MOTION_TO_AMEND_LOST("motion to amend lost",""), //44
    MOTION_TO_DISCHARGE_LOST("motion to discharge lost",""), //45
    MOTION_TAKEN_FROM_TABLE("motion taken from table",""), //46
    MOTION_TO_RECONSIDER_TABLED("motion to reconsider-tabled",""), //47
    RECOMMIT_ENACTING_CLAUSE_STRIKEN("recommit, enacting clause striken",""), //48
    SUBSTITUTION_RECONSIDERED("substiution reconsidered",""), //49
    TO_ATTORNEY_GENERAL_FOR_OPINION("to attorney-general for opinion",""), //50

    DELIVERED_TO_GOVERNOR("delivered to governor",""), //51
    DELIVERED_TO_SECRETARY_OF_STATE("delivered to secretry of state",""), //52
    DELIVERED_TO_ASSEMBLY("delivered to assembly",""), //53
    DELIVERED_TO_SENATE("delivered to senate",""), //54
    RECALLED_FROM_GOVERNOR("recalled from governor",""), //55
    RECALLED_FROM_ASSEMBLY("recalled from assembly",""), //56
    RECALLED_FROM_SENATE("recalled from senate",""), //57
    RETURNED_TO_GOVERNOR("returned to governor",""), //58
    RETURNED_FROM_GOVERNOR("returned to governor",""), //59
    RETURNED_TO_ASSEMBLY("returned to assembly",""), //60
    RETURNED_TO_SENATE("returned to senate",""), //61

    ;

    protected String billActionDesc;

    protected String expectedActionData;

    BillActionCode(String billActionDesc, String expectedActionData) {
        this.billActionDesc = billActionDesc;
        this.expectedActionData = expectedActionData;
    }

    public String getBillActionDesc() {
        return billActionDesc;
    }

    public String getExpectedActionData() {
        return expectedActionData;
    }

    public BillActionCode findBillActionCode(int actionNumber) {
        BillActionCode requestedCode;
        switch (actionNumber) {
            case 1:
                requestedCode = REFERRED_TO;
                break;
            case 2:
                requestedCode = AMEND_AND_RECOMMIT_TO;
                break;
            case 3:
                requestedCode = REPORTED_REFERRED_TO;
                break;
            case 4:
                requestedCode = REPORTED_AND_COMMITTED_TO;
                break;
            case 5:
                requestedCode = COMMITTED_TO;
                break;
            case 6:
                requestedCode = RECOMMITTED_TO;
                break;
            case 7:
                requestedCode = REFERENCE_CHANGE_TO;
                break;
            case 8:
                requestedCode = OPINION_REFERRED_TO;
                break;
            case 9:
                requestedCode = COMMITTEE_DISCHARGED_AND_COMMITTED_TO;
                break;
            case 10:
                requestedCode = REPORTED_WITH_AMENDMENTS_AND_COMMITTED_TO;
                break;
            case 11:
                requestedCode = HELD_FOR_CONSIDERATION_IN;
                break;
            case 12:
                requestedCode = DEFEATED_IN;
                break;
            case 13:
                requestedCode = REPORTED;
                break;
            case 14:
                requestedCode = REPORTED_WITH_AMENDMENT;
                break;
            case 15:
                requestedCode = RULES_REPORT;
                break;
            case 16:
                requestedCode = RULES_REPORT_WITH_AMENDMENT;
                break;
            case 17:
                requestedCode = ORDERED_TO_THIRD_READING;
                break;
            case 18:
                requestedCode = FIRST_REPORT;
                break;
            case 19:
                requestedCode = SECOND_REPORT;
                break;
            case 20:
                requestedCode = SPECIAL_REPORT;
                break;
            case 21:
                requestedCode = COMPANION_BILL;
                break;
            case 22:
                requestedCode = SUBSITUTED_FOR;
                break;
            case 23:
                requestedCode = SUBSTITUTED_BY;
                break;
            case 25:
                requestedCode = CHAPTER;
                break;
            case 26:
                requestedCode = SIGNED;
                break;
            case 27:
                requestedCode = VETOED;
                break;
            case 28:
                requestedCode = AMENDED_BY_RESTORING_TO_PREVIOUS_PRINT;
                break;
            case 29:
                requestedCode = LINE_VETO;
                break;
            case 30:
                requestedCode = HOME_RULE_REQUEST;
                break;
            case 31:
                requestedCode = MESSAGE_OF_NECESSITY;
                break;
            case 32:
                requestedCode = ADVANCED_TO_THIRD_READING;
                break;
            case 33:
                requestedCode = RESTORED_TO_THIRD_READING;
                break;
            case 34:
                requestedCode = VOTE_RECONSIDERED_RESTORED_TO_PREVIOUS_PRINT;
                break;
            case 35:
                requestedCode = COMMITTEE_DISCHARGED_AND_ADVANCED_TO_THIRD_READING;
                break;
            case 36:
                requestedCode = REPORTED_RESTORED_TO_THIRD_READING;
                break;
            case 37:
                requestedCode = AMENDED;
                break;
            case 38:
                requestedCode = ASSEMBLY_CONCURS_IN_SENATE_AMENDMENTS;
                break;
            case 39:
                requestedCode = SENATE_CONCURS_IN_ASSEMBLY_AMENDMENTS;
                break;
            case 40:
                requestedCode = PASSED;
                break;
            case 41:
                requestedCode = REPASSED;
                break;
            case 42:
                requestedCode = ADOPTED;
                break;
            case 43:
                requestedCode = LOST;
                break;
            case 44:
                requestedCode = MOTION_TO_AMEND_LOST;
                break;
            case 45:
                requestedCode = MOTION_TO_DISCHARGE_LOST;
                break;
            case 46:
                requestedCode = MOTION_TAKEN_FROM_TABLE;
                break;
            case 47:
                requestedCode = MOTION_TO_RECONSIDER_TABLED;
                break;
            case 48:
                requestedCode = RECOMMIT_ENACTING_CLAUSE_STRIKEN;
                break;
            case 49:
                requestedCode = SUBSTITUTION_RECONSIDERED;
                break;
            case 50:
                requestedCode = TO_ATTORNEY_GENERAL_FOR_OPINION;
                break;
            case 51:
                requestedCode = DELIVERED_TO_GOVERNOR;
                break;
            case 52:
                requestedCode = DELIVERED_TO_SECRETARY_OF_STATE;
                break;
            case 53:
                requestedCode = DELIVERED_TO_ASSEMBLY;
                break;
            case 54:
                requestedCode = DELIVERED_TO_SENATE;
                break;
            case 55:
                requestedCode = RECALLED_FROM_GOVERNOR;
                break;
            case 56:
                requestedCode = RECALLED_FROM_ASSEMBLY;
                break;
            case 57:
                requestedCode = RECALLED_FROM_SENATE;
                break;
            case 58:
                requestedCode = RETURNED_TO_GOVERNOR;
                break;
            case 59:
                requestedCode = RETURNED_FROM_GOVERNOR;
                break;
            case 60:
                requestedCode = RETURNED_TO_ASSEMBLY;
                break;
            case 61:
                requestedCode = RETURNED_TO_SENATE;
                break;



            default:
                requestedCode = null;
        }
        return requestedCode;
    }
}
