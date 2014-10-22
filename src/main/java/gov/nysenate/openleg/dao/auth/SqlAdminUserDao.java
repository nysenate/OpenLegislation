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
     */
    @Override
    public void addAdmin(AdminUser admin) throws DataAccessException {
        if (jdbcNamed.update(AdminUserQuery.INSERT_ADMIN.getSql(schema()), userParams(admin)) == 0)
            jdbcNamed.update(AdminUserQuery.INSERT_ADMIN.getSql(schema()), userParams(admin));
    }

    @Override
    public void deleteAdmin(String username) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("username", username));
        if (jdbcNamed.update(AdminUserQuery.DELETE_BY_NAME.getSql(schema()), params) == 0)
            jdbcNamed.update(AdminUserQuery.DELETE_BY_NAME.getSql(schema()), params);
    }

    @Override
    public void deleteAdminByLevel(int level ) throws DataAccessException {
        ImmutableParams params = ImmutableParams.from(new MapSqlParameterSource().addValue("privilegeLevel", level));
        if (jdbcNamed.update(AdminUserQuery.DELETE_BY_LEVEL.getSql(schema()), params) == 0)
            jdbcNamed.update(AdminUserQuery.DELETE_BY_LEVEL.getSql(schema()), params);
    }

    protected MapSqlParameterSource userParams(AdminUser admin) {
        return new MapSqlParameterSource()
                .addValue("username", admin.getUsername())
                .addValue("password", admin.getPassword())
                .addValue("privilegeLevel", admin.getPrivileges());
    }

    @Override
    public AdminUser getAdmin(String username) {
        String pass = getPasswordFromUser(username);
        int level = getLevelFromUser(username);

        return new AdminUser(username, pass, level);
    }

    /**
     * From a given username, find the permissions level of that account
     * @param user The username
     * @return The user's permissions level
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
