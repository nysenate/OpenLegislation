package gov.nysenate.openleg.processors.config;

import gov.nysenate.openleg.processors.bill.SourceType;
import gov.nysenate.openleg.processors.bill.LegDataFragment;
import gov.nysenate.openleg.processors.bill.sobi.SobiBlock;

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
