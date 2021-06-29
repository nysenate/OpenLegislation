package gov.nysenate.openleg.legislation.bill;

import java.util.HashMap;

public enum AssemblyCommitteeCode {

    BANKS(3), CITIES(4), AGING(5), CODES(7), ALCOHOLISM_AND_DRUG_ABUSE(9),
    CONSUMER_AFFAIRS_AND_PROTECTION(10), EDUCATION(11), ELECTION_LAW(12), MENTAL_HEALTH(14), HEALTH(15),
    HIGHER_EDUCATION(16), INSURANCE(18), JUDICIARY(19), REAL_PROPERTY_TAXATION(20), ENERGY(21),
    OVERSIGHT__ANALYSIS__AND_INVESTIGATION(25), CHILDREN_AND_FAMILIES(26), RULES(27), SOCIAL_SERVICES(28),
    AGRICULTURE(32), ECONOMIC_DEVELOPMENT(33), CORRECTION(34), CORPORATIONS__AUTHORITIES__AND_COMMISSIONS(35),
    ETHICS_AND_GUIDANCE(36), GOVERNMENTAL_EMPLOYEES(37), GOVERNMENTAL_OPERATIONS(38), HOUSING(39),
    LABOR(40), LOCAL_GOVERNMENTS(41), TRANSPORTATION(42), WAYS_AND_MEANS(43),
    VETERANS_AFFAIRS(47), // Should have an apostrophe after the s.
    ENVIRONMENTAL_CONSERVATION(51), RACING_AND_WAGERING(53), SMALL_BUSINESS(54),
    LIBRARIES_AND_EDUCATION_TECHNOLOGY(59), TOURISM_PARKS_ARTS_AND_SPORTS_DEVELOPMENT(64);

    private static final HashMap<Integer, AssemblyCommitteeCode> CODE_BY_NUM = new HashMap<>();

    static {
        for (AssemblyCommitteeCode code : values())
            CODE_BY_NUM.put(code.committeeNumber, code);
    }

    private final String committee;
    private final int committeeNumber;

    AssemblyCommitteeCode(int comNum) {
        this.committee = name().toLowerCase().replaceAll("__", ", ").replaceAll("_", " ");
        this.committeeNumber = comNum;
    }

    public static AssemblyCommitteeCode findAssemblyCommittee(int committeeNumber) {
        return CODE_BY_NUM.getOrDefault(committeeNumber, null);
    }

    public String getCommittee() {
        return committee;
    }

    public int getCommitteeNumber() {
        return committeeNumber;
    }
}
