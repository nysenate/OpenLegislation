package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;

/**
 * Allows only xml data to be processed
 */
public class XmlOnlyWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(SobiFragment sobiFragment) {
        return sobiFragment.getParentSobiFile().getSourceType() == SourceType.XML;
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return false;
    }
}
