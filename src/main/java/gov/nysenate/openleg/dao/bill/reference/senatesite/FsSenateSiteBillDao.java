package gov.nysenate.openleg.dao.bill.reference.senatesite;

import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpFragId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpId;
import gov.nysenate.openleg.util.FileIOUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

@Repository
public class FsSenateSiteBillDao implements SenateSiteBillDao {

    @Autowired Environment environment;

    public static final String SENSITE_BILL_DUMP_DIRNAME = "sensite-billdump";

    /** Directory where new bill dumps are placed. */
    private File incomingBillDumpDir;

    /** Directory where bill dumps that have been processed are stored. */
    private File archiveBillDumpDir;

    @PostConstruct
    protected void init() throws IOException {
        this.incomingBillDumpDir = FileIOUtils.safeGetFolder(environment.getStagingDir(), SENSITE_BILL_DUMP_DIRNAME);
        this.archiveBillDumpDir = FileIOUtils.safeGetFolder(environment.getStagingDir(), SENSITE_BILL_DUMP_DIRNAME);
    }

    /** --- Implemented Methods --- */

    @Override
    public Collection<SenateSiteBillDump> getPendingDumps() {
        return null;
    }

    @Override
    public void saveDumpFragment(SenateSiteBillDumpFragId fragmentId, Object fragmentData) throws IOException {
        File fragmentFile = new File(incomingBillDumpDir, getDumpFragFilename(fragmentId));
        FileUtils.write(fragmentFile, OutputUtils.toJson(fragmentData));
    }

    @Override
    public void setProcessed(SenateSiteBillDumpFragId fragId) {

    }

    /** --- Internal Methods --- */

    /**
     * @param dumpId SenateSiteBillDumpId
     * @return String - the prefix that is used for all dump fragments of the designated dump
     */
    private static String getDumpFragFilenamePrefix(SenateSiteBillDumpId dumpId) {
        return "bill_dump-" +
                dumpId.getFromDateTime().toString() +
                "-" +
                dumpId.getToDateTime().toString() +
                "-";
    }

    /**
     * @param fragId SenateSiteBillDumpFragId
     * @return String - the filename that is used for the designated dump fragment
     */
    private static String getDumpFragFilename(SenateSiteBillDumpFragId fragId) {
        return getDumpFragFilenamePrefix(fragId) +
                fragId.getSequenceNo() + ".json";
    }
}
