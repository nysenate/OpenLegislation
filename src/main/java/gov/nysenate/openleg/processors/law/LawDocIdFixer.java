package gov.nysenate.openleg.processors.law;


import gov.nysenate.openleg.legislation.law.LawChapterCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static gov.nysenate.openleg.legislation.law.LawChapterCode.*;

/**
 * Some of the law document ids that are sent to us break the usual convention of
 * prefixing with the parent doc's id. This class can be used to patch the document
 * ids with ids that reflect the proper document structure.
 */
public final class LawDocIdFixer {
    private static final Logger logger = LoggerFactory.getLogger(LawDocIdFixer.class);

    private LawDocIdFixer() {}

    /** Hacks to fix various document id inconsistencies. */
    private static final HashMap<String, String> docIdReplacements = new HashMap<>();
    static {
        addDocToReplace(PAR, "TG", "A43");
        addDocToReplace(PEN, "P4TX", "A470");
        addDocToReplace(PEN, "P4TX", "A480");
        addDocToReplace(VAT, "T5", "A19-B");
        addDocToReplace(VAT, "T7", "A34-C");
        addDocToReplace(VAT, "T8", "A44-A");
        addDocToReplace(VAT, "T11", "A48-C");
        addDocToReplace(MHY, "TE", "A47");
        docIdReplacements.put(LEH.name() + "1", LEH.name() + "-CH21-1962");
        docIdReplacements.put(NNY.name() + "1", NNY.name() + "-CH649-1992");
    }

    /**
     *
     * @param parents that should've been included in the locationId.
     * @param locId before fixing.
     */
    private static void addDocToReplace(LawChapterCode code, String parents, String locId) {
        docIdReplacements.put(code.name() + locId, code.name() + parents + locId);
    }

    /** Ignore these document ids since they cause issues with constructing the trees properly. */
    private static final Set<String> ignoreDocIds = new HashSet<>();
    static {
        String[] locIdsToIgnore = {"A2-A*", "41*", "42*", "43*"};
        for (String locId : locIdsToIgnore)
            ignoreDocIds.add(SOS.name() + locId);
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