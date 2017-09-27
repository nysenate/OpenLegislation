package gov.nysenate.openleg.model.bill;

public enum AssemblyCommitteeCodes {

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

    AssemblyCommitteeCodes(String committee) {
        this.committee = committee;
    }

    public String getCommittee() {return this.committee;}
}
