package gov.nysenate.openleg.model.bill;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.base.SessionYear;
import gov.nysenate.openleg.model.base.Version;
import gov.nysenate.openleg.model.entity.Chamber;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An immutable representation of the fields that are used to identify a particular bill.
 * This is mostly useful when other classes need to reference a particular bill but do not
 * necessarily need to store a complete object reference of the Bill or BillAmendment.
 */
public class BillId implements Serializable, Comparable<BillId>
{
    private static final long serialVersionUID = 6494036869654732240L;

    public static String printNumberRegex = "([ASLREJKBC])([0-9]{1,5})([A-Z]?)";

    public static Pattern printNumberPattern = Pattern.compile(printNumberRegex);
    public static Pattern billIdPattern = Pattern.compile("(?<printNo>" + printNumberRegex + ")-?(?<year>[0-9]{4})");

    /** The default amendment version letter. */
    public static final Version DEFAULT_VERSION = Version.DEFAULT;

    /** A number assigned to a bill when it's introduced in the Legislature. Each printNo begins with a
     *  letter (A for Assembly, S for Senate) followed by 1 to 5 digits. This printNo is valid only for the
     *  2 year session period, after which it will be recycled. */
    protected String basePrintNo;

    /** The session year of the bill. */
    protected SessionYear session;

    /** The amendment version of the bill. */
    protected Version version = DEFAULT_VERSION;

    /* --- Constructors --- */

    public BillId(String printNo, int session) {
        this(printNo, new SessionYear(session));
    }

    /**
     * Use this constructor when the version is not known or when the version may
     * be attached to the print no and needs to be parsed out.
     *
     * @param printNo String - e.g. 'S1234' or 'S1234A'
     * @param session int - e.g. 2013
     */
    public BillId(String printNo, SessionYear session) {
        printNo = normalizePrintNo(printNo);
        // Strip out the version from the print no if it exists.
        if (printNo.matches(".*[A-Z]$")) {
            this.version = Version.of(printNo.substring(printNo.length() - 1));
            printNo = printNo.substring(0, printNo.length() - 1);
        }
        checkBasePrintHasNoVersion(printNo);
        this.basePrintNo = printNo;
        checkSessionYear(session);
        this.session = session;
    }

    /**
     * Performs strict checks on the basePrintNo when constructing BillId. If you have a bill id
     * as S02134A-2013, you should pass it in as ("S02134", 2013, "A"). If you do not have the
     * parsed representation of the printNo, use the {@link BillId(String, int)} constructor instead.
     *
     * @param basePrintNo String - e.g. S1234 -> GOOD,  S1234A -> INVALID
     * @param session int
     * @param version String
     */
    public BillId(String basePrintNo, int session, String version) {
        this(basePrintNo, new SessionYear(session), Version.of(version));
    }

    /**
     * Performs strict checks on the basePrintNo when constructing BillId. If you have a bill id
     * as S02134A-2013, you should pass it in as ("S02134", 2013, "A"). If you do not have the
     * parsed representation of the printNo, use the {@link BillId(String, int)} constructor instead.
     *
     * @param baseBillId
     * @param version
     */
    public BillId(BaseBillId baseBillId, Version version){
        this(baseBillId.getBasePrintNo(),baseBillId.getSession(), version );
    }

    /**
     * Performs strict checks on the basePrintNo when constructing BillId. If you have a bill id
     * as S02134A-2013, you should pass it in as ("S02134", 2013, "A"). If you do not have the
     * parsed representation of the printNo, use the {@link BillId(String, int)} constructor instead.
     *
     * @param basePrintNo String - e.g. S1234 -> GOOD,  S1234A -> INVALID
     * @param session int
     * @param version String
     */
    public BillId(String basePrintNo, SessionYear session, Version version) {
        basePrintNo = normalizePrintNo(basePrintNo);
        checkBasePrintHasNoVersion(basePrintNo);
        this.basePrintNo = basePrintNo;
        checkSessionYear(session);
        this.session = session;
        if (version == null) {
            version = DEFAULT_VERSION;
        }
        this.version = version;
    }


    /** --- Methods --- */

    /**
     * Returns a BaseBillId instance from the given bill id which ensures that no amendment
     * version info will be stored.
     */
    @JsonIgnore
    public static BaseBillId getBaseId(BillId billId) {
        return new BaseBillId(billId.basePrintNo, billId.session);
    }

    /**
     * Returns the full print no including amendment version, e.g. S1234A
     */
    public String getPrintNo() {
        return this.basePrintNo + ((this.version != null) ? this.version : "");
    }

    /**
     * Retrieves the type of bill based on the first letter designator.
     */
    @JsonIgnore
    public BillType getBillType() {
        return BillType.valueOf(this.basePrintNo.substring(0, 1));
    }

    /**
     * Indicates the chamber of the bill based on the letter designator.
     */
    @JsonIgnore
    public Chamber getChamber() {
        return getBillType().getChamber();
    }

