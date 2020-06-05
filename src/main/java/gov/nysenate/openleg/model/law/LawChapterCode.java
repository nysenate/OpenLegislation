package gov.nysenate.openleg.model.law;

import com.google.common.collect.Sets;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.model.law.LawType.*;

/**
 * Current enumeration of all law chapters.
 */
public enum LawChapterCode
{
    /** --- Consolidated Laws --- */

    ABP("Abandoned Property", CONSOLIDATED),
    AGM("Agriculture & Markets", CONSOLIDATED),
    ABC("Alcoholic Beverage Control", CONSOLIDATED),
    ACG("Alternative County Government", CONSOLIDATED),
    ACA("Arts and Cultural Affairs", CONSOLIDATED),
    BNK("Banking",  CONSOLIDATED),
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
    NPC("Not-for-Profit Corporation", CONSOLIDATED),
    PAR("Parks, Recreation and Historic Preservation", CONSOLIDATED),
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
    NYP("NYS Project Finance Agency Act 7/75", UNCONSOLIDATED),
    YFA("Yonkers Financial Emergency Act 103/84", UNCONSOLIDATED),
    YTS("Yonkers Income Tax Surcharge", UNCONSOLIDATED),
    FDC("Facilities Development Corporation Act 359/68", UNCONSOLIDATED),
    GCM("General City Model 772/66", UNCONSOLIDATED),
    LEH("Local Emergency Housing Rent Control Act 21/62", UNCONSOLIDATED),
    ERL("Emergency Housing Rent Control Law 274/46 337/61", UNCONSOLIDATED),
    LSA("Lost and Strayed Animals 115/1894", UNCONSOLIDATED),
    MCF("Medical Care Facilities Finance Agency 392/73", UNCONSOLIDATED),
    NYW("N. Y. Wine/Grape 80/85", UNCONSOLIDATED),
    HHC("New York City Health and Hospitals Corporation Act 1016/69", UNCONSOLIDATED),
    PCM("Police Certain Municipalities 360/11", UNCONSOLIDATED),
    PNY("Port of New York Authority 154/21", UNCONSOLIDATED),
    POA("Port of Albany 192/25", UNCONSOLIDATED),
    PAB("Private Activity Bond 47/90", UNCONSOLIDATED),
    RLA("Regulation of Lobbying Act 1040/81", UNCONSOLIDATED),
    SNH("Special Needs Housing Act 261/88", UNCONSOLIDATED),
    SCT("Suffolk County Tax Act", UNCONSOLIDATED),
    TSF("Tobacco Settlement Financing Corporation Act", UNCONSOLIDATED),
    UDG("Urban Development Guarantee Fund of New York 175/68", UNCONSOLIDATED),
    UDA("Urban Development Corporation Act 174/68", UNCONSOLIDATED),
    UDR("Urban Development Research Corporation Act 173/68", UNCONSOLIDATED),
    NNY("New, New York Bond Act 649/92", UNCONSOLIDATED),

    /** --- Court Acts --- */

    CTC("Court of Claims Act", COURT_ACTS),
    FCT("Family Court Act", COURT_ACTS),
    CCA("New York City Civil Court Act", COURT_ACTS),
    CRC("New York City Criminal Court Act", COURT_ACTS),
    SCP("Surrogate's Court Procedure Act", COURT_ACTS),
    UCT("Uniform City Court Act", COURT_ACTS),
    UDC("Uniform District Court Act", COURT_ACTS),
    UJC("Uniform Justice Court Act", COURT_ACTS),

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

    public static final Pattern NUMBERED_CHAPTER = Pattern.compile("Chap (\\d+) of (\\d+)");
    private static final Set<LawChapterCode> nonNumericalVolumes= Sets.newHashSet(LawChapterCode.ACA,
            LawChapterCode.CPL, LawChapterCode.CVS, LawChapterCode.PAR, LawChapterCode.MHY, LawChapterCode.PEN);
    private static Map<String, LawChapterCode> uniqueCitations = new HashMap<>();
    static {
        uniqueCitations.put("Rec & Pks", PAR);
        uniqueCitations.put("El", ELN);
        uniqueCitations.put("NYS Med Care Fac Fin Ag Act", MCF);
        uniqueCitations.put("Fin", STF);
    }

    public boolean hasNumericalTitles() {
        return !nonNumericalVolumes.contains(this);
    }

