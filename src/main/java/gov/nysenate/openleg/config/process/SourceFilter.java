package gov.nysenate.openleg.config.process;

import gov.nysenate.openleg.model.sourcefiles.sobi.SobiBlock;
import gov.nysenate.openleg.model.sourcefiles.sobi.SobiFragment;

public interface SourceFilter {

   public boolean acceptFragment(SobiFragment sobiFragment);

   public boolean acceptBlock(SobiBlock sobiBlock);
}
