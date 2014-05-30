package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.model.sobi.SOBIFragment;

public interface SOBIFragmentDao
{
    /**
     *
     * @param fragmentFileName
     * @return
     */
    public SOBIFragment getSOBIFragmentByFileName(String fragmentFileName);

    /**
     *
     * @param fragment
     */
    public void saveSOBIFragment(SOBIFragment fragment);
}
