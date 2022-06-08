package gov.nysenate.openleg.common.util.stringDiffer;

/**
 * Class representing one diff operation.
 */
public class Diff {
    public Operation operation;
    public String text;

    /**
     * Constructor.  Initializes the diff with the provided values.
     * @param operation One of INSERT, DELETE or EQUAL.
     * @param text The text being applied.
     */
    public Diff(Operation operation, String text) {
        // Construct a diff with the specified operation and text.
        this.operation = operation;
        this.text = text;
    }

    /**
     * Display a human-readable version of this Diff.
     * @return text version.
     */
    public String toString() {
        String prettyText = this.text.replace('\n', '\u00b6');
        return "Diff(" + this.operation + ",\"" + prettyText + "\")";
    }

    /**
     * Create a numeric hash value for a Diff.
     * This function is not used by DMP.
     * @return Hash value.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = (operation == null) ? 0 : operation.hashCode();
        result += prime * ((text == null) ? 0 : text.hashCode());
        return result;
    }

    /**
     * Is this Diff equivalent to another Diff?
     * @param obj Another Diff to compare against.
     * @return true or false.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Diff other = (Diff) obj;
        if (operation != other.operation) {
            return false;
        }
        if (text == null) {
            return other.text == null;
        } else return text.equals(other.text);
    }
}
