package gov.nysenate.openleg.legislation.bill;

import java.util.HashMap;
import java.util.Map;

public enum SenateCommitteeCode {

    CONSUMER_PROTECTION(2), BANKS(3), CITIES(4), AGING(5), CIVIL_SERVICE_AND_PENSIONS(6),
    CODES(7), CHILDREN_AND_FAMILIES(8), ALCOHOLISM_AND_DRUG_ABUSE(9), EDUCATION(11), FINANCE(13),
    HEALTH(15), HIGHER_EDUCATION(16), HOUSING__CONSTRUCTION_AND_COMMUNITY_DEVELOPMENT(17), INSURANCE(18),
    JUDICIARY(19), CRIME_VICTIMS__CRIME_AND_CORRECTION(24), RULES(27), SOCIAL_SERVICES(28),
    INVESTIGATIONS_AND_GOVERNMENT_OPERATIONS(29), AGRICULTURE(32), ETHICS(35),
    CORPORATIONS__AUTHORITIES_AND_COMMISSIONS(-1), //35
    LABOR(40), TRANSPORTATION(42), ENVIRONMENTAL_CONSERVATION(45),
    COMMERCE__ECONOMIC_DEVELOPMENT_AND_SMALL_BUSINESS(46), ELECTIONS(48), LOCAL_GOVERNMENT(49),
    MENTAL_HEALTH_AND_DEVELOPMENTAL_DISABILITIES(50), ENERGY_AND_TELECOMMUNICATIONS(56),
    RACING__GAMING_AND_WAGERING(57), VETERANS__HOMELAND_SECURITY_AND_MILITARY_AFFAIRS(60),
    CULTURAL_AFFAIRS__TOURISM__PARKS_AND_RECREATION(62), INFRASTRUCTURE_AND_CAPITAL_INVESTMENT(65),
    NEW_YORK_CITY_EDUCATION_SUBCOMMITTEE(85);

    private final static Map<Integer, SenateCommitteeCode> NUM_TO_COMMITTEE = new HashMap<>();
    static {
        for (var com : SenateCommitteeCode.values())
            NUM_TO_COMMITTEE.put(com.committeeNumber, com);
    }
    private final String committee;
    private final int committeeNumber;

    SenateCommitteeCode(int comNum) {
        this.committee = this.name().toLowerCase().replaceAll("__", ", ").replaceAll("_", " ");
        this.committeeNumber = comNum;
    }

    public String getCommittee() {
        return this.committee;
    }

    public int getCommitteeNumber() {
        return this.committeeNumber;
    }

    public static SenateCommitteeCode findSenateCommittee(int committeeNumber) {
        return NUM_TO_COMMITTEE.get(committeeNumber);
    }
}
