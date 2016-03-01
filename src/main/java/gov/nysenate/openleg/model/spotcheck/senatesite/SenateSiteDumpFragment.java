package gov.nysenate.openleg.model.spotcheck.senatesite;

import java.io.File;

public class SenateSiteDumpFragment {

    private final SenateSiteDumpId dumpId;
    private final int sequenceNo;
    /** The file that contains the dump fragment */
    private File fragmentFile;

    public SenateSiteDumpFragment(SenateSiteDumpId dumpId, int sequenceNo) {
        this.dumpId = dumpId;
        this.sequenceNo = sequenceNo;
    }

    /** --- Getters --- */

    public SenateSiteDumpId getDumpId() {
        return dumpId;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public File getFragmentFile() {
        return fragmentFile;
    }

    public void setFragmentFile(File fragmentFile) {
        this.fragmentFile = fragmentFile;
    }

    @Override
    public String toString() {
        return "SenateSiteDumpFragment{" +
               "dumpId=" + dumpId +
               ", sequenceNo=" + sequenceNo +
               ", fragmentFile=" + fragmentFile +
               '}';
    }
}
