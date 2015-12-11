package gov.nysenate.openleg.dao.bill.reference.senatesite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpFragId;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteBillDumpId;
import gov.nysenate.openleg.util.FileIOUtils;
import gov.nysenate.openleg.util.OutputUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;


import static gov.nysenate.openleg.util.DateUtils.*;

@Repository
public class FsSenateSiteBillDao implements SenateSiteBillDao {

    private static final Logger logger = LoggerFactory.getLogger(FsSenateSiteBillDao.class);

    @Autowired Environment environment;
    @Autowired ObjectMapper objectMapper;

    public static final String SENSITE_BILL_DUMP_DIRNAME = "sensite-billdump";

    public static final String DUMP_FRAG_FILENAME_PREFIX_TEMPL = "bill_dump-${fromDateTime}-${toDateTime}-";
    public static final String DUMP_FRAG_FILENAME_TEMPL = DUMP_FRAG_FILENAME_PREFIX_TEMPL + "${seqNo}.json";

    public static final Pattern DUMP_FRAG_FILENAME_REGEX = Pattern.compile(
            StrSubstitutor.replace(DUMP_FRAG_FILENAME_TEMPL, ImmutableMap.of(
                    "fromDateTime", "(" + BASIC_ISO_DATE_TIME_REGEX.toString() + ")",
                    "toDateTime", "(" + BASIC_ISO_DATE_TIME_REGEX.toString() + ")",
                    "seqNo", "(\\d+)"))
    );

    /** Directory where new bill dumps are placed. */
    private File incomingBillDumpDir;

    /** Directory where bill dumps that have been processed are stored. */
    private File archiveBillDumpDir;

    @PostConstruct
    protected void init() throws IOException {
        this.incomingBillDumpDir = FileIOUtils.safeGetFolder(environment.getStagingDir(), SENSITE_BILL_DUMP_DIRNAME);
        this.archiveBillDumpDir = FileIOUtils.safeGetFolder(environment.getArchiveDir(), SENSITE_BILL_DUMP_DIRNAME);
    }

    /** --- Implemented Methods --- */

    @Override
    public Collection<SenateSiteBillDump> getPendingDumps() throws IOException {
        Collection<File> fragmentFiles =
                FileUtils.listFiles(incomingBillDumpDir, new RegexFileFilter(DUMP_FRAG_FILENAME_REGEX), null);
        List<SenateSiteBillDumpFragment> fragments = new LinkedList<>();
        for(File file : fragmentFiles) {
            fragments.add(getFragmentFromFile(file));
        }
        return SenateSiteBillDump.categorizeDumpFragments(fragments);
    }

    @Override
    public void saveDumpFragment(SenateSiteBillDumpFragId fragmentId, Object fragmentData) throws IOException {
        File fragmentFile = new File(incomingBillDumpDir, getDumpFragFilename(fragmentId));
        logger.info("saving senate site bill dump fragment {}", fragmentFile.getAbsolutePath());
        FileUtils.write(fragmentFile, OutputUtils.toJson(fragmentData));
    }

    @Override
    public void setProcessed(SenateSiteBillDump dump) throws IOException {
        for(SenateSiteBillDumpFragment fragment : dump.getDumpFragments()) {
            File fragFile = fragment.getFragmentFile();
            try {
                FileUtils.moveFileToDirectory(fragFile, archiveBillDumpDir, true);
            } catch (FileExistsException ex) {
                File destFile = new File(archiveBillDumpDir, fragFile.getName());
                logger.warn("attempting to overwrite " + destFile.getAbsolutePath());
                destFile.delete();
                FileUtils.moveFileToDirectory(fragFile, archiveBillDumpDir, true);
            }
        }
    }

    /** --- Internal Methods --- */

    /**
     * Parse bill dump fragment metadata from a fragment json file
     */
    private SenateSiteBillDumpFragment getFragmentFromFile(File fragFile) throws IOException {
        return new SenateSiteBillDumpFragment(
                objectMapper.readValue(fragFile, SenateSiteBillDumpFragId.class),
                fragFile
        );
    }

    /**
     * @param dumpId SenateSiteBillDumpId
     * @return String - the prefix that is used for all dump fragments of the designated dump
     */
    private static String getDumpFragFilenamePrefix(SenateSiteBillDumpId dumpId) {
        return StrSubstitutor.replace(DUMP_FRAG_FILENAME_PREFIX_TEMPL, getDumpIdSubMap(dumpId));
    }

    /**
     * @param fragId SenateSiteBillDumpFragId
     * @return String - the filename that is used for the designated dump fragment
     */
    private static String getDumpFragFilename(SenateSiteBillDumpFragId fragId) {
        return StrSubstitutor.replace(DUMP_FRAG_FILENAME_TEMPL, getDumpFragIdSubMap(fragId));
    }

    private static ImmutableMap<String, String> getDumpIdSubMap(SenateSiteBillDumpId dumpId) {
        return ImmutableMap.of(
                "fromDateTime", dumpId.getFromDateTime().format(BASIC_ISO_DATE_TIME),
                "toDateTime", dumpId.getToDateTime().format(BASIC_ISO_DATE_TIME));
    }

    private static ImmutableMap<String, String> getDumpFragIdSubMap(SenateSiteBillDumpFragId fragId) {
        return ImmutableMap.<String, String>builder()
                .putAll(getDumpIdSubMap(fragId))
                .put("seqNo", Integer.toString(fragId.getSequenceNo()))
                .build();
    }
}
