package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType;

import java.util.HashSet;

public class XmlWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(SobiFragment sobiFragment, HashSet<SobiFragmentType> xmlProcessWhitelist) {
        return xmlProcessWhitelist.contains(sobiFragment.getType());
    }

    public boolean acceptBlock(SobiBlock sobiBlock, HashSet<SobiLineType> sobiBlockWhitelist) {
        return false;
    }
}
