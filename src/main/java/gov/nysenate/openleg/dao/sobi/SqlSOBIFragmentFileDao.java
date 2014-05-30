package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

public class SqlSOBIFragmentFileDao extends SqlSOBIFileDao implements SOBIFragmentDao
{
    private static Logger logger = Logger.getLogger(SqlSOBIFragmentFileDao.class);

    /** The database table where SOBI fragments are recorded */
    private static String SOBI_FRAGMENT_TABLE = "sobi_fragment";

    public SqlSOBIFragmentFileDao(Environment environment) {
        super(environment);
    }

    @Override
    public SOBIFragment getSOBIFragmentByFileName(String fragmentFileName) {
        return null;
    }

    @Override
    public void saveSOBIFragment(SOBIFragment fragment) {

    }
}
