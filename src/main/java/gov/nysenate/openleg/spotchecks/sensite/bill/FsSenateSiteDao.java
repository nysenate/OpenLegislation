package gov.nysenate.openleg.spotchecks.sensite.bill;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import gov.nysenate.openleg.common.util.FileIOUtils;
import gov.nysenate.openleg.common.util.SenateSiteDumpFragParser;
import gov.nysenate.openleg.config.OpenLegEnvironment;
import gov.nysenate.openleg.spotchecks.model.SpotCheckRefType;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDump;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpFragment;
import gov.nysenate.openleg.spotchecks.sensite.SenateSiteDumpId;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static gov.nysenate.openleg.common.util.DateUtils.BASIC_ISO_DATE_TIME;
import static gov.nysenate.openleg.common.util.DateUtils.BASIC_ISO_DATE_TIME_REGEX;

@Repository
public class FsSenateSiteDao implements SenateSiteDao {

    private static final Logger logger = LoggerFactory.getLogger(FsSenateSiteDao.class);
    private static final Object lock = new Object();

    @Autowired
    private OpenLegEnvironment environment;
    @Autowired
    private SenateSiteDumpFragParser parser;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Establishes the fixed number of digits in the sequence No portion of the file name
     */
    private static final String seqNoFormat = "%03d";

    public static final String SENSITE_DUMP_DIRNAME = "sensite-dump";
    private static final String DUMP_FRAG_FILENAME_PREFIX_TEMPL = "_dump-${year}-${refDateTime}-";
    private static final String DUMP_FRAG_FILENAME_TEMPL = "${seqNo}.json";

    /**
     * --- Implemented Methods ---
     */

    @Override
    public Collection<SenateSiteDump> getPendingDumps(SpotCheckRefType refType) throws IOException {
        List<SenateSiteDumpFragment> fragments = new ArrayList<>();
        synchronized (lock) {
            Collection<File> fragmentFiles = FileUtils.listFiles(getIncomingDumpDir(refType),
                    new RegexFileFilter(dumpFragFilenameRegex(refType)), null);
            for (File file : fragmentFiles) {
                fragments.add(getFragmentFromFile(file));
            }
        }
        return groupFragmentsIntoDumps(fragments);
    }

