package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SqlAdminUserDao extends SqlBaseDao implements AdminUserDao
{
    public static final Logger logger = LoggerFactory.getLogger(SqlAdminUserDao.class);

    /**
     * Add a new Admin account from an admin in the model
     * @param admin The admin to be added to the Database
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public void addAdmin(AdminUser admin) throws DataAccessException {
        if (jdbcNamed.update(AdminUserQuery.UPDATE_ADMIN.getSql(schema()), userParams(admin)) == 0)
            jdbcNamed.update(AdminUserQuery.INSERT_ADMIN.getSql(schema()), userParams(admin));
    }

    /**
     * Deletes the admin account with the given username
     * @param username The username of the account that is being deleted
     * @throws DataAccessException
     */
    @Override
    public void deleteAdmin(String username) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("username", username));
        jdbcNamed.update(AdminUserQuery.DELETE_BY_NAME.getSql(schema()), params);
    }

    /**
     * Update an admin
     * @param admin The administrator account
     * @throws DataAccessException
     */
    @Override
    public void updateAdmin(AdminUser admin) throws DataAccessException {
        jdbcNamed.update(AdminUserQuery.UPDATE_ADMIN.getSql(schema()), userParams(admin));
    }

    protected MapSqlParameterSource userParams(AdminUser admin) {
        return new MapSqlParameterSource()
                .addValue("username", admin.getUsername())
                .addValue("password", BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt()))
                .addValue("active", admin.isActive())
                .addValue("master", admin.isMaster());
    }

    @Override
    public List<AdminUser> getAdminUsers() throws DataAccessException {
        return jdbcNamed.query(AdminUserQuery.SELECT_ALL.getSql(schema()), adminUserRowMapper);
    }

    /**
     * From a given username, check the database to find their password.
     * @param user The username
     * @return The user's password
     * @throws org.springframework.dao.DataAccessException
     */
    public AdminUser getAdminUser(String user) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("username", user));
        return jdbcNamed.queryForObject(AdminUserQuery.SELECT_BY_NAME.getSql(schema()), params, adminUserRowMapper);
    }

    private static RowMapper<AdminUser> adminUserRowMapper = (rs,row) ->
            new AdminUser(rs.getString("username"), rs.getString("password"),
                    rs.getBoolean("active"), rs.getBoolean("master"));
}
