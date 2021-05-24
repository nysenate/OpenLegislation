package testing_utils;

import gov.nysenate.openleg.legislation.law.LawChapterCode;
import gov.nysenate.openleg.legislation.law.LawDocInfo;
import gov.nysenate.openleg.legislation.law.LawDocumentType;
import gov.nysenate.openleg.legislation.law.LawType;
import gov.nysenate.openleg.processors.law.LawBlock;

import java.time.LocalDate;

public class LawTestUtils {
    public static final String TEST_DATA_DIRECTORY = "src/test/resources/processor/law/";

    private LawTestUtils() {}

    /**
     * Creates and returns a LawDocInfo. See getLawBlock for param info.
     * @return a new LawDocInfo based on the LawBlock from getLawBlock.
     */
    public static LawDocInfo getLawDocInfo(LawChapterCode code, String locId, LawDocumentType type) {
        LawDocInfo ret = new LawDocInfo(getLawBlock(code, locId));
        ret.setDocType(type);
        return ret;
    }

    /**
     * Creates and returns a LawBlock with the default method (which is really an update).
     * See getLawBlock for param info.
     * @return the new LawBlock.
     */
    public static LawBlock getLawBlock(LawChapterCode code, String locId) {
        return getLawBlock(code, locId, "");
    }

    /**
     * Creates and returns a LawBlock with a published date of today.
     * @return the new LawBlock
     */
    public static LawBlock getLawBlock(LawChapterCode code, String locId, String method) {
        String lawId = code.name();
        LawBlock ret = new LawBlock();
        ret.setDocumentId(lawId + locId);
        ret.setLawId(lawId);
        ret.setPublishedDate(LocalDate.now());
        ret.setLocationId(locId);
        ret.setMethod(method);
        ret.setConsolidated(code.getType() == LawType.CONSOLIDATED);
        ret.getText().append(ret.getDocumentId()).append(" text, with method ").append(method.isEmpty() ? "UPDATE" : method);
        return ret;
    }
}
