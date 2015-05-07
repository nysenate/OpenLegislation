package gov.nysenate.openleg.processor.law;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import gov.nysenate.openleg.util.DateUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
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
    }

    public static String applyReplacement(String documentId, LocalDate publishedDate) {
        if (docIdReplacements.containsKey(documentId)) {
            Optional<Pair<String, Range<LocalDate>>> match = docIdReplacements.get(documentId).stream()
                    .filter(e -> e.getRight().contains(publishedDate))
                    .findFirst();
            if (match.isPresent()) {
                return match.get().getLeft();
            }
        }
        return documentId;
    }
}