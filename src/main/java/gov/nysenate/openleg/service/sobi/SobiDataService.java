package gov.nysenate.openleg.service.sobi;

import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.Map;

public interface SobiDataService
{
    public SobiFile getSobiFile(String fileName);

    public Map<SobiFragmentType, SobiFragment> getSobiFragments(String sobiFileName);

    public SobiFragment getSobiFragment(String fragmentName);
}
