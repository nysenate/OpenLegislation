package gov.nysenate.openleg.dao.app;

import gov.nysenate.openleg.model.base.Environment;

import java.util.Map;

public interface EnvironmentDao
{
    public Environment getActiveEnvironment();

    public Map<String, Environment> getEnvironmentMap();

    public void addEnvironment(Environment env);

    public void deleteEnvironment(Environment env);
}
