package gov.nysenate.openleg.dao;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.model.sobi.SOBI;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import org.apache.log4j.Logger;

import java.sql.SQLException;

public class SOBIDao extends BaseDao
{
    private static Logger logger = Logger.getLogger(SOBIDao.class);

    private static String sobiTable = "sobi";

    private static String sobiFragmentTable = "sobi_fragment";

    private Environment environment;

    private String envSchema = BaseDao.defaultEnvSchema;

    public SOBIDao(Environment environment) {
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
        "INSERT INTO " + envSchema + "." + sobiTable +
        "(file_name, published_date_time)" +
        "VALUES (?, ?)";

    private String insertSOBIFragmentSql =
        "INSERT INTO " + envSchema + "." + sobiFragmentTable +
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
