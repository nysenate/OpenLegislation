package gov.nysenate.openleg.dao.bill.reference.senatesite;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.config.Environment;
import gov.nysenate.openleg.model.spotcheck.SpotCheckRefType;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDump;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpFragment;
import gov.nysenate.openleg.model.spotcheck.senatesite.SenateSiteDumpId;
import gov.nysenate.openleg.util.DateUtils;
import gov.nysenate.openleg.util.FileIOUtils;
import gov.nysenate.openleg.util.SenateSiteDumpFragParser;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;


import static gov.nysenate.openleg.util.DateUtils.*;

@Repository
public class FsSenateSiteDao implements SenateSiteDao {

    private static final Logger logger = LoggerFactory.getLogger(FsSenateSiteDao.class);

    @Autowired private Environment environment;
    @Autowired private SenateSiteDumpFragParser parser;
    @Autowired private ObjectMapper objectMapper;

    public static final String SENSITE_DUMP_DIRNAME = "sensite-dump";
    private static final String DUMP_FRAG_FILENAME_PREFIX_TEMPL = "_dump-${fromDateTime}-${toDateTime}-";
    private static final String DUMP_FRAG_FILENAME_TEMPL = "${seqNo}.json";

    /** --- Implemented Methods --- */

    @Override
    public Collection<SenateSiteDump> getPendingDumps(SpotCheckRefType refType) throws IOException {
        Collection<File> fragmentFiles =
                FileUtils.listFiles(getIncomingDumpDir(refType), new RegexFileFilter(dumpFragFilenameRegex(refType)), null);
        List<SenateSiteDumpFragment> fragments = new LinkedList<>();
        for(File file : fragmentFiles) {
            fragments.add(getFragmentFromFile(file, refType));
        }
        return groupFragmentsIntoDumps(fragments);
    }

    private Collection<SenateSiteDump> groupFragmentsIntoDumps(Collection<SenateSiteDumpFragment> fragments) {
        Map<SenateSiteDumpId, SenateSiteDump> dumpMap = new HashMap<>();
        fragments.stream().forEach(fragment -> {
            if (!dumpMap.containsKey(fragment.getDumpId())) {
                SenateSiteDump dump = new SenateSiteDump(fragment.getDumpId());
                dump.addDumpFragment(fragment);
                dumpMap.put(fragment.getDumpId(), dump);
            }
            dumpMap.get(fragment.getDumpId()).addDumpFragment(fragment);
        });
        return dumpMap.values();
    }

    @Override
    public void saveDumpFragment(SenateSiteDumpFragment fragment, String fragmentData) throws IOException {
        File fragmentFile = new File(getIncomingDumpDir(fragment.getDumpId().getRefType()), getDumpFragFilename(fragment));
        logger.info("saving senate site dump fragment {}", fragmentFile.getAbsolutePath());
        FileUtils.write(fragmentFile, prettyPrintJson(fragmentData), Charset.forName("UTF-8"));
    }

    private String prettyPrintJson(String fragmentData) throws IOException {
        Object json = objectMapper.readValue(fragmentData, Object.class);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
    }

    @Override
    public void setProcessed(SenateSiteDump dump) throws IOException {
        for(SenateSiteDumpFragment fragment : dump.getDumpFragments()) {
            File fragFile = fragment.getFragmentFile();
            try {
                FileUtils.moveFileToDirectory(fragFile, getArchiveBillDir(dump.getDumpId().getRefType()), true);
            } catch (FileExistsException ex) {
                File destFile = new File(getArchiveBillDir(dump.getDumpId().getRefType()), fragFile.getName());
                logger.warn("attempting to overwrite " + destFile.getAbsolutePath());
                destFile.delete();
                FileUtils.moveFileToDirectory(fragFile, getArchiveBillDir(dump.getDumpId().getRefType()), true);
            }
        }
    }

    /** --- Internal Methods --- */

    /**
     * Parse dump fragment metadata from a fragment json file
     */
    private SenateSiteDumpFragment getFragmentFromFile(File fragFile, SpotCheckRefType refType) throws IOException {
        SenateSiteDumpFragment fragment = parser.parseFragment(FileUtils.readFileToString(fragFile, "UTF-8"), refType);
        fragment.setFragmentFile(fragFile);
        return fragment;
    }

    /**
     * @param fragment SenateSiteDumpFragment
     * @return String - the prefix that is used for all dump fragments of the designated dump
     */
    private static String getDumpFragFilenamePrefix(SenateSiteDumpFragment fragment) {
        return StrSubstitutor.replace(dumpFragFilenamePrefix(fragment.getDumpId().getRefType()), getDumpIdSubMap(fragment.getDumpId()));
    }

    /**
     * @param fragment SenateSiteDumpFragment
     * @return String - the filename that is used for the designated dump fragment
     */
    private static String getDumpFragFilename(SenateSiteDumpFragment fragment) {
        return StrSubstitutor.replace(dumpFragFilename(fragment.getDumpId().getRefType()), getDumpFragSubMap(fragment));
    }

    private static ImmutableMap<String, String> getDumpIdSubMap(SenateSiteDumpId dumpId) {
        return ImmutableMap.of(
                "fromDateTime", DateUtils.startOfDateTimeRange(dumpId.getRange()).format(BASIC_ISO_DATE_TIME),
                "toDateTime", DateUtils.endOfDateTimeRange(dumpId.getRange()).format(BASIC_ISO_DATE_TIME));
    }

    private static ImmutableMap<String, String> getDumpFragSubMap(SenateSiteDumpFragment fragment) {
        return ImmutableMap.<String, String>builder()
                .putAll(getDumpIdSubMap(fragment.getDumpId()))
                .put("seqNo", Integer.toString(fragment.getSequenceNo()))
                .build();
    }

    /** Directory where new dumps are placed. */
    private File getIncomingDumpDir(SpotCheckRefType refType) throws IOException {
        return FileIOUtils.safeGetFolder(environment.getStagingDir(), SENSITE_DUMP_DIRNAME + "/" + refType.getRefName());
    }

    /** Directory where dumps that have been processed are stored. */
    private File getArchiveBillDir(SpotCheckRefType refType) throws IOException {
        return FileIOUtils.safeGetFolder(environment.getArchiveDir(), SENSITE_DUMP_DIRNAME + "/" + refType.getRefName());
    }

    /** Prefix for naming SenateSiteDumpFragment files. */
    private static String dumpFragFilenamePrefix(SpotCheckRefType refType) {
        return refType.getRefName() + DUMP_FRAG_FILENAME_PREFIX_TEMPL;
    }

    /** Full filename for SenateSiteDumpFragment files. */
    private static String dumpFragFilename(SpotCheckRefType refType) {
        return dumpFragFilenamePrefix(refType) + DUMP_FRAG_FILENAME_TEMPL;
    }

    /** Regex to match SenateSiteDumpFragments of the given refType. */
    private static Pattern dumpFragFilenameRegex(SpotCheckRefType refType) {
             return Pattern.compile(
                     StrSubstitutor.replace(dumpFragFilename(refType), ImmutableMap.of(
                    "fromDateTime", "(" + BASIC_ISO_DATE_TIME_REGEX.toString() + ")",
                    "toDateTime", "(" + BASIC_ISO_DATE_TIME_REGEX.toString() + ")",
                    "seqNo", "(\\d+)")));
    }
}
