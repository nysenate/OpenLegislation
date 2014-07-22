package gov.nysenate.openleg.model.bill;

import gov.nysenate.openleg.model.entity.Chamber;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.model.BaseLegislativeContent.resolveSessionYear;

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

    /** The base print number of the bill (no trailing character), e.g S1234 */
    private String basePrintNo;

    /** The session year of the bill. */
    private int session;

    /** The version of the bill. */
    private String version = BASE_VERSION;

    /* --- Constructors --- */

    /**
     * Use this constructor when the version is not known/applicable.
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
        this.session = resolveSessionYear(session);
    }

    /** --- Methods --- */

    public static BaseBillId getBaseId(BillId billId) {
        return new BaseBillId(billId.basePrintNo, billId.session);
    }

    public String getPrintNo() {
        return this.basePrintNo + ((this.version != null) ? this.version : "");
    }

    public BillType getBillType() {
        return BillType.valueOf(this.basePrintNo.substring(0, 1));
    }

    public Chamber getChamber() {
        return getBillType().getChamber();
    }

    /**
     * Indicates if this bill is currently set to the base version.
     * @param version The bill version
     * @return Returns true if the version will be represented as a base bill
     */
    public static boolean isBaseVersion(String version) {
        return version == null || version.equals(BillId.BASE_VERSION);
    }

    /**
     * Creates a unique id for the bill with padding to resemble LBDC's representation.
     * @return - The billId padded to 5 digits with zeros.
     */
    public String getPaddedBillIdString() {
        return this.getPaddedPrintNumber() + "-" + this.getSession();
    }

    /**
     * Returns the print number padded with 5 zeros, e.g S01234. This is how LBDC represents
     * print numbers in their SOBI files.
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
        BillId billId = (BillId) o;
        if (version != null ? !version.equals(billId.version) : billId.version != null) return false;
        return true;
    }

    /**
     * Not an override but is a useful equals comparison that ignores version so that two BillIds are
     * equivalent if their base BillIds match.
     * @param o
     * @return boolean
     */
    public boolean equalsBase(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillId billId = (BillId) o;
        if (session != billId.session) return false;
        if (!basePrintNo.equals(billId.basePrintNo)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = hashCodeBase();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }

    /**
     * Get hashcode without factoring in the version. Should use this when implementing hashcode method
     * for classes that contain a BillId where the version of the bill is not relevant.
     * @return int
     */
    public int hashCodeBase() {
        int result = basePrintNo.hashCode();
        result = 31 * result + session;
        return result;
    }

    @Override
    public int compareTo(BillId o) {
        return this.toString().compareTo(o.toString());
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
