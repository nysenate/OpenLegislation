package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.model.sobi.SOBIFile;
import gov.nysenate.openleg.model.sobi.SOBIFragment;
import gov.nysenate.openleg.model.sobi.SOBIFragmentType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlSOBIFragmentDao extends SqlSOBIFileDao implements SOBIFragmentDao
{
    private static Logger logger = Logger.getLogger(SqlSOBIFragmentDao.class);

    /** The database table where SOBI fragments are recorded */
    protected static final String SOBI_FRAGMENT_TABLE = "sobi_fragment";

    /** --- SQL Statements --- */

    private final String CHECK_SOBI_FRAGMENT_EXISTS_SQL =
        "SELECT 1 FROM " + table(SOBI_FRAGMENT_TABLE) + "\n" +
        "WHERE fragment_file_name = :fragmentFileName";

    private final String GET_SOBI_FRAGMENT_BY_FILE_NAME_SQL =
        "SELECT * FROM " + table(SOBI_FRAGMENT_TABLE) + "\n" +
        "WHERE sobi_fragment_type = :sobiFragmentType AND fragment_file_name = :fileName";

    private final String GET_SOBI_FRAGMENTS_BY_SOBI_FILE_SQL =
        "SELECT * FROM " + table(SOBI_FRAGMENT_TABLE) + " WHERE sobi_file_name = :sobiFileName ";

    private final String GET_SOBI_FRAGMENTS_BY_SOBI_FILE_FILTERED_SQL =
        "SELECT * FROM " + table(SOBI_FRAGMENT_TABLE) + "\n" +
        "WHERE sobi_file_name = :sobiFileName AND sobi_fragment_type = :sobiFragmentType ";

    private final String UPDATE_SOBI_FRAGMENT_SQL =
        "UPDATE " + table(SOBI_FRAGMENT_TABLE) + "\n" +
        "SET sobi_file_name = :sobiFileName, published_date_time = :publishedDateTime, " +
        "    sobi_fragment_type = :sobiFragmentType, file_counter = :fileCounter, text = :text\n" +
        "WHERE fragment_file_name = :fragmentFileName";

    private final String INSERT_SOBI_FRAGMENT_SQL =
        "INSERT INTO " + table(SOBI_FRAGMENT_TABLE) +
        " (sobi_file_name, fragment_file_name, published_date_time, sobi_fragment_type, file_counter, text)" +
        " VALUES (:sobiFileName, :fragmentFileName, :publishedDateTime, :sobiFragmentType, :fileCounter, :text)";

    private final String DELETE_SOBI_FRAGMENTS_SQL =
        "DELETE FROM " + table(SOBI_FRAGMENT_TABLE) + " WHERE sobi_file_name = :sobiFileName";

    /** --- Constructors --- */

    public SqlSOBIFragmentDao(Environment environment) {
        super(environment);
    }

    /** --- Implemented Methods --- */

    @Override
    public SOBIFragment getSOBIFragment(SOBIFragmentType fragmentType, String fragmentFileName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFragmentType", fragmentType.name());
        params.addValue("fileName", fragmentFileName);
        return jdbcNamed.queryForObject(GET_SOBI_FRAGMENT_BY_FILE_NAME_SQL, params, new SOBIFragmentRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SOBIFragment> getSOBIFragments(SOBIFile sobiFile, SortOrder order) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName", sobiFile.getFileName());
        return jdbcNamed.query(GET_SOBI_FRAGMENTS_BY_SOBI_FILE_SQL + orderBy("fragment_file_name", order), params,
                               new SOBIFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public List<SOBIFragment> getSOBIFragments(SOBIFile sobiFile, SOBIFragmentType fragmentType, SortOrder order) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFileName", sobiFile.getFileName());
        params.addValue("sobiFragmentType", fragmentType.name());
        return jdbcNamed.query(GET_SOBI_FRAGMENTS_BY_SOBI_FILE_FILTERED_SQL + orderBy("fragment_file_name", order),
                              params, new SOBIFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public void saveSOBIFragments(List<SOBIFragment> fragments) {
        for (SOBIFragment fragment : fragments) {
            // Set a unique file name for this fragment
            fragment.setFileName(createFileNameForFragment(fragment));
            // Persist the meta data of the fragment in the database
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("sobiFileName", fragment.getParentSOBIFile().getFileName());
            params.addValue("fragmentFileName", fragment.getFileName());
            params.addValue("publishedDateTime", toTimestamp(fragment.getPublishedDateTime()));
            params.addValue("sobiFragmentType", fragment.getType().name());
            params.addValue("fileCounter", fragment.getCounter());
            params.addValue("text", fragment.getText().replace('\0', ' '));
            if (jdbcNamed.update(UPDATE_SOBI_FRAGMENT_SQL, params) == 0) {
                jdbcNamed.update(INSERT_SOBI_FRAGMENT_SQL, params);
            }
            logger.debug("Saved sobi fragment " + fragment.getFileName());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSOBIFragments(SOBIFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName", sobiFile.getFileName());
        jdbcNamed.update(DELETE_SOBI_FRAGMENTS_SQL, params);
    }

    /** --- Helper Classes --- */

    /**
     * Maps rows from the sobi fragment table to SOBIFragment objects.
     */
    protected class SOBIFragmentRowMapper implements RowMapper<SOBIFragment> {
        private String pfx = "";
        private Map<String, SOBIFile> sobiFileMap = new HashMap<>();

        public SOBIFragmentRowMapper() {
            this("", Collections.<SOBIFile>emptyList());
        }

        public SOBIFragmentRowMapper(SOBIFile sobiFile) {
             this("", Arrays.asList(sobiFile));
        }

        public SOBIFragmentRowMapper(String pfx, List<SOBIFile> sobiFiles) {
            this.pfx = pfx;
            for (SOBIFile sobiFile : sobiFiles) {
                this.sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }

        @Override
        public SOBIFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sobiFileName = rs.getString(pfx + "sobi_file_name");
            // Passing the sobi file objects in the constructor is a means of caching the objects
            // so that they don't have to be re-mapped. If not supplied, the underlying sobi file dao
            // will need to be utilized.
            SOBIFile sobiFile = this.sobiFileMap.get(sobiFileName);
            if (sobiFile == null) {
                sobiFile = getSOBIFile(sobiFileName);
            }
            String fileName = rs.getString(pfx + "fragment_file_name");
            SOBIFragmentType type = SOBIFragmentType.valueOf(rs.getString(pfx + "sobi_fragment_type"));
            int counter = rs.getInt(pfx + "file_counter");
            String text = rs.getString(pfx + "text");
            SOBIFragment fragment = new SOBIFragment(sobiFile, type, text, counter);
            fragment.setFileName(fileName);
            return fragment;
        }
    }

    /** --- Internal Methods --- */

    /**
     * Generates unique file names for the fragments.
     * E.g SOBI.20140101.000001-bill-1.sobi, SOBI.20140101.000001-agenda-2.sobi
     * @param fragment SOBIFragment
     * @return String
     */
    private String createFileNameForFragment(SOBIFragment fragment) {
        return fragment.getParentSOBIFile().getFileName() + "-" +
               fragment.getType().name().toLowerCase() + "-" + fragment.getCounter() +
               ((fragment.getType().isXml()) ? ".xml" : ".sobi");
    }
}
