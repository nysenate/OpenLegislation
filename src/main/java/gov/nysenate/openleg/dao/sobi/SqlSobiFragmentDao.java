package gov.nysenate.openleg.dao.sobi;

import gov.nysenate.openleg.dao.base.SortOrder;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.sobi.SobiFile;
import gov.nysenate.openleg.model.sobi.SobiFragment;
import gov.nysenate.openleg.model.sobi.SobiFragmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static gov.nysenate.openleg.dao.sobi.SqlSobiFragmentQuery.*;

@Repository
public class SqlSobiFragmentDao extends SqlBaseDao implements SobiFragmentDao
{
    private static final Logger logger = LoggerFactory.getLogger(SqlSobiFragmentDao.class);

    @Autowired
    private SobiFileDao sobiFileDao;

    /** --- Constructors --- */

    public SqlSobiFragmentDao() {
        super();
    }

    /** --- Implemented Methods --- */

    @Override
    public SobiFragment getSOBIFragment(SobiFragmentType fragmentType, String fragmentFileName) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFragmentType", fragmentType.name());
        params.addValue("fileName", fragmentFileName);
        return jdbcNamed.queryForObject(
            GET_SOBI_FRAGMENT_BY_FILE_NAME_SQL.getSql(schema()), params, new SobiFragmentRowMapper());
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getSOBIFragments(SobiFile sobiFile, SortOrder order) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName", sobiFile.getFileName());
        return jdbcNamed.query(
             GET_SOBI_FRAGMENTS_BY_SOBI_FILE_SQL.getSql(schema()) + orderBy("fragment_id", order),
             params, new SobiFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public List<SobiFragment> getSOBIFragments(SobiFile sobiFile, SobiFragmentType fragmentType, SortOrder order) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sobiFileName", sobiFile.getFileName());
        params.addValue("sobiFragmentType", fragmentType.name());
        return jdbcNamed.query(
            GET_SOBI_FRAGMENTS_BY_SOBI_FILE_AND_TYPE_SQL.getSql(schema()) + orderBy("fragment_id", order),
            params, new SobiFragmentRowMapper(sobiFile));
    }

    /** {@inheritDoc} */
    @Override
    public void saveSOBIFragments(List<SobiFragment> fragments) {
        for (SobiFragment fragment : fragments) {
            // Set a unique file name for this fragment
            fragment.setFragmentId(createIdForFragment(fragment));
            // Persist the meta data of the fragment in the database
            MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("sobiFileName", fragment.getParentSobiFile().getFileName());
            params.addValue("fragmentId", fragment.getFragmentId());
            params.addValue("publishedDateTime", toTimestamp(fragment.getPublishedDateTime()));
            params.addValue("sobiFragmentType", fragment.getType().name());
            params.addValue("fileCounter", fragment.getCounter());
            params.addValue("text", fragment.getText().replace('\0', ' '));
            if (jdbcNamed.update(UPDATE_SOBI_FRAGMENT_SQL.getSql(schema()), params) == 0) {
                jdbcNamed.update(INSERT_SOBI_FRAGMENT_SQL.getSql(schema()), params);
            }
            logger.debug("Saved sobi fragment " + fragment.getFragmentId());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void deleteSOBIFragments(SobiFile sobiFile) {
        MapSqlParameterSource params = new MapSqlParameterSource("sobiFileName", sobiFile.getFileName());
        jdbcNamed.update(DELETE_SOBI_FRAGMENTS_SQL.getSql(schema()), params);
    }

    /** --- Helper Classes --- */

    /**
     * Maps rows from the sobi fragment table to SobiFragment objects.
     */
    protected class SobiFragmentRowMapper implements RowMapper<SobiFragment> {
        private String pfx = "";
        private Map<String, SobiFile> sobiFileMap = new HashMap<>();

        public SobiFragmentRowMapper() {
            this("", Collections.<SobiFile>emptyList());
        }

        public SobiFragmentRowMapper(SobiFile sobiFile) {
             this("", Arrays.asList(sobiFile));
        }

        public SobiFragmentRowMapper(String pfx, List<SobiFile> sobiFiles) {
            this.pfx = pfx;
            for (SobiFile sobiFile : sobiFiles) {
                this.sobiFileMap.put(sobiFile.getFileName(), sobiFile);
            }
        }

        @Override
        public SobiFragment mapRow(ResultSet rs, int rowNum) throws SQLException {
            String sobiFileName = rs.getString(pfx + "sobi_file_name");
            // Passing the sobi file objects in the constructor is a means of caching the objects
            // so that they don't have to be re-mapped. If not supplied, the underlying sobi file dao
            // will need to be utilized.
            SobiFile sobiFile = this.sobiFileMap.get(sobiFileName);
            if (sobiFile == null) {
                sobiFile = sobiFileDao.getSobiFile(sobiFileName);
            }
            String fragmentId = rs.getString(pfx + "fragment_id");
            SobiFragmentType type = SobiFragmentType.valueOf(rs.getString(pfx + "sobi_fragment_type"));
            int counter = rs.getInt(pfx + "file_counter");
            String text = rs.getString(pfx + "text");
            SobiFragment fragment = new SobiFragment(sobiFile, type, text, counter);
            fragment.setFragmentId(fragmentId);
            return fragment;
        }
    }

    /** --- Internal Methods --- */

    /**
     * Generates unique file names for the fragments.
     * E.g SOBI.20140101.000001-bill-1.sobi, SOBI.20140101.000001-agenda-2.sobi
     * @param fragment SobiFragment
     * @return String
     */
    private String createIdForFragment(SobiFragment fragment) {
        return fragment.getParentSobiFile().getFileName() + "-" +
               fragment.getType().name().toLowerCase() + "-" + fragment.getCounter() +
               ((fragment.getType().isXml()) ? ".xml" : ".sobi");
    }
}
