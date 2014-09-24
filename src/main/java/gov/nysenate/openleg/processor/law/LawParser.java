package gov.nysenate.openleg.processor.law;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Sets;
import gov.nysenate.openleg.model.law.LawDocLevel;
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
        Pattern.compile("\\.\\.SO DOC (\\w{3})(.{13})(.{8}) (.{15}) (?:LAWS\\(((?:UN)?CONSOLIDATED)\\))");

    protected static BiMap<String, LawDocLevel> lawLevelCodes = HashBiMap.create();
    static {
        lawLevelCodes.put("A", LawDocLevel.ARTICLE);
        lawLevelCodes.put("T", LawDocLevel.TITLE);
        lawLevelCodes.put("ST", LawDocLevel.SUBTITLE);
        lawLevelCodes.put("P", LawDocLevel.PART);
        lawLevelCodes.put("SP", LawDocLevel.SUB_PART);
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

    /** --- Internal Methods --- */

    protected List<LawBlock> getRawDocuments() throws IOException {
        List<LawBlock> rawDocList = new ArrayList<>();
        logger.debug("Extracting law blocks...");
        LawBlock doc = null;
        while (fileItr.hasNext()) {
            String line = fileItr.next();
            if (lawHeader.matcher(line).matches()) {
                if (doc != null) {
                    rawDocList.add(doc);
                }
                doc = new LawBlock();
                doc.header = line;
            }
            else {
                if (doc == null) throw new LawParseException("No doc header received prior to line: " + line);
                doc.getText().append(line);
            }
        }
        return rawDocList;
    }

    /** --- Internal Classes --- */

    protected static class LawBlock
    {
        private String header = "";
        private StringBuilder text = new StringBuilder();

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public StringBuilder getText() {
            return text;
        }
    }
}