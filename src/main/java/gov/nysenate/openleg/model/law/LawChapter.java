package gov.nysenate.openleg.model.law;

import static gov.nysenate.openleg.model.law.LawType.*;

/**
 * Current enumeration of all law chapters. It is possible that new laws chapters may come
 * in as part of an update so this listing should be maintained accordingly.
 */
public enum LawChapter
{
    /** --- Consolidated Laws --- */

    ABP("Abandoned Property", CONSOLIDATED),
    AGM("Agriculture & Markets", CONSOLIDATED),
    ABC("Alcoholic Beverage Control", CONSOLIDATED),
    ACG("Alternative County Government", CONSOLIDATED),
    ACA("Arts and Cultural Affairs", CONSOLIDATED),
    BNK("Banking", CONSOLIDATED),
    BVO("Benevolent Orders", CONSOLIDATED),
    BSC("Business Corporation", CONSOLIDATED),
    CAL("Canal", CONSOLIDATED),
    CVP("Civil Practice Law & Rules", CONSOLIDATED),
    CVR("Civil Rights", CONSOLIDATED),
    CVS("Civil Service", CONSOLIDATED),
    CCO("Cooperative Corporations", CONSOLIDATED),
    COR("Correction", CONSOLIDATED),
    CNT("County", CONSOLIDATED),
    CPL("Criminal Procedure", CONSOLIDATED),
    DCD("Debtor & Creditor", CONSOLIDATED),
    DOM("Domestic Relations", CONSOLIDATED),
    COM("Economic Development Law", CONSOLIDATED),
    EDN("Education", CONSOLIDATED),
    ELD("Elder", CONSOLIDATED),
    ELN("Election", CONSOLIDATED),
    EDP("Eminent Domain Procedure", CONSOLIDATED),
    EML("Employers' Liability", CONSOLIDATED),
    ENG("Energy", CONSOLIDATED),
    ENV("Environmental Conservation", CONSOLIDATED),
    EPT("Estates, Powers & Trusts", CONSOLIDATED),
    EXC("Executive", CONSOLIDATED),
    FIS("Financial Services Law", CONSOLIDATED),
    GAS("General Associations", CONSOLIDATED),
    GBS("General Business", CONSOLIDATED),
    GCT("General City", CONSOLIDATED),
    GCN("General Construction", CONSOLIDATED),
    GMU("General Municipal", CONSOLIDATED),
    GOB("General Obligations", CONSOLIDATED),
    HAY("Highway", CONSOLIDATED),
    IND("Indian", CONSOLIDATED),
    ISC("Insurance", CONSOLIDATED),
    JUD("Judiciary", CONSOLIDATED),
    LAB("Labor", CONSOLIDATED),
    LEG("Legislative", CONSOLIDATED),
    LIE("Lien", CONSOLIDATED),
    LLC("Limited Liability Company Law", CONSOLIDATED),
    LFN("Local Finance", CONSOLIDATED),
    MHY("Mental Hygiene", CONSOLIDATED),
    MIL("Military", CONSOLIDATED),
    MDW("Multiple Dwelling", CONSOLIDATED),
    MRE("Multiple Residence", CONSOLIDATED),
    MHA("Municipal Housing Authorities", CONSOLIDATED),
    MHR("Municipal Home Rule", CONSOLIDATED),
    NAV("Navigation", CONSOLIDATED),
    PPD("New York State Printing and Public Documents", CONSOLIDATED),
    NPC("Not-For-Profit Corporation", CONSOLIDATED),
    PAR("Parks, recreation and historic preservation", CONSOLIDATED),
    PTR("Partnership", CONSOLIDATED),
    PEN("Penal", CONSOLIDATED),
    PEP("Personal Property", CONSOLIDATED),
    PVH("Private Housing Finance", CONSOLIDATED),
    PBA("Public Authorities", CONSOLIDATED),
    PBB("Public Buildings", CONSOLIDATED),
    PBH("Public Health", CONSOLIDATED),
    PBG("Public Housing", CONSOLIDATED),
    PBL("Public Lands", CONSOLIDATED),
    PBO("Public Officers", CONSOLIDATED),
    PBS("Public Service", CONSOLIDATED),
    PML("Racing, Pari-Mutuel Wagering and Breeding Law", CONSOLIDATED),
    RRD("Railroad", CONSOLIDATED),
    RAT("Rapid Transit", CONSOLIDATED),
    RPP("Real Property", CONSOLIDATED),
    RPA("Real Property Actions & Proceedings", CONSOLIDATED),
    RPT("Real Property Tax", CONSOLIDATED),
    RCO("Religious Corporations", CONSOLIDATED),
    RSS("Retirement & Social Security", CONSOLIDATED),
    REL("Rural Electric Cooperative", CONSOLIDATED),
    SCC("Second Class Cities", CONSOLIDATED),
    SOS("Social Services", CONSOLIDATED),
    SWC("Soil & Water Conservation Districts", CONSOLIDATED),
    STL("State", CONSOLIDATED),
    SAP("State Administrative Procedure Act", CONSOLIDATED),
    STF("State Finance", CONSOLIDATED),
    STT("State Technology", CONSOLIDATED),
    SLG("Statute of Local Governments", CONSOLIDATED),
    TAX("Tax", CONSOLIDATED),
    TWN("Town", CONSOLIDATED),
    TRA("Transportation", CONSOLIDATED),
    TCP("Transportation Corporations", CONSOLIDATED),
    UCC("Uniform Commercial Code", CONSOLIDATED),
    VAT("Vehicle & Traffic", CONSOLIDATED),
    VIL("Village", CONSOLIDATED),
    VAW("Volunteer Ambulance Workers' Benefit", CONSOLIDATED),
    VOL("Volunteer Firefighters' Benefit", CONSOLIDATED),
    WKC("Workers' Compensation", CONSOLIDATED),

