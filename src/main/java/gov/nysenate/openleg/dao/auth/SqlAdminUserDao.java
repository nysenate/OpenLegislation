package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.dao.base.ImmutableParams;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.auth.AdminUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

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
    public void updateAdmin(AdminUser admin) throws DataAccessException {
        jdbcNamed.update(AdminUserQuery.UPDATE_ADMIN.getSql(schema()), userParams(admin));
    }

    /**
     * Deletes every admin with a certain permissions level.
     * @param level The permission level to delete
     * @throws DataAccessException
     */
    @Override
    public void deleteAdminByLevel(int level ) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("privilegeLevel", level));
        jdbcNamed.update(AdminUserQuery.DELETE_BY_LEVEL.getSql(schema()), params);
    }

    protected MapSqlParameterSource userParams(AdminUser admin) {
        return new MapSqlParameterSource()
                .addValue("username", admin.getUsername())
                .addValue("password", admin.getPassword())
                .addValue("privilegeLevel", admin.getPrivileges());
    }

    /**
     * Create an AdminUser object from the given username
     * @param username The username of the admin
     * @return The new admin user object associated with this username
     */
    @Override
    public AdminUser getAdmin(String username) {
        String pass = "";
        int level = -1;

        try {
            pass = getPasswordFromUser(username);
            level = getLevelFromUser(username);

        } catch (DataAccessException dae) {

        } finally {
            if (pass != null && level != -1)
                return new AdminUser(username, pass, level);
        }

        return null;
    }

    /**
     * From a given username, find the permissions level of that account
     * @param user The username
     * @return The user's permissions level
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public int getLevelFromUser(String user) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("username", user));
        return jdbcNamed.queryForObject(AdminUserQuery.SELECT_BY_NAME.getSql(schema()), params,
                (rs,row) -> rs.getInt("permissions_level"));
    }

    /**
     * From a given username, check the database to find their password.
     * @param user The username
     * @return The user's password
     * @throws org.springframework.dao.DataAccessException
     */
    @Override
    public String getPasswordFromUser(String user) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("username", user));
        return jdbcNamed.queryForObject(AdminUserQuery.SELECT_BY_NAME.getSql(schema()), params,
                (rs,row) -> rs.getString("password"));
    }

    @Override
    public String getUsername(AdminUser admin) { return admin.getUsername(); }

    @Override
    public String getPassword(AdminUser admin) { return admin.getUsername(); }

    @Override
    public int getPrivilegeLevel(AdminUser admin) { return admin.getPrivileges(); }
}
