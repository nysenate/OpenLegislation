package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SOBI;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 *
 */
public class SqlSOBIDao extends SqlBaseDao
{
    private static Logger logger = Logger.getLogger(SqlSOBIDao.class);

    private static String SOBI_TABLE = "sobi";

    private static String SOBI_FRAGMENT_TABLE = "sobi_fragment";

    private String envSchema = SqlBaseDao.DEFAULT_ENV_SCHEMA;

    public SqlSOBIDao(Environment environment) {
        this.environment = environment;
    }

    /**
     *
     * @param fileName
     * @return
     */
    public SOBI getSOBI(String fileName) {
        return null;
    }

    private String insertSOBISql =
        "INSERT INTO " + envSchema + "." + SOBI_TABLE +
        "(file_name, published_date_time)" +
        "VALUES (?, ?)";

    private String insertSOBIFragmentSql =
        "INSERT INTO " + envSchema + "." + SOBI_FRAGMENT_TABLE +
        "(sobi_file_name, fragment_file_name, sobi_fragment_type, published_date_time) \n" +
        "VALUES (?, ?, ?, ?)";

    /**
     *
     * @param sobi
     */
    public void saveSOBI(SOBI sobi) {
        if (sobi != null) {
            try {
                runner.update(insertSOBISql, sobi.getFileName(), toTimestamp(sobi.getPublishedDateTime()));
            }
            catch (SQLException e) {
                logger.error("Failed to ", e);
            }
        }
        else {
            throw new IllegalArgumentException("Supplied SOBI object is null. Cannot persist to database.");
        }
    }

    /**
     *
     * @param sobiFragment
     */
    public void saveSOBIFragment(SOBIFragment sobiFragment) {
        if (sobiFragment != null) {
            try {
                runner.update(insertSOBIFragmentSql, sobiFragment.getParentSOBI().getFileName(), sobiFragment.getFileName(),
                              sobiFragment.getFragmentType().name().toLowerCase(), toTimestamp(sobiFragment.getPublishedDateTime()));
                //this.environment.getWorkingDirectory();
            }
            catch (SQLException e) {
                logger.error("Failed to ", e);
            }
        }
        else {
            throw new IllegalArgumentException("Supplied SOBIFragment object is null. Cannot persist to database.");
        }
    }
}