    /** --- Unconsolidated Laws --- */

    BSW("Boxing, Sparring and Wrestling Ch. 912/20", UNCONSOLIDATED),
    BAT("Bridges and Tunnels New York/New Jersey 47/31", UNCONSOLIDATED),
    CCT("Cigarettes, Cigars, Tobacco 235/52", UNCONSOLIDATED),
    TRY("City of Troy Issuance of Serial Bonds", UNCONSOLIDATED),
    DEA("Defense Emergency Act 1951 784/51", UNCONSOLIDATED),
    DPN("Development of Port of New York 43/22", UNCONSOLIDATED),
    ETP("Emergency Tenant Protection Act 576/74", UNCONSOLIDATED),
    EHC("Expanded Health Care Coverage Act 703/88", UNCONSOLIDATED),
    FEA("NYS Financial Emergency Act for the city of NY 868/75", UNCONSOLIDATED),
    NYP("NYS Project Finance Agency Act7/75", UNCONSOLIDATED),
    YFA("Yonkers financial emergency act 103/84", UNCONSOLIDATED),
    YTS("Yonkers income tax surcharge", UNCONSOLIDATED),
    FDC("Facilities Development Corporation Act 359/68", UNCONSOLIDATED),
    GCM("General City Model 772/66", UNCONSOLIDATED),
    LEH("Local Emergency Housing Rent Control Act 21/62", UNCONSOLIDATED),
    ERL("Emergency Housing Rent Control Law 274/46 337/61", UNCONSOLIDATED),
    LSA("Lost and Strayed Animals 115/1894", UNCONSOLIDATED),
    MCF("Medical Care Facilities Finance Agency 392/73", UNCONSOLIDATED),
    NYW("N. Y. wine/grape 80/85", UNCONSOLIDATED),
    HHC("New York City health and hospitals corporation act 1016/69", UNCONSOLIDATED),
    PCM("Police Certain Municipalities 360/11", UNCONSOLIDATED),
    PNY("Port of New York Authority 154/21", UNCONSOLIDATED),
    POA("Port of Albany 192/25", UNCONSOLIDATED),
    PAB("Private Activity Bond 47/90", UNCONSOLIDATED),
    RLA("Regulation of Lobbying Act 1040/81", UNCONSOLIDATED),
    SNH("Special Needs Housing Act 261/88", UNCONSOLIDATED),
    SCT("Suffolk County Tax Act", UNCONSOLIDATED),
    TSF("Tobacco Settlement Financing Corporation Act", UNCONSOLIDATED),
    UDG("Urban development guarantee fund of New York 175/68", UNCONSOLIDATED),
    UDA("Urban Development Corporation Act 174/68", UNCONSOLIDATED),
    UDR("Urban development research corporation act 173/68", UNCONSOLIDATED),
    NNY("New, New York Bond Act 649/92", UNCONSOLIDATED),

    /** --- Court Acts --- */

    CTC("Court of Claims", COURT_ACTS),
    FCT("Family Court", COURT_ACTS),
    CCA("New York City Civil Court", COURT_ACTS),
    CRC("New York City Criminal Court", COURT_ACTS),
    SCP("Surrogate's Court Procedure", COURT_ACTS),
    UCT("Uniform City Court", COURT_ACTS),
    UDC("Uniform District Court", COURT_ACTS),
    UJC("Uniform Justice Court", COURT_ACTS),

    /** --- Rules --- */

    CMA("Assembly Rules", RULES),
    CMS("Senate Rules", RULES),

    /** --- Misc --- */

    CNS("Constitution", MISC),
    ADC("New York City Administrative Code", MISC),
    NYC("New York City Charter", MISC);

    /** --- Fields --- */

    private String name;
    private LawType type;

    /** --- Constructor --- */

    LawChapter(String name, LawType type) {
        this.name = name;
        this.type = type;
    }

    /** --- Basic Getters --- */

    public String getName() {
        return name;
    }

    public LawType getType() {
        return type;
    }
}
