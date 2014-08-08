package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.entity.Chamber;
import gov.nysenate.openleg.util.DateUtils;

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

    public static Pattern printNumberPattern = Pattern.compile("([ASLREJKBC])([0-9]{1,5})([A-Z]?)");

    /** The default amendment version letter. */
    public static final String BASE_VERSION = "";

    /** A number assigned to a bill when it's introduced in the Legislature. Each printNo begins with a
     *  letter (A for Assembly, S for Senate) followed by 1 to 5 digits. This printNo is valid only for the
     *  2 year session period, after which it will be recycled. */
    protected String basePrintNo;

    /** The session year of the bill. */
    protected int session;

    /** The amendment version of the bill. Its either a blank string or a single character from A-Z. */
    protected String version = BASE_VERSION;

    /* --- Constructors --- */

    /**
     * Use this constructor when the version is not known/applicable.
     *
     * @param printNo String
     * @param session int
     */
    public BillId(String printNo, int session) {
        this(printNo, session, null);
    }

    /**
     * Performs strict checks on the basePrintNo when constructing BillId. If you have a bill id
     * as S02134A-2013, you should pass it in as ("S02134", 2013, "A"). However you can also
     * pass it in as ("S02134A", 2013, null|"") and the constructor will parse out the version.
     *
     * @param basePrintNo String
     * @param session int
     * @param version String
     */
    public BillId(String basePrintNo, int session, String version) {
        if (basePrintNo == null) {
            throw new IllegalArgumentException("basePrintNo when constructing BillId cannot be null!");
        }
        // Remove all non-alphanumeric characters from the print no.
        basePrintNo = basePrintNo.trim().toUpperCase().replaceAll("[^0-9A-Z]", "");
        if (!basePrintNo.matches("[A-Z].*")) {
            throw new IllegalArgumentException("basePrintNo must begin with the letter designator!");
        }
        // Strip out the version from the print no if it exists.
        if (basePrintNo.matches(".*[A-Z]$")) {
            String strippedPrintNo = basePrintNo.substring(0, basePrintNo.length() - 1);
            version = (version == null || version.isEmpty()) ? basePrintNo.substring(basePrintNo.length() - 1)
                                                             : version;
            basePrintNo = strippedPrintNo;
        }
        try {
            // Remove any leading zeros from the print no.
            this.basePrintNo = basePrintNo.substring(0, 1) + Integer.parseInt(basePrintNo.substring(1, basePrintNo.length()));
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException("basePrintNo must be numerical after the letter designator!");
        }
        this.version = (version != null) ? version.trim().toUpperCase() : "";
        this.session = DateUtils.resolveSession(session);
    }

    /** --- Methods --- */

    /**
     * Returns a BaseBillId instance from the given bill id which ensures that no amendment
     * version info will be stored.
     */
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
    public BillType getBillType() {
        return BillType.valueOf(this.basePrintNo.substring(0, 1));
    }

    /**
     * Indicates the chamber of the bill based on the letter designator.
     */
    public Chamber getChamber() {
        return getBillType().getChamber();
    }

    /**
     * Indicates if this bill is currently set to the base version.
     *
     * @param version The bill version
     * @return Returns true if the version will be represented as a base bill
     */
    public static boolean isBaseVersion(String version) {
        return version == null || version.equals(BillId.BASE_VERSION);
    }

    /**
     * Creates a unique id for the bill with padding to resemble LBDC's representation.
     *
     * @return - The billId padded to 5 digits with zeros.
     */
    public String getPaddedBillIdString() {
        return this.getPaddedPrintNumber() + "-" + this.getSession();
    }

    /**
     * Returns the print number padded with 5 zeros, e.g S01234. This is how LBDC represents
     * print numbers in their SOBI files.
     *
     * @return - The print number padded to 5 digits with zeros.
     */
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
        if (o == null || getClass() != o.getClass()) return false;
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
        if (o == null || getClass() != o.getClass()) return false;
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

    /** --- Basic Getters/Setters --- */

    public String getBasePrintNo() {
        return basePrintNo;
    }

    public int getSession() {
        return session;
    }

    public String getVersion() {
        return version;
    }
}
