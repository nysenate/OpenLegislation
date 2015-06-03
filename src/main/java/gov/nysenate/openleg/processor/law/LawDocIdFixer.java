package gov.nysenate.openleg.processor.law;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Some of the law document ids that are sent to us break the usual convention of
 * prefixing with the parent doc's id. This class can be used to patch the document
 * ids with ids that reflect the proper document structure.
 */
public class LawDocIdFixer
{
    private static final Logger logger = LoggerFactory.getLogger(LawDocIdFixer.class);

    /** Hacks to fix various document id inconsistencies. */
    protected static Multimap<String, Pair<String, Range<LocalDate>>> docIdReplacements = ArrayListMultimap.create();
    static {
        docIdReplacements.put("PARA43", Pair.of("PARTGA43", DateUtils.ALL_DATES));
        docIdReplacements.put("PENA470", Pair.of("PENP4TXA470", DateUtils.ALL_DATES));
        docIdReplacements.put("PENA480", Pair.of("PENP4TXA480", DateUtils.ALL_DATES));
        docIdReplacements.put("VATA19-B", Pair.of("VATT5A19-B", DateUtils.ALL_DATES));
        docIdReplacements.put("VATA34-C", Pair.of("VATT7A34-C", DateUtils.ALL_DATES));
        docIdReplacements.put("VATA44-A", Pair.of("VATT8A44-A", DateUtils.ALL_DATES));
        docIdReplacements.put("VATA48-C", Pair.of("VATT11A48-C", DateUtils.ALL_DATES));
        docIdReplacements.put("MHYA47", Pair.of("MHYTEA47", DateUtils.ALL_DATES));
        docIdReplacements.put("LEH1", Pair.of("LEH-CH21-1962", DateUtils.ALL_DATES));
        docIdReplacements.put("NNY1", Pair.of("NNY-CH649-1992", DateUtils.ALL_DATES));
    }

    /** Ignore these document ids since they cause issues with constructing the trees properly. */
    protected static Map<String, Range<LocalDate>> ignoreDocIds = new HashMap<>();
    static {
        ignoreDocIds.put("SOSA2-A*", DateUtils.ALL_DATES);
        ignoreDocIds.put("SOS41*", DateUtils.ALL_DATES);
        ignoreDocIds.put("SOS42*", DateUtils.ALL_DATES);
        ignoreDocIds.put("SOS43*", DateUtils.ALL_DATES);
    }

    public static String applyReplacement(String documentId, LocalDate publishedDate) {
        if (docIdReplacements.containsKey(documentId)) {
            Optional<Pair<String, Range<LocalDate>>> match = docIdReplacements.get(documentId).stream()
                    .filter(e -> e.getRight().contains(publishedDate))
                    .findFirst();
            if (match.isPresent()) {
                logger.info("Doc Id Replacement made from {} to {}", documentId, match.get().getLeft());
                return match.get().getLeft();
            }
        }
        return documentId;
    }

    public static boolean ignoreDocument(String documentId, LocalDate publishedDate) {
        return ignoreDocIds.containsKey(documentId) && ignoreDocIds.get(documentId).contains(publishedDate);
    }
}