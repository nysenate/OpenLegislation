package gov.nysenate.openleg.model.bill;

import com.google.common.collect.ComparisonChain;
import gov.nysenate.openleg.model.entity.Chamber;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

/**
 * Represents a single action on a single bill. E.g. REFERRED TO RULES.
 */
public class BillAction implements Serializable, Comparable<BillAction>
{
    private static final long serialVersionUID = -508975280380827827L;

    /** Identifies the bill this action was taken on. */
    private BillId billId;

    /** The date this action was performed. Has no time component. */
    private LocalDate date;

    /** The chamber in which this action occurred */
    private Chamber chamber;

    /** Number used for chronological ordering. */
    private int sequenceNo = 0;

    /** The text of this action. */
    private String text = "";

    /** Bill amendment version, typically single character A, B, C ... . */
    private String billAmd = "";

    /** Action code. */
    private int code = 0;

    /** Data code. Can be code or bill number, depending on action code. */
    private int data = 0;

    /** Data amd. Unknown purpose. Single character. */
    private String dataAmd = "";

    /** Post date. Unknown purpose. */
    private LocalDate postDate;

    /** Act session year. Unknown purpose; does not conform to session year odd-only numbering. */
    private int actSessionYear;

    /** Indicates whether or not the BillAction was parsed by XML (true) or bill action text (false) */
    private final boolean fromXML;

    /** --- Constructors --- */

    public BillAction() {
        super();
        fromXML = false;
    }

    /**
     * Fully constructs a new action.
     *
     * @param date LocalDate - The date of the action
     * @param text String - The text of the action
     * @param chamber Chamber - The chamber this bill action occurred in
     * @param sequenceNo int - Indicates the ordering of this action
     * @param billId BillId - The id of the bill the action was performed on
     */
    public BillAction(LocalDate date, String text, Chamber chamber, int sequenceNo, BillId billId) {
        super();
        this.setDate(date);
        this.setText(text);
        this.setBillId(billId);
        this.setChamber(chamber);
        this.setSequenceNo(sequenceNo);
        fromXML = false;
    }

    /**
     * Fully constructs a new action from XML source.
     *
     * @param date LocalDate - The date of the action
     * @param text String - The text of the action
     * @param chamber Chamber - The chamber this bill action occurred in
     * @param sequenceNo int - Indicates the ordering of this action
     * @param billAmd String -
     * @param code int - Action code from XML Data
     * @param data int - Data code from XML Data;
     *             represents different types of data based on the action code (e.g. committee codes, bill numbers)
     * @param dataAmd String -
     * @param postDate LocalDate -
     * @param actSessionYear int - Unknown year value that does not conform to the session year odd-only rule
     * @param billId BillId - The id of the bill the action was performed on
     */
    public BillAction(LocalDate date, String text, Chamber chamber, int sequenceNo,
                      String billAmd, int code, int data, String dataAmd, LocalDate postDate, int actSessionYear,
                      BillId billId) {
        super();
        this.setDate(date);
        this.setText(text);
        this.setBillId(billId);
        this.setChamber(chamber);
        this.setSequenceNo(sequenceNo);

        this.setBillAmd(billAmd);
        this.setCode(code);
        this.setData(data);
        this.setDataAmd(dataAmd);
        this.setPostDate(postDate);
        this.setActSessionYear(actSessionYear);
        fromXML = true;
    }

    /** --- Overrides --- */

    @Override
    public String toString() {
        return date.toString() + " (" + chamber + ") " + text;
    }

    /**
     * Every BillAction is assigned a BillId which may contain an amendment version other than
     * the base version. For the sake of equality checking, we will use the base version of the
     * bill id since the actions are stored on the base bill anyways.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final BillAction other = (BillAction) obj;
        return this.billId.equalsBase(other.billId) &&
               Objects.equals(this.date, other.date) &&
               Objects.equals(this.sequenceNo, other.sequenceNo) &&
               Objects.equals(this.chamber, other.chamber) &&
               StringUtils.equalsIgnoreCase(this.text, other.text) &&
               Objects.equals(this.code, other.code) &&
               Objects.equals(this.data, other.data) &&
               Objects.equals(this.dataAmd, other.dataAmd) &&
               Objects.equals(this.postDate, other.postDate) &&
               Objects.equals(this.actSessionYear, other.actSessionYear) &&
               Objects.equals(this.fromXML, other.fromXML);
    }

    @Override
    public int hashCode() {
        return 31 * billId.hashCodeBase() + Objects.hash(date, sequenceNo, chamber, text.toLowerCase())
                + Objects.hash(code, data, dataAmd, postDate, actSessionYear, fromXML);
    }

    @Override
    public int compareTo(BillAction o) {
        return ComparisonChain.start().compare(this.sequenceNo, o.sequenceNo).result();
    }

    /** --- Helper classes --- */

    public static class ByEventSequenceAsc implements Comparator<BillAction>
    {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o1.getSequenceNo(), o2.getSequenceNo());
        }
    }

    public static class ByEventSequenceDesc implements Comparator<BillAction> {
        @Override
        public int compare(BillAction o1, BillAction o2) {
            return Integer.compare(o2.getSequenceNo(), o1.getSequenceNo());
        }
    }

    /** --- Basic Getters/Setters --- */

    public BillId getBillId() {
        return billId;
    }

    public void setBillId(BillId billId) {
        this.billId = billId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(int sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public Chamber getChamber() {
        return chamber;
    }

    public void setChamber(Chamber chamber) {
        this.chamber = chamber;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    /** --- XML Getters/Setters --- */
    public String getBillAmd() { return billAmd; }

    public void setBillAmd(String billAmd) { this.billAmd = billAmd; }

    public int getCode() { return code; }

    public void setCode(int code) { this.code = code; }

    public int getData() { return data; }

    public void setData(int data) { this.data = data; }

    public String getDataAmd() { return dataAmd; }

    public void setDataAmd(String dataAmd) { this.dataAmd = dataAmd; }

    public LocalDate getPostDate() { return postDate; }

    public void setPostDate(LocalDate postDate) { this.postDate = postDate; }

    public int getActSessionYear() { return actSessionYear; }

    public void setActSessionYear(int actSessionYear) { this.actSessionYear = actSessionYear; }

    /** fromXML cannot be modified after the object is constructed */
    public boolean fromXML() { return fromXML; }
}
