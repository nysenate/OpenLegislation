package gov.nysenate.openleg.dao.app;

import gov.nysenate.openleg.dao.base.SqlBaseDao;
import gov.nysenate.openleg.model.base.Environment;

import java.util.Map;

public class SqlEnvironmentDao extends SqlBaseDao implements EnvironmentDao
{
    @Override
    public Environment getActiveEnvironment() {
        throw new UnsupportedOperationException("Not sure about this yet.");
    }

    @Override
    public Map<String, Environment> getEnvironmentMap() {
        throw new UnsupportedOperationException("Not sure about this yet.");
    }

    @Override
    public void addEnvironment(Environment env) {
        throw new UnsupportedOperationException("Not sure about this yet.");
    }

    @Override
    public void deleteEnvironment(Environment env) {
        throw new UnsupportedOperationException("Not sure about this yet.");
    }
}
