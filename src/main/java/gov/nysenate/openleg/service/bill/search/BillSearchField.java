package gov.nysenate.openleg.service.bill.search;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all the possible bill data fields that can be targeted when performing an
 * advanced search.
 */
public enum BillSearchField
{
    PRINT_NO          (true,  "printno"),
    SESSION_YEAR      (false, "sessionyear"),
    SPONSOR           (false, "sponsor"),
    COSPONSORS        (true,  "cosponsor"),
    MULTISPONSORS     (true,  "multisponsor"),
    TITLE             (false, "title"),
    SUMMARY           (false, "summary"),
    MEMO              (false, "memo"),
    LAW_SECTION       (false, "lawsection"),
    LAW_CODE          (false, "lawcode"),
    PROGRAM_INFO      (false, "programinfo"),
    ACT_CLAUSE        (true,  "actclause"),
    FULLTEXT          (true,  "fulltext"),
    COMMITTEE_NAME    (true,  "committee"),
    ACTIONS           (false, "actions"),
    VOTE_INFO         (true,  "voteinfo");

    /** Indicate if this search field is attached to the amendment (true) or the base bill (false) */
    boolean versionSpecfic;

    /** The name of the parameter used when constructing query strings. */
    String paramName;

    static Map<String, BillSearchField> paramNameMap = new HashMap<>();
    static {
        Arrays.asList(BillSearchField.values()).stream().forEach(f -> paramNameMap.put(f.getParamName(), f));
    }

    BillSearchField(boolean versionSpecfic, String paramName) {
        this.versionSpecfic = versionSpecfic;
        this.paramName = paramName;
    }

    public static boolean isValidParam(String param) {
        return paramNameMap.containsKey(param.trim().toLowerCase());
    }

    public static BillSearchField valueOfParam(String param) {
        if (param == null) {
            throw new IllegalArgumentException("Supplied param name cannot be null!");
        }
        BillSearchField field = paramNameMap.get(param.trim().toLowerCase());
        if (field == null) {
            throw new IllegalArgumentException("Failed to map " + param + " to a supported search field.");
        }
        return field;
    }

    public boolean isVersionSpecfic() {
        return versionSpecfic;
    }

    public String getParamName() {
        return paramName;
    }
}
