package gov.nysenate.openleg.model.spotcheck;

import gov.nysenate.openleg.util.StringDiffer;

import java.util.LinkedList;

/**
 * Encapsulates basic information about a mismatch between the reference and target content.
 */
public class SpotCheckMismatch
{
    /** The type of mismatch that occurred. */
    protected SpotCheckMismatchType mismatchType;

    /** String representation of the reference data (their data) */
    protected String theirs;

    /** String representation of the target data (our data) */
    protected String ours;

    /** Any details about this mismatch. (Optional) */
    protected String notes;

    /** --- Constructor --- */

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String theirs, String ours) {
        this(mismatchType, theirs, ours, "");
    }

    public SpotCheckMismatch(SpotCheckMismatchType mismatchType, String theirs, String ours, String notes) {
        this.mismatchType = mismatchType;
        this.theirs = (theirs == null) ? "" : theirs;
        this.ours = (ours == null) ? "" : ours;
        this.notes = notes;
    }

    /** --- Methods --- */

    /**
     * Computes the difference between the reference and target data.
     *
     * @param simple boolean - Set to true to make the results of the diff less granular.
     * @return LinkedList<StringDiffer.Diff>
     */
    public LinkedList<StringDiffer.Diff> getDiff(boolean simple) {
        StringDiffer stringDiffer = new StringDiffer();
        LinkedList<StringDiffer.Diff> diffs = stringDiffer.diff_main(theirs, ours);
        if (simple) {
            stringDiffer.diff_cleanupSemantic(diffs);
        }
        return diffs;
    }

    /** --- Basic Getters --- */

    public SpotCheckMismatchType getMismatchType() {
        return mismatchType;
    }

    public String getTheirs() {
        return theirs;
    }

    public String getOurs() {
        return ours;
    }

    public String getNotes() {
        return notes;
    }
}