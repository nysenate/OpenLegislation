package gov.nysenate.openleg.model.spotcheck.senatesite;

import java.io.File;
import java.time.LocalDateTime;

public class SenateSiteBillDumpFragment extends SenateSiteBillDumpFragId {

    /** The file that contains the dump fragment */
    protected File fragmentFile;

    public SenateSiteBillDumpFragment(SenateSiteBillDumpFragId fragId, File fragmentFile) {
        super(fragId);
        this.fragmentFile = fragmentFile;
    }

    /** --- Getters --- */

    public File getFragmentFile() {
        return fragmentFile;
    }
}
