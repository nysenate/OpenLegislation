package gov.nysenate.openleg.processor.law;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.law.LawChapter;
import gov.nysenate.openleg.model.law.LawDocumentType;
import gov.nysenate.openleg.model.law.LawDocument;
import gov.nysenate.openleg.model.law.LawFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class LawParser
{
    private static final Logger logger = LoggerFactory.getLogger(LawParser.class);

    /** The law files are most likely sent in CP850 encoding. */
    protected static Charset LAWFILE_CHARSET = Charset.forName("CP850");

    /** Set of all law sections that are designated as Court Act laws. */
    private static Set<String> courtActLawSections = Sets.newHashSet("CTC", "FCT", "CCA", "CRC", "SCP", "UCT", "UDC", "UJC");

    /** Set of all law sections that are designated as Rules laws. */
    private static Set<String> rulesLawSections = Sets.newHashSet("CMA", "CMS");

    /** Pattern for law doc headers.  */
    protected static Pattern lawHeader =
        Pattern.compile("\\.\\.SO DOC ((\\w{3})(.{13}))(.{8}) (.{15}) (?:LAWS\\(((?:UN)?CONSOLIDATED)\\))");

    protected static BiMap<String, LawDocumentType> lawLevelCodes = HashBiMap.create();
    static {
        lawLevelCodes.put("A", LawDocumentType.ARTICLE);
        lawLevelCodes.put("T", LawDocumentType.TITLE);
        lawLevelCodes.put("ST", LawDocumentType.SUBTITLE);
        lawLevelCodes.put("P", LawDocumentType.PART);
        lawLevelCodes.put("SP", LawDocumentType.SUB_PART);
        lawLevelCodes.put("INDEX", LawDocumentType.INDEX);
        lawLevelCodes.put("S", LawDocumentType.SECTION);
    }

    /** --- Instance Variables --- */

    protected File file;
    protected Iterator<String> fileItr;

    /** --- Constructors --- */

    public LawParser(LawFile lawFile) throws IOException {
        this.file = lawFile.getFile();
        this.fileItr = Files.lines(file.toPath(), LAWFILE_CHARSET).iterator();
    }

    /** --- Methods --- */

    public List<LawDocument> getLawDocuments() throws IOException {
        return null;
    }

    /**
     *
     *
     * @return
     * @throws IOException
     */


    public LawDocumentType parseLawLevelFromLocationId(String lawId, String locationId) {
        Set<LawDocumentType> pathLevels = new HashSet<>();
        // Try to match given law against our known law ids
        LawChapter lawChapter = null;
        try {
            lawChapter = LawChapter.valueOf(lawId);
        }
        catch (IllegalArgumentException ex) {
            logger.warn("Failed to map {} to a LawChapter! This may indicate a new law chapter.", lawId);
        }

        LawDocumentType level = LawDocumentType.SECTION;
        if (!locationId.matches("^[0-9].*")) {
//            logger.info("{}", locationId);
        }

        logger.info("{} {}", locationId, level);
        return level;
    }

    /** --- Internal Methods --- */

}