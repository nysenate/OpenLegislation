package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.SourceType;
import gov.nysenate.openleg.model.sourcefiles.LegDataFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;

/**
 * Allows only xml data to be processed
 */
public class XmlOnlyWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(LegDataFragment legDataFragment) {
        return legDataFragment.getParentLegDataFile().getSourceType() == SourceType.XML;
    }

    public boolean acceptBlock(SobiBlock sobiBlock) {
        return false;
    }
}
