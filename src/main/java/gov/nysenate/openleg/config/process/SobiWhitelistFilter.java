package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiLineType;

import java.util.HashSet;

public class SobiWhitelistFilter implements SourceFilter {

    public boolean acceptFragment(SobiFragment sobiFragment, HashSet<SobiFragmentType> sobiProcessWhitelist) {
        return sobiProcessWhitelist.contains(sobiFragment.getType());
    }

    public boolean acceptBlock(SobiBlock sobiBlock, HashSet<SobiLineType> sobiBlockWhitelist) {
        return sobiBlockWhitelist.contains(sobiBlock.getType());
    }
}