    /**
     * Attempts to match a citation to a LawChapterCode.
     * @param citation to process.
     * @return an Optional of the matching code, or an empty one if a code was not found.
     */
    public static Optional<LawChapterCode> lookupCitation(String citation) {
        if (citation == null)
            throw new IllegalArgumentException("Null citation supplied.");
        if (citation.trim().length() < 2 || citation.startsWith("NYC LL"))
            return Optional.empty();
        // Many citations end in " L", which doesn't belong.
        citation = citation.replaceFirst("[ ]?L(aw)?[.]?$", "").trim().replaceAll("(\\s{2,})", " ");
        if (uniqueCitations.containsKey(citation))
            return Optional.of(uniqueCitations.get(citation));
        citation = citation.replaceAll(" (and|&) ", " ");
        Pattern lawChapterNamePattern = makePatternFromCitation(citation);

        LawChapterCode ret = null;
        for (LawChapterCode code : values()) {
            String name = code.name;
            if (lawChapterNamePattern.matcher(name).matches()) {
                ret = chooseCode(ret, code, citation);
            }
        }
        // If no result is found, try dropping a letter. But if it starts with "Chap", it's an
        // unconsolidated law we don't have. If it starts or ends with digits, it's a malformed citation.
        if (ret == null && !citation.matches("^(Chap|\\d+).*"))
            return lookupCitation(citation.replaceFirst(".$", ""));
        return Optional.ofNullable(ret);
    }

    /**
     * Creates a Pattern to match a chapter name to.
     * @param citation to create Pattern from.
     * @return The proper Pattern.
     */
    private static Pattern makePatternFromCitation(String citation) {
        Matcher chapterNumberMatcher = NUMBERED_CHAPTER.matcher(citation);
        if (chapterNumberMatcher.matches()) {
            String year = chapterNumberMatcher.group(2);
            // Laws in the 1900's are simply referred to by their last 2 digits.
            if (year.startsWith("19"))
                year = year.substring(2);
            return Pattern.compile(".*" + chapterNumberMatcher.group(1) + "/" + year + ".*");
        }
        // The letters in a citation are usually in the same order in the law code name.
        StringBuilder toMatch = new StringBuilder();
        char[] ar = citation.toCharArray();
        for (int i = 0; i < ar.length; i++) {
            toMatch.append("[^ ]*");
            // If this and the last character are uppercase, then we are dealing with an abbreviation.
            if (Character.isUpperCase(ar[i]) && i != 0 && Character.isUpperCase(ar[i - 1]))
                toMatch.append(".*?");
            if (ar[i] != ' ')
                toMatch.append(ar[i]);
            else
                toMatch.append("(,| |&|\\.|and|of)+");
        }
        return Pattern.compile(".*?" + toMatch.append(".*").toString());
    }

    /**
     * Chooses which LawChapterCode is more likely to correspond to the given citation.
     * @param curr Current citation matched.
     * @param next Next citation match.
     * @param citation To compare to.
     * @return The more accurate LawChapterCode.
     */
    private static LawChapterCode chooseCode(LawChapterCode curr, LawChapterCode next, String citation) {
        if (curr == null)
            return next;
        if (citation.isEmpty())
            return curr;
        String currStr = curr.name;
        String nextStr = next.name;
        // If the citation is an abbreviation, abbreviates the law chapter names as well.
        if (citation.toUpperCase().equals(citation)) {
            currStr = currStr.replaceAll("[a-z0-9/ ]", "");
            nextStr = nextStr.replaceAll("[a-z0-9/ ]", "");
        }
        String[] citationSplit = citation.split(" ");
        for (String word : citationSplit) {
            if (currStr.startsWith(word) && nextStr.startsWith(word)) {
                if (nextStr.replaceFirst(word, "").length() < currStr.replaceFirst(word, "").length())
                    return next;
                return curr;
            }
            if (currStr.startsWith(word))
                return curr;
            if (nextStr.startsWith(word))
                return next;
        }
        return chooseCode(curr, next, citation.substring(0, citation.length()-1).trim());
    }

    /** --- Constructor --- */

    LawChapterCode(String name, LawType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * For use in LawTitleParser.
     * @param lawId to check
     * @return if the law chapter is unconsolidated.
     */
    public static boolean isUnconsolidated(String lawId) {
        return LawChapterCode.valueOf(lawId).type == UNCONSOLIDATED;
    }

    /** --- Basic Getters --- */

    public String getName() {
        return name;
    }

    public LawType getType() {
        return type;
    }
}
