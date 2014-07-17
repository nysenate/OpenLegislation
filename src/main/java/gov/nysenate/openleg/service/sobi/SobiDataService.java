package gov.nysenate.openleg.service.sobi;

import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;

import java.util.Map;

public interface SobiDataService
{
    public SobiFile getSobiFile(String fileName);

    public Map<SOBIFragmentType, SobiFragment> getSobiFragments(String sobiFileName);

    public SobiFragment getSobiFragment(String fragmentName);
}