    private Collection<SenateSiteDump> groupFragmentsIntoDumps(Collection<SenateSiteDumpFragment> fragments) {
        Map<SenateSiteDumpId, SenateSiteDump> dumpMap = new HashMap<>();
        fragments.forEach(fragment -> {
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
        File fragmentFile = new File(getIncomingDumpDir(fragment.getDumpId().refType()), getDumpFragFilename(fragment));
        logger.info("Saving senate site dump fragment {}", fragmentFile.getAbsolutePath());
        try {  // Delete existing dump if possible
            synchronized (lock) {
                FileUtils.forceDelete(fragmentFile);
            }
        } catch (FileNotFoundException ignored) {}

        // Write pretty printed json to a temporary file, then rename it to the desired name
        File tempFile = FileIOUtils.getTempFile(fragmentFile);
        Object json = objectMapper.readValue(fragmentData, Object.class);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(tempFile, json);
        FileUtils.moveFile(tempFile, fragmentFile);
    }

    @Override
    public void archiveDump(SenateSiteDump dump) throws IOException {
        List<File> fragFiles = dump.getDumpFragments().stream()
                .map(SenateSiteDumpFragment::getFragmentFile).toList();
        List<File> archivedFiles = new ArrayList<>();

        try {
            for (File fragFile : fragFiles) {
                File archivedFile = archiveFile(fragFile, getArchiveBillDir(dump.getDumpId().refType()));
                archivedFiles.add(archivedFile);
            }
        } catch (IOException | NullPointerException ex) {
            // Unable to archive the entire dump. Delete any archived files to leave the filesystem in a valid state.
            for (File archivedFile : archivedFiles) {
                FileUtils.deleteQuietly(archivedFile);
            }
            throw ex;
        }

        // The entire dump was archived successfully. The fragments from the incoming directory are no longer needed.
        for (File fragFile : fragFiles) {
            FileUtils.deleteQuietly(fragFile);
        }
    }

    /**
     * Creates a gzipped archive of {@code fragFile} in the {@code archiveDir} directory
     *
     * If successful, a gzipped copy of {@code fragFile} will be placed in the {@code archiveDir}
     * and returned. {@code fragFile} is left unchanged.
     *
     * In case of an error: {@code fragFile} will be unchanged, no gzipped file will be saved, and
     * an exception will be thrown.
     *
     * @param fragFile A non null File for an individual fragment of a senate site dump.
     * @param archiveDir A non null, already existing directory, where the archive should be placed.
     * @return the gzipped and archived file.
     * @throws NullPointerException if {@code fragFile} is null.
     * @throws NullPointerException if {@code archiveDir} is null.
     * @throws IOException if {@code fragFile} or {@code archiveDir} are invalid.
     * @throws IOException if an IO error occurs.
     */
    private File archiveFile(File fragFile, File archiveDir) throws IOException {
        Objects.requireNonNull(fragFile);
        Objects.requireNonNull(archiveDir);

        File gzipFragFile = FileIOUtils.gzipFile(fragFile);
        File destFile = new File(archiveDir, gzipFragFile.getName());
        try {
            FileUtils.moveFile(gzipFragFile, destFile);
        } catch (Exception ex) {
            // Moving the gzip to the archive failed. Clean up by deleting the gzip file.
            FileUtils.deleteQuietly(gzipFragFile);
            logger.error("Error moving gzipped file '" + fragFile + "' to '" + archiveDir + "'.");
            throw ex;
        }
        destFile.setReadable(true, false);
        return destFile;
    }

    /** --- Internal Methods --- */

    /**
     * Parse dump fragment metadata from a fragment json file
     */
    private SenateSiteDumpFragment getFragmentFromFile(File fragFile) throws IOException {
        SenateSiteDumpFragment fragment = parser.parseFragment(FileUtils.readFileToString(fragFile, "UTF-8"));
        fragment.setFragmentFile(fragFile);
        return fragment;
    }

    /**
     * @param fragment SenateSiteDumpFragment
     * @return String - the filename that is used for the designated dump fragment
     */
    private static String getDumpFragFilename(SenateSiteDumpFragment fragment) {
        return StringSubstitutor.replace(dumpFragFilename(fragment.getDumpId().refType()), getDumpFragSubMap(fragment));
    }

    private static ImmutableMap<String, String> getDumpIdSubMap(SenateSiteDumpId dumpId) {
        return ImmutableMap.of(
                "year", Integer.toString(dumpId.year()),
                "refDateTime", dumpId.dumpTime().format(BASIC_ISO_DATE_TIME));
    }

    private static ImmutableMap<String, String> getDumpFragSubMap(SenateSiteDumpFragment fragment) {
        return ImmutableMap.<String, String>builder()
                .putAll(getDumpIdSubMap(fragment.getDumpId()))
                .put("seqNo", String.format(seqNoFormat, fragment.getSequenceNo()))
                .build();
    }

    /**
     * Directory where new dumps are placed.
     */
    private File getIncomingDumpDir(SpotCheckRefType refType) throws IOException {
        return FileIOUtils.safeGetFolder(environment.getStagingDir(), SENSITE_DUMP_DIRNAME + "/" + refType.getRefName());
    }

    /**
     * Directory where dumps that have been processed are stored.
     */
    private File getArchiveBillDir(SpotCheckRefType refType) throws IOException {
        return FileIOUtils.safeGetFolder(environment.getArchiveDir(), SENSITE_DUMP_DIRNAME + "/" + refType.getRefName());
    }

    /**
     * Prefix for naming SenateSiteDumpFragment files.
     */
    private static String dumpFragFilenamePrefix(SpotCheckRefType refType) {
        return refType.getRefName() + DUMP_FRAG_FILENAME_PREFIX_TEMPL;
    }

    /**
     * Full filename for SenateSiteDumpFragment files.
     */
    private static String dumpFragFilename(SpotCheckRefType refType) {
        return dumpFragFilenamePrefix(refType) + DUMP_FRAG_FILENAME_TEMPL;
    }

    /**
     * Regex to match SenateSiteDumpFragments of the given refType.
     */
    private static Pattern dumpFragFilenameRegex(SpotCheckRefType refType) {
        String reTemplate = "^" + dumpFragFilename(refType);
        String regex = StringSubstitutor.replace(reTemplate, ImmutableMap.of(
                "year", "(\\d{4})",
                "refDateTime", "(" + BASIC_ISO_DATE_TIME_REGEX + ")",
                "seqNo", "(\\d+)"));
        return Pattern.compile(regex);
    }
}
