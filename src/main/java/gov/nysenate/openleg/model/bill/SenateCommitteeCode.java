package gov.nysenate.openleg.model.bill;

public enum SenateCommitteeCode {

    CONSUMER_PROTECTION("consumer protection"), //2
    BANKS("banks"), //3
    CITIES("cities"), //4
    AGING("aging"), //5
    CIVIL_SERVICE_AND_PENSIONS("civil service and pensions"), //6
    CODES("codes"), //7
    CHILDREN_AND_FAMILIES("children and families"), //8
    ALCOHOLISM_AND_DRUG_ABUSE("alcoholism and drug abuse"), //9
    EDUCATION("education"), //11
    FINANCE("finance"), //13
    HEALTH("health"), //15
    HIGHER_EDUCATION("higher education"), //16
    HOUSING_CONSTRUCTION_AND_COMMUNITY_DEVELOPMENT("housing, construction and community development"), //17
    INSURANCE("insurance"), //18
    JUDICIARY("judiciary"), //19
    CRIME_VICTIMS_CRIME_AND_CORRECTION("crime vitctims, crime and correction"), //24
    RULES("rules"), //27
    SOCIAL_SERVICES("social services"), //28
    INVESTIGATIONS_AND_GOVERNMENT_OPERATIONS("investigations and government operations"), //29
    AGRICULTURE("agriculture"), //32
    EHTICS("ethics"), //35
    CORPORATIONS_AUTHORITIES_AND_COMMISIONS("corporations, authorities and commissions"), //35
    LABOR("labor"), //40
    TRANSPORTATION("transportation"), //42
    ENVIRONMENTAL_CONSERVATION("environmental conservation"), //45
    COMMERCE_ECONOMIC_DEVELOPMENT_AND_SMALL_BUSINESS("commerce, economic developtment and small business"), //46
    ELECTIONS("elections"), //48
    LOCAL_GOVERNMENT("local government"), //49
    MENTAL_HEALTH_AND_DEVELOPMENTAL_DISABILITIES("mental health and developmental disabilities"), //50
    ENERGY_AND_TELECOMMUNICATIONS("energy and telecommunications"), //56
    RACING_GAMING_AND_WAGERING("racing, gaming and wagering"), //57
    VETERANS_HOMELAND_SECURITY_AND_MILITARY_AFFAIRS("veterans, homeland security and military affairs"), //60
    CULTURAL_AFFAIRS_TOURISM_PARKS_AND_RECREATION("cultural affairs, tourism, parks and recreation"), //62
    INFRASTRUCTURE_AND_CAPITAL_INVESTMENT("infrastructure and capital investment"), //65
    NEW_YORK_CITY_EDUCATION_SUBCOMMITTEE("new york city education subcommittee"), //85
    ;

    protected String committee;

    SenateCommitteeCode(String committee) {
        this.committee = committee;
    }

    public String getCommittee() {return this.committee;}

    public SenateCommitteeCode findSenateCommittee(int committeeNumber) {
        SenateCommitteeCode requestedCommittee;
        switch(committeeNumber) {
            case 2: requestedCommittee = CONSUMER_PROTECTION;
                break;
            case 3: requestedCommittee = BANKS;
                break;
            case 4: requestedCommittee = CITIES;
                break;
            case 5: requestedCommittee = AGING;
                break;
            case 6: requestedCommittee = CIVIL_SERVICE_AND_PENSIONS;
                break;
            case 7: requestedCommittee = CODES;
                break;
            case 8: requestedCommittee = CHILDREN_AND_FAMILIES;
                break;
            case 9: requestedCommittee = ALCOHOLISM_AND_DRUG_ABUSE;
                break;
            case 11: requestedCommittee = EDUCATION;
                break;
            case 13: requestedCommittee = FINANCE;
                break;
            case 15: requestedCommittee = HEALTH;
                break;
            case 16: requestedCommittee = HIGHER_EDUCATION;
                break;
            case 17: requestedCommittee = HOUSING_CONSTRUCTION_AND_COMMUNITY_DEVELOPMENT;
                break;
            case 18: requestedCommittee = INSURANCE;
                break;
            case 19: requestedCommittee = JUDICIARY;
                break;
            case 24: requestedCommittee = CRIME_VICTIMS_CRIME_AND_CORRECTION;
                break;
            case 27: requestedCommittee = RULES;
                break;
            case 28: requestedCommittee = SOCIAL_SERVICES;
                break;
            case 29: requestedCommittee = INVESTIGATIONS_AND_GOVERNMENT_OPERATIONS;
                break;
            case 32: requestedCommittee = AGRICULTURE;
                break;
            case 35: requestedCommittee = EHTICS;
                break;
            case 40: requestedCommittee = LABOR;
                break;
            case 42: requestedCommittee = TRANSPORTATION;
                break;
            case 45: requestedCommittee = ENVIRONMENTAL_CONSERVATION;
                break;
            case 46: requestedCommittee = COMMERCE_ECONOMIC_DEVELOPMENT_AND_SMALL_BUSINESS;
                break;
            case 48: requestedCommittee = ELECTIONS;
                break;
            case 49: requestedCommittee = LOCAL_GOVERNMENT;
                break;
            case 50: requestedCommittee = MENTAL_HEALTH_AND_DEVELOPMENTAL_DISABILITIES;
                break;
            case 56: requestedCommittee = ENERGY_AND_TELECOMMUNICATIONS;
                break;
            case 57: requestedCommittee = RACING_GAMING_AND_WAGERING;
                break;
            case 60: requestedCommittee = VETERANS_HOMELAND_SECURITY_AND_MILITARY_AFFAIRS;
                break;
            case 62: requestedCommittee = CULTURAL_AFFAIRS_TOURISM_PARKS_AND_RECREATION;
                break;
            case 65: requestedCommittee = INFRASTRUCTURE_AND_CAPITAL_INVESTMENT;
                break;
            case 85: requestedCommittee = NEW_YORK_CITY_EDUCATION_SUBCOMMITTEE;
                break;
            default: requestedCommittee = null;
        }
        return requestedCommittee;
    }
}
