package gov.nysenate.openleg.dao.app;

import gov.nysenate.openleg.Environment;
import gov.nysenate.openleg.dao.base.SqlBaseDao;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public class SqlEnvironmentDao extends SqlBaseDao implements EnvironmentDao
{
    @Override
    public Environment getActiveEnvironment() {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Environment> getEnvironmentMap() {
        throw new NotImplementedException();
    }

    @Override
    public void addEnvironment(Environment env) {
        throw new NotImplementedException();
    }

    @Override
    public void deleteEnvironment(Environment env) {
        throw new NotImplementedException();
    }
}
