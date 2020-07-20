package gov.nysenate.openleg.processor.law;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;

/**
 * Some of the law document ids that are sent to us break the usual convention of
 * prefixing with the parent doc's id. This class can be used to patch the document
 * ids with ids that reflect the proper document structure.
 */
public class LawDocIdFixer
{
    private static final Logger logger = LoggerFactory.getLogger(LawDocIdFixer.class);

    /** Hacks to fix various document id inconsistencies. */
    private static final HashMap<String, String> docIdReplacements = new HashMap<>();
    static {
        docIdReplacements.put("PARA43", "PARTGA43");
        docIdReplacements.put("PENA470", "PENP4TXA470");
        docIdReplacements.put("PENA480", "PENP4TXA480");
        docIdReplacements.put("VATA19-B", "VATT5A19-B");
        docIdReplacements.put("VATA34-C", "VATT7A34-C");
        docIdReplacements.put("VATA44-A", "VATT8A44-A");
        docIdReplacements.put("VATA48-C", "VATT11A48-C");
        docIdReplacements.put("MHYA47", "MHYTEA47");
        docIdReplacements.put("LEH1", "LEH-CH21-1962");
        docIdReplacements.put("NNY1", "NNY-CH649-1992");
    }

    /** Ignore these document ids since they cause issues with constructing the trees properly. */
    private static final Set<String> ignoreDocIds = new HashSet<>();
    static {
        ignoreDocIds.add("SOSA2-A*");
        ignoreDocIds.add("SOS41*");
        ignoreDocIds.add("SOS42*");
        ignoreDocIds.add("SOS43*");
    }

    public static String applyReplacement(String documentId) {
        String ret = docIdReplacements.getOrDefault(documentId, documentId);
        if (!ret.equals(documentId))
            logger.info("Doc Id Replacement made from {} to {}", documentId, ret);
        return ret;
    }

    public static boolean ignoreDocument(String documentId) {
        return ignoreDocIds.contains(documentId);
    }
}