    /**
     * Gets the number portion of the print number
     */
    @JsonIgnore
    public int getNumber() {
        return Integer.parseInt(basePrintNo.replaceAll("[^\\d]", ""));
    }
    /**
     * Indicates if this bill is currently set to the base version.
     *
     * @param version The bill version
     * @return Returns true if the version will be represented as a base bill
     */
    public static boolean isBaseVersion(Version version) {
        return version == null || version.equals(BillId.DEFAULT_VERSION);
    }

    /**
     * Creates a unique id for the bill with padding to resemble LBDC's representation.
     *
     * @return - The billId padded to 5 digits with zeros.
     */
    @JsonIgnore
    public String getPaddedBillIdString() {
        return this.getPaddedPrintNumber() + "-" + this.getSession();
    }

    /**
     * Returns the print number padded with 5 zeros, e.g S01234. This is how LBDC represents
     * print numbers in their SOBI files.
     *
     * @return - The print number padded to 5 digits with zeros.
     */
    @JsonIgnore
    public String getPaddedPrintNumber() {
        Matcher billIdMatcher = printNumberPattern.matcher(this.getPrintNo());
        if (billIdMatcher.find()) {
            return String.format("%s%05d%s", billIdMatcher.group(1), Integer.parseInt(billIdMatcher.group(2)),
                                             billIdMatcher.group(3));
        }
        return "";
    }

    /** --- Overrides --- */

    /**
     * Given {basePrint:'S1234', version:'A', session:2013}, Output: 'S1234A-2013'
     * Note: Does not pad the string to a fixed length, use #getPaddedBillIdString() if padding is desired
     *
     * @return String representation of BillId.
     */
    @Override
    public String toString() {
        return basePrintNo + ((version != null) ? version : "") + "-" + session;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!equalsBase(o)) return false;
        BillId oBillId = (BillId) o;
        return Objects.equals(this.version, oBillId.version);
    }

    @Override
    public int hashCode() {
        int result = hashCodeBase();
        return (31 * result + Objects.hash(this.version));
    }

    /**
     * An alternate equals comparison that ignores version so that two BillIds are equivalent if
     * their base BillIds match.
     */
    public boolean equalsBase(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        BillId oBillId = (BillId) o;
        return Objects.equals(this.session, oBillId.session) &&
               Objects.equals(this.basePrintNo, oBillId.basePrintNo);
    }

    /**
     * Get hashcode without factoring in the version. Should use this when implementing hashcode method
     * for classes that contain a BillId where the version of the bill is not relevant.
     */
    public int hashCodeBase() {
        return Objects.hash(this.basePrintNo, this.session);
    }

    @Override
    public int compareTo(BillId o) {
        return ComparisonChain.start()
            .compare(this.session, o.session)
            .compare(this.basePrintNo, o.basePrintNo)
            .compare(this.version, o.version)
            .result();
    }

    /** --- Internal --- */

    /**
     * Converts the printNo into a normalized form (no whitespace, all caps, all alphanumeric) and performs
     * basic checks to ensure that the printNo starts with the correct BillType designator. The normalized
     * printNo will be returned or an IllegalArgumentException thrown if printNo is invalid.
     *
     * @param printNo String - Input printNo
     * @return String - Normalized printNo
     */
    private String normalizePrintNo(String printNo) {
        // Basic Null Check
        if (printNo == null || printNo.trim().isEmpty()) {
            throw new IllegalArgumentException("PrintNo when constructing BillId cannot be null/empty.");
        }
        // Remove all non-alphanumeric characters from the printNo.
        printNo = printNo.trim().toUpperCase().replaceAll("[^0-9A-Z]", "");
        // Check that printNo starts with a valid bill type designator
        try {
            BillType.valueOf(String.valueOf(printNo.charAt(0)));
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("PrintNo (" + printNo + ") must begin with a valid letter designator.");
        }
        // Trim leading 0's after the first character
        printNo = printNo.substring(0, 1) + StringUtils.trimLeadingCharacter(printNo.substring(1), '0');
        return printNo;
    }

    /**
     * Check that base print no has no version character at the end. Throw an IllegalArgumentException if
     * there is.
     *
     * @param basePrintNo String
     * @throws java.lang.IllegalArgumentException - If basePrintNo has a character appended at the end
     */
    private void checkBasePrintHasNoVersion(String basePrintNo) {
        if (basePrintNo.matches(".*[A-Z]$")) {
            throw new IllegalArgumentException("BasePrintNo cannot have a version appended to it. (" + basePrintNo + ")");
        }
    }

    /**
     * Basic checks on the supplied SessionYear.
     *
     * @param session SessionYear
     */
    private void checkSessionYear(SessionYear session) {
        if (session == null) {
            throw new IllegalArgumentException("Supplied SessionYear cannot be null");
        }
    }


    /** --- Basic Getters/Setters --- */

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public SessionYear getSession() {
        return session;
    }

    public Version getVersion() {
        return version;
    }
}