package gov.nysenate.openleg.processor.law;

import gov.nysenate.openleg.model.law.LawTree;
import gov.nysenate.openleg.model.law.LawVersionId;

/**
 * A class for the special cases of the Constitution, Senate Rules, and Assembly Rules.
 */
public class SpecialChapterLawBuilder extends IdBasedLawBuilder implements LawBuilder {

    public SpecialChapterLawBuilder(LawVersionId lawVersionId, LawTree previousTree) {
        super(lawVersionId, previousTree);
    }


}
