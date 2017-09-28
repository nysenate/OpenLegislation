package gov.nysenate.openleg.model.bill;

public enum AssemblyCommitteeCode {

    BANKS("banks"), //3
    CITIES("cities"), //4
    AGING("aging"), //5
    CODES("codes"), //7
    ALCOHOLISM_AND_DRUG_ABUSE("alcoholism and drug abuse"), //9
    CONSUMER_AFFAIRS_AND_PROTECTION("consumer affairs and protection"), //10
    EDUCATION("education"), //11
    ELECTION_LAW("election law"), //12
    MENTAL_HEALTH("mental health"), //14
    HEALTH("health"), //15
    HIGHER_EDUCATION("higher education"), //16
    INSURANCE("insurance"), //18
    JUDICIARY("judicairy"), //19
    REAL_PROPERTY_TAXATION("real property taxation"), //20
    ENERGY("energy"), //21
    OVERSIGHT_ANALYSIS_AND_INVESTIGATION("oversight, analysis, and investigation"), //25
    CHILDREN_AND_FAMILIES("children and families"), //26
    RULES("rules"), //27
    SOCIAL_SERVICES("social services"), //28
    AGRICULTURE("agriculture"), //32
    ECONOMIC_DEVELOPMENT("economic development"), //33
    CORRECTION("correction"), //34
    CORPORATIONS_AUTHORITIES_AND_COMMISSIONS("corporations, authorities, and commissions"), //35
    ETHICS_AND_GUIDANCE("ethics and guidance"), //36
    GOVERNMENTAL_EMPLOYEES("governemtal employees"), //37
    GOVERNMENTAL_OPERATIONS("governmental operations"), //38
    HOUSING("housing"), //39
    LABOR("labor"), //40
    LOCAL_GOVERNMENTS("local governments"), //41
    TRANSPORTATION("transportation"), //42
    WAYS_AND_MEANS("ways and means"), //43
    VETERANS_AFFAIRS("veterans' affairs"), //47
    ENVIRONMENTAL_CONSERVATION("environmental convservation"), //51
    RACING_AND_WAGERING("racing and wagering"), //53
    SMALL_BUSINESS("small business"), //54
    LIBRARIES_AND_EDUCATION_TECHNOLOGY("libraries and education technology"), //59
    TOURISM_PARKS_ARTS_AND_SPORTS_DEVELOPMENT("tourism, parks, arts, and sports development") //64
    ;

    protected String committee;

    AssemblyCommitteeCode(String committee) {
        this.committee = committee;
    }

    public String getCommittee() {return this.committee;}

    public AssemblyCommitteeCode findSenateCommittee(int committeeNumber) {
        AssemblyCommitteeCode requestedCommittee;
        switch(committeeNumber) {
            case 3: requestedCommittee = BANKS;
                break;
            case 4: requestedCommittee = CITIES;
                break;
            case 5: requestedCommittee = AGING;
                break;
            case 7: requestedCommittee = CODES;
                break;
            case 9: requestedCommittee = ALCOHOLISM_AND_DRUG_ABUSE;
                break;
            case 11: requestedCommittee = EDUCATION;
                break;
            case 12: requestedCommittee = ELECTION_LAW;
                break;
            case 14: requestedCommittee = MENTAL_HEALTH;
                break;
            case 15: requestedCommittee = HEALTH;
                break;
            case 16: requestedCommittee = HIGHER_EDUCATION;
                break;
            case 18: requestedCommittee = INSURANCE;
                break;
            case 19: requestedCommittee = JUDICIARY;
                break;
            case 20: requestedCommittee = REAL_PROPERTY_TAXATION;
                break;
            case 21: requestedCommittee = ENERGY;
                break;
            case 25: requestedCommittee = OVERSIGHT_ANALYSIS_AND_INVESTIGATION;
                break;
            case 26: requestedCommittee = CHILDREN_AND_FAMILIES;
                break;
            case 27: requestedCommittee = RULES;
                break;
            case 28: requestedCommittee = SOCIAL_SERVICES;
                break;
            case 32: requestedCommittee = AGRICULTURE;
                break;
            case 33: requestedCommittee = ECONOMIC_DEVELOPMENT;
                break;
            case 35: requestedCommittee = CORPORATIONS_AUTHORITIES_AND_COMMISSIONS;
                break;
            case 36: requestedCommittee = ETHICS_AND_GUIDANCE;
                break;
            case 37: requestedCommittee = GOVERNMENTAL_EMPLOYEES;
                break;
            case 38: requestedCommittee = GOVERNMENTAL_OPERATIONS;
                break;
            case 39: requestedCommittee = HOUSING;
                break;
            case 40: requestedCommittee = LABOR;
                break;
            case 41: requestedCommittee = LOCAL_GOVERNMENTS;
                break;
            case 42: requestedCommittee = TRANSPORTATION;
                break;
            case 43: requestedCommittee = WAYS_AND_MEANS;
                break;
            case 47: requestedCommittee = VETERANS_AFFAIRS;
                break;
            case 51: requestedCommittee = ENVIRONMENTAL_CONSERVATION;
                break;
            case 53: requestedCommittee = RACING_AND_WAGERING;
                break;
            case 54: requestedCommittee = SMALL_BUSINESS;
                break;
            case 59: requestedCommittee = LIBRARIES_AND_EDUCATION_TECHNOLOGY;
                break;
            case 64: requestedCommittee = TOURISM_PARKS_ARTS_AND_SPORTS_DEVELOPMENT;
                break;
            default: requestedCommittee = null;
        }
        return requestedCommittee;
    }
}